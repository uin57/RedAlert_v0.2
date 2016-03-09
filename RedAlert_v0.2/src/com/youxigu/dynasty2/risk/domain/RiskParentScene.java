package com.youxigu.dynasty2.risk.domain;

import java.util.List;

import com.youxigu.dynasty2.risk.enums.RiskType;
import com.youxigu.dynasty2.util.BaseException;

/**
 * 冒险大场景
 * 
 * @author Administrator
 *
 */
public class RiskParentScene implements java.io.Serializable {
	private static final long serialVersionUID = -1670721474050974028L;

	private int id;

	private String name;

	private String pic;

	/** 关卡类型 @see RiskType */
	private int types;
	/** * 解锁需要的君主等级 */
	private int reqUserLv;
	/*** 解锁需要通关的前一个场景的Id */
	private int prevSceneId;

	/*** 星数奖励1 */
	private short star1;
	private int award1;

	/*** 星数奖励2 */
	private short star2;
	private int award2;

	/*** 星数奖励3 */
	private short star3;
	private int award3;

	/** 所属的章节id */
	private int parentsceneid;

	/*** 包含的小关卡列表 */
	private transient List<RiskScene> scenes;

	/*** 解锁需要的前置通关场景 */
	private transient RiskParentScene reqRiskParentScene;

	private transient RiskType riskType = null;
	private transient Risk risk = null;

	public RiskParentScene() {
		super();
	}

	public void init(Risk risk) {
		riskType = RiskType.valueOf(types);
		if (riskType == null) {
			throw new BaseException("关卡类型错误" + types);
		}
		this.risk = risk;
	}

	public Risk getRisk() {
		return risk;
	}

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

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	public int getReqUserLv() {
		return reqUserLv;
	}

	public void setReqUserLv(int reqUserLv) {
		this.reqUserLv = reqUserLv;
	}

	public int getPrevSceneId() {
		return prevSceneId;
	}

	public void setPrevSceneId(int prevSceneId) {
		this.prevSceneId = prevSceneId;
	}

	public short getStar1() {
		return star1;
	}

	public void setStar1(short star1) {
		this.star1 = star1;
	}

	public int getAward1() {
		return award1;
	}

	public void setAward1(int award1) {
		this.award1 = award1;
	}

	public short getStar2() {
		return star2;
	}

	public void setStar2(short star2) {
		this.star2 = star2;
	}

	public int getAward2() {
		return award2;
	}

	public void setAward2(int award2) {
		this.award2 = award2;
	}

	public short getStar3() {
		return star3;
	}

	public void setStar3(short star3) {
		this.star3 = star3;
	}

	public int getAward3() {
		return award3;
	}

	public void setAward3(int award3) {
		this.award3 = award3;
	}

	public List<RiskScene> getScenes() {
		return scenes;
	}

	public void setScenes(List<RiskScene> scenes) {
		this.scenes = scenes;
	}

	public RiskParentScene getReqRiskParentScene() {
		return reqRiskParentScene;
	}

	public void setReqRiskParentScene(RiskParentScene reqRiskParentScene) {
		this.reqRiskParentScene = reqRiskParentScene;
	}

	public int getTypes() {
		return types;
	}

	public void setTypes(int types) {
		this.types = types;
	}

	public RiskType getRiskType() {
		return riskType;
	}

	public int getParentsceneid() {
		return parentsceneid;
	}

	public void setParentsceneid(int parentsceneid) {
		this.parentsceneid = parentsceneid;
	}

}
