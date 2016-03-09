package com.youxigu.dynasty2.map.service;

import com.youxigu.dynasty2.combat.domain.CombatTeam;
import com.youxigu.dynasty2.hero.domain.SerialTroop;
import com.youxigu.dynasty2.map.domain.*;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public interface IResCasService {
    void lockResCas(int mapCellId);

    UserResCas lockAndGetUserResCas(int mapCellId);

    GuildResCas lockAndGetGuildResCasByMapCellId(int mapCellId);

    CountryResCas lockAndGetCountryResCasByMapCellId(int mapCellId);

    /**
     * 占领个人资源点。创建或修改该资源点UserResCas相关信息
     * @param userId 占领该坐标资源点的用户
     * @param mapCellId 要占领的坐标点
     */
    void doOccupyUserResCas(long userId, int mapCellId);

    /**
     * 占领联盟资源点
     *
     */
    void doOccupyGuildResCas(long guildId, int mapCellId);

    /**
     * 占领国家资源点
     */
    void doOccupyCountryResCas(long guildId, int mapCellId);

    /**
     *  刷新指定区的动态个人资源点和野怪NPC
     * @param stateId
     */
    void refreshDynResCas(int stateId) throws InterruptedException;

    /**
     * 清理当前未被占领且未被出征的动态个人资源点
     * @param stateId
     */
    void cleanDynResCas(int stateId);

    /**
     * 重置动态资源点为空。重置前会检查资源点状态是否满足定时清空的条件
     * @param resCas
     * @param throwException
     */
    void doResetIdleDynResCas(DynResCas resCas, boolean throwException);

    /**
     * 删除动态资源点，并重置MapCell的相关属性
     * @param mapCellId
     * @throws InterruptedException
     */
    void deleteDynResCas(int mapCellId) throws InterruptedException;

    /**
     * 根据策划配数，刷新个人资源点和野怪NPC
     * @param stateId
     */
    void newDynResCas(int stateId);

    /**
     * 随机创建一个个人资源点
     * @param resCasDefines 从中随机选取一个个人资源点定义
     * @param stateId
     * @param random
     */
    void doCreateRandomResCas(List<ResCas> resCasDefines, int stateId, Random random);

    /**
     * 随机创建一个野怪NPC
     * @param npcCasDefines 从中随机选取一个野怪NPC定义
     * @param stateId
     * @param random
     */
    void doCreateRandomNpcCas(List<ResCas> npcCasDefines, int stateId, Random random);

    /**
     * 判断联盟资源城市或国家资源城市是否开放可进攻
     * @param mapCellId
     * @return
     */
    boolean isGuildOrCountryResCasOpen(int mapCellId);

    /**
     * 给个人资源点设置镜像军团
     * @param sourceTroop 镜像军团
     * @param mapCellId
     * @param combatTeam
     * @param time 镜像生效时长
     * @param timeUnit 镜像时长的单位
     */
    void doSetMirrorCombatTeam(int mapCellId, CombatTeam combatTeam, int time, TimeUnit timeUnit);

    /**
     * 获取指定个人资源点的镜像驻守军团。如果未设置镜像驻守军团或镜像驻守军团已超期，则返回null
     * @param mapCellId
     * @return
     */
    CombatTeam doGetMirrorCombatTeam(int mapCellId);
}
