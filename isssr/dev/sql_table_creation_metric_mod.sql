-- MySQL dump 10.13  Distrib 5.6.12, for Win64 (x86_64)
--
-- Host: localhost    Database: GQM_BLIND
-- ------------------------------------------------------
-- Server version	5.6.12-log

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
-- Table structure for table `app_user`
--

DROP TABLE IF EXISTS `app_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `app_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `account_expired` bit(1) NOT NULL,
  `account_locked` bit(1) NOT NULL,
  `address` varchar(150) DEFAULT NULL,
  `city` varchar(50) DEFAULT NULL,
  `country` varchar(100) DEFAULT NULL,
  `postal_code` varchar(15) DEFAULT NULL,
  `province` varchar(100) DEFAULT NULL,
  `credentials_expired` bit(1) NOT NULL,
  `email` varchar(255) NOT NULL,
  `account_enabled` bit(1) DEFAULT NULL,
  `first_name` varchar(50) NOT NULL,
  `last_name` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `password_hint` varchar(255) DEFAULT NULL,
  `phone_number` varchar(255) DEFAULT NULL,
  `username` varchar(50) NOT NULL,
  `version` int(11) DEFAULT NULL,
  `website` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `goal`
--

DROP TABLE IF EXISTS `goal`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `goal` (
  `goal_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `activity` varchar(255) DEFAULT NULL,
  `constraints` varchar(255) DEFAULT NULL,
  `context` varchar(255) DEFAULT NULL,
  `description` varchar(255) NOT NULL,
  `focus` varchar(255) DEFAULT NULL,
  `impact_of_variation` varchar(255) DEFAULT NULL,
  `interpretation_model` int(11) DEFAULT NULL,
  `magnitude` varchar(255) DEFAULT NULL,
  `object` varchar(255) DEFAULT NULL,
  `refinement` varchar(255) DEFAULT NULL,
  `relations` varchar(255) DEFAULT NULL,
  `scope` varchar(255) DEFAULT NULL,
  `status` varchar(50) DEFAULT NULL,
  `subject` varchar(255) DEFAULT NULL,
  `timeframe` varchar(255) DEFAULT NULL,
  `type` varchar(255) NOT NULL,
  `viewpoint` varchar(255) DEFAULT NULL,
  `ge_id` bigint(20) DEFAULT NULL,
  `go_id` bigint(20) NOT NULL,
  `parent_id` bigint(20) DEFAULT NULL,
  `project_id` bigint(20) NOT NULL,
  `strategy_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`goal_id`),
  UNIQUE KEY `goal_id` (`goal_id`),
  KEY `FK21F33318496EA4` (`strategy_id`),
  KEY `FK21F3333A83F18` (`go_id`),
  KEY `FK21F333BA9B7810` (`project_id`),
  KEY `FK21F3333A3B362` (`ge_id`),
  KEY `FK21F333C0683AAD` (`parent_id`),
  CONSTRAINT `FK21F33318496EA4` FOREIGN KEY (`strategy_id`) REFERENCES `strategy` (`strategy_id`),
  CONSTRAINT `FK21F3333A3B362` FOREIGN KEY (`ge_id`) REFERENCES `app_user` (`id`),
  CONSTRAINT `FK21F3333A83F18` FOREIGN KEY (`go_id`) REFERENCES `app_user` (`id`),
  CONSTRAINT `FK21F333BA9B7810` FOREIGN KEY (`project_id`) REFERENCES `project` (`project_id`),
  CONSTRAINT `FK21F333C0683AAD` FOREIGN KEY (`parent_id`) REFERENCES `goal` (`goal_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `goal_mmdm`
--

DROP TABLE IF EXISTS `goal_mmdm`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `goal_mmdm` (
  `goal_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`goal_id`,`user_id`),
  KEY `FK7B20DFD55096BFA4` (`goal_id`),
  KEY `FK7B20DFD5F503D155` (`user_id`),
  CONSTRAINT `FK7B20DFD55096BFA4` FOREIGN KEY (`goal_id`) REFERENCES `goal` (`goal_id`),
  CONSTRAINT `FK7B20DFD5F503D155` FOREIGN KEY (`user_id`) REFERENCES `app_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `goal_qs`
--

DROP TABLE IF EXISTS `goal_qs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `goal_qs` (
  `goal_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`goal_id`,`user_id`),
  KEY `FKB9536CE5096BFA4` (`goal_id`),
  KEY `FKB9536CEF503D155` (`user_id`),
  CONSTRAINT `FKB9536CE5096BFA4` FOREIGN KEY (`goal_id`) REFERENCES `goal` (`goal_id`),
  CONSTRAINT `FKB9536CEF503D155` FOREIGN KEY (`user_id`) REFERENCES `app_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `goal_question`
--

DROP TABLE IF EXISTS `goal_question`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `goal_question` (
  `refinement` varchar(255) DEFAULT NULL,
  `status` varchar(50) DEFAULT NULL,
  `goal_id` bigint(20) NOT NULL DEFAULT '0',
  `question_id` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`goal_id`,`question_id`),
  KEY `FK864933925096BFA4` (`goal_id`),
  KEY `FK8649339256B5C4` (`question_id`),
  CONSTRAINT `FK864933925096BFA4` FOREIGN KEY (`goal_id`) REFERENCES `goal` (`goal_id`),
  CONSTRAINT `FK8649339256B5C4` FOREIGN KEY (`question_id`) REFERENCES `question` (`question_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `goal_vote`
--

DROP TABLE IF EXISTS `goal_vote`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `goal_vote` (
  `goal_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`goal_id`,`user_id`),
  KEY `FK7B2500965096BFA4` (`goal_id`),
  KEY `FK7B250096F503D155` (`user_id`),
  CONSTRAINT `FK7B2500965096BFA4` FOREIGN KEY (`goal_id`) REFERENCES `goal` (`goal_id`),
  CONSTRAINT `FK7B250096F503D155` FOREIGN KEY (`user_id`) REFERENCES `app_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `measurement`
--

DROP TABLE IF EXISTS `measurement`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `measurement` (
  `measurement_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `collecting_d` date DEFAULT NULL,
  `collecting_t` varchar(255) DEFAULT NULL,
  `ts` datetime DEFAULT NULL,
  `value` double DEFAULT NULL,
  `measuremento_id` bigint(20) DEFAULT NULL,
  `metric_id` bigint(20) NOT NULL,
  PRIMARY KEY (`measurement_id`),
  KEY `FKF75C839C616367ED` (`measuremento_id`),
  KEY `FKF75C839CF24AD404` (`metric_id`),
  CONSTRAINT `FKF75C839C616367ED` FOREIGN KEY (`measuremento_id`) REFERENCES `app_user` (`id`),
  CONSTRAINT `FKF75C839CF24AD404` FOREIGN KEY (`metric_id`) REFERENCES `metric` (`metric_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `metric`
--

DROP TABLE IF EXISTS `metric`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `metric` (
  `metric_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `actual_value` double DEFAULT NULL,
  `code` varchar(50) NOT NULL,
  `collecting_type` varchar(50) DEFAULT NULL,
  `hypothesis` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `operation` varchar(50) DEFAULT NULL,
  `satisfying_condition_peration` varchar(50) DEFAULT NULL,
  `satisfying_condition_value` double DEFAULT NULL,
  `keywords` varchar(255) DEFAULT NULL,
  `type` varchar(50) DEFAULT NULL,
  `metric_a_id` bigint(20) DEFAULT NULL,
  `metric_b_id` bigint(20) DEFAULT NULL,
  `mmdmo_id` bigint(20) NOT NULL,
  `project_id` bigint(20) NOT NULL,
  `scale_id` bigint(20) DEFAULT NULL,
  `unit_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`metric_id`),
  KEY `FK892AE1D0ACE7D0E2` (`metric_a_id`),
  KEY `FK892AE1D0BA9B7810` (`project_id`),
  KEY `FK892AE1D06F88BFDA` (`mmdmo_id`),
  KEY `FK892AE1D026AAC3F0` (`scale_id`),
  KEY `FK892AE1D0ACE84541` (`metric_b_id`),
  KEY `FK892AE1D033F3DE04` (`unit_id`),
  CONSTRAINT `FK892AE1D026AAC3F0` FOREIGN KEY (`scale_id`) REFERENCES `scale` (`scale_id`),
  CONSTRAINT `FK892AE1D033F3DE04` FOREIGN KEY (`unit_id`) REFERENCES `unit` (`unit_id`),
  CONSTRAINT `FK892AE1D06F88BFDA` FOREIGN KEY (`mmdmo_id`) REFERENCES `app_user` (`id`),
  CONSTRAINT `FK892AE1D0ACE7D0E2` FOREIGN KEY (`metric_a_id`) REFERENCES `metric` (`metric_id`),
  CONSTRAINT `FK892AE1D0ACE84541` FOREIGN KEY (`metric_b_id`) REFERENCES `metric` (`metric_id`),
  CONSTRAINT `FK892AE1D0BA9B7810` FOREIGN KEY (`project_id`) REFERENCES `project` (`project_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `metric`
--

--
-- Table structure for table `project`
--

DROP TABLE IF EXISTS `project`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `project` (
  `project_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(50) DEFAULT NULL,
  `po_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`project_id`),
  KEY `FK50C8E2F942712A1` (`po_id`),
  CONSTRAINT `FK50C8E2F942712A1` FOREIGN KEY (`po_id`) REFERENCES `app_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `project_gqm`
--

DROP TABLE IF EXISTS `project_gqm`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `project_gqm` (
  `project_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  KEY `FKC7FFD69DBA9B7810` (`project_id`),
  KEY `FKC7FFD69DF503D155` (`user_id`),
  CONSTRAINT `FKC7FFD69DBA9B7810` FOREIGN KEY (`project_id`) REFERENCES `project` (`project_id`),
  CONSTRAINT `FKC7FFD69DF503D155` FOREIGN KEY (`user_id`) REFERENCES `app_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `project_pm`
--

DROP TABLE IF EXISTS `project_pm`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `project_pm` (
  `project_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  KEY `FK8A94A4E3BA9B7810` (`project_id`),
  KEY `FK8A94A4E3F503D155` (`user_id`),
  CONSTRAINT `FK8A94A4E3BA9B7810` FOREIGN KEY (`project_id`) REFERENCES `project` (`project_id`),
  CONSTRAINT `FK8A94A4E3F503D155` FOREIGN KEY (`user_id`) REFERENCES `app_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `project_team`
--

DROP TABLE IF EXISTS `project_team`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `project_team` (
  `project_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  KEY `FK3800B7C3BA9B7810` (`project_id`),
  KEY `FK3800B7C3F503D155` (`user_id`),
  CONSTRAINT `FK3800B7C3BA9B7810` FOREIGN KEY (`project_id`) REFERENCES `project` (`project_id`),
  CONSTRAINT `FK3800B7C3F503D155` FOREIGN KEY (`user_id`) REFERENCES `app_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `question`
--

DROP TABLE IF EXISTS `question`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `question` (
  `question_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `text` varchar(255) NOT NULL,
  `project_id` bigint(20) NOT NULL,
  `qso_id` bigint(20) NOT NULL,
  PRIMARY KEY (`question_id`),
  UNIQUE KEY `question_id` (`question_id`),
  KEY `FKBE5CA006BA9B7810` (`project_id`),
  KEY `FKBE5CA006C524F753` (`qso_id`),
  CONSTRAINT `FKBE5CA006BA9B7810` FOREIGN KEY (`project_id`) REFERENCES `project` (`project_id`),
  CONSTRAINT `FKBE5CA006C524F753` FOREIGN KEY (`qso_id`) REFERENCES `app_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `question_metric`
--

DROP TABLE IF EXISTS `question_metric`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `question_metric` (
  `refinement` varchar(255) DEFAULT NULL,
  `status` varchar(50) DEFAULT NULL,
  `question_id` bigint(20) NOT NULL DEFAULT '0',
  `metric_id` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`metric_id`,`question_id`),
  KEY `FK6A19A829F24AD404` (`metric_id`),
  KEY `FK6A19A82956B5C4` (`question_id`),
  CONSTRAINT `FK6A19A82956B5C4` FOREIGN KEY (`question_id`) REFERENCES `question` (`question_id`),
  CONSTRAINT `FK6A19A829F24AD404` FOREIGN KEY (`metric_id`) REFERENCES `metric` (`metric_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `description` varchar(64) DEFAULT NULL,
  `name` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `scale`
--

DROP TABLE IF EXISTS `scale`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `scale` (
  `scale_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `description` varchar(1000) DEFAULT NULL,
  `examples` varchar(4000) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `operations` varchar(255) DEFAULT NULL,
  `type` varchar(1000) DEFAULT NULL,
  PRIMARY KEY (`scale_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `strategy`
--

DROP TABLE IF EXISTS `strategy`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `strategy` (
  `strategy_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `assumption` varchar(4000) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `project_id` bigint(20) NOT NULL,
  `so_id` bigint(20) NOT NULL,
  PRIMARY KEY (`strategy_id`),
  UNIQUE KEY `strategy_id` (`strategy_id`),
  KEY `FK6E6A0793BA9B7810` (`project_id`),
  KEY `FK6E6A07934515924` (`so_id`),
  CONSTRAINT `FK6E6A07934515924` FOREIGN KEY (`so_id`) REFERENCES `app_user` (`id`),
  CONSTRAINT `FK6E6A0793BA9B7810` FOREIGN KEY (`project_id`) REFERENCES `project` (`project_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `unit`
--

DROP TABLE IF EXISTS `unit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `unit` (
  `unit_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `base_unit` char(1) DEFAULT NULL,
  `multiples` bigint(20) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `physical` varchar(50) DEFAULT NULL,
  `symbol` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`unit_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_role`
--

DROP TABLE IF EXISTS `user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_role` (
  `user_id` bigint(20) NOT NULL,
  `role_id` bigint(20) NOT NULL,
  PRIMARY KEY (`user_id`,`role_id`),
  KEY `FK143BF46A4FD90D75` (`role_id`),
  KEY `FK143BF46AF503D155` (`user_id`),
  CONSTRAINT `FK143BF46A4FD90D75` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`),
  CONSTRAINT `FK143BF46AF503D155` FOREIGN KEY (`user_id`) REFERENCES `app_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;


/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2013-06-23 17:20:12
