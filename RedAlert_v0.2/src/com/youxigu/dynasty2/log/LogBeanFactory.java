package com.youxigu.dynasty2.log;

import java.sql.Timestamp;
import java.util.Map;

import com.manu.core.ServiceLocator;
import com.youxigu.dynasty2.entity.domain.SysHero;
import com.youxigu.dynasty2.hero.domain.Hero;
import com.youxigu.dynasty2.mail.domain.MailMessage;
import com.youxigu.dynasty2.openapi.Constant;
import com.youxigu.dynasty2.risk.domain.RiskScene;
import com.youxigu.dynasty2.user.domain.Account;
import com.youxigu.dynasty2.user.domain.User;
import com.youxigu.dynasty2.user.service.IAccountService;
import com.youxigu.wolf.net.OnlineUserSessionManager;
import com.youxigu.wolf.net.UserSession;

public class LogBeanFactory {
	private static final IAccountService ACCOUNT_SERVICE = (IAccountService) ServiceLocator
			.getSpringBean("accountService");

	/**
	 * 
	 * 
	 * 
	 * 获得途径，获得武将名称及ID或武将信物名称和ID，获得时间
	 * 
	 * TLOG_GETHERO("tlog_gethero", new String[] { "areaId", "openId", "userId",
	 * "userName", "userLv", "entId", "heroName","reason","type","dttm" }),
	 * 
	 * @param user
	 * @param hero
	 * @param type
	 *            0获得武将，1信物
	 * @return
	 */
	public static AbsLogLineBuffer buildGetHeroLog(User user, int entId,
			String heroName, int type, String reason) {
		AbsLogLineBuffer buffer = getHead(user,
				TLogTable.TLOG_GETHERO.getName());
		buffer.append(entId).append(heroName).append(reason).append(type)
				.append(new Timestamp(System.currentTimeMillis()));
		return buffer;
	}

	/**
	 * 升级武将
	 * 
	 * 武将名称及ID，升级后等级， 、升级消耗铜币数量，升级后武将经验池剩余经验，剩余铜币数量，升级时间
	 * 
	 * TLOG_LVHERO("tlog_lvhero", new String[] { "areaId", "openId", "userId",
	 * "userName", "userLv", "entId", "beforeLv", "afterLv",
	 * "num","heroId","remainMoney", "dttm" }),
	 * 
	 */
	public static AbsLogLineBuffer buildLvHeroLog(User user, int entId,
			int beforeLv, int afterLv, int num, long heroId,
			int remainMoney) {
		AbsLogLineBuffer buffer = getHead(user, TLogTable.TLOG_LVHERO.getName());
		buffer.append(entId).append(beforeLv).append(afterLv).append(num)
				.append(heroId).append(remainMoney)
				.append(new Timestamp(System.currentTimeMillis()));
		return buffer;
	}

	/**
	 * 分解武将日志
	 * 
	 * TLOG_DISCARDHERO("tlog_discardhero", new String[] { "areaId", "openId",
	 * "userId", "userName", "userLv", "entId", "heroName", "heroId",
	 * "sysHeroId", "dttm" }),
	 * 
	 * @param user
	 * @param entId
	 * @param heroName
	 * @param jianghunId
	 * @param jianghunCount
	 * @param type0获得武将
	 *            ，1信物
	 * @return
	 */
	public static AbsLogLineBuffer buildDiscardHeroLog(User user, int entId,
			String heroName, long heroId, int sysHeroId) {
		AbsLogLineBuffer buffer = getHead(user,
				TLogTable.TLOG_DISCARDHERO.getName());
		buffer.append(entId).append(heroName).append(heroId)
				.append(sysHeroId)
				.append(new Timestamp(System.currentTimeMillis()));
		return buffer;
	}

	/**
	 * 
	 * 神秘商店购买 购买物品名称和ID，购买消耗（将魂），购买后剩余将魂数量，购买时间，
	 * 
	 * TLOG_MYSTRICSHOP("tlog_mysticshop", new String[] { "areaId", "openId",
	 * "userId", "userName", "userLv", "entId", "entName",
	 * "count","remainResource", "type", "dttm" }),
	 * 
	 * @param user
	 * @param entId
	 * @param entName
	 * @param count
	 * @param remainResource
	 *            消耗资源剩余量
	 * @param type
	 *            货币类型
	 * @return
	 */
	public static AbsLogLineBuffer buildMysticShopLog(User user, int entId,
			String entName, int count, int remainResource, int type) {
		AbsLogLineBuffer buffer = getHead(user,
				TLogTable.TLOG_MYSTRICSHOP.getName());
		buffer.append(entId).append(entName).append(count)
				.append(remainResource).append(type)
				.append(new Timestamp(System.currentTimeMillis()));
		return buffer;
	}

