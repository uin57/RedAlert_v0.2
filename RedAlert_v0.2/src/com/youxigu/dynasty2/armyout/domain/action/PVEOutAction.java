package com.youxigu.dynasty2.armyout.domain.action;

import com.youxigu.dynasty2.armyout.domain.Armyout;
import com.youxigu.dynasty2.armyout.domain.Strategy;
import com.youxigu.dynasty2.map.domain.MapCell;
import com.youxigu.dynasty2.user.domain.User;

/**
 * pve出征
 * 
 * @author LK
 * @date 2016年2月29日
 */
public class PVEOutAction extends ArmyOutAction {

	public PVEOutAction(MapCell fromCell, MapCell toCell, Armyout armyout,
			User fromUser, Strategy strategy,
			long time) {
		super(fromCell, toCell, armyout, fromUser, strategy, time);
	}
}
