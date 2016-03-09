package com.youxigu.dynasty2.entity.domain.effect;

import java.io.Serializable;

/**
 * 实体效果类型 table：EffectType
 * 
 * @author Administrator
 * 
 */
public class EffectTypeDefine implements Serializable {
	/**
	 * ************************************************
	 * **********战斗中的武将效果 格式H_XXXXX****************
	 * ************************************************
	 */

	// 玩家武将全属性
	public static final String H_ALL = "H_ALL";

	// 法术攻击
	public static final String H_MAGICATTACK = "H_MAGICATTACK";

	// 物理攻击
	public static final String H_PHYSICALATTACK = "H_PHYSICALATTACK";
	
	// 纯粹攻击-不区分攻击类型
	public static final String H_ATTACK = "H_ATTACK";

	// 法术防御
	public static final String H_MAGICDEFEND = "H_MAGICDEFEND";

	// 物理防御
	public static final String H_PHYSICALDEFEND = "H_PHYSICALDEFEND";

	// 命中
	public static final String H_Hit = "H_Hit";

	// 闪避
	public static final String H_DODGE = "H_DODGE";

	// 免爆率
	public static final String H_CRITDEC = "H_CRITDEC";

	// 暴击率
	public static final String H_CRITADD = "H_CRITADD";

	// 暴击伤害
	public static final String H_CRITDAMAGE = "H_CRITDAMAGE";

	// 暴击伤害减免
	public static final String H_CRITDAMAGE_DEC = "H_CRITDAMAGE_DEC";

	// 伤害千分比加成
	public static final String H_DAMAGE_PER = "H_DAMAGE_PER";

	// 伤害千分比减免
	public static final String H_SHIELD_PER = "H_SHIELD_PER";

	// 血量上限
	public static final String H_INIT_HP = "H_INIT_HP";

	// 兵种克制系数千分比
	public static final String H_ARMY_BITE_ROIT = "H_ARMY_BITE_ROIT";

	// 固定伤害abs
	public static final String H_DAMAGE = "H_DAMAGE";

	// 固定伤害减免abs
	public static final String H_SHIELD = "H_SHIELD";

	// 士气
	public static final String H_MORALE = "H_MORALE";

	
	/**
	 * ************************************************
	 * **********npc效果加成 格式N_XXX********
	 * ************************************************
	 */

	// NPC武将全效果加成
	public static final String N_ALL = "N_ALL";

	// npc法术攻击
	public static final String N_MAGICATTACK = "N_MAGICATTACK";

	// npc物理攻击
	public static final String N_PHYSICALATTACK = "N_PHYSICALATTACK";

	// // npc纯粹攻击-不区分攻击类型
	// @Deprecated
	// public static final String N_ATTACK = "N_ATTACK";

	// npc法术防御
	public static final String N_MAGICDEFEND = "N_MAGICDEFEND";

	// npc 物理防御
	public static final String N_PHYSICALDEFEND = "N_PHYSICALDEFEND";

	// npc命中
	public static final String N_Hit = "N_Hit";

	// npc闪避
	public static final String N_DODGE = "N_DODGE";

	// npc免爆率
	public static final String N_CRITDEC = "N_CRITDEC";

	// npc暴击率
	public static final String N_CRITADD = "N_CRITADD";

	// npc暴击伤害
	public static final String N_CRITDAMAGE = "N_CRITDAMAGE";

	// npc暴击伤害减免
	public static final String N_CRITDAMAGE_DEC = "N_CRITDAMAGE_DEC";

	// npc血量上限
	public static final String N_INIT_HP = "N_INIT_HP";

	/**
	 * ******************************************
	 * ********城池效果-建筑效果 格式B_XXXXX***********
	 * ******************************************
	 */
	public static final String B_BUILD_QUEUE_SPEED = "B_BUILD_SPEED";//建筑队列速度效果
	public static final String B_NEWARMY_LIMIT = "B_SOLDIER_MAX";//生产零件的上限
    public static final String B_TECH_QUEUE_SPEED = "B_TECH_SPEED";//科技升级加速效果

