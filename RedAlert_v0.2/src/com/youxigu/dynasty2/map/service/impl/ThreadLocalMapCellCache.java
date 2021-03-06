package com.youxigu.dynasty2.map.service.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.manu.core.ServiceLocator;
import com.youxigu.dynasty2.armyout.domain.Armyout;
import com.youxigu.dynasty2.armyout.service.IArmyoutService;
import com.youxigu.dynasty2.map.domain.MapCell;
import com.youxigu.dynasty2.map.service.IMapService;
import com.youxigu.dynasty2.util.BaseException;

/**
 * 坐标点threadlocal缓存
 * 
 * @author LK
 * @date 2016年2月18日
 */
public class ThreadLocalMapCellCache {
    private static final Logger log = LoggerFactory.getLogger(ThreadLocalMapCellCache.class);

	/**
	 * 插入缓存
	 */
	public static final int INSERT = 1;

	/**
	 * 更新缓存
	 */
	public static final int UPDATE = 2;

	/**
	 * 删除缓存
	 */
	public static final int DELETE = 4;


	private static ThreadLocal<Boolean> inTrans = new ThreadLocal<Boolean>() {
		protected Boolean initialValue() {
			return Boolean.FALSE;
		}

	};

	// 数据对象缓存
	private static ThreadLocal<Map<String, PrepareMapData>> messages = new ThreadLocal<Map<String, PrepareMapData>>() {
		protected Map<String, PrepareMapData> initialValue() {
			return new HashMap<String, PrepareMapData>();
		}
	};

	// 锁缓存
	private static ThreadLocal<List<MapCell>> lockedMapCells = new ThreadLocal<List<MapCell>>() {
		// protected List<MapCell> initialValue() {
		// return new ArrayList<MapCell>();
		// }
	};

	public static void clean() {
		messages.remove();
		inTrans.set(false);
        lockedMapCells.remove();
	}

	/**
	 * 更新mapcell or armyout 缓存
	 * 
	 * @return
	 */
	public static List<Object> commit() {
		inTrans.set(false);
		List<Object> dbupdates = new LinkedList<Object>();
		Map<String, PrepareMapData> map = messages.get();
		if (map != null && map.size() > 0) {
			Iterator<Map.Entry<String, PrepareMapData>> itl = map.entrySet()
					.iterator();
			while (itl.hasNext()) {
				Map.Entry<String, PrepareMapData> ent = itl.next();
				PrepareMapData data = map.remove(ent.getValue().getKey());
				if (data != null) {
					commitOne(data, dbupdates);
				}
			}
		}
		if (dbupdates.size() > 0) {
			return dbupdates;
		} else {
			return null;
		}
	}

	/**
	 * 更新mapcell or armyout 缓存
	 * 
	 * @param data
	 * @param dbupdates
	 */
	private static void commitOne(PrepareMapData data,
			List<Object> dbupdates) {

		IArmyoutService armyoutService = (IArmyoutService) ServiceLocator
				.getSpringBean("armyoutService");

		IMapService mapService = (IMapService) ServiceLocator
				.getSpringBean("mapService");

		Object obj = data.getData();
		if (data.getType() == INSERT) {
			if (obj != null && obj instanceof Armyout) {
				armyoutService.addArmyoutCache((Armyout) obj);
			}
		} else if (data.getType() == UPDATE) {
			// replace
			if (obj != null) {
				if (obj instanceof Armyout) {
					Armyout armyout = (Armyout) obj;
					Armyout armyoutCache = armyoutService
							.getArmyoutCache(armyout.getId());
					if (armyoutCache != null) {
						armyoutCache.copyFrom(armyout);
					}

					// 更新缓存
					armyoutService.addArmyoutCache(armyoutCache);
				} else if (obj instanceof MapCell) {
					MapCell mapCell = (MapCell) obj;
					MapCell cellCache = mapService.getMapCellCache(mapCell.getId());
					if (cellCache != null) {
						cellCache.copyFrom(mapCell);
					}
				}
				dbupdates.add(obj);
			}
		} else if (data.getType() == DELETE) {
			if (obj != null && obj instanceof Armyout) {
				armyoutService.removeArmyoutCache(((Armyout) obj));
			}
		}

	}

	public static Map<String, PrepareMapData> getData() {
		return messages.get();
	}

	public static PrepareMapData getData(String key) {
		Map<String, PrepareMapData> map = messages.get();
		return map.get(key);
	}

	public static Object getObject(String key) {
		Map<String, PrepareMapData> map = messages.get();
		PrepareMapData data = map.get(key);
		if (data == null)
			return null;
		else
			return data.getData();
	}

	public static void addData(int type, String key, Object data) {
		addData(new PrepareMapData(type, key, data));
	}

	public static void addData(PrepareMapData data) {
		if (!ThreadLocalMapCellCache.isInTrans()) {
			throw new BaseException("只能在事务中操作");
		}
		Map<String, PrepareMapData> map = messages.get();
		if (map == null) {
			map = new HashMap<String, PrepareMapData>();
			messages.set(map);
		}

		// 这里可能不安全：
		String key = data.getKey();
		PrepareMapData old = map.get(key);
		if (old != null) {
			if (old.getType() != data.getType()) {
				throw new RuntimeException("同一事务内不支持对同一个对象的多次不同类型的db操作");
			} else if (data.getType() != UPDATE) {
				throw new RuntimeException("同一事务内仅支持对同一个对象的多次update操作");
			}
		}
		map.put(key, data);
	}

	public static List<MapCell> getLockedMapCells() {
		return lockedMapCells.get();
	}

	public static void addLockedMapCell(MapCell cell) {
		List<MapCell> cells = lockedMapCells.get();
		if (cells == null) {
			cells = new LinkedList<MapCell>();
			lockedMapCells.set(cells);
		}
		cells.add(cell);
        if (log.isDebugEnabled()) {
            log.debug("add MapCell({},{}) to ThreadLocalMapCellCache", cell.getPosX(), cell.getPosY());
        }
    }

	public static void setInTrans(boolean trans) {
		inTrans.set(trans);
	}

	public static boolean isInTrans() {
		return inTrans.get();
	}

	public static class PrepareMapData {
		private int type;
		private String key;
		private Object data;

		public PrepareMapData() {
		}

		public PrepareMapData(int type, String key, Object data) {
			this.type = type;
			this.key = key;
			this.data = data;
		}

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public Object getData() {
			return data;
		}

		public void setData(Object data) {
			this.data = data;
		}
	}
}
