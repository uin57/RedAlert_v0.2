package com.youxigu.dynasty2.user.service.impl;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.ibatis.sqlmap.engine.cache.memcached.MemcachedManager;
import com.manu.util.UtilMisc;
import com.youxigu.dynasty2.chat.ChatChannelManager;
import com.youxigu.dynasty2.chat.EventMessage;
import com.youxigu.dynasty2.chat.client.IChatClientService;
import com.youxigu.dynasty2.common.AppConstants;
import com.youxigu.dynasty2.common.service.ICommonService;
import com.youxigu.dynasty2.common.service.ISensitiveWordService;
import com.youxigu.dynasty2.core.event.Event;
import com.youxigu.dynasty2.core.event.EventDispatcher;
import com.youxigu.dynasty2.core.event.EventTypeConstants;
import com.youxigu.dynasty2.core.flex.amf.IAMF3Action;
import com.youxigu.dynasty2.core.wolf.IWolfClientService;
import com.youxigu.dynasty2.develop.domain.Castle;
import com.youxigu.dynasty2.develop.service.ICastleService;
import com.youxigu.dynasty2.entity.domain.SysHero;
import com.youxigu.dynasty2.friend.service.IFriendService;
import com.youxigu.dynasty2.hero.domain.Hero;
import com.youxigu.dynasty2.hero.domain.Troop;
import com.youxigu.dynasty2.hero.service.IHeroService;
import com.youxigu.dynasty2.hero.service.ITroopService;
import com.youxigu.dynasty2.log.AbsLogLineBuffer;
import com.youxigu.dynasty2.log.ILogService;
import com.youxigu.dynasty2.log.LogBeanFactory;
import com.youxigu.dynasty2.log.LogTypeConst;
import com.youxigu.dynasty2.log.MylogHeadUtil;
import com.youxigu.dynasty2.log.TLogTable;
import com.youxigu.dynasty2.log.TlogHeadUtil;
import com.youxigu.dynasty2.log.imp.JunGongAct;
import com.youxigu.dynasty2.log.imp.LogActiveAct;
import com.youxigu.dynasty2.log.imp.LogCashAct;
import com.youxigu.dynasty2.log.imp.LogHeroAct;
import com.youxigu.dynasty2.log.imp.LogHpAct;
import com.youxigu.dynasty2.log.imp.LogUserExpAct;
import com.youxigu.dynasty2.map.domain.Country;
import com.youxigu.dynasty2.map.domain.MapCell;
import com.youxigu.dynasty2.map.service.IMapService;
import com.youxigu.dynasty2.mission.domain.Mission;
import com.youxigu.dynasty2.mission.service.IMissionService;
import com.youxigu.dynasty2.openapi.Constant;
import com.youxigu.dynasty2.treasury.service.ITreasuryService;
import com.youxigu.dynasty2.user.dao.IUserDao;
import com.youxigu.dynasty2.user.domain.Account;
import com.youxigu.dynasty2.user.domain.AchieveType;
import com.youxigu.dynasty2.user.domain.LvParaLimit;
import com.youxigu.dynasty2.user.domain.User;
import com.youxigu.dynasty2.user.domain.UserConsumeLog;
import com.youxigu.dynasty2.user.domain.UserCount;
import com.youxigu.dynasty2.user.domain.UserRechargeLog;
import com.youxigu.dynasty2.user.proto.ActPointMessage;
import com.youxigu.dynasty2.user.proto.HpPointMessage;
import com.youxigu.dynasty2.user.service.IAccountService;
import com.youxigu.dynasty2.user.service.IUserAchieveService;
import com.youxigu.dynasty2.user.service.IUserAttrService;
import com.youxigu.dynasty2.user.service.IUserCountService;
import com.youxigu.dynasty2.user.service.IUserService;
import com.youxigu.dynasty2.util.BaseException;
import com.youxigu.dynasty2.util.StringUtils;
import com.youxigu.dynasty2.vip.service.IVipService;
import com.youxigu.wolf.net.MobileClient;
import com.youxigu.wolf.net.OnlineUserSessionManager;
import com.youxigu.wolf.net.UserSession;

public class UserService implements IUserService, ApplicationContextAware {
	private static Logger log = LoggerFactory.getLogger(UserService.class);
	private Pattern reg = Pattern.compile("[a-zA-Z0-9_\u4e00-\u9fa5]{1,16}");
	private Pattern passwordReg = Pattern.compile("^[A-Za-z0-9]+$");
	// \|- 这3个怎么处理呢
	private Pattern reg_1 = Pattern
			.compile("[a-zA-Z0-9,./;':\"\\[\\]\\{\\}?<>_=\\+\\(\\)`~!@#$%\\^&*，。、《》？；‘’：“”【】、·！@#￥…（）\u0020\u4e00-\u9fa5]{1,30}");
	private Map<Integer, LvParaLimit> lvParaLimitMaps = new HashMap<Integer, LvParaLimit>();

	/**
	 * 所有手Q平台预约用户名单openid
	 */
	private Map<String, Byte> qqWhiteOpenIds = new HashMap<String, Byte>();
	/**
	 * 所有微信平台预约用户名单openid
	 */
	private Map<String, Byte> wxWhiteOpenIds = new HashMap<String, Byte>();
	
	/**
	 * 是否开放钻石消费返还，预冲值使用
	 */
	private boolean consumeBack;

	/**
	 * 开放消费记录
	 */
	private boolean consumeLog;

	/** 记录那些分配新手建城点的时候是没有分配成功的，未分配成功的则可以使用手动分配功能 */
	private Map<Long, Long> cityPoints = new HashMap<Long, Long>();

	private IUserDao userDao;
	private IUserCountService userCountService;
	private IChatClientService messageService;
	private ISensitiveWordService sensitiveWordService;
	private IUserAttrService userAttrService;
	private IWolfClientService mainServerClient;
	private ICommonService commonService;
	private ILogService logService;
	private ILogService tlogService;
	private IAccountService accountService;
	private IMapService mapService;
	private IHeroService heroService;
	private ITroopService troopService;
	private IVipService vipService;
	private ICastleService castleService;
	private IFriendService friendService;
	private IMissionService missionService;
	private ITreasuryService treasuryService;
	private IUserAchieveService userAchieveService;
	/**
	 * 开放平台用于赠送钻石的discountId
	 */
	public String discountId;
	/**
	 * 开放平台用于赠送钻石的giftId
	 */
	public String giftId;

	public void setTreasuryService(ITreasuryService treasuryService) {
		this.treasuryService = treasuryService;
	}

	public void setDiscountId(String discountId) {
		this.discountId = discountId;
	}

	public void setGiftId(String giftId) {
		this.giftId = giftId;
	}

	public void setAccountService(IAccountService accountService) {
		this.accountService = accountService;
	}

	public void setMapService(IMapService mapService) {
		this.mapService = mapService;
	}

	public void setUserCountService(IUserCountService userCountService) {
		this.userCountService = userCountService;
	}

	public void setConsumeBack(boolean consumeBack) {
		this.consumeBack = consumeBack;
	}

	public void setConsumeLog(boolean consumeLog) {
		this.consumeLog = consumeLog;
	}

	public void setHeroService(IHeroService heroService) {
		this.heroService = heroService;
	}

	public void setLogService(ILogService logService) {
		this.logService = logService;
	}

	public void setTlogService(ILogService tlogService) {
		this.tlogService = tlogService;
	}

	public void setCommonService(ICommonService commonService) {
		this.commonService = commonService;
	}

