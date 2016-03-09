package com.youxigu.dynasty2.hero.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibatis.sqlmap.engine.cache.memcached.MemcachedManager;
import com.youxigu.dynasty2.chat.ChatChannelManager;
import com.youxigu.dynasty2.chat.EventMessage;
import com.youxigu.dynasty2.chat.client.IChatClientService;
import com.youxigu.dynasty2.common.AppConstants;
import com.youxigu.dynasty2.common.service.ICommonService;
import com.youxigu.dynasty2.core.event.Event;
import com.youxigu.dynasty2.core.event.EventDispatcher;
import com.youxigu.dynasty2.core.event.EventTypeConstants;
import com.youxigu.dynasty2.core.event.IEventListener;
import com.youxigu.dynasty2.develop.domain.CastleArmy;
import com.youxigu.dynasty2.develop.service.ICastleArmyService;
import com.youxigu.dynasty2.entity.domain.DropPackEntity;
import com.youxigu.dynasty2.entity.domain.DroppedEntity;
import com.youxigu.dynasty2.entity.domain.Entity;
import com.youxigu.dynasty2.entity.domain.HeroSkill;
import com.youxigu.dynasty2.entity.domain.Item;
import com.youxigu.dynasty2.entity.domain.SysHero;
import com.youxigu.dynasty2.entity.domain.effect.EffectTypeDefine;
import com.youxigu.dynasty2.entity.domain.enumer.ColorType;
import com.youxigu.dynasty2.entity.domain.enumer.ItemType;
import com.youxigu.dynasty2.entity.service.IEntityService;
import com.youxigu.dynasty2.entity.service.ISysHeroService;
import com.youxigu.dynasty2.hero.dao.IHeroDao;
import com.youxigu.dynasty2.hero.domain.CommanderColorProperty;
import com.youxigu.dynasty2.hero.domain.Hero;
import com.youxigu.dynasty2.hero.domain.HeroStrength;
import com.youxigu.dynasty2.hero.domain.RelifeHeroBackItem;
import com.youxigu.dynasty2.hero.domain.RelifeLimit;
import com.youxigu.dynasty2.hero.domain.Troop;
import com.youxigu.dynasty2.hero.enums.HeroIdle;
import com.youxigu.dynasty2.hero.enums.HeroTroopIng;
import com.youxigu.dynasty2.hero.proto.EffectValueMsg;
import com.youxigu.dynasty2.hero.proto.HeroCardAndDebris;
import com.youxigu.dynasty2.hero.proto.HeroFlushView;
import com.youxigu.dynasty2.hero.proto.HeroMsg.ExpItem;
import com.youxigu.dynasty2.hero.proto.HeroMsg.HeroArmyInfo;
import com.youxigu.dynasty2.hero.proto.SendHeroCardAndDebrisMsg;
import com.youxigu.dynasty2.hero.service.IHeroService;
import com.youxigu.dynasty2.hero.service.ITroopService;
import com.youxigu.dynasty2.log.ILogService;
import com.youxigu.dynasty2.log.LogBeanFactory;
import com.youxigu.dynasty2.log.LogTypeConst;
import com.youxigu.dynasty2.log.imp.LogCashAct;
import com.youxigu.dynasty2.log.imp.LogHeroAct;
import com.youxigu.dynasty2.log.imp.LogItemAct;
import com.youxigu.dynasty2.mission.domain.Mission;
import com.youxigu.dynasty2.mission.service.IMissionService;
import com.youxigu.dynasty2.treasury.domain.DropPackItemInfo;
import com.youxigu.dynasty2.treasury.domain.Treasury;
import com.youxigu.dynasty2.treasury.service.ITreasuryService;
import com.youxigu.dynasty2.user.domain.Account;
import com.youxigu.dynasty2.user.domain.AchieveType;
import com.youxigu.dynasty2.user.domain.LvParaLimit;
import com.youxigu.dynasty2.user.domain.User;
import com.youxigu.dynasty2.user.service.IAccountService;
import com.youxigu.dynasty2.user.service.IUserAchieveService;
import com.youxigu.dynasty2.user.service.IUserService;
import com.youxigu.dynasty2.util.BaseException;
import com.youxigu.dynasty2.util.EffectValue;
import com.youxigu.dynasty2.util.StringUtils;
import com.youxigu.wolf.net.OnlineUserSessionManager;
import com.youxigu.wolf.net.UserSession;

import edu.emory.mathcs.backport.java.util.Collections;

public class HeroService implements IHeroService, IEventListener {
	private static final Logger log = LoggerFactory
			.getLogger(HeroService.class);
	private IUserService userService = null;
	private ILogService logService = null;
	private IEntityService entityService = null;
	private IAccountService accountService = null;
	private IHeroDao heroDao = null;
	private ITreasuryService treasuryService = null;
	private IChatClientService messageService;
	private IMissionService missionService = null;
	private ICommonService commonService = null;
	private ICastleArmyService castleArmyService = null;
	private ITroopService troopService = null;
	private ISysHeroService sysHeroEntityService = null;
	private IUserAchieveService userAchieveService;

	private Map<Integer, Integer> expMap = new HashMap<Integer, Integer>();// 武将级别-经验
	private Map<Integer, Integer> expSumMap = new HashMap<Integer, Integer>();// 武将级别-累计需要的经验
	// 重生消耗元宝
	private Map<ColorType, Integer> reBirthCash = null;
	// 武将经验卡，主要是重生的时候退换武将经验卡操作用
	private List<HeroExpCard> heroExpCard = null;

	// 指挥官的品质映射品质属性配数
	private Map<Integer, CommanderColorProperty> color4Map = new HashMap<Integer, CommanderColorProperty>();

	private class HeroExpCard implements Comparable<HeroExpCard> {
		public int entId;
		public int exp;

		public HeroExpCard(int entId, int exp) {
			this.entId = entId;
			this.exp = exp;
		}

		@Override
		public int compareTo(HeroExpCard o) {
			return o.exp - exp;
		}
	}

	protected void init() {
		EventDispatcher.registerListener(EventTypeConstants.EVT_USER_LEVEL_UP,
				this);
		EventDispatcher.registerListener(
				EventTypeConstants.EVT_USER_INFO_CHANGE, this);
		initExpMap();
		String str = commonService
				.getSysParaValue(AppConstants.SYS_HERO_REBIRTH_CASH);
		reBirthCash = new HashMap<ColorType, Integer>();
		if (!StringUtils.isEmpty(str)) {
			String[] arr = StringUtils.split(str, ";");
			for (int i = 0; i < arr.length; i++) {
				String v = arr[i];
				String[] vl = v.split(":");
				if (vl.length != 2) {
					throw new BaseException("武将重生消耗元宝配数错误");
				}
				ColorType t = ColorType.valueOf(Integer.parseInt(vl[0]));
				if (t == null) {
					throw new BaseException("武将重生消耗元宝配数错误,品质部存在" + vl[0]);
				}
				reBirthCash.put(t, Integer.parseInt(vl[1]));
			}
		}

		if (reBirthCash.size() != ColorType.values().length) {
			throw new BaseException("武将重生消耗元宝配数错误,应该配置7个品质的消耗元宝");
		}

		heroExpCard = new ArrayList<HeroService.HeroExpCard>();
		String expStr = commonService
				.getSysParaValue(AppConstants.SYS_HERO_REBIRTH_EXP_CARD);
		if (!StringUtils.isEmpty(expStr)) {
			String s[] = expStr.split(";");
			for (String ep : s) {
				String rt[] = ep.split(":");
				HeroExpCard hp = new HeroService.HeroExpCard(
						Integer.valueOf(rt[0]), Integer.valueOf(rt[1]));
				this.heroExpCard.add(hp);
			}
		}
		Collections.sort(heroExpCard);

		// 指挥官品质对应的属性
		List<CommanderColorProperty> colorProps = heroDao
				.listCommanderColorPropertys();
		if (colorProps != null && colorProps.size() > 0) {
			for (CommanderColorProperty ccp : colorProps) {
				color4Map.put(ccp.getColor(), ccp);
			}
		}
	}

	private void initExpMap() {
		List<Map<Integer, Integer>> list = heroDao.getHeroExp();
		int size = list.size();
		if (size <= 0) {
			throw new IllegalArgumentException("未配置武将升级经验");
		}
		for (Map<Integer, Integer> map : list) {
			expMap.put(map.get("level"), map.get("exp"));
		}
		int sum = 0;
		expSumMap.put(1, 0);
		for (int i = 2; i <= size; i++) {
			if (!expMap.containsKey(i)) {
				throw new IllegalArgumentException("配置的武将升级经验等级不连续");
			}
			sum += expMap.get(i - 1);// 升到当前级需要多少经验
			expSumMap.put(i, sum);
		}
		log.info("加载武将升级经验配数数据完毕");
	}

	/**
	 * !!!!这个方法没有锁，调用的要注意
	 */
	@Override
	public int doChangeHeroExp(Hero hero, int exp, boolean isUserOpt) {
		return doChangeHeroExp(hero, exp, isUserOpt, false);
	}

