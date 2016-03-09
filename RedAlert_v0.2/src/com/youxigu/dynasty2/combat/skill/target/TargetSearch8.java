package com.youxigu.dynasty2.combat.skill.target;

import java.util.ArrayList;
import java.util.List;

import com.youxigu.dynasty2.combat.domain.CanotAttackedCombatUnit;
import com.youxigu.dynasty2.combat.domain.CombatUnit;
import com.youxigu.dynasty2.combat.domain.WallCombatUnit;
import com.youxigu.dynasty2.combat.skill.CombatSkillEffect;
import com.youxigu.dynasty2.combat.skill.ITargetSearcher;

/**
 * 8：己方全体,不包括城墙，城防 ；
 * 
 * @author Administrator
 * 
 */
public class TargetSearch8 implements ITargetSearcher {

	@Override
	public List<CombatUnit> searchTarget(CombatSkillEffect skillEffect, CombatUnit source, CombatUnit target) {
		List<CombatUnit> targets = new ArrayList<CombatUnit>(6);
		List<CombatUnit> all = source.getParent().getUnits();
		for (CombatUnit unit : all) {
			if (!unit.dead()) {
				if (!(unit instanceof CanotAttackedCombatUnit) && !(unit instanceof WallCombatUnit)) {
					targets.add(unit);
				}
			}
		}
		return targets;
	}
}
