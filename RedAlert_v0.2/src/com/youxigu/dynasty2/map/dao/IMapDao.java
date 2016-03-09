package com.youxigu.dynasty2.map.dao;

import java.util.List;

import com.youxigu.dynasty2.map.domain.Country;
import com.youxigu.dynasty2.map.domain.CountryCharacter;
import com.youxigu.dynasty2.map.domain.MapCell;
import com.youxigu.dynasty2.map.domain.NPCAttackConf;
import com.youxigu.dynasty2.map.domain.OpenState;
import com.youxigu.dynasty2.map.domain.State;

/*
 * 地图数据处理DAO接口
 */
public interface IMapDao {
	// /**
	// * 按主键获取地图坐标位置
	// *
	// * @param id
	// * @return
	// */
	// public MapCell getMapCellById(int id);

	// /**
	// * 创建建城点，工具方法，业务不调用
	// * @param mapCell
	// */
	// void createMapCell(MapCell mapCell);
	/**
	 * 根据指定的郡城ID获取所属的全部建城点
	 * 
	 * @param stateId
	 * @return
	 */
	List<MapCell> getMapCellsByStateId(int stateId);

	// /**
	// * 随机选择某个郡的count个未分配的建城点
	// *
	// * @param stateId
	// * @param count
	// * @return
	// */
	// List<MapCell> getAvialbleMapCells(int stateId, int count);
	//
	// /**
	// * 根据指定的ID集合获取建城点
	// *
	// * @param ids
	// * @return
	// */
	// List<MapCell> getMapCellsByIds(List<Integer> ids);
	//
	// /**
	// * 获取每个郡得城池总数
	// *
	// * @return
	// */
	// List<Map<String, Object>> getCastleNums();

	/**
	 * 更新MapCell信息
	 * 
	 * @param mapCell
	 */
	void updateMapCell(MapCell mapCell);

	/**
	 * 获取所有的国家
	 * 
	 * @return
	 */
	List<Country> getCountrys();

	// /**
	// * 获取国家
	// *
	// * @param stateId
	// * @return
	// */
	// Country getCountryById(int countryId);

	/**
	 * 获取国家的郡列表
	 * 
	 * @param countryId
	 * @return
	 */
	List<State> getStatesByCountryId(int countryId);

	// /**
	// * 获取郡
	// *
	// * @param stateId
	// * @return
	// */
	// State getStateById(int stateId);
	//
	// /**
	// * 更新郡的状态
	// *
	// * @param state
	// */
	// void updateState(State state);
	
	
	/**
	 * 获取所有的国家的角色
	 * 
	 * @return
	 */
	List<CountryCharacter> getCountryCharacters();

	/**
	 * 统计每个国家的人数
	 * 
	 * @param countryId
	 * @return
	 */
	int statisticsAllUsers(int countryId);
	
	
	/**
	 * 记录开放的区块信息
	 * @param st
	 */
	void insertOpenState(OpenState st);
	
	/**
	 * 获取开放区块的信息
	 * @return
	 */
	List<OpenState> getAllOpenStates();
	/**
	 * 获取开放区块的信息
	 * @return
	 */
	List<OpenState> getAllOpenStatesById(int stateId);

	/**
	 * 获得大地图怪物配置
	 * 
	 * @return
	 */
	List<NPCAttackConf> getNPCAttackConfs();
}