	public void setMessageService(IChatClientService messageService) {
		this.messageService = messageService;
	}

	public void setCastleService(ICastleService castleService) {
		this.castleService = castleService;
	}

	public void setMainServerClient(IWolfClientService mainServerClient) {
		this.mainServerClient = mainServerClient;
	}

	public void setSensitiveWordService(
			ISensitiveWordService sensitiveWordService) {
		this.sensitiveWordService = sensitiveWordService;
	}

	public void setUserDao(IUserDao userDao) {
		this.userDao = userDao;
	}

	public void setUserAttrService(IUserAttrService userAttrService) {
		this.userAttrService = userAttrService;
	}

	public void setVipService(IVipService vipService) {
		this.vipService = vipService;
	}

	public void setTroopService(ITroopService troopService) {
		this.troopService = troopService;
	}

	public void setMissionService(IMissionService missionService) {
		this.missionService = missionService;
	}

	public void setUserAchieveService(IUserAchieveService userAchieveService) {
		this.userAchieveService = userAchieveService;
	}

	public void init() {
		List<LvParaLimit> lvParams = userDao.getLvParaLimits();
		if (lvParams != null) {
			for (LvParaLimit lv : lvParams) {
				lvParaLimitMaps.put(lv.getUsrLv(), lv);
			}
		}
	}

	@Override
	public User getUserById(long userId) {
		return userDao.getUserById(userId);
	}

	@Override
	public User getUserByaccId(long accId) {
		return userDao.getUserByAccId(accId);
	}

	@Override
	public void doCreateUser(User user, boolean isRecommend) {
		Country country = mapService.getCountryById(user.getCountryId());
		if (user.getCountryId() <= 0 || country == null) {
			throw new BaseException("必须选择国家");
		}
		String name = user.getUserName();
		if (name == null) {
			throw new BaseException("玩家名不能为空");
		}
		name = name.trim();
		user.setUserName(name);
		if (name.length() == 0) {
			throw new BaseException("玩家名不能为空");
		}
		if (name.length() > 6) {
			throw new BaseException("玩家名必须为1-6个字符");
		}
		if (!StringUtils.match(name, reg)) {
			throw new BaseException("玩家名包含非法字符或空格，请重新输入");
		}
		if (sensitiveWordService.match(name)) {
			throw new BaseException("玩家名包含非法词语,请重新输入");
		}
		if (userDao.getUserByName(name) != null)
			throw new BaseException("此玩家名已使用。");

		long accId = user.getAccId();
		try {
			MemcachedManager.lock("ACC_" + accId);
		} catch (TimeoutException e) {
			throw new BaseException(e.toString());
		}

		User oldUser = userDao.getUserByAccId(accId);
		if (oldUser != null) {
			throw new BaseException("角色已经存在");
		}

		long now = System.currentTimeMillis();
		Timestamp nowDate = new Timestamp(now);
		user.setUsrLv(1);
		user.setCreateDate(nowDate);
		user.setSelfSignature("");
		user.setUpLevelTime(new Timestamp(System.currentTimeMillis()));
		// String icon = country.getMaleIcon();
		// if (user.getSex() == User.SEX_WOMAN) {
		// icon = country.getFeMaleIcon();
		// }
		// user.setIcon(icon == null ? "" : icon);
		user.setColor(AppConstants.INIT_COLOR);// 要上移，因为下面创建武将的时候回用到颜色值
		user.setTitle(AppConstants.INIT_TITLEID);

		userDao.insertUser(user);

		setTlogCreateUser(user);

		// 创建初始武将
		int sysHeroId = commonService.getSysParaIntValue(
				AppConstants.ENUMER_FIRST_SYS_HEROID, 100000001);
		SysHero sysHero = heroService.getSysHeroById(sysHeroId);
		if (sysHero == null) {
			throw new BaseException("指挥官武将不存在");
		}
		Hero hero = heroService.doCreateAHero(user.getUserId(), sysHeroId,
				true, LogHeroAct.Init_Hero_ADD);
		user.setHeroId(hero.getHeroId());// 设置指挥官id

		// 创建军团
		Troop troop = troopService.doCreateTroop(user.getUserId(),
				hero.getHeroId(), true);
		user.setTroopId(troop.getTroopId());// 设置主战军团的id

		Castle castle = castleService.createCastle(user.getUserId(), -1);

		user.setMainCastleId(castle.getCasId());
		userDao.updateUserMainCastleId(user);

		// 初始化任务
		missionService.doInitUserMission(user);
	}

	@Override
	public void doUpdateUser(User user) {
		userDao.updateUser(user);
	}

	@Override
	public User getUserByName(String userName) {
		return userDao.getUserByName(userName);
	}

