package com.youxigu.dynasty2.armyout.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.ibatis.sqlmap.engine.cache.memcached.MemcachedManager;
import com.youxigu.dynasty2.armyout.dao.IArmyoutDao;
import com.youxigu.dynasty2.armyout.domain.Armyout;
import com.youxigu.dynasty2.armyout.domain.Strategy;
import com.youxigu.dynasty2.armyout.domain.UserArmyout;
import com.youxigu.dynasty2.armyout.domain.action.ArmyOutAction;
import com.youxigu.dynasty2.armyout.domain.action.AssistOutAction;
import com.youxigu.dynasty2.armyout.domain.action.OutBackAction;
import com.youxigu.dynasty2.armyout.domain.action.PVEOutAction;
import com.youxigu.dynasty2.armyout.domain.action.SpyAction;
import com.youxigu.dynasty2.armyout.service.IArmyoutService;
import com.youxigu.dynasty2.common.AppConstants;
import com.youxigu.dynasty2.common.service.ICommonService;
import com.youxigu.dynasty2.develop.domain.CastleBuilding;
import com.youxigu.dynasty2.develop.service.ICastleService;
import com.youxigu.dynasty2.map.domain.MapCell;
import com.youxigu.dynasty2.map.service.ICommandDistatcher;
import com.youxigu.dynasty2.map.service.IMapService;
import com.youxigu.dynasty2.map.service.impl.ThreadLocalMapCellCache;
import com.youxigu.dynasty2.user.domain.User;
import com.youxigu.dynasty2.user.service.IUserService;
import com.youxigu.dynasty2.util.BaseException;
import com.youxigu.wolf.net.NodeSessionMgr;

public class ArmyoutService implements IArmyoutService, ApplicationListener {
	public static final Logger log = LoggerFactory
			.getLogger(ArmyoutService.class);

	private Map<Long, Armyout> ARMYOUTS = new ConcurrentHashMap<Long, Armyout>();
	private Map<Integer, Strategy> STRATEGYS = new HashMap<Integer, Strategy>();
	private IArmyoutDao armyoutDao;
	private IMapService mapService;
	private ICommandDistatcher commandDistatcher;
	private ICommonService commonService;
	private IUserService userService;
	private ICastleService castleService;

	public void setArmyoutDao(IArmyoutDao armyoutDao) {
		this.armyoutDao = armyoutDao;
	}

	public void setMapService(IMapService mapService) {
		this.mapService = mapService;
	}

	public void setCommandDistatcher(ICommandDistatcher commandDistatcher) {
		this.commandDistatcher = commandDistatcher;
	}

	public void setCommonService(ICommonService commonService) {
		this.commonService = commonService;
	}

	public void setUserService(IUserService userService) {
		this.userService = userService;
	}

	public void setCastleService(ICastleService castleService) {
		this.castleService = castleService;
	}

	@Override
	public void loadArmyout() {
		// 初始化出征策略
		List<Strategy> strategyList = armyoutDao.listStrategys();
		if (strategyList != null && strategyList.size() > 0) {
			for (Strategy strategy : strategyList) {
				STRATEGYS.put(strategy.getId(), strategy);
			}
		}

		// 只在缓存在mainserver上加载
		String str = System.getProperty(NodeSessionMgr.SERVER_TYPE_KEY);
		if (str == null) {
			return;
		}
		int serverType = Integer.parseInt(str);
		if (serverType != NodeSessionMgr.SERVER_TYPE_MAIN) {
			return;
		}

		if (ARMYOUTS != null) {
			ARMYOUTS.clear();
		}

		// 初始化出征缓存
		List<Armyout> list = armyoutDao.listArmyouts();
		if (list != null && list.size() > 0) {
			List<MapCell> cells = new ArrayList<MapCell>();
			for (Armyout armyout : list) {
				ARMYOUTS.put(armyout.getId(), armyout);

				cells.clear();
				MapCell mapCell = mapService
						.getMapCellCache(armyout.getFromCellId());
				if (mapCell != null) {
					cells.add(mapCell);
				}
				mapCell = mapService.getMapCellCache(armyout.getToCellId());
				if (mapCell != null) {
					cells.add(mapCell);
				}
				// 出征和坐标绑定 这块可以不锁
				this._addArmyoutToCell(cells, armyout);

				// 出征加入到执行队列
				this.armyoutJoinExeQueue(armyout);
			}
		}

		log.info("*************加载出征数据结束*********");
	}

