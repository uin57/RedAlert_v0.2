package com.youxigu.dynasty2.map.domain;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 动态刷新出的个人资源点和NPC。刷新时创建，超时或再次刷新时如达到指定条件则删除
 */
public class DynResCas implements Serializable {
    private static final long serialVersionUID = -3889829244032729825L;
    private int mapCellId;
    private int stateId;
    private int resCasId;//ResCas资源点定义的id
    private long userId;
    private Timestamp occupyTime; //占领时间点
    private Timestamp expireTime; //到期时间点
    private int casType; //资源点类型（个人资源点、野怪NPC）
    private int casLevel; //资源点等级
    private int npcId; //驻守npcId
    private byte[] mirrorTroop; //镜像驻守军团
    private Timestamp mirrorSetTime; //镜像驻守起始时间
    private Timestamp mirrorExpireTime; //镜像驻守结束时间
    private int casHp; //资源点耐久值
    private Timestamp mianzhanExpireTime; //免战到期时间。未占领则为null

    public int getMapCellId() {
        return mapCellId;
    }

    public void setMapCellId(int mapCellId) {
        this.mapCellId = mapCellId;
    }

    public int getResCasId() {
        return resCasId;
    }

    public void setResCasId(int resCasId) {
        this.resCasId = resCasId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public Timestamp getOccupyTime() {
        return occupyTime;
    }

    public void setOccupyTime(Timestamp occupyTime) {
        this.occupyTime = occupyTime;
    }

    public Timestamp getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Timestamp expireTime) {
        this.expireTime = expireTime;
    }

    public int getCasLevel() {
        return casLevel;
    }

    public void setCasLevel(int casLevel) {
        this.casLevel = casLevel;
    }

    public int getCasType() {
        return casType;
    }

    public void setCasType(int casType) {
        this.casType = casType;
    }

    public int getNpcId() {
        return npcId;
    }

    public void setNpcId(int npcId) {
        this.npcId = npcId;
    }

    public byte[] getMirrorTroop() {
        return mirrorTroop;
    }

    public void setMirrorTroop(byte[] mirrorTroop) {
        this.mirrorTroop = mirrorTroop;
    }

    public Timestamp getMirrorSetTime() {
        return mirrorSetTime;
    }

    public void setMirrorSetTime(Timestamp mirrorSetTime) {
        this.mirrorSetTime = mirrorSetTime;
    }

    public Timestamp getMirrorExpireTime() {
        return mirrorExpireTime;
    }

    public void setMirrorExpireTime(Timestamp mirrorExpireTime) {
        this.mirrorExpireTime = mirrorExpireTime;
    }

    public int getCasHp() {
        return casHp;
    }

    public void setCasHp(int casHp) {
        this.casHp = casHp;
    }

    public Timestamp getMianzhanExpireTime() {
        return mianzhanExpireTime;
    }

    public void setMianzhanExpireTime(Timestamp mianzhanExpireTime) {
        this.mianzhanExpireTime = mianzhanExpireTime;
    }

    public int getStateId() {
        return stateId;
    }

    public void setStateId(int stateId) {
        this.stateId = stateId;
    }
}