	private void setTlogCreateUser(User user) {
		// Date now = new Date();
		UserSession us = OnlineUserSessionManager.getUserSessionByAccId(user
				.getAccId());
		String openId = null;
		// String pf = null;
		String ip = null;
		// String via = null;
		String areaId = null;
		// String appid = null;
		// int dType = 0;
		MobileClient mobile;
		Account account = accountService.getAccount(user.getAccId());
		if (us != null) {
			mobile = us.getMobileClient();
			// appid = Constant.getAppId(us.getpType());

			// if (us.getdType() == Constant.DEVIDE_TYPE_IOS) {
			// dType = 0;
			// } else {
			// dType = 1;
			// }
			openId = us.getOpenid();
			// pf = us.getPfEx();
			ip = us.getUserip();
			// via = us.getVia();
			areaId = us.getAreaId();

		} else {
			// via = account.getVia();
			openId = account.getAccName();
			ip = account.getLoginIp();
			// pf = Constant.getPfEx(account.getPf());
			areaId = account.getAreaId();
			mobile = new MobileClient();
		}
		// 用户可能不在线升级，这个时候UserSession没有，如果一定要取，可以先取Account
		// 这里没有取，是不想再取一次account
		// Account account = accountService.getAccount(accId);

		// *************************mylog格式**********************
		// <struct name="PlayerRegister" version="1" desc="(必填)玩家注册">
		// <entry name="GameSvrId" type="string" size="25" desc="(必填)登录的游戏服务器编号"
		// />
		// <entry name="dtEventTime" type="datetime"
		// desc="(必填)游戏事件的时间, 格式 YYYY-MM-DD HH:MM:SS" />
		// <entry name="vGameAppid" type="string" size="32" desc="(必填)游戏APPID"
		// />
		// <entry name="PlatID" type="int" defaultvalue="0"
		// desc="(必填)ios 0 /android 1"/>
		// <entry name="ZoneID" type="int" defaultvalue="0"
		// desc="(必填)针对分区分服的游戏填写分区id，用来唯一标示一个区；非分区分服游戏请填写0"/>
		// <entry name="vopenid" type="string" size="64" desc="(必填)用户OPENID号" />
		// <entry name="ClientVersion" type="string" size="64"
		// defaultvalue="NULL" desc="(可选)客户端版本"/>
		// <entry name="SystemSoftware" type="string" size="64"
		// defaultvalue="NULL" desc="(必填)移动终端操作系统版本"/>
		// <entry name="SystemHardware" type="string" size="64"
		// defaultvalue="NULL" desc="(必填)移动终端机型"/>
		// <entry name="TelecomOper" type="string" size="64" defaultvalue="NULL"
		// desc="(必填)运营商"/>
		// <entry name="Network" type="string" size="64" defaultvalue="NULL"
		// desc="(可选)3G/WIFI/2G"/>
		// <entry name="ScreenWidth" type="int" defaultvalue="0"
		// desc="(必填)显示屏宽度"/>
		// <entry name="ScreenHight" type="int" defaultvalue="0"
		// desc="(必填)显示屏高度"/>
		// <entry name="Density" type="float" defaultvalue="0" desc="(必填)像素密度"/>
		// <entry name="RegChannel" type="int" defaultvalue="0"
		// desc="(必填)注册渠道"/>
		// <entry name="CpuHardware" type="string" size="64" defaultvalue="NULL"
		// desc="(可选)cpu类型|频率|核数"/>
		// <entry name="Memory" type="int" defaultvalue="0" desc="(可选)内存信息单位M"/>
		// <entry name="GLRender" type="string" size="64" defaultvalue="NULL"
		// desc="(可选)opengl render信息"/>
		// <entry name="GLVersion" type="string" size="64" defaultvalue="NULL"
		// desc="(可选)opengl版本信息"/>
		// <entry name="DeviceId" type="string" size="64" defaultvalue="NULL"
		// desc="(可选)设备ID"/>
		// <entry name="RoleId" type="string" size="64" desc="(必填)角色ID" />
		// <entry name="Ip" type="string" size="64" desc="(必填)玩家登录IP" />
		// </struct>
		logService.log(MylogHeadUtil
				.getMylogHead(TLogTable.TLOG_PLAYERREGISTER.getName(), user)
				.append(mobile.vClientVersion).append(mobile.vSystemSoftware)
				.append(mobile.vSystemHardware).append(mobile.vTelecomOper)
				.append(mobile.vNetwork).append(mobile.iScreenWidth)
				.append(mobile.iScreenHight).append(mobile.Density)
				.append(mobile.iRegChannel).append(mobile.vCpuHardware)
				.append(mobile.iMemory).append(mobile.vGLRender)
				.append(mobile.vGLVersion).append(mobile.vDeviceId)
				.append(user.getUserId()).append(ip));

		AbsLogLineBuffer buf = TlogHeadUtil.getTlogHead(
				LogTypeConst.TLOG_TYPE_ROLE_CREATE, account, user, us);
		tlogService.log(buf.append(mobile.vClientVersion)
				.append(mobile.vSystemSoftware).append(mobile.vSystemHardware)
				.append(mobile.vTelecomOper).append(mobile.vNetwork)
				.append(mobile.iScreenWidth).append(mobile.iScreenHight)
				.append(mobile.Density).append(mobile.iRegChannel)
				.append(mobile.vCpuHardware).append(mobile.iMemory)
				.append(mobile.vGLRender).append(mobile.vGLVersion)
				.append(mobile.vDeviceId).append(user.getCountryId()));

		int moneyios = 0;
		int moneyandroid = 0;
		int diamondios = 0;
		int diamondandroid = 0;
		int foodandroid = 0;
		int foodios = 0;
		int brzoneandroid = 0;
		int brzoneios = 0;

		int temGold = commonService.getSysParaIntValue(
				AppConstants.INIT_GOLD_NUM, 0);
		int temIron = commonService.getSysParaIntValue(
				AppConstants.INIT_IRON_NUM, 0);
		int temOil = commonService.getSysParaIntValue(
				AppConstants.INIT_OIL_NUM, 0);
		int temUranium = commonService.getSysParaIntValue(
				AppConstants.INIT_URANIUM_NUM, 0);
		if (Constant.isAndroid()) {
			moneyandroid = temGold;
			foodandroid = temIron;
			brzoneandroid = temOil;
			diamondandroid = user.getCash();
		} else {
			moneyios = temGold;
			foodios = temIron;
			brzoneios = temOil;
			diamondios = user.getCash();
		}

		// gameappid,openid,zoneid,regtime,level,iFriends,moneyios,moneyandroid,diamondios,diamondandroid,foodios,foodandroid,brzoneios,brzoneandroid
		tlogService.logDB(new Object[] { LogTypeConst.SQL_INSERT_roleinfo,
				Constant.getAppId(), openId, areaId, user.getCreateDate(), 1,
				0, moneyios, moneyandroid, diamondios, diamondandroid, foodios,
				foodandroid, brzoneios, brzoneandroid });
	}

	// <struct name="PlayerExpFlow" version="1" desc="(可选)人物等级流水表">
	// <entry name="vGameSvrId" type="string" size="25" desc="(必填)登录的游戏服务器编号" />
	// <entry name="dtEventTime" type="datetime"
	// desc="(必填)游戏事件的时间, 格式 YYYY-MM-DD HH:MM:SS" />
	// <entry name="vGameAppid" type="string" size="32" desc="(必填)游戏APPID" />
	// <entry name="iPlatID" type="int" desc="(必填)ios 0/android 1"/>
	// <entry name="iZoneID" type="int"
	// desc="(必填)针对分区分服的游戏填写分区id，用来唯一标示一个区；非分区分服游戏请填写0"/>
	// <entry name="vopenid" type="string" size="64" desc="(必填)玩家" />
	// <entry name="iExpChange" type="int" desc="(必填)经验变化" />
	// <entry name="iBeforeLevel" type="int" desc="(可选)动作前等级" />
	// <entry name="iAfterLevel" type="int" desc="(必填)动作后等级" />
	// <entry name="iTime" type="int" desc="(必填)升级所用时间(秒)" />
	// <entry name="iReason" type="int" desc="(必填)经验流动一级原因" />
	// <entry name="iSubReason" type="int" desc="(必填)经验流动二级原因" />
	// </struct>
	private void setTlogByUserLv(User user, int ExpChange, int BeforeLevel,
			int AfterLevel, LogUserExpAct iAction, long time) {
		AbsLogLineBuffer buf = TlogHeadUtil.getTlogHead(
				LogTypeConst.TLOG_TYPE_ROLE_LVUP, user);
		tlogService.log(buf.append(ExpChange).append(BeforeLevel)
				.append(AfterLevel).append(0).append(iAction.value).append(0));
		UserSession us = OnlineUserSessionManager.getUserSessionByAccId(user
				.getAccId());
		int channel = us == null ? 0 : us.getMobileClient().iRegChannel;
		logService.log(MylogHeadUtil
				.getMylogHead(TLogTable.TLOG_PLAYEREXPFLOW.getName(), user)
				.append(ExpChange).append(BeforeLevel).append(AfterLevel)
				.append((int) (time / 1000)).append(iAction.value).append(0)
				.append(channel).append(user.getUserId()));

	}

	@Override
	public void updateUserMainCastleId(long userId, long casId) {
		User user = this.getUserById(userId);
		if (user != null) {
			user = lockGetUser(user);
			user.setMainCastleId(casId);
			userDao.updateUserMainCastleId(user);
		}
	}

	@Override
	public LvParaLimit getLvParaLimit(int level) {
		return lvParaLimitMaps.get(level);
	}

	@Override
	public User lockGetUser(User user) {
		try {
			user = (User) MemcachedManager.lockObject(user, user.getUserId());
		} catch (TimeoutException e) {
			throw new BaseException(e.toString());
		}
		return user;
	}

	@Override
	public User lockGetUser(long userId) {
		try {
			MemcachedManager.lockClass(User.class, userId);
			return this.getUserById(userId);
		} catch (TimeoutException e) {
			throw new BaseException(e.toString());
		}
	}

	@Override
	public void lockUser(long userId) {
		try {
			MemcachedManager.lockClass(User.class, userId);
		} catch (TimeoutException e) {
			throw new BaseException(e.toString());
		}
	}

