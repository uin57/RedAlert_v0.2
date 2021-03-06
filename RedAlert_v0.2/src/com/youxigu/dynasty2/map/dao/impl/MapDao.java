package com.youxigu.dynasty2.map.dao.impl;

import java.util.List;

import com.manu.core.base.BaseDao;
import com.youxigu.dynasty2.map.dao.IMapDao;
import com.youxigu.dynasty2.map.domain.Country;
import com.youxigu.dynasty2.map.domain.CountryCharacter;
import com.youxigu.dynasty2.map.domain.MapCell;
import com.youxigu.dynasty2.map.domain.NPCAttackConf;
import com.youxigu.dynasty2.map.domain.OpenState;
import com.youxigu.dynasty2.map.domain.State;
@SuppressWarnings("unchecked")
public class MapDao extends BaseDao implements IMapDao {
	// @Override
	// public MapCell getMapCellById(int id) {
	// return (MapCell) this.getSqlMapClientTemplate()
	// .queryForObject("getMapCellById", id);
	// }

	@Override
	public List<MapCell> getMapCellsByStateId(int stateId) {
		return this.getSqlMapClientTemplate()
				.queryForList("getMapCellsByStateId", stateId);
	}

	// @Override
	// public List<MapCell> getAvialbleMapCells(int stateId, int count) {
	// Map<String, Object> params = new HashMap<String, Object>();
	// params.put("stateId", stateId);
	// params.put("count", count);
	// return this.getSqlMapClientTemplate()
	// .queryForList("getAvialbleMapCells", params);
	// }
	//
	// @Override
	// public List<MapCell> getMapCellsByIds(List<Integer> ids) {
	// if (ids == null || ids.size() == 0) {
	// return new ArrayList<MapCell>(1);
	// }
	// return this.getSqlMapClientTemplate().queryForList("getMapCellsByIds",
	// ids);
	// }
	//
	// @Override
	// public List<Map<String, Object>> getCastleNums() {
	// return this.getSqlMapClientTemplate().queryForList("getCastleNums");
	// }

	@Override
	public void updateMapCell(MapCell mapCell) {
		this.getSqlMapClientTemplate().update("updateMapCell", mapCell);
	}

	@Override
	public List<Country> getCountrys() {
		return this.getSqlMapClientTemplate().queryForList("listAllCountry");
	}

	// @Override
	// public Country getCountryById(int countryId) {
	// return (Country) this.getSqlMapClientTemplate()
	// .queryForObject("getCountryById", countryId);
	// }
	//
	// @Override
	// public State getStateById(int stateId) {
	// return (State) this.getSqlMapClientTemplate()
	// .queryForObject("getStateById", stateId);
	// }

	@Override
	public List<State> getStatesByCountryId(int countryId) {
		return this.getSqlMapClientTemplate()
				.queryForList("getStatesByCountryId", countryId);
	}
	//
	// @Override
	// public void updateState(State state) {
	// this.getSqlMapClientTemplate().update("updateState", state);
	//
	// }

	// @Override
	// public void createMapCell(MapCell mapCell) {
	// this.getSqlMapClientTemplate().insert("insertMapCell",mapCell);
	//
	// }

	@Override
	public List<CountryCharacter> getCountryCharacters() {
		return this.getSqlMapClientTemplate()
				.queryForList("listAllCountryCharacters");
	}

	@Override
	public int statisticsAllUsers(int countryId) {
		Object obj = this.getSqlMapClientTemplate()
				.queryForObject("statisticsAllUsers", countryId);
		if (obj == null) {
			return 0;
		}
		return Integer.class.cast(obj);
	}
	@Override
	public void insertOpenState(OpenState st) {
		this.getSqlMapClientTemplate().insert("insertOpenState",st);
	}
	
	
	@Override
	public List<OpenState> getAllOpenStates() {
		return this.getSqlMapClientTemplate().queryForList("getAllOpenStates");
	}
	
	@Override
	public List<OpenState> getAllOpenStatesById(int stateId) {
		return this.getSqlMapClientTemplate().queryForList("getAllOpenStatesById",stateId);
	}

	@Override
	public List<NPCAttackConf> getNPCAttackConfs() {
		return this.getSqlMapClientTemplate().queryForList("getNPCAttackConfs");
	}

}