	/**
	 * 商店刷新消耗（使用免费刷新次数还是消耗元宝刷新），剩余免费刷新次数，进行商店刷新时间
	 * 
	 * TLOG_MYSTRICFREASH("tlog_mysticfreash", new String[] { "areaId",
	 * "openId", "userId", "userName", "userLv", "freashType", "cost",
	 * "remainCnt", "dttm" }),
	 * 
	 * 
	 * @param user
	 * @param entId
	 * @param freashType
	 *            ==1花费
	 * @param cost
	 * @param remainCnt
	 * @return
	 */
	public static AbsLogLineBuffer buildMysticFreashLog(User user,
			int freashType, int cost, int remainCnt) {
		AbsLogLineBuffer buffer = getHead(user,
				TLogTable.TLOG_MYSTRICFREASH.getName());
		buffer.append(freashType).append(cost).append(remainCnt)
				.append(new Timestamp(System.currentTimeMillis()));
		return buffer;
	}

	/**
	 * 装备合成 合成消耗（装备碎片名称和数量），合成的装备名称和ID，合成后剩余装备碎片数量，合成时间
	 * 
	 * TLOG_EquipCompose("tlog_equipcompose", new String[] { "areaId", "openId",
	 * "userId", "userName", "userLv", "item1", "num1", "item2", "num2",
	 * "item3", "num3", "composeEntId", "entName", "remainItem1", "remainNum1",
	 * "remainItem2", "remainNum2", "remainItem3", "remainNum3", "dttm" }),
	 * 
	 * @param user
	 * @param item1
	 *            消耗物品
	 * @param num1
	 * @param item2
	 * @param num2
	 * @param item3
	 * @param num3
	 * @param composeEntId
	 *            合成物品
	 * @param entName
	 * @param remainItem1
	 *            剩余物品
	 * @param remainNum1
	 * @param remainItem2
	 * @param remainNum2
	 * @param remainItem3
	 * @param remainNum3
	 * @param dttm
	 * @return
	 */
	public static AbsLogLineBuffer buildEquipComposeLog(User user, int item1,
			int num1, int item2, int num2, int item3, int num3,
			int composeEntId, String entName, int remainNum1, int remainNum2,
			int remainNum3) {
		AbsLogLineBuffer buffer = getHead(user,
				TLogTable.TLOG_EQUIPCOMPOSE.getName());
		buffer.append(item1).append(num1).append(item2).append(num2)
				.append(item3).append(num3).append(composeEntId)
				.append(entName).append(remainNum1).append(remainNum2)
				.append(remainNum3)
				.append(new Timestamp(System.currentTimeMillis()));
		return buffer;
	}

	/**
	 * 装备回炉日志 装备名称和ID，装备回炉返还（道具名称和数量），回炉时间
	 * 
	 * TLOG_EquipRebirth("tlog_equiprebirth", new String[] { "areaId", "openId",
	 * "userId", "userName", "userLv", "item1", "num1", "item2", "num2",
	 * "item3", "num3", "item4", "num4", "item5", "num5", "entId",
	 * "entName","returnMoney", "dttm" }), ;
	 * 
	 * @param user
	 * @param entId
	 * @param entName
	 * @param returnMoney
	 * @param returnItems
	 * @return
	 */
	public static AbsLogLineBuffer buildEquipRebirthLog(User user, int entId,
			String entName, int returnMoney, Map<Integer, Integer> returnItems) {
		AbsLogLineBuffer buffer = getHead(user,
				TLogTable.TLOG_EQUIPREBIRTH.getName());
		int index = 0;
		for (Map.Entry<Integer, Integer> entry : returnItems.entrySet()) {
			buffer.append(entry.getKey()).append(entry.getValue());
			++index;
		}
		for (int i = index; i < 5; i++) {
			buffer.append(0).append(0);
		}
		buffer.append(entId).append(entName).append(returnMoney)
				.append(new Timestamp(System.currentTimeMillis()));
		return buffer;
	}

