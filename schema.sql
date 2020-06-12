-- MySQL dump 10.13  Distrib 5.7.30, for Linux (x86_64)
--
-- Host: commander-aurora-mysql-cluster.cluster-cedt9oio6456.us-east-1.rds.amazonaws.com    Database: commander
-- ------------------------------------------------------
-- Server version	5.7.12

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
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `accountName` varchar(255) NOT NULL,
  `createDate` bigint(20) unsigned NOT NULL,
  `activationKey` varchar(20) NOT NULL,
  `phoneNumber` varchar(25) DEFAULT NULL,
  `accountState` tinyint(3) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2833 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `account_member`
--

DROP TABLE IF EXISTS `account_member`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `account_member` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `account_id` bigint(20) unsigned NOT NULL,
  `user_id` bigint(20) unsigned NOT NULL,
  `role` tinyint(3) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`,`account_id`,`user_id`),
  KEY `account` (`account_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4379 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `additionalCharge`
--

DROP TABLE IF EXISTS `additionalCharge`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `additionalCharge` (
  `additionalChargeType` tinyint(3) unsigned NOT NULL,
  `season_id` tinyint(3) unsigned NOT NULL,
  `energyPlan_id` bigint(20) unsigned NOT NULL,
  `rate` decimal(18,9) DEFAULT NULL,
  PRIMARY KEY (`additionalChargeType`,`season_id`,`energyPlan_id`),
  KEY `fk_additionalCharge_energyPlanSeason1_idx` (`season_id`,`energyPlan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `advice`
--

DROP TABLE IF EXISTS `advice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `advice` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `adviceName` varchar(255) NOT NULL DEFAULT 'Advice',
  `virtualECC_id` bigint(20) unsigned NOT NULL,
  `account_id` bigint(20) unsigned NOT NULL,
  `adviceState` tinyint(4) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `VIRTUALECC` (`virtualECC_id`),
  KEY `OWNER` (`account_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2511 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `advice_recipient`
--

DROP TABLE IF EXISTS `advice_recipient`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `advice_recipient` (
  `advice_id` bigint(20) unsigned NOT NULL,
  `user_id` bigint(20) unsigned NOT NULL,
  `useEmail` tinyint(1) unsigned NOT NULL DEFAULT '1',
  `usePush` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`advice_id`,`user_id`),
  KEY `USER` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `advice_trigger`
--

DROP TABLE IF EXISTS `advice_trigger`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `advice_trigger` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `advice_id` bigint(20) unsigned NOT NULL,
  `startTime` int(10) unsigned NOT NULL DEFAULT '0',
  `endTime` int(10) unsigned NOT NULL DEFAULT '0',
  `triggerType` int(10) unsigned NOT NULL DEFAULT '0',
  `sendAtMost` int(10) unsigned NOT NULL DEFAULT '0',
  `delayHours` int(10) unsigned NOT NULL DEFAULT '0',
  `amount` double NOT NULL DEFAULT '0',
  `allMTUs` int(1) unsigned NOT NULL DEFAULT '1',
  `offPeakApplicable` int(1) unsigned NOT NULL DEFAULT '1',
  `peakApplicable` int(1) unsigned NOT NULL DEFAULT '1',
  `midPeakApplicable` int(1) unsigned NOT NULL DEFAULT '1',
  `superPeakApplicable` int(1) unsigned NOT NULL DEFAULT '1',
  `sinceStart` int(10) unsigned NOT NULL DEFAULT '0',
  `minutesBefore` int(10) unsigned NOT NULL DEFAULT '0',
  `lastSent` bigint(20) unsigned NOT NULL DEFAULT '0',
  `triggerState` tinyint(4) unsigned NOT NULL DEFAULT '0',
  `delayMinutes` int(10) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `ADVICE` (`advice_id`),
  KEY `STATE` (`advice_id`,`triggerState`),
  KEY `COMMANDER` (`advice_id`,`triggerType`),
  KEY `TYPE` (`triggerType`)
) ENGINE=InnoDB AUTO_INCREMENT=2511 DEFAULT CHARSET=latin1 COMMENT='trigger table for TED Advisor	';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `alexa_location`
--

DROP TABLE IF EXISTS `alexa_location`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `alexa_location` (
  `user_id` bigint(20) NOT NULL,
  `virtualECC_id` bigint(20) NOT NULL,
  `alexaName` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`user_id`,`virtualECC_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `demandChargeTOU`
--

DROP TABLE IF EXISTS `demandChargeTOU`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `demandChargeTOU` (
  `peakType` tinyint(3) unsigned NOT NULL,
  `season_id` tinyint(3) unsigned NOT NULL,
  `energyPlan_id` bigint(20) unsigned NOT NULL,
  `rate` decimal(18,9) NOT NULL,
  PRIMARY KEY (`energyPlan_id`,`season_id`,`peakType`),
  KEY `fk_demandChargeTOU_touLevel1_idx` (`peakType`),
  KEY `fk_demandChargeTOU_energyPlanSeason1_idx` (`season_id`,`energyPlan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `demandChargeTier`
--

DROP TABLE IF EXISTS `demandChargeTier`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `demandChargeTier` (
  `id` tinyint(3) unsigned NOT NULL,
  `season_id` tinyint(3) unsigned NOT NULL,
  `energyPlan_id` bigint(20) unsigned NOT NULL,
  `peak` decimal(18,9) NOT NULL,
  `rate` decimal(18,9) NOT NULL,
  PRIMARY KEY (`id`,`season_id`,`energyPlan_id`),
  KEY `fk_demandChargeTier_energyPlanSeason1_idx` (`season_id`,`energyPlan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ecc`
--

DROP TABLE IF EXISTS `ecc`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ecc` (
  `id` bigint(20) unsigned NOT NULL,
  `account_id` bigint(20) unsigned NOT NULL,
  `state` tinyint(4) NOT NULL,
  `securityKey` varchar(20) DEFAULT NULL,
  `version` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`,`account_id`),
  KEY `fk_gateway_user1_idx` (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `energyPlan`
--

DROP TABLE IF EXISTS `energyPlan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `energyPlan` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `account_id` bigint(20) unsigned NOT NULL,
  `meterReadDate` tinyint(3) unsigned NOT NULL DEFAULT '1',
  `meterReadCycle` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `planType` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `touApplicableSaturday` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `touApplicableSunday` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `touApplicableHoliday` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `holidaySchedule_id` bigint(20) unsigned NOT NULL DEFAULT '0',
  `demandPlanType` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `demandUseActivePower` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `demandApplicableSaturday` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `demandApplicableSunday` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `demandApplicableHoliday` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `demandApplicableOffPeak` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `demandAverageTime` int(10) unsigned NOT NULL DEFAULT '0',
  `description` varchar(255) DEFAULT NULL,
  `utilityName` varchar(255) DEFAULT NULL,
  `rateType` varchar(45) DEFAULT NULL,
  `buybackRate` decimal(18,9) DEFAULT '0.900000000',
  `buyback` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `numberSeasons` tinyint(3) unsigned NOT NULL DEFAULT '1',
  `numberTOU` tinyint(3) unsigned NOT NULL DEFAULT '1',
  `numberTier` tinyint(3) unsigned NOT NULL DEFAULT '1',
  `numberDemandSteps` tinyint(3) unsigned NOT NULL DEFAULT '1',
  `surcharge` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `fixed` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `minimum` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `taxes` tinyint(1) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `fk_energyPlan_user1_idx` (`account_id`),
  KEY `fk_energyPlan_holidaySchedule1_idx` (`holidaySchedule_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5393 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `energyPlanSeason`
--

DROP TABLE IF EXISTS `energyPlanSeason`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `energyPlanSeason` (
  `id` tinyint(3) unsigned NOT NULL,
  `energyPlan_id` bigint(20) unsigned NOT NULL,
  `seasonName` varchar(45) DEFAULT NULL,
  `seasonMonth` tinyint(3) unsigned DEFAULT NULL,
  `seasonDayOfMonth` tinyint(3) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`,`energyPlan_id`),
  KEY `fk_energyPlanSeason_energyPlan1_idx` (`energyPlan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `energyPlanTOU`
--

DROP TABLE IF EXISTS `energyPlanTOU`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `energyPlanTOU` (
  `season_id` tinyint(3) unsigned NOT NULL,
  `energyPlan_id` bigint(20) unsigned NOT NULL,
  `peakType` tinyint(4) unsigned NOT NULL,
  `isAM` tinyint(1) unsigned NOT NULL,
  `touStartHour` tinyint(3) unsigned DEFAULT NULL,
  `touStartMinute` tinyint(3) unsigned DEFAULT NULL,
  `touEndHour` tinyint(3) unsigned DEFAULT NULL,
  `touEndMinute` tinyint(3) unsigned DEFAULT NULL,
  PRIMARY KEY (`season_id`,`energyPlan_id`,`peakType`,`isAM`),
  KEY `fk_energyPlanTOU_energyPlanSeason1_idx` (`season_id`,`energyPlan_id`),
  KEY `fk_energyPlanTOU_touLevel1_idx` (`peakType`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `energyPlanTier`
--

DROP TABLE IF EXISTS `energyPlanTier`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `energyPlanTier` (
  `id` tinyint(3) unsigned NOT NULL,
  `season_id` tinyint(3) unsigned NOT NULL,
  `energyPlan_id` bigint(20) unsigned NOT NULL,
  `kwh` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`,`season_id`,`energyPlan_id`),
  KEY `fk_eneryPlanTiers_energyPlanSeason1_idx` (`season_id`,`energyPlan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `energyRate`
--

DROP TABLE IF EXISTS `energyRate`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `energyRate` (
  `season_id` tinyint(3) unsigned NOT NULL,
  `energyPlan_id` bigint(20) unsigned NOT NULL,
  `peakType` tinyint(3) unsigned NOT NULL,
  `tier` tinyint(3) unsigned NOT NULL,
  `rate` decimal(18,9) NOT NULL,
  PRIMARY KEY (`season_id`,`energyPlan_id`,`peakType`,`tier`),
  KEY `fk_energyRate_energyPlanSeason1_idx` (`season_id`,`energyPlan_id`),
  KEY `fk_energyRate_energyPlanTOU1_idx` (`peakType`),
  KEY `fk_energyRate_eneryPlanTiers1_idx` (`tier`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `energydata`
--

DROP TABLE IF EXISTS `energydata`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `energydata` (
  `timestamp` bigint(20) unsigned NOT NULL,
  `mtu_id` bigint(20) unsigned NOT NULL,
  `account_id` bigint(20) unsigned NOT NULL,
  `energy` decimal(32,4) DEFAULT NULL,
  `energyDifference` decimal(21,6) DEFAULT NULL,
  `pf` decimal(6,3) unsigned DEFAULT NULL,
  `voltage` decimal(6,3) unsigned DEFAULT NULL,
  `avg5` decimal(23,4) DEFAULT '0.0000',
  `avg10` decimal(23,4) DEFAULT '0.0000',
  `avg20` decimal(23,4) DEFAULT '0.0000',
  `avg30` decimal(23,4) DEFAULT '0.0000',
  `avg15` decimal(23,4) DEFAULT '0.0000',
  `smoothing` tinyint(1) unsigned DEFAULT '0',
  `processed` tinyint(1) unsigned DEFAULT '0',
  PRIMARY KEY (`account_id`,`mtu_id`,`timestamp`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Table that stores all energy data posted from each gateway'
/*!50100 PARTITION BY HASH (account_id)
PARTITIONS 10 */;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `group`
--

DROP TABLE IF EXISTS `group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `group` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `account_id` bigint(20) unsigned NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`,`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `group_permission`
--

DROP TABLE IF EXISTS `group_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `group_permission` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `group_id` bigint(20) unsigned NOT NULL,
  `user_id` bigint(20) unsigned NOT NULL,
  `role` tinyint(3) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`,`group_id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `group_virtualecc`
--

DROP TABLE IF EXISTS `group_virtualecc`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `group_virtualecc` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `group_id` bigint(20) unsigned NOT NULL,
  `virtualECC_id` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `history_billingCycle`
--

DROP TABLE IF EXISTS `history_billingCycle`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `history_billingCycle` (
  `virtualECC_id` bigint(20) unsigned NOT NULL,
  `billingCycleMonth` int(10) unsigned NOT NULL,
  `billingCycleYear` int(10) unsigned NOT NULL,
  `startEpoch` bigint(20) unsigned NOT NULL,
  `endEpoch` bigint(20) unsigned NOT NULL DEFAULT '0',
  `net` double(21,6) NOT NULL DEFAULT '0.000000',
  `loadValue` double(21,6) NOT NULL DEFAULT '0.000000',
  `generation` double(21,6) NOT NULL DEFAULT '0.000000',
  `demandPeak` double(21,6) NOT NULL DEFAULT '0.000000',
  `demandPeakTime` bigint(20) NOT NULL DEFAULT '0',
  `demandCost` double(21,6) NOT NULL DEFAULT '0.000000',
  `demandCostPeakTime` bigint(20) NOT NULL DEFAULT '0',
  `demandCostPeak` double(21,6) NOT NULL DEFAULT '0.000000',
  `demandCostPeakTOU` tinyint(4) unsigned NOT NULL DEFAULT '0',
  `demandCostPeakTOUName` varchar(45) NOT NULL DEFAULT 'Off Peak',
  `minimumCharge` double(21,6) NOT NULL DEFAULT '0.000000',
  `fixedCharge` double(21,6) NOT NULL DEFAULT '0.000000',
  `minVoltage` double(6,2) unsigned NOT NULL DEFAULT '0.00',
  `minVoltageTime` bigint(20) unsigned NOT NULL DEFAULT '0',
  `peakVoltage` double(6,2) unsigned NOT NULL DEFAULT '0.00',
  `peakVoltageTime` bigint(20) unsigned NOT NULL DEFAULT '0',
  `pfTotal` double(10,2) unsigned NOT NULL DEFAULT '0.00',
  `mtuCount` int(10) unsigned NOT NULL DEFAULT '0',
  `touOffPeakNet` double(21,6) NOT NULL DEFAULT '0.000000',
  `touPeakNet` double(21,6) NOT NULL DEFAULT '0.000000',
  `touMidPeakNet` double(21,6) NOT NULL DEFAULT '0.000000',
  `touSuperPeakNet` double(21,6) NOT NULL DEFAULT '0.000000',
  `touOffPeakGen` double(21,6) NOT NULL DEFAULT '0.000000',
  `touPeakGen` double(21,6) NOT NULL DEFAULT '0.000000',
  `touMidPeakGen` double(21,6) NOT NULL DEFAULT '0.000000',
  `touSuperPeakGen` double(21,6) NOT NULL DEFAULT '0.000000',
  `touOffPeakLoad` double(21,6) NOT NULL DEFAULT '0.000000',
  `touPeakLoad` double(21,6) NOT NULL DEFAULT '0.000000',
  `touMidPeakLoad` double(21,6) NOT NULL DEFAULT '0.000000',
  `touSuperPeakLoad` double(21,6) NOT NULL DEFAULT '0.000000',
  `pfSampleCount` bigint(20) unsigned NOT NULL DEFAULT '0',
  `rateInEffect` double(21,6) NOT NULL DEFAULT '0.000000',
  `netCost` double(21,6) NOT NULL DEFAULT '0.000000',
  `loadCost` double(21,6) NOT NULL DEFAULT '0.000000',
  `genCost` double(21,6) NOT NULL DEFAULT '0.000000',
  `loadPeak` double(21,6) NOT NULL DEFAULT '0.000000',
  `loadPeakTime` bigint(20) NOT NULL DEFAULT '0',
  `generationPeak` double(21,6) NOT NULL DEFAULT '0.000000',
  `generationPeakTime` bigint(20) NOT NULL DEFAULT '0',
  `meterReadStartDate` int(11) unsigned DEFAULT '1',
  `meterReadDateChanged` int(1) unsigned DEFAULT '0',
  PRIMARY KEY (`virtualECC_id`,`billingCycleMonth`,`billingCycleYear`),
  UNIQUE KEY `EPOCH` (`virtualECC_id`,`startEpoch`,`endEpoch`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `history_day2`
--

DROP TABLE IF EXISTS `history_day2`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `history_day2` (
  `virtualECC_id` bigint(20) unsigned NOT NULL,
  `startEpoch` bigint(20) unsigned NOT NULL,
  `endEpoch` bigint(20) unsigned NOT NULL DEFAULT '0',
  `net` double(21,6) NOT NULL DEFAULT '0.000000',
  `loadValue` double(21,6) NOT NULL DEFAULT '0.000000',
  `generation` double(21,6) NOT NULL DEFAULT '0.000000',
  `demandPeak` double(21,6) NOT NULL DEFAULT '0.000000',
  `demandPeakTime` bigint(20) unsigned NOT NULL DEFAULT '0',
  `minVoltage` double(21,6) NOT NULL DEFAULT '0.000000',
  `minVoltageTime` bigint(20) unsigned NOT NULL DEFAULT '0',
  `peakVoltage` double(21,6) NOT NULL DEFAULT '0.000000',
  `peakVoltageTime` bigint(20) unsigned NOT NULL DEFAULT '0',
  `pfTotal` double(10,2) unsigned NOT NULL DEFAULT '0.00',
  `mtuCount` int(10) unsigned NOT NULL DEFAULT '0',
  `touOffPeakNet` double(21,6) NOT NULL DEFAULT '0.000000',
  `touPeakNet` double(21,6) NOT NULL DEFAULT '0.000000',
  `touMidPeakNet` double(21,6) NOT NULL DEFAULT '0.000000',
  `touSuperPeakNet` double(21,6) NOT NULL DEFAULT '0.000000',
  `touOffPeakGen` double(21,6) NOT NULL DEFAULT '0.000000',
  `touPeakGen` double(21,6) NOT NULL DEFAULT '0.000000',
  `touMidPeakGen` double(21,6) NOT NULL DEFAULT '0.000000',
  `touSuperPeakGen` double(21,6) NOT NULL DEFAULT '0.000000',
  `touOffPeakLoad` double(21,6) NOT NULL DEFAULT '0.000000',
  `touPeakLoad` double(21,6) NOT NULL DEFAULT '0.000000',
  `touMidPeakLoad` double(21,6) NOT NULL DEFAULT '0.000000',
  `touSuperPeakLoad` double(21,6) NOT NULL DEFAULT '0.000000',
  `pfSampleCount` bigint(20) unsigned NOT NULL DEFAULT '0',
  `rateInEffect` double(21,6) NOT NULL DEFAULT '0.000000',
  `netCost` double(21,6) NOT NULL DEFAULT '0.000000',
  `loadCost` double(21,6) NOT NULL DEFAULT '0.000000',
  `genCost` double(21,6) NOT NULL DEFAULT '0.000000',
  `loadPeak` double(21,6) NOT NULL DEFAULT '0.000000',
  `loadPeakTime` bigint(20) NOT NULL DEFAULT '0',
  `generationPeak` double(21,6) NOT NULL DEFAULT '0.000000',
  `generationPeakTime` bigint(20) NOT NULL DEFAULT '0',
  `billingCycleMonth` int(11) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`virtualECC_id`,`startEpoch`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `history_hour2`
--

DROP TABLE IF EXISTS `history_hour2`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `history_hour2` (
  `virtualECC_id` bigint(20) unsigned NOT NULL,
  `startEpoch` bigint(20) unsigned NOT NULL,
  `endEpoch` bigint(20) unsigned NOT NULL DEFAULT '0',
  `net` double(21,6) NOT NULL DEFAULT '0.000000',
  `loadValue` double(21,6) NOT NULL DEFAULT '0.000000',
  `generation` double(21,6) NOT NULL DEFAULT '0.000000',
  `demandPeak` double(21,6) NOT NULL DEFAULT '0.000000',
  `demandPeakTime` bigint(20) unsigned NOT NULL DEFAULT '0',
  `minVoltage` double(21,6) NOT NULL DEFAULT '0.000000',
  `minVoltageTime` bigint(20) unsigned NOT NULL DEFAULT '0',
  `peakVoltage` double(21,6) NOT NULL DEFAULT '0.000000',
  `peakVoltageTime` bigint(20) unsigned NOT NULL DEFAULT '0',
  `pfTotal` double(10,2) unsigned NOT NULL DEFAULT '0.00',
  `mtuCount` int(10) unsigned NOT NULL DEFAULT '0',
  `touOffPeakNet` double(21,6) NOT NULL DEFAULT '0.000000',
  `touPeakNet` double(21,6) NOT NULL DEFAULT '0.000000',
  `touMidPeakNet` double(21,6) NOT NULL DEFAULT '0.000000',
  `touSuperPeakNet` double(21,6) NOT NULL DEFAULT '0.000000',
  `touOffPeakGen` double(21,6) NOT NULL DEFAULT '0.000000',
  `touPeakGen` double(21,6) NOT NULL DEFAULT '0.000000',
  `touMidPeakGen` double(21,6) NOT NULL DEFAULT '0.000000',
  `touSuperPeakGen` double(21,6) NOT NULL DEFAULT '0.000000',
  `touOffPeakLoad` double(21,6) NOT NULL DEFAULT '0.000000',
  `touPeakLoad` double(21,6) NOT NULL DEFAULT '0.000000',
  `touMidPeakLoad` double(21,6) NOT NULL DEFAULT '0.000000',
  `touSuperPeakLoad` double(21,6) NOT NULL DEFAULT '0.000000',
  `minCount` int(10) unsigned NOT NULL DEFAULT '0',
  `pfSampleCount` bigint(20) unsigned NOT NULL DEFAULT '0',
  `rateInEffect` double(21,6) NOT NULL DEFAULT '0.000000',
  `netCost` double(21,6) NOT NULL DEFAULT '0.000000',
  `loadCost` double(21,6) NOT NULL DEFAULT '0.000000',
  `genCost` double(21,6) NOT NULL DEFAULT '0.000000',
  `loadPeak` double(21,6) NOT NULL DEFAULT '0.000000',
  `loadPeakTime` bigint(20) NOT NULL DEFAULT '0',
  `generationPeak` double(21,6) NOT NULL DEFAULT '0.000000',
  `generationPeakTime` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`virtualECC_id`,`startEpoch`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `history_minute2`
--

DROP TABLE IF EXISTS `history_minute2`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `history_minute2` (
  `virtualECC_id` bigint(20) unsigned NOT NULL,
  `startEpoch` bigint(20) unsigned NOT NULL,
  `net` double(21,6) NOT NULL DEFAULT '0.000000',
  `loadValue` double(21,6) NOT NULL DEFAULT '0.000000',
  `generation` double(21,6) NOT NULL DEFAULT '0.000000',
  `demandPeak` double(21,6) NOT NULL DEFAULT '0.000000',
  `loadPeak` double(21,6) NOT NULL DEFAULT '0.000000',
  `generationPeak` double(21,6) NOT NULL DEFAULT '0.000000',
  `voltageTotal` double(21,6) NOT NULL DEFAULT '0.000000',
  `pfTotal` double(10,2) unsigned NOT NULL DEFAULT '0.00',
  `mtuCount` int(11) unsigned NOT NULL DEFAULT '0',
  `touOffPeakNet` double(21,6) NOT NULL DEFAULT '0.000000',
  `touPeakNet` double(21,6) NOT NULL DEFAULT '0.000000',
  `touMidPeakNet` double(21,6) NOT NULL DEFAULT '0.000000',
  `touSuperPeakNet` double(21,6) NOT NULL DEFAULT '0.000000',
  `touOffPeakGen` double(21,6) NOT NULL DEFAULT '0.000000',
  `touPeakGen` double(21,6) NOT NULL DEFAULT '0.000000',
  `touMidPeakGen` double(21,6) NOT NULL DEFAULT '0.000000',
  `touSuperPeakGen` double(21,6) NOT NULL DEFAULT '0.000000',
  `touOffPeakLoad` double(21,6) NOT NULL DEFAULT '0.000000',
  `touPeakLoad` double(21,6) NOT NULL DEFAULT '0.000000',
  `touMidPeakLoad` double(21,6) NOT NULL DEFAULT '0.000000',
  `touSuperPeakLoad` double(21,6) NOT NULL DEFAULT '0.000000',
  `pfSampleCount` bigint(20) unsigned NOT NULL DEFAULT '0',
  `runningNetTotal` double(32,4) NOT NULL DEFAULT '0.0000',
  `rateInEffect` double(21,6) NOT NULL DEFAULT '0.000000',
  `netCost` double(21,6) NOT NULL DEFAULT '0.000000',
  `loadCost` double(21,6) NOT NULL DEFAULT '0.000000',
  `genCost` double(21,6) NOT NULL DEFAULT '0.000000',
  `dow` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`virtualECC_id`,`startEpoch`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `history_mtu_billingCycle`
--

DROP TABLE IF EXISTS `history_mtu_billingCycle`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `history_mtu_billingCycle` (
  `virtualECC_id` bigint(20) unsigned NOT NULL,
  `mtu_id` bigint(20) NOT NULL,
  `billingCycleMonth` int(11) unsigned NOT NULL,
  `billingCycleYear` int(11) unsigned NOT NULL,
  `startEpoch` bigint(20) unsigned NOT NULL,
  `endEpoch` bigint(20) unsigned NOT NULL DEFAULT '0',
  `energy` double(21,6) NOT NULL DEFAULT '0.000000',
  `cost` double(21,6) NOT NULL DEFAULT '0.000000',
  `demandPeak` double(21,6) NOT NULL DEFAULT '0.000000',
  `demandPeakTime` bigint(20) unsigned NOT NULL DEFAULT '0',
  `touOffPeak` double(21,6) NOT NULL DEFAULT '0.000000',
  `touMidPeak` double(21,6) NOT NULL DEFAULT '0.000000',
  `touPeak` double(21,6) NOT NULL DEFAULT '0.000000',
  `touSuperPeak` double(21,6) NOT NULL DEFAULT '0.000000',
  `peakVoltage` double(6,3) NOT NULL DEFAULT '0.000',
  `peakVoltageTime` bigint(20) NOT NULL DEFAULT '0',
  `minVoltage` double(6,3) NOT NULL DEFAULT '0.000',
  `minVoltageTime` bigint(20) NOT NULL DEFAULT '0',
  `pfTotal` double(10,2) DEFAULT '0.00',
  `pfSampleCount` bigint(20) DEFAULT '0',
  PRIMARY KEY (`virtualECC_id`,`mtu_id`,`billingCycleMonth`,`billingCycleYear`),
  KEY `BILLINGCYCLE` (`virtualECC_id`,`billingCycleYear`,`billingCycleMonth`),
  KEY `EPOCH` (`virtualECC_id`,`mtu_id`,`startEpoch`,`endEpoch`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `history_mtu_day2`
--

DROP TABLE IF EXISTS `history_mtu_day2`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `history_mtu_day2` (
  `virtualECC_id` bigint(20) unsigned NOT NULL,
  `mtu_id` bigint(20) NOT NULL,
  `startEpoch` bigint(20) unsigned NOT NULL,
  `endEpoch` bigint(20) unsigned NOT NULL DEFAULT '0',
  `energy` double(21,6) NOT NULL DEFAULT '0.000000',
  `cost` double(21,6) NOT NULL DEFAULT '0.000000',
  `demandPeak` double(21,6) NOT NULL DEFAULT '0.000000',
  `demandPeakTime` bigint(20) unsigned NOT NULL DEFAULT '0',
  `touOffPeak` double(21,6) NOT NULL DEFAULT '0.000000',
  `touMidPeak` double(21,6) NOT NULL DEFAULT '0.000000',
  `touPeak` double(21,6) NOT NULL DEFAULT '0.000000',
  `touSuperPeak` double(21,6) NOT NULL DEFAULT '0.000000',
  `peakVoltage` double(6,3) NOT NULL DEFAULT '0.000',
  `peakVoltageTime` bigint(20) NOT NULL DEFAULT '0',
  `minVoltage` double(6,3) NOT NULL DEFAULT '0.000',
  `minVoltageTime` bigint(20) NOT NULL DEFAULT '0',
  `pfTotal` double(10,2) DEFAULT '0.00',
  `pfSampleCount` bigint(20) DEFAULT '0',
  PRIMARY KEY (`virtualECC_id`,`mtu_id`,`startEpoch`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `history_mtu_hour2`
--

DROP TABLE IF EXISTS `history_mtu_hour2`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `history_mtu_hour2` (
  `virtualECC_id` bigint(20) unsigned NOT NULL,
  `mtu_id` bigint(20) NOT NULL,
  `startEpoch` bigint(20) unsigned NOT NULL,
  `endEpoch` bigint(20) unsigned NOT NULL DEFAULT '0',
  `energy` double(21,6) NOT NULL DEFAULT '0.000000',
  `cost` double(21,6) NOT NULL DEFAULT '0.000000',
  `demandPeak` double(21,6) NOT NULL DEFAULT '0.000000',
  `demandPeakTime` bigint(20) unsigned NOT NULL DEFAULT '0',
  `touOffPeak` double(21,6) NOT NULL DEFAULT '0.000000',
  `touMidPeak` double(21,6) NOT NULL DEFAULT '0.000000',
  `touPeak` double(21,6) NOT NULL DEFAULT '0.000000',
  `touSuperPeak` double(21,6) NOT NULL DEFAULT '0.000000',
  `peakVoltage` double(6,3) NOT NULL DEFAULT '0.000',
  `peakVoltageTime` bigint(20) NOT NULL DEFAULT '0',
  `minVoltage` double(6,3) NOT NULL DEFAULT '0.000',
  `minVoltageTime` bigint(20) NOT NULL DEFAULT '0',
  `pfTotal` double(10,2) DEFAULT '0.00',
  `pfSampleCount` bigint(20) DEFAULT '0',
  PRIMARY KEY (`virtualECC_id`,`mtu_id`,`startEpoch`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `java_tz`
--

DROP TABLE IF EXISTS `java_tz`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `java_tz` (
  `name` varchar(255) NOT NULL,
  `name2` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mtu`
--

DROP TABLE IF EXISTS `mtu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mtu` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `account_id` bigint(20) unsigned NOT NULL,
  `mtuType` tinyint(4) unsigned DEFAULT NULL,
  `description` varchar(1000) DEFAULT NULL,
  `spyder` tinyint(1) unsigned DEFAULT '0',
  `lastpost` bigint(20) unsigned NOT NULL DEFAULT '0',
  `validation` bigint(20) NOT NULL DEFAULT '0',
  `no_negative` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`,`account_id`),
  KEY `ACCOUNT` (`account_id`)
) ENGINE=InnoDB AUTO_INCREMENT=639397646 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `oauth_access_token`
--

DROP TABLE IF EXISTS `oauth_access_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `oauth_access_token` (
  `token_id` varchar(256) DEFAULT NULL,
  `token` blob,
  `authentication_id` varchar(256) NOT NULL,
  `user_name` varchar(256) DEFAULT NULL,
  `client_id` varchar(256) DEFAULT NULL,
  `authentication` blob,
  `refresh_token` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`authentication_id`),
  UNIQUE KEY `authentication_id_UNIQUE` (`authentication_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `oauth_client_details`
--

DROP TABLE IF EXISTS `oauth_client_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `oauth_client_details` (
  `client_id` varchar(256) NOT NULL,
  `resource_ids` varchar(256) DEFAULT NULL,
  `client_secret` varchar(256) DEFAULT NULL,
  `scope` varchar(256) DEFAULT NULL,
  `authorized_grant_types` varchar(256) DEFAULT NULL,
  `web_server_redirect_uri` varchar(256) DEFAULT NULL,
  `authorities` varchar(256) DEFAULT NULL,
  `access_token_validity` int(11) unsigned zerofill DEFAULT NULL,
  `refresh_token_validity` int(11) DEFAULT NULL,
  `additional_information` varchar(4096) DEFAULT NULL,
  `autoapprove` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`client_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `oauth_code`
--

DROP TABLE IF EXISTS `oauth_code`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `oauth_code` (
  `code` varchar(256) DEFAULT NULL,
  `authentication` blob
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `oauth_refresh_token`
--

DROP TABLE IF EXISTS `oauth_refresh_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `oauth_refresh_token` (
  `token_id` varchar(256) DEFAULT NULL,
  `token` blob,
  `authentication` blob
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `playback_queue`
--

DROP TABLE IF EXISTS `playback_queue`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `playback_queue` (
  `virtualECC_id` bigint(20) unsigned NOT NULL,
  `startDate` bigint(20) unsigned NOT NULL,
  `endDate` bigint(20) unsigned NOT NULL,
  `server` int(11) unsigned NOT NULL DEFAULT '0',
  `completed` int(11) unsigned NOT NULL DEFAULT '0',
  `running` tinyint(1) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`virtualECC_id`,`startDate`,`endDate`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Temporary table structure for view `processlist`
--

DROP TABLE IF EXISTS `processlist`;
/*!50001 DROP VIEW IF EXISTS `processlist`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE VIEW `processlist` AS SELECT 
 1 AS `ID`,
 1 AS `USER`,
 1 AS `HOST`,
 1 AS `DB`,
 1 AS `COMMAND`,
 1 AS `TIME`,
 1 AS `STATE`,
 1 AS `INFO`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `push_id`
--

DROP TABLE IF EXISTS `push_id`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `push_id` (
  `registrationId` varchar(767) NOT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  `ios` tinyint(1) NOT NULL,
  `adm` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`registrationId`),
  UNIQUE KEY `registrationId_UNIQUE` (`registrationId`),
  KEY `USER` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sumter_import`
--

DROP TABLE IF EXISTS `sumter_import`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sumter_import` (
  `mtu_hex` varchar(10) NOT NULL,
  `mtu_id` bigint(20) NOT NULL,
  PRIMARY KEY (`mtu_hex`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `test`
--

DROP TABLE IF EXISTS `test`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `test` (
  `id` bigint(20) NOT NULL,
  `id2` bigint(20) NOT NULL,
  `value` int(11) DEFAULT NULL,
  `value2` int(11) DEFAULT '0',
  PRIMARY KEY (`id`,`id2`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `third_party_post`
--

DROP TABLE IF EXISTS `third_party_post`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `third_party_post` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `virtualECC_id` bigint(20) unsigned NOT NULL,
  `server` varchar(255) DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  `port` int(11) unsigned NOT NULL DEFAULT '80',
  `securityKey` varchar(16) DEFAULT NULL,
  `useSSL` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `postRate` int(11) unsigned NOT NULL DEFAULT '1',
  `highPrec` tinyint(1) unsigned NOT NULL DEFAULT '1',
  `spyder` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `lastPost` bigint(20) unsigned NOT NULL DEFAULT '0',
  `description` varchar(45) DEFAULT NULL,
  `attempts` int(10) unsigned NOT NULL DEFAULT '0',
  `success` int(10) unsigned NOT NULL DEFAULT '0',
  `thirdPartyPostState` tinyint(4) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='	';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `touLevel`
--

DROP TABLE IF EXISTS `touLevel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `touLevel` (
  `peakType` tinyint(4) unsigned NOT NULL,
  `energyPlan_id` bigint(20) unsigned NOT NULL,
  `touLevelName` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`peakType`,`energyPlan_id`),
  KEY `fk_touLevel_energyPlan1_idx` (`energyPlan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `username` varchar(200) NOT NULL,
  `password` varchar(45) DEFAULT NULL,
  `firstName` varchar(50) DEFAULT NULL,
  `lastName` varchar(50) DEFAULT NULL,
  `middleName` varchar(50) DEFAULT NULL,
  `state` tinyint(4) DEFAULT NULL,
  `emailActivationKey` varchar(45) DEFAULT NULL,
  `joinDate` bigint(20) DEFAULT NULL,
  `adminRole` tinyint(1) unsigned zerofill NOT NULL DEFAULT '0',
  `permissionAdvice` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `permission5kPost` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `alexaAccessToken` varchar(45) DEFAULT NULL,
  `billingEnabled` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username_UNIQUE` (`username`),
  KEY `username` (`username`),
  KEY `alexa` (`alexaAccessToken`)
) ENGINE=InnoDB AUTO_INCREMENT=3552 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Temporary table structure for view `user_account_view`
--

DROP TABLE IF EXISTS `user_account_view`;
/*!50001 DROP VIEW IF EXISTS `user_account_view`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE VIEW `user_account_view` AS SELECT 
 1 AS `user_id`,
 1 AS `account_id`,
 1 AS `id`,
 1 AS `username`,
 1 AS `password`,
 1 AS `firstName`,
 1 AS `lastName`,
 1 AS `middleName`,
 1 AS `state`,
 1 AS `emailActivationKey`,
 1 AS `joinDate`,
 1 AS `adminRole`,
 1 AS `permissionAdvice`,
 1 AS `permission5kPost`,
 1 AS `alexaAccessToken`,
 1 AS `billingEnabled`,
 1 AS `accountName`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `utility_resource`
--

DROP TABLE IF EXISTS `utility_resource`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `utility_resource` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `utility_name` varchar(300) DEFAULT NULL,
  `utility_loc` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1770 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `virtualECC`
--

DROP TABLE IF EXISTS `virtualECC`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `virtualECC` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `account_id` bigint(20) unsigned NOT NULL,
  `eccName` varchar(245) DEFAULT NULL,
  `timezone` varchar(45) DEFAULT NULL,
  `energyPlan_id` bigint(20) unsigned DEFAULT NULL,
  `street1` varchar(255) DEFAULT NULL,
  `street2` varchar(255) DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `state` varchar(3) DEFAULT NULL,
  `postal` varchar(255) DEFAULT NULL,
  `country` varchar(3) DEFAULT NULL,
  `systemType` int(1) DEFAULT '0',
  `weatherId` bigint(20) DEFAULT NULL,
  `pvoutputKey` varchar(45) DEFAULT NULL,
  `converted` int(1) unsigned NOT NULL DEFAULT '0',
  `convertServer` int(10) unsigned NOT NULL DEFAULT '0',
  `donotconvert` int(1) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `fk_virtualECC_user1_idx` (`account_id`),
  KEY `fk_virtualECC_energyPlan1_idx` (`energyPlan_id`),
  KEY `ACCOUNT` (`account_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8163 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `virtualECCMTU`
--

DROP TABLE IF EXISTS `virtualECCMTU`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `virtualECCMTU` (
  `virtualECC_id` bigint(20) unsigned NOT NULL,
  `mtu_id` bigint(20) unsigned NOT NULL,
  `account_id` bigint(20) unsigned NOT NULL,
  `mtuType` tinyint(4) DEFAULT NULL,
  `mtuDescription` varchar(255) DEFAULT NULL,
  `powerMultiplier` decimal(7,4) DEFAULT NULL,
  `voltageMultiplier` decimal(7,4) DEFAULT NULL,
  `lastPost` bigint(20) unsigned DEFAULT '0',
  PRIMARY KEY (`virtualECC_id`,`mtu_id`,`account_id`),
  KEY `PT` (`virtualECC_id`,`account_id`,`mtuType`),
  KEY `VIRTUALECC` (`virtualECC_id`),
  KEY `MTU` (`mtu_id`),
  KEY `MTU_ACCOUNT` (`mtu_id`,`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `virtualecc_energyplan`
--

DROP TABLE IF EXISTS `virtualecc_energyplan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `virtualecc_energyplan` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `energyPlan_id` bigint(20) unsigned NOT NULL DEFAULT '0',
  `virtualECC_id` bigint(20) unsigned NOT NULL,
  `startDate` bigint(20) unsigned NOT NULL DEFAULT '1293840000',
  PRIMARY KEY (`id`),
  KEY `EFFECTIVE` (`virtualECC_id`,`startDate`)
) ENGINE=InnoDB AUTO_INCREMENT=543 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `virtualecc_permission`
--

DROP TABLE IF EXISTS `virtualecc_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `virtualecc_permission` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `virtualecc_id` bigint(20) unsigned NOT NULL,
  `user_id` bigint(20) unsigned NOT NULL,
  `role` tinyint(3) unsigned NOT NULL,
  PRIMARY KEY (`id`,`virtualecc_id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Temporary table structure for view `weatherHistoryTimestamps`
--

DROP TABLE IF EXISTS `weatherHistoryTimestamps`;
/*!50001 DROP VIEW IF EXISTS `weatherHistoryTimestamps`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE VIEW `weatherHistoryTimestamps` AS SELECT 
 1 AS `timestamp`,
 1 AS `weatherId`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `weatherKey`
--

DROP TABLE IF EXISTS `weatherKey`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `weatherKey` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `lat` decimal(9,2) DEFAULT NULL,
  `lon` decimal(9,2) DEFAULT NULL,
  `cityId` bigint(20) DEFAULT NULL,
  `metric` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1437 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Temporary table structure for view `weatherQueue`
--

DROP TABLE IF EXISTS `weatherQueue`;
/*!50001 DROP VIEW IF EXISTS `weatherQueue`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE VIEW `weatherQueue` AS SELECT 
 1 AS `id`,
 1 AS `lat`,
 1 AS `lon`,
 1 AS `cityId`,
 1 AS `metric`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `weather_history`
--

DROP TABLE IF EXISTS `weather_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `weather_history` (
  `timestamp` bigint(20) NOT NULL,
  `weatherId` int(11) NOT NULL,
  `temp` decimal(6,3) DEFAULT NULL,
  `windspeed` int(11) DEFAULT NULL,
  `clouds` int(11) DEFAULT NULL,
  PRIMARY KEY (`timestamp`,`weatherId`),
  KEY `TIMESTAMP` (`timestamp`),
  KEY `SECONDARY` (`weatherId`,`timestamp`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Final view structure for view `processlist`
--

/*!50001 DROP VIEW IF EXISTS `processlist`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`commander`@`%` SQL SECURITY DEFINER */
/*!50001 VIEW `processlist` AS select `information_schema`.`PROCESSLIST`.`ID` AS `ID`,`information_schema`.`PROCESSLIST`.`USER` AS `USER`,`information_schema`.`PROCESSLIST`.`HOST` AS `HOST`,`information_schema`.`PROCESSLIST`.`DB` AS `DB`,`information_schema`.`PROCESSLIST`.`COMMAND` AS `COMMAND`,`information_schema`.`PROCESSLIST`.`TIME` AS `TIME`,`information_schema`.`PROCESSLIST`.`STATE` AS `STATE`,`information_schema`.`PROCESSLIST`.`INFO` AS `INFO` from `INFORMATION_SCHEMA`.`PROCESSLIST` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `user_account_view`
--

/*!50001 DROP VIEW IF EXISTS `user_account_view`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`commander`@`%` SQL SECURITY DEFINER */
/*!50001 VIEW `user_account_view` AS select `am`.`user_id` AS `user_id`,`am`.`account_id` AS `account_id`,`u`.`id` AS `id`,`u`.`username` AS `username`,`u`.`password` AS `password`,`u`.`firstName` AS `firstName`,`u`.`lastName` AS `lastName`,`u`.`middleName` AS `middleName`,`u`.`state` AS `state`,`u`.`emailActivationKey` AS `emailActivationKey`,`u`.`joinDate` AS `joinDate`,`u`.`adminRole` AS `adminRole`,`u`.`permissionAdvice` AS `permissionAdvice`,`u`.`permission5kPost` AS `permission5kPost`,`u`.`alexaAccessToken` AS `alexaAccessToken`,`u`.`billingEnabled` AS `billingEnabled`,`a`.`accountName` AS `accountName` from ((`user` `u` straight_join `account_member` `am` on((`u`.`id` = `am`.`user_id`))) straight_join `account` `a` on((`a`.`id` = `am`.`account_id`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `weatherHistoryTimestamps`
--

/*!50001 DROP VIEW IF EXISTS `weatherHistoryTimestamps`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`commander`@`%` SQL SECURITY DEFINER */
/*!50001 VIEW `weatherHistoryTimestamps` AS select max(`weather_history`.`timestamp`) AS `timestamp`,`weather_history`.`weatherId` AS `weatherId` from `weather_history` group by `weather_history`.`weatherId` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `weatherQueue`
--

/*!50001 DROP VIEW IF EXISTS `weatherQueue`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`commander`@`%` SQL SECURITY DEFINER */
/*!50001 VIEW `weatherQueue` AS select distinct `wk`.`id` AS `id`,`wk`.`lat` AS `lat`,`wk`.`lon` AS `lon`,`wk`.`cityId` AS `cityId`,`wk`.`metric` AS `metric` from (`weatherKey` `wk` left join `weatherHistoryTimestamps` `wh` on((`wk`.`id` = `wh`.`weatherId`))) where (isnull(`wh`.`timestamp`) or ((unix_timestamp(now()) - `wh`.`timestamp`) > 3600)) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-06-12 16:01:29
