package com.youxigu.dynasty2.mission.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mission implements Serializable {
	private static final long serialVersionUID = 2966563460568861022L;
	public static final String MISSION_TYPE_MAIN = "QTYPE_MAIN"; // 主线任务
	public static final String MISSION_TYPE_WORLD = "QTYPE_WORLD"; // 特殊事件/世界任务
	
	public static final String AWARD_TYPE_ITEM = "item";
	
	/**
	 * 累计次数任务： entId=0/1
	 */
	public static final String QCT_TYPE_RECORD = "QCT_Record"; // 酒馆抽武将N次,累计值
	public static final String QCT_TYPE_REBRON = "QCT_Rebron"; //武将重生N次,累计值
	public static final String QCT_TYPE_USE="QCT_Use"; //使用道具,累计值
	public static final String QCT_TYPE_HEROPOWER="QCT_HeroPower"; // 强化武将N次,累计值
	public static final String QCT_TYPE_MOREBUILD = "QCT_MoreBuild";// 执行自动建造，累计值
	public static final String QCT_TYPE_EQUIPLVTIMES = "QCT_EquipLvTimes";// 强化装备N次，累计值
	public static final String QCT_TYPE_EQUIPBUILD = "QCT_EquipBuild";// 生产装备N次，累计值
	public static final String QCT_TYPE_RELIFEHERO = "QCT_RelifeHero";// 武将进阶N次，累计值
	public static final String QCT_TYPE_GAINGOLD = "QCT_GainGold";// 收取金矿N次，累计值
	public static final String QCT_TYPE_GAINIRON = "QCT_GainIron";// 收取铁矿N次，累计值
	public static final String QCT_TYPE_GAINOIL = "QCT_GainOil";// 收取油矿N次，累计值
	public static final String QCT_TYPE_GAINURANIUM = "QCT_GainUranium";// 收取铀矿N次，累计值

	/**
	 * 当前数量任务
	 */
	public static final String QCT_TYPE_RESOURCE = "QCT_Resource";// 囤积资源：资源entId/资源当前数量
	public static final String QCT_TYPE_Study = "QCT_Study";// 科技升级:科技entId/科技当前等级
	public static final String QCT_TYPE_COLLECTION = "QCT_Collection";// 道具任务:道具entId/当前数
	public static final String QCT_TYPE_UPLEVEL = "QCT_Uplevel";// 君主级别:0/君主当前等级
	public static final String QCT_TYPE_LEVEL = "QCT_Level";// 升级建筑 “建筑ID”到N级: 建筑entId/当前等级
	// 拥有N个Y等级的武将 missionLimit(entId:heroLv)：武将当前等级/0
	public static final String QCT_TYPE_HERONUMLEVEL = "QCT_HeroNumLevel";
	// 拥有N个Y品质的武将missionLimit(entId:color)：系统武将的color/0
	public static final String QCT_TYPE_HERONUMCOLOR = "QCT_HeroNumColor";
	// 主力军团位置上有N个武将：0/武将数量
	public static final String QCT_TYPE_MAINTROOPHERONUM = "QCT_MainTroopHeroNum";
    
	/**
	 * 最大数量任务
	 */
	public static final String QCT_TYPE_HEROLEVEL = "QCT_HeroLevel";// 任意武将升到N级： 0/当前等级
	public static final String QCT_TYPE_EQUIPLV = "QCT_equipLv";// 强化装备到N级,最大值： 0/当前等级
	public static final String QCT_TYPE_HEROEQUIPNUM = "QCT_heroEquipNum";// 任意武将身上装备达到N个  0/当前数
	
	
	public static final String QCT_TYPE_PASSONE = "QCT_PassOne";// 通关冒险某一关
    public static final String QCT_TYPE_TOWER_STAGE = "QCT_towerStage";//作战实验室通关记录


    private int missionId;// 任务id

	private String missionType;// 任务类型

	private String missionChildType;// 任务子类型

	private String missionName;// 任务名

	private String missionDesc;// 任务描述

	private String missionCompleteDesc1;// 任务完成条件描述

	private String missionCompleteDesc2;// 任务完成条件描述

	private String missionCompleteDesc3;// 任务完成条件描述

	private String missionCompleteDesc4;// 任务完成条件描述

	private int missionHardLevel;// 任务难度

	private int missionLevel;// 任务等级

	/**
	 * 完成次数限制,0 不限制
	 */
	private int limitCount;

	@Deprecated
	private String serviceDexName;// 玩家,城堡

	// //////////////////对应MissionLimit 的missioncompleteId
	private int missioncompleteId1;// 任务完成条件 1

	private int missioncompleteId2;

	private int missioncompleteId3;

	private int missioncompleteId4;

	private int missioncompleteId5;

	private String awardtype1;// 任务奖励类型
	private int awardNum1;// 任务奖励数量
	private int awardId1;// 任务奖励id

	private String awardtype2;// 任务奖励类型
	private int awardNum2;// 任务奖励数量
	private int awardId2;// 任务奖励id

	private String awardtype3;// 任务奖励类型
	private int awardNum3;// 任务奖励数量
	private int awardId3;// 任务奖励id

	private String awardtype4;// 任务奖励类型
	private int awardNum4;// 任务奖励数量
	private int awardId4;// 任务奖励id

	/**
	 * 父节点任务id 0:跟任务，创建帐号时自动产生
	 * 
	 * >0, 其他:任务i -2表示按君主等级生成的任务 -3表示冒险通关生成的任务 -1表示威望任务
	 */
	private int parentMissionId;// d

	// /发布任务的npcId
	private int issueMissionId;//

	private long timeLimit;// 持续时间（秒），超过持续时间，无法完成

	// private transient MissionLimit missionLimit1;
	// private transient MissionLimit missionLimit2;
	// private transient MissionLimit missionLimit3;
	// private transient MissionLimit missionLimit4;
	// private transient MissionLimit missionLimit5;
	/*
	 * 按MissionLimit.octType分类的MissionLimit Map集合 系统初始化时根据missioncompleteId1-5构造
	 */
	private transient Map<String, List<MissionLimit>> limitMaps = new HashMap<String, List<MissionLimit>>();
	/**
	 * MissionLimit索引：与missioncompleteId1-5的对应关系
	 */
	private transient Map<MissionLimit, Integer> limitIndexs = new HashMap<MissionLimit, Integer>();

	/**
	 * 奖励列表
	 */
	private transient List<MissionAward> missionAwards = new ArrayList<MissionAward>();
	private transient List<Mission> childMissions;
	private transient Mission parent;

	public List<Mission> getChildMissions() {
		return childMissions;
	}

	public void addChild(Mission mission) {
		if (childMissions == null) {
			childMissions = new ArrayList<Mission>();
		}
		childMissions.add(mission);
	}

	public Mission getParent() {
		return parent;
	}

	public void setParent(Mission parent) {
		this.parent = parent;
	}

	public int getMissionId() {
		return missionId;
	}

	public void setMissionId(int missionId) {
		this.missionId = missionId;
	}

	public String getMissionType() {
		return missionType;
	}

	public void setMissionType(String missionType) {
		this.missionType = missionType;
	}

	public int getMissionHardLevel() {
		return missionHardLevel;
	}

	public void setMissionHardLevel(int missionHardLevel) {
		this.missionHardLevel = missionHardLevel;
	}

	public int getMissionLevel() {
		return missionLevel;
	}

	public void setMissionLevel(int missionLevel) {
		this.missionLevel = missionLevel;
	}

	public int getParentMissionId() {
		return parentMissionId;
	}

	public void setParentMissionId(int parentMissionId) {
		this.parentMissionId = parentMissionId;
	}

	@Deprecated
	public String getServiceDexName() {
		return serviceDexName;
	}

	@Deprecated
	public void setServiceDexName(String serviceDexName) {
		this.serviceDexName = serviceDexName;
	}

	public int getLimitCount() {
		return limitCount;
	}

	public void setLimitCount(int limitCount) {
		this.limitCount = limitCount;
	}

	public int getMissioncompleteId1() {
		return missioncompleteId1;
	}

	public void setMissioncompleteId1(int missioncompleteId1) {
		this.missioncompleteId1 = missioncompleteId1;
	}

	public int getMissioncompleteId2() {
		return missioncompleteId2;
	}

	public void setMissioncompleteId2(int missioncompleteId2) {
		this.missioncompleteId2 = missioncompleteId2;
	}

	public int getMissioncompleteId3() {
		return missioncompleteId3;
	}

	public void setMissioncompleteId3(int missioncompleteId3) {
		this.missioncompleteId3 = missioncompleteId3;
	}

	public int getMissioncompleteId4() {
		return missioncompleteId4;
	}

	public void setMissioncompleteId4(int missioncompleteId4) {
		this.missioncompleteId4 = missioncompleteId4;
	}

	public int getMissioncompleteId5() {
		return missioncompleteId5;
	}

	public void setMissioncompleteId5(int missioncompleteId5) {
		this.missioncompleteId5 = missioncompleteId5;
	}

	public int getCompleteIdByIndex(int index) {
		if (index == 1) {
			return missioncompleteId1;
		} else if (index == 2) {
			return missioncompleteId2;
		} else if (index == 3) {
			return missioncompleteId3;
		} else if (index == 4) {
			return missioncompleteId4;
		} else {
			return missioncompleteId5;
		}
	}

	public String getMissionName() {
		return missionName;
	}

	public void setMissionName(String missionName) {
		this.missionName = missionName;
	}

	public String getMissionDesc() {
		return missionDesc;
	}

	public void setMissionDesc(String missionDesc) {
		this.missionDesc = missionDesc;
	}

	public String getMissionChildType() {
		return missionChildType;
	}

	public void setMissionChildType(String missionChildType) {
		this.missionChildType = missionChildType;
	}

	public String getMissionCompleteDesc1() {
		return missionCompleteDesc1;
	}

	public void setMissionCompleteDesc1(String missionCompleteDesc1) {
		this.missionCompleteDesc1 = missionCompleteDesc1;
	}

	public String getMissionCompleteDesc2() {
		return missionCompleteDesc2;
	}

	public void setMissionCompleteDesc2(String missionCompleteDesc2) {
		this.missionCompleteDesc2 = missionCompleteDesc2;
	}

	public String getMissionCompleteDesc3() {
		return missionCompleteDesc3;
	}

	public void setMissionCompleteDesc3(String missionCompleteDesc3) {
		this.missionCompleteDesc3 = missionCompleteDesc3;
	}

	public String getMissionCompleteDesc4() {
		return missionCompleteDesc4;
	}

	public void setMissionCompleteDesc4(String missionCompleteDesc4) {
		this.missionCompleteDesc4 = missionCompleteDesc4;
	}

	public String getAwardtype1() {
		return awardtype1;
	}

	public void setAwardtype1(String awardtype1) {
		this.awardtype1 = awardtype1;
	}

	public int getAwardNum1() {
		return awardNum1;
	}

	public void setAwardNum1(int awardNum1) {
		this.awardNum1 = awardNum1;
	}

	public int getAwardId1() {
		return awardId1;
	}

	public void setAwardId1(int awardId1) {
		this.awardId1 = awardId1;
	}

	public String getAwardtype2() {
		return awardtype2;
	}

	public void setAwardtype2(String awardtype2) {
		this.awardtype2 = awardtype2;
	}

	public int getAwardNum2() {
		return awardNum2;
	}

	public void setAwardNum2(int awardNum2) {
		this.awardNum2 = awardNum2;
	}

	public int getAwardId2() {
		return awardId2;
	}

	public void setAwardId2(int awardId2) {
		this.awardId2 = awardId2;
	}

	public String getAwardtype3() {
		return awardtype3;
	}

	public void setAwardtype3(String awardtype3) {
		this.awardtype3 = awardtype3;
	}

	public int getAwardNum3() {
		return awardNum3;
	}

	public void setAwardNum3(int awardNum3) {
		this.awardNum3 = awardNum3;
	}

	public int getAwardId3() {
		return awardId3;
	}

	public void setAwardId3(int awardId3) {
		this.awardId3 = awardId3;
	}

	public String getAwardtype4() {
		return awardtype4;
	}

	public void setAwardtype4(String awardtype4) {
		this.awardtype4 = awardtype4;
	}

	public int getAwardNum4() {
		return awardNum4;
	}

	public void setAwardNum4(int awardNum4) {
		this.awardNum4 = awardNum4;
	}

	public int getAwardId4() {
		return awardId4;
	}

	public void setAwardId4(int awardId4) {
		this.awardId4 = awardId4;
	}

	public void addLimit(MissionLimit limit, int index) {

		if (limit != null) {

			limitIndexs.put(limit, index);

			List<MissionLimit> limits = limitMaps.get(limit.getOctType());
			if (limits == null) {
				limits = new ArrayList<MissionLimit>();
				limitMaps.put(limit.getOctType(), limits);
			}
			limits.add(limit);
		}
	}

	public int getLimitIndex(MissionLimit limit) {
		return limitIndexs.get(limit);
	}

	public Map<String, List<MissionLimit>> getLimitMaps() {
		return limitMaps;
	}

	public Map<MissionLimit, Integer> getLimitIndexs() {
		return limitIndexs;
	}

	public boolean hasOctType(String octType) {
		return limitMaps.containsKey(octType);
	}

	public List<MissionLimit> getLimitsByOctType(String octType) {
		return limitMaps.get(octType);
	}

	public List<MissionAward> getMissionAwards() {
		return missionAwards;
	}

	public void addMissionAward(MissionAward missionAward) {
		missionAwards.add(missionAward);
	}

	public int getIssueMissionId() {
		return issueMissionId;
	}

	public void setIssueMissionId(int issueMissionId) {
		this.issueMissionId = issueMissionId;
	}

	public long getTimeLimit() {
		return timeLimit;
	}

	public void setTimeLimit(long timeLimit) {
		this.timeLimit = timeLimit;
	}

}
