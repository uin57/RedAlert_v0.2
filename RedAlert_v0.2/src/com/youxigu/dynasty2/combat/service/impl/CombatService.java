package com.youxigu.dynasty2.combat.service.impl;

import java.sql.Timestamp;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibatis.sqlmap.engine.cache.CacheModel;
import com.ibatis.sqlmap.engine.cache.memcached.MemcachedManager;
import com.youxigu.dynasty2.armyout.domain.Armyout;
import com.youxigu.dynasty2.armyout.domain.Strategy;
import com.youxigu.dynasty2.armyout.service.IArmyoutService;
import com.youxigu.dynasty2.combat.dao.ICombatDao;
import com.youxigu.dynasty2.combat.domain.Combat;
import com.youxigu.dynasty2.combat.domain.CombatConstants;
import com.youxigu.dynasty2.combat.domain.CombatDB;
import com.youxigu.dynasty2.combat.domain.CombatTeam;
import com.youxigu.dynasty2.combat.proto.CombatMsg;
import com.youxigu.dynasty2.combat.service.IAtferCombatService;
import com.youxigu.dynasty2.combat.service.ICombatEngine;
import com.youxigu.dynasty2.combat.service.ICombatService;
import com.youxigu.dynasty2.combat.service.ICombatTeamService;
import com.youxigu.dynasty2.develop.domain.Castle;
import com.youxigu.dynasty2.develop.service.ICastleService;
import com.youxigu.dynasty2.hero.service.IHeroService;
import com.youxigu.dynasty2.hero.service.ITroopService;
import com.youxigu.dynasty2.map.domain.MapCell;
import com.youxigu.dynasty2.map.service.IMapService;
import com.youxigu.dynasty2.user.domain.User;
import com.youxigu.dynasty2.user.service.IUserService;
import com.youxigu.dynasty2.util.CompressUtils;

public class CombatService implements ICombatService {
	public static final Logger log = LoggerFactory
			.getLogger(CombatService.class);

	// 战斗对象在缓存中保存的时间
	protected int cacheSeconds = 60 * 60;// 10分钟
	// 战斗对象在数据库中保存的天数
	protected int cacheDays = 7;// 7天

	/**
	 * CombatTeam构造工厂
	 */
	private Map<Short, ICombatTeamService> teamFactorys;

	/**
	 * 战斗后处理器
	 */
	private Map<Short, IAtferCombatService> atferCombatFactorys;

	private ICombatDao combatDao;
	private ICombatEngine combatEngine;
	private IArmyoutService armyoutService;
	private ICastleService castleService;
	private ITroopService troopService;
	private IHeroService heroService;
	private IMapService mapService;
	private IUserService userService;

	public void setCombatDao(ICombatDao combatDao) {
		this.combatDao = combatDao;
	}

	public void setCacheSeconds(int cacheSeconds) {
		this.cacheSeconds = cacheSeconds;
	}

	public void setCacheDays(int cacheDays) {
		this.cacheDays = cacheDays;
	}

	public void setCombatEngine(ICombatEngine combatEngine) {
		this.combatEngine = combatEngine;
	}

	public void setArmyoutService(IArmyoutService armyoutService) {
		this.armyoutService = armyoutService;
	}

	public void setCastleService(ICastleService castleService) {
		this.castleService = castleService;
	}

	public void setTroopService(ITroopService troopService) {
		this.troopService = troopService;
	}

	public void setHeroService(IHeroService heroService) {
		this.heroService = heroService;
	}

	public void setMapService(IMapService mapService) {
		this.mapService = mapService;
	}

	public void setUserService(IUserService userService) {
		this.userService = userService;
	}

	@Override
	public Combat getCombat(String combatId) {
		return getCombat(combatId, false);
	}

	@Override
	public CombatMsg.Combat getCombatPf(String combatId) {
		return getCombatPf(combatId, false);
	}

	public void setTeamFactorys(Map<Short, ICombatTeamService> teamFactorys) {
		this.teamFactorys = teamFactorys;
	}

	public void setAtferCombatFactorys(
			Map<Short, IAtferCombatService> atferCombatFactorys) {
		this.atferCombatFactorys = atferCombatFactorys;
	}

	@Override
	public Combat getCombat(String combatId, boolean db) {
		Combat combat = (Combat) MemcachedManager.get(combatId,
				CacheModel.CACHE_REMOTE);
		if (combat == null && db) {
			CombatDB cb = combatDao.getCombatDB(combatId);
			if (cb != null) {
				combat = (Combat) CompressUtils
						.decompressAndDeSerialize(cb.getCombatData());
				if (combat != null) {
					MemcachedManager.set(combatId, combat,
							CacheModel.CACHE_REMOTE, cacheSeconds);
				}
			}
		}
		return combat;

	}