	@Override
	public int doChangeHeroExp(Hero hero, int exp, boolean isUserOpt,
			boolean alreadyAddEff) {
		if (hero == null) {
			if (isUserOpt) {
				throw new BaseException("武将不存在。");
			}
			return exp;
		}
		if (!hero.idle()) {
			if (isUserOpt) {
				throw new BaseException("武将必须是空闲状态");
			}
		}

		if (exp <= 0) {
			if (isUserOpt) {
				throw new BaseException("输入不合法。");
			}
			return exp;
		}

		// RelifeLimit relifeLimit = relifeLimitList.get(hero.getRelifeNum());
		// int maxLv = relifeLimit.getLevelRequest();
		// if (hero.getLevel() == maxLv) {
		// if (isUserOpt) {
		// throw new BaseException("武将已达到最大等级。");
		// }
		// return exp;
		// }
		User user = userService.getUserById(hero.getUserId());
		if (isUserOpt) {// 经验池分配经验
			// if (user != null && user.getExpPoint() < exp) {
			// throw new BaseException("经验池中经验不足。");
			// }
			// userService.updateUserExpPoint(hero.getUserId(), exp * -1);
			//
			// LvParaLimit lvp = userService.getLvParaLimit(user.getUsrLv());
			// if (lvp != null) {
			// int maxLv = lvp.getHeroLvLimit();
			// if (user.getUsrLv() >= maxLv) {
			// throw new BaseException("武将等级不可超过君主等级，请提升君主等级。");
			// }
			// }
			// userDailyActivityService.addUserDailyActivity(user.getUserId(),
			// DailyActivity.ACT_HERO_EXP, (byte) 1);

		}

		// 取得加成后的武将经验
		if (!isUserOpt) {
			if (!alreadyAddEff) {
				exp = this.getActualExp(exp, hero.getUserId(), hero.getCasId(),
						null);
			}
		}
		int oldlevel = hero.getLevel();// 原先的等级
		int oldExp = hero.getExp();
		int newExp = oldExp + exp;
		if (newExp < 0 || newExp > MAX_EXP) {
			newExp = MAX_EXP;
			exp = MAX_EXP - oldExp;
		}
		hero.setExp(newExp);
		hero = this.levelUpHero(hero, user);
		this.updateHero(hero, true);
		// 发提示消息
		StringBuilder sb = new StringBuilder(64);
		sb.append(hero.getHrefStr()).append("得到经验").append(exp);
		if (oldlevel != hero.getLevel()) {
			sb.append("，从").append(oldlevel).append("级升级到")
					.append(hero.getLevel()).append("级。");
		}
		String msg = sb.toString();
		messageService.sendMessage(0, hero.getUserId(),
				ChatChannelManager.CHANNEL_SYSTEM, null, msg);
		if (log.isDebugEnabled()) {
			log.debug(msg + "_userId[" + hero.getUserId() + "]_heroId["
					+ hero.getHeroId() + "]");
		}
		return exp;
	}

	/**
	 * 增加经验\升级
	 * 
	 * @param hero
	 */
	private Hero levelUpHero(Hero hero, User user) {
		if (!isOpenBarHero()) {
			throw new BaseException("功能未开放");
		}
		// RelifeLimit relifeLimit = relifeLimitList.get(hero.getRelifeNum());

		if (user == null) {
			user = userService.getUserById(hero.getUserId());
		}
		LvParaLimit lvp = userService.getLvParaLimit(user.getUsrLv());
		int maxLv = 0;
		if (lvp != null) {
			maxLv = lvp.getHeroLvLimit();
		}
		int oldLevel = hero.getLevel();// 原等级
		if (maxLv == oldLevel) {
			return hero;
		}
		if (hero.getExp() <= 0) {
			// hero.setExp(0);
			// heroDao.updateHero(hero);
			return hero;
		}
		int newLevel = oldLevel;
		int newExp = hero.getExp();// 剩余的exp
		int curr = expMap.get(newLevel);
		while (newLevel < maxLv && newExp >= curr) {
			newExp = newExp - curr;
			newLevel++;
			curr = expMap.get(newLevel);
		}
		// 要求保留剩余经验
		// if (newLevel == maxLv) {// 不要剩下的经验
		// hero.setExp(0);
		// } else {
		// hero.setExp(newExp);
		// }
		hero.setExp(newExp);
		hero.setLevel(newLevel);

		if (oldLevel != newLevel) {// 武将升级了
			// 升级导致基础属性变化
			// int growing = hero.getGrowing();
			// recalcHeroBaseProp(hero, growing);
			missionService.notifyMissionModule(user,
					Mission.QCT_TYPE_HEROLEVEL, 0, hero.getLevel());
			missionService.notifyMissionModule(user,
					Mission.QCT_TYPE_HERONUMLEVEL, hero.getLevel(), 0);

			// X辆Z级Y品质坦克
			userAchieveService.doNotifyAchieveModule(user.getUserId(),
					AchieveType.TYPE_MILITARY,
					AchieveType.TYPE_MILITARY_ENTID4, 0);

		}

		return hero;
	}

	/**
	 * 重新计算英雄的基本属性...存库的...
	 * 
	 * @param hero
	 * @param growing
	 */
	private void recalcHeroBaseProp(Hero hero, int growing) {
		// 默认系统武将的5维属性
		SysHero sysHero = hero.getSysHero();
		int initHp = sysHero.getInitHp();
		int magicAttack = sysHero.getMagicAttack();// 法术攻击
		int physicalAttack = sysHero.getPhysicalAttack();// 物理攻击
		int magicDefend = sysHero.getMagicDefend();// 法术防御
		int physicalDefend = sysHero.getPhysicalDefend();// 物理防御

		// 指挥官的5维属性
		boolean isUserHero = hero.isCommander();
		if (isUserHero) {
			CommanderColorProperty ccp = this.getCommanderColorProperty(hero
					.getUserId());
			if (ccp != null) {
				growing = ccp.getGrowing();
				initHp = ccp.getInitHp();
				magicAttack = ccp.getMagicAttack();
				physicalAttack = ccp.getPhysicalAttack();
				magicDefend = ccp.getMagicDefend();
				physicalDefend = ccp.getPhysicalDefend();
			}
		}

		// 获取基本属性
		float base = 1000;
		int level = hero.getLevel();
		int _hp = (int) ((growing / base) * initHp * level + initHp);
		int _magicAttack = (int) ((growing / base) * magicAttack * level + magicAttack);
		int _physicalAttack = (int) ((growing / base) * physicalAttack * level + physicalAttack);
		int _magicDefend = (int) ((growing / base) * magicDefend * level + magicDefend);
		int _physicalDefend = (int) ((growing / base) * physicalDefend * level + physicalDefend);

		hero.setPhysicalAttack(_physicalAttack);
		hero.setMagicAttack(_magicAttack);
		hero.setPhysicalDefend(_physicalDefend);
		hero.setMagicDefend(_magicDefend);
		hero.setIntHp(_hp);
	}

	@Override
	public Hero lockAndGetHero(long userId, long heroId) {
		lockHero(userId);
		return heroDao.getHeroByHeroId(userId, heroId);
	}

	/**
	 * @return 酒馆功能开关
	 */
	private boolean isOpenBarHero() {
		int opened = commonService.getSysParaIntValue(
				AppConstants.SYS_USERPUBATTR_OPEN_STATUS,
				AppConstants.SYS_OPNE_STATUS_DEFAULTVALUE);
		return opened == 1;
	}

	@Override
	public SysHero getSysHeroById(int sysHeroId) {
		Entity ent = entityService.getEntity(sysHeroId);
		if (ent == null) {
			throw new BaseException("系统武将不存在" + sysHeroId);
		}
		return (SysHero) ent;
	}

	@Override
	public Hero getHeroByHeroId(long userId, long heroId) {
		Hero hero = heroDao.getHeroByHeroId(userId, heroId);
		if (hero == null) {
			return null;
		}
		if (!hero.isHero()) {
			throw new BaseException("不是武将数据");
		}
		return hero;
	}

	@Override
	public Hero getHeroCardByHeroId(long userId, long heroId) {
		Hero hero = heroDao.getHeroByHeroId(userId, heroId);
		return hero;
	}

	@Override
	public HeroSkill getHeroSkill(int heroSkillId) {
		return (HeroSkill) entityService.getEntity(heroSkillId);
	}

	@Override
	public Hero getHeroBySysHeroId(long userId, int sysHeroId) {
		// List<Hero> heroList = getUserHeroList(userId);
		// if (heroList != null)
		// for (Hero h : heroList) {
		// if (h.getSysHeroId() == sysHeroId)
		// return h;
		// }
		return heroDao.getHeroBySysHeroId(userId, sysHeroId);
	}

	@Override
	public List<Hero> getUserHeroList(long userId) {
		List<Hero> hero = heroDao.getHeroListByUserId(userId);
		List<Hero> rs = new ArrayList<Hero>();
		if (hero != null && !hero.isEmpty()) {
			for (Hero h : hero) {
				if (h.isHero()) {
					rs.add(h);
				}
			}
		}
		return rs;
	}

	@Override
	public void lockHero(long userId) {
		try {
			MemcachedManager.lock("HERO_" + userId, "hero_");
		} catch (TimeoutException e) {
			throw new BaseException(e);
		}
	}

	@Override
	public Hero doCreateAHero(long userId, int sysHeroId, LogHeroAct reason) {
		return this.doCreateAHero(userId, sysHeroId, false, reason);
	}

