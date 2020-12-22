-- MariaDB dump 10.17  Distrib 10.5.6-MariaDB, for Win64 (AMD64)
--
-- Host: localhost    Database: soundtrack
-- ------------------------------------------------------
-- Server version	10.5.6-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `soundtrack`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `soundtrack` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `soundtrack`;

--
-- Table structure for table `artist`
--

DROP TABLE IF EXISTS `artist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `artist` (
  `Name` varchar(15) NOT NULL,
  `Debut_Date` varchar(10) NOT NULL,
  `Sex` varchar(2) DEFAULT '남성',
  `Solo_Group` varchar(15) DEFAULT 'solo_',
  PRIMARY KEY (`Name`,`Debut_Date`),
  UNIQUE KEY `Name` (`Name`,`Debut_Date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `artist`
--

LOCK TABLES `artist` WRITE;
/*!40000 ALTER TABLE `artist` DISABLE KEYS */;
INSERT INTO `artist` VALUES ('김범수','1999-10-05','남자','solo_'),('김용준','2004-03-10','남자','SG워너비'),('김진호','2004-03-15','남자','SG워너비'),('박재범','2008-10-13','남자','2PM'),('박진영','1992-11-08','남자','solo_'),('서인국','2009-03-10','남자','solo_'),('슬기','2014-08-01','여자','레드벨벳'),('신승훈','1990-03-14','남자','solo_'),('아이린','2014-08-01','여자','레드벨벳'),('아이유','2015-03-15','여자','solo_'),('에일리','2012-01-03','여자','solo_'),('예리','2014-08-01','여자','레드벨벳'),('유노윤호','2010-03-15','남자','동방신기'),('윤종신','1990-10-08','남자','solo_'),('이름없음','','','solo_'),('이석훈','2004-02-05','남자','SG워너비'),('임영웅','2020-04-10','남자','solo_'),('정은지','2011-03-16','여자','에이핑크'),('제시','2005-10-11','여자','solo_'),('태연','2007-03-10','여자','소녀시대');
/*!40000 ALTER TABLE `artist` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `compose_write`
--

DROP TABLE IF EXISTS `compose_write`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `compose_write` (
  `CW_Birth` varchar(10) NOT NULL DEFAULT '',
  `CW_Name` varchar(19) NOT NULL DEFAULT '이름 없음',
  `S_Title` varchar(15) NOT NULL DEFAULT '이름 없음',
  `S_Sing_Time` varchar(15) NOT NULL DEFAULT '00:00:00',
  PRIMARY KEY (`CW_Birth`,`CW_Name`,`S_Title`,`S_Sing_Time`),
  KEY `S_Title` (`S_Title`,`S_Sing_Time`),
  KEY `CW_Name` (`CW_Name`,`CW_Birth`),
  CONSTRAINT `compose_write_ibfk_2` FOREIGN KEY (`S_Title`, `S_Sing_Time`) REFERENCES `soundtrack` (`Title`, `Sing_time`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `compose_write_ibfk_3` FOREIGN KEY (`CW_Name`, `CW_Birth`) REFERENCES `composer_writer` (`Name`, `Birth`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `compose_write`
--

LOCK TABLES `compose_write` WRITE;
/*!40000 ALTER TABLE `compose_write` DISABLE KEYS */;
INSERT INTO `compose_write` VALUES ('0','x','All For You','03:50'),('0','x','Bad Boy','03:13'),('0','x','I Believe','03:30'),('0','x','끝사랑','03:35'),('0','x','내 사람','04:13'),('0','x','몸매','03:15'),('0','x','보여줄게','04:13'),('0','x','어머님이 누구니','03:12'),('0','x','좋은날','03:35'),('0','x','주문','03:35'),('0','x','하루 끝','03:55'),('710410','유영진','Bad Boy','03:13'),('710410','유영진','주문','03:35'),('710419','유희열','보여줄게','04:13'),('720113','박진영','너의 뒤에서','04:30'),('720113','박진영','어머님이 누구니','03:12'),('720310','박근태','하루 끝','03:55'),('731010','김도훈','내 사람','04:13'),('740221','윤일상','끝사랑','03:35'),('740221','윤일상','좋은날','03:35'),('760809','조영수','I Believe','03:30'),('841030','김한범','All For You','03:50'),('870425','박재범','몸매','03:15');
/*!40000 ALTER TABLE `compose_write` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `composer_writer`
--

DROP TABLE IF EXISTS `composer_writer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `composer_writer` (
  `Name` varchar(20) NOT NULL,
  `Type` char(2) DEFAULT '작곡',
  `Birth` varchar(10) NOT NULL DEFAULT '0',
  PRIMARY KEY (`Name`,`Birth`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `composer_writer`
--

LOCK TABLES `composer_writer` WRITE;
/*!40000 ALTER TABLE `composer_writer` DISABLE KEYS */;
INSERT INTO `composer_writer` VALUES ('x','x','0'),('김도훈','작곡','731010'),('김한범','작곡','841030'),('박근태','작곡','720310'),('박재범','작곡','870425'),('박진영','작곡','720113'),('유영진','작곡','710410'),('유영진','작사','791012'),('유희열','작곡','710419'),('윤일상','작곡','740221'),('윤종신','작사','691015'),('이상규','작곡','441006'),('임영웅','작사','961107'),('조영수','작곡','760809'),('켄지','작곡','760203');
/*!40000 ALTER TABLE `composer_writer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `play_count`
--

DROP TABLE IF EXISTS `play_count`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `play_count` (
  `Tens` int(11) DEFAULT 0,
  `Twentys` int(11) DEFAULT 0,
  `Thirtys` int(11) DEFAULT 0,
  `Fourtys` int(11) DEFAULT 0,
  `under_tens` int(11) DEFAULT 0,
  `upper_fiftys` int(11) DEFAULT 0,
  `C_Title` varchar(15) NOT NULL,
  `C_Sing_Time` varchar(15) NOT NULL,
  PRIMARY KEY (`C_Title`,`C_Sing_Time`),
  CONSTRAINT `play_count_ibfk_1` FOREIGN KEY (`C_Title`, `C_Sing_Time`) REFERENCES `soundtrack` (`Title`, `Sing_time`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `play_count`
--

LOCK TABLES `play_count` WRITE;
/*!40000 ALTER TABLE `play_count` DISABLE KEYS */;
INSERT INTO `play_count` VALUES (10,203,321,200,230,120,'All For You','03:50'),(200,251,200,100,125,122,'Bad Boy','03:13'),(250,123,250,120,250,1222,'I Believe','03:30'),(1223,312,22,13,144,22,'끝사랑','03:35'),(100,323,3,125,280,210,'내 사람','04:13'),(255,12,156,85,466,3,'너의 뒤에서','04:30'),(120,233,200,122,299,25,'몸매','03:15'),(140,340,220,210,23,202,'보여줄게','04:13'),(234,245,234,55,231,44,'어머님이 누구니','03:12'),(45,52,88,123,421,112,'좋은날','03:35'),(235,303,32,280,245,521,'주문','03:35'),(300,278,125,80,265,155,'하루 끝','03:55');
/*!40000 ALTER TABLE `play_count` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `playlist`
--

DROP TABLE IF EXISTS `playlist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `playlist` (
  `Playlist_name` varchar(10) NOT NULL DEFAULT '이름 없음',
  `U_id` varchar(10) NOT NULL,
  `listen_count` int(11) DEFAULT 0,
  `Play_Title` varchar(15) NOT NULL,
  `Play_Sing_Time` varchar(15) NOT NULL,
  PRIMARY KEY (`Playlist_name`,`U_id`,`Play_Title`,`Play_Sing_Time`),
  KEY `U_id` (`U_id`),
  KEY `Play_Title` (`Play_Title`,`Play_Sing_Time`),
  CONSTRAINT `playlist_ibfk_4` FOREIGN KEY (`U_id`) REFERENCES `streaming_subscriber` (`Id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `playlist_ibfk_5` FOREIGN KEY (`Play_Title`, `Play_Sing_Time`) REFERENCES `soundtrack` (`Title`, `Sing_time`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `playlist`
--

LOCK TABLES `playlist` WRITE;
/*!40000 ALTER TABLE `playlist` DISABLE KEYS */;
INSERT INTO `playlist` VALUES ('DATABASE','root',0,'All For You','03:50'),('DATABASE','root',1,'내 사람','04:13'),('DATABASE','root',1,'좋은날','03:35'),('DATABASE','root',0,'주문','03:35'),('GOOD','root',0,'좋은날','03:35'),('GOOD','root',0,'주문','03:35'),('GOOD','root',0,'하루 끝','03:55'),('HERO','rlqja',2,'끝사랑','03:35'),('HERO','root',0,'주문','03:35'),('HERO','root',0,'하루 끝','03:55');
/*!40000 ALTER TABLE `playlist` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `playlist_name`
--

DROP TABLE IF EXISTS `playlist_name`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `playlist_name` (
  `Playlist_Name` varchar(10) NOT NULL DEFAULT '이름 없음1',
  `U_Id` varchar(13) NOT NULL,
  PRIMARY KEY (`Playlist_Name`,`U_Id`),
  KEY `U_Id` (`U_Id`),
  CONSTRAINT `playlist_name_ibfk_1` FOREIGN KEY (`U_Id`) REFERENCES `streaming_subscriber` (`Id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `playlist_name`
--

LOCK TABLES `playlist_name` WRITE;
/*!40000 ALTER TABLE `playlist_name` DISABLE KEYS */;
INSERT INTO `playlist_name` VALUES ('DATABASE','root'),('GOOD','root'),('HERO','Hanyang'),('HERO','rlqja'),('HERO','root');
/*!40000 ALTER TABLE `playlist_name` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sing`
--

DROP TABLE IF EXISTS `sing`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sing` (
  `S_Title` varchar(15) NOT NULL,
  `S_Sing_Time` varchar(15) NOT NULL,
  `S_Name` varchar(15) NOT NULL DEFAULT '이름없음',
  `S_Debut_Date` varchar(10) NOT NULL DEFAULT '',
  PRIMARY KEY (`S_Name`,`S_Debut_Date`,`S_Title`,`S_Sing_Time`),
  KEY `S_Title` (`S_Title`,`S_Sing_Time`),
  CONSTRAINT `sing_ibfk_1` FOREIGN KEY (`S_Title`, `S_Sing_Time`) REFERENCES `soundtrack` (`Title`, `Sing_time`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `sing_ibfk_2` FOREIGN KEY (`S_Name`, `S_Debut_Date`) REFERENCES `artist` (`Name`, `Debut_Date`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sing`
--

LOCK TABLES `sing` WRITE;
/*!40000 ALTER TABLE `sing` DISABLE KEYS */;
INSERT INTO `sing` VALUES ('끝사랑','03:35','김범수','1999-10-05'),('내 사람','04:13','김용준','2004-03-10'),('몸매','03:15','박재범','2008-10-13'),('너의 뒤에서','04:30','박진영','1992-11-08'),('어머님이 누구니','03:12','박진영','1992-11-08'),('All For You','03:50','서인국','2009-03-10'),('Bad Boy','03:13','슬기','2014-08-01'),('I Believe','03:30','신승훈','1990-03-14'),('좋은날','03:35','아이유','2015-03-15'),('하루 끝','03:55','아이유','2015-03-15'),('보여줄게','04:13','에일리','2012-01-03'),('주문','03:35','유노윤호','2010-03-15'),('All For You','03:50','정은지','2011-03-16'),('어머님이 누구니','03:12','제시','2005-10-11');
/*!40000 ALTER TABLE `sing` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `soundtrack`
--

DROP TABLE IF EXISTS `soundtrack`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `soundtrack` (
  `Title` varchar(15) NOT NULL,
  `Over_19` tinyint(1) DEFAULT 0,
  `Sing_time` varchar(15) NOT NULL,
  `Genre` varchar(10) DEFAULT NULL,
  `Today_streaming` int(11) DEFAULT 0,
  `Super_ID` varchar(13) NOT NULL DEFAULT 'root',
  `is_Group` tinyint(1) DEFAULT 0,
  PRIMARY KEY (`Title`,`Sing_time`),
  KEY `Super_ID` (`Super_ID`),
  CONSTRAINT `soundtrack_ibfk_1` FOREIGN KEY (`Super_ID`) REFERENCES `streaming_subscriber` (`Id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `soundtrack`
--

LOCK TABLES `soundtrack` WRITE;
/*!40000 ALTER TABLE `soundtrack` DISABLE KEYS */;
INSERT INTO `soundtrack` VALUES ('All For You',0,'03:50','가요',121,'root',0),('Bad Boy',0,'03:13','가요',21,'root',1),('I Believe',0,'03:30','가요',230,'root',0),('끝사랑',0,'03:35','발륻',25,'root',0),('내 사람',0,'04:13','발라드',127,'root',1),('너의 뒤에서',0,'04:30','발라드',100,'root',0),('몸매',1,'03:15','힙합',240,'root',0),('보여줄게',0,'04:13','가요',122,'root',0),('아이유',0,'03:14','발라드',40,'root',0),('어머님이 누구니',0,'03:12','댄스',122,'root',0),('좋은날',0,'03:35','발라드',133,'root',0),('주문',1,'03:35','댄스',258,'root',1),('하루 끝',0,'03:55','가요',124,'root',0);
/*!40000 ALTER TABLE `soundtrack` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary table structure for view `soundtrack_artist_2`
--

DROP TABLE IF EXISTS `soundtrack_artist_2`;
/*!50001 DROP VIEW IF EXISTS `soundtrack_artist_2`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `soundtrack_artist_2` (
  `Title` tinyint NOT NULL,
  `Sing_time` tinyint NOT NULL,
  `Over_19` tinyint NOT NULL,
  `Genre` tinyint NOT NULL,
  `Artist_Name` tinyint NOT NULL,
  `Solo_Group` tinyint NOT NULL,
  `is_Group` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `soundtrack_compose_write`
--

DROP TABLE IF EXISTS `soundtrack_compose_write`;
/*!50001 DROP VIEW IF EXISTS `soundtrack_compose_write`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `soundtrack_compose_write` (
  `Title` tinyint NOT NULL,
  `Sing_time` tinyint NOT NULL,
  `Name` tinyint NOT NULL,
  `Birth` tinyint NOT NULL,
  `Type` tinyint NOT NULL,
  `Over_19` tinyint NOT NULL,
  `is_Group` tinyint NOT NULL,
  `Genre` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `streaming_subscriber`
--

DROP TABLE IF EXISTS `streaming_subscriber`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `streaming_subscriber` (
  `Id` varchar(10) NOT NULL,
  `Phone` varchar(11) DEFAULT NULL,
  `Address` varchar(30) DEFAULT NULL,
  `SSN` varchar(13) NOT NULL,
  `Password` varchar(11) NOT NULL,
  `Name` varchar(10) NOT NULL,
  `Subscribe_term` int(11) DEFAULT 0,
  `Email` varchar(30) DEFAULT NULL,
  `Payment_day` date DEFAULT '2015-10-10',
  `Super_ID` varchar(15) DEFAULT 'root',
  PRIMARY KEY (`Id`),
  KEY `Super_ID` (`Super_ID`),
  CONSTRAINT `streaming_subscriber_ibfk_2` FOREIGN KEY (`Super_ID`) REFERENCES `streaming_subscriber` (`Id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `streaming_subscriber`
--

LOCK TABLES `streaming_subscriber` WRITE;
/*!40000 ALTER TABLE `streaming_subscriber` DISABLE KEYS */;
INSERT INTO `streaming_subscriber` VALUES ('database','01022222222','Seoul','981101','1234','Hanyang',3,'hanyang.ac.kr','2016-01-08','root'),('Hanyang','01020887777','Soul','800712','1234','한양',0,'hanyang.ac.kr','2015-10-10','root'),('rlqja','010230','Seoul','920310','1234','임꺽정',5,'hanyang.ac.kr','2016-03-08','root'),('rlqja1107','01099999999','Seoul','970807','1234','홍길동',1,'hongil@naver.com','2020-12-01','root'),('root','01012345678','Seoul','9512031255421','1234','김기범',1,'rlqja9611@naver.com','2020-12-02',NULL);
/*!40000 ALTER TABLE `streaming_subscriber` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Current Database: `soundtrack`
--

USE `soundtrack`;

--
-- Final view structure for view `soundtrack_artist_2`
--

/*!50001 DROP TABLE IF EXISTS `soundtrack_artist_2`*/;
/*!50001 DROP VIEW IF EXISTS `soundtrack_artist_2`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_unicode_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `soundtrack_artist_2` AS (select `soundtrack`.`Title` AS `Title`,`soundtrack`.`Sing_time` AS `Sing_time`,`soundtrack`.`Over_19` AS `Over_19`,`soundtrack`.`Genre` AS `Genre`,group_concat(distinct `artist`.`Name` separator ',') AS `Artist_Name`,`artist`.`Solo_Group` AS `Solo_Group`,`soundtrack`.`is_Group` AS `is_Group` from ((`soundtrack` join `sing`) join `artist`) where `soundtrack`.`Title` = `sing`.`S_Title` and `soundtrack`.`Sing_time` = `sing`.`S_Sing_Time` and `sing`.`S_Name` = `artist`.`Name` and `sing`.`S_Debut_Date` = `artist`.`Debut_Date` group by `soundtrack`.`Title`,`soundtrack`.`Sing_time`) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `soundtrack_compose_write`
--

/*!50001 DROP TABLE IF EXISTS `soundtrack_compose_write`*/;
/*!50001 DROP VIEW IF EXISTS `soundtrack_compose_write`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_unicode_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `soundtrack_compose_write` AS (select `soundtrack`.`Title` AS `Title`,`soundtrack`.`Sing_time` AS `Sing_time`,`composer_writer`.`Name` AS `Name`,`composer_writer`.`Birth` AS `Birth`,`composer_writer`.`Type` AS `Type`,`soundtrack`.`Over_19` AS `Over_19`,`soundtrack`.`is_Group` AS `is_Group`,`soundtrack`.`Genre` AS `Genre` from ((`soundtrack` join `compose_write`) join `composer_writer`) where `soundtrack`.`Title` = `compose_write`.`S_Title` and `soundtrack`.`Sing_time` = `compose_write`.`S_Sing_Time` and `compose_write`.`CW_Name` = `composer_writer`.`Name` and `compose_write`.`CW_Birth` = `composer_writer`.`Birth`) */;
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

-- Dump completed on 2020-12-03  1:04:28