	// /**
	// * 邀请的军师名称和ID，邀请时间 TLOG_GetArmyAdviser("tlog_getarmyadviser", new String[]
	// {
	// * "areaId", "openId", "userId", "userName", "userLv", "entId", "entName",
	// * "dttm" }),
	// *
	// * @param user
	// * @param entId
	// * @param entName
	// * @return
	// */
	// public static AbsLogLineBuffer buildGetArmyAdviserLog(User user, int
	// entId,
	// String entName) {
	// AbsLogLineBuffer buffer = getHead(user,
	// TLogTable.TLOG_GETARMYADVISER.getName());
	// buffer.append(entId).append(entName)
	// .append(new Timestamp(System.currentTimeMillis()));
	// return buffer;
	// }
	//
	// /**
	// * 军师合成装备名称，时间 TLOG_ARMYEQUIPCOMPOSE("tlog_armyequipCompose", new String[]
	// {
	// * "areaId", "openId", "userId", "userName", "userLv", "entId", "entName",
	// * "dttm" }),,
	// *
	// * @param user
	// * @param entId
	// * @param entName
	// * @return
	// */
	// public static AbsLogLineBuffer buildArmyEquipLog(User user, int entId,
	// String entName) {
	// AbsLogLineBuffer buffer = getHead(user,
	// TLogTable.TLOG_ARMYEQUIPCOMPOSE.getName());
	// buffer.append(entId).append(entName)
	// .append(new Timestamp(System.currentTimeMillis()));
	// return buffer;
	// }

	/**
	 * 联盟祈福
	 * 
	 * 联盟名称和ID，祈福获得道具名称及ID，当前贡献，消耗贡献，祈福时间,luckNum
	 * 
	 * TLOG_GUILDLUCK("tlog_guildluck", new String[] { "areaId", "openId",
	 * "userId", "userName", "userLv", "guildId", "guildName",
	 * "remainConstruction", "costCons", "luckNum", "entId", "entName",
	 * "entNum", "dttm" }),
	 * 
	 * @param user
	 * @param guildId
	 * @param guildName
	 * @param remainConstruction
	 * @param costCons
	 * @param luckNum
	 * @param entId
	 *            奖励，只有一个
	 * @param entName
	 * @param entNum
	 * @return
	 */
	public static AbsLogLineBuffer buildGuildLuckLog(User user, long guildId,
			String guildName, int remainConstruction, int costCons, int luckNum,
			int entId, String entName, int entNum) {
		AbsLogLineBuffer buffer = getHead(user,
				TLogTable.TLOG_GUILDLUCK.getName());
		buffer.append(guildId).appendLegacy(guildName);
		buffer.append(remainConstruction).append(costCons);
		buffer.append(luckNum);
		buffer.append(entId).append(entName).append(entNum)
				.append(new Timestamp(System.currentTimeMillis()));
		return buffer;
	}

	/**
	 * 建筑升级
	 * 
	 * 建筑名称，当前等级，升级后等级，升级时间
	 * 
	 * TLOG_LVBUILD("tlog_lvbuild", new String[] { "areaId", "openId", "userId",
	 * "userName", "userLv", "entId", "entName", "curLv", "nextLv", "dttm" }),
	 */
	public static AbsLogLineBuffer buildLvBuildLog(User user, int entId,
			String entName, int curLv, int nextLv) {
		AbsLogLineBuffer buffer = getHead(user,
				TLogTable.TLOG_LVBUILD.getName());
		buffer.append(entId).append(entName).append(curLv).append(nextLv)
				.append(new Timestamp(System.currentTimeMillis()));
		return buffer;
	}

	/**
	 * 科技升级
	 * 
	 * 科技实体id，名称， 科技原等级，科技升级后等级
	 * 
	 * TLOG_LVTECH("tlog_lvtech", new String[] { "areaId", "openId", "userId",
	 * "userName", "userLv", "entId", "entName", "curLv", "nextLv", "dttm" }),
	 */
	public static AbsLogLineBuffer buildLvTechLog(User user, int entId,
			String entName, int curLv, int nextLv) {
		AbsLogLineBuffer buffer = getHead(user, TLogTable.TLOG_LVTECH.getName());
		buffer.append(entId).append(entName).append(curLv).append(nextLv)
				.append(new Timestamp(System.currentTimeMillis()));
		return buffer;
	}