	@Override
	public String doUpdateSelfSignature(User user, String selfSignature) {
		selfSignature = StringUtils.trim(selfSignature);
		selfSignature = sensitiveWordService
				.replace(selfSignature, null, false);

		if (selfSignature.length() > 30) {
			throw new BaseException("玩家签名必须为1-30个字符");
		}
		if (!selfSignature.equals("")
				&& !StringUtils.match(selfSignature, reg_1)) {
			throw new BaseException("玩家签名包含非法字符，请重新输入");
		}
		user = this.lockGetUser(user);
		user.setSelfSignature(selfSignature);
		userDao.updateUser(user);
		return user.getSelfSignature();
	}

	@Override
	public boolean isOnline(long userId) {
		boolean online = OnlineUserSessionManager.isOnline(userId);
		if (!online) {
			online = mainServerClient.sendTask(Boolean.class,
					"wolf_onlineUserService", "isOnline", userId);
		}
		return online;
	}

	@Override
	public Map<Long, Boolean> isOnlines(long[] userIds) {
		return mainServerClient.sendTask(Map.class, "wolf_onlineUserService",
				"isOnlines", userIds);
	}

	@Override
	public UserCount getUserCount(long userId, String type) {
		return userCountService.getUserCount(userId, type);
	}

	@Override
	public void createUserCount(UserCount userCount) {
		userCountService.createUserCount(userCount);
	}

	@Override
	public void updateUserCount(UserCount userCount) {
		userCountService.updateUserCount(userCount);
	}

	@Override
	public void setApplicationContext(ApplicationContext arg0)
			throws BeansException {
		// TODO Auto-generated method stub

	}

	@Override
	public int getCountUsers() {
		return userDao.getCountUsers();
	}

	@Override
	public User addCash(long userId, int num, LogCashAct iaction) {
		if (num < 0) {
			throw new BaseException("钻石数量不能小于0");
		}

		User user = this.lockGetUser(userId);
		if (num == 0) {
			return user;
		}

		if (user == null) {// 君主不存在
			throw new BaseException("君主不存在。");
		}

		long accId = user.getAccId();
		int oldCash = user.getCash();
		int balance = oldCash + num;
		user.setCashTotal(user.getCashTotal() + num);
		user.setCash(balance);

		// 记录日志
		setCashLog(LogTypeConst.TYPE_ADDCASH, user, 1, num, oldCash, balance,
				iaction, false);

		// TODO 后期可增加充值赠送
		int giftCash = 0;
		userDao.updateUser(user);

		// 贵族
		vipService.doUpdateUserVip(user, num);

		// 本地的充值记录
		UserSession us = OnlineUserSessionManager.getUserSessionByAccId(accId);
		UserRechargeLog ulog = new UserRechargeLog(user.getUserId(), (short) 0,
				num, balance, 0, 0, new Timestamp(System.currentTimeMillis()),
				null, us != null ? us.getPf() : "");
		userDao.insertUserRechargeLog(ulog);

		// 推送钻石值
		this.sendUserEvent(user);

		// TODO 通知活动钻石消费

		if (log.isDebugEnabled()) {
			log.debug("君主:" + userId + "行为:" + iaction + "钻石数增加"
					+ Math.abs(num + giftCash) + ",当前钻石：" + balance);
		}

		return user;
	}

	/**
	 * 代币tlog & mylog
	 * 
	 * @param logType
	 * @param user
	 * @param type
	 * @param addValue
	 * @param oldCash
	 * @param restCash
	 * @param iaction
	 * @param recordLocalLog
	 */
	private void setCashLog(String logType, User user, int type, int addValue,
			int oldCash, int restCash, LogCashAct iaction,
			boolean recordLocalLog) {
		Account account = accountService.getAccount(user.getAccId());
		UserSession us = OnlineUserSessionManager.getUserSessionByAccId(user
				.getAccId());
		String pf = null;
		String via = null;
		if (us != null) {
			pf = us.getPfEx();
			via = us.getVia();
		} else {
			via = account.getVia();
			pf = Constant.getPfEx(account.getPf());
		}

		// **************************mylog日志格式*************************
		// <struct name="MoneyFlow" version="1" desc="(必填)货币流水">
		// <entry name="GameSvrId" type="string" size="25" desc="(必填)登录的游戏服务器编号"
		// />
		// <entry name="dtEventTime" type="datetime"
		// desc="(必填)游戏事件的时间, 格式 YYYY-MM-DD HH:MM:SS" />
		// <entry name="vGameAppid" type="string" size="32" desc="(必填)游戏APPID"
		// />
		// <entry name="PlatID" type="int" defaultvalue="0"
		// desc="(必填)ios 0/android 1"/>
		// <entry name="ZoneID" type="int" defaultvalue="0"
		// desc="(必填)针对分区分服的游戏填写分区id，用来唯一标示一个区；非分区分服游戏请填写0"/>
		// <entry name="vopenid" type="string" size="64" desc="(必填)用户OPENID号" />
		// <entry name="Sequence" type="int" desc="(可选)用于关联一次动作产生多条不同类型的货币流动日志"
		// />
		// <entry name="Level" type="int" desc="(必填)玩家等级" />
		// <entry name="AfterMoney" type="int" desc="(可选)动作后的金钱数" />
		// <entry name="iMoney" type="int" desc="(必填)动作涉及的金钱数" />
		// <entry name="Reason" type="int" desc="(必填)货币流动一级原因" />
		// <entry name="SubReason" type="int"
		// desc="(必填) 功能消费 1/非功能能消费 0 功能消费指界面上直接扣钻石的不产生道具转换" />
		// <entry name="AddOrReduce" type="int" desc="(必填)增加 0/减少 1" />
		// <entry name="iMoneyType" type="int" desc="(必填)钱的类型MONEYTYPE" />
		// <entry name="LoginChannel" type="int" defaultvalue="0"
		// desc="(必填)登录渠道"/>
		// <entry name="RoleId" type="string" size="64" desc="(必填)角色ID" />
		// <entry name="Rmb" type="int" desc="(可选)充值的RMB数量" />
		// </struct>
		logService.log(MylogHeadUtil
				.getMylogHead(TLogTable.TLOG_MONEYFLOW.getName(), user)
				.append("0").append(user.getUsrLv()).append(user.getCash())
				.append(addValue).append(iaction.id).append(0)
				.append(restCash > oldCash ? 0 : 1).append(iaction.cashType)
				.append(MylogHeadUtil.getIntVia(via)).append(user.getUserId())
				.append(0));

		// *******************tlog钻石消耗日志************************
		// <struct name="MoneyFlow" version="1" desc="(必填)货币流水">
		// <entry name="vGameSvrId" type="string" size="25"
		// desc="(必填)登录的游戏服务器编号" />
		// <entry name="dtEventTime" type="datetime"
		// desc="(必填)游戏事件的时间, 格式 YYYY-MM-DD HH:MM:SS" />
		// <entry name="vGameAppid" type="string" size="32" desc="(必填)游戏APPID"
		// />
		// <entry name="iPlatID" type="int" desc="(必填)ios 0/android 1"/>
		// <entry name="iZoneID" type="int"
		// desc="(必填)针对分区分服的游戏填写分区id，用来唯一标示一个区；非分区分服游戏请填写0"/>
		// <entry name="vopenid" type="string" size="64" desc="(必填)用户OPENID号" />
		// <entry name="iSequence" type="int" desc="(可选)用于关联一次动作产生多条不同类型的货币流动日志"
		// />
		// <entry name="iLevel" type="int" desc="(必填)玩家等级" />
		// <entry name="iAfterMoney" type="int" desc="(可选)动作后的金钱数" />
		// <entry name="iMoney" type="int" desc="(必填)动作涉及的金钱数" />
		// <entry name="iReason" type="int" desc="(必填)货币流动一级原因" />
		// <entry name="iSubReason" type="int" desc="(可选)货币流动二级原因" />
		// <entry name="iAddOrReduce" type="int" desc="(必填)增加 0/减少 1" />
		// <entry name="iMoneyType" type="int"
		// desc="(必填)钱的类型MONEYTYPE,其它货币类型参考FAQ文档" />
		// </struct>
		AbsLogLineBuffer buf = TlogHeadUtil.getTlogHead(
				LogTypeConst.TLOG_TYPE_CASH, account, user, us);
		buf.append("0").append(user.getUsrLv()).append(user.getCash())
				.append(addValue > 0 ? addValue : -addValue);
		buf.append(iaction.id).append(0).append(iaction.id < 30 ? 0 : 1)
				.append(iaction.cashType);
		tlogService.log(buf);
	}

