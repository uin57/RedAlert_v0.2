package com.youxigu.dynasty2.map.domain;

import java.io.Serializable;

/**
 * 资源点定义（个人、联盟、国家、野怪NPC）
 */
public class ResCas implements Serializable {
    private static final long serialVersionUID = -5498575885792189265L;
    public static final int CAS_TYPE_PERSONAL = 7;
    public static final int CAS_TYPE_GUILD = 8;
    public static final int CAS_TYPE_COUNTRY = 9;
    public static final int CAS_TYPE_NPC = 10;

    private int id;
    private String name;
    private int length; //边长
    private int casType; //资源点类型（个人、联盟、国家、野怪NPC）
    private int casLevel;
    private int oilYield; //油矿产量/15分钟
    private int ironYield;
    private int uraniumYield;
    private int goldYield;
    private int refugeeNum; //难民数量
    private int garrisonLimit; //驻守军团数量上限
    private int npcConfId; //驻守npcId列表的第一个NpcAttackConfId
    private int timeSpan; //可占领的时长，单位秒。仅动态刷新的个人资源点使用
    private String prevResCasId; //前序资源点，逗号分隔，多个ResCas的Id
    private int occupyValue; //占领值，联盟和国家城市需要
    private int casHp; //资源点耐久值

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

    public int getCasType() {
        return casType;
    }

    public void setCasType(int casType) {
        this.casType = casType;
    }

    public int getCasLevel() {
        return casLevel;
    }

    public void setCasLevel(int casLevel) {
        this.casLevel = casLevel;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getOilYield() {
        return oilYield;
    }

    public void setOilYield(int oilYield) {
        this.oilYield = oilYield;
    }

    public int getIronYield() {
        return ironYield;
    }

    public void setIronYield(int ironYield) {
        this.ironYield = ironYield;
    }

    public int getUraniumYield() {
        return uraniumYield;
    }

    public void setUraniumYield(int uraniumYield) {
        this.uraniumYield = uraniumYield;
    }

    public int getGoldYield() {
        return goldYield;
    }

    public void setGoldYield(int goldYield) {
        this.goldYield = goldYield;
    }

    public int getRefugeeNum() {
        return refugeeNum;
    }

    public void setRefugeeNum(int refugeeNum) {
        this.refugeeNum = refugeeNum;
    }

    public int getGarrisonLimit() {
        return garrisonLimit;
    }

    public void setGarrisonLimit(int garrisonLimit) {
        this.garrisonLimit = garrisonLimit;
    }

    public int getTimeSpan() {
        return timeSpan;
    }

    public void setTimeSpan(int timeSpan) {
        this.timeSpan = timeSpan;
    }

    public String getPrevResCasId() {
        return prevResCasId;
    }

    public void setPrevResCasId(String prevResCasId) {
        this.prevResCasId = prevResCasId;
    }

    public int getOccupyValue() {
        return occupyValue;
    }

    public void setOccupyValue(int occupyValue) {
        this.occupyValue = occupyValue;
    }

    public int getCasHp() {
        return casHp;
    }

    public void setCasHp(int casHp) {
        this.casHp = casHp;
    }

    public int getNpcConfId() {
        return npcConfId;
    }

    public void setNpcConfId(int npcConfId) {
        this.npcConfId = npcConfId;
    }
}
