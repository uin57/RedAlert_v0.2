package com.youxigu.dynasty2.map.service.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

import com.youxigu.dynasty2.armyout.dao.IArmyoutDao;
import com.youxigu.dynasty2.armyout.domain.Armyout;
import com.youxigu.dynasty2.map.dao.IMapDao;
import com.youxigu.dynasty2.map.domain.MapCell;
import com.youxigu.dynasty2.map.service.IAsyUpdateMapService;
import com.youxigu.wolf.net.NodeSessionMgr;

/**
 * 异步更新map数据
 * 
 * @author LK
 * @date 2016年2月17日
 */
public class AsyUpdateMapService
		implements IAsyUpdateMapService, ApplicationListener {
	public static final Logger log = LoggerFactory
			.getLogger(AsyUpdateMapService.class);

	private AsyUpdateWorker asyUpdateWorker = null;
	private static final String WORK_UPDATE_PREFIX = "MAP_ASYUPDATE_THREAD";// 线程名
	private static final String MAPCELL_KEY = "MapCell_";
	private static final String ARMYOUT_KEY = "Armyout_";
	private int period = 120000;// 毫秒，更新数据库线程的启动时间间隔

	private IMapDao mapDao;
	private IArmyoutDao armyoutDao;

	public void setMapDao(IMapDao mapDao) {
		this.mapDao = mapDao;
	}

	public void setArmyoutDao(IArmyoutDao armyoutDao) {
		this.armyoutDao = armyoutDao;
	}

	public void setPeriod(int period) {
		this.period = period;
	}

	@Override
	public void initService() {
		// 只在mainserver上加载
		String str = System.getProperty(NodeSessionMgr.SERVER_TYPE_KEY);
		if (str == null) {
			return;
		}
		int serverType = Integer.parseInt(str);
		if (serverType != NodeSessionMgr.SERVER_TYPE_MAIN) {
			return;
		}

		// 默认启动异步更新线程
		asyUpdateWorker = new AsyUpdateWorker(WORK_UPDATE_PREFIX);
		asyUpdateWorker.start();
		log.info("启动地图异步更新线程{}", asyUpdateWorker.getName());

		// /通过spring 来停止，防止 mysql 先停止了
		/**
		 * 检查是否有 没处理完的请求
		 */
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				shutdown();
			}
		}));
	}

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		if (event instanceof ContextClosedEvent) {
			shutdown();
		}
	}

	public void shutdown() {
		if (asyUpdateWorker == null || !asyUpdateWorker.isStart()) {
			return;
		}
		log.info("map asyupdate{}线程,开始检查退出条件了！", asyUpdateWorker.getName());
		int remainSize = 0;

		do {
			// 检查主线程是否执行完毕
			remainSize = asyUpdateWorker.size();
			if (remainSize > 0) {
				asyUpdateWorker.setStart(true);
				asyUpdateWorker.weakup();
			}
			if (remainSize > 0) {
				log.info(asyUpdateWorker.getName() + "剩余sql数量：" + remainSize);
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} while (remainSize > 0);

		asyUpdateWorker.setStart(false);
		asyUpdateWorker.weakup();

		log.info("map asyupdate{}线程,退出条件达成！", asyUpdateWorker.getName());
	}

	/**
	 * 处理db update的线程
	 * 
	 * @author LK
	 * @date 2016年2月17日
	 */
	class AsyUpdateWorker extends Thread {
		private Map<String, Object> produce;// 用作替换更新对象
		private Map<String, Object> consume;// 用作替换更新对象
		private boolean start = true;

		public AsyUpdateWorker(String name) {
			this.setDaemon(true);
			this.setName(name);
			this.setProduce(new HashMap<String, Object>());
			this.setConsume(new HashMap<String, Object>());
		}

		public Map<String, Object> getProduce() {
			return produce;
		}

		public void setProduce(Map<String, Object> produce) {
			this.produce = produce;
		}

		public Map<String, Object> getConsume() {
			return consume;
		}

		public void setConsume(Map<String, Object> consume) {
			this.consume = consume;
		}

		public void addMap(String key, Object o) {
			synchronized (this) {
				produce.put(key, o);
			}
		}

		public boolean isStart() {
			return start;
		}

		public void setStart(boolean start) {
			this.start = start;
		}

		public int size() {
			return this.produce.size() + this.consume.size();
		}

		// 唤醒，在退出系统时用
		public void weakup() {
			synchronized (this) {
				this.notifyAll();
			}
		}

		@Override
		public void run() {
			while (start) {
				try {
					synchronized (this) {
						if (produce == null || produce.size() == 0 || !start) {
							this.wait(period);// 等2分钟
							continue;
						}
					}

					synchronized (this) {
						Map<String, Object> tmp = produce;
						produce = consume;
						consume = tmp;
					}

					if (consume.size() > 0) {
						Iterator<Map.Entry<String, Object>> itl = consume
								.entrySet().iterator();
						while (itl.hasNext()) {
							Object o = itl.next().getValue();
							AsyUpdateMapService.this.updateObject(o);
						}
						consume.clear();
					}

					synchronized (this) {
						this.wait(period);// 等2分钟
					}
				} catch (Exception e) {
					log.error(e.toString(), e);
				} finally {

				}
			}
		}
	}

	/**
	 * 执行数据库
	 * 
	 * @param o
	 */
	private void updateObject(Object o) {
		if (o instanceof MapCell) {
			mapDao.updateMapCell((MapCell) o);
		} else if (o instanceof Armyout) {
			armyoutDao.updateArmyout((Armyout) o);
		}
	}

	/**
	 * 加入到异步更新队列
	 * 
	 * @param o
	 */
	private void asyUpdateObject(Object o) {
		if (o instanceof MapCell) {
			MapCell mapCell = (MapCell) o;
			String key = AsyUpdateMapService.MAPCELL_KEY + mapCell.getId();
			asyUpdateWorker.addMap(key, mapCell);
		} else if (o instanceof Armyout) {
			Armyout armyout = (Armyout) o;
			String key = AsyUpdateMapService.ARMYOUT_KEY + armyout.getId();
			asyUpdateWorker.addMap(key, armyout);
		}
	}

	@Override
	public void update(List<Object> objs) {
		if (objs != null && objs.size() > 0) {
			for (Object obj : objs) {
				this.asyUpdateObject(obj);
			}
		}
	}
}