	@Override
	public Hero doCreateAHero(long userId, int sysHeroId, boolean isCommander,
			LogHeroAct reason) {
		User user = userService.getUserById(userId);
		this.lockHero(userId);
		Hero src = null;// 原来拥有的武将
		List<Hero> heroList = heroDao.getHeroListByUserId(userId);
		if (heroList != null) {
			for (Hero h : heroList) {
				if (h.getSysHeroId() == sysHeroId) {
					src = h;
					break;
				}
			}
		}

		SysHero sysHero = this.getSysHeroById(sysHeroId);
		String msg = null;
		if (src != null && src.isHero()) {// 如果原来的武将不是武将卡或者碎片
			Item temp = (Item) entityService.getEntity(sysHero.getHeroCardId());
			this.doAddHeroCardNum(userId, src, temp, 1,
					LogItemAct.LOGITEMACT_146);
			logService
					.log(LogBeanFactory.buildGetHeroLog(user,
							sysHero.getHeroCardId(), temp.getItemName(), 1,
							reason.desc));
			return null;
		}
		boolean update = true;
		if (src == null) {
			update = false;
			// 原来没有武将。。创建一个
			src = new Hero(sysHero, userId);
		} else {
			// 有了。。但是只是武将卡或者碎片更新成武将
			src.initHero(sysHero, userId);
		}
		recalcHeroBaseProp(src, src.getGrowing());

		// 设置指挥官标示
		if (isCommander) {
			src.setCommander((byte) 1);
		}

		if (update) {
			heroDao.updateHero(src);
		} else {
			heroDao.insertHero(src);

			if (src.isHero()) {
				// 拥有N个Y品质的武将missionLimit(entId:color)：系统武将的color/0
				missionService.notifyMissionModule(user,
						Mission.QCT_TYPE_HERONUMCOLOR, sysHero.getColorType()
								.getIndex(), 0);

				// 坦克收集
				userAchieveService.doNotifyAchieveModule(userId,
						AchieveType.TYPE_MILITARY,
						AchieveType.TYPE_MILITARY_ENTID3, 1);

				// X辆Z级Y品质坦克
				userAchieveService.doNotifyAchieveModule(userId,
						AchieveType.TYPE_MILITARY,
						AchieveType.TYPE_MILITARY_ENTID4, 1);
			}
		}
		msg = new StringBuilder(128).append("增加一名武将").append(src.getHrefStr())
				.append("。").toString();

		// missionService.notifyMissionModule(user,
		// Mission.QCT_TYPE_HERO_RECRUIT, 0, (hiredNum + 1));

		this.sendFlushHeroEvent(src);
		logService.log(LogBeanFactory.buildGetHeroLog(user, sysHero.getEntId(),
				sysHero.getName(), 0, reason.desc));

		// （1） 玩家获得橙色及以上武将：XXX精诚所至，将XXX（武将名）招至麾下！获得方式包括：酒馆招将、商店购买、碎片合成、签到获得。
		if (sysHero.getEvaluate() >= 5) {
			messageService.sendMessage(0, 0, ChatChannelManager.CHANNEL_NOTICE,
					"system", msg);
		}

		messageService.sendMessage(0, src.getUserId(),
				ChatChannelManager.CHANNEL_SYSTEM, null, msg);
		if (log.isDebugEnabled()) {
			log.debug(msg + "_userId[" + src.getUserId() + "]_heroId["
					+ src.getHeroId() + "]");
		}
		// missionService.notifyMissionModule(user, Mission.QCT_TYPE_HEROARMY,
		// 0,
		// 1);
		return src;
	}

	public Hero doLevelUpHero(Hero hero, User user) {
		if (!isOpenBarHero()) {
			throw new BaseException("功能未开放");
		}
		if (user == null) {
			user = userService.getUserById(hero.getUserId());
		}
		LvParaLimit lvp = userService.getLvParaLimit(user.getUsrLv());
		int maxLv = 0;
		if (lvp != null) {
			maxLv = lvp.getHeroLvLimit();
		}
		int oldLevel = hero.getLevel();// 原等级
		if (maxLv == oldLevel) {
			return hero;
		}
		if (hero.getExp() <= 0) {
			return hero;
		}
		int newLevel = oldLevel;
		int newExp = hero.getExp();// 剩余的exp
		int curr = expMap.get(newLevel);
		while (newLevel < maxLv && newExp >= curr) {
			newExp = newExp - curr;
			newLevel++;
			curr = expMap.get(newLevel);
		}
		// 要求保留剩余经验
		hero.setExp(newExp);
		hero.setLevel(newLevel);

		if (oldLevel != newLevel) {// 武将升级了
			// 升级导致基础属性变化
			int growing = hero.getGrowing();
			recalcHeroBaseProp(hero, growing);
			missionService.notifyMissionModule(user,
					Mission.QCT_TYPE_HEROLEVEL, 0, hero.getLevel());
			missionService.notifyMissionModule(user,
					Mission.QCT_TYPE_HERONUMLEVEL, hero.getLevel(), 0);

		}
		return hero;
	}

	@SuppressWarnings("unchecked")
	public List<DropPackItemInfo> dropItem(User user, int dropEntId, int num,
			boolean notUpdate, LogItemAct action) {
		List<DropPackItemInfo> retuDatas = null;
		if (dropEntId != 0) {
			Entity entity = entityService.getEntity(dropEntId);
			if (entity != null) {

				if (entity instanceof DropPackEntity
						&& ((DropPackEntity) entity).getChildType() != ItemType.ITEM_TYPE_DROPPACK_CHILD1
								.getIndex()) {

					Map<String, Object> params = new HashMap<String, Object>();
					params.put("user", user);
					params.put("iAction", action);
					if (notUpdate) {
						params.put("notUpdate", 1);
					}
					params.put("num", num);

					Map<String, Object> dropItems = entityService.doAction(
							entity, Entity.ACTION_USE, params);
					if (dropItems != null && dropItems.size() > 0) {
						Map<Integer, DroppedEntity> datas = (Map<Integer, DroppedEntity>) dropItems
								.get("items");
						if (datas != null) {
							Collection<DroppedEntity> dropedEntitys = datas
									.values();
							if (dropedEntitys != null
									&& dropedEntitys.size() > 0) {
								retuDatas = new ArrayList<DropPackItemInfo>(
										dropedEntitys.size());
								Iterator<DroppedEntity> itl = dropedEntitys
										.iterator();

								while (itl.hasNext()) {
									DroppedEntity drop = itl.next();
									DropPackItemInfo info = new DropPackItemInfo(
											drop.getEntId(), drop.getNum());
									retuDatas.add(info);
								}
							}
						}
					}

				} else {
					// 直接掉道具
					throw new BaseException("unsupport");
				}
			} else {
				throw new BaseException("掉落包不存在");
			}
		}
		return retuDatas;
	}

	public void tlog_hero_hire(User user, int mode, SysHero sysHero, int num,
			boolean free) {
		logService.log(LogBeanFactory.buildHeroGetLog(user, mode, sysHero, num,
				free));

	}

	@Override
	public void updateHero(Hero hero, boolean sendMessage) {
		heroDao.updateHero(hero);
		if (sendMessage) {
			this.sendFlushHeroEvent(hero);
		}
	}

	@Override
	public void sendFlushHeroEvent(Hero hero) {
		// event 刷新武将
		messageService.sendEventMessage(hero.getUserId(),
				EventMessage.TYPE_BACK_FRESH_HERO, new HeroFlushView(hero));
	}

	@Override
	public void sendFlushHeroAttrEvent(Hero hero) {
		messageService
				.sendEventMessage(hero.getUserId(),
						EventMessage.TYPE_BACK_FRESH_HERO_ATTR,
						new HeroFlushView(hero));
	}

	@Override
	public Hero updateHeroActionStatus(Hero hero, HeroIdle actionStatus,
			Timestamp freeDttm) {
		if (hero == null)
			return null;
		// 没有锁，外部加锁
		hero.setActionStatus(actionStatus.getIndex());
		hero.setFreeDttm(freeDttm);
		this.updateHero(hero, true);
		return hero;
	}

	@Override
	public int getActualExp(int exp, long userId, long casId, String expType) {
		EffectValue value = getHeroPracticeEffect(userId, casId, expType);
		return getActualExp(exp, userId, value);
	}

	private int getActualExp(int exp, long userId, EffectValue value) {
		exp = Math.max((int) ((exp + value.getAbsValue()) * (1.0d + value
				.getDoublePerValue())), 0);
		return exp;
	}

	public EffectValue getHeroPracticeEffect(long userId, long casId,
			String expType) {
		// 其他经验加成效果
		EffectValue value = new EffectValue();
		return value;
	}

	@Override
	public boolean doHeroStrength(long userId, long heroId) {
		Hero hero = this.lockAndGetHero(userId, heroId);// 锁定这个武将
		if (hero == null) {
			throw new BaseException("武将不存在");
		}
		if (!hero.isInTroop()) {
			throw new BaseException("武将未在军团里面");
		}

		if (!hero.idle()) {
			throw new BaseException("武将必须是空闲状态");
		}
		if (!hero.isHero()) {
			throw new BaseException("不是武将");
		}
		if (hero.isCommander()) {
			throw new BaseException("君主坦克不能强化");
		}
		SysHero sysHero = (SysHero) entityService
				.getEntity(hero.getSysHeroId());
		int relifeNum = hero.getRelifeNum();
		int heroStrength = hero.getHeroStrength();
		// 当前成长
		int currGrowing = hero.getGrowing();
		List<RelifeLimit> relifeLimits = sysHero.getRelifeLimits();
		if (relifeLimits == null) {
			throw new BaseException("武将不能强化");
		}
		RelifeLimit relife = relifeLimits.get(relifeNum);
		if (relife == null) {
			throw new BaseException("达到强化上限,必须进阶后才能再强化");
		}
		List<HeroStrength> hStrength = relife.getHeroStrengthLimits();
		if (hStrength == null) {
			throw new BaseException("强化数据为null");
		}
		// 获取当前进阶等级对应的强化等级
		HeroStrength hs = null;
		if (heroStrength <= 0) {
			hs = relife.getInitHeroStrength();
		} else {
			hs = relife.getHeroStrength(heroStrength);
			if (hs == null || hs.getNextHeroStrength() == null) {
				throw new BaseException("达到强化上限,必须进阶后才能再强化");
			}
			hs = hs.getNextHeroStrength();
		}

		// 消耗道具
		if (hs.getCount1() > 0) {
			int count = treasuryService.getTreasuryCountByEntId(userId,
					hs.getItemId1());
			if (hs.getCount1() > count) {
				throw new BaseException("道具不足,不能强化");
			}
			treasuryService.deleteItemFromTreasury(userId, hs.getItemId1(),
					hs.getCount1(), true, LogItemAct.LOGITEMACT_3);
		}
		if (hs.getCount2() > 0) {
			int count = treasuryService.getTreasuryCountByEntId(userId,
					hs.getItemId2());
			if (hs.getCount2() > count) {
				throw new BaseException("道具不足,不能强化");
			}
			treasuryService.deleteItemFromTreasury(userId, hs.getItemId2(),
					hs.getCount2(), true, LogItemAct.LOGITEMACT_3);
		}
		int oldGrowing = currGrowing;
		currGrowing = currGrowing + hs.getAddGrowing();
		hero.setGrowing(currGrowing);
		hero.setHeroStrength(hs.getId());

		// 由于强化需要的道具是小数，无法保证重生的时候返还一致，只好把强化消耗的保存上
		// hero.setGrowingItem(hero.getGrowingItem());
		recalcHeroBaseProp(hero, currGrowing);
		this.updateHero(hero, true);
		// tlog
		Date now = new Date();
		String via = null;
		String areaId = null;
		UserSession us = OnlineUserSessionManager
				.getUserSessionByUserId(userId);
		User user = userService.getUserById(userId);
		if (us != null) {
			via = us.getVia();
			areaId = us.getAreaId();
		} else {
			Account account = accountService.getAccount(user.getAccId());
			via = account.getVia();
			areaId = account.getAreaId();
		}

		// 强化武将N次,累计值
		missionService.notifyMissionModule(user, Mission.QCT_TYPE_HEROPOWER, 0,
				1);

		logService.log(LogBeanFactory.buildHeroStrengthLog(user, hero,
				oldGrowing, currGrowing, via));
		return true;
	}

