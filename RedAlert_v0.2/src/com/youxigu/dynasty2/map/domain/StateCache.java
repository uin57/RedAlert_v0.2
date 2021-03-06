package com.youxigu.dynasty2.map.domain;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.manu.core.ServiceLocator;
import com.youxigu.dynasty2.map.service.IMapService;

/**
 * 区缓存
 * 
 * @author LK
 * @date 2016年2月4日
 */
public class StateCache implements Serializable {
	private State state;// 区定义
	private Map<Integer, MapCell> cells;// 区下的所有点

	// 用于清空数据
    @Deprecated
	private Map<Integer, MapCell> resCells;// 动态个人资源点
    @Deprecated
	private Map<Integer, MapCell> npcCells;// npc点
	// private Map<Integer, MapCell> idleCells;// 空闲坐标点
    private Integer[] allKeys;//所有坐标点的键值集合，为了提高随机取坐标的性能
    /**标记下一个区块是否开放*/
    private boolean nextOpen;

	public StateCache() {
	}

	public StateCache(State state) {
		this.state = state;
		this.cells = new ConcurrentHashMap<Integer, MapCell>();// 区下的所有点
		this.resCells = new ConcurrentHashMap<Integer, MapCell>();// 资源点
		this.npcCells = new ConcurrentHashMap<Integer, MapCell>();// npc点
		// this.idleCells = new ConcurrentHashMap<Integer, MapCell>();
	}

	public int getStateId() {
		return state.getStateId();
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public Map<Integer, MapCell> getCells() {
		return cells;
	}

	public void setCells(Map<Integer, MapCell> cells) {
		this.cells = cells;
	}

	public Map<Integer, MapCell> getResCells() {
		return resCells;
	}

	public void setResCells(Map<Integer, MapCell> resCells) {
		this.resCells = resCells;
	}

	public Map<Integer, MapCell> getNpcCells() {
		return npcCells;
	}

	public void setNpcCells(Map<Integer, MapCell> npcCells) {
		this.npcCells = npcCells;
	}

	/**
	 * 初始化点的缓存
	 * 
	 * @param mapcell
	 */
	public void addCell(MapCell mapcell) {
		int id = mapcell.getId();
		cells.put(id, mapcell);
		int castType = mapcell.getCastType();
		if (castType == MapCell.CAS_TYPE_NPC) {
			npcCells.put(id, mapcell);
		} else if (castType == MapCell.CAS_TYPE_RES) {
			resCells.put(id, mapcell);
		} /*
			 * else if (castType == MapCell.CAS_TYPE_EMPTY) { idleCells.put(id,
			 * mapcell); }
			 */
	}

	public int getCellsNum() {
		return cells.size();
	}

	// public Map<Integer, MapCell> getIdleCells() {
	// return idleCells;
	// }
	//
	// public void setIdleCells(Map<Integer, MapCell> idleCells) {
	// this.idleCells = idleCells;
	// }

    public Integer[] getAllKeys() {
        return allKeys;
    }

    public void setAllKeys(Integer[] allKeys) {
        this.allKeys = allKeys;
    }
    
    /**
     * 设置区块是开放的，只有指定资源点被占领后才开放
     */
    public void open(){
    	this.nextOpen = true;
    }
    
    /**
     * 判断当前区域是否开
     * @return
     */
    public boolean isOpen(){
    	State st = state.getParentState();
    	if(st==null){//新手区 一定开启
    		return true;
    	}
    	
    	IMapService mapService = (IMapService)ServiceLocator.getSpringBean("mapService");
    	StateCache stc = mapService.getStateCacheByStateId(st.getStateId());
    	return stc.isNextOpen();
    }
    
    /**
     * 判断下一个区域是否开启
     * @return
     */
    public boolean isNextOpen(){
    	return this.nextOpen;
    }
}