	/**
	 * 庇佑方式，庇佑消耗（元宝或铜币数量），庇佑获得经验数量，剩余庇佑次数，庇佑时间。
	 * 
	 * @param user
	 * @param cashCost
	 * @param resCost
	 * @param resourceNum
	 * @param cashNum
	 * @return
	 */
	public static AbsLogLineBuffer buildShelterLog(User user, int cashCost,
			int resCost, int resourceNum, int cashNum) {
		AbsLogLineBuffer buffer = getHead(user,
				TLogTable.TLOG_SHELTER.getName());
		buffer.append(cashCost).append(resCost).append(cashNum)
				.append(resourceNum)
				.append(new Timestamp(System.currentTimeMillis()));
		return buffer;
	}

	/**
	 * 经验兑换数量，兑换时间，兑换后武将经验池经验。
	 * 
	 * TLOG_EXCHANGE("tlog_exchange", new String[] { "areaId", "openId",
	 * "userId", "userName", "userLv", "exchange", "remainExp", "dttm" }),
	 */
	public static AbsLogLineBuffer buildExchangeLog(User user, int exchange,
			int remainExp) {
		AbsLogLineBuffer buffer = getHead(user,
				TLogTable.TLOG_EXCHANGE.getName());
		buffer.append(exchange).append(remainExp)
				.append(new Timestamp(System.currentTimeMillis()));
		return buffer;
	}

	/**
	 * 
	 * 活跃度获得途径，当前活跃度，增加数量，获得时间 TLOG_GETACTIVE("tlog_getactive", new String[] {
	 * "areaId", "openId", "userId", "userName", "userLv", "actId", "curActive",
	 * "num", "dttm" }),
	 * 
	 * 
	 * @param user
	 * @param actId
	 * @param curActive
	 * @param num
	 * @return
	 */
	public static AbsLogLineBuffer buildGetActiveLog(User user, int actId,
			int curActive, int num) {
		AbsLogLineBuffer buffer = getHead(user,
				TLogTable.TLOG_GETACTIVE.getName());
		buffer.append(actId).append(curActive).append(num)
				.append(new Timestamp(System.currentTimeMillis()));
		return buffer;
	}

	/**
	 * 
	 * CDK兑换 兑换奖励 accName,userID，角色名、角色等级 使用的兑换码，兑换奖励内容，兑换时间
	 * TLOG_CDKEXCHANGE("tlog_cdkexchange", new String[] { "areaId", "openId",
	 * "userId", "userName", "userLv", "cdk", "rwds", "dttm" }),
	 * 
	 * @param user
	 * @param cdk
	 * @return
	 */
	public static AbsLogLineBuffer buildCDKExchangeLog(User user, String cdk,
			String rwds) {
		AbsLogLineBuffer buffer = getHead(user,
				TLogTable.TLOG_CDKEXCHANGE.getName());
		if (rwds != null) {
			rwds = rwds.replaceAll(",", "-");
		}
		buffer.append(cdk).append(rwds)
				.append(new Timestamp(System.currentTimeMillis()));
		return buffer;
	}

	// "openId", "userId",
	// * "userName", "userLv",
	private static AbsLogLineBuffer getHead(User user, String logName) {
		UserSession us = null;
		long userId = 0;
		String userName = "";
		int usrLv = 0;
		long accId = 0;
		if (user != null) {
			us = OnlineUserSessionManager.getUserSessionByUserId(user
					.getUserId());
			userId = user.getUserId();
			userName = user.getUserName();
			usrLv = user.getUsrLv();
			accId  = user.getAccId();
		}
		
		
		String areaId = "";
		String openId = "";
		if (us != null) {
			areaId = us.getAreaId();
			openId = us.getOpenid();
		} else {
			Account account = ACCOUNT_SERVICE.getAccount(accId);
			if(account!=null){
				openId = account.getAccName();
				areaId = account.getAreaId();
			}
		}
		return AbsLogLineBuffer.getBuffer(areaId, logName).append(openId)
				.append(userId).append(userName)
				.append(usrLv);
	}