	//约束，不能收取，其他获得方式可以
	public static final String B_GOLD_CAP = "B_GOLD_CAP";// 金矿数存储上限,主基地效果
	public static final String B_IRON_CAP = "B_IRON_CAP"; // 铁矿数存储上限,主基地效果
	public static final String B_OIL_CAP = "B_OIL_CAP"; // 油矿数存储上限,主基地效果
	public static final String B_URANIUM_CAP = "B_URANIUM_CAP"; // 铀矿存储上限,主基地效果
	
	//打仗被掠夺上限
	public static final String B_GOLDROB_LIMIT = "B_GOLDROB_LIMIT";// 金矿掠夺上限,仓库效果
	public static final String B_IRONROB_LIMIT = "B_IRONROB_LIMIT"; // 铁矿掠夺上限,仓库效果
	public static final String B_OILROB_LIMIT = "B_OILROB_LIMIT"; // 油矿掠夺上限,仓库效果
	public static final String B_URANIUMROB_LIMIT = "B_URANIUMROB_LIMIT"; // 铀矿掠夺上限,仓库效果

	//资源建筑生产上限
	public static final String B_GOLD_LIMIT = "B_GOLD_LIMIT"; // 金矿场上限
	public static final String B_IRON_LIMIT = "B_IRON_LIMIT"; // 铁矿场上限
	public static final String B_OIL_LIMIT = "B_OIL_LIMIT"; // 油矿场上限
	public static final String B_URANIUM_LIMIT = "B_URANIUM_LIMIT"; // 铀矿场上限

	//资源建筑每15分钟的产量
	public static final String B_GOLD_YIELD = "B_GOLD_YIELD"; // 金矿每15分钟产量
	public static final String B_IRON_YIELD = "B_IRON_YIELD"; // 铁矿每15分钟产量
	public static final String B_OIL_YIELD = "B_OIL_YIELD"; // 油矿每15分钟产量
	public static final String B_URANIUM_YIELD = "B_URANIUM_YIELD"; // 铀矿每15分钟产量
	
	//科技
	public static final String B_GOLD_SKILL = "B_GOLD_SKILL"; // 生产金矿技巧
	public static final String B_IRON_SKILL = "B_IRON_SKILL";// 生产铁矿技巧
	public static final String B_OIL_SKILL = "B_OIL_SKILL"; // 生产油矿技巧
	public static final String B_URANIUM_SKILL = "B_URANIUM_SKILL"; // 生产铀矿技巧
	
	/**
	 * *************************************************
	 * **************道具效果 格式I_XXXXX*******************
	 * *************************************************
	 */
	public static final String I_HERO_EXP = "I_HERO_EXP";//武将经验卡效果
	public static final String I_GOLD = "I_GOLD";//加金矿
	public static final String I_IRON = "I_IRON";//加铁矿 
	public static final String I_OIL = "I_OIL";//加油矿
	public static final String I_URANIUM = "I_URANIUM";//加铀矿
    public static final String I_CASH = "I_CASH";//加元宝
    
    public static final String EFFECT_CASTLE_ICON = "EFFECT_CASTLE_ICON";//城池外观效果类型

	private String effTypeId;

	private String effTypeName;

	private String effTypedesc;

	private int showFlag;// 是否显示 - 1：显示；0：不显示

	private int viewOrder;// 装备效果显示顺序

	// private String serviceName;// 该类效果执行使用的serviceName

	public String getEffTypeId() {
		return effTypeId;
	}

	public void setEffTypeId(String effTypeId) {
		this.effTypeId = effTypeId;
	}

	public String getEffTypeName() {
		return effTypeName;
	}

	public void setEffTypeName(String effTypeName) {
		this.effTypeName = effTypeName;
	}

	public String getEffTypedesc() {
		return effTypedesc;
	}

	public void setEffTypedesc(String effTypedesc) {
		this.effTypedesc = effTypedesc;
	}

	public int getShowFlag() {
		return showFlag;
	}

	public void setShowFlag(int showFlag) {
		this.showFlag = showFlag;
	}

	public int getViewOrder() {
		return viewOrder;
	}

	public void setViewOrder(int viewOrder) {
		this.viewOrder = viewOrder;
	}

}