	@Override
	public void doRelifeHero(long userId, long mainCasId, long heroId) {
		if (!isOpenBarHero()) {
			throw new BaseException("功能未开放");
		}
		lockHero(userId);// 锁定这个武将

		Hero hero = this.getHeroByHeroId(userId, heroId);
		if (hero == null || hero.getUserId() != userId) {
			throw new BaseException("武将不存在。");
		}

		if (!hero.isInTroop()) {
			throw new BaseException("武将未在军团里面");
		}

		if (!hero.idle()) {
			throw new BaseException("武将必须是空闲状态");
		}
		if (!hero.isHero()) {
			throw new BaseException("不是武将");
		}
		if (hero.isCommander()) {
			throw new BaseException("君主坦克不能进阶");
		}

		// 原成长
		int oldGrowing = hero.getGrowing();

		SysHero sysHero = (SysHero) entityService
				.getEntity(hero.getSysHeroId());

		List<RelifeLimit> relifeLimits = sysHero.getRelifeLimits();
		if (relifeLimits == null) {
			throw new BaseException("该武将不能进阶。");
		}
		int relifeNum = hero.getRelifeNum();
		if (relifeNum >= relifeLimits.size()) {
			throw new BaseException("武将进阶次数已满。");
		}
		RelifeLimit relifeLimit = relifeLimits.get(relifeNum);
		List<HeroStrength> hStrength = relifeLimit.getHeroStrengthLimits();
		if (hStrength == null) {
			throw new BaseException("不能进阶");
		}

		HeroStrength hs = relifeLimit.getHeroStrength(hero.getHeroStrength());
		if (hs == null || hs.getNextHeroStrength() != null) {
			throw new BaseException("强化等级不够,不能进阶");
		}

		// 消耗同名武将卡
		int cardNum = relifeLimit.getHeroCardNum();
		int cardId = sysHero.getHeroCardId();
		if (cardNum != 0 && cardId != 0) {
			this.doDelHeroCardNum(userId, hero, cardNum,
					LogItemAct.LOGITEMACT_142);
		}

		// 更改基础属性:
		int addGrowing = relifeLimit.getAddGrowing();

		// // 进阶次数加1
		relifeNum++;
		hero.setRelifeNum(relifeNum);
		// 新成长
		int newGrowing = addGrowing + oldGrowing;

		hero.setGrowing(newGrowing);
		hero.setHeroStrength(0);// 如果进阶成功。。那么强化就重置为0

		// 重新分配属性
		recalcHeroBaseProp(hero, newGrowing);

		this.updateHero(hero, true);

		// 通知任务
		User user = userService.getUserById(hero.getUserId());
		// missionService.notifyMissionModule(user,
		// Mission.QCT_TYPE_HERO_REBRON,
		// 0, 1);
		// tlog
		Date now = new Date();
		String via = null;
		String areaId = null;
		UserSession us = OnlineUserSessionManager
				.getUserSessionByUserId(userId);

		if (us != null) {
			via = us.getVia();
			areaId = us.getAreaId();
		} else {
			Account account = accountService.getAccount(user.getAccId());
			via = account.getVia();
			areaId = account.getAreaId();
		}

		// 武将进阶N次，累计值
		missionService.notifyMissionModule(user, Mission.QCT_TYPE_RELIFEHERO,
				0, 1);
		logService.log(LogBeanFactory.buildHeroRelifeLog(user, hero, relifeNum,
				via));
	}

	@Override
	public void doHeroRebirth(long userId, List<Long> heroIds, boolean retire) {
		if (!isOpenBarHero()) {
			throw new BaseException("功能未开放");
		}
		User user = userService.lockGetUser(userId);
		List<Hero> heros = new ArrayList<Hero>(heroIds.size());
		for (Long heroId : heroIds) {
			// 锁定这个武将
			Hero hero = this.lockAndGetHero(userId, heroId);
			if (hero == null || hero.getUserId() != userId) {
				throw new BaseException("武将不存在。");
			}

			if (hero.isInTroop()) {
				throw new BaseException("军团的武将不能重生或分解");
			}
			if (!hero.idle()) {
				throw new BaseException("武将必须是空闲状态");
			}

			if (hero.isCommander()) {
				throw new BaseException("君主坦克不能重生和退役");
			}
			int heroLv = hero.getLevel();
			int heroExp = hero.getExp();
			int relifeNum = hero.getRelifeNum();
			int heroStrength = hero.getHeroStrength();
			if (!retire) {
				//
				if (heroLv == 1 && heroExp == 0 && relifeNum == 0
						&& heroStrength == 0) {
					throw new BaseException("武将不需要重生");
				}
			}
			heros.add(hero);
		}
		// 锁住背包，判断格子数量
		treasuryService.lockTreasury(userId);

		for (Hero hero : heros) {
			long heroId = hero.getHeroId();
			int heroLv = hero.getLevel();
			int heroExp = hero.getExp();
			int relifeNum = hero.getRelifeNum();
			int heroStrength = hero.getHeroStrength();
			SysHero sysHero = (SysHero) entityService.getEntity(hero
					.getSysHeroId());
			int baseRelifeNum = 0;
			// 消耗元宝
			if (!retire) {
				Integer cash = reBirthCash.get(sysHero.getColorType());
				if (cash == null) {
					throw new BaseException("未配置武将重生消耗元宝数量");
				}

				if (cash > 0) {
					userService.doConsumeCash(userId, cash,
							LogCashAct.HERO_CHONGSHENG_ACTION);
				}
			}

			int oldLv = hero.getLevel();
			int oldExp = hero.getExp();
			int oldGrowing = hero.getGrowing();
			int oldRelifeNum = hero.getRelifeNum();
			// int freeCount = treasuryService.getBagFreeCount(userId);
			// if (needCount > freeCount) {
			// throw new BaseException("背包已满，不能进行武将重生操作");
			// }
			// 返还经验,资源，道具
			// 升级经验：返还到经验池
			int totalExp = heroExp;
			if (heroExp > 0 || heroLv > 1) {
				// for (int i = 1; i < heroLv; i++) {
				// totalExp = totalExp + expMap.get(i);
				// }
				totalExp += expSumMap.get(heroLv);
				if (totalExp <= 0) {
					throw new BaseException("经验超上限");
				}
				int tmpId = 0;
				int num = 0;
				for (HeroExpCard hp : heroExpCard) {
					num = totalExp / hp.exp;
					if (num > 0) {
						tmpId = hp.entId;
						totalExp -= hp.exp * num;
						Entity en = entityService.getEntity(tmpId);
						if (!(en instanceof Item)) {
							throw new BaseException("退还经验道具错误");
						}
						if (!((Item) en).getItemType().isExpItem()) {
							throw new BaseException("不是经验道具" + tmpId);
						}
						treasuryService.doAddItemToTreasury(userId, tmpId, num);
					}
				}
			}

			Map<Integer, Integer> entIdNumMaps = new HashMap<Integer, Integer>();
			int cardNum = 0;
			// 退还进阶道具
			if (relifeNum > 0) {
				for (int i = 0; i < relifeNum; i++) {
					// 退还每进阶一级的物品和强化的所有物品
					List<RelifeLimit> relifeLimits = sysHero.getRelifeLimits();
					RelifeLimit relifeLimit = relifeLimits.get(i);
					cardNum = cardNum + relifeLimit.getHeroCardNum();

					List<HeroStrength> l = relifeLimit.getHeroStrengthLimits();
					int hs = l.size();
					if (i == relifeNum - 1) {
						// 最近一次的强化数据。的强化等级
						HeroStrength hst = relifeLimit
								.getHeroStrength(heroStrength);
						if (hst == null) {
							// 可能的情况就是 1阶 强化等级0级
							hs = l.size();
						} else {
							hs = hst.getLevel();
						}
					}
					// 退还强化所需要的道具
					handBackHeroStrength(entIdNumMaps, l, hs);
				}
			} else {
				if (heroStrength > 0) {// 0阶但是有强化
					List<RelifeLimit> relifeLimits = sysHero.getRelifeLimits();
					RelifeLimit relifeLimit = relifeLimits.get(0);
					List<HeroStrength> l = relifeLimit.getHeroStrengthLimits();
					HeroStrength hst = relifeLimit
							.getHeroStrength(heroStrength);
					// 退还强化所需要的道具
					handBackHeroStrength(entIdNumMaps, l, hst.getLevel());
				}
			}
			hero.setLevel(1);
			hero.setExp(0);
			hero.setRelifeNum(baseRelifeNum);
			hero.restSkillId();
			// 四维+成长恢复成初始值
			hero.setGrowing(sysHero.getGrowing());
			recalcHeroBaseProp(hero, hero.getGrowing());

			// 强化返还
			if (heroStrength > 0) {
				hero.setHeroStrength(0);
			}
			if (relifeNum > 0) {
				hero.setRelifeNum(0);
			}
			int cardId = sysHero.getHeroCardId();
			LogItemAct log1 = LogItemAct.LOGITEMACT_5;
			LogItemAct log2 = LogItemAct.LOGITEMACT_6;
			if (retire) {
				log1 = LogItemAct.LOGITEMACT_7;
				log2 = LogItemAct.LOGITEMACT_8;
				doAddSumHeroCardNum(userId, hero, 1, LogItemAct.LOGITEMACT_15);

				// 退役获得军备材料
				int num = commonService.getSysParaIntValue(
						AppConstants.HERO_CARD_DISCARD_ENTID_COLOR
								+ hero.getSysHero().getColorType().getIndex(),
						1);

				int itemId = commonService.getSysParaIntValue(
						AppConstants.HERO_CARD_DISCARD_ENTID, 0);
				treasuryService.addItemToTreasury(userId, itemId, num, 1, -1,
						false, true, LogItemAct.LOGITEMACT_53);
			}

			if (cardNum > 0) {
				this.doAddHeroCardNum(userId, hero,
						(Item) entityService.getEntity(cardId), cardNum, log1);
			}

			for (Entry<Integer, Integer> en : entIdNumMaps.entrySet()) {
				treasuryService.addItemToTreasury(userId, en.getKey(),
						en.getValue(), 1, -1, false, true, log2);
			}

			if (retire) {
				// 因为绑定了武将卡。。所以不能删除数据了
				Hero newHero = new Hero(userId, hero.getSysHeroId(),
						hero.getHeroCardEntId(), hero.getHeroCardNum(),
						hero.getHeroSoulEntId(), hero.getHeroSoulNum(),
						hero.getSumHeroCardNum());
				hero.setStatus(HeroTroopIng.HERO_NONE.getIndex());
				newHero.setHeroId(heroId);
				hero = newHero;
				// heroDao.deleteHero(hero);
				updateHero(hero, false);
			} else {
				updateHero(hero, true);
			}
			// tlog
			Date now = new Date();
			String via = null;
			String areaId = null;
			UserSession us = OnlineUserSessionManager
					.getUserSessionByUserId(userId);

			if (us != null) {
				via = us.getVia();
				areaId = us.getAreaId();
			} else {
				Account account = accountService.getAccount(user.getAccId());
				via = account.getVia();
				areaId = account.getAreaId();
			}
			logService.log(LogBeanFactory.buildHeroRebirthLog(user, hero,
					oldLv, oldExp, oldGrowing, oldRelifeNum, totalExp, cardNum,
					via, retire));
		}

		// 通知武将重生N次,累计值
		missionService.notifyMissionModule(user, Mission.QCT_TYPE_REBRON, 0, 1);
	}

