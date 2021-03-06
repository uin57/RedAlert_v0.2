package com.youxigu.dynasty2.map.domain;

import com.youxigu.dynasty2.hero.domain.Troop;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

/**
 * 用户占领的个人资源点(不包含动态刷新的个人资源点)。占领时新增记录，丢弃或防守失败时删除
 */
public class UserResCas implements Serializable {
    private static final long serialVersionUID = -872659597340180110L;
    private int mapCellId;
    private int resCasId;
    private long userId;
    private Timestamp occupyTime; //占领时间点
    private Timestamp expireTime; //到期时间点
    private byte[] mirrorTroop; //镜像驻守军团
    private Timestamp mirrorSetTime; //镜像驻守起始时间
    private Timestamp mirrorExpireTime; //镜像驻守结束时间
    private int casHp; //资源点耐久值
    private Timestamp mianzhanExpireTime; //免战到期时间。未占领则为null

    private transient List<Troop> troops;

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

    public byte[] getMirrorTroop() {
        return mirrorTroop;
    }

    public void setMirrorTroop(byte[] mirrorTroop) {
        this.mirrorTroop = mirrorTroop;
    }

    public List<Troop> getTroops() {
        return troops;
    }

    public void setTroops(List<Troop> troops) {
        this.troops = troops;
    }

    public int getCasHp() {
        return casHp;
    }

    public void setCasHp(int casHp) {
        this.casHp = casHp;
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

    public Timestamp getMianzhanExpireTime() {
        return mianzhanExpireTime;
    }

    public void setMianzhanExpireTime(Timestamp mianzhanExpireTime) {
        this.mianzhanExpireTime = mianzhanExpireTime;
    }
//    private transient SerialTroop mirrorTroopObj;
}
