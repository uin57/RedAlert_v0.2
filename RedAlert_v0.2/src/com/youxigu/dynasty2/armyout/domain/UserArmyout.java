package com.youxigu.dynasty2.armyout.domain;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.youxigu.dynasty2.util.BaseException;
import com.youxigu.dynasty2.util.DateUtils;

/**
 * 用户出征队列
 * 
 * @author LK
 * @date 2016年2月29日
 */
public class UserArmyout implements Serializable {
	public static final int MAX_QUEUESIZE = 3;// 出征队列最大的size
	private long userId;
	private long outId1;
	private long outId2;
	private long outId3;
	private int spyNum;// 侦查次数
	private int outNum;// 出征次数
	private int stayNum;// 驻守次数
	private int togatherNum;// 集结次数
	private int pvpNum;// pvp次数
	private Timestamp lastDttm;// 最后一次出征的时间
	private Timestamp lastDeadDttm;// 上一次流亡的时间
	private Timestamp lastMoveDttm;// 上一次迁城的时间

	public UserArmyout() {
	}

	public UserArmyout(long userId) {
		this.userId = userId;
	}

	/**
	 * 隔天重置
	 */
	public void dayPassReset() {
		if (this.lastDttm != null && !DateUtils.isSameDay(this.lastDttm,
				DateUtils.nowTimestamp())) {
			this.spyNum = 0;
			this.outNum = 0;
			this.stayNum = 0;
			this.togatherNum = 0;
			this.pvpNum = 0;
			this.lastDttm = null;
		}
	}

	/**
	 * 取取得出征队列size
	 * 
	 * @return
	 */
	public int getQueueSize() {
		int size = 0;
		if (outId1 > 0) {
			size = size + 1;
		}
		if (outId2 > 0) {
			size = size + 1;
		}
		if (outId3 > 0) {
			size = size + 1;
		}
		return size;
	}

	/**
	 * 队列是否已满
	 * 
	 * @return
	 */
	public boolean isQueueFull() {
		return this.getQueueSize() >= UserArmyout.MAX_QUEUESIZE;
	}

	/**
	 * 设置出征id
	 * 
	 * @param armyoutId
	 */
	public void setArmyout(long armyoutId) {
		if (this.isQueueFull()) {
			throw new BaseException("同时只能有三条出征");
		}
		if (this.outId1 <= 0) {
			this.outId1 = armyoutId;
		} else if (this.outId2 <= 0) {
			this.outId2 = armyoutId;
		} else if (this.outId3 <= 0) {
			this.outId3 = armyoutId;
		}
	}

	/**
	 * 删除出征
	 * 
	 * @param armyoutId
	 */
	public void removeArmyout(long armyoutId) {
		if (this.outId1 == armyoutId) {
			this.outId1 = 0;
		} else if (this.outId2 == armyoutId) {
			this.outId2 = 0;
		} else if (this.outId3 == armyoutId) {
			this.outId3 = 0;
		}
	}

	/**
	 * 删除所有出征
	 */
	public void removeAllArmyout() {
		this.outId1 = 0;
		this.outId2 = 0;
		this.outId3 = 0;
	}

	/**
	 * 取得出征列表
	 * 
	 * @return
	 */
	public List<Long> getArmyout() {
		List<Long> list = new ArrayList<Long>();
		if (outId1 > 0) {
			list.add(outId1);
		}
		if (outId2 > 0) {
			list.add(outId2);
		}
		if (outId3 > 0) {
			list.add(outId3);
		}
		return list;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getOutId1() {
		return outId1;
	}

	public void setOutId1(long outId1) {
		this.outId1 = outId1;
	}

	public long getOutId2() {
		return outId2;
	}

	public void setOutId2(long outId2) {
		this.outId2 = outId2;
	}

	public long getOutId3() {
		return outId3;
	}

	public void setOutId3(long outId3) {
		this.outId3 = outId3;
	}

	public int getSpyNum() {
		return spyNum;
	}

	public void setSpyNum(int spyNum) {
		this.spyNum = spyNum;
	}

	public int getOutNum() {
		return outNum;
	}

	public void setOutNum(int outNum) {
		this.outNum = outNum;
	}

	public int getStayNum() {
		return stayNum;
	}

	public void setStayNum(int stayNum) {
		this.stayNum = stayNum;
	}

	public int getTogatherNum() {
		return togatherNum;
	}

	public void setTogatherNum(int togatherNum) {
		this.togatherNum = togatherNum;
	}

	public int getPvpNum() {
		return pvpNum;
	}

	public void setPvpNum(int pvpNum) {
		this.pvpNum = pvpNum;
	}

	public Timestamp getLastDttm() {
		return lastDttm;
	}

	public void setLastDttm(Timestamp lastDttm) {
		this.lastDttm = lastDttm;
	}

	public Timestamp getLastDeadDttm() {
		return lastDeadDttm;
	}

	public void setLastDeadDttm(Timestamp lastDeadDttm) {
		this.lastDeadDttm = lastDeadDttm;
	}

	public Timestamp getLastMoveDttm() {
		return lastMoveDttm;
	}

	public void setLastMoveDttm(Timestamp lastMoveDttm) {
		this.lastMoveDttm = lastMoveDttm;
	}

	/**
	 * 按出征类型取次数
	 * 
	 * @param outType
	 * @return
	 */
	public int getNumByOutType(int outType) {
		if (outType == Strategy.COMMAND_201) {
			return this.spyNum;
		} else if (outType == Strategy.COMMAND_202) {
			return this.outNum;
		} else if (outType == Strategy.COMMAND_203) {
			return this.stayNum;
		} else if (outType == Strategy.COMMAND_204) {
			return this.togatherNum;
		} else {
			throw new BaseException("出征类型不存在");
		}
	}

	/**
	 * 设置出征次数
	 * 
	 * @param outType
	 */
	public void setNumByOutType(int outType) {
		if (outType == Strategy.COMMAND_201) {
			this.spyNum = this.spyNum + 1;
		} else if (outType == Strategy.COMMAND_202) {
			this.outNum = this.outNum + 1;
		} else if (outType == Strategy.COMMAND_203) {
			this.stayNum = this.stayNum + 1;
		} else if (outType == Strategy.COMMAND_204) {
			this.togatherNum = this.togatherNum + 1;
		} else {
			throw new BaseException("出征类型不存在");
		}
	}
}