	@Override
	public Strategy getStrategy(int outType) {
		return STRATEGYS.get(outType);
	}

	@Override
	public Armyout lockArmyout(Armyout armyout) {
		List<Integer> cellIds = new ArrayList<Integer>();
		cellIds.add(armyout.getFromCellId());
		cellIds.add(armyout.getToCellId());
		this.lockArmyout(cellIds);
		return this.getArmyoutForWrite(armyout.getId());
	}

	@Override
	public void lockArmyout(List<Integer> cellIds) {
		try {
			mapService.lockMapCells(cellIds);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/**
	 * 出征和坐标绑定
	 * 
	 * @param cells
	 * @param armyout
	 */
	private void _addArmyoutToCell(List<MapCell> cells, Armyout armyout) {
		if (cells != null && cells.size() > 0) {
			for (MapCell mapCell : cells) {
				if (mapCell != null) {
					mapCell.addArmyout(armyout);
				}
			}
		}
	}

	/**
	 * 出征加入到执行队列
	 * 
	 * @param armyout
	 */
	private void armyoutJoinExeQueue(Armyout armyout) {
		int outType = armyout.getOutType();
		MapCell fromMapCell = mapService
				.getMapCellCache(armyout.getFromCellId());
		MapCell toMapCell = mapService.getMapCellCache(armyout.getToCellId());
		User fromUser = userService.getUserById(armyout.getAttackerUserId());
		Strategy strategy = STRATEGYS.get(outType);
		this.doArmtoutJoinExeQueue(armyout, strategy, fromMapCell, toMapCell,
				fromUser);
	}

	@Override
	public void doArmtoutJoinExeQueue(Armyout armyout, Strategy strategy,
			MapCell fromMapCell, MapCell toMapCell, User fromUser) {
		ArmyOutAction action = null;
		switch (armyout.getOutType()) {
		case Strategy.COMMAND_201:
			// 侦查
			CastleBuilding casBui = castleService
					.getMaxLevelCastBuildingByEntId(fromUser.getMainCastleId(),
							AppConstants.ENT_BUILDING_RADAR);
			action = new SpyAction(fromMapCell, toMapCell, armyout, fromUser,
					strategy, casBui, armyout.getOutArriveDttm().getTime());
			break;
		case Strategy.COMMAND_202:
			// PVP
			action = new ArmyOutAction(fromMapCell, toMapCell, armyout,
					fromUser, strategy, armyout.getOutArriveDttm().getTime());
			break;
		case Strategy.COMMAND_205:
		case Strategy.COMMAND_206:
		case Strategy.COMMAND_207:
			// 回城
			action = new OutBackAction(fromMapCell, toMapCell, armyout,
					fromUser, strategy, armyout.getOutBackDttm().getTime());
			break;
		case Strategy.COMMAND_208:
			// PVE
			action = new PVEOutAction(fromMapCell, toMapCell, armyout, fromUser,
					strategy, armyout.getOutArriveDttm().getTime());
			break;
		case Strategy.COMMAND_210:
			// 派遣
			action = new AssistOutAction(fromMapCell, toMapCell, armyout, fromUser,
					strategy, armyout.getOutArriveDttm().getTime());
			break;
		default:
			break;
		}
		armyout.parent = action;// 设置所属的行为
		commandDistatcher.putCommander(action);
	}

	@Override
	public void addArmyoutToCell(List<Integer> cellIds, Armyout armyout) {
		List<MapCell> cells = null;
		try {
			cells = mapService.getMapCellForWrite(cellIds);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this._addArmyoutToCell(cells, armyout);
	}

	@Override
	public void removeArmyoutFromCell(List<Integer> cellIds, Armyout armyout) {
		List<MapCell> cells = null;
		try {
			cells = mapService.getMapCellForWrite(cellIds);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (cells != null && cells.size() > 0) {
			for (MapCell mapCell : cells) {
				if (mapCell != null) {
					mapCell.clearArmyout(armyout);
				}
			}
		}
	}

	@Override
	public Armyout getArmyoutCache(long armyoutId) {
		return ARMYOUTS.get(armyoutId);
	}

	@Override
	public void addArmyoutCache(Armyout armyout) {
		if (!ARMYOUTS.containsKey(armyout.getId())) {
			ARMYOUTS.put(armyout.getId(), armyout);
		}

		// 修改mapcell
		this.addArmyoutToCell(armyout.getCellIds(), armyout);
	}

	@Override
	public void removeArmyoutCache(Armyout armyout) {
		ARMYOUTS.remove(armyout.getId());

		// 修改mapcell
		this.removeArmyoutFromCell(armyout.getCellIds(), armyout);
	}

	@Override
	public Armyout getArmyoutForRead(long armyoutId) {
		Armyout armyout = ARMYOUTS.get(armyoutId);
		if (armyout == null) {
			throw new BaseException("指定的armyout不存在：" + armyoutId);
		}
		if (log.isDebugEnabled()) {
			log.debug("get Armyout({}) for read", armyoutId);
		}
		// 复制一个对象并返回，避免错误修改缓存中的对象。
		return armyout.clone();
	}

	@Override
	public Armyout getArmyoutForWrite(long armyoutId) {
		if (!ThreadLocalMapCellCache.isInTrans()) {
			throw new BaseException("只有在事务中才能修改Armyout");
		}

		Armyout armyout = null;
		Object obj = ThreadLocalMapCellCache
				.getObject(Armyout.getCacheKey(armyoutId));
		if (obj != null) {
			return (Armyout) obj;
		}

		armyout = ARMYOUTS.get(armyoutId);
		if (armyout == null) {
			throw new BaseException("指定的armyout不存在：" + armyoutId);
		}
		// 复制一个对象并返回，避免错误修改缓存中的对象。
		return armyout.clone();
	}

	@Override
	public void updateArmyout(Armyout armyout) {
		ThreadLocalMapCellCache.addData(ThreadLocalMapCellCache.UPDATE,
				armyout.getKey(), armyout);
	}

	@Override
	public void createArmyOut(Armyout armyout) {
		armyoutDao.createArmyOut(armyout);
	}

	@Override
	public void deleteArmyOut(Armyout armyout) {
		armyoutDao.deleteArmyOut(armyout);
	}

	@Override
	public int getMaxPvpTimes() {
		return commonService.getSysParaIntValue(AppConstants.MAX_PVP_TIMES,
				100);
	}

	@Override
	public UserArmyout lockUserArmyout(long userId) {
		try {
			MemcachedManager.lock("USERARMYOUT_" + userId);
		} catch (TimeoutException e) {
			throw new BaseException(e);
		}
		UserArmyout userArmyout = armyoutDao.getUserArmyout(userId);
		if (userArmyout == null) {
			userArmyout = new UserArmyout(userId);
			armyoutDao.createUserArmyout(userArmyout);
		}
		// 隔天重置
		userArmyout.dayPassReset();
		return userArmyout;
	}

	@Override
	public UserArmyout doCheakUserArmyout(long userId, Strategy strategy) {
		UserArmyout userArmyout = this.lockUserArmyout(userId);
		if (strategy.getConsumeTimes() > 0) {
			if (userArmyout.getPvpNum() >= this.getMaxPvpTimes()) {
				throw new BaseException("每日出征次数已用尽");
			} else {
				userArmyout.setPvpNum(userArmyout.getPvpNum() + 1);
			}
		}

		if (userArmyout.getNumByOutType(strategy.getId()) >= strategy
				.getMaxTimes()) {
			throw new BaseException("每日" + strategy.getName() + "次数已用尽");
		}

		if (userArmyout.isQueueFull()) {
			throw new BaseException("出征队列已满");
		}
		return userArmyout;
	}

	@Override
	public void doJoinUserArmyout(Armyout armyout, Strategy strategy) {
		UserArmyout userArmyout = this
				.lockUserArmyout(armyout.getAttackerUserId());
		this.doJoinUserArmyout(userArmyout, armyout, strategy);
	}

	@Override
	public void doJoinUserArmyout(UserArmyout userArmyout, Armyout armyout,
			Strategy strategy) {
		if (strategy.getConsumeTimes() > 0) {
			// 增加pvp次数
			userArmyout.setPvpNum(userArmyout.getPvpNum() + 1);
			// 加入出征队列
			userArmyout.setArmyout(armyout.getId());
		}
		// 记录出征次数
		userArmyout.setNumByOutType(strategy.getId());
		this.updateUserArmyout(userArmyout);
	}

	@Override
	public void doExitUserArmyout(Armyout armyout) {
		UserArmyout userArmyout = this
				.lockUserArmyout(armyout.getAttackerUserId());
		userArmyout.removeArmyout(armyout.getId());
		this.updateUserArmyout(userArmyout);
	}

	@Override
	public void updateUserArmyout(UserArmyout userArmyout) {
		armyoutDao.updateUserArmyout(userArmyout);
	}

	@Override
	public double getDistanceBetweenPoints(int fromX, int fromY, int toX,
			int toY) {
		int disX = Math.abs(fromX - toX);
		int disY = Math.abs(fromY - toY);
		return Math.pow(disX * disX + disY * disY, 0.5);
	}

	/**
	 * 所有的速度加成
	 * 
	 * @return
	 */
	private double getSpeedBonus() {
		return 1d;
	}

	public double getSpyTimeBetweenPoints(User user, MapCell fromCell,
			MapCell toCell) {
		return this.getBaseTimeBetweenPoints(user, fromCell, toCell) * 0.1d;
	}

	@Override
	public double getBaseTimeBetweenPoints(User user, MapCell fromCell,
			MapCell toCell) {
		// 2.出征，驻守，集结时间 = 向上取整[目标与自己距离 × 15 / *速度科技参数* ×
		// if(从13~15区出发并且目标是1~12区中和自己国家不同的地区时,1.5,1) ×
		// if(出发地与目标的联盟资源点距离小于等于9,0.5,1)]
		// 3.侦查时间 = 出征时间 / 10

		int fromX = fromCell.getPosX();
		int fromY = fromCell.getPosY();
		int fromStateId = fromCell.getStateId();
		int toX = toCell.getPosX();
		int toY = toCell.getPosY();
		int toStateId = toCell.getStateId();

		// 区加成
		double stateBunus = 1d;
		int fromCountryId = 0;
		if (user != null && user.getCountryId() > 0) {
			fromCountryId = user.getCountryId();
		}

		// 区域加成 (从13~15区出发并且目标是1~12区中和自己国家不同的地区时,1.5,1)
		if (fromStateId >= 13 && fromStateId <= 15 && toStateId >= 1
				&& toStateId <= 12 && toCell.getCountryId() != fromCountryId) {
			stateBunus = 1.5d;
		}

		double dis = getDistanceBetweenPoints(fromX, fromY, toX, toY);
		dis = dis * 15 / getSpeedBonus() * stateBunus;

		// 公路加成，出发地或是目标地是公路&&路程<=9,
		double roadBonus = 1d;
		if ((fromCell.getEarthType() == MapCell.EARTH_TYPE_ROAD
				|| toCell.getEarthType() == MapCell.EARTH_TYPE_ROAD)
				&& dis <= 9) {
			roadBonus = 0.5d;
		}
		dis = dis * roadBonus;

		return dis;
	}

	@Override
	public double getBaseResConsume(Strategy strategy, double distance) {
		// 向其他玩家基地出征消耗石油=向上取整[距离^0.5]×100
		return Math.pow(distance, 0.5) * strategy.getResNumFactor() * 1d;
	}

	@Override
	public double getBaseResConsume(Strategy strategy, MapCell fromCell,
			MapCell toCell) {
		int dis = (int) this.getDistanceBetweenPoints(fromCell.getPosX(),
				fromCell.getPosY(), toCell.getPosX(), toCell.getPosY());
		return this.getBaseResConsume(strategy, dis);
	}

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		if (event instanceof ContextRefreshedEvent) {
			// 最后加载出征数据
			loadArmyout();
		}

	}
}