	@Override
	public void doHeroRebirth2(long userId, List<Long> heroIds, boolean retire) {
		if (!isOpenBarHero()) {
			throw new BaseException("功能未开放");
		}
		if (heroIds.isEmpty()) {
			return;
		}
		User user = userService.lockGetUser(userId);
		Map<Long, Hero> heros = new HashMap<Long, Hero>(heroIds.size());// 数据排重
		for (Long heroId : heroIds) {
			// 锁定这个武将
			Hero hero = this.lockAndGetHero(userId, heroId);
			if (hero == null || hero.getUserId() != userId) {
				throw new BaseException("武将不存在。");
			}
			if (!hero.isHero()) {
				throw new BaseException("不是武将");
			}
			if (hero.isInTroop()) {
				throw new BaseException("军团的武将不能重生或分解");
			}
			if (!hero.idle()) {
				throw new BaseException("武将必须是空闲状态");
			}

			if (hero.isCommander()) {
				throw new BaseException("君主坦克不能退役或重生");
			}

			if (heros.containsKey(heroId)) {
				throw new BaseException("重复的武将数据");
			}
			int heroLv = hero.getLevel();
			int heroExp = hero.getExp();
			int relifeNum = hero.getRelifeNum();
			int heroStrength = hero.getHeroStrength();
			if (!retire) {
				if (heroLv == 1 && heroExp == 0 && relifeNum == 0
						&& heroStrength == 0) {
					throw new BaseException("武将不需要重生");
				}
			}
			heros.put(heroId, hero);
		}

		if (heros.isEmpty()) {
			throw new BaseException("无武将数据");
		}
		// 锁住背包，判断格子数量
		treasuryService.lockTreasury(userId);

		for (Hero hero : heros.values()) {
			long heroId = hero.getHeroId();
			int heroLv = hero.getLevel();
			int heroExp = hero.getExp();
			int relifeNum = hero.getRelifeNum();
			int heroStrength = hero.getHeroStrength();
			SysHero sysHero = (SysHero) entityService.getEntity(hero
					.getSysHeroId());
			int baseRelifeNum = 0;
			// 消耗元宝
			if (!retire) {
				Integer cash = reBirthCash.get(sysHero.getColorType());
				if (cash == null) {
					throw new BaseException("未配置武将重生消耗元宝数量");
				}

				if (cash > 0) {
					userService.doConsumeCash(userId, cash,
							LogCashAct.HERO_CHONGSHENG_ACTION);
				}
			}

			int oldLv = hero.getLevel();
			int oldExp = hero.getExp();
			int oldGrowing = hero.getGrowing();
			int oldRelifeNum = hero.getRelifeNum();
			// int freeCount = treasuryService.getBagFreeCount(userId);
			// if (needCount > freeCount) {
			// throw new BaseException("背包已满，不能进行武将重生操作");
			// }
			// 返还经验,资源，道具
			// 升级经验：返还到经验池
			int totalExp = heroExp;
			if (heroExp > 0 || heroLv > 1) {
				totalExp += expSumMap.get(heroLv);
				if (totalExp <= 0) {
					throw new BaseException("经验超上限");
				}
				int tmpId = 0;
				int num = 0;
				for (HeroExpCard hp : heroExpCard) {
					num = totalExp / hp.exp;
					if (num > 0) {
						tmpId = hp.entId;
						totalExp -= hp.exp * num;
						Entity en = entityService.getEntity(tmpId);
						if (!(en instanceof Item)) {
							throw new BaseException("退还经验道具错误");
						}
						if (!((Item) en).getItemType().isExpItem()) {
							throw new BaseException("不是经验道具" + tmpId);
						}
						treasuryService.doAddItemToTreasury(userId, tmpId, num);
					}
				}
			}
			Map<Integer, Integer> entIdNumMaps = new HashMap<Integer, Integer>();
			int cardNum = 0;

			List<RelifeLimit> relifeLimits = sysHero.getRelifeLimits();
			if (relifeLimits != null) {
				RelifeLimit relife = relifeLimits.get(relifeNum);
				if (relife != null) {
					List<HeroStrength> hStrength = relife
							.getHeroStrengthLimits();
					if (hStrength != null) {
						// 获取当前进阶等级对应的强化等级
						int hs = 0;
						HeroStrength hst = relife.getHeroStrength(heroStrength);
						if (hst != null) {
							hs = hst.getLevel();
						}
						RelifeHeroBackItem bitem = sysHeroEntityService
								.getRelifeHeroBackItem(hero.getSysHeroId(),
										oldRelifeNum, hs);
						if (bitem != null) {
							cardNum = bitem.getCardNum();
							entIdNumMaps.putAll(bitem.getItems());
						}
					}
				}

			}
			hero.setLevel(1);
			hero.setExp(0);
			hero.setRelifeNum(baseRelifeNum);
			hero.restSkillId();
			// 四维+成长恢复成初始值
			hero.setGrowing(sysHero.getGrowing());
			recalcHeroBaseProp(hero, hero.getGrowing());

			// 强化返还
			if (heroStrength > 0) {
				hero.setHeroStrength(0);
			}
			if (relifeNum > 0) {
				hero.setRelifeNum(0);
			}
			int cardId = sysHero.getHeroCardId();
			LogItemAct log1 = LogItemAct.LOGITEMACT_5;
			LogItemAct log2 = LogItemAct.LOGITEMACT_6;
			if (retire) {
				log1 = LogItemAct.LOGITEMACT_7;
				log2 = LogItemAct.LOGITEMACT_8;
				doAddSumHeroCardNum(userId, hero, 1, LogItemAct.LOGITEMACT_15);
				// 退役获得军备材料
				int num = commonService.getSysParaIntValue(
						AppConstants.HERO_CARD_DISCARD_ENTID_COLOR
								+ hero.getSysHero().getColorType().getIndex(),
						1);

				int itemId = commonService.getSysParaIntValue(
						AppConstants.HERO_CARD_DISCARD_ENTID, 0);
				treasuryService.addItemToTreasury(userId, itemId, num, 1, -1,
						false, true, LogItemAct.LOGITEMACT_53);
			}

			if (cardNum > 0) {
				this.doAddHeroCardNum(userId, hero,
						(Item) entityService.getEntity(cardId), cardNum, log1);
			}

			for (Entry<Integer, Integer> en : entIdNumMaps.entrySet()) {
				treasuryService.addItemToTreasury(userId, en.getKey(),
						en.getValue(), 1, -1, false, true, log2);
			}

			if (retire) {
				// 因为绑定了武将卡。。所以不能删除数据了
				Hero newHero = new Hero(userId, hero.getSysHeroId(),
						hero.getHeroCardEntId(), hero.getHeroCardNum(),
						hero.getHeroSoulEntId(), hero.getHeroSoulNum(),
						hero.getSumHeroCardNum());
				hero.setStatus(HeroTroopIng.HERO_NONE.getIndex());
				newHero.setHeroId(heroId);
				hero = newHero;
				// heroDao.deleteHero(hero);
				updateHero(hero, false);
			} else {
				updateHero(hero, true);
			}
			// 通知武将重生N次,累计值
			missionService.notifyMissionModule(user, Mission.QCT_TYPE_REBRON,
					0, 1);

			// tlog
			String via = null;
			UserSession us = OnlineUserSessionManager
					.getUserSessionByUserId(userId);
			if (us != null) {
				via = us.getVia();
			} else {
				Account account = accountService.getAccount(user.getAccId());
				via = account.getVia();
			}
			if (retire) {// 退役
				logService.log(LogBeanFactory.buildDiscardHeroLog(user, 0,
						hero.getName(), heroId, hero.getSysHeroId()));
			} else {
				logService.log(LogBeanFactory.buildHeroRebirthLog(user, hero,
						oldLv, oldExp, oldGrowing, oldRelifeNum, totalExp,
						cardNum, via, retire));
			}

		}

	}

