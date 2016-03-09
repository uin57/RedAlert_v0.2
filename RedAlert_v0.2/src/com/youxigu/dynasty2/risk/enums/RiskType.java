package com.youxigu.dynasty2.risk.enums;

import java.util.List;

import com.youxigu.dynasty2.util.IndexEnum;

/**
 * 关卡类型 普通，精英
 * 
 * @author fengfeng
 *
 */
public enum RiskType implements IndexEnum {
	DEFAULT(0, "default"), //
	NORMAL(1, "普通关卡"), //
	ELITE(2, "精英关卡"), //
	;
	private int type;
	private String desc;

	private RiskType(int type, String desc) {
		this.type = type;
		this.desc = desc;
	}

	public String getDesc() {
		return desc;
	}

	static List<RiskType> result = IndexEnumUtil.toIndexes(RiskType.values());

	/**
	 * 是否为普通关卡
	 * 
	 * @return
	 */
	public boolean isNormal() {
		return RiskType.NORMAL.equals(this);
	}

	/**
	 * 判断是否为精英关卡
	 * 
	 * @return
	 */
	public boolean isElite() {
		return RiskType.ELITE.equals(this);
	}

	@Override
	public int getIndex() {
		return type;
	}

	public static RiskType valueOf(int type) {
		return result.get(type);
	}

}