	/**
	 * 增加军功日志 TLOG_USER_JUNGONG("tlog_getJungong", new String[] { "areaId",
	 * "openId", "userId", "userName", "userLv", "actId", "curJunGong", "num",
	 * "dttm" }),
	 * 
	 * @param user
	 * @param actId
	 * @param junGong
	 * @param num
	 * @return
	 */
	public static AbsLogLineBuffer buildGetJunGongLog(User user, int actId,
			int junGong, int num) {
		AbsLogLineBuffer buffer = getHead(user,
				TLogTable.TLOG_USER_JUNGONG.getName());
		buffer.append(actId).append(junGong).append(num)
				.append(new Timestamp(System.currentTimeMillis()));
		return buffer;
	}

	/**
	 * 君主任务 TLOG_USER_MISSION("tlog_mission", new String[] { "areaId", "openId",
	 * "userId", "userName", "userLv", "missionType", "missionId",
	 * "missionName", "status", "dttm" }),
	 * 
	 * @param user
	 * @param missionType
	 * @param missionId
	 * @param missionName
	 * @param flag
	 * @return
	 */
	public static AbsLogLineBuffer buildMissionLog(User user,
			String missionType, int missionId, String missionName, int status) {
		AbsLogLineBuffer buffer = getHead(user,
				TLogTable.TLOG_USER_MISSION.getName());
		buffer.append(missionType).append(missionId).append(missionName)
				.append(status)
				.append(new Timestamp(System.currentTimeMillis()));
		return buffer;
	}

	/**
	 *
	 * 特殊事件
	 *
	 * TLOG_USER_WORLGMISSION("tlog_worldmission", new String[] { "areaId",
	 * "missionId", "missionName", "status", "dttm" }),
	 * 
	 * @param missionId
	 * @param missionName
	 * @param status
	 * @return
	 */
	public static AbsLogLineBuffer buildWorldMissionLog(int missionId,
			String missionName, int status) {
		AbsLogLineBuffer buffer = AbsLogLineBuffer.getBuffer(Constant.AREA_ID,
				TLogTable.TLOG_USER_WORLGMISSION.getName());
		buffer.append(missionId).append(missionName).append(status)
				.append(new Timestamp(System.currentTimeMillis()));
		return buffer;
	}

	/**
	 * 用户信息日志
	 * 
	 * TLOG_USERINFO("tlog_userinfo", new String[] { "areaId", "openId",
	 * "userId", "userName", "userLv", "accId", "pf", "via", "userCreateDttm",
	 * "dttm" }),
	 * 
	 * @param user
	 * @param missionType
	 * @param missionId
	 * @param missionName
	 * @param flag
	 * @return
	 */
	public static AbsLogLineBuffer buildUserInfoLog(User user, long accId,
			String pf, String via, Timestamp userCreateDttm) {
		AbsLogLineBuffer buffer = getHead(user,
				TLogTable.TLOG_USERINFO.getName());
		buffer.append(accId).append(pf).append(via).append(userCreateDttm)
				.append(new Timestamp(System.currentTimeMillis()));
		return buffer;
	}

	/**
	 * 登录登出
	 *
	 * TLOG_LOGIN("tlog_login", new String[] { "areaId", "openId", "userId",
	 * "userName", "userLv", "logType", "accId", "pf", "via", "loginIp",
	 * "onlineTime", "userCreateDttm", "dttm" }),
	 * 
	 * @param user
	 * @param logType
	 * @param accId
	 * @param pf
	 * @param via
	 * @param loginIp
	 * @param onlineTime
	 * @param userCreateDttm
	 * @return
	 */
	public static AbsLogLineBuffer buildLoginLog(User user, String logType,
			long accId, String pf, String via, String loginIp,
			Timestamp onlineTime, Timestamp userCreateDttm) {
		AbsLogLineBuffer buffer = getHead(user, TLogTable.TLOG_LOGIN.getName());
		buffer.append(logType).append(accId).append(pf).append(via)
				.append(loginIp).append(onlineTime).append(userCreateDttm)
				.append(new Timestamp(System.currentTimeMillis()));
		return buffer;
	}

	/**
	 * 关卡日志
	 * 
	 * @param user
	 * @param logType
	 * @param scene
	 * @param difficulty
	 * @param win
	 * @param battleId
	 * @param via
	 * @return
	 */
	public static AbsLogLineBuffer buildRiskLog(User user, String logType,
			RiskScene scene, byte difficulty, boolean win, int battleId,
			String via) {
		AbsLogLineBuffer buffer = getHead(user, TLogTable.TLOG_RISK.getName());
		buffer.append(logType).append(scene.getSceneId())
				.append(scene.getName()).append(difficulty).append(win ? 1 : 0)
				.append(via).append(user.getCreateDate())
				.append(scene.getParentId())
				.append(new Timestamp(System.currentTimeMillis()));
		return buffer;
	}

