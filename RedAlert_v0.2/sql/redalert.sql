﻿CREATE DATABASE  IF NOT EXISTS `redalert` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `redalert`;
-- MySQL dump 10.13  Distrib 5.6.24, for Win64 (x86_64)
--
-- Host: 192.168.3.42    Database: redalert
-- ------------------------------------------------------
-- Server version	5.5.46

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `account`
--

DROP TABLE IF EXISTS `account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `account` (
  `accId` bigint(16) NOT NULL,
  `accName` varchar(64) DEFAULT NULL,
  `accDesc` varchar(40) DEFAULT '',
  `status` int(2) DEFAULT '0',
  `createDttm` timestamp NULL DEFAULT NULL,
  `loginIp` varchar(20) DEFAULT NULL,
  `lastLoginDttm` timestamp NULL DEFAULT NULL,
  `envelopDttm` timestamp NULL DEFAULT NULL COMMENT '封号时间',
  `inviteAccId` bigint(16) DEFAULT '0' COMMENT '邀请人的账号',
  `nodeIp` varchar(50) DEFAULT NULL,
  `qqYellowLv` int(4) DEFAULT '0' COMMENT 'qq黄钻等级',
  `pf` varchar(255) DEFAULT NULL,
  `dType` int(10) DEFAULT '0',
  `pType` int(10) DEFAULT '0',
  `dInfo` varchar(2048) DEFAULT NULL,
  `via` varchar(255) DEFAULT NULL,
  `qqFlag` int(16) DEFAULT '0' COMMENT 'qq标志',
  `qqBlueLv` int(4) DEFAULT '0' COMMENT 'qq蓝钻等级',
  `qqPlusLv` int(4) DEFAULT '0' COMMENT 'q+等级',
  `qqLv` int(4) DEFAULT '0' COMMENT 'qq等级',
  `qqVipLv` int(4) DEFAULT '0' COMMENT 'qqVip等级',
  `qq3366Lv` int(4) DEFAULT '0' COMMENT '3366等级',
  `qqRedLv` int(4) DEFAULT '0' COMMENT '红钻等级',
  `qqGreenLv` int(4) DEFAULT '0' COMMENT '绿钻等级',
  `qqPinkLv` int(4) DEFAULT '0' COMMENT '粉钻等级',
  `qqSuperLv` int(4) DEFAULT '0' COMMENT '超Q等级',
  `qqCurrFlag` int(16) DEFAULT '0' COMMENT '当前qq标志',
  `lastLogoutDttm` timestamp NULL DEFAULT NULL,
  `onlineTimeSum` int(16) DEFAULT '0' COMMENT '累计在线时间',
  `offlineTimeSum` int(16) DEFAULT '0' COMMENT '累计离线时间',
  `areaId` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`accId`),
  UNIQUE KEY `accName` (`accName`,`areaId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `accountwhitelist`
--

DROP TABLE IF EXISTS `accountwhitelist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `accountwhitelist` (
  `openId` varchar(64) NOT NULL,
  `dttm` timestamp NULL DEFAULT NULL,
  `status` smallint(6) DEFAULT '0',
  PRIMARY KEY (`openId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `army`
--