	@Override
	public User doGmAddCash(long userId, int num, LogCashAct iaction) {
		if (num < 0) {
			throw new BaseException("钻石数量不能小于0");
		}

		User user = this.lockGetUser(userId);
		if (num == 0) {
			return user;
		}

		if (user == null) {// 君主不存在
			throw new BaseException("君主不存在。");
		}
		int oldCash = user.getCash();
		int balance = oldCash + num;
		user.setCash(balance);
		user.setGiftTotal(user.getGiftTotal() + num);
		userDao.updateUser(user);

		// 记录日志
		setCashLog(LogTypeConst.TYPE_ADDCASH, user, 1, num, oldCash, balance,
				iaction, false);

		// 推送钻石值
		this.sendUserEvent(user);

		if (log.isDebugEnabled()) {
			log.debug("君主:" + userId + "行为:" + iaction + "钻石数增加"
					+ Math.abs(num) + ",当前钻石：" + balance);
		}
		return user;
	}

	/**
	 * 外部调用必须锁user
	 */
	@Override
	public User addGiftCash(User user, int num, LogCashAct iaction) {

		if (user == null) {// 君主不存在
			throw new BaseException("君主不存在。");
		}
		if (num <= 0) {
			throw new BaseException("赠送钻石数量必须是正数。");
		}
		long userId = user.getUserId();
		int balance = 0;
		// if (Constant.USE_OP_TRANS) {
		// long accId = user.getAccId();
		//
		// try {
		// UserSession us = OnlineUserSessionManager
		// .getUserSessionByAccId(accId);
		// // throw new BaseException("用户不在线，不能加钻石");
		// if (us != null) {
		// if (us.isUnion()) {
		// tecentUnionPayService.present(us, addValue);
		// } else if(us.isFaceBook()){
		// //什么都不做
		// }else {
		// balance = tecentPayService.present(us.getpType(), us
		// .getOpenid(), us.getOpenkey(), us
		// .getPay_token(), us.getPf_customer(user), us
		// .getPfKey(), us.getAreaId(), discountId,
		// giftId, String.valueOf(addValue));
		// }
		// }
		//
		// // 查询余额
		// // if (balance == -1) {
		// // balance = tecentPayService.getBalance(us.getpType(), us
		// // .getOpenid(), us.getOpenkey(), us.getPay_token(),
		// // us.getPf_customer(user), us.getPfKey(), us
		// // .getAreaId());
		// // }
		// } catch (Exception e) {
		// log.error("赠送异常", e); // 吃掉
		// }
		// //
		// balance = user.getCash() + addValue;
		// } else {
		// balance = user.getCash() + addValue;
		// }
		balance = user.getCash() + num;

		user.setCash(balance);
		user.setGiftTotal(user.getGiftTotal() + num);
		userDao.updateUser(user);

		// 刷新内政数据
		this.sendUserEvent(user);

		setCashLog(LogTypeConst.TYPE_ADDCASH, user, 1, num, balance - num,
				balance, iaction, false);

		if (log.isDebugEnabled()) {
			log.debug("君主:" + userId + " 行为:" + iaction + " 钻石数"
					+ (num > 0 ? " 增加" : "减少" + Math.abs(num)) + ",当前钻石："
					+ balance);
		}

		return user;
	}

	@Override
	public User doConsumeCash(long userId, int num, LogCashAct iaction) {
		return doConsumeCash(this.lockGetUser(userId), num, iaction, true);
	}

	@Override
	public User doConsumeCash(long userId, int num, LogCashAct iaction,
			boolean recordLocalLog) {
		return doConsumeCash(this.lockGetUser(userId), num, iaction,
				recordLocalLog);
	}

	@Override
	public User doConsumeCash(User user, int num, LogCashAct iaction) {
		return doConsumeCash(user, num, iaction, true);
	}

	@Override
	public User doConsumeCash(User user, int addValue, LogCashAct iaction,
			boolean recordLocalLog) {
		if (addValue == 0) {
			return user;
		}

		if (addValue < 0) {
			throw new BaseException("钻石数量不能小于0");
		}

		if (user == null) {// 君主不存在
			throw new BaseException("君主不存在。");
		}

		long userId = user.getUserId();
		int oldCash = user.getCash();
		int balance = 0;// 使用后的剩余钻石
		if (Constant.USE_OP_TRANS) {// 开放平台
			// 消费
			balance = oldCash - addValue;
			if (balance < 0) {
				StringBuilder sb = new StringBuilder();
				sb.append("您的钻石不足！");
				throw new BaseException(IAMF3Action.ERROR_CASH, sb.toString());
			}
			// TODO 如果需要通知平台加钻石，可以加到这里
		} else {
			balance = oldCash - addValue;
			if (balance < 0) {
				StringBuilder sb = new StringBuilder();
				sb.append("您的钻石不足！");
				throw new BaseException(IAMF3Action.ERROR_CASH, sb.toString());
			}
		}

		user.setCash(balance);
		user.setConsumeCash(user.getConsumeCash() + addValue);
		userDao.updateUser(user);

		// 记录日志
		setCashLog(LogTypeConst.TYPE_USECASH, user, 1, addValue, oldCash,
				balance, iaction, recordLocalLog);

		// 消费本地日志
		if (recordLocalLog) {
			Account account = accountService.getAccount(user.getAccId());
			String pf = Constant.getPfEx(account.getPf());
			UserConsumeLog ulog = new UserConsumeLog(user.getUserId(),
					iaction.iaction, addValue, balance, new Timestamp(
							System.currentTimeMillis()), pf);
			userDao.insertUserConsumeLog(ulog);
		}

		// 推送钻石值
		this.sendUserEvent(user);

		// TODO 通知活动钻石消费
		if (log.isDebugEnabled()) {
			log.debug("君主:" + userId + "行为:" + iaction + "钻石数" + "减少"
					+ addValue + ",当前钻石：" + balance);
		}

		return user;
	}

	@Override
	public void sendUserEvent(User user) {
		// event 刷新道具
		messageService.sendEventMessage(user.getUserId(),
				EventMessage.TYPE_USER_CHANGED, user.getView());

	}

	@Override
	public void doUpdateUserHonor(long userId, int honor, LogUserExpAct iAction) {
		User user = lockGetUser(userId);
		if (user.getUsrLv() >= User.MAX_LV) {
			return;
		}
		_updateUserHonor(user, honor, iAction);
	}