	private void handBackHeroStrength(Map<Integer, Integer> entIdNumMaps,
			List<HeroStrength> l, int hs) {
		for (int z = 0; z < hs; z++) {
			HeroStrength h = l.get(z);
			Integer it = entIdNumMaps.get(h.getItemId1());
			if (it == null) {
				entIdNumMaps.put(h.getItemId1(), h.getCount1());
			} else {
				entIdNumMaps.put(h.getItemId1(), it + h.getCount1());
			}

			it = entIdNumMaps.get(h.getItemId2());
			if (it == null) {
				entIdNumMaps.put(h.getItemId2(), h.getCount2());
			} else {
				entIdNumMaps.put(h.getItemId2(), it + h.getCount2());
			}
		}
	}

	@Override
	public void doHeroLevelUp(long userId, long heroId, List<ExpItem> exps) {
		treasuryService.lockTreasury(userId);
		Hero hero = lockAndGetHero(userId, heroId);
		if (!hero.isInTroop()) {
			throw new BaseException("不在军团里面,不能吃经验道具");
		}
		if (!hero.idle()) {
			throw new BaseException("武将必须是空闲状态");
		}
		if (hero.isCommander()) {
			throw new BaseException("君主坦克不能升级");
		}
		Map<Integer, Integer> srcCounts = new HashMap<Integer, Integer>();
		for (ExpItem ep : exps) {// 排重
			Integer it = srcCounts.get(ep.getEntId());
			if (it == null) {
				it = 0;
			}
			it += ep.getNum();
			srcCounts.put(ep.getEntId(), it);
		}

		Map<Integer, Integer> counts = new HashMap<Integer, Integer>();
		for (Entry<Integer, Integer> et : srcCounts.entrySet()) {
			if (et.getValue() < 1) {
				continue;
			}
			Entity en = entityService.getEntity(et.getKey());
			if (en == null || !(en instanceof Item)) {
				throw new BaseException("经验道具物品不存在");
			}

			if (!((Item) en).getItemType().isExpItem()) {
				throw new BaseException("使用的不是经验道具");
			}

			List<Treasury> e = treasuryService.getTreasurysByEntId(userId,
					et.getKey());
			int count = 0;
			if (e != null && !e.isEmpty()) {
				for (Treasury t : e) {
					count += t.getItemCount();
				}
			}
			if (et.getValue() > count) {
				throw new BaseException("使用的经验道具不足");
			}
			counts.put(et.getKey(), et.getValue());
		}

		for (Entry<Integer, Integer> en : counts.entrySet()) {
			Entity ent = entityService.getEntity(en.getKey());
			Map<String, Object> params = new HashMap<String, Object>();
			User user = userService.lockGetUser(userId);
			params.put("user", user);
			params.put("num", en.getValue());
			params.put("entId", en.getKey());
			params.put("heroId", heroId);
			entityService.doAction(ent, Entity.ACTION_USE, params);
		}
	}

	@Override
	public Map<String, Object> doHeroSoulComposite(long userId, long heroId) {
		lockHero(userId);
		Hero hero = getHeroCardByHeroId(userId, heroId);
		if (hero == null) {
			throw new BaseException("武将不存在");
		}
		int num = hero.getHeroSoulNum();
		if (num <= 0) {
			throw new BaseException("坦克碎片不存在");
		}
		SysHero sysHero = hero.getSysHero();

		if (sysHero.getSoulNum() > num) {
			throw new BaseException("坦克碎片不够");
		}
		doDelHeroSoulNum(userId, hero, sysHero.getSoulNum(),
				LogItemAct.LOGITEMACT_13);
		updateHero(hero, false);
		Hero nHero = doCreateAHero(userId, sysHero.getEntId(),
				LogHeroAct.Hecheng_Hero_ADD);
		int type = 1;
		if (nHero == null) {
			type = 2;
		}
		Map<String, Object> rt = new HashMap<String, Object>();
		rt.put("type", type);
		rt.put("entId", sysHero.getEntId());
		return rt;
	}

	public void doAddHeroCardNum(long userId, int entId, int num,
			LogItemAct reason) {
		Item item = (Item) entityService.getEntity(entId);
		this.doAddHeroCardNum(userId, item, num, reason);
	}

	@Override
	public void doAddHeroCardNum(long userId, Item item, int num,
			LogItemAct reason) {
		this.lockHero(userId);

		int sysHeroId = item.getSysHeroEntId();
		if (sysHeroId <= 0) {
			throw new BaseException("该道具不能存放在武将身上entId=" + item.getEntId());
		}
		Hero hero = heroDao.getHeroBySysHeroId(userId, sysHeroId);
		this.doAddHeroCardNum(userId, hero, item, num, reason);
	}

	@Override
	public void doAddHeroCardNum(long userId, Hero hero, Item item, int num,
			LogItemAct reason) {
		this.lockHero(userId);

		int sysHeroId = item.getSysHeroEntId();
		if (sysHeroId <= 0) {
			throw new BaseException("该道具不能存放在武将身上entId=" + item.getEntId());
		}

		if (hero != null && userId != hero.getUserId()) {
			throw new BaseException("只能操作自己的武将");
		}

		if (!item.getItemType().isHeroCard()) {
			throw new BaseException("只能添加图纸道具");
		}

		if (hero == null) {
			hero = new Hero(userId, sysHeroId, item.getEntId(), num, 0, 0, 0);
			heroDao.insertHero(hero);
		} else {
			hero.setHeroCardEntId(item.getEntId());
			hero.setHeroCardNum(Math.min(hero.getHeroCardNum() + num,
					Integer.MAX_VALUE));
			heroDao.updateHero(hero);
		}
		// log
		treasuryService.setItemTLog(LogTypeConst.TYPE_ADDITEM, userId, item,
				num, hero.getHeroCardNum(), reason);
		// 推送消息
		// HeroCardMsg info = new HeroCardMsg(item.getEntId(), num);
		// messageService.sendEventMessage(userId, EventMessage.TYPE_HERO_CARD,
		// info);
		messageService.sendEventMessage(userId, EventMessage.TYPE_HERO_CARD,
				new SendHeroCardAndDebrisMsg(hero.getHeroCardAndDebris()));

	}

	@Override
	public void doDelHeroCardNum(long userId, Hero hero, int num,
			LogItemAct reason) {
		this.lockHero(userId);

		if (hero == null || userId != hero.getUserId()) {
			throw new BaseException("只能操作自己的武将");
		}

		if (hero.getHeroCardEntId() <= 0) {
			throw new BaseException("道具不足");
		}

		Item item = (Item) entityService.getEntity(hero.getHeroCardEntId());
		if (item == null) {
			throw new BaseException("武将卡物品不存在" + hero.getHeroCardEntId());
		}
		if (num <= 0 || hero.getHeroCardNum() < num) {
			throw new BaseException(item.getItemName() + "数量不足");
		}

		hero.setHeroCardNum(Math.max(hero.getHeroCardNum() - num, 0));
		heroDao.updateHero(hero);

		// log
		treasuryService.setItemTLog(LogTypeConst.TYPE_DELITEM, userId, item,
				num, hero.getHeroCardNum(), reason);

		// 推送消息
		// HeroCardMsg info = new HeroCardMsg(item.getEntId(), num);
		// messageService.sendEventMessage(userId, EventMessage.TYPE_HERO_CARD,
		// info);
		messageService.sendEventMessage(userId, EventMessage.TYPE_HERO_CARD,
				new SendHeroCardAndDebrisMsg(hero.getHeroCardAndDebris()));
	}

	@Override
	public void doDelHeroCardNum(long userId, Item item, int num,
			LogItemAct reason) {
		this.lockHero(userId);

		int sysHeroId = item.getSysHeroEntId();
		if (sysHeroId <= 0) {
			throw new BaseException("该道具不能存放在武将身上entId=" + item.getEntId());
		}

		if (!item.getItemType().isHeroCard()) {
			throw new BaseException("只能添加图纸道具");
		}

		Hero hero = heroDao.getHeroBySysHeroId(userId, sysHeroId);
		this.doDelHeroCardNum(userId, hero, num, reason);
	}

	@Override
	public void doAddHeroSoulNum(long userId, int entId, int num,
			LogItemAct reason) {
		Item item = (Item) entityService.getEntity(entId);
		this.doAddHeroSoulNum(userId, item, num, reason);
	}