	/**
	 * 冒险奖励
	 * 
	 * @param user
	 * @param pId
	 * @param idx
	 * @param via
	 * @return
	 */
	public static AbsLogLineBuffer buildRiskAwardLog(User user, int pId,
			int idx, String via) {
		AbsLogLineBuffer buffer = getHead(user,
				TLogTable.TLOG_RISK_AWARD.getName());
		buffer.append(pId).append(idx + 1).append(via)
				.append(user.getCreateDate())
				.append(new Timestamp(System.currentTimeMillis()));
		return buffer;
	}

	/**
	 * 装备强化
	 * 
	 * @param user
	 * @param treId
	 * @param itemId
	 * @param consumeItemId1
	 * @param num1
	 * @param consumeItemId2
	 * @param num2
	 * @param consumeMoney
	 * @return
	 */
	public static AbsLogLineBuffer buildEquipLvUpLog(User user, long treId,
			int itemId, int consumeItemId1, int num1, int consumeItemId2,
			int num2, int consumeMoney) {
		AbsLogLineBuffer buffer = getHead(user,
				TLogTable.TLOG_EQUIP_LVUP.getName());
		buffer.append(treId).append(itemId).append(consumeItemId1).append(num1)
				.append(consumeItemId2).append(num2).append(consumeMoney)
				.append(new Timestamp(System.currentTimeMillis()));
		return buffer;
	}

	/**
	 * 装备销毁日志
	 * 
	 * @param user
	 * @param treId
	 * @param itemId
	 * @param equipLevel
	 * @param returnItemId1
	 * @param num1
	 * @param returnItemId2
	 * @param num2
	 * @param returnMoney
	 * @return
	 */
	public static AbsLogLineBuffer buildEquipDestroyLog(User user, long treId,
			int itemId, int equipLevel, int returnItemId1, int num1,
			int returnItemId2, int num2, int returnMoney) {
		AbsLogLineBuffer buffer = getHead(user,
				TLogTable.TLOG_EQUIP_DESTROY.getName());
		buffer.append(treId).append(itemId).append(equipLevel)
				.append(returnItemId1).append(num1).append(returnItemId2)
				.append(num2).append(returnMoney)
				.append(new Timestamp(System.currentTimeMillis()));
		return buffer;
	}

	/**
	 * 抽武将日志
	 * 
	 * @param user
	 * @param mode
	 * @param sysHero
	 * @param num
	 * @param free
	 * @return
	 */
	public static AbsLogLineBuffer buildHeroGetLog(User user, int mode,
			SysHero sysHero, int num, boolean free) {
		AbsLogLineBuffer buffer = getHead(user,
				TLogTable.TLOG_HERO_GET.getName());
		buffer.append(mode).append(sysHero.getEntId())
				.append(sysHero.getEvaluate()).append(num)
				.append(free ? "1" : "2").append(user.getCreateDate())
				.append(new Timestamp(System.currentTimeMillis()));
		return buffer;
	}

	/**
	 * 武将强化日志
	 * 
	 * @param user
	 * @param hero
	 * @param oldGrowing
	 * @param currGrowing
	 * @param via
	 * @return
	 */
	public static AbsLogLineBuffer buildHeroStrengthLog(User user, Hero hero,
			int oldGrowing, int currGrowing, String via) {
		AbsLogLineBuffer buffer = getHead(user,
				TLogTable.TLOG_HERO_STRENGTH.getName());
		buffer.append(hero.getHeroId()).append(hero.getLevel())
				.append(hero.getSysHeroId()).append(oldGrowing)
				.append(currGrowing).append(via).append(user.getCreateDate())
				.append(new Timestamp(System.currentTimeMillis()));
		return buffer;
	}

	/**
	 * 武将进阶日志
	 * 
	 * @param user
	 * @param hero
	 * @param relifeNum
	 * @param via
	 * @return
	 */
	public static AbsLogLineBuffer buildHeroRelifeLog(User user, Hero hero,
			int relifeNum, String via) {
		AbsLogLineBuffer buffer = getHead(user,
				TLogTable.TLOG_HERO_RELIFE.getName());
		buffer.append(hero.getHeroId()).append(hero.getLevel())
				.append(hero.getSysHeroId()).append(relifeNum).append(via)
				.append(user.getCreateDate())
				.append(new Timestamp(System.currentTimeMillis()));
		return buffer;
	}