	@Override
	public User doUpdateUserHonor(User user, int honor, LogUserExpAct iAction) {
		// /////
		if (user.getUsrLv() >= User.MAX_LV) {
			return user;
		}

		user = lockGetUser(user);
		return _updateUserHonor(user, honor, iAction);
	}

	private User _updateUserHonor(User user, int honor, LogUserExpAct iAction) {
		int oldHonor = user.getHonor();
		int newHonor = user.getHonor() + honor;
		if (honor < 0) {
			user.setHonor(newHonor);
			userDao.updateUser(user);

		} else {
			int lvNum = user.getUsrLv();
			int restHonor = newHonor;// 升级后剩余的
			LvParaLimit lvp = null;
			while (lvNum < User.MAX_LV) {
				LvParaLimit tmp = this.getLvParaLimit(lvNum);
				if (tmp == null) {
					break;
				}
				lvp = tmp;
				if (lvp != null && restHonor >= lvp.getExp()) {
					restHonor = restHonor - lvp.getExp();
				} else {
					break;
				}
				lvNum++;
			}
			if (lvp != null) {
				int oldLv = user.getUsrLv();
				int newLv = lvNum;
				if (newLv > oldLv) {
					user.setUsrLv(newLv);
					user.setHonor(restHonor);
					long time = System.currentTimeMillis();
					long t = user.getUpLevelTm(time);
					user.setUpLevelTime(new Timestamp(time));
					userDao.updateUser(user);

					Map<String, Object> params = new HashMap<String, Object>(3);
					params.put("user", user);
					params.put("oldLv", oldLv);
					params.put("newLv", newLv);

					// 触发用户升级事件
					Event event = new Event();
					event.setEventType(EventTypeConstants.EVT_USER_LEVEL_UP);
					event.setParams(params);
					EventDispatcher.fireEvent(event);

					// 好友推荐缓存
					friendService.putToFriendRecommendCache(user.getUserId(),
							oldLv, newLv);
					// 用户升级tlog
					this.setTlogByUserLv(user, honor, oldLv, newLv, iAction,
							(time - t));

					// 君主级别:0/君主当前等级
					missionService.notifyMissionModule(user,
							Mission.QCT_TYPE_UPLEVEL, 0, newLv);

					// 成就
					userAchieveService.doNotifyAchieveModule(user.getUserId(),
							AchieveType.TYPE_MILITARY,
							AchieveType.TYPE_MILITARY_ENTID5, newLv);
				} else {
					user.setHonor(newHonor);
					userDao.updateUser(user);
				}

			}
		}
		if (oldHonor != newHonor) {
			sendUserEvent(user);
		}
		return user;
	}

	@Override
	public User addGiftCash(long userId, int num, LogCashAct iaction) {
		return addGiftCash(this.lockGetUser(userId), num, iaction);
	}

	public void setFriendService(IFriendService friendService) {
		this.friendService = friendService;
	}

	@Override
	public int getActPointMaxA(User user) {
		LvParaLimit limit = this.getLvParaLimit(user.getUsrLv());
		int max = limit.getActionPointLimit();
		return max;
	}

	@Override
	public int getActPointMaxB() {
		return commonService.getSysParaIntValue(
				AppConstants.USER_MAX_ACT_POINT, 600);
	}

	@Override
	public void sendActPointEvent(long userId, int actionPoint, int actionLimit) {
		// event 刷新道具
		messageService.sendEventMessage(userId,
				EventMessage.TYPE_ACTPOINT_CHANGED, new ActPointMessage(
						actionPoint, actionLimit));
	}

	@Override
	public User doCostCurActPoint(long userId, int num, LogActiveAct act) {
		User user = this.lockGetUser(userId);
		return doCostCurActPoint(user, num, act);
	}

	@Override
	public User doCostCurActPoint(User user, int num, LogActiveAct act) {
		if (num <= 0) {
			throw new BaseException("扣除行动力数据错误" + num);
		}
		user = this.lockGetUser(user);
		int curActPoint = user.getCurActPoint();
		if (num > curActPoint) {
			throw new BaseException("消耗的行动力超过拥有的值");
		}
		int rest = curActPoint - num;
		if (curActPoint != rest) {
			user.setHpPoint(rest);
			this.doUpdateUser(user);
			// 推送体力变化
			this.sendHpPointEvent(user.getUserId(), user.getCurActPoint(),
					this.getActPointMaxA(user));
			if (null != act) {
				AbsLogLineBuffer buffer = TlogHeadUtil.getTlogHead(
						LogTypeConst.TLOG_TYPE_HpActiveFlow, user);
				tlogService.log(buffer.append(0).append(curActPoint)
						.append(rest).append(act.vuale));
			}
		}
		return user;
	}

	@Override
	public User doAddCurActPoint(User user, int num, LogActiveAct act) {
		if (num <= 0) {
			throw new BaseException("增加行动力数据错误" + num);
		}
		user = this.lockGetUser(user.getUserId());
		int maxActPoint = this.getActPointMaxB();
		int curActPoint = user.getCurActPoint();
		if (curActPoint >= maxActPoint) {
			throw new BaseException("行动力已经达到上限。");
		}
		int rest = curActPoint + num;
		if (rest != curActPoint) {
			user.setCurActPoint(rest);
			userDao.updateUser(user);
			// 推送行动力变化
			this.sendActPointEvent(user.getUserId(), user.getCurActPoint(),
					getActPointMaxA(user));
			if (null != act) {
				AbsLogLineBuffer buffer = TlogHeadUtil.getTlogHead(
						LogTypeConst.TLOG_TYPE_HpActiveFlow, user);
				tlogService.log(buffer.append(1).append(curActPoint)
						.append(rest).append(act.vuale));
			}
		}
		return user;
	}

	@Override
	public User doAddCurActPoint(long userId, int num, LogActiveAct act) {
		User user = this.lockGetUser(userId);
		return doAddCurActPoint(user, num, act);
	}

	@Override
	public int getHpPointMaxA(User user) {
		LvParaLimit limit = this.getLvParaLimit(user.getUsrLv());
		int max = limit.getHpPointLimit();
		return max;
	}

	@Override
	public int getHpPointMaxB() {
		int maxHpPoint = commonService.getSysParaIntValue(
				AppConstants.USER_MAX_HP_POINT, 600);
		return maxHpPoint;
	}

	@Override
	public void sendHpPointEvent(long userId, int hpPoint, int hpLimit) {
		// event 刷新道具
		messageService.sendEventMessage(userId,
				EventMessage.TYPE_HPPOINT_CHANGED, new HpPointMessage(hpPoint,
						hpLimit));
	}

	@Override
	public User doAddHpPoint(long userId, int num, LogHpAct act) {
		User user = this.lockGetUser(userId);
		return doAddHpPoint(user, num, act);
	}

	@Override
	public User doAddHpPoint(User user, int num, LogHpAct act) {
		if (num <= 0) {
			throw new BaseException("增加体力数据错误" + num);
		}
		user = this.lockGetUser(user);

		int maxHpPoint = getHpPointMaxB();

		int curHpPoint = user.getHpPoint();
		if (curHpPoint >= maxHpPoint) {
			throw new BaseException("体力已经达到上限。");
		}
		int rest = curHpPoint + num;
		if (curHpPoint != rest) {
			user.setHpPoint(rest);
			this.doUpdateUser(user);
			// 推送体力变化
			this.sendHpPointEvent(user.getUserId(), user.getHpPoint(),
					this.getHpPointMaxA(user));

			if (null != act) {
				AbsLogLineBuffer buffer = TlogHeadUtil.getTlogHead(
						LogTypeConst.TLOG_TYPE_HpActiveFlow, user);
				tlogService.log(buffer.append(0).append(curHpPoint)
						.append(rest).append(act.vuale));
			}
		}
		return user;
	}

