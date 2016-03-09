package com.youxigu.dynasty2.armyout.service;

import java.util.List;

import com.youxigu.dynasty2.armyout.domain.qingbao.BeAssembled;
import com.youxigu.dynasty2.armyout.domain.qingbao.BeAttacked;
import com.youxigu.dynasty2.armyout.domain.qingbao.BeDetected;
import com.youxigu.dynasty2.armyout.domain.qingbao.MilitarySituation;
import com.youxigu.dynasty2.armyout.domain.qingbao.MyDetect;

/**
 * 设置情报
 * */
public interface IMilitarySituationService {
	
	public List<MilitarySituation> getMilitarySituationList(long userId,int pageNum);
	public MilitarySituation getMilitarySituation(int id);
	public void markHasView(int id);
	/**
	 * 添加被侦查情报
	 * */
	public void doAddBeDetected(long userId , BeDetected beDetected);
	/**
	 * 添加被集结情报
	 * */
	public void doAddBeAssembled(long userId , BeAssembled beAssembled);
	/**
	 * 添加被进攻情报
	 * */
	public void doAddBeAttacked(long userId , BeAttacked beAttacked);
	/**
	 * 添加我的侦查情报
	 * */
	public void doAddMyDetect(long userId , MyDetect myDetect);
}