	/**
	 * 武将重生日志
	 * 
	 * @param user
	 * @param hero
	 * @param oldLv
	 * @param oldExp
	 * @param oldGrowing
	 * @param oldRelifeNum
	 * @param totalExp
	 * @param cardNum
	 * @param via
	 * @param retire
	 * @return
	 */
	public static AbsLogLineBuffer buildHeroRebirthLog(User user, Hero hero,
			int oldLv, int oldExp, int oldGrowing, int oldRelifeNum,
			int totalExp, int cardNum, String via, boolean retire) {
		AbsLogLineBuffer buffer = getHead(user,
				TLogTable.TLOG_HERO_REBIRTH.getName());
		buffer.append(hero.getHeroId()).append(oldLv)
				.append(hero.getSysHeroId()).append(oldExp).append(oldGrowing)
				.append(oldRelifeNum).append(totalExp).append(cardNum)
				.append(via).append(user.getCreateDate()).append(retire)
				.append(new Timestamp(System.currentTimeMillis()));
		return buffer;
	}

	/**
	 * 邮件日志
	 * 
	 * @param user
	 * @param mail
	 * @return
	 */
    public static AbsLogLineBuffer buildMailLog(User user, MailMessage mail){
        AbsLogLineBuffer buffer = getHead(user, TLogTable.TLOG_MESSAGE.getName());
        buffer.append(mail.getTitle())
                .append(mail.getEntityId0()).append(mail.getItemNum0())
                .append(mail.getEntityId1()).append(mail.getItemNum1())
                .append(mail.getEntityId2()).append(mail.getItemNum2())
                .append(mail.getEntityId3()).append(mail.getItemNum3())
                .append(mail.getEntityId4()).append(mail.getItemNum4())
                .append(mail.getEntityId5()).append(mail.getItemNum5())
                .append(mail.getSendDttm());
        return buffer;
    }

	/**
	 * 君主成就 TLOG_USER_ACHIEVE("tlog_achieve", new String[] { "areaId", "openId",
	 * "userId", "userName", "userLv", "achieveId", "achieveName", "addJungong",
	 * "dttm" }),
	 * 
	 * @param user
	 * @param achieveId
	 * @param achieveName
	 * @param addJungong
	 * @return
	 */
	public static AbsLogLineBuffer buildAchieveLog(User user, int achieveId,
			String achieveName, int addJungong) {
		AbsLogLineBuffer buffer = getHead(user,
				TLogTable.TLOG_USER_ACHIEVE.getName());
		buffer.append(achieveId).append(achieveName).append(addJungong)
				.append(new Timestamp(System.currentTimeMillis()));
		return buffer;
	}

	/**
	 *
	 * 君主军衔
	 *
	 * TLOG_USER_TITLE("tlog_title", new String[] { "areaId", "openId",
	 * "userId", "userName", "userLv", "titleId", "awardId", "consumeJunGong",
	 * "awardType", "awardDetail", "upTitleId", "dttm" }),
	 * 
	 * @param user
	 * @param titleId
	 * @param awardId
	 * @param consumeJunGong
	 * @param awardType
	 * @param awardDetail
	 * @param upTitleId
	 * @return
	 */
	public static AbsLogLineBuffer buildTitleLog(User user, int titleId,
			int awardId, int consumeJunGong, String awardType,
			String awardDetail, int upTitleId) {
		AbsLogLineBuffer buffer = getHead(user,
				TLogTable.TLOG_USER_TITLE.getName());
		buffer.append(titleId).append(awardId).append(consumeJunGong)
				.append(awardType).append(awardDetail).append(upTitleId)
				.append(new Timestamp(System.currentTimeMillis()));
		return buffer;
	}

    /**
     * 重楼
     * @param user
     * @param stageId
     * @param win
     * @param battleId
     * @return
     */
 	public static AbsLogLineBuffer buildTowerLog(User user, int stageId, boolean win, String battleId) {
		AbsLogLineBuffer buffer = getHead(user, TLogTable.TLOG_RISK.getName());
		buffer.append(stageId).append(win).append(battleId)
				.append(new Timestamp(System.currentTimeMillis()));
		return buffer;
	}
}