	@Override
	public CombatMsg.Combat getCombatPf(String combatId, boolean db) {
		byte[] datas = (byte[]) MemcachedManager.get(combatId,
				CacheModel.CACHE_REMOTE);
		if (datas == null && db) {
			CombatDB cb = combatDao.getCombatDB(combatId);
			if (cb != null) {
				datas = cb.getCombatData();
				if (datas != null) {
					MemcachedManager.set(combatId, datas,
							CacheModel.CACHE_REMOTE, cacheSeconds);
				}
			}
		}

		CombatMsg.Combat combat = null;
		if (datas != null) {
			try {
				combat = CombatMsg.Combat.parseFrom(datas);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return combat;
	}

	@Override
	public void saveCombat(Combat combat) {
		saveCombat(combat, false);
	}

	@Override
	public void saveCombatPf(Combat combat) {
		saveCombatPf(combat, false);
	}

	@Override
	public void saveCombat(Combat combat, boolean db) {
		MemcachedManager.set(combat.getCombatId(), combat,
				CacheModel.CACHE_REMOTE, cacheSeconds);
		if (db) {
			byte[] datas = CompressUtils.serializeAndCompress(combat);
			CombatDB cb = new CombatDB();
			cb.setCombatId(combat.getCombatId());
			cb.setCombatDt(new Timestamp(System.currentTimeMillis()));
			cb.setCombatData(datas);

			combatDao.saveCombatDB(cb);
		}
	}

	@Override
	public void saveCombatPf(Combat combat, boolean db) {
		if (combat == null) {
			return;
		}
		byte[] datas = combat.toCombat().toByteArray();
		MemcachedManager.set(combat.getCombatId(), datas,
				CacheModel.CACHE_REMOTE, cacheSeconds);
		if (db) {
			CombatDB cb = new CombatDB();
			cb.setCombatId(combat.getCombatId());
			cb.setCombatDt(new Timestamp(System.currentTimeMillis()));
			cb.setCombatData(datas);
			combatDao.saveCombatDB(cb);
		}
	}

	@Override
	public void deleteCombat(String combatId, boolean onlyDB) {
		if (!onlyDB) {
			MemcachedManager.delete(combatId, CacheModel.CACHE_REMOTE);
		}
		combatDao.deleteCombatDBById(combatId);
	}

	@Override
	public void cleanCombat() {
		combatDao.deleteCombatDBByDate(cacheDays);
	}

	@Override
	public void execCombat(Armyout armyout) {
		// timeaction 设置为无效
		armyout.parent.setInValid(true);

		Combat combat = null;
		// 判断类型
		short combatType = 0;
		if (armyout.getAttackerType() == Armyout.PLAER_TYPE
				&& armyout.getDefenderType() == Armyout.PLAER_TYPE) {
			combatType = CombatConstants.COMBAT_TYPE_PVP_ROB;
		} else if (armyout.getAttackerType() == Armyout.PLAER_TYPE
				&& armyout.getDefenderType() == Armyout.ENVIRONMENT_TYPE) {
			combatType = CombatConstants.COMBAT_TYPE_PVE_ROB;
		} else if (armyout.getAttackerType() == Armyout.ENVIRONMENT_TYPE
				&& armyout.getDefenderType() == Armyout.PLAER_TYPE) {
			combatType = CombatConstants.COMBAT_TYPE_EVP_ROB;
		}

		if (combatType == 0) {
			log.error("不支持的战斗类型");
			return;
		}

		// 进攻方队伍factory
		ICombatTeamService attackFactory = teamFactorys
				.get(armyout.getAttackerType());
		if (attackFactory == null) {
			log.error("数据错误，不能获取进攻方战斗方信息");
			return;
		}

		// 目标所在的坐标
		MapCell toCell = armyout.parent.toCell;

		// 防守方factory
		CombatTeam defenceTeam = null;
		CombatTeam attackTeam = null;
		ICombatTeamService defenderFactory = teamFactorys
				.get(armyout.getDefenderType());
		if (defenderFactory == null) {
			log.error("数据错误，不能获取防守方战斗方信息");
			return;
		}

		// String lockKey = COMBAT_LOCKER + armyOut.getDefenderId();
		try {
			// TODO 没想好要不要锁
			defenceTeam = defenderFactory
					.getNextDefenceCombatTeam(armyout.getDefenderCasId());

			if (defenceTeam == null) {
				log.error("没有防守队伍");
				return;
			}

			// 为防守设置一个当前所在的坐标
			defenceTeam.setPosX(toCell.getPosX());
			defenceTeam.setPosY(toCell.getPosY());

			// 攻击方 TODO:这里不要循环取attackTeam,似乎做一个clone更好
			attackTeam = attackFactory.getCombatTeam(armyout.getAttackerCasId(),
					armyout.getTroopId(), false);
			if (attackTeam == null) {
				log.debug("没有进攻队伍");
				return;
			}
			attackTeam.setPosX(armyout.parent.fromCell.getPosX());
			attackTeam.setPosY(armyout.parent.fromCell.getPosY());

			IAtferCombatService afterService = atferCombatFactorys
					.get(combatType);
			boolean allEnd = false;
			// 获得有效的防守团队，直到没有下一个防守团队
			while (!allEnd) {
				long time = System.currentTimeMillis();

				combat = new Combat(combatType, CombatConstants.SCORETYPE_HP,
						attackTeam, defenceTeam);

				combatEngine.execCombat(combat);

				if (log.isDebugEnabled()) {
					log.debug("一场战斗{}的战斗时间:{}", combat.getCombatId(),
							(System.currentTimeMillis() - time));
					// log.debug(combat.showLog());
				}

				if (combat.getWinType() == CombatConstants.WIN_DEF) {
					// 进攻方失败，终止
					allEnd = true;
				}

				if (afterService != null) {
					// 单场战斗结束
					afterService.doSaveAfterCombat(combat, allEnd, null);
					if (log.isDebugEnabled()) {
						log.debug("一场战斗总时间:"
								+ (System.currentTimeMillis() - time));
					}

				}
				// 没终止,找下一个防守队伍
				if (!allEnd) {
					defenceTeam = defenderFactory.getNextDefenceCombatTeam(
							armyout.getDefenderCasId());
					// 没有其他防守方，终止
					if (defenceTeam == null) {
						allEnd = true;
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.error("exception:", e);
		} /*
			 * finally { MemcachedManager.releaseLock(lockKey); }
			 */
	}

	@Override
	public Armyout doStartArmyoutBackJob(Armyout armyout) {
		return doStartArmyoutBackJob(armyout, Strategy.COMMAND_205, null);
	}

	@Override
	public Armyout doStartArmyoutBackJob(Armyout armyout, int outType) {
		return doStartArmyoutBackJob(armyout, outType, null);
	}

	@Override
	public Armyout doStartArmyoutBackJob(Armyout armyout, int outType,
			String combatId) {
		// timeaction 设置为无效
		armyout.parent.setInValid(true);

		// 锁出征
		Armyout armyout1 = armyoutService.lockArmyout(armyout);
		if (armyout1 != null) {
			armyout = armyout1;
		}

		// 前进或者协防状态可以召回
		if (armyout.getStatus() == Armyout.STATUS_FORWARD
				|| armyout.getStatus() == Armyout.STATUS_STAY) {
			long now = System.currentTimeMillis();
			long distanceTime = 0;
			if (armyout.getStatus() == Armyout.STATUS_FORWARD) { // 前进状态的强制返回
				distanceTime = now - armyout.getOutDttm().getTime();
			} else {// 返回
				distanceTime = armyout.getBaseTime() * 1000L;
			}

			// TODO 修改回城时间的计算方法需要一起修改强行策略中关于回城时间的计算
			Timestamp outbackDttm = new Timestamp(now + distanceTime);

			if (armyout.getAttackerType() == Armyout.PLAER_TYPE) {
				Castle castle = castleService
						.getCastleById(armyout.getAttackerCasId());
				if (castle == null) {
					// 玩家基地已经消失，要不要做啥处理那
					return armyout;
				}
				// long userId = castle.getUserId();
				//
				// if (armyout.getTroopId() > 0) {
				// troopService.updateTroopStatus(userId, armyout.getTroopId(),
				// TroopIdle.STATUS_OUT, outbackDttm);
				// }
			}

			armyout.setOutType(outType);
			armyout.setStatus(Armyout.STATUS_BACK);
			if (combatId != null) {
				armyout.setCombatId(combatId);
			}
			armyout.setOutArriveDttm(new Timestamp(now));
			armyout.setOutBackDttm(outbackDttm);
			armyoutService.updateArmyout(armyout);

			// 回城进入线程队列
			// 要不要重新取一遍那
			Strategy strategy = armyoutService.getStrategy(outType);
			// MapCell fromCell = mapService
			// .getMapCellForRead(armyout.getFromCellId());
			// MapCell toCell = mapService
			// .getMapCellForRead(armyout.getToCellId());
			User fromUser = userService
					.getUserById(armyout.getAttackerUserId());
			armyoutService.doArmtoutJoinExeQueue(armyout, strategy,
					armyout.parent.fromCell, armyout.parent.toCell, fromUser);

			if (log.isDebugEnabled()) {
				log.debug("开始一次出征返回,攻击者:{},返回城池时间:{}",
						armyout.getAttackerCasId(), armyout.getOutBackDttm());
			}

		} else {
			log.error("出征状态错误，无法进行出征返回:{}->{}", armyout.getAttackerCasId(),
					armyout.getDefenderCasId());
		}
		return armyout;
	}
}
