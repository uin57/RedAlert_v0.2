package com.youxigu.dynasty2.map;
/**
 * 地图包文档类
 *
 */
public class _package_ {
	/**
	 * 坐标点配置-策划配置 加载坐标点
	 * 
	 * 地图操作 建号落地，迁城（定点，随机），流亡落地，转国， 刷怪，刷资源地，抢地盘，宣战，攻打，进驻，国战, 大地图拖拽, 划线, 军事策略，加速等
	 * 
	 * 需要解决的问题 
	 * 1.坐标锁 AtomicInteger解决 建号落地，
	 * 2.迁城，流亡落地，转国 这几个操作需要解决快速需找空闲坐标的问题
	 * 3.刷怪，刷资源地操作需要解决，批量操作，搜索空闲坐标 
	 * 4.大地图拖拽 要解决按坐标点半径范围取玩家数据的问题 
	 * 5.划线要解决按坐标点范围取相关出征数据，尽量小 
	 * 6.建城需要占用4个坐标点，联盟基地或是国家基地可能占用更多的建城点
	 * 7.军事策略，加速等需要取消job
	 * 
	 * 可选择的方案 memcached方式的是数据全部持久化，大量使用job，用mc锁，解决批量抓取空闲点的问题 全缓存方式
	 * 数据用本地缓存来做，使用时间优先级队列， 通过分缓存来做取空闲点的问题， 用AtomicInteger做锁
	 * 
	 * 
	 * 
	 * 
数据格式，保存为。txt文件
"11400600","1140","600","1","0","0","1"
"11400601","1140","601","1","0","0","1"



字段值用""括起来，智志坚用,分割

mapcell结构
id,posX,posY,stateId,casId,castType,countryId
"11400600","1140","600","1","0","0","1"

表格MapCell
坐标表 （策划配数）
代码	注释	数据类型
id	Pk  id=posX*10000+posY	integer(16)
posX	坐标X	integer(16)
posY	坐标Y	integer(16)
stateId	区id	integer(16)
castType	城池类型 默认0	integer(16)
countryId	国家id	integer(16)
casId	城池id  默认0	bigint(19)
userId	领地君主 默认0	bigint(19)
guildId	领地联盟 默认0	bigint(19)
earthId	地表对应的配置id		integer(16)
earthType	地表类型	integer(16)

地表类型
	名称	可否迁城	可否被占领
1	草地	1	0
2	沙地	1	0
3	浅滩	1	0
4	山峰	0	0
5	海洋	0	0
6	公路	1	0
7	个人资源	0	1
8	联盟资源	0	1
9	国家资源	0	1

state区
stateId,countryId,stateName,status,level
"1","1","A国超新手区","1","10"

stateId;//郡id
countryId;//国家id
stateName;//郡名
status;//未开放,已开放,已满




LOAD DATA INFILE '/usr/local/maps/MapCell001.txt' IGNORE INTO TABLE redalert.mapcell_1 CHARACTER SET utf8 FIELDS TERMINATED BY '\,' OPTIONALLY ENCLOSED BY '\"' LINES TERMINATED BY '\r\n';
LOAD DATA INFILE '/usr/local/maps/MapCell002.txt' IGNORE INTO TABLE redalert.mapcell_2 CHARACTER SET utf8 FIELDS TERMINATED BY '\,' OPTIONALLY ENCLOSED BY '\"' LINES TERMINATED BY '\r\n';
LOAD DATA INFILE '/usr/local/maps/MapCell003.txt' IGNORE INTO TABLE redalert.mapcell_3 CHARACTER SET utf8 FIELDS TERMINATED BY '\,' OPTIONALLY ENCLOSED BY '\"' LINES TERMINATED BY '\r\n';
LOAD DATA INFILE '/usr/local/maps/MapCell004.txt' IGNORE INTO TABLE redalert.mapcell_4 CHARACTER SET utf8 FIELDS TERMINATED BY '\,' OPTIONALLY ENCLOSED BY '\"' LINES TERMINATED BY '\r\n';
LOAD DATA INFILE '/usr/local/maps/MapCell005.txt' IGNORE INTO TABLE redalert.mapcell_5 CHARACTER SET utf8 FIELDS TERMINATED BY '\,' OPTIONALLY ENCLOSED BY '\"' LINES TERMINATED BY '\r\n';
LOAD DATA INFILE '/usr/local/maps/MapCell006.txt' IGNORE INTO TABLE redalert.mapcell_6 CHARACTER SET utf8 FIELDS TERMINATED BY '\,' OPTIONALLY ENCLOSED BY '\"' LINES TERMINATED BY '\r\n';
LOAD DATA INFILE '/usr/local/maps/MapCell007.txt' IGNORE INTO TABLE redalert.mapcell_7 CHARACTER SET utf8 FIELDS TERMINATED BY '\,' OPTIONALLY ENCLOSED BY '\"' LINES TERMINATED BY '\r\n';
LOAD DATA INFILE '/usr/local/maps/MapCell008.txt' IGNORE INTO TABLE redalert.mapcell_8 CHARACTER SET utf8 FIELDS TERMINATED BY '\,' OPTIONALLY ENCLOSED BY '\"' LINES TERMINATED BY '\r\n';
LOAD DATA INFILE '/usr/local/maps/MapCell009.txt' IGNORE INTO TABLE redalert.mapcell_9 CHARACTER SET utf8 FIELDS TERMINATED BY '\,' OPTIONALLY ENCLOSED BY '\"' LINES TERMINATED BY '\r\n';
LOAD DATA INFILE '/usr/local/maps/MapCell010.txt' IGNORE INTO TABLE redalert.mapcell_10 CHARACTER SET utf8 FIELDS TERMINATED BY '\,' OPTIONALLY ENCLOSED BY '\"' LINES TERMINATED BY '\r\n';
LOAD DATA INFILE '/usr/local/maps/MapCell011.txt' IGNORE INTO TABLE redalert.mapcell_11 CHARACTER SET utf8 FIELDS TERMINATED BY '\,' OPTIONALLY ENCLOSED BY '\"' LINES TERMINATED BY '\r\n';
LOAD DATA INFILE '/usr/local/maps/MapCell012.txt' IGNORE INTO TABLE redalert.mapcell_12 CHARACTER SET utf8 FIELDS TERMINATED BY '\,' OPTIONALLY ENCLOSED BY '\"' LINES TERMINATED BY '\r\n';
LOAD DATA INFILE '/usr/local/maps/MapCell013.txt' IGNORE INTO TABLE redalert.mapcell_13 CHARACTER SET utf8 FIELDS TERMINATED BY '\,' OPTIONALLY ENCLOSED BY '\"' LINES TERMINATED BY '\r\n';
LOAD DATA INFILE '/usr/local/maps/MapCell014.txt' IGNORE INTO TABLE redalert.mapcell_14 CHARACTER SET utf8 FIELDS TERMINATED BY '\,' OPTIONALLY ENCLOSED BY '\"' LINES TERMINATED BY '\r\n';
LOAD DATA INFILE '/usr/local/maps/MapCell015.txt' IGNORE INTO TABLE redalert.mapcell_15 CHARACTER SET utf8 FIELDS TERMINATED BY '\,' OPTIONALLY ENCLOSED BY '\"' LINES TERMINATED BY '\r\n';

LOAD DATA INFILE '/usr/local/maps/State.txt' IGNORE INTO TABLE redalert.state CHARACTER SET utf8 FIELDS TERMINATED BY '\,' OPTIONALLY ENCLOSED BY '\"' LINES TERMINATED BY '\r\n';
	 */
}