	@Override
	public void doAddHeroSoulNum(long userId, Item item, int num,
			LogItemAct reason) {
		this.lockHero(userId);

		int sysHeroId = item.getSysHeroEntId();
		if (sysHeroId <= 0) {
			throw new BaseException("该道具不能存放在武将身上entId=" + item.getEntId());
		}

		Hero hero = heroDao.getHeroBySysHeroId(userId, sysHeroId);
		this.doAddHeroSoulNum(userId, hero, item, num, reason);
	}

	@Override
	public void doAddHeroSoulNum(long userId, Hero hero, Item item, int num,
			LogItemAct reason) {
		this.lockHero(userId);

		int sysHeroId = item.getSysHeroEntId();
		if (sysHeroId <= 0) {
			throw new BaseException("该道具不能存放在武将身上entId=" + item.getEntId());
		}

		if (hero != null && userId != hero.getUserId()) {
			throw new BaseException("只能操作自己的武将");
		}

		if (!item.getItemType().isHeroSoul()) {
			throw new BaseException("只能添加武将碎片");
		}

		if (hero == null) {
			hero = new Hero(userId, sysHeroId, 0, 0, item.getEntId(), num, 0);
			heroDao.insertHero(hero);
		} else {
			hero.setHeroSoulEntId(item.getEntId());
			hero.setHeroSoulNum(Math.min(hero.getHeroSoulNum() + num,
					Integer.MAX_VALUE));
			heroDao.updateHero(hero);
		}

		// log
		treasuryService.setItemTLog(LogTypeConst.TYPE_ADDITEM, userId, item,
				num, hero.getHeroSoulNum(), reason);

		// 推送消息
		// HeroCardMsg info = new HeroCardMsg(item.getEntId(), num);
		// messageService.sendEventMessage(userId, EventMessage.TYPE_HERO_CARD,
		// info);
		messageService.sendEventMessage(userId, EventMessage.TYPE_HERO_SOUL,
				new SendHeroCardAndDebrisMsg(hero.getHeroCardAndDebris()));
	}

	@Override
	public void doDelHeroSoulNum(long userId, Hero hero, int num,
			LogItemAct reason) {
		this.lockHero(userId);

		if (hero == null || userId != hero.getUserId()) {
			throw new BaseException("只能操作自己的武将");
		}

		if (hero.getHeroSoulEntId() <= 0) {
			throw new BaseException("道具不足");
		}

		Item item = (Item) entityService.getEntity(hero.getHeroSoulEntId());
		if (num <= 0 || hero.getHeroSoulNum() < num) {
			throw new BaseException(item.getItemName() + "数量不足");
		}

		hero.setHeroSoulNum(Math.max(hero.getHeroSoulNum() - num, 0));
		heroDao.updateHero(hero);

		// log
		treasuryService.setItemTLog(LogTypeConst.TYPE_DELITEM, userId, item,
				num, hero.getHeroSoulNum(), reason);

		// 推送消息
		// HeroCardMsg info = new HeroCardMsg(item.getEntId(), num);
		// messageService.sendEventMessage(userId, EventMessage.TYPE_HERO_CARD,
		// info);

		messageService.sendEventMessage(userId, EventMessage.TYPE_HERO_SOUL,
				new SendHeroCardAndDebrisMsg(hero.getHeroCardAndDebris()));
	}

	@Override
	public void doHeroCardDiscard(long userId, List<Long> heroIds) {
		this.lockHero(userId);
		int jianghun = 0;
		for (long heroId : heroIds) {
			Hero hero = getHeroCardByHeroId(userId, heroId);
			if (hero == null) {
				throw new BaseException("指定的武将卡不存在");
			}
			if (hero.getHeroCardNum() < 1) {
				throw new BaseException("武将卡数量不足");
			}
			// if (hero.isHero()) {
			// throw new BaseException("武将不能分解");
			// }
			int srcNum = hero.getHeroCardNum();
			int num = commonService.getSysParaIntValue(
					AppConstants.HERO_CARD_DISCARD_ENTID_COLOR
							+ hero.getSysHero().getColorType().getIndex(), 1);
			jianghun += srcNum * num;

			doDelHeroCardNum(userId, hero, hero.getHeroCardNum(),
					LogItemAct.LOGITEMACT_14);
			// 累计分解的坦克图纸数量
			doAddSumHeroCardNum(userId, hero, srcNum, LogItemAct.LOGITEMACT_16);

			updateHero(hero, false);
		}
		int itemId = commonService.getSysParaIntValue(
				AppConstants.HERO_CARD_DISCARD_ENTID, 0);
		if (jianghun > 0) {
			treasuryService.doAddItemToTreasury(userId, itemId, jianghun);
		}

	}

	@Override
	public void doDelHeroSoulNum(long userId, Item item, int num,
			LogItemAct reason) {
		this.lockHero(userId);

		int sysHeroId = item.getSysHeroEntId();
		if (sysHeroId <= 0) {
			throw new BaseException("该道具不能存放在武将身上entId=" + item.getEntId());
		}

		if (!item.getItemType().isHeroSoul()) {
			throw new BaseException("只能添加图纸道具");
		}

		Hero hero = heroDao.getHeroBySysHeroId(userId, sysHeroId);
		this.doDelHeroSoulNum(userId, hero, num, reason);
	}

	@Override
	public void doDelSumHeroCardNum(long userId, Hero hero, int num,
			LogItemAct reason) {
		this.lockHero(userId);
		if (hero == null) {
			throw new BaseException("武将为null");
		}
		if (hero.getUserId() != userId) {
			throw new BaseException("武将不是自己的");
		}
		if (num <= 0 || num > hero.getSumHeroCardNum()) {
			throw new BaseException("减少的数量错误");
		}

		hero.setSumHeroCardNum(hero.getSumHeroCardNum() - num);
		heroDao.updateHero(hero);
	}

	@Override
	public void doAddSumHeroCardNum(long userId, Hero hero, int num,
			LogItemAct reason) {
		this.lockHero(userId);
		if (hero == null) {
			throw new BaseException("武将为null");
		}
		if (hero.getUserId() != userId) {
			throw new BaseException("武将不是自己的");
		}
		if (num <= 0) {
			throw new BaseException("增加的数量错误");
		}

		int sum = hero.getSumHeroCardNum() + num;
		if (sum <= 0) {
			throw new BaseException("增加的数量数据越界");
		}

		hero.setSumHeroCardNum(sum);
		heroDao.updateHero(hero);

	}

	// @Override
	// public void doGmUppdateHeroLevel(long userId, long heroId, int level) {
	// Hero h = getHeroByHeroId(userId, heroId);
	// if (h == null) {
	// throw new BaseException("武将不存在");
	// }
	// if (level <= 0) {
	// throw new BaseException("修改等级错误");
	// }
	// h.setLevel(level);
	// heroDao.updateHero(h);
	// }

	@Override
	public int getHeroArmyNum(long userId) {
		List<Hero> heroList = this.getUserHeroList(userId);
		int num = 0;
		if (heroList != null) {
			for (Hero hero : heroList) {
				num = num + hero.getCurHp();
			}
		}
		return num;
	}

	@Override
	public List<HeroCardAndDebris> doHeroCardAndDebris(long userId) {
		List<HeroCardAndDebris> res = new ArrayList<HeroCardAndDebris>();
		List<Hero> hero = heroDao.getHeroListByUserId(userId);
		if (hero != null && !hero.isEmpty()) {
			for (Hero h : hero) {
				if (h.getHeroCardNum() <= 0 && h.getHeroSoulNum() <= 0) {
					continue;
				}
				res.add(h.getHeroCardAndDebris());
			}
		}
		return res;
	}

	public void setUserService(IUserService userService) {
		this.userService = userService;
	}

	public void setLogService(ILogService logService) {
		this.logService = logService;
	}

	public void setEntityService(IEntityService entityService) {
		this.entityService = entityService;
	}

	public void setAccountService(IAccountService accountService) {
		this.accountService = accountService;
	}

	public void setHeroDao(IHeroDao heroDao) {
		this.heroDao = heroDao;
	}

	public void setTreasuryService(ITreasuryService treasuryService) {
		this.treasuryService = treasuryService;
	}

	public void setMessageService(IChatClientService messageService) {
		this.messageService = messageService;
	}

	public void setMissionService(IMissionService missionService) {
		this.missionService = missionService;
	}

	public void setCommonService(ICommonService commonService) {
		this.commonService = commonService;
	}