DROP TABLE IF EXISTS `army`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `army` (
  `entId` int(16) NOT NULL,
  `armyName` varchar(200) NOT NULL DEFAULT '0',
  `armyDesc` varchar(1000) NOT NULL,
  `armyType` varchar(400) NOT NULL,
  PRIMARY KEY (`entId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `buffdefine`
--

DROP TABLE IF EXISTS `buffdefine`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `buffdefine` (
  `buffId` int(16) NOT NULL,
  `buffName` varchar(40) NOT NULL,
  `itemId` int(16) DEFAULT '0',
  PRIMARY KEY (`buffId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `bufftip`
--

DROP TABLE IF EXISTS `bufftip`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bufftip` (
  `id` int(16) NOT NULL AUTO_INCREMENT,
  `userId` bigint(19) DEFAULT '0',
  `buffId` int(16) DEFAULT '0',
  `effId` int(16) DEFAULT '0',
  `buffName` varchar(40) DEFAULT NULL,
  `startTime` timestamp NULL DEFAULT NULL,
  `endTime` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `index_BuffTip_userId` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `building`
--

DROP TABLE IF EXISTS `building`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `building` (
  `entId` int(16) NOT NULL,
  `buiName` varchar(40) DEFAULT NULL,
  `buiDesc` varchar(200) DEFAULT NULL,
  `maxLevel` int(4) DEFAULT '0',
  `initialLevel` int(4) DEFAULT '1',
  `needEntId` int(4) DEFAULT '0',
  `needLevel` int(4) DEFAULT '0',
  PRIMARY KEY (`entId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `castle`
--

DROP TABLE IF EXISTS `castle`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `castle` (
  `casId` bigint(19) NOT NULL,
  `userId` bigint(19) NOT NULL,
  `calcuDttm` timestamp NULL DEFAULT NULL,
  `quarCalcuDttm` timestamp NULL DEFAULT NULL,
  `stateId` int(16) DEFAULT NULL COMMENT '国家ID',
  `changeCountryDttm` timestamp NULL DEFAULT NULL COMMENT '转换国家的时间',
  `autoBuild` tinyint(2) DEFAULT '0' COMMENT '0: 非自动建造；1: 自动建造',
  `posX` int(4) NOT NULL DEFAULT '0',
  `posY` int(4) NOT NULL DEFAULT '0',
  `status` int(4) DEFAULT '0',
  `casName` varchar(40) DEFAULT NULL,
  PRIMARY KEY (`casId`),
  KEY `index_castle_userId` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `castlearmy`
--

DROP TABLE IF EXISTS `castlearmy`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `castlearmy` (
  `casId` bigint(19) NOT NULL,
  `buildNum` smallint(32) NOT NULL DEFAULT '0',
  `num` int(32) NOT NULL DEFAULT '0',
  `refreshCD` timestamp NULL DEFAULT NULL,
  `status` int(32) DEFAULT '0',
  PRIMARY KEY (`casId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `castlebuilder`
--

DROP TABLE IF EXISTS `castlebuilder`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `castlebuilder` (
  `id` int(16) NOT NULL AUTO_INCREMENT,
  `userId` bigint(19) NOT NULL DEFAULT '0',
  `casId` bigint(19) NOT NULL DEFAULT '0',
  `builderIndex` tinyint(2) NOT NULL,
  `buiId` int(16) NOT NULL DEFAULT '0',
  `beginDttm` timestamp NULL DEFAULT NULL,
  `endDttm` timestamp NULL DEFAULT NULL,
  `status` tinyint(2) DEFAULT '0' COMMENT '建筑队列状态。0：空闲；1：忙碌；2：未开放',
  PRIMARY KEY (`id`),
  KEY `index_CastleBuilder_userId` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `castlebuilding_0`
--

DROP TABLE IF EXISTS `castlebuilding_0`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `castlebuilding_0` (
  `casBuiId` int(16) NOT NULL AUTO_INCREMENT,
  `casId` bigint(19) NOT NULL,
  `buiEntId` int(16) NOT NULL,
  `level` smallint(6) DEFAULT '0',
  `builderIndex` tinyint(2) DEFAULT '0',
  `beginDttm` timestamp NULL DEFAULT NULL,
  `endDttm` timestamp NULL DEFAULT NULL,
  `autoBuild` tinyint(2) DEFAULT '0',
  PRIMARY KEY (`casBuiId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `castlebuilding_1`
--

DROP TABLE IF EXISTS `castlebuilding_1`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `castlebuilding_1` (
  `casBuiId` int(16) NOT NULL AUTO_INCREMENT,
  `casId` bigint(19) NOT NULL,
  `buiEntId` int(16) NOT NULL,
  `level` smallint(6) DEFAULT '0',
  `builderIndex` tinyint(2) DEFAULT '0',
  `beginDttm` timestamp NULL DEFAULT NULL,
  `endDttm` timestamp NULL DEFAULT NULL,
  `autoBuild` tinyint(2) DEFAULT '0',
  PRIMARY KEY (`casBuiId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `castlebuilding_2`
--

DROP TABLE IF EXISTS `castlebuilding_2`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `castlebuilding_2` (
  `casBuiId` int(16) NOT NULL AUTO_INCREMENT,
  `casId` bigint(19) NOT NULL,
  `buiEntId` int(16) NOT NULL,
  `level` smallint(6) DEFAULT '0',
  `builderIndex` tinyint(2) DEFAULT '0',
  `beginDttm` timestamp NULL DEFAULT NULL,
  `endDttm` timestamp NULL DEFAULT NULL,
  `autoBuild` tinyint(2) DEFAULT '0',
  PRIMARY KEY (`casBuiId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `castlebuilding_3`
--

DROP TABLE IF EXISTS `castlebuilding_3`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `castlebuilding_3` (
  `casBuiId` int(16) NOT NULL AUTO_INCREMENT,
  `casId` bigint(19) NOT NULL,
  `buiEntId` int(16) NOT NULL,
  `level` smallint(6) DEFAULT '0',
  `builderIndex` tinyint(2) DEFAULT '0',
  `beginDttm` timestamp NULL DEFAULT NULL,
  `endDttm` timestamp NULL DEFAULT NULL,
  `autoBuild` tinyint(2) DEFAULT '0',
  PRIMARY KEY (`casBuiId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `castlebuilding_4`
--

DROP TABLE IF EXISTS `castlebuilding_4`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `castlebuilding_4` (
  `casBuiId` int(16) NOT NULL AUTO_INCREMENT,
  `casId` bigint(19) NOT NULL,
  `buiEntId` int(16) NOT NULL,
  `level` smallint(6) DEFAULT '0',
  `builderIndex` tinyint(2) DEFAULT '0',
  `beginDttm` timestamp NULL DEFAULT NULL,
  `endDttm` timestamp NULL DEFAULT NULL,
  `autoBuild` tinyint(2) DEFAULT '0',
  PRIMARY KEY (`casBuiId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `castlebuilding_5`
--

DROP TABLE IF EXISTS `castlebuilding_5`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `castlebuilding_5` (
  `casBuiId` int(16) NOT NULL AUTO_INCREMENT,
  `casId` bigint(19) NOT NULL,
  `buiEntId` int(16) NOT NULL,
  `level` smallint(6) DEFAULT '0',
  `builderIndex` tinyint(2) DEFAULT '0',
  `beginDttm` timestamp NULL DEFAULT NULL,
  `endDttm` timestamp NULL DEFAULT NULL,
  `autoBuild` tinyint(2) DEFAULT '0',
  PRIMARY KEY (`casBuiId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `castlebuilding_6`
--

DROP TABLE IF EXISTS `castlebuilding_6`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `castlebuilding_6` (
  `casBuiId` int(16) NOT NULL AUTO_INCREMENT,
  `casId` bigint(19) NOT NULL,
  `buiEntId` int(16) NOT NULL,
  `level` smallint(6) DEFAULT '0',
  `builderIndex` tinyint(2) DEFAULT '0',
  `beginDttm` timestamp NULL DEFAULT NULL,
  `endDttm` timestamp NULL DEFAULT NULL,
  `autoBuild` tinyint(2) DEFAULT '0',
  PRIMARY KEY (`casBuiId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `castlebuilding_7`
--

DROP TABLE IF EXISTS `castlebuilding_7`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `castlebuilding_7` (
  `casBuiId` int(16) NOT NULL AUTO_INCREMENT,
  `casId` bigint(19) NOT NULL,
  `buiEntId` int(16) NOT NULL,
  `level` smallint(6) DEFAULT '0',
  `builderIndex` tinyint(2) DEFAULT '0',
  `beginDttm` timestamp NULL DEFAULT NULL,
  `endDttm` timestamp NULL DEFAULT NULL,
  `autoBuild` tinyint(2) DEFAULT '0',
  PRIMARY KEY (`casBuiId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `castlebuilding_8`
--

DROP TABLE IF EXISTS `castlebuilding_8`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `castlebuilding_8` (
  `casBuiId` int(16) NOT NULL AUTO_INCREMENT,
  `casId` bigint(19) NOT NULL,
  `buiEntId` int(16) NOT NULL,
  `level` smallint(6) DEFAULT '0',
  `builderIndex` tinyint(2) DEFAULT '0',
  `beginDttm` timestamp NULL DEFAULT NULL,
  `endDttm` timestamp NULL DEFAULT NULL,
  `autoBuild` tinyint(2) DEFAULT '0',
  PRIMARY KEY (`casBuiId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `castlebuilding_9`
--

DROP TABLE IF EXISTS `castlebuilding_9`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `castlebuilding_9` (
  `casBuiId` int(16) NOT NULL AUTO_INCREMENT,
  `casId` bigint(19) NOT NULL,
  `buiEntId` int(16) NOT NULL,
  `level` smallint(6) DEFAULT '0',
  `builderIndex` tinyint(2) DEFAULT '0',
  `beginDttm` timestamp NULL DEFAULT NULL,
  `endDttm` timestamp NULL DEFAULT NULL,
  `autoBuild` tinyint(2) DEFAULT '0',
  PRIMARY KEY (`casBuiId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `castleeffect_0`
--

DROP TABLE IF EXISTS `castleeffect_0`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `castleeffect_0` (
  `id` int(16) NOT NULL AUTO_INCREMENT,
  `casId` bigint(19) NOT NULL,
  `entId` int(16) NOT NULL DEFAULT '0',
  `effTypeId` varchar(40) DEFAULT NULL COMMENT '加成类型（科技、建筑...）',
  `absValue` int(8) DEFAULT '0' COMMENT '效果绝对值',
  `perValue` int(8) DEFAULT '0' COMMENT '效果百分比',
  `expireDttm` timestamp NULL DEFAULT NULL COMMENT '效果过期时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `castleeffect_1`
--

DROP TABLE IF EXISTS `castleeffect_1`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `castleeffect_1` (
  `id` int(16) NOT NULL AUTO_INCREMENT,
  `casId` bigint(19) NOT NULL,
  `entId` int(16) NOT NULL DEFAULT '0',
  `effTypeId` varchar(40) DEFAULT NULL COMMENT '加成类型（科技、建筑...）',
  `absValue` int(8) DEFAULT '0' COMMENT '效果绝对值',
  `perValue` int(8) DEFAULT '0' COMMENT '效果百分比',
  `expireDttm` timestamp NULL DEFAULT NULL COMMENT '效果过期时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `castleeffect_2`
--

DROP TABLE IF EXISTS `castleeffect_2`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `castleeffect_2` (
  `id` int(16) NOT NULL AUTO_INCREMENT,
  `casId` bigint(19) NOT NULL,
  `entId` int(16) NOT NULL DEFAULT '0',
  `effTypeId` varchar(40) DEFAULT NULL COMMENT '加成类型（科技、建筑...）',
  `absValue` int(8) DEFAULT '0' COMMENT '效果绝对值',
  `perValue` int(8) DEFAULT '0' COMMENT '效果百分比',
  `expireDttm` timestamp NULL DEFAULT NULL COMMENT '效果过期时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `castleeffect_3`
--

DROP TABLE IF EXISTS `castleeffect_3`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `castleeffect_3` (
  `id` int(16) NOT NULL AUTO_INCREMENT,
  `casId` bigint(19) NOT NULL,
  `entId` int(16) NOT NULL DEFAULT '0',
  `effTypeId` varchar(40) DEFAULT NULL COMMENT '加成类型（科技、建筑...）',
  `absValue` int(8) DEFAULT '0' COMMENT '效果绝对值',
  `perValue` int(8) DEFAULT '0' COMMENT '效果百分比',
  `expireDttm` timestamp NULL DEFAULT NULL COMMENT '效果过期时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `castleeffect_4`
--

DROP TABLE IF EXISTS `castleeffect_4`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `castleeffect_4` (
  `id` int(16) NOT NULL AUTO_INCREMENT,
  `casId` bigint(19) NOT NULL,
  `entId` int(16) NOT NULL DEFAULT '0',
  `effTypeId` varchar(40) DEFAULT NULL COMMENT '加成类型（科技、建筑...）',
  `absValue` int(8) DEFAULT '0' COMMENT '效果绝对值',
  `perValue` int(8) DEFAULT '0' COMMENT '效果百分比',
  `expireDttm` timestamp NULL DEFAULT NULL COMMENT '效果过期时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `castleeffect_5`
--

DROP TABLE IF EXISTS `castleeffect_5`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `castleeffect_5` (
  `id` int(16) NOT NULL AUTO_INCREMENT,
  `casId` bigint(19) NOT NULL,
  `entId` int(16) NOT NULL DEFAULT '0',
  `effTypeId` varchar(40) DEFAULT NULL COMMENT '加成类型（科技、建筑...）',
  `absValue` int(8) DEFAULT '0' COMMENT '效果绝对值',
  `perValue` int(8) DEFAULT '0' COMMENT '效果百分比',
  `expireDttm` timestamp NULL DEFAULT NULL COMMENT '效果过期时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `castleeffect_6`
--

DROP TABLE IF EXISTS `castleeffect_6`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `castleeffect_6` (
  `id` int(16) NOT NULL AUTO_INCREMENT,
  `casId` bigint(19) NOT NULL,
  `entId` int(16) NOT NULL DEFAULT '0',
  `effTypeId` varchar(40) DEFAULT NULL COMMENT '加成类型（科技、建筑...）',
  `absValue` int(8) DEFAULT '0' COMMENT '效果绝对值',
  `perValue` int(8) DEFAULT '0' COMMENT '效果百分比',
  `expireDttm` timestamp NULL DEFAULT NULL COMMENT '效果过期时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `castleeffect_7`
--

DROP TABLE IF EXISTS `castleeffect_7`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `castleeffect_7` (
  `id` int(16) NOT NULL AUTO_INCREMENT,
  `casId` bigint(19) NOT NULL,
  `entId` int(16) NOT NULL DEFAULT '0',
  `effTypeId` varchar(40) DEFAULT NULL COMMENT '加成类型（科技、建筑...）',
  `absValue` int(8) DEFAULT '0' COMMENT '效果绝对值',
  `perValue` int(8) DEFAULT '0' COMMENT '效果百分比',
  `expireDttm` timestamp NULL DEFAULT NULL COMMENT '效果过期时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `castleeffect_8`
--

DROP TABLE IF EXISTS `castleeffect_8`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `castleeffect_8` (
  `id` int(16) NOT NULL AUTO_INCREMENT,
  `casId` bigint(19) NOT NULL,
  `entId` int(16) NOT NULL DEFAULT '0',
  `effTypeId` varchar(40) DEFAULT NULL COMMENT '加成类型（科技、建筑...）',
  `absValue` int(8) DEFAULT '0' COMMENT '效果绝对值',
  `perValue` int(8) DEFAULT '0' COMMENT '效果百分比',
  `expireDttm` timestamp NULL DEFAULT NULL COMMENT '效果过期时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `castleeffect_9`
--

DROP TABLE IF EXISTS `castleeffect_9`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `castleeffect_9` (
  `id` int(16) NOT NULL AUTO_INCREMENT,
  `casId` bigint(19) NOT NULL,
  `entId` int(16) NOT NULL DEFAULT '0',
  `effTypeId` varchar(40) DEFAULT NULL COMMENT '加成类型（科技、建筑...）',
  `absValue` int(8) DEFAULT '0' COMMENT '效果绝对值',
  `perValue` int(8) DEFAULT '0' COMMENT '效果百分比',
  `expireDttm` timestamp NULL DEFAULT NULL COMMENT '效果过期时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `castleres`
--

DROP TABLE IF EXISTS `castleres`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `castleres` (
  `casId` bigint(19) NOT NULL,
  `goldNum` bigint(19) DEFAULT '0',
  `ironNum` bigint(19) DEFAULT '0',
  `oilNum` bigint(19) DEFAULT '0',
  `uranium` bigint(19) DEFAULT '0',
  `casGoldNum` bigint(19) DEFAULT '0',
  `casIronNum` bigint(19) DEFAULT '0',
  `casOilNum` bigint(19) DEFAULT '0',
  `casUranium` bigint(19) DEFAULT '0',
  `cashNum` int(4) DEFAULT '0',
  `lastBuyDttm` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`casId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `combatdb`
--

DROP TABLE IF EXISTS `combatdb`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `combatdb` (
  `combatId` varchar(64) NOT NULL,
  `combatData` blob,
  `combatDt` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`combatId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `combatfactor`
--

DROP TABLE IF EXISTS `combatfactor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `combatfactor` (
  `formula` varchar(40) NOT NULL,
  `para1` double(12,2) NOT NULL DEFAULT '1.00',
  `para2` double(12,2) NOT NULL DEFAULT '1.00',
  `para3` double(12,2) NOT NULL DEFAULT '1.00',
  `para4` double(12,2) NOT NULL DEFAULT '1.00',
  PRIMARY KEY (`formula`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `country`
--

DROP TABLE IF EXISTS `country`;
CREATE TABLE `country` (
  `countryId` int(16) NOT NULL,
  `countryName` varchar(100) NOT NULL,
  `neutral` int(16) DEFAULT '0',
  `stateId` int(16) DEFAULT '0',
  PRIMARY KEY (`countryId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `droppack`
--

DROP TABLE IF EXISTS `droppack`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `droppack` (
  `id` int(16) NOT NULL AUTO_INCREMENT,
  `entId` int(16) DEFAULT '0',
  `dropentId` int(16) DEFAULT '0',
  `dropPercent` int(4) DEFAULT '0',
  `minValue` int(8) DEFAULT '0',
  `maxValue2` int(8) DEFAULT '0',
  `weight` int(4) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `index_dropPack_dropPackId` (`entId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `effectdefine`
--

DROP TABLE IF EXISTS `effectdefine`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `effectdefine` (
  `effId` int(16) NOT NULL AUTO_INCREMENT,
  `effTypeId` varchar(40) DEFAULT NULL,
  `level` int(4) DEFAULT '0',
  `para1` int(16) DEFAULT '0',
  `para2` int(16) DEFAULT '0',
  `para3` int(16) DEFAULT '0',
  `serviceName` varchar(100) DEFAULT NULL,
  `iconPath` varchar(40) DEFAULT NULL,
  `buffName` varchar(40) DEFAULT NULL,
  `buffId` int(4) DEFAULT '0',
  `effName` varchar(100) NOT NULL,
  `effdesc` varchar(200) NOT NULL,
  `target` int(2) DEFAULT '0',
  PRIMARY KEY (`effId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='1=对城池效果,2=对用户效果3=对英雄效果...';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `effecttypedefine`
--

DROP TABLE IF EXISTS `effecttypedefine`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `effecttypedefine` (
  `effTypeId` varchar(40) NOT NULL,
  `effTypeName` varchar(100) NOT NULL,
  `effTypedesc` varchar(200) NOT NULL,
  `showFlag` int(2) DEFAULT '0',
  `viewOrder` int(4) DEFAULT '0',
  PRIMARY KEY (`effTypeId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `entity`
--

DROP TABLE IF EXISTS `entity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `entity` (
  `entId` int(16) NOT NULL AUTO_INCREMENT,
  `entType` varchar(20) NOT NULL,
  PRIMARY KEY (`entId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `entityconsume`
--

DROP TABLE IF EXISTS `entityconsume`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `entityconsume` (
  `id` int(16) NOT NULL AUTO_INCREMENT,
  `entId` int(16) NOT NULL,
  `level` int(4) NOT NULL DEFAULT '0',
  `needEntId` varchar(100) NOT NULL,
  `needEntNum` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `index_entId` (`entId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `entityeffect`
--

DROP TABLE IF EXISTS `entityeffect`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `entityeffect` (
  `entId` int(16) NOT NULL,
  `effId` int(16) NOT NULL,
  PRIMARY KEY (`entId`,`effId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `entitylimit`
--

DROP TABLE IF EXISTS `entitylimit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `entitylimit` (
  `id` int(16) NOT NULL AUTO_INCREMENT,
  `entId` int(16) NOT NULL,
  `level` int(4) NOT NULL DEFAULT '0',
  `needEntId` int(16) NOT NULL,
  `needLevel` int(4) NOT NULL DEFAULT '0',
  `leastNum` int(8) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `index_entId` (`entId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `enumer`
--

DROP TABLE IF EXISTS `enumer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `enumer` (
  `enumId` varchar(40) NOT NULL,
  `enumGroup` varchar(40) NOT NULL,
  `enumValue` varchar(100) NOT NULL,
  `enumDesc` varchar(200) NOT NULL,
  `orderBy` int(2) NOT NULL DEFAULT '0',
  PRIMARY KEY (`enumId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `equip`
--

DROP TABLE IF EXISTS `equip`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `equip` (
  `entId` int(16) NOT NULL,
  `suitId` int(16) DEFAULT '0',
  `etype` int(16) DEFAULT '0',
  `value` varchar(500) DEFAULT NULL,
  `fragmentItem` varchar(500) DEFAULT NULL,
  `gold` int(32) DEFAULT NULL,
  `buildItem` varchar(500) DEFAULT NULL,
  `specSysHeroId` varchar(500) DEFAULT NULL,
  `specValue` varchar(500) DEFAULT NULL,
  `buildAttr1` varchar(500) DEFAULT NULL,
  `buildAttr2` varchar(500) DEFAULT NULL,
  `buildAttr3` varchar(500) DEFAULT NULL,
  `buildSpecialAttr` varchar(500) DEFAULT NULL,
  `buildFactor` int(16) DEFAULT NULL,
  `equipDebris` int(16) DEFAULT NULL,
  `equipDebrisCount` int(16) DEFAULT NULL,
  `goldNum` int(16) DEFAULT NULL,
  PRIMARY KEY (`entId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `equiplevelup`
--

DROP TABLE IF EXISTS `equiplevelup`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `equiplevelup` (
  `equipColor` int(11) NOT NULL,
  `level` int(11) NOT NULL DEFAULT '0',
  `lvUpGoldNum` int(32) NOT NULL DEFAULT '0',
  `entId` int(32) DEFAULT '0',
  `entNum` int(32) DEFAULT '0',
  `entId2` int(32) DEFAULT '0',
  `entNum2` int(32) DEFAULT '0',
  `destroyGoldNum` int(32) NOT NULL DEFAULT '0',
  `destroyEntId` int(32) DEFAULT '0',
  `destroyEntNum` int(32) DEFAULT '0',
  `destroyEntId2` int(32) DEFAULT '0',
  `destroyEntNum2` int(32) DEFAULT '0',
  PRIMARY KEY (`equipColor`,`level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `hero_0`
--

DROP TABLE IF EXISTS `hero_0`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hero_0` (
  `heroId` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '武将id',
  `casId` bigint(19) NOT NULL COMMENT '城池id',
  `userId` bigint(19) NOT NULL COMMENT '君主id',
  `sysHeroId` int(16) DEFAULT '0' COMMENT '系统武将id',
  `name` varchar(40) DEFAULT NULL COMMENT '名字',
  `icon` varchar(40) DEFAULT NULL COMMENT '武将头像',
  `level` int(4) DEFAULT '0' COMMENT '等级',
  `exp` int(16) DEFAULT '0' COMMENT '经验',
  `curHp` int(32) DEFAULT '0' COMMENT '当前血量',
  `magicAttack` int(32) DEFAULT '0' COMMENT '法术攻击',
  `physicalAttack` int(32) DEFAULT '0' COMMENT '物理攻击',
  `magicDefend` int(32) DEFAULT '0' COMMENT '法术防御',
  `physicalDefend` int(32) DEFAULT '0' COMMENT '物理防御',
  `intHp` int(32) DEFAULT '0' COMMENT '生命值',
  `actionStatus` smallint(6) DEFAULT '0' COMMENT '行动状态',
  `freeDttm` timestamp NULL DEFAULT NULL COMMENT '行动超时时间',
  `troopGridId` bigint(19) NOT NULL COMMENT '武将所属的格子id',
  `skillId1` int(16) DEFAULT '0' COMMENT '技能编号',
  `skillId2` int(16) DEFAULT '0' COMMENT '技能编号',
  `skillId3` int(16) DEFAULT '0' COMMENT '技能编号',
  `skillId4` int(16) DEFAULT '0' COMMENT '技能编号',
  `skillId5` int(16) DEFAULT '0' COMMENT '技能编号',
  `skillId6` int(16) DEFAULT '0' COMMENT '技能编号',
  `heroFate` varchar(200) DEFAULT '0' COMMENT '武将情缘', 
  `growing` smallint(6) NOT NULL DEFAULT '0' COMMENT '当前成长',
  `growingItem` int(16) NOT NULL DEFAULT '0' COMMENT '强化达到当前成长消耗的道具数量',
  `relifeNum` int(4) DEFAULT '0' COMMENT '进阶等级',
  `heroStrength` int(4) DEFAULT '0' COMMENT '强化等级',
  `status` smallint(6) DEFAULT '0' COMMENT '雇佣状态',
  `teamLeader` smallint(6) DEFAULT NULL,
  `heroCardEntId` int(16) DEFAULT '0',
  `heroCardNum` int(16) DEFAULT '0',
  `heroSoulEntId` int(16) DEFAULT '0',
  `heroSoulNum` int(16) DEFAULT '0',
  `sumHeroCardNum` int(16) DEFAULT '0',
  `commander` tinyint(2) DEFAULT '0',
  PRIMARY KEY (`heroId`),
  KEY `index_Hero_userId` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

DROP TABLE IF EXISTS `hero_1`;
CREATE TABLE `hero_1` LIKE `hero_0`;
DROP TABLE IF EXISTS `hero_2`;
CREATE TABLE `hero_2` LIKE `hero_0`;
DROP TABLE IF EXISTS `hero_3`;
CREATE TABLE `hero_3` LIKE `hero_0`;
DROP TABLE IF EXISTS `hero_4`;
CREATE TABLE `hero_4` LIKE `hero_0`;
DROP TABLE IF EXISTS `hero_5`;
CREATE TABLE `hero_5` LIKE `hero_0`;
DROP TABLE IF EXISTS `hero_6`;
CREATE TABLE `hero_6` LIKE `hero_0`;
DROP TABLE IF EXISTS `hero_7`;
CREATE TABLE `hero_7` LIKE `hero_0`;
DROP TABLE IF EXISTS `hero_8`;
CREATE TABLE `hero_8` LIKE `hero_0`;
DROP TABLE IF EXISTS `hero_9`;
CREATE TABLE `hero_9` LIKE `hero_0`;

--
-- Table structure for table `heroequipdebris`
--

DROP TABLE IF EXISTS `heroequipdebris`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `heroequipdebris` (
  `userId` bigint(19) NOT NULL DEFAULT '0',
  `debris` varchar(6000) DEFAULT NULL,
  `cards` varchar(6000) DEFAULT NULL,
  PRIMARY KEY (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `heroexp`
--

DROP TABLE IF EXISTS `heroexp`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `heroexp` (
  `level` int(4) NOT NULL DEFAULT '0' COMMENT '武将等级',
  `exp` int(16) DEFAULT '0' COMMENT '升级所需经验',
  PRIMARY KEY (`level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `herofate`
--

DROP TABLE IF EXISTS `herofate`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `herofate` (
  `fateId` int(16) NOT NULL,
  `fateName` varchar(20) DEFAULT NULL,
  `fateDesc` varchar(255) DEFAULT NULL,
  `type` smallint(6) DEFAULT '0',
  `reqIds` varchar(500) DEFAULT NULL,
  `value` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`fateId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `heropub`
--

DROP TABLE IF EXISTS `heropub`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `heropub` (
  `id` int(16) NOT NULL DEFAULT '0',
  `type` smallint(6) DEFAULT '0',
  `vipLv` smallint(6) DEFAULT '0',
  `item` int(16) NOT NULL DEFAULT '0',
  `itemCount` int(16) NOT NULL DEFAULT '0',
  `itemCount10` int(16) NOT NULL DEFAULT '0',
  `cash` int(16) NOT NULL DEFAULT '0',
  `cash10` int(16) NOT NULL DEFAULT '0',
  `dropPackId` int(16) NOT NULL DEFAULT '0',
  `freePeriod` int(16) NOT NULL DEFAULT '0',
  `freeDropPackId` int(16) NOT NULL DEFAULT '0',
  `luckNum` varchar(500) DEFAULT NULL,
  `luckNum2` int(16) NOT NULL DEFAULT '0',
  `luckDropPackId` int(16) NOT NULL DEFAULT '0',
  `freetimes` int(16) NOT NULL DEFAULT '0',
  `firstDropPackId` int(16) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `heroskill`
--

DROP TABLE IF EXISTS `heroskill`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `heroskill` (
  `entId` int(16) NOT NULL,
  `skillName` varchar(40) DEFAULT NULL,
  `level` int(16) NOT NULL,
  `nextSkillId` int(16) NOT NULL,
  `skillDesc` varchar(400) DEFAULT NULL,
  `pic` varchar(40) DEFAULT NULL,
  `icon` varchar(40) DEFAULT NULL,
  `quality` int(16) NOT NULL DEFAULT '0',
  `ownSkill` smallint(6) DEFAULT '0',
  `skillType` smallint(6) DEFAULT '0',
  `firedAt` smallint(6) DEFAULT '1',
  `percent` smallint(6) DEFAULT '100',
  `roundPeriod` smallint(6) DEFAULT '1',
  `power` int(16) DEFAULT '0',
  `powerType` tinyint(4) DEFAULT '0',
  `morale` int(16) DEFAULT '0',
  `priority` int(16) DEFAULT '0',
  PRIMARY KEY (`entId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `heroskilleffect`
--

DROP TABLE IF EXISTS `heroskilleffect`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
DROP TABLE IF EXISTS `heroskilleffect`;
CREATE TABLE `heroskilleffect` (
  `effId` int(16) NOT NULL,
  `effPic` varchar(40) DEFAULT NULL,
  `groupId` int(16) DEFAULT '0',
  `priority` smallint(6) DEFAULT '1',
  `target` smallint(6) DEFAULT '1',
  `targetNum` smallint(6) DEFAULT '1',
  `targetArmy` int(16) DEFAULT '0',
  `targetCountryId` int(16) DEFAULT '0',
  `effType` smallint(6) NOT NULL DEFAULT '0',
  `effKey` varchar(40) NOT NULL,
  `round` int(16) DEFAULT '0',
  `para1` int(16) DEFAULT '0',
  `para2` varchar(500) DEFAULT NULL,
  `isShow` int(16) DEFAULT '0',
  `owner` smallint(6) DEFAULT '1',
  PRIMARY KEY (`effId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `heroskilleffectlimit`
--

DROP TABLE IF EXISTS `heroskilleffectlimit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `heroskilleffectlimit` (
  `id` int(16) NOT NULL,
  `effId` int(16) NOT NULL,
  `limitType` varchar(20) DEFAULT NULL,
  `para1` int(16) NOT NULL,
  `para2` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `heroskilleffectrela`
--

DROP TABLE IF EXISTS `heroskilleffectrela`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `heroskilleffectrela` (
  `skillId` int(16) NOT NULL,
  `effId` int(16) NOT NULL,
  PRIMARY KEY (`skillId`,`effId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `heroskilllimit`
--

DROP TABLE IF EXISTS `heroskilllimit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `heroskilllimit` (
  `id` int(16) NOT NULL,
  `skillId` int(16) NOT NULL,
  `limitType` varchar(20) DEFAULT NULL,
  `para1` int(16) NOT NULL,
  `para2` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `herostrength`
--

DROP TABLE IF EXISTS `herostrength`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `herostrength` (
  `id` int(16) NOT NULL DEFAULT '0',
  `level` smallint(6) NOT NULL DEFAULT '0',
  `nextLvevlId` int(16) NOT NULL DEFAULT '0',
  `addGrowing` smallint(6) NOT NULL DEFAULT '0',
  `itemId1` int(16) NOT NULL DEFAULT '0',
  `count1` smallint(6) NOT NULL DEFAULT '0',
  `itemId2` int(16) NOT NULL DEFAULT '0',
  `count2` smallint(6) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `idgen`
--

DROP TABLE IF EXISTS `idgen`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `idgen` (
  `idType` varchar(10) NOT NULL,
  `cacheCount` int(4) NOT NULL DEFAULT '100',
  `currValue` bigint(19) NOT NULL DEFAULT '1',
  PRIMARY KEY (`idType`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `item`
--

DROP TABLE IF EXISTS `item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `item` (
  `entId` int(16) NOT NULL,
  `itemName` varchar(40) NOT NULL,
  `itemDesc` varchar(500) DEFAULT NULL,
  `iconPath` varchar(100) DEFAULT NULL,
  `type` int(2) DEFAULT '0',
  `sumAble` int(2) DEFAULT '0',
  `bandAble` int(2) DEFAULT '0',
  `throwAble` int(2) DEFAULT '0',
  `useAble` int(2) DEFAULT '0',
  `useMaxNum` int(4) DEFAULT '0',
  `childType` int(2) DEFAULT '0',
  `level` int(2) DEFAULT '0',
  `color` int(2) DEFAULT '0',
  `missionId` int(16) DEFAULT '0',
  `time` int(4) DEFAULT '0',
  `userHasMaxNum` int(4) DEFAULT '0',
  `stackNum` int(16) DEFAULT '0',
  `exchangeId` int(16) DEFAULT '0',
  `notJoinPack` tinyint(2) DEFAULT '0',
  `sysHeroEntId` int(16) DEFAULT '0',
  
  `sellType` tinyint(2) DEFAULT '0',
  `sellPrice` int(16) DEFAULT '0',
  PRIMARY KEY (`entId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `itemexchange`
--

DROP TABLE IF EXISTS `itemexchange`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `itemexchange` (
  `exchangeId` int(16) NOT NULL,
  `needEntIds` varchar(1024) NOT NULL,
  `toEntIds` varchar(1024) NOT NULL,
  PRIMARY KEY (`exchangeId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jobcontext`
--

DROP TABLE IF EXISTS `jobcontext`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jobcontext` (
  `jobId` int(16) NOT NULL AUTO_INCREMENT,
  `jobGroupName` varchar(20) NOT NULL,
  `jobIdInGroup` varchar(40) NOT NULL,
  `jobExcuteTime` bigint(16) DEFAULT '0',
  `repeatRunRule` varchar(200) DEFAULT NULL,
  `serviceName` varchar(200) DEFAULT NULL,
  `methodName` varchar(50) DEFAULT NULL,
  `jobPara` blob,
  `jobType` int(2) DEFAULT '0',
  `dispatch` varchar(100) DEFAULT NULL,
  `runOnServer` int(2) DEFAULT '0',
  `runOnStart` int(2) DEFAULT '0',
  `errorDesc` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`jobId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jobcontext_err`
--

DROP TABLE IF EXISTS `jobcontext_err`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jobcontext_err` (
  `jobId` int(16) NOT NULL,
  `jobGroupName` varchar(20) NOT NULL,
  `jobIdInGroup` varchar(40) NOT NULL,
  `jobExcuteTime` bigint(16) DEFAULT '0',
  `repeatRunRule` varchar(200) DEFAULT NULL,
  `runOnStart` int(2) DEFAULT '0',
  `serviceName` varchar(200) DEFAULT NULL,
  `methodName` varchar(50) DEFAULT NULL,
  `jobPara` blob,
  `jobType` int(2) DEFAULT '0',
  `dispatch` varchar(100) DEFAULT NULL,
  `runOnServer` int(2) DEFAULT '0',
  `errorDesc` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`jobId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `lvparalimit`
--

DROP TABLE IF EXISTS `lvparalimit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `lvparalimit` (
  `usrLv` int(16) NOT NULL,
  `exp` int(16) DEFAULT '0',
  `tipTitle` varchar(40) DEFAULT NULL,
  `tipContent` varchar(500) DEFAULT NULL,
  `heroNumLimit` int(16) DEFAULT '0' COMMENT '武将数量上限',
  `heroLvLimit` int(16) DEFAULT '0' COMMENT '武将等级上限',
  `actionPointLimit` int(16) DEFAULT '0' COMMENT '行动力上限',
  `hpPointLimit` int(16) DEFAULT '0' COMMENT '体力上限',
  `perHpPoint` int(16) DEFAULT '0' COMMENT '每15分钟增加的体力值',
  `perActPoint` int(16) DEFAULT '0' COMMENT '每15分钟增加的行动力值',
  PRIMARY KEY (`usrLv`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='君主等级与参数上限对应表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `message_0`
--

DROP TABLE IF EXISTS `message_0`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `message_0` (
  `messageId` int(16) NOT NULL AUTO_INCREMENT,
  `sendUserId` bigint(19) NOT NULL,
  `receiveUserId` bigint(19) NOT NULL,
  `sendDttm` timestamp NULL DEFAULT NULL,
  `readDttm` timestamp NULL DEFAULT NULL,
  `comment` varchar(4000) DEFAULT NULL,
  `title` varchar(100) DEFAULT NULL,
  `status` int(2) DEFAULT '0',
  `messageType` tinyint(4) DEFAULT '0',
  `entityId0` int(16) DEFAULT '0' COMMENT '道具id',
  `itemNum0` int(16) DEFAULT '0',
  `status0` tinyint(4) DEFAULT '0',
  `entityId1` int(11) DEFAULT NULL,
  `itemNum1` int(16) DEFAULT '0',
  `status1` tinyint(4) DEFAULT '0',
  `entityId2` int(11) DEFAULT NULL,
  `itemNum2` int(16) DEFAULT '0',
  `status2` tinyint(4) DEFAULT '0',
  `entityId3` int(11) DEFAULT NULL,
  `itemNum3` int(16) DEFAULT '0',
  `status3` tinyint(4) DEFAULT '0',
  `entityId4` int(11) DEFAULT NULL,
  `itemNum4` int(16) DEFAULT '0',
  `status4` tinyint(4) DEFAULT '0',
  `entityId5` int(11) DEFAULT NULL,
  `itemNum5` int(16) DEFAULT '0',
  `status5` tinyint(4) DEFAULT '0',
  `map` blob,
  `isGift` tinyint(4) DEFAULT '0',
  PRIMARY KEY (`messageId`),
  KEY `index_message_recUserId` (`receiveUserId`),
  KEY `idx_message_status0` (`status0`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `message_1`
--

DROP TABLE IF EXISTS `message_1`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `message_1` (
  `messageId` int(16) NOT NULL AUTO_INCREMENT,
  `sendUserId` bigint(19) NOT NULL,
  `receiveUserId` bigint(19) NOT NULL,
  `sendDttm` timestamp NULL DEFAULT NULL,
  `readDttm` timestamp NULL DEFAULT NULL,
  `comment` varchar(4000) DEFAULT NULL,
  `title` varchar(100) DEFAULT NULL,
  `status` int(2) DEFAULT '0',
  `messageType` tinyint(4) DEFAULT '0',
  `entityId0` int(16) DEFAULT '0' COMMENT '道具id',
  `itemNum0` int(16) DEFAULT '0',
  `status0` tinyint(4) DEFAULT '0',
  `entityId1` int(11) DEFAULT NULL,
  `itemNum1` int(16) DEFAULT '0',
  `status1` tinyint(4) DEFAULT '0',
  `entityId2` int(11) DEFAULT NULL,
  `itemNum2` int(16) DEFAULT '0',
  `status2` tinyint(4) DEFAULT '0',
  `entityId3` int(11) DEFAULT NULL,
  `itemNum3` int(16) DEFAULT '0',
  `status3` tinyint(4) DEFAULT '0',
  `entityId4` int(11) DEFAULT NULL,
  `itemNum4` int(16) DEFAULT '0',
  `status4` tinyint(4) DEFAULT '0',
  `entityId5` int(11) DEFAULT NULL,
  `itemNum5` int(16) DEFAULT '0',
  `status5` tinyint(4) DEFAULT '0',
  `map` blob,
  `isGift` tinyint(4) DEFAULT '0',
  PRIMARY KEY (`messageId`),
  KEY `index_message_recUserId` (`receiveUserId`),
  KEY `idx_message_status0` (`status0`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `message_2`
--

DROP TABLE IF EXISTS `message_2`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `message_2` (
  `messageId` int(16) NOT NULL AUTO_INCREMENT,
  `sendUserId` bigint(19) NOT NULL,
  `receiveUserId` bigint(19) NOT NULL,
  `sendDttm` timestamp NULL DEFAULT NULL,
  `readDttm` timestamp NULL DEFAULT NULL,
  `comment` varchar(4000) DEFAULT NULL,
  `title` varchar(100) DEFAULT NULL,
  `status` int(2) DEFAULT '0',
  `messageType` tinyint(4) DEFAULT '0',
  `entityId0` int(16) DEFAULT '0' COMMENT '道具id',
  `itemNum0` int(16) DEFAULT '0',
  `status0` tinyint(4) DEFAULT '0',
  `entityId1` int(11) DEFAULT NULL,
  `itemNum1` int(16) DEFAULT '0',
  `status1` tinyint(4) DEFAULT '0',
  `entityId2` int(11) DEFAULT NULL,
  `itemNum2` int(16) DEFAULT '0',
  `status2` tinyint(4) DEFAULT '0',
  `entityId3` int(11) DEFAULT NULL,
  `itemNum3` int(16) DEFAULT '0',
  `status3` tinyint(4) DEFAULT '0',
  `entityId4` int(11) DEFAULT NULL,
  `itemNum4` int(16) DEFAULT '0',
  `status4` tinyint(4) DEFAULT '0',
  `entityId5` int(11) DEFAULT NULL,
  `itemNum5` int(16) DEFAULT '0',
  `status5` tinyint(4) DEFAULT '0',
  `map` blob,
  `isGift` tinyint(4) DEFAULT '0',
  PRIMARY KEY (`messageId`),
  KEY `index_message_recUserId` (`receiveUserId`),
  KEY `idx_message_status0` (`status0`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `message_3`
--

DROP TABLE IF EXISTS `message_3`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `message_3` (
  `messageId` int(16) NOT NULL AUTO_INCREMENT,
  `sendUserId` bigint(19) NOT NULL,
  `receiveUserId` bigint(19) NOT NULL,
  `sendDttm` timestamp NULL DEFAULT NULL,
  `readDttm` timestamp NULL DEFAULT NULL,
  `comment` varchar(4000) DEFAULT NULL,
  `title` varchar(100) DEFAULT NULL,
  `status` int(2) DEFAULT '0',
  `messageType` tinyint(4) DEFAULT '0',
  `entityId0` int(16) DEFAULT '0' COMMENT '道具id',
  `itemNum0` int(16) DEFAULT '0',
  `status0` tinyint(4) DEFAULT '0',
  `entityId1` int(11) DEFAULT NULL,
  `itemNum1` int(16) DEFAULT '0',
  `status1` tinyint(4) DEFAULT '0',
  `entityId2` int(11) DEFAULT NULL,
  `itemNum2` int(16) DEFAULT '0',
  `status2` tinyint(4) DEFAULT '0',
  `entityId3` int(11) DEFAULT NULL,
  `itemNum3` int(16) DEFAULT '0',
  `status3` tinyint(4) DEFAULT '0',
  `entityId4` int(11) DEFAULT NULL,
  `itemNum4` int(16) DEFAULT '0',
  `status4` tinyint(4) DEFAULT '0',
  `entityId5` int(11) DEFAULT NULL,
  `itemNum5` int(16) DEFAULT '0',
  `status5` tinyint(4) DEFAULT '0',
  `map` blob,
  `isGift` tinyint(4) DEFAULT '0',
  PRIMARY KEY (`messageId`),
  KEY `index_message_recUserId` (`receiveUserId`),
  KEY `idx_message_status0` (`status0`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `message_4`
--

DROP TABLE IF EXISTS `message_4`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `message_4` (
  `messageId` int(16) NOT NULL AUTO_INCREMENT,
  `sendUserId` bigint(19) NOT NULL,
  `receiveUserId` bigint(19) NOT NULL,
  `sendDttm` timestamp NULL DEFAULT NULL,
  `readDttm` timestamp NULL DEFAULT NULL,
  `comment` varchar(4000) DEFAULT NULL,
  `title` varchar(100) DEFAULT NULL,
  `status` int(2) DEFAULT '0',
  `messageType` tinyint(4) DEFAULT '0',
  `entityId0` int(16) DEFAULT '0' COMMENT '道具id',
  `itemNum0` int(16) DEFAULT '0',
  `status0` tinyint(4) DEFAULT '0',
  `entityId1` int(11) DEFAULT NULL,
  `itemNum1` int(16) DEFAULT '0',
  `status1` tinyint(4) DEFAULT '0',
  `entityId2` int(11) DEFAULT NULL,
  `itemNum2` int(16) DEFAULT '0',
  `status2` tinyint(4) DEFAULT '0',
  `entityId3` int(11) DEFAULT NULL,
  `itemNum3` int(16) DEFAULT '0',
  `status3` tinyint(4) DEFAULT '0',
  `entityId4` int(11) DEFAULT NULL,
  `itemNum4` int(16) DEFAULT '0',
  `status4` tinyint(4) DEFAULT '0',
  `entityId5` int(11) DEFAULT NULL,
  `itemNum5` int(16) DEFAULT '0',
  `status5` tinyint(4) DEFAULT '0',
  `map` blob,
  `isGift` tinyint(4) DEFAULT '0',
  PRIMARY KEY (`messageId`),
  KEY `index_message_recUserId` (`receiveUserId`),
  KEY `idx_message_status0` (`status0`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `message_5`
--

DROP TABLE IF EXISTS `message_5`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `message_5` (
  `messageId` int(16) NOT NULL AUTO_INCREMENT,
  `sendUserId` bigint(19) NOT NULL,
  `receiveUserId` bigint(19) NOT NULL,
  `sendDttm` timestamp NULL DEFAULT NULL,
  `readDttm` timestamp NULL DEFAULT NULL,
  `comment` varchar(4000) DEFAULT NULL,
  `title` varchar(100) DEFAULT NULL,
  `status` int(2) DEFAULT '0',
  `messageType` tinyint(4) DEFAULT '0',
  `entityId0` int(16) DEFAULT '0' COMMENT '道具id',
  `itemNum0` int(16) DEFAULT '0',
  `status0` tinyint(4) DEFAULT '0',
  `entityId1` int(11) DEFAULT NULL,
  `itemNum1` int(16) DEFAULT '0',
  `status1` tinyint(4) DEFAULT '0',
  `entityId2` int(11) DEFAULT NULL,
  `itemNum2` int(16) DEFAULT '0',
  `status2` tinyint(4) DEFAULT '0',
  `entityId3` int(11) DEFAULT NULL,
  `itemNum3` int(16) DEFAULT '0',
  `status3` tinyint(4) DEFAULT '0',
  `entityId4` int(11) DEFAULT NULL,
  `itemNum4` int(16) DEFAULT '0',
  `status4` tinyint(4) DEFAULT '0',
  `entityId5` int(11) DEFAULT NULL,
  `itemNum5` int(16) DEFAULT '0',
  `status5` tinyint(4) DEFAULT '0',
  `map` blob,
  `isGift` tinyint(4) DEFAULT '0',
  PRIMARY KEY (`messageId`),
  KEY `index_message_recUserId` (`receiveUserId`),
  KEY `idx_message_status0` (`status0`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `message_6`
--

DROP TABLE IF EXISTS `message_6`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `message_6` (
  `messageId` int(16) NOT NULL AUTO_INCREMENT,
  `sendUserId` bigint(19) NOT NULL,
  `receiveUserId` bigint(19) NOT NULL,
  `sendDttm` timestamp NULL DEFAULT NULL,
  `readDttm` timestamp NULL DEFAULT NULL,
  `comment` varchar(4000) DEFAULT NULL,
  `title` varchar(100) DEFAULT NULL,
  `status` int(2) DEFAULT '0',
  `messageType` tinyint(4) DEFAULT '0',
  `entityId0` int(16) DEFAULT '0' COMMENT '道具id',
  `itemNum0` int(16) DEFAULT '0',
  `status0` tinyint(4) DEFAULT '0',
  `entityId1` int(11) DEFAULT NULL,
  `itemNum1` int(16) DEFAULT '0',
  `status1` tinyint(4) DEFAULT '0',
  `entityId2` int(11) DEFAULT NULL,
  `itemNum2` int(16) DEFAULT '0',
  `status2` tinyint(4) DEFAULT '0',
  `entityId3` int(11) DEFAULT NULL,
  `itemNum3` int(16) DEFAULT '0',
  `status3` tinyint(4) DEFAULT '0',
  `entityId4` int(11) DEFAULT NULL,
  `itemNum4` int(16) DEFAULT '0',
  `status4` tinyint(4) DEFAULT '0',
  `entityId5` int(11) DEFAULT NULL,
  `itemNum5` int(16) DEFAULT '0',
  `status5` tinyint(4) DEFAULT '0',
  `map` blob,
  `isGift` tinyint(4) DEFAULT '0',
  PRIMARY KEY (`messageId`),
  KEY `index_message_recUserId` (`receiveUserId`),
  KEY `idx_message_status0` (`status0`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `message_7`
--

DROP TABLE IF EXISTS `message_7`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `message_7` (
  `messageId` int(16) NOT NULL AUTO_INCREMENT,
  `sendUserId` bigint(19) NOT NULL,
  `receiveUserId` bigint(19) NOT NULL,
  `sendDttm` timestamp NULL DEFAULT NULL,
  `readDttm` timestamp NULL DEFAULT NULL,
  `comment` varchar(4000) DEFAULT NULL,
  `title` varchar(100) DEFAULT NULL,
  `status` int(2) DEFAULT '0',
  `messageType` tinyint(4) DEFAULT '0',
  `entityId0` int(16) DEFAULT '0' COMMENT '道具id',
  `itemNum0` int(16) DEFAULT '0',
  `status0` tinyint(4) DEFAULT '0',
  `entityId1` int(11) DEFAULT NULL,
  `itemNum1` int(16) DEFAULT '0',
  `status1` tinyint(4) DEFAULT '0',
  `entityId2` int(11) DEFAULT NULL,
  `itemNum2` int(16) DEFAULT '0',
  `status2` tinyint(4) DEFAULT '0',
  `entityId3` int(11) DEFAULT NULL,
  `itemNum3` int(16) DEFAULT '0',
  `status3` tinyint(4) DEFAULT '0',
  `entityId4` int(11) DEFAULT NULL,
  `itemNum4` int(16) DEFAULT '0',
  `status4` tinyint(4) DEFAULT '0',
  `entityId5` int(11) DEFAULT NULL,
  `itemNum5` int(16) DEFAULT '0',
  `status5` tinyint(4) DEFAULT '0',
  `map` blob,
  `isGift` tinyint(4) DEFAULT '0',
  PRIMARY KEY (`messageId`),
  KEY `index_message_recUserId` (`receiveUserId`),
  KEY `idx_message_status0` (`status0`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `message_8`
--

DROP TABLE IF EXISTS `message_8`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `message_8` (
  `messageId` int(16) NOT NULL AUTO_INCREMENT,
  `sendUserId` bigint(19) NOT NULL,
  `receiveUserId` bigint(19) NOT NULL,
  `sendDttm` timestamp NULL DEFAULT NULL,
  `readDttm` timestamp NULL DEFAULT NULL,
  `comment` varchar(4000) DEFAULT NULL,
  `title` varchar(100) DEFAULT NULL,
  `status` int(2) DEFAULT '0',
  `messageType` tinyint(4) DEFAULT '0',
  `entityId0` int(16) DEFAULT '0' COMMENT '道具id',
  `itemNum0` int(16) DEFAULT '0',
  `status0` tinyint(4) DEFAULT '0',
  `entityId1` int(11) DEFAULT NULL,
  `itemNum1` int(16) DEFAULT '0',
  `status1` tinyint(4) DEFAULT '0',
  `entityId2` int(11) DEFAULT NULL,
  `itemNum2` int(16) DEFAULT '0',
  `status2` tinyint(4) DEFAULT '0',
  `entityId3` int(11) DEFAULT NULL,
  `itemNum3` int(16) DEFAULT '0',
  `status3` tinyint(4) DEFAULT '0',
  `entityId4` int(11) DEFAULT NULL,
  `itemNum4` int(16) DEFAULT '0',
  `status4` tinyint(4) DEFAULT '0',
  `entityId5` int(11) DEFAULT NULL,
  `itemNum5` int(16) DEFAULT '0',
  `status5` tinyint(4) DEFAULT '0',
  `map` blob,
  `isGift` tinyint(4) DEFAULT '0',
  PRIMARY KEY (`messageId`),
  KEY `index_message_recUserId` (`receiveUserId`),
  KEY `idx_message_status0` (`status0`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `message_9`
--

DROP TABLE IF EXISTS `message_9`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `message_9` (
  `messageId` int(16) NOT NULL AUTO_INCREMENT,
  `sendUserId` bigint(19) NOT NULL,
  `receiveUserId` bigint(19) NOT NULL,
  `sendDttm` timestamp NULL DEFAULT NULL,
  `readDttm` timestamp NULL DEFAULT NULL,
  `comment` varchar(4000) DEFAULT NULL,
  `title` varchar(100) DEFAULT NULL,
  `status` int(2) DEFAULT '0',
  `messageType` tinyint(4) DEFAULT '0',
  `entityId0` int(16) DEFAULT '0' COMMENT '道具id',
  `itemNum0` int(16) DEFAULT '0',
  `status0` tinyint(4) DEFAULT '0',
  `entityId1` int(11) DEFAULT NULL,
  `itemNum1` int(16) DEFAULT '0',
  `status1` tinyint(4) DEFAULT '0',
  `entityId2` int(11) DEFAULT NULL,
  `itemNum2` int(16) DEFAULT '0',
  `status2` tinyint(4) DEFAULT '0',
  `entityId3` int(11) DEFAULT NULL,
  `itemNum3` int(16) DEFAULT '0',
  `status3` tinyint(4) DEFAULT '0',
  `entityId4` int(11) DEFAULT NULL,
  `itemNum4` int(16) DEFAULT '0',
  `status4` tinyint(4) DEFAULT '0',
  `entityId5` int(11) DEFAULT NULL,
  `itemNum5` int(16) DEFAULT '0',
  `status5` tinyint(4) DEFAULT '0',
  `map` blob,
  `isGift` tinyint(4) DEFAULT '0',
  PRIMARY KEY (`messageId`),
  KEY `index_message_recUserId` (`receiveUserId`),
  KEY `idx_message_status0` (`status0`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mysticshop`
--

DROP TABLE IF EXISTS `mysticshop`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mysticshop` (
  `shopId` int(11) NOT NULL DEFAULT '0',
  `vipFreeCount` varchar(100) DEFAULT NULL,
  `vipdaytime` varchar(100) DEFAULT NULL,
  `cashCost` varchar(100) DEFAULT NULL,
  `num` int(11) DEFAULT NULL,
  `itemId` int(11) DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  `freashTime` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`shopId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mysticshopitem`
--

DROP TABLE IF EXISTS `mysticshopitem`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mysticshopitem` (
  `shopItemId` int(11) NOT NULL DEFAULT '0',
  `type` int(11) DEFAULT NULL,
  `level` varchar(20) DEFAULT NULL,
  `itemId` int(11) DEFAULT NULL,
  `itemNum` int(11) DEFAULT NULL,
  `costType` int(11) DEFAULT NULL,
  `costNum` int(11) DEFAULT NULL,
  `wight1` int(11) DEFAULT NULL,
  `wightNum` int(11) DEFAULT NULL,
  `wight2` int(11) DEFAULT NULL,
  PRIMARY KEY (`shopItemId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `npcdefine`
--

DROP TABLE IF EXISTS `npcdefine`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `npcdefine` (
  `npcId` int(16) NOT NULL DEFAULT '0',
  `level` int(4) DEFAULT '0',
  `npcType` int(2) DEFAULT '0',
  `npcName` varchar(40) DEFAULT NULL,
  `npcDesc` varchar(200) DEFAULT NULL,
  `iconPath` varchar(40) DEFAULT NULL,
  `heroId1` int(16) DEFAULT '0',
  `heroId2` int(16) DEFAULT '0',
  `heroId3` int(16) DEFAULT '0',
  `heroId4` int(16) DEFAULT '0',
  `heroId5` int(16) DEFAULT '0',
  `heroId6` int(16) DEFAULT '0',
  `combatPower` int(16) DEFAULT '0',
  `leaderPos` smallint(6) DEFAULT '0',
  PRIMARY KEY (`npcId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `npchero`
--

DROP TABLE IF EXISTS `npchero`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `npchero` (
  `heroId` int(16) NOT NULL DEFAULT '0',
  `name` varchar(16) DEFAULT NULL,
  `sysHeroId` int(16) NOT NULL DEFAULT '0' COMMENT '系统武将实体id',
  `level` int(4) DEFAULT '0',
  PRIMARY KEY (`heroId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `party`
--

DROP TABLE IF EXISTS `party`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `party` (
  `entId` int(16) NOT NULL,
  `partyName` varchar(100) DEFAULT NULL,
  `iconPath` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`entId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `relifelimit`
--

DROP TABLE IF EXISTS `relifelimit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `relifelimit` (
  `id` int(16) NOT NULL DEFAULT '0',
  `relifeNum` smallint(6) NOT NULL DEFAULT '0' COMMENT '转生次数',
  `sysHeroId` int(16) NOT NULL DEFAULT '0',
  `relifeDesc` varchar(500) DEFAULT NULL,
  `addGrowing` smallint(6) NOT NULL DEFAULT '0' COMMENT '进阶后增加的成长值',
  `heroCardNum` int(16) DEFAULT '0' COMMENT '需要的武将卡数量',
  `attrValue` varchar(500) DEFAULT NULL,
  `heroStrengthId` int(16) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `index_relifelimit_sysHeroId` (`sysHeroId`,`relifeNum`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `resource`
--

DROP TABLE IF EXISTS `resource`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `resource` (
  `entId` int(16) NOT NULL,
  `resName` varchar(40) NOT NULL,
  `resDesc` varchar(500) DEFAULT NULL,
  `iconPath` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`entId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `serverinfo`
--

DROP TABLE IF EXISTS `serverinfo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `serverinfo` (
  `serverId` varchar(30) NOT NULL,
  `dttm` timestamp NULL DEFAULT NULL,
  `serverName` varchar(200) DEFAULT NULL,
  `interServer` varchar(200) DEFAULT NULL,
  `interServer2` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`serverId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `syshero`
--

DROP TABLE IF EXISTS `syshero`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `syshero` (
  `entId` int(16) NOT NULL,
  `level` int(4) DEFAULT '0',
  `gen` int(2) DEFAULT '0',
  `name` varchar(16) DEFAULT NULL,
  `picPath` varchar(40) DEFAULT NULL,
  `countryId` int(2) DEFAULT '0',
  `attackType` int(2) DEFAULT NULL,
  `evaluate` tinyint(4) NOT NULL DEFAULT '1',
  `armyEntId` int(16) DEFAULT '0',
  `heroCardId` int(16) DEFAULT '0',
  `magicAttack` int(16) DEFAULT '0',
  `physicalAttack` int(16) DEFAULT '0',
  `magicDefend` int(16) DEFAULT '0',
  `physicalDefend` int(16) DEFAULT '0',
  `initHp` int(16) DEFAULT '0',
  `initMorale` int(16) DEFAULT '0',
  `initHit` int(16) DEFAULT '0',
  `initDodge` int(16) DEFAULT '0',
  `critDec` int(16) DEFAULT '0',
  `critAdd` int(16) DEFAULT '0',
  `critDamageAdd` int(16) DEFAULT '0',
  `critDamageDec` int(16) DEFAULT '0',
  `skillId1` int(16) DEFAULT '0',
  `skillId2` int(16) DEFAULT '0',
  `skillId3` int(16) DEFAULT '0',
  `skillId4` int(16) DEFAULT '0',
  `skillId5` int(16) DEFAULT '0',
  `skillId6` int(16) DEFAULT '0',
  `attackMorale` int(16) DEFAULT '0',
  `defendMorale` int(16) DEFAULT '0',
  `heroDesc` varchar(200) DEFAULT '',
  `heroFateIds` varchar(500) DEFAULT '',
  `growing` int(16) DEFAULT '0',
  `soulEntId` int(16) DEFAULT NULL,
  `soulNum` int(32) DEFAULT NULL,
  PRIMARY KEY (`entId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `syspara`
--

DROP TABLE IF EXISTS `syspara`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `syspara` (
  `paraId` varchar(40) NOT NULL,
  `paraGroup` varchar(40) NOT NULL,
  `paraType` varchar(40) NOT NULL,
  `paraValue` varchar(10000) NOT NULL,
  `paraName` varchar(40) NOT NULL,
  `paraDesc` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`paraId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `syspara_gm`
--

DROP TABLE IF EXISTS `syspara_gm`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `syspara_gm` (
  `paraId` varchar(40) NOT NULL,
  `paraGroup` varchar(40) NOT NULL,
  `paraType` varchar(40) NOT NULL,
  `paraValue` varchar(10000) NOT NULL,
  `paraName` varchar(40) NOT NULL,
  `paraDesc` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`paraId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `technology`
--

DROP TABLE IF EXISTS `technology`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `technology` (
  `entId` int(16) NOT NULL,
  `techType` varchar(40) DEFAULT NULL,
  `techName` varchar(20) DEFAULT NULL,
  `techDesc` varchar(100) DEFAULT NULL,
  `iconPath` varchar(100) DEFAULT NULL,
  `maxLevel` int(4) DEFAULT '0',
  `orderIndex` int(2) DEFAULT '0',
  `target` tinyint(4) DEFAULT '0',
  PRIMARY KEY (`entId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `treasury_0`
--

DROP TABLE IF EXISTS `treasury_0`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `treasury_0` (
  `id` bigint(16) NOT NULL,
  `userId` bigint(19) NOT NULL,
  `entId` int(16) NOT NULL,
  `entType` int(2) DEFAULT '0',
  `itemCount` int(4) DEFAULT '0',
  `useCount` int(4) DEFAULT '0',
  `band` int(2) DEFAULT '0',
  `equip` bigint(16) DEFAULT '0',
  `throwAble` int(2) DEFAULT '0',
  `childType` int(4) DEFAULT '0',
  `isGift` int(2) DEFAULT '0',
  `existEndTime` timestamp NULL DEFAULT NULL,
  `level` int(2) DEFAULT '0',
  `holeNum` int(2) DEFAULT '0',
  `gemIds` varchar(200) DEFAULT NULL,
  `randomProp` varchar(400) DEFAULT NULL,
  `randomPropTmp` varchar(400) DEFAULT NULL,
  `spiritLv` int(16) DEFAULT '0',
  `gemExp` int(16) DEFAULT '0',
  `isLock` int(2) DEFAULT '0',
  `specialAttr` varchar(400) DEFAULT '',
  PRIMARY KEY (`id`),
  KEY `idx_treasury_userId_entId` (`userId`,`entId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `treasury_1`
--

DROP TABLE IF EXISTS `treasury_1`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `treasury_1` (
  `id` bigint(16) NOT NULL,
  `userId` bigint(19) NOT NULL,
  `entId` int(16) NOT NULL,
  `entType` int(2) DEFAULT '0',
  `itemCount` int(4) DEFAULT '0',
  `useCount` int(4) DEFAULT '0',
  `band` int(2) DEFAULT '0',
  `equip` bigint(16) DEFAULT '0',
  `throwAble` int(2) DEFAULT '0',
  `childType` int(4) DEFAULT '0',
  `isGift` int(2) DEFAULT '0',
  `existEndTime` timestamp NULL DEFAULT NULL,
  `level` int(2) DEFAULT '0',
  `holeNum` int(2) DEFAULT '0',
  `gemIds` varchar(200) DEFAULT NULL,
  `randomProp` varchar(400) DEFAULT NULL,
  `randomPropTmp` varchar(400) DEFAULT NULL,
  `spiritLv` int(16) DEFAULT '0',
  `gemExp` int(16) DEFAULT '0',
  `isLock` int(2) DEFAULT '0',
  `specialAttr` varchar(400) DEFAULT '',
  PRIMARY KEY (`id`),
  KEY `idx_treasury_userId_entId` (`userId`,`entId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `treasury_2`
--

DROP TABLE IF EXISTS `treasury_2`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `treasury_2` (
  `id` bigint(16) NOT NULL,
  `userId` bigint(19) NOT NULL,
  `entId` int(16) NOT NULL,
  `entType` int(2) DEFAULT '0',
  `itemCount` int(4) DEFAULT '0',
  `useCount` int(4) DEFAULT '0',
  `band` int(2) DEFAULT '0',
  `equip` bigint(16) DEFAULT '0',
  `throwAble` int(2) DEFAULT '0',
  `childType` int(4) DEFAULT '0',
  `isGift` int(2) DEFAULT '0',
  `existEndTime` timestamp NULL DEFAULT NULL,
  `level` int(2) DEFAULT '0',
  `holeNum` int(2) DEFAULT '0',
  `gemIds` varchar(200) DEFAULT NULL,
  `randomProp` varchar(400) DEFAULT NULL,
  `randomPropTmp` varchar(400) DEFAULT NULL,
  `spiritLv` int(16) DEFAULT '0',
  `gemExp` int(16) DEFAULT '0',
  `isLock` int(2) DEFAULT '0',
  `specialAttr` varchar(400) DEFAULT '',
  PRIMARY KEY (`id`),
  KEY `idx_treasury_userId_entId` (`userId`,`entId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `treasury_3`
--

DROP TABLE IF EXISTS `treasury_3`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `treasury_3` (
  `id` bigint(16) NOT NULL,
  `userId` bigint(19) NOT NULL,
  `entId` int(16) NOT NULL,
  `entType` int(2) DEFAULT '0',
  `itemCount` int(4) DEFAULT '0',
  `useCount` int(4) DEFAULT '0',
  `band` int(2) DEFAULT '0',
  `equip` bigint(16) DEFAULT '0',
  `throwAble` int(2) DEFAULT '0',
  `childType` int(4) DEFAULT '0',
  `isGift` int(2) DEFAULT '0',
  `existEndTime` timestamp NULL DEFAULT NULL,
  `level` int(2) DEFAULT '0',
  `holeNum` int(2) DEFAULT '0',
  `gemIds` varchar(200) DEFAULT NULL,
  `randomProp` varchar(400) DEFAULT NULL,
  `randomPropTmp` varchar(400) DEFAULT NULL,
  `spiritLv` int(16) DEFAULT '0',
  `gemExp` int(16) DEFAULT '0',
  `isLock` int(2) DEFAULT '0',
  `specialAttr` varchar(400) DEFAULT '',
  PRIMARY KEY (`id`),
  KEY `idx_treasury_userId_entId` (`userId`,`entId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `treasury_4`
--

DROP TABLE IF EXISTS `treasury_4`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `treasury_4` (
  `id` bigint(16) NOT NULL,
  `userId` bigint(19) NOT NULL,
  `entId` int(16) NOT NULL,
  `entType` int(2) DEFAULT '0',
  `itemCount` int(4) DEFAULT '0',
  `useCount` int(4) DEFAULT '0',
  `band` int(2) DEFAULT '0',
  `equip` bigint(16) DEFAULT '0',
  `throwAble` int(2) DEFAULT '0',
  `childType` int(4) DEFAULT '0',
  `isGift` int(2) DEFAULT '0',
  `existEndTime` timestamp NULL DEFAULT NULL,
  `level` int(2) DEFAULT '0',
  `holeNum` int(2) DEFAULT '0',
  `gemIds` varchar(200) DEFAULT NULL,
  `randomProp` varchar(400) DEFAULT NULL,
  `randomPropTmp` varchar(400) DEFAULT NULL,
  `spiritLv` int(16) DEFAULT '0',
  `gemExp` int(16) DEFAULT '0',
  `isLock` int(2) DEFAULT '0',
  `specialAttr` varchar(400) DEFAULT '',
  PRIMARY KEY (`id`),
  KEY `idx_treasury_userId_entId` (`userId`,`entId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `treasury_5`
--

DROP TABLE IF EXISTS `treasury_5`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `treasury_5` (
  `id` bigint(16) NOT NULL,
  `userId` bigint(19) NOT NULL,
  `entId` int(16) NOT NULL,
  `entType` int(2) DEFAULT '0',
  `itemCount` int(4) DEFAULT '0',
  `useCount` int(4) DEFAULT '0',
  `band` int(2) DEFAULT '0',
  `equip` bigint(16) DEFAULT '0',
  `throwAble` int(2) DEFAULT '0',
  `childType` int(4) DEFAULT '0',
  `isGift` int(2) DEFAULT '0',
  `existEndTime` timestamp NULL DEFAULT NULL,
  `level` int(2) DEFAULT '0',
  `holeNum` int(2) DEFAULT '0',
  `gemIds` varchar(200) DEFAULT NULL,
  `randomProp` varchar(400) DEFAULT NULL,
  `randomPropTmp` varchar(400) DEFAULT NULL,
  `spiritLv` int(16) DEFAULT '0',
  `gemExp` int(16) DEFAULT '0',
  `isLock` int(2) DEFAULT '0',
  `specialAttr` varchar(400) DEFAULT '',
  PRIMARY KEY (`id`),
  KEY `idx_treasury_userId_entId` (`userId`,`entId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `treasury_6`
--

DROP TABLE IF EXISTS `treasury_6`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `treasury_6` (
  `id` bigint(16) NOT NULL,
  `userId` bigint(19) NOT NULL,
  `entId` int(16) NOT NULL,
  `entType` int(2) DEFAULT '0',
  `itemCount` int(4) DEFAULT '0',
  `useCount` int(4) DEFAULT '0',
  `band` int(2) DEFAULT '0',
  `equip` bigint(16) DEFAULT '0',
  `throwAble` int(2) DEFAULT '0',
  `childType` int(4) DEFAULT '0',
  `isGift` int(2) DEFAULT '0',
  `existEndTime` timestamp NULL DEFAULT NULL,
  `level` int(2) DEFAULT '0',
  `holeNum` int(2) DEFAULT '0',
  `gemIds` varchar(200) DEFAULT NULL,
  `randomProp` varchar(400) DEFAULT NULL,
  `randomPropTmp` varchar(400) DEFAULT NULL,
  `spiritLv` int(16) DEFAULT '0',
  `gemExp` int(16) DEFAULT '0',
  `isLock` int(2) DEFAULT '0',
  `specialAttr` varchar(400) DEFAULT '',
  PRIMARY KEY (`id`),
  KEY `idx_treasury_userId_entId` (`userId`,`entId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `treasury_7`
--

DROP TABLE IF EXISTS `treasury_7`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `treasury_7` (
  `id` bigint(16) NOT NULL,
  `userId` bigint(19) NOT NULL,
  `entId` int(16) NOT NULL,
  `entType` int(2) DEFAULT '0',
  `itemCount` int(4) DEFAULT '0',
  `useCount` int(4) DEFAULT '0',
  `band` int(2) DEFAULT '0',
  `equip` bigint(16) DEFAULT '0',
  `throwAble` int(2) DEFAULT '0',
  `childType` int(4) DEFAULT '0',
  `isGift` int(2) DEFAULT '0',
  `existEndTime` timestamp NULL DEFAULT NULL,
  `level` int(2) DEFAULT '0',
  `holeNum` int(2) DEFAULT '0',
  `gemIds` varchar(200) DEFAULT NULL,
  `randomProp` varchar(400) DEFAULT NULL,
  `randomPropTmp` varchar(400) DEFAULT NULL,
  `spiritLv` int(16) DEFAULT '0',
  `gemExp` int(16) DEFAULT '0',
  `isLock` int(2) DEFAULT '0',
  `specialAttr` varchar(400) DEFAULT '',
  PRIMARY KEY (`id`),
  KEY `idx_treasury_userId_entId` (`userId`,`entId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `treasury_8`
--

DROP TABLE IF EXISTS `treasury_8`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `treasury_8` (
  `id` bigint(16) NOT NULL,
  `userId` bigint(19) NOT NULL,
  `entId` int(16) NOT NULL,
  `entType` int(2) DEFAULT '0',
  `itemCount` int(4) DEFAULT '0',
  `useCount` int(4) DEFAULT '0',
  `band` int(2) DEFAULT '0',
  `equip` bigint(16) DEFAULT '0',
  `throwAble` int(2) DEFAULT '0',
  `childType` int(4) DEFAULT '0',
  `isGift` int(2) DEFAULT '0',
  `existEndTime` timestamp NULL DEFAULT NULL,
  `level` int(2) DEFAULT '0',
  `holeNum` int(2) DEFAULT '0',
  `gemIds` varchar(200) DEFAULT NULL,
  `randomProp` varchar(400) DEFAULT NULL,
  `randomPropTmp` varchar(400) DEFAULT NULL,
  `spiritLv` int(16) DEFAULT '0',
  `gemExp` int(16) DEFAULT '0',
  `isLock` int(2) DEFAULT '0',
  `specialAttr` varchar(400) DEFAULT '',
  PRIMARY KEY (`id`),
  KEY `idx_treasury_userId_entId` (`userId`,`entId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `treasury_9`
--

DROP TABLE IF EXISTS `treasury_9`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `treasury_9` (
  `id` bigint(16) NOT NULL,
  `userId` bigint(19) NOT NULL,
  `entId` int(16) NOT NULL,
  `entType` int(2) DEFAULT '0',
  `itemCount` int(4) DEFAULT '0',
  `useCount` int(4) DEFAULT '0',
  `band` int(2) DEFAULT '0',
  `equip` bigint(16) DEFAULT '0',
  `throwAble` int(2) DEFAULT '0',
  `childType` int(4) DEFAULT '0',
  `isGift` int(2) DEFAULT '0',
  `existEndTime` timestamp NULL DEFAULT NULL,
  `level` int(2) DEFAULT '0',
  `holeNum` int(2) DEFAULT '0',
  `gemIds` varchar(200) DEFAULT NULL,
  `randomProp` varchar(400) DEFAULT NULL,
  `randomPropTmp` varchar(400) DEFAULT NULL,
  `spiritLv` int(16) DEFAULT '0',
  `gemExp` int(16) DEFAULT '0',
  `isLock` int(2) DEFAULT '0',
  `specialAttr` varchar(400) DEFAULT '',
  PRIMARY KEY (`id`),
  KEY `idx_treasury_userId_entId` (`userId`,`entId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `troop`
--

DROP TABLE IF EXISTS `troop`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `troop` (
  `troopId` bigint(19) NOT NULL DEFAULT '0',
  `userId` bigint(19) NOT NULL DEFAULT '0',
  `name` varchar(40) NOT NULL,
  `mainHeroId` bigint(19) NOT NULL DEFAULT '0',
  `indexs` smallint(6) NOT NULL DEFAULT '0',
  `troopGridId1` bigint(19) NOT NULL DEFAULT '0',
  `troopGridId2` bigint(19) NOT NULL DEFAULT '0',
  `troopGridId3` bigint(19) NOT NULL DEFAULT '0',
  `troopGridId4` bigint(19) NOT NULL DEFAULT '0',
  `troopGridId5` bigint(19) NOT NULL DEFAULT '0',
  `troopGridId6` bigint(19) NOT NULL DEFAULT '0',
  `status` smallint(6) NOT NULL DEFAULT '0',
  `freeDttm` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`troopId`),
  KEY `idx_troop_userId` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `troopgrid_0`
--

DROP TABLE IF EXISTS `troopgrid_0`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `troopgrid_0` (
  `troopGridId` bigint(19) NOT NULL COMMENT '军团格子id',
  `userId` bigint(19) NOT NULL COMMENT '君主id',
  `troopId` bigint(19) DEFAULT '0',
  `heroId` bigint(19) NOT NULL COMMENT '格子绑定的武将id',
  `sysHeroId` int(16) DEFAULT NULL,
  `equ1` int(16) DEFAULT '0' COMMENT '装备1',
  `equ2` int(16) DEFAULT '0' COMMENT '装备2',
  `equ3` int(16) DEFAULT '0' COMMENT '装备3',
  `equ4` int(16) DEFAULT '0' COMMENT '装备4',
  `equ5` int(16) DEFAULT '0' COMMENT '装备5',
  `equ6` int(16) DEFAULT '0' COMMENT '装备6',
  `equipFate` varchar(200) DEFAULT '0' COMMENT '装备情缘',
  PRIMARY KEY (`troopGridId`),
  KEY `index_Hero_userId` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

DROP TABLE IF EXISTS `troopgrid_1`;
CREATE TABLE `troopgrid_1` LIKE `troopgrid_0`;
DROP TABLE IF EXISTS `troopgrid_2`;
CREATE TABLE `troopgrid_2` LIKE `troopgrid_0`;
DROP TABLE IF EXISTS `troopgrid_3`;
CREATE TABLE `troopgrid_3` LIKE `troopgrid_0`;
DROP TABLE IF EXISTS `troopgrid_4`;
CREATE TABLE `troopgrid_4` LIKE `troopgrid_0`;
DROP TABLE IF EXISTS `troopgrid_5`;
CREATE TABLE `troopgrid_5` LIKE `troopgrid_0`;
DROP TABLE IF EXISTS `troopgrid_6`;
CREATE TABLE `troopgrid_6` LIKE `troopgrid_0`;
DROP TABLE IF EXISTS `troopgrid_7`;
CREATE TABLE `troopgrid_7` LIKE `troopgrid_0`;
DROP TABLE IF EXISTS `troopgrid_8`;
CREATE TABLE `troopgrid_8` LIKE `troopgrid_0`;
DROP TABLE IF EXISTS `troopgrid_9`;
CREATE TABLE `troopgrid_9` LIKE `troopgrid_0`;
--
-- Table structure for table `user`
--


DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `userId` bigint(19) NOT NULL,
  `accId` bigint(16) NOT NULL,
  `userName` varchar(20) DEFAULT NULL,
  `mainCastleId` bigint(19) DEFAULT '0',
  `charId` int(11) DEFAULT '0',
  `usrLv` int(2) NOT NULL DEFAULT '0',
  `vip` int(2) DEFAULT '0',
  `cash` int(16) DEFAULT '0',
  `consumeCash` int(16) DEFAULT '0',
  `giftTotal` int(16) DEFAULT '0',
  `cashTotal` int(16) DEFAULT '0',
  `honor` int(16) DEFAULT '0',
  `expPoint` int(16) DEFAULT '0',
  `lastLoginDttm` timestamp NULL DEFAULT NULL,
  `payPoint` int(16) DEFAULT '0',
  `createDate` timestamp NULL DEFAULT NULL,
  `selfSignature` varchar(200) DEFAULT '‘’' ,
  `countryId` int(4) DEFAULT '0' ,
  `guildId` bigint(16) NOT NULL DEFAULT '0',
  `upLevelTime` timestamp NULL DEFAULT NULL,
  `password` varchar(100) DEFAULT NULL,
  `curActPoint` int(16) DEFAULT '0',
  `troopId` bigint(19) DEFAULT '0',
  `hpPoint` int(16) DEFAULT '0',
  `title` int(16) DEFAULT '0',
  `junGong` int(16) DEFAULT '0',
  `autoResumeArmy` tinyint(4) DEFAULT '0',
  `heroId` bigint(16) DEFAULT '0',
  `color` int(16) DEFAULT '0',
  `titleAwardId` int(16) DEFAULT '0',
  PRIMARY KEY (`userId`),
  UNIQUE KEY `userName` (`userName`),
  KEY `index_User_accId` (`accId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `userattributes_0`
--

DROP TABLE IF EXISTS `userattributes_0`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `userattributes_0` (
  `userId` bigint(19) NOT NULL,
  `attrName` varchar(40) NOT NULL,
  `attrValue` varchar(40) NOT NULL,
  PRIMARY KEY (`userId`,`attrName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `userattributes_1`
--

DROP TABLE IF EXISTS `userattributes_1`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `userattributes_1` (
  `userId` bigint(19) NOT NULL,
  `attrName` varchar(40) NOT NULL,
  `attrValue` varchar(40) NOT NULL,
  PRIMARY KEY (`userId`,`attrName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `userattributes_2`
--

DROP TABLE IF EXISTS `userattributes_2`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `userattributes_2` (
  `userId` bigint(19) NOT NULL,
  `attrName` varchar(40) NOT NULL,
  `attrValue` varchar(40) NOT NULL,
  PRIMARY KEY (`userId`,`attrName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `userattributes_3`
--

DROP TABLE IF EXISTS `userattributes_3`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `userattributes_3` (
  `userId` bigint(19) NOT NULL,
  `attrName` varchar(40) NOT NULL,
  `attrValue` varchar(40) NOT NULL,
  PRIMARY KEY (`userId`,`attrName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `userattributes_4`
--

DROP TABLE IF EXISTS `userattributes_4`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `userattributes_4` (
  `userId` bigint(19) NOT NULL,
  `attrName` varchar(40) NOT NULL,
  `attrValue` varchar(40) NOT NULL,
  PRIMARY KEY (`userId`,`attrName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `userattributes_5`
--

DROP TABLE IF EXISTS `userattributes_5`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `userattributes_5` (
  `userId` bigint(19) NOT NULL,
  `attrName` varchar(40) NOT NULL,
  `attrValue` varchar(40) NOT NULL,
  PRIMARY KEY (`userId`,`attrName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `userattributes_6`
--

DROP TABLE IF EXISTS `userattributes_6`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `userattributes_6` (
  `userId` bigint(19) NOT NULL,
  `attrName` varchar(40) NOT NULL,
  `attrValue` varchar(40) NOT NULL,
  PRIMARY KEY (`userId`,`attrName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `userattributes_7`
--

DROP TABLE IF EXISTS `userattributes_7`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `userattributes_7` (
  `userId` bigint(19) NOT NULL,
  `attrName` varchar(40) NOT NULL,
  `attrValue` varchar(40) NOT NULL,
  PRIMARY KEY (`userId`,`attrName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `userattributes_8`
--

DROP TABLE IF EXISTS `userattributes_8`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `userattributes_8` (
  `userId` bigint(19) NOT NULL,
  `attrName` varchar(40) NOT NULL,
  `attrValue` varchar(40) NOT NULL,
  PRIMARY KEY (`userId`,`attrName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `userattributes_9`
--

DROP TABLE IF EXISTS `userattributes_9`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `userattributes_9` (
  `userId` bigint(19) NOT NULL,
  `attrName` varchar(40) NOT NULL,
  `attrValue` varchar(40) NOT NULL,
  PRIMARY KEY (`userId`,`attrName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `userconsumelog`
--

DROP TABLE IF EXISTS `userconsumelog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `userconsumelog` (
  `id` int(16) NOT NULL AUTO_INCREMENT,
  `userId` bigint(19) NOT NULL DEFAULT '0',
  `reason` varchar(40) NOT NULL,
  `cash` int(16) DEFAULT '0',
  `balance` int(16) DEFAULT '0',
  `pf` varchar(100) DEFAULT NULL,
  `dttm` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `index_userconsumelog_userId` (`userId`,`pf`,`dttm`),
  KEY `index_charge_dttm` (`dttm`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `usercount`
--

DROP TABLE IF EXISTS `usercount`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `usercount` (
  `userId` bigint(19) NOT NULL DEFAULT '0' COMMENT '君主id',
  `type` varchar(100) NOT NULL DEFAULT '' COMMENT '类型',
  `num` int(16) NOT NULL DEFAULT '0' COMMENT '数量',
  `lastDttm` timestamp NULL DEFAULT NULL COMMENT '上一次的时间',
  PRIMARY KEY (`userId`,`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='通用的计数器';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `usereffect`
--

DROP TABLE IF EXISTS `usereffect`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `usereffect` (
  `id` int(16) NOT NULL AUTO_INCREMENT,
  `userId` bigint(19) NOT NULL,
  `entId` int(16) NOT NULL DEFAULT '0',
  `effTypeId` varchar(40) DEFAULT NULL COMMENT '加成类型（科技、建筑...）',
  `absValue` int(8) DEFAULT '0' COMMENT '效果绝对值',
  `perValue` int(8) DEFAULT '0' COMMENT '效果百分比',
  `showFlag` int(2) DEFAULT '0' COMMENT '（暂时不使用）',
  `expireDttm` timestamp NULL DEFAULT NULL COMMENT '效果过期时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_usereff_userid` (`userId`,`entId`,`effTypeId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `usermysticshop`
--

DROP TABLE IF EXISTS `usermysticshop`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `usermysticshop` (
  `userId` bigint(13) NOT NULL DEFAULT '0',
  `shopId` int(11) NOT NULL DEFAULT '0',
  `curShopIds` varchar(300) DEFAULT NULL,
  `curBuyStatus` int(11) DEFAULT NULL,
  `buyShopIds` varchar(300) DEFAULT NULL,
  `freeCount` int(11) DEFAULT NULL,
  `buyCount` int(11) DEFAULT NULL,
  `lastTimes` timestamp NULL DEFAULT NULL,
  `cashCount` int(11) DEFAULT NULL,
  PRIMARY KEY (`userId`,`shopId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `userpubattr`
--

DROP TABLE IF EXISTS `userpubattr`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `userpubattr` (
  `userId` bigint(19) NOT NULL DEFAULT '0',
  `hireNum1` int(16) NOT NULL DEFAULT '0',
  `hireCD1` timestamp NULL DEFAULT NULL,
  `freetimes1` int(16) DEFAULT NULL,
  `hireNum2` int(16) NOT NULL DEFAULT '0',
  `hireCD2` timestamp NULL DEFAULT NULL,
  `freetimes2` int(16) DEFAULT NULL,
  `hireNum3` int(16) NOT NULL DEFAULT '0',
  `hireCD3` timestamp NULL DEFAULT NULL,
  `freetimes3` int(16) DEFAULT NULL,
  `version` int(32) DEFAULT NULL,
  `status` int(32) DEFAULT NULL,
  PRIMARY KEY (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `userrechargelog`
--

DROP TABLE IF EXISTS `userrechargelog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `userrechargelog` (
  `id` int(16) NOT NULL AUTO_INCREMENT,
  `userId` bigint(19) NOT NULL DEFAULT '0',
  `type` smallint(6) DEFAULT '0',
  `cash` int(16) DEFAULT '0',
  `balance` int(16) DEFAULT '0',
  `qb` int(16) DEFAULT '0',
  `quan` int(16) DEFAULT '0',
  `dttm` timestamp NULL DEFAULT NULL,
  `orderId` varchar(128) DEFAULT NULL,
  `pf` varchar(255) DEFAULT 'pengyou',
  PRIMARY KEY (`id`),
  KEY `index_userrechargelog_userId` (`userId`),
  KEY `index_charge_dttm` (`dttm`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `uservip`
--

DROP TABLE IF EXISTS `uservip`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `uservip` (
  `userId` bigint(19) NOT NULL,
  `vipStartTime` timestamp NULL DEFAULT NULL COMMENT 'vip开始时间',
  `vipEndTime` timestamp NULL DEFAULT NULL COMMENT 'vip结束时间',
  `lastDttm` timestamp NULL DEFAULT NULL COMMENT '最后计算时间',
  `vipLv` tinyint(2) DEFAULT '0' COMMENT 'vip等级',
  `vipPoint` int(16) DEFAULT '0' COMMENT 'vip积分',
  `prizeCount` smallint(6) DEFAULT '0' COMMENT '礼包',
  `totalCash` int(16) DEFAULT '0',
  `reward` int(16) DEFAULT '0',
  `everyReward` int(16) DEFAULT '0',
  `unfreeReward` int(16) DEFAULT '0' COMMENT '收费终身奖励领取标志位',
  PRIMARY KEY (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `weightvalueconf`
--

DROP TABLE IF EXISTS `weightvalueconf`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `weightvalueconf` (
  `id` int(16) NOT NULL DEFAULT '0',
  `description` varchar(100) DEFAULT NULL,
  `weight0` int(16) NOT NULL DEFAULT '0',
  `value0` int(16) NOT NULL DEFAULT '0',
  `weight1` int(16) NOT NULL DEFAULT '0',
  `value1` int(16) NOT NULL DEFAULT '0',
  `weight2` int(16) NOT NULL DEFAULT '0',
  `value2` int(16) NOT NULL DEFAULT '0',
  `weight3` int(16) NOT NULL DEFAULT '0',
  `value3` int(16) NOT NULL DEFAULT '0',
  `weight4` int(16) NOT NULL DEFAULT '0',
  `value4` int(16) NOT NULL DEFAULT '0',
  `weight5` int(16) NOT NULL DEFAULT '0',
  `value5` int(16) NOT NULL DEFAULT '0',
  `weight6` int(16) NOT NULL DEFAULT '0',
  `value6` int(16) NOT NULL DEFAULT '0',
  `weight7` int(16) NOT NULL DEFAULT '0',
  `value7` int(16) NOT NULL DEFAULT '0',
  `weight8` int(16) NOT NULL DEFAULT '0',
  `value8` int(16) NOT NULL DEFAULT '0',
  `weight9` int(16) NOT NULL DEFAULT '0',
  `value9` int(16) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `activity`
--

DROP TABLE IF EXISTS `activity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `activity` (
  `actId` int(10) NOT NULL,
  `startDttm` timestamp NOT NULL default '0000-00-00 00:00:00',
  `endDttm` timestamp NOT NULL default '0000-00-00 00:00:00',
  `name` varchar(50) NOT NULL,
  `channel` varchar(500) default NULL,
  `efId` varchar(2000) default NULL,
  `timeStart` bigint(16) default '0',
  `timeEnd` bigint(16) default '0',
  `weekStart` int(2) default '1',
  `weekEnd` int(2) default '7',
  `status` tinyint(2) default '0',
  `relation` tinyint(2) default '0',
  `qqYellowLv` varchar(10) default NULL,
  `qqBlueLv` varchar(10) default NULL,
  `qqPlusLv` varchar(10) default NULL,
  `qqLv` varchar(10) default NULL,
  `qqVipLv` varchar(10) default NULL,
  `qq3366Lv` varchar(10) default NULL,
  `qqRedLv` varchar(10) default NULL,
  `qqGreenLv` varchar(10) default NULL,
  `qqPinkLv` varchar(10) default NULL,
  `qqSuperLv` varchar(10) default NULL,
  `usrLv` varchar(10) default NULL,
  `casteLv` varchar(10) default NULL,
  `description` varchar(200) default NULL,
  `url` varchar(100) default NULL,
  `icon` varchar(100) default NULL,
  `qqYellowLvYear` varchar(10) default NULL,
  `qqYellowLvHigh` varchar(10) default NULL,
  `qqBlueLvYear` varchar(10) default NULL,
  `qqBlueLvHigh` varchar(10) default NULL,
  `qqVipLvYear` varchar(10) default NULL,
  `qqPinkLvYear` varchar(10) default NULL,
  PRIMARY KEY  (`actId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `awardactivity`
--

DROP TABLE IF EXISTS `awardactivity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `awardactivity` (
  `id` int(16) NOT NULL default '0',
  `actName` varchar(50) default NULL,
  `actDesc` varchar(2048) default NULL,
  `icon` varchar(255) default NULL,
  `type` int(16) default '0',
  `startDttm` timestamp NULL default NULL,
  `endDttm` timestamp NULL default NULL,
  `minUsrLv` int(16) default '0',
  `maxUsrLv` int(16) default '0',
  `itemId1` int(16) default '0',
  `num1` int(16) default '0',
  `itemId2` int(16) default '0',
  `num2` int(16) default '0',
  `itemId3` int(16) default '0',
  `num3` int(16) default '0',
  `itemId4` int(16) default '0',
  `num4` int(16) default '0',
  `itemId5` int(16) default '0',
  `num5` int(16) default '0',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

DROP TABLE IF EXISTS `useroperateactivity`;
CREATE TABLE `useroperateactivity` (
  `userId` bigint(11) NOT NULL default '0',
  `actId` bigint(13) default NULL,
  `type` smallint(3) NOT NULL default '0',
  `status` varchar(1000) default NULL,
  `lastTime` timestamp NULL default NULL,
  PRIMARY KEY  (`userId`,`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
DROP TABLE IF EXISTS `operateactivity`;
CREATE TABLE `operateactivity` (
  `actId` bigint(11) NOT NULL default '0',
  `type` smallint(3) default '0',
  `actName` varchar(200) default NULL,
  `startTime` timestamp NULL default NULL,
  `endTime` timestamp NULL default NULL,
  `maxTime` timestamp NULL default NULL,
  `rewardContext` varchar(2000) default NULL,
  `autoReward` tinyint(1) default '0',
  `status` int(32) default '0',
  PRIMARY KEY  (`actId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `forum`;
CREATE TABLE `forum` (
  `forumId` int(11) NOT NULL auto_increment,
  `forumType` int(11) default '0' COMMENT '1，天子公告，2，上周热帖，3，本周热帖',
  `title` varchar(50) default NULL,
  `forumContext` varchar(10000) default NULL,
  `lastTime` timestamp NULL default NULL,
  `countryId` int(11) default NULL,
  `tianZiName` varchar(50) default NULL,
  PRIMARY KEY  (`forumId`)
) ENGINE=InnoDB AUTO_INCREMENT=124 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `userchat`;
CREATE TABLE `userchat` (
  `userId` bigint(19) NOT NULL,
  `config` varchar(40) DEFAULT NULL,
  `hisRecord` blob,
  `recentTimeFriend` blob,
  `statu` int(32) NOT NULL,
  PRIMARY KEY (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `technology`;
CREATE TABLE `technology` (
  `entId` int(16) NOT NULL,
  `techType` varchar(40) DEFAULT NULL,
  `techName` varchar(20) DEFAULT NULL,
  `techDesc` varchar(100) DEFAULT NULL,
  `iconPath` varchar(100) DEFAULT NULL,
  `maxLevel` int(4) DEFAULT '0',
  `orderIndex` int(2) DEFAULT '0',
  `target` tinyint(4) DEFAULT '0', -- 0: 玩家科技；1: 联盟科技
  PRIMARY KEY (`entId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `usertech_0`;
CREATE TABLE `usertech_0` (
  `id` int(16) NOT NULL AUTO_INCREMENT,
  `userId` bigint(19) NOT NULL,
  `entId` int(16) NOT NULL,
  `level` int(4) DEFAULT '0',
  `beginDttm` timestamp NULL DEFAULT NULL,
  `endDttm` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_usertech_userid` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `usertech_1`;
CREATE TABLE usertech_1 LIKE usertech_0;

DROP TABLE IF EXISTS `usertech_2`;
CREATE TABLE usertech_2 LIKE usertech_0;

DROP TABLE IF EXISTS `usertech_3`;
CREATE TABLE usertech_3 LIKE usertech_0;

DROP TABLE IF EXISTS `usertech_4`;
CREATE TABLE usertech_4 LIKE usertech_0;

DROP TABLE IF EXISTS `usertech_5`;
CREATE TABLE usertech_5 LIKE usertech_0;

DROP TABLE IF EXISTS `usertech_6`;
CREATE TABLE usertech_6 LIKE usertech_0;

DROP TABLE IF EXISTS `usertech_7`;
CREATE TABLE usertech_7 LIKE usertech_0;

DROP TABLE IF EXISTS `usertech_8`;
CREATE TABLE usertech_8 LIKE usertech_0;

DROP TABLE IF EXISTS `usertech_9`;
CREATE TABLE usertech_9 LIKE usertech_0;

DROP TABLE IF EXISTS `friendrecommend`;
CREATE TABLE `friendrecommend` (
  `id` int(16) NOT NULL auto_increment,
  `usrLv` smallint(6) default '0',
  `minLv` smallint(6) default '0',
  `maxLv` smallint(6) default '0',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `friend_0`;
CREATE TABLE `friend_0` (
  `id` int(16) NOT NULL auto_increment,
  `userId` bigint(19) NOT NULL default '0' COMMENT '所属君主id',
  `friendUserId` bigint(19) NOT NULL default '0' COMMENT '好友君主id',
  `friendUserName` varchar(20) default NULL COMMENT '好友君主名',
  `friendMainCasId` bigint(19) NOT NULL default '0' COMMENT '好友主城id',
  `groupId` int(2) NOT NULL default '0' COMMENT '所属组(显示是通过组来过滤) 1,2,3：3个组 4：黑名单 5:申请中 6：好友',
  `addTime` timestamp NULL default NULL COMMENT '好友的添加时间,用在好友显示时排序',
  `note` varchar(200) default NULL COMMENT '备注',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `idx_userId_friendUserId` (`userId`,`friendUserId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `friend_1`;
CREATE TABLE friend_1 LIKE friend_0;

DROP TABLE IF EXISTS `friend_2`;
CREATE TABLE friend_2 LIKE friend_0;

DROP TABLE IF EXISTS `friend_3`;
CREATE TABLE friend_3 LIKE friend_0;

DROP TABLE IF EXISTS `friend_4`;
CREATE TABLE friend_4 LIKE friend_0;

DROP TABLE IF EXISTS `friend_5`;
CREATE TABLE friend_5 LIKE friend_0;

DROP TABLE IF EXISTS `friend_6`;
CREATE TABLE friend_6 LIKE friend_0;

DROP TABLE IF EXISTS `friend_7`;
CREATE TABLE friend_7 LIKE friend_0;

DROP TABLE IF EXISTS `friend_8`;
CREATE TABLE friend_8 LIKE friend_0;

DROP TABLE IF EXISTS `friend_9`;
CREATE TABLE friend_9 LIKE friend_0;


DROP TABLE IF EXISTS `friendapp`;
CREATE TABLE `friendapp` (
  `id` int(16) NOT NULL auto_increment,
  `userId` bigint(19) NOT NULL default '0' COMMENT '所属君主id',
  `userName` varchar(20) default NULL COMMENT '好友君主名',
  `friendUserId` bigint(19) NOT NULL default '0' COMMENT '好友君主id',
  `friendUserName` varchar(20) default NULL COMMENT '好友君主名',
  `addTime` timestamp NULL default NULL COMMENT '好友的添加时间,用在好友显示时排序',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `idx_userId_friendUserId` (`userId`,`friendUserId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `friendhp`;
CREATE TABLE `friendhp` (
  `userId` bigint(11) NOT NULL default '0',
  `giftUserIds` varchar(6000) default '' COMMENT '发送给好友的体力 列表',
  `giveMeUserIds` varchar(6000) default '' COMMENT '赠送给我体力的好友id列表',
  `receiveUserIds` varchar(6000) default '' COMMENT '已经领取的好友列表id',
  `lastTime` timestamp NULL default NULL COMMENT '最后操作时间',
  PRIMARY KEY  (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `mission`;
CREATE TABLE `mission` (
  `missionId` int(16) NOT NULL AUTO_INCREMENT,
  `missionType` varchar(40) DEFAULT NULL,
  `missionName` varchar(100) DEFAULT NULL,
  `missionDesc` varchar(800) DEFAULT NULL,
  `missionGuildDesc` varchar(800) DEFAULT NULL,
  `missionCompleteDesc1` varchar(200) DEFAULT NULL,
  `missionCompleteDesc2` varchar(200) DEFAULT NULL,
  `missionCompleteDesc3` varchar(200) DEFAULT NULL,
  `missionCompleteDesc4` varchar(200) DEFAULT NULL,
  `missionCompleteDesc5` varchar(200) DEFAULT NULL,
  `missionHardLevel` int(2) DEFAULT '0',
  `missionLevel` int(2) DEFAULT '0',
  `completeCondition` int(2) DEFAULT '0',
  `serviceName` varchar(40) DEFAULT NULL,
  `parentMissionId` int(16) DEFAULT '0',
  `isDelItem` int(2) DEFAULT '0',
  `limitCount` int(4) DEFAULT '0',
  `missioncompleteId1` int(16) DEFAULT '0',
  `missioncompleteId2` int(16) DEFAULT '0',
  `missioncompleteId3` int(16) DEFAULT '0',
  `missioncompleteId4` int(16) DEFAULT '0',
  `missioncompleteId5` int(16) DEFAULT '0',
  `missionGuidDesc` varchar(500) DEFAULT NULL,
  `awardtype1` varchar(40) DEFAULT NULL,
  `awardNum1` int(16) DEFAULT '0',
  `awardId1` int(16) DEFAULT '0',
  `awardtype2` varchar(40) DEFAULT NULL,
  `awardNum2` int(16) DEFAULT '0',
  `awardId2` int(16) DEFAULT '0',
  `awardtype3` varchar(40) DEFAULT NULL,
  `awardNum3` int(16) DEFAULT '0',
  `awardId3` int(16) DEFAULT '0',
  `awardtype4` varchar(40) DEFAULT NULL,
  `awardNum4` int(16) DEFAULT '0',
  `awardId4` int(16) DEFAULT '0',
  `pic1` varchar(20) DEFAULT NULL,
  `pic2` varchar(20) DEFAULT NULL,
  `pic3` varchar(20) DEFAULT NULL,
  `pic4` varchar(20) DEFAULT NULL,
  `missionChildType` varchar(20) DEFAULT NULL,
  `award` varchar(100) DEFAULT NULL,
  `issueMissionId` int(16) DEFAULT '0',
  `timeLimit` bigint(19) DEFAULT '0',
  PRIMARY KEY (`missionId`),
  KEY `idx_parentMissionId` (`parentMissionId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for missionlimit
-- ----------------------------
DROP TABLE IF EXISTS `missionlimit`;
CREATE TABLE `missionlimit` (
  `missioncompleteId` int(16) NOT NULL AUTO_INCREMENT,
  `entId` int(16) DEFAULT '0',
  `entNum` int(8) DEFAULT '0',
  `octType` varchar(20) DEFAULT NULL,
  `entType` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`missioncompleteId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for missiontype
-- ----------------------------
DROP TABLE IF EXISTS `missiontype`;
CREATE TABLE `missiontype` (
  `missionChildType` varchar(40) NOT NULL,
  `missionChildTypeName` varchar(100) DEFAULT NULL,
  `missionType` varchar(40) NOT NULL,
  `limitType` smallint(6) DEFAULT '1',
  `limitValue` smallint(6) DEFAULT '1',
  PRIMARY KEY (`missionChildType`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `usermission_0`;
CREATE TABLE `usermission_0` (
  `userMissionId` int(16) NOT NULL AUTO_INCREMENT,
  `missionId` int(16) DEFAULT '0',
  `userId` bigint(19) NOT NULL DEFAULT '0',
  `completeLimitTime` timestamp NULL DEFAULT NULL,
  `status` smallint(6) DEFAULT '0',
  `num1` int(8) DEFAULT '0',
  `num2` int(8) DEFAULT '0',
  `num3` int(8) DEFAULT '0',
  `num4` int(8) DEFAULT '0',
  `num5` int(8) DEFAULT '0',
  `factor` int(8) DEFAULT '1',
  `read0` smallint(6) DEFAULT '0',
  PRIMARY KEY (`userMissionId`),
  KEY `idx_usermission_userId_missionId` (`userId`,`missionId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;


DROP TABLE IF EXISTS `usermission_1`;
CREATE TABLE `usermission_1` LIKE `usermission_0`;

DROP TABLE IF EXISTS `usermission_2`;
CREATE TABLE `usermission_2` LIKE `usermission_0`;

DROP TABLE IF EXISTS `usermission_3`;
CREATE TABLE `usermission_3` LIKE `usermission_0`;

DROP TABLE IF EXISTS `usermission_4`;
CREATE TABLE `usermission_4` LIKE `usermission_0`;

DROP TABLE IF EXISTS `usermission_5`;
CREATE TABLE `usermission_5` LIKE `usermission_0`;

DROP TABLE IF EXISTS `usermission_6`;
CREATE TABLE `usermission_6` LIKE `usermission_0`;

DROP TABLE IF EXISTS `usermission_7`;
CREATE TABLE `usermission_7` LIKE `usermission_0`;

DROP TABLE IF EXISTS `usermission_8`;
CREATE TABLE `usermission_8` LIKE `usermission_0`;

DROP TABLE IF EXISTS `usermission_9`;
CREATE TABLE `usermission_9` LIKE `usermission_0`;

DROP TABLE IF EXISTS `userworldmission_0`;
CREATE TABLE `userworldmission_0` (
  `id` int(16) NOT NULL AUTO_INCREMENT,
  `missionId` int(16) DEFAULT '0',
  `userId` bigint(19) DEFAULT '0',
  `finishDttm` timestamp NULL DEFAULT NULL,
  `awardDttm` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `userworldmission_1`;
CREATE TABLE `userworldmission_1` LIKE `userworldmission_0`;

DROP TABLE IF EXISTS `userworldmission_2`;
CREATE TABLE `userworldmission_2` LIKE `userworldmission_0`;

DROP TABLE IF EXISTS `userworldmission_3`;
CREATE TABLE `userworldmission_3` LIKE `userworldmission_0`;

DROP TABLE IF EXISTS `userworldmission_4`;
CREATE TABLE `userworldmission_4` LIKE `userworldmission_0`;

DROP TABLE IF EXISTS `userworldmission_5`;
CREATE TABLE `userworldmission_5` LIKE `userworldmission_0`;

DROP TABLE IF EXISTS `userworldmission_6`;
CREATE TABLE `userworldmission_6` LIKE `userworldmission_0`;

DROP TABLE IF EXISTS `userworldmission_7`;
CREATE TABLE `userworldmission_7` LIKE `userworldmission_0`;

DROP TABLE IF EXISTS `userworldmission_8`;
CREATE TABLE `userworldmission_8` LIKE `userworldmission_0`;

DROP TABLE IF EXISTS `userworldmission_9`;
CREATE TABLE `userworldmission_9` LIKE `userworldmission_0`;

DROP TABLE IF EXISTS `worldmission`;
CREATE TABLE `worldmission` (
  `missionId` int(16) NOT NULL DEFAULT '0',
  `completeLimitTime` timestamp NULL DEFAULT NULL,
  `status` smallint(6) DEFAULT '0',
  `num1` int(8) DEFAULT '0',
  `num2` int(8) DEFAULT '0',
  `num3` int(8) DEFAULT '0',
  `num4` int(8) DEFAULT '0',
  `num5` int(8) DEFAULT '0',
  `factor` int(8) DEFAULT '1',
  `read0` smallint(6) DEFAULT '0',
  `endDttm` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`missionId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `achieve`;
CREATE TABLE `achieve` (
  `achieveId` int(16) NOT NULL AUTO_INCREMENT,
  `name` varchar(40) DEFAULT NULL,
  `type` int(16) DEFAULT '0',
  `entId` int(16) DEFAULT '0',
  `entNum` int(16) DEFAULT '0',
  `jungong` int(16) DEFAULT '0',
  `icon` varchar(200) DEFAULT NULL,
  `isCumulative` int(16) DEFAULT '0',
  PRIMARY KEY (`achieveId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `achievetype`;
CREATE TABLE `achievetype` (
  `type` int(16) NOT NULL,
  `name` varchar(40) DEFAULT NULL,
  `icon` varchar(200) DEFAULT NULL,
  `num` int(16) DEFAULT '0',
  `achieveIds` varchar(1000) DEFAULT NULL,  
  PRIMARY KEY (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `achievelimit`;
CREATE TABLE `achievelimit` (
  `achieveId` int(16) NOT NULL,
  `octType` varchar(200) DEFAULT NULL,
  `para1` int(16) DEFAULT '0',
  `para2` int(16) DEFAULT '0',
  `para3` int(16) DEFAULT '0',
  `para4` int(16) DEFAULT '0',
  `para5` int(16) DEFAULT '0',
  PRIMARY KEY (`achieveId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `userachieve`;
CREATE TABLE `userachieve` (
  `id` int(16) NOT NULL AUTO_INCREMENT,
  `userId` bigint(19) DEFAULT '0',
  `type` int(16) DEFAULT '0',
  `achieveId` int(16) DEFAULT '0',
  `entId` int(16) DEFAULT '0',
  `entNum` int(16) DEFAULT '0',
  `finishDttm` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_userachieve_userId_entId` (`userId`,`entId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;



DROP TABLE IF EXISTS `riskparentscene`;
CREATE TABLE `riskparentscene` (
  `id` int(16) NOT NULL,
  `name` varchar(40) NOT NULL,        
  `pic` varchar(255),        
  `types` int(16) default '0',
  `reqUserLv` int(16) default '0',
  `prevSceneId` int(16) default '0',
  `star1` int(16) default '0',
  `award1` int(16) default '0',
  `star2` int(16) default '0',
  `award2` int(16) default '0',
  `star3` int(16) default '0',
  `award3` int(16) default '0',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `riskscene`;
CREATE TABLE `riskscene` (
  `sceneId` int(16) NOT NULL,
  `parentId` int(16) NOT NULL,
  `name` varchar(40) NOT NULL,        
  `pic` varchar(255),        
  `prevSceneId` int(16) default '0',
  `sceneType` int(16) default '0',
  `hpPoint` int(16) default '0',
  `maxJoinNum` int(16) default '0',
  `userExp` int(16) default '0',
  `npcId` int(16) default '0',
  `npcId2` int(16) default '0',
  `dropPackId` int(16) default '0',
  `operateDropPackId` int(16) default '0',
  `terrian` int(16) default '0',
  `sceneNpc1` int(16) default '0',
  `sceneNpc2` int(16) default '0',
  `storyId1` int(16) default '0',
  `storyId2` int(16) default '0', 
  
  `addition` varchar(100) default '',
  `firstBonus` int(16) default '0',
  `failNum` int(16) default '0',
  `restVipNum` varchar(100) default '0',  
  PRIMARY KEY  (`sceneId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;




DROP TABLE IF EXISTS `userriskscene_0`;
CREATE TABLE `userriskscene_0` (
  `userId` bigint(19) NOT NULL default 0,
  `pid` int(16) NOT NULL default 0,
  `starAward` int(16)  default 0,
  `sceneId0` int(16)  default 0,
  `version0` int(16)  default 0,
  `data0` BIGINT(64)  default 0,
  `items0` varchar(100),
  
  `sceneId1` int(16)  default 0,
  `version1` int(16)  default 0,
  `data1` BIGINT(64)  default 0,
  `items1` varchar(100),
  
  `sceneId2` int(16)  default 0,
  `version2` int(16)  default 0,
  `data2` BIGINT(64)  default 0,
  `items2` varchar(100),
  
  `sceneId3` int(16)  default 0,
  `version3` int(16)  default 0,
  `data3` BIGINT(64)  default 0,
  `items3` varchar(100),
  
  `sceneId4` int(16)  default 0,
  `version4` int(16)  default 0,
  `data4` BIGINT(64)  default 0,
  `items4` varchar(100),
  
  `sceneId5` int(16)  default 0,
  `version5` int(16)  default 0,
  `data5` BIGINT(64)  default 0,
  `items5` varchar(100),
  
  `sceneId6` int(16)  default 0,
  `version6` int(16)  default 0,
  `data6` BIGINT(64)  default 0,
  `items6` varchar(100),
  
  `sceneId7` int(16)  default 0,
  `version7` int(16)  default 0,
  `data7` BIGINT(64)  default 0,
  `items7` varchar(100),
  
  `sceneId8` int(16)  default 0,
  `version8` int(16)  default 0,
  `data8` BIGINT(64)  default 0,
  `items8` varchar(100),
  
  `sceneId9` int(16)  default 0,
  `version9` int(16)  default 0,
  `data9` BIGINT(64)  default 0,
  `items9` varchar(100),
  
  `sceneId10` int(16)  default 0,
  `version10` int(16)  default 0,
  `data10` BIGINT(64)  default 0,
  `items10` varchar(100),
  
  `sceneId11` int(16)  default 0,
  `version11` int(16)  default 0,
  `data11` BIGINT(64)  default 0,
  `items11` varchar(100),
  
  `sceneId12` int(16)  default 0,
  `version12` int(16)  default 0,
  `data12` BIGINT(64)  default 0,
  `items12` varchar(100),
  
  `sceneId13` int(16)  default 0,
  `version13` int(16)  default 0,
  `data13` BIGINT(64)  default 0,
  `items13` varchar(100),
  
  `sceneId14` int(16)  default 0,
  `version14` int(16)  default 0,
  `data14` BIGINT(64)  default 0,
  `items14` varchar(100),
  
  `flags` int(32)	   default 0,
  
  PRIMARY KEY  (`userId`,`pid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `userriskscene_1`;
CREATE TABLE `userriskscene_1` LIKE `userriskscene_0`;

DROP TABLE IF EXISTS `userriskscene_2`;
CREATE TABLE `userriskscene_2` LIKE `userriskscene_0`;

DROP TABLE IF EXISTS `userriskscene_3`;
CREATE TABLE `userriskscene_3` LIKE `userriskscene_0`;

DROP TABLE IF EXISTS `userriskscene_4`;
CREATE TABLE `userriskscene_4` LIKE `userriskscene_0`;

DROP TABLE IF EXISTS `userriskscene_5`;
CREATE TABLE `userriskscene_5` LIKE `userriskscene_0`;

DROP TABLE IF EXISTS `userriskscene_6`;
CREATE TABLE `userriskscene_6` LIKE `userriskscene_0`;

DROP TABLE IF EXISTS `userriskscene_7`;
CREATE TABLE `userriskscene_7` LIKE `userriskscene_0`;

DROP TABLE IF EXISTS `userriskscene_8`;
CREATE TABLE `userriskscene_8` LIKE `userriskscene_0`;

DROP TABLE IF EXISTS `userriskscene_9`;
CREATE TABLE `userriskscene_9` LIKE `userriskscene_0`;



DROP TABLE IF EXISTS `commandercolorproperty`;
CREATE TABLE `commandercolorproperty` (
  `color` int(16) NOT NULL,
  `growing` int(16) DEFAULT '0',
  `magicAttack` int(16) DEFAULT '0',
  `physicalAttack` int(16) DEFAULT '0',
  `magicDefend` int(16) DEFAULT '0',
  `physicalDefend` int(16) DEFAULT '0',
  `initHp` int(16) DEFAULT '0',
  `initMorale` int(16) DEFAULT '0',
  `initHit` int(16) DEFAULT '0',
  `initDodge` int(16) DEFAULT '0',
  `critDec` int(16) DEFAULT '0',
  `critAdd` int(16) DEFAULT '0',
  `critDamageAdd` int(16) DEFAULT '0',
  `critDamageDec` int(16) DEFAULT '0',
  PRIMARY KEY (`color`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `title`;
CREATE TABLE `title` (
  `titleId` int(16) NOT NULL,
  `name` varchar(40) DEFAULT NULL,
  `icon` varchar(200) DEFAULT NULL,
  `parentId` int(16) DEFAULT '0',
  `color` int(16) DEFAULT '0',
  `award1` int(16) DEFAULT '0',
  `award2` int(16) DEFAULT '0',
  `award3` int(16) DEFAULT '0',
  `award4` int(16) DEFAULT '0',
  `award5` int(16) DEFAULT '0',
  `entId` int(16) DEFAULT '0',
  PRIMARY KEY (`titleId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `titleaward`;
CREATE TABLE `titleaward` (
  `awardId` int(16) NOT NULL,
  `name` varchar(40) DEFAULT NULL,
  `icon` varchar(200) DEFAULT NULL,
  `awardType` varchar(100) DEFAULT NULL,
  `junGong` int(16) DEFAULT '0',
  `awardDetail` varchar(400) DEFAULT NULL,
  `awardTips` varchar(400) DEFAULT NULL,
  PRIMARY KEY (`awardId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `mapcell_0`;
CREATE TABLE `mapcell_0` (
  `id` int(16) NOT NULL,
  `posX` int(16) DEFAULT '0',
  `posY` int(16) DEFAULT '0',
  `stateId` int(16) DEFAULT '0',
  `castType` int(16) DEFAULT '0',
  `countryId` int(16) DEFAULT '0',
  `casId` bigint(19) DEFAULT '0',
  `userId` bigint(19) DEFAULT '0',
  `guildId` bigint(19) DEFAULT '0',
  `earthId` int(16) DEFAULT '0',
  `earthType` int(16) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `mapcell_1`;
CREATE TABLE `mapcell_1` LIKE `mapcell_0`;
DROP TABLE IF EXISTS `mapcell_2`;
CREATE TABLE `mapcell_2` LIKE `mapcell_0`;
DROP TABLE IF EXISTS `mapcell_3`;
CREATE TABLE `mapcell_3` LIKE `mapcell_0`;
DROP TABLE IF EXISTS `mapcell_4`;
CREATE TABLE `mapcell_4` LIKE `mapcell_0`;
DROP TABLE IF EXISTS `mapcell_5`;
CREATE TABLE `mapcell_5` LIKE `mapcell_0`;
DROP TABLE IF EXISTS `mapcell_6`;
CREATE TABLE `mapcell_6` LIKE `mapcell_0`;
DROP TABLE IF EXISTS `mapcell_7`;
CREATE TABLE `mapcell_7` LIKE `mapcell_0`;
DROP TABLE IF EXISTS `mapcell_8`;
CREATE TABLE `mapcell_8` LIKE `mapcell_0`;
DROP TABLE IF EXISTS `mapcell_9`;
CREATE TABLE `mapcell_9` LIKE `mapcell_0`;
DROP TABLE IF EXISTS `mapcell_10`;
CREATE TABLE `mapcell_10` LIKE `mapcell_0`;
DROP TABLE IF EXISTS `mapcell_11`;
CREATE TABLE `mapcell_11` LIKE `mapcell_0`;
DROP TABLE IF EXISTS `mapcell_12`;
CREATE TABLE `mapcell_12` LIKE `mapcell_0`;
DROP TABLE IF EXISTS `mapcell_13`;
CREATE TABLE `mapcell_13` LIKE `mapcell_0`;
DROP TABLE IF EXISTS `mapcell_14`;
CREATE TABLE `mapcell_14` LIKE `mapcell_0`;
DROP TABLE IF EXISTS `mapcell_15`;
CREATE TABLE `mapcell_15` LIKE `mapcell_0`;

DROP TABLE IF EXISTS `state`;
CREATE TABLE `state` (
  `stateId` int(16) NOT NULL,
  `countryId` int(16) DEFAULT '0',
  `stateName` varchar(100) NOT NULL,
  `status` int(16) DEFAULT '0',
  `level` int(16) DEFAULT '0',
  PRIMARY KEY (`stateId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `tower`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tower` (
  `stageId` int(16) NOT NULL,
  `name` varchar(100) default NULL COMMENT '名称',
  `npcId` int(16) default '0' COMMENT '怪id',
  `additionEffect` varchar(200) default NULL COMMENT 'npc属性加成',
  `firstBonusId` int(16) default '0' COMMENT '首通奖励掉落包',
  `bonusId` int(16) default '0' COMMENT '奖励掉落包',
  `description` varchar(200) default NULL COMMENT '关描述',
  PRIMARY KEY  (`stageId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='打塔关卡表 ';
/*!40101 SET character_set_client = @saved_cs_client */;

DROP TABLE IF EXISTS `towersectionbonus`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `towersectionbonus` (
  `id` int(16) NOT NULL,
  `startStage` int(16) NOT NULL COMMENT '起始楼层',
  `endStage` int(16) NOT NULL COMMENT '结束楼层',
  `dropEntId` int(16) default '0' COMMENT '掉落包',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='打塔关卡保底奖励表 ';
/*!40101 SET character_set_client = @saved_cs_client */;

DROP TABLE IF EXISTS `toweruser`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `toweruser` (
  `userId` bigint(19) NOT NULL default '0',
  `stageId` int(16) NOT NULL default '0' COMMENT '关卡id',
  `joinStatus` smallint(6) default '0' COMMENT '0:不在嗒内 1：在嗒内',
  `reliveTimes` smallint(6) default '0' COMMENT '重生次数',
  `freeJoinTime` smallint(6) default '0' COMMENT '免费进入的次数',
  `itemJoinTime` smallint(6) default '0' COMMENT '道具进入的次数',
  `topStageId` smallint(6) default '0' COMMENT '最高挑战记录',
  `topDttm` timestamp NULL default NULL COMMENT '最高挑战记录对应时间',
  `score` smallint(6) default '0' COMMENT '上一场得分',
  `winItemId` int(16) default '0' COMMENT '道具奖励',
  `seasonWinItemId` varchar(2500) default NULL COMMENT '本轮获得的道具奖励  stageId-itemId',
  `combatId` varchar(100) default NULL,
  `lastJoinDttm` timestamp NULL default NULL COMMENT '上一次进入打塔活动的时间',
  PRIMARY KEY  (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户打塔记录表';
/*!40101 SET character_set_client = @saved_cs_client */;



DROP TABLE IF EXISTS `countrycharacter`;
CREATE TABLE `countrycharacter` (
  `id` bigint(19) NOT NULL default '0',
  `countryId` int(16) NOT NULL default '0' COMMENT '国家id',
  `sex` smallint(6) default '0' COMMENT '性别',
  `icon` varchar(40) default '0' COMMENT '图像',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='国家绑定角色信息';

DROP TABLE IF EXISTS `sysherocountry`;
CREATE TABLE `sysherocountry` (
  `id` int(16) NOT NULL AUTO_INCREMENT,
  `countryName` varchar(100) NOT NULL,
  `icon` varchar(80) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `armyout`;
CREATE TABLE `armyout` (
  `id` bigint(19) NOT NULL,
  `outType` int(16) DEFAULT '0',
  `attackerCellId` int(16) DEFAULT '0',
  `attackerUserId` bigint(19) DEFAULT '0',
  `attackerCasId` bigint(19) DEFAULT '0',
  `attackerGuildId` bigint(19) DEFAULT '0',
  `attackerType` int(16) DEFAULT '0',
  `defenderCellId` int(16) DEFAULT '0',
  `defenderUserId` bigint(19) DEFAULT '0',
  `defenderCasId` bigint(19) DEFAULT '0',
  `defenderGuildId` bigint(19) DEFAULT '0',
  `defenderType` int(16) DEFAULT '0',
  `troopId` bigint(19) DEFAULT '0',
  `atkPower` int(16) DEFAULT '0',
  `defPower` int(16) DEFAULT '0',
  `baseTime` int(16) DEFAULT '0',
  `outDttm` timestamp NULL DEFAULT NULL,
  `outArriveDttm` timestamp NULL DEFAULT NULL,
  `outBackDttm` timestamp NULL DEFAULT NULL,
  `status` smallint(6) DEFAULT '0',
  `combatId` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `userarmyout`;
CREATE TABLE `userarmyout` (
  `userId` bigint(19) NOT NULL,
  `outId1` bigint(19) DEFAULT '0',
  `outId2` bigint(19) DEFAULT '0',
  `outId3` bigint(19) DEFAULT '0',
  `spyNum` int(16) DEFAULT '0',
  `outNum` int(16) DEFAULT '0',
  `stayNum` int(16) DEFAULT '0',
  `togatherNum` int(16) DEFAULT '0',
  `pvpNum` int(16) DEFAULT '0',
  `lastDttm` timestamp NULL DEFAULT NULL,
  `lastDeadDttm` timestamp NULL DEFAULT NULL,
  `lastMoveDttm` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `strategy`;
CREATE TABLE `strategy` (
  `id` int(16) NOT NULL,
  `name` varchar(40) DEFAULT NULL,
  `consumeActPoint` int(16) DEFAULT '0',
  `consumeTimes` int(16) DEFAULT '0',
  `maxTimes` int(16) DEFAULT '0',
  `resEntId` int(16) DEFAULT '0',
  `resNumFactor` int(16) DEFAULT '0',
  `entId2` int(16) DEFAULT '0',
  `entNum2` int(16) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `dynrescasrule`;
CREATE TABLE `dynrescasrule` (
  `id` int(16) NOT NULL,
  `stateid` int(16) NOT NULL,
  `type` int(16) NOT NULL,
  `level` int(16) NOT NULL,
  `count` int(16) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `rescas`;
CREATE TABLE `rescas` (
  `id` int(16) NOT NULL,
  `name` varchar(40) DEFAULT NULL,
  `length` int(16) NOT NULL ,
  `castype` int(16) NOT NULL,
  `caslevel` int(16) NOT NULL,
  `oilyield` int(16) DEFAULT '0',
  `ironyield` int(16) DEFAULT '0',
  `uraniumyield` int(16) DEFAULT '0',
  `goldyield` int(16) DEFAULT '0',
  `refugeenum` int(16) DEFAULT '0',
  `garrisonlimit` int(16) NOT NULL ,
  `npcconfid` int(16) NOT NULL,
  `timespan` int(16) DEFAULT '0',
  `prevrescasid` varchar(100) DEFAULT NULL,
  `occupyvalue` int(16) DEFAULT '0',
  `cashp` int(16) NOT NULL ,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `dynrescas`;
CREATE TABLE `dynrescas` (
  `mapcellid` int(16) NOT NULL,
  `stateid` int(16) NOT NULL,
  `rescasid` int(16) NOT NULL,
  `userid` bigint(19) NOT NULL,
  `occupytime` TIMESTAMP NULL NOT NULL,
  `expiretime` TIMESTAMP NULL NOT NULL,
  `castype` int(16) NOT NULL,
  `caslevel` int(16) NOT NULL,
  `npcid` int(16) NOT NULL,
  `mirrortroop` BLOB,
  `mirrorsettime` TIMESTAMP NULL NOT NULL,
  `mirrorexpiretime` TIMESTAMP NULL NOT NULL,
  `cashp` int(16) NOT NULL,
  `mianzhanexpiretime` TIMESTAMP NULL NOT NULL,
  PRIMARY KEY (`mapcellid`),
  INDEX index_state_id (stateid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `userrescas`;
CREATE TABLE `userrescas` (
  `mapcellid` int(16) NOT NULL,
  `rescasid` int(16) NOT NULL,
  `userid` bigint(19) NOT NULL,
  `occupytime` TIMESTAMP NULL NOT NULL,
  `expiretime` TIMESTAMP NULL NOT NULL,
  `mirrortroop` BLOB,
  `mirrorsettime` TIMESTAMP NULL NOT NULL,
  `mirrorexpiretime` TIMESTAMP NULL NOT NULL,
  `cashp` int(16) NOT NULL,
  `mianzhanexpiretime` TIMESTAMP NULL NOT NULL,
  PRIMARY KEY (`mapcellid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `guildrescas`;
CREATE TABLE `guildrescas` (
  `mapcellid` int(16) NOT NULL,
  `rescasid` int(16) NOT NULL,
  `currentguildid` bigint(19) NOT NULL,
  `currentguildname` VARCHAR(40) DEFAULT NULL,
  `firstguildid` bigint(19) NOT NULL,
  `firstguildname` VARCHAR(40) DEFAULT NULL,
  `occupytime` TIMESTAMP NULL NOT NULL,
  `cashp` int(16) NOT NULL,
  PRIMARY KEY (`mapcellid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `countryrescas`;
CREATE TABLE `countryrescas` (
  `mapcellid` int(16) NOT NULL,
  `rescasid` int(16) NOT NULL,
  `currentcountryid` bigint(19) NOT NULL,
  `currentcountryname` VARCHAR(40) DEFAULT NULL,
  `currentguildid` bigint(19) NOT NULL,
  `currentguildname` VARCHAR(40) DEFAULT NULL,
  `firstguildid` bigint(19) NOT NULL,
  `firstguildname` VARCHAR(40) DEFAULT NULL,
  `occupytime` TIMESTAMP NULL NOT NULL,
  `cashp` int(16) NOT NULL,
  PRIMARY KEY (`mapcellid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `militarysituation`;
CREATE TABLE `militarysituation` (
  `id` int(16) unsigned NOT NULL AUTO_INCREMENT,
  `miSiType` int(16) NOT NULL,
  `name` varchar(60) NOT NULL DEFAULT '0',
  `content` varchar(60) NOT NULL,
  `misiDetail` blob NOT NULL,
  `userId` bigint(20) NOT NULL,
  `hasView` tinyint(4) NOT NULL,
  `time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `npcattackconf`;
CREATE TABLE `npcattackconf` (
  `id` int(16) NOT NULL,
  `model` varchar(40) DEFAULT NULL,
  `level` int(16) DEFAULT '0',
  `npcId` int(16) DEFAULT '0',
  `heroExp` int(16) DEFAULT '0',
  `dropPackId` int(16) DEFAULT '0',
  `nextId` int(16) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

