package com.youxigu.dynasty2.armyout.domain;

import java.io.Serializable;

/**
 * 策略
 * 
 * @author Dagangzi
 *
 */
public class Strategy implements Serializable {
	// 出征
	public static final int COMMAND_201 = 201;// 侦查
	public static final int COMMAND_202 = 202;// pvp出征
	public static final int COMMAND_203 = 203;// 驻守
	public static final int COMMAND_204 = 204;// 集结
	public static final int COMMAND_205 = 205;// 返回
	public static final int COMMAND_206 = 206;// 遣返
	public static final int COMMAND_207 = 207;// 召回
	public static final int COMMAND_208 = 208;// pve出征
	public static final int COMMAND_209 = 209;// evp出征
	public static final int COMMAND_210 = 210;// 派遣

	private int id;
	private String name;
	private int consumeActPoint;// 消耗行动力
	private int consumeTimes;// 消耗pvp次数
	private int maxTimes;// 使用次数上限
	private int resEntId;// 消耗资源entId
	private int resNumFactor;// 资源数量公式系数
	private int entId2;
	private int entNum2;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getConsumeActPoint() {
		return consumeActPoint;
	}

	public void setConsumeActPoint(int consumeActPoint) {
		this.consumeActPoint = consumeActPoint;
	}

	public int getConsumeTimes() {
		return consumeTimes;
	}

	public void setConsumeTimes(int consumeTimes) {
		this.consumeTimes = consumeTimes;
	}

	public int getMaxTimes() {
		return maxTimes;
	}

	public void setMaxTimes(int maxTimes) {
		this.maxTimes = maxTimes;
	}

	public int getResEntId() {
		return resEntId;
	}

	public void setResEntId(int resEntId) {
		this.resEntId = resEntId;
	}

	public int getResNumFactor() {
		return resNumFactor;
	}

	public void setResNumFactor(int resNumFactor) {
		this.resNumFactor = resNumFactor;
	}

	public int getEntId2() {
		return entId2;
	}

	public void setEntId2(int entId2) {
		this.entId2 = entId2;
	}

	public int getEntNum2() {
		return entNum2;
	}

	public void setEntNum2(int entNum2) {
		this.entNum2 = entNum2;
	}

}
