package com.youxigu.dynasty2.armyout.domain;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.youxigu.dynasty2.armyout.domain.action.ArmyOutAction;

/**
 * 出征数据
 * 
 * @author LK
 * @date 2016年2月24日
 */
public class Armyout implements Serializable {
	public static final short STATUS_FORWARD = 1;// 出征
	public static final short STATUS_BACK = 2;// 返回
	public static final short STATUS_STAY = 3;// 驻守

	public static final short PLAER_TYPE = 1;// 玩家
	public static final short ENVIRONMENT_TYPE = 2;// 怪

	private long id;
	private int outType;// 战斗类型 =Strategy.id
	private int attackerCellId;// 攻击方坐标
	private long attackerUserId;// 攻击方userId
	private long attackerCasId;// 攻击方casId
	private long attackerGuildId;// 攻击方所属联盟Id-这个id，可能在出征到达前发生变化
	private short attackerType;// 攻击方类型
	private int defenderCellId;// 防守方坐标

	// 这几个字段目前看起来没啥用
	private long defenderUserId;// 防守方userId
	private long defenderCasId;// 防守方casId
	private long defenderGuildId;// 出征方所属联盟Id
	private short defenderType;// 防守方类型

	private long troopId;

	// 没啥用的属性
	private int atkPower;// 出征战斗力
	private int defPower;// 防守战斗力

	private int baseTime;// 单程时间,秒
	private Timestamp outDttm; // 出征时刻
	private Timestamp outArriveDttm; // 出征抵达的时间
	private Timestamp outBackDttm; // 出征回来的时间
	private short status;// 1:出征, 2=返回 3=防守（协防）
	private String combatId;// 战斗ID,战斗后回填

	public transient ArmyOutAction parent;// 所属的行为

	public Armyout() {
	}

	/**
	 * 侦查
	 * 
	 * @param outType
	 * @param attackerCellId
	 * @param attackerUserId
	 * @param attackerCasId
	 * @param attackerGuildId
	 * @param attackerType
	 * @param defenderCellId
	 * @param defenderType
	 * @param baseTime
	 * @param outDttm
	 * @param outArriveDttm
	 * @param outBackDttm
	 * @param status
	 */
	public Armyout(int outType, int attackerCellId, long attackerUserId,
			long attackerCasId, long attackerGuildId, short attackerType,
			int defenderCellId, short defenderType, int baseTime,
			Timestamp outDttm, Timestamp outArriveDttm, Timestamp outBackDttm,
			short status) {
		this(outType, attackerCellId, attackerUserId, attackerCasId,
				attackerGuildId, attackerType, defenderCellId, 0, 0, 0,
				defenderType, 0, baseTime,
				outDttm, outArriveDttm, outBackDttm, status);
	}