	@Override
	public CastleArmy doChangeHeroArmyNum(long userId, long casId,
			List<HeroArmyInfo> armys, int status) {
		List<Hero> heros = null;
		this.lockHero(userId);// 加锁
		CastleArmy casArmy = castleArmyService.lockGetCastleArmy(casId);// 锁兵

		int hasCasArmyNum = casArmy.getNum();
		int oldHasCasArmyNum = hasCasArmyNum;
		heros = new ArrayList<Hero>(armys.size());

		for (HeroArmyInfo tmp : armys) {
			long heroId = tmp.getHeroId();
			Hero hero = heroDao.getHeroByHeroId(userId, heroId);
			if (hero == null || hero.getUserId() != userId) {
				throw new BaseException("武将数据错误");
			}
			if (!hero.isInTroop()) {
				throw new BaseException("武将不在军团中");
			}
			if (!hero.isHero()) {
				throw new BaseException("不是武将");
			}
			// 状态判断
			if (!hero.idle()) {
				throw new BaseException("武将不是空闲状态");
			}

			int armyNum = 0;
			int heroLead = hero._getInitHp();// 携带零件的上限
			if (tmp.hasArmyNum()) {
				armyNum = tmp.getArmyNum();// 获取分配的零件数量
				if (armyNum > heroLead) {// 不能超过统兵上限
					armyNum = heroLead;
				} else if (armyNum < 0) {
					armyNum = 0;
				}
			} else {
				armyNum = heroLead;
			}
			int heroArmyNum = hero.getCurHp();
			if (heroArmyNum == armyNum) {
				continue;
			}
			hasCasArmyNum = hasCasArmyNum + heroArmyNum;
			hero.setCurHp(armyNum);// 临时保存，作为下面循环的判断
			heros.add(hero);
		}
		// ///////////////补兵
		for (Hero hero : heros) {
			int armyNum = hero.getCurHp();
			if (hasCasArmyNum > armyNum) {// 城池兵力足够
				hasCasArmyNum = hasCasArmyNum - armyNum;
			} else {
				armyNum = hasCasArmyNum;
				hasCasArmyNum = 0;
			}
			hero.setCurHp(armyNum);
			heroDao.updateHero(hero);// 修改武将兵力
			// event 刷新武将
			this.sendFlushHeroEvent(hero);
		}
		// 整体更新城池兵力
		if (oldHasCasArmyNum != hasCasArmyNum) {
			casArmy.setNum(hasCasArmyNum);
		}
		casArmy.setStatus(status);
		castleArmyService.doUpdateCasArmy(casArmy);
		return casArmy;
	}

	@Override
	public CastleArmy doAutoChangeHeroArmyNum(long userId, long casId,
			int status) {
		troopService.lockTroop(userId);
		lockHero(userId);

		List<Troop> troops = troopService.getTroopsByUserId(userId);
		if (troops == null || troops.isEmpty()) {
			throw new BaseException("您没有军团");
		}
		CastleArmy casArmy = castleArmyService.lockGetCastleArmy(casId);
		int hasCasArmyNum = casArmy.getNum();
		int oldHasCasArmyNum = hasCasArmyNum;
		if (hasCasArmyNum <= 0) {
			throw new BaseException("您没有足够的零件");
		}

		for (Troop t : troops) {
			if (!t.canArmyOut()) {
				throw new BaseException("您的军团不是空闲状态");
			}
			Hero hs[] = t.getHeros();
			for (Hero h : hs) {
				if (h == null) {
					continue;
				}
				if (hasCasArmyNum <= 0) {// 没有零件可以补充了
					break;
				}
				// 判断武将是否满状态
				int heroLead = h._getInitHp();// 携带零件的上限
				if (h.getCurHp() >= heroLead) {
					continue;// 满了
				}
				// 需要补充零件
				int tp = heroLead - h.getCurHp();
				if (hasCasArmyNum < tp) {// 零件不够补满
					tp = hasCasArmyNum;
				}
				// 扣除空闲的零件
				hasCasArmyNum -= tp;

				h.setCurHp(tp);
				heroDao.updateHero(h);

				sendFlushHeroEvent(h);
			}
		}

		if (oldHasCasArmyNum != hasCasArmyNum) {
			casArmy.setNum(hasCasArmyNum);
		}
		casArmy.setStatus(status);
		castleArmyService.doUpdateCasArmy(casArmy);
		return casArmy;
	}

	@Override
	public void doHeroDownTroop(Hero hero) {
		hero.heroDownTroop();// 坦克的套装

		User user = userService.getUserById(hero.getUserId());

		// 卸下坦克身上带的零件
		int cp = hero.getCurHp();
		castleArmyService.doUpdateCasArmyNum(user.getMainCastleId(), cp);
		hero.setCurHp(0);
		updateHero(hero, false);
	}

	@Override
	public List<EffectValueMsg> getHeroEffectValue(long userId, long heroId) {
		List<EffectValueMsg> res = new ArrayList<EffectValueMsg>();
		Hero h = getHeroByHeroId(userId, heroId);
		if (h == null) {
			throw new BaseException("武将不存在");
		}
		res.add(new EffectValueMsg(EffectTypeDefine.H_MAGICATTACK, h
				._getPEffectValue(h.getMagicAttack(),
						EffectTypeDefine.H_MAGICATTACK, false), false));
		res.add(new EffectValueMsg(EffectTypeDefine.H_PHYSICALATTACK, h
				._getPEffectValue(h.getPhysicalAttack(),
						EffectTypeDefine.H_PHYSICALATTACK, false), false));
		res.add(new EffectValueMsg(EffectTypeDefine.H_MAGICDEFEND, h
				._getPEffectValue(h.getMagicDefend(),
						EffectTypeDefine.H_MAGICDEFEND, false), false));
		res.add(new EffectValueMsg(EffectTypeDefine.H_PHYSICALDEFEND, h
				._getPEffectValue(h.getPhysicalDefend(),
						EffectTypeDefine.H_PHYSICALDEFEND, false), false));
		res.add(new EffectValueMsg(EffectTypeDefine.H_INIT_HP, h
				._getPEffectValue(h.getIntHp(), EffectTypeDefine.H_INIT_HP,
						false), false));
		// res.add(new EffectValueMsg(EffectTypeDefine.H_Hit,
		// h._getPEffectValue(h
		// .getSysHero().getInitHit(), EffectTypeDefine.H_Hit, false),
		// false));
		// res.add(new EffectValueMsg(EffectTypeDefine.H_DODGE, h
		// ._getPEffectValue(h.getSysHero().getInitDodge(),
		// EffectTypeDefine.H_DODGE, false), false));
		// res.add(new EffectValueMsg(EffectTypeDefine.H_CRITDEC,
		// h._getCritDec()));
		// res.add(new EffectValueMsg(EffectTypeDefine.H_CRITADD,
		// h._getCritAdd()));
		// res.add(new EffectValueMsg(EffectTypeDefine.H_CRITDAMAGE, h
		// ._getCritDamageAdd()));
		// res.add(new EffectValueMsg(EffectTypeDefine.H_CRITDAMAGE_DEC, h
		// ._getCritDamageDec()));
		// res.add(new EffectValueMsg(EffectTypeDefine.H_DAMAGE_PER, h
		// ._getDamageAdd(), true));
		// res.add(new EffectValueMsg(EffectTypeDefine.H_SHIELD_PER, h
		// ._getDamageDec(), true));
		// res.add(new EffectValueMsg(EffectTypeDefine.H_INIT_HP,
		// h._getInitHp()));
		// res.add(new EffectValueMsg(EffectTypeDefine.H_DAMAGE,
		// h._getDamage()));
		// res.add(new EffectValueMsg(EffectTypeDefine.H_SHIELD,
		// h._getShield()));
		// res.add(new EffectValueMsg(EffectTypeDefine.H_MORALE,
		// h._getMorale()));
		return res;
	}

	@Override
	public boolean isMainHero(long userId, Hero hero) {
		return isMainHero(userId, hero.getHeroId());
	}

	@Override
	public void doUpdateMainHero(User user) {
		long main = troopService.getMainHero(user.getUserId());
		Hero hero = lockAndGetHero(user.getUserId(), main);
		if (hero == null) {
			throw new BaseException("君主坦克错误" + main);
		}
		hero.setExp(user.getHonor());
		hero.setLevel(user.getUsrLv());

		recalcHeroBaseProp(hero, hero.getGrowing());
		updateHero(hero, true);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void doEvent(Event event) {
		if (event.getEventType() == EventTypeConstants.EVT_USER_INFO_CHANGE
				|| event.getEventType() == EventTypeConstants.EVT_USER_LEVEL_UP) {
			Map<String, Object> params = (Map<String, Object>) event
					.getParams();
			User user = (User) params.get("user");
			doUpdateMainHero(user);
		}
	}

	/**
	 * 这里不判断状态，这个方法不会前台调用
	 */
	@Override
	public boolean doFillUpHeroArmy(User user, Hero[] heros) {
		boolean full = true;
		long casId = user.getMainCastleId();
		CastleArmy casArmy = castleArmyService.lockGetCastleArmy(casId);// 锁兵
		int hascasArmy = casArmy.getNum();
		int oldHascasArmy = hascasArmy;

		for (Hero hero : heros) {
			if (hascasArmy == 0) {
				break;
			}
			// && hero.getActionStatus() == Hero.ACTION_STATUS_IDLE
			// ，这里不判断状态，这个方法不会前台调用
			if (hero != null) {
				int max = hero._getInitHp();// 携带零件的上限
				int curr = hero.getCurHp();
				if (curr < max) {
					int fillup = max - curr;
					if (fillup > hascasArmy) {
						hero.setCurHp(curr + hascasArmy);
						hascasArmy = 0;
						full = false;
						// break;
					} else {
						hero.setCurHp(max);
						hascasArmy = hascasArmy - fillup;
					}
					this.updateHero(hero, true);
				}

			}
		}
		if (oldHascasArmy != hascasArmy) {
			casArmy.setNum(hascasArmy);
			castleArmyService.doUpdateCasArmy(casArmy);
		}
		return full;

	}

	@Override
	public boolean isMainHero(long userId, long heroId) {
		long id = troopService.getMainHero(userId);
		return heroId == id;
	}

	public void setCastleArmyService(ICastleArmyService castleArmyService) {
		this.castleArmyService = castleArmyService;
	}

	public void setTroopService(ITroopService troopService) {
		this.troopService = troopService;
	}

	public void setSysHeroEntityService(ISysHeroService sysHeroEntityService) {
		this.sysHeroEntityService = sysHeroEntityService;
	}

	@Override
	public CommanderColorProperty getCommanderColorProperty(long userId) {
		User user = userService.getUserById(userId);
		return color4Map.get(user.getColor());
	}

	@Override
	public CommanderColorProperty getCommanderColorProperty(int color) {
		return color4Map.get(color);
	}

	public void setUserAchieveService(IUserAchieveService userAchieveService) {
		this.userAchieveService = userAchieveService;
	}
}
