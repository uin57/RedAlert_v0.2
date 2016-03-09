package com.youxigu.dynasty2.map.domain.action;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * 时间行为
 * 
 * @author LK
 * @date 2016年2月5日
 */
public class TimeAction implements Delayed, java.io.Serializable {
	protected transient int cmd;
	protected transient long time;
	protected transient boolean inValid;// 是否无效

	public long _getTime() {
		return time;
	}

	public void _setTime(long time) {
		this.time = time;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public int getCmd() {
		return cmd;
	}

	public void setCmd(int cmd) {
		this.cmd = cmd;
	}

	public boolean isInValid() {
		return inValid;
	}

	public void setInValid(boolean inValid) {
		this.inValid = inValid;
	}

	@Override
	public long getDelay(TimeUnit unit) {
		// unit.convert(sourceDuration, sourceUnit)
		// 这里就简单写了， 忽略TimeUnit
		long now = System.currentTimeMillis();
		return time - now;
	}

	@Override
	public int compareTo(Delayed o) {
		long myTime = this.time;
		long oTime = ((TimeAction) o).time;
		if (myTime > oTime) {
			return 1;
		} else if (myTime == oTime) {
			return 0;
		} else {
			return -1;
		}
		// return (int) ((this.time - ((SceneAction) o).time)/1000);
	}

}