	@Override
	public User doCostHpPoint(User user, int num, LogHpAct act) {
		if (num <= 0) {
			throw new BaseException("扣除体力数据错误" + num);
		}
		user = this.lockGetUser(user);
		int curHpPoint = user.getHpPoint();
		if (num > curHpPoint) {
			throw new BaseException("消耗的体力超过拥有的值");
		}
		int rest = curHpPoint - num;
		if (curHpPoint != rest) {
			user.setHpPoint(rest);
			this.doUpdateUser(user);
			// 推送体力变化
			this.sendHpPointEvent(user.getUserId(), user.getHpPoint(),
					this.getHpPointMaxA(user));
			if (null != act) {
				AbsLogLineBuffer buffer = TlogHeadUtil.getTlogHead(
						LogTypeConst.TLOG_TYPE_HpActiveFlow, user);
				tlogService.log(buffer.append(0).append(curHpPoint)
						.append(rest).append(act.vuale));
			}
		}
		return user;
	}

	@Override
	public User doCostHpPoint(long userId, int num, LogHpAct act) {
		User user = this.lockGetUser(userId);
		return doCostHpPoint(user, num, act);
	}

	@Override
	public String doChangeUserName(long userId, String name) {
		name = StringUtils.trim(name);
		if (StringUtils.isEmpty(name)) {
			throw new BaseException("玩家名不能为空");
		}
		if (name.length() > 6) {
			throw new BaseException("玩家名必须为1-6个字符");
		}
		if (!StringUtils.match(name, reg)) {
			throw new BaseException("玩家名包含非法字符或空格，请重新输入");
		}
		if (sensitiveWordService.match(name)) {
			throw new BaseException("玩家名包含非法词语,请重新输入");
		}
		if (userDao.getUserByName(name) != null) {
			throw new BaseException("此玩家名已使用。");
		}

		int itemId = commonService.getSysParaIntValue(
				AppConstants.CHANGE_USERNAME_ITEM_ITEM, 0);
		int num = 1;
		// 判断是否有道具
		if (treasuryService.getTreasuryCountByEntId(userId, itemId) < num) {
			throw new BaseException("更名卡道具不足！");
		}

		treasuryService.deleteItemFromTreasury(userId, itemId, num, true,
				com.youxigu.dynasty2.log.imp.LogItemAct.LOGITEMACT_65);
		User user = lockGetUser(userId);
		String oldName = user.getUserName();
		user.setUserName(name);
		userDao.updateUser(user);
		// user是异步更新的，5分钟内按名字查，查不到数据，除非修改成同步更新，或者自己加入到mc中
		// 更新城池名字
		Castle castle = castleService.lockAndGetCastle(user.getMainCastleId());
		castle.setCasName(name);
		castleService.doUpdateCastle(castle);

		// // 更新rankData
		// rankService.updateUserName(userId, name);
		//
		// long guildId = user.getGuildId();
		// if (guildId != 0) {
		// Guild guild = guildService.getGuildById(guildId);
		// if (guild != null) {
		// if (userId == guild.getLeaderId()) {
		// guild = guildService.lockGuild(guildId);
		// guild.setLeaderId(userId);
		// guild.setLeader(name);
		// guildService.updateGuild(guild);
		// }
		// GuildUser gUser = guildService.getGuildUserByUserId(userId);
		// gUser.setName(name);
		// guildService.updateGuildUser(gUser);
		// }
		// }

		// /修改在线聊天中缓存的名字
		messageService.changeUserNameSexUsrLv(userId, name, user.getSex(),
				user.getUsrLv());

		// //缓存中会保留原来的名字，依然可以查询到，直到缓存丢失.
		MemcachedManager.delete("getUserByName_" + oldName, 3);
		MemcachedManager.set("getUserByName_" + name, "user_" + userId);

		return name;
	}

	@Override
	public void doFillUpHpAfteQuarters(User user, Timestamp now, int quarters) {
		if (quarters <= 0) {
			return;
		}
		// 刷新君主行动力
		boolean update = false;
		LvParaLimit limit = getLvParaLimit(user.getUsrLv());
		user = lockGetUser(user);
		// user.setLastActDmDttm(now);
		int dayActPoint = getActPointMaxA(user);// 行动力上限
		int oldActPoint = user.getCurActPoint();
		// int curActPoint = user.getCurActPoint();
		if (oldActPoint < dayActPoint) {
			int curActPoint = oldActPoint + limit.getPerActPoint() * quarters;
			if (curActPoint > dayActPoint) {
				curActPoint = dayActPoint;
			}
			user.setCurActPoint(curActPoint);
			update = true;
		}

		int maxHpPoint = getHpPointMaxA(user);// 体力上限
		int oldHpPoint = user.getHpPoint();
		if (oldHpPoint < maxHpPoint) {
			int curHpPoint = oldHpPoint + limit.getPerHpPoint() * quarters;
			if (curHpPoint > maxHpPoint) {
				curHpPoint = maxHpPoint;
			}
			user.setHpPoint(curHpPoint);
			update = true;
		}

		if (update) {
			userDao.updateUser(user);
			// 发提示消//
			StringBuilder sb = new StringBuilder(30);
			sb.append("原行动力：").append(oldActPoint).append(",增加")
					.append(user.getCurActPoint() - oldActPoint)
					.append(",原体力：").append(oldHpPoint).append(",增加")
					.append(user.getHpPoint() - oldHpPoint);
			messageService.sendMessage(0, user.getUserId(),
					ChatChannelManager.CHANNEL_SYSTEM, null, sb.toString());
		}

	}

	@Override
	public User doAddjunGong(long userId, int num, JunGongAct act) {
		User user = this.lockGetUser(userId);
		return doAddjunGong(user, num, act);
	}

	@Override
	public User doAddjunGong(User user, int num, JunGongAct act) {
		if (num <= 0) {
			throw new BaseException("增加军功数据错误" + num);
		}
		user = this.lockGetUser(user);

		int junGong = user.getJunGong();
		int rest = junGong + num;
		if (junGong != rest) {
			user.setJunGong(rest);
			this.doUpdateUser(user);

			if (null != act) {
				logService.log(LogBeanFactory.buildGetJunGongLog(user,
						act.value, rest, num));
			}

			// 触发用户升级事件
			Event event = new Event();
			event.setEventType(EventTypeConstants.EVT_USER_JUNGONG_ADD);
			event.setParams(UtilMisc.toMap("user", user, "jungong", rest));
			EventDispatcher.fireEvent(event);
		}
		return user;
	}

	@Override
	public User doCostjunGong(long userId, int num, JunGongAct act) {
		User user = this.lockGetUser(userId);
		return doCostjunGong(user, num, act);

	}

	@Override
	public User doCostjunGong(User user, int num, JunGongAct act) {
		if (num <= 0) {
			throw new BaseException("扣除军功数据错误" + num);
		}
		user = this.lockGetUser(user);
		int junGong = user.getJunGong();
		if (num > junGong) {
			throw new BaseException("消耗的军功超过拥有的值");
		}
		int rest = junGong - num;
		if (junGong != rest) {
			user.setJunGong(rest);
			this.doUpdateUser(user);

			if (null != act) {
				logService.log(LogBeanFactory.buildGetJunGongLog(user,
						act.value, rest, num));
			}
		}
		return user;

	}