	/**
	 * 出征
	 * 
	 * @param outType
	 * @param attackerCellId
	 * @param attackerUserId
	 * @param attackerCasId
	 * @param attackerGuildId
	 * @param attackerType
	 * @param defenderCellId
	 * @param defenderUserId
	 * @param defenderCasId
	 * @param defenderGuildId
	 * @param defenderType
	 * @param troopId
	 * @param baseTime
	 * @param outDttm
	 * @param outArriveDttm
	 * @param outBackDttm
	 * @param status
	 */
	public Armyout(int outType, int attackerCellId, long attackerUserId,
			long attackerCasId, long attackerGuildId, short attackerType,
			int defenderCellId, long defenderUserId, long defenderCasId,
			long defenderGuildId, short defenderType, long troopId,
			int baseTime, Timestamp outDttm, Timestamp outArriveDttm,
			Timestamp outBackDttm, short status) {
		this();
		this.outType = outType;
		this.attackerCellId = attackerCellId;
		this.attackerUserId = attackerUserId;
		this.attackerCasId = attackerCasId;
		this.attackerGuildId = attackerGuildId;
		this.attackerType = attackerType;
		this.defenderCellId = defenderCellId;
		this.defenderUserId = defenderUserId;
		this.defenderCasId = defenderCasId;
		this.defenderGuildId = defenderGuildId;
		this.defenderType = defenderType;
		this.troopId = troopId;
		this.baseTime = baseTime;
		this.outDttm = outDttm;
		this.outArriveDttm = outArriveDttm;
		this.outBackDttm = outBackDttm;
		this.status = status;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getOutType() {
		return outType;
	}

	public void setOutType(int outType) {
		this.outType = outType;
	}

	/**
	 * 这个点与出征的方向无关，为攻击方的坐标，不能当fromCellId用
	 * 
	 * @return
	 */
	public int getAttackerCellId() {
		return attackerCellId;
	}

	public void setAttackerCellId(int attackerCellId) {
		this.attackerCellId = attackerCellId;
	}

	public long getAttackerUserId() {
		return attackerUserId;
	}

	public void setAttackerUserId(long attackerUserId) {
		this.attackerUserId = attackerUserId;
	}

	public long getAttackerCasId() {
		return attackerCasId;
	}

	public void setAttackerCasId(long attackerCasId) {
		this.attackerCasId = attackerCasId;
	}

	public long getAttackerGuildId() {
		return attackerGuildId;
	}

	public void setAttackerGuildId(long attackerGuildId) {
		this.attackerGuildId = attackerGuildId;
	}

	public short getAttackerType() {
		return attackerType;
	}

	public void setAttackerType(short attackerType) {
		this.attackerType = attackerType;
	}

	/**
	 * 这个点与出征的方向无关，为防守方的坐标，不能当toCellId用
	 * 
	 * @return
	 */
	public int getDefenderCellId() {
		return defenderCellId;
	}

	public void setDefenderCellId(int defenderCellId) {
		this.defenderCellId = defenderCellId;
	}

	public long getDefenderUserId() {
		return defenderUserId;
	}

	public void setDefenderUserId(long defenderUserId) {
		this.defenderUserId = defenderUserId;
	}

	public long getDefenderCasId() {
		return defenderCasId;
	}

	public void setDefenderCasId(long defenderCasId) {
		this.defenderCasId = defenderCasId;
	}

	public long getDefenderGuildId() {
		return defenderGuildId;
	}

	public void setDefenderGuildId(long defenderGuildId) {
		this.defenderGuildId = defenderGuildId;
	}

	public short getDefenderType() {
		return defenderType;
	}

	public void setDefenderType(short defenderType) {
		this.defenderType = defenderType;
	}

	public long getTroopId() {
		return troopId;
	}

	public void setTroopId(long troopId) {
		this.troopId = troopId;
	}

	public int getAtkPower() {
		return atkPower;
	}

	public void setAtkPower(int atkPower) {
		this.atkPower = atkPower;
	}

	public int getDefPower() {
		return defPower;
	}

	public void setDefPower(int defPower) {
		this.defPower = defPower;
	}

	public int getBaseTime() {
		return baseTime;
	}

	public void setBaseTime(int baseTime) {
		this.baseTime = baseTime;
	}

	public Timestamp getOutDttm() {
		return outDttm;
	}

	public void setOutDttm(Timestamp outDttm) {
		this.outDttm = outDttm;
	}

	public Timestamp getOutArriveDttm() {
		return outArriveDttm;
	}

	public void setOutArriveDttm(Timestamp outArriveDttm) {
		this.outArriveDttm = outArriveDttm;
	}

	public Timestamp getOutBackDttm() {
		return outBackDttm;
	}

	public void setOutBackDttm(Timestamp outBackDttm) {
		this.outBackDttm = outBackDttm;
	}

	public short getStatus() {
		return status;
	}

	public void setStatus(short status) {
		this.status = status;
	}

	public String getCombatId() {
		return combatId;
	}

	public void setCombatId(String combatId) {
		this.combatId = combatId;
	}

	public String getKey() {
		return "Armyout_" + id;
	}

	public static String getCacheKey(long id) {
		return "Armyout_" + id;
	}

	/**
	 * 取得出发地
	 * 
	 * @return
	 */
	public int getFromCellId() {
		int cellId = 0;
		switch (status) {
		case STATUS_FORWARD:
			cellId = this.attackerCellId;
			break;
		case STATUS_BACK:
			cellId = this.defenderCellId;
			break;
		case STATUS_STAY:
			cellId = this.defenderCellId;
			break;
		default:
			break;
		}
		return cellId;
	}

	/**
	 * 取得目的地
	 * 
	 * @return
	 */
	public int getToCellId() {
		int cellId = 0;
		switch (status) {
		case STATUS_FORWARD:
			cellId = this.defenderCellId;
			break;
		case STATUS_BACK:
			cellId = this.attackerCellId;
			break;
		case STATUS_STAY:
			cellId = this.attackerCellId;
			break;
		default:
			break;
		}
		return cellId;
	}

	/**
	 * 更新缓存数据
	 * 
	 * @param tmpMapCell
	 * @return
	 */
	public Armyout copyFrom(Armyout tmpArmyout) {
		this.outType = tmpArmyout.getOutType();// 战斗类型
		this.attackerCellId = tmpArmyout.getAttackerCellId();// 出征方坐标
		this.attackerUserId = tmpArmyout.getAttackerUserId();
		this.attackerCasId = tmpArmyout.getAttackerCasId();
		this.attackerGuildId = tmpArmyout.getAttackerGuildId();// 出征方所属联盟Id
		this.attackerType = tmpArmyout.getAttackerType();// 进攻方类型
		this.defenderCellId = tmpArmyout.getDefenderCellId();// 防守方坐标
		this.defenderUserId = tmpArmyout.getDefenderUserId();
		this.defenderCasId = tmpArmyout.getDefenderCasId();
		this.defenderGuildId = tmpArmyout.getDefenderGuildId();// 出征方所属联盟Id
		this.defenderType = tmpArmyout.getDefenderType();// 防守方类型
		this.troopId = tmpArmyout.getTroopId();
		this.atkPower = tmpArmyout.getAtkPower();// 出征战斗力
		this.defPower = tmpArmyout.getDefPower();// 防守战斗力
		this.baseTime = tmpArmyout.getBaseTime();// 单程时间,秒
		this.outDttm = tmpArmyout.getOutDttm(); // 出征时刻
		this.outArriveDttm = tmpArmyout.getOutArriveDttm(); // 出征抵达的时间
		this.outBackDttm = tmpArmyout.getOutBackDttm(); // 出征回来的时间
		this.status = tmpArmyout.getStatus();// 0: 正常 ,1=防守（协防） 2=战斗
		this.combatId = tmpArmyout.getCombatId();// 战斗ID,战斗后回填
		this.parent = tmpArmyout.parent;
		return this;
	}

	public Armyout clone() {
		Armyout tmpArmyout = new Armyout();
		tmpArmyout.setId(this.id);
		tmpArmyout.setOutType(this.outType);// 战斗类型
		tmpArmyout.setAttackerCellId(this.attackerCellId);// 出征方坐标
		tmpArmyout.setAttackerUserId(this.attackerUserId);
		tmpArmyout.setAttackerCasId(this.attackerCasId);
		tmpArmyout.setAttackerGuildId(this.attackerGuildId);// 出征方所属联盟Id
		tmpArmyout.setAttackerType(this.attackerType);// 进攻方类型
		tmpArmyout.setDefenderCellId(this.defenderCellId);// 防守方坐标
		tmpArmyout.setDefenderUserId(this.defenderUserId);
		tmpArmyout.setDefenderCasId(this.defenderCasId);
		tmpArmyout.setDefenderGuildId(this.defenderGuildId);// 出征方所属联盟Id
		tmpArmyout.setDefenderType(this.defenderType);// 防守方类型
		tmpArmyout.setTroopId(this.troopId);
		tmpArmyout.setAtkPower(this.atkPower);// 出征战斗力
		tmpArmyout.setDefPower(this.defPower);// 防守战斗力
		tmpArmyout.setBaseTime(this.baseTime);// 单程时间,秒
		tmpArmyout.setOutDttm(this.outDttm); // 出征时刻
		tmpArmyout.setOutArriveDttm(this.outArriveDttm); // 出征抵达的时间
		tmpArmyout.setOutBackDttm(this.outBackDttm); // 出征回来的时间
		tmpArmyout.setStatus(this.status);// 0: 正常 ,1=防守（协防） 2=战斗
		tmpArmyout.setCombatId(this.combatId);// 战斗ID,战斗后回填
		tmpArmyout.parent = this.parent;
		return tmpArmyout;
	}

	/**
	 * 取得坐标集合
	 * 
	 * @return
	 */
	public List<Integer> getCellIds() {
		List<Integer> cellIds = new ArrayList<Integer>(2);
		cellIds.add(this.getFromCellId());
		cellIds.add(this.getToCellId());
		return cellIds;
	}
}