	@Override
	public void doChangeCountry(long userId, int newCountryId) {
		// Country country = mapService.getCountryById(newCountryId);
		// if (country == null) {
		// throw new BaseException("国家编号错误");
		// }
		// // 锁君主
		// User user = lockGetUser(userId);
		// // 校验是否免费
		// boolean isFree = this.getNewBeeNumChangeCountry(user);// 是否已经在新手期转国
		//
		// if (!isFree) {
		// // 如果不是免费，需要消耗道具
		// if (treasuryService.getTreasuryCountByEntId(userId,
		// AppConstants.CHANGE_COUNTRY_ITEM_ITEM) < 1) {
		// Item itm = (Item) entityService
		// .getEntity(AppConstants.CHANGE_COUNTRY_ITEM_ITEM);
		// throw new BaseException(itm.getItemName() + "不足！");
		// }
		// treasuryService.deleteItemFromTreasury(userId,
		// AppConstants.CHANGE_COUNTRY_ITEM_ITEM, 1, true,
		// com.youxigu.dynasty2.log.imp.LogItemAct.LOGITEMACT_30);
		// }
		// // 锁城池
		// Castle castle = this.lockGetCastle(user.getMainCastleId());
		// if (castle.getStatus() == Castle.STATUS_OVER) {
		// throw new BaseException("流亡状态，不能转换国家。");
		// }
		//
		// // 校验是否在同一国家
		// int oldCountryId = user.getCountryId();
		// if (oldCountryId == newCountryId) {
		// throw new BaseException("不能选择当前国家。");
		// }
		//
		// // 校验是否在联盟内
		// if (user.getGuildId() > 0) {
		// throw new BaseException("请先退出联盟。");
		// }
		//
		// // 校验是否在冷却期内
		// Timestamp now = DateUtils.nowTimestamp();
		// int time = this.getChangeCountryTime();// 转国的冷却时间
		//
		// if (castle.getChangeCountryDttm() != null) {// 转换过国家
		// if (castle.getChangeCountryDttm().getTime() - now.getTime() > 0) {//
		// 没有到达冷却时间
		// throw new BaseException("冷却时间内无法转换国家。");
		// }
		// }
		//
		// // 删除所有联盟邀请，联盟申请
		// guildService.deleteGuildAppAndInvDataByUserId(userId);
		//
		// Timestamp endDttm = UtilDate.moveSecond(now, time);
		// castle.setChangeCountryDttm(endDttm);// 记录转国的时间
		// this.updateCastle(castle);
		//
		// // 修改君主信息
		// user.setCountryId(newCountryId);
		// user.setIcon(userService
		// .getUserIcon(user.getCountryId(), user.getSex()));
		//
		// // 修改君主威望
		// int prestige = user.getPrestige();
		// double reducePrestige = Double.valueOf(commonService.getSysParaValue(
		// AppConstants.SYS_OFFICIAL_REDUCE_PRECENT, "0.9"));
		// int lastPrestige = (int) (prestige * reducePrestige);
		// Map<String, Object> params = new HashMap<String, Object>(3);
		//
		// if (lastPrestige > 0) {
		// user.setPrestige(lastPrestige);
		// params.put("prestige", lastPrestige);// 声望
		// }
		// // 官职设置为小卒
		// params.put("gradeId", 1);
		// params.put("isOffical", 0);
		// //封地加成效果清零
		// params.put("fiefBuffTime", 0);// 封地效果加成buff剩余秒数
		// params.put("fiefBuffId", null);// 封地效果加成buff配置id
		// // 票数清零
		// officialService.doClearTickets(userId);
		//
		// userService.updateUser(user);
		//
		// // 更新排行
		// rankService.updateCountry(userId, newCountryId);
		//
		// // 重新加入国家频道
		// chatService.changeUserCountryIcon(userId, newCountryId,
		// user.getIcon());
		//
		// // event 刷新内政数据TYPE_MOVECASTLE
		// // Map<String, Object> params = new HashMap<String, Object>();
		//
		// params.put("countryId", newCountryId);
		// params.put("icon", user.getIcon());
		// this.sendFlushDevDataEvent(castle.getUserId(),
		// EventMessage.TYPE_USER_CHANGED, params);
		// // 记录免费转国使用记录
		// if (isFree) {
		// userAttrService.saveUserAttr(user.getUserId(),
		// AppConstants.USERATTR_NEWBEE_CHANGE_COUNTRY, 1);
		// }
		// String via = null;
		// String areaId = null;
		// UserSession us = OnlineUserSessionManager
		// .getUserSessionByUserId(userId);
		// if (us != null) {
		// via = us.getVia();
		// areaId = us.getAreaId();
		// } else {
		// Account account = accountService.getAccount(user.getAccId());
		// via = account.getVia();
		// areaId = account.getAreaId();
		// }
		//
		// logService.log(AbsLogLineBuffer.getBuffer(areaId,
		// TLogTable.TLOG_CHANGECOUNTRY.getName()).append(userId).append(
		// user.getUsrLv()).append(oldCountryId).append(newCountryId)
		// .append(now).append(via).append(user.getCreateDate()));
	}

	@Override
	public int[] doAutoCityPoint(long userId) {
		// 判断玩家是否分配的建城点
		User user = userDao.getUserById(userId);
		Castle castle = castleService.getCastleByUserId(user.getMainCastleId());
		if (castle == null) {
			// 城池不存在
			throw new BaseException("城池不存在");
		}

		if (castle.hasPoint()) {
			throw new BaseException("已经分配建城点");
		}

		Country ct = mapService.getCountryById(user.getCountryId());
		List<MapCell> mcs = mapService.getRandomIdleCityCellGroup(
				ct.getStateId(), true);
		if (mcs == null) {
			cityPoints.put(userId, userId);
			// 未找到空闲的建城点
			return null;
		}

		// 开始建城
		MapCell first = mcs.get(0);
		castle.setPosX(first.getPosX());
		castle.setPosY(first.getPosY());
		castleService.doUpdateCastle(castle);

		for (MapCell m : mcs) {
			m.changeOwer(userId);
			mapService.updateMapCell(m);
		}

		removeCityPoint(userId);
		return new int[] { first.getPosX(), first.getPosY() };
	}

	private void removeCityPoint(long userId) {
		cityPoints.remove(userId);
	}

	@Override
	public boolean doSetCityPoint(long userId, int posX, int posY) {
		Long u = cityPoints.remove(userId);
		if (u == null) {
			throw new BaseException("请使用自动分配建城点");
		}
		// 判断玩家是否分配的建城点
		User user = userDao.getUserById(userId);
		Castle castle = castleService.getCastleByUserId(user.getMainCastleId());
		if (castle == null) {
			// 城池不存在
			throw new BaseException("城池不存在");
		}

		if (castle.hasPoint()) {
			throw new BaseException("已经分配建城点");
		}
		List<Integer> ids = mapService.getCityPoint(posX, posY);
		List<MapCell> cells = null;
		try {
			cells = mapService.getMapCellForWrite(ids);
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw new BaseException("设置建城点异常");
		}
		// 判断是否已经绑定用户
		boolean b = mapService.canSetPointUses(cells);
		if (!b) {
			throw new BaseException("建城点不符合规则");
		}

		for (MapCell m : cells) {
			m.changeOwer(userId);
			mapService.updateMapCell(m);
		}

		castle.setPosX(posX);
		castle.setPosY(posY);
		castleService.doUpdateCastle(castle);
		return true;
	}
}
