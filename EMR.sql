CREATE DATABASE  IF NOT EXISTS `emr` /*!40100 DEFAULT CHARACTER SET utf8 */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `emr`;
-- MySQL dump 10.13  Distrib 8.0.16, for Win64 (x86_64)
--
-- Host: localhost    Database: emr
-- ------------------------------------------------------
-- Server version	8.0.16

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
 SET NAMES utf8 ;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `test_item`
--

DROP TABLE IF EXISTS `test_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `test_item` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `item` varchar(45) DEFAULT NULL COMMENT '项目名',
  `abbreviation` varchar(45) DEFAULT NULL COMMENT '英文缩写',
  `unit` varchar(45) DEFAULT NULL COMMENT '单位',
  `lower` double DEFAULT NULL COMMENT '参考值下限',
  `higher` double DEFAULT NULL COMMENT '参考值上限',
  `test_type_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=108 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `test_item`
--

LOCK TABLES `test_item` WRITE;
/*!40000 ALTER TABLE `test_item` DISABLE KEYS */;
INSERT INTO `test_item` VALUES (1,'白细胞计数','WBC','10^9g/L',3.5,9.5,1),(2,'淋巴细胞百分比','LY%','%',20,50,1),(3,'单核细胞百分比','MO%','%',3,10,1),(4,'粒细胞百分比','Neu%','%',40,75,1),(5,'嗜酸性细胞比例','EO%','%',0.4000000059604645,8,1),(6,'嗜碱性细胞比例','BASO%','%',0,1,1),(7,'粒细胞绝对值','Neu#','10^9/L',1.7999999523162842,6.300000190734863,1),(8,'单核细胞绝对值','MO#','10^9/L',0.10000000149011612,0.6000000238418579,1),(9,'嗜酸细胞绝对值','EO#','10^9/L',0.019999999552965164,0.5199999809265137,1),(10,'嗜碱性细胞绝对值','BASO#','10^9/L',0,0.05999999865889549,1),(11,'淋巴细胞绝对值','LY#','10^9/L',1.100000023841858,3.200000047683716,1),(12,'血细胞计数','RBC','x10^12/I',4.300000190734863,5.800000190734863,1),(13,'血红蛋白','HGB','g/L',130,175,1),(14,'红细胞压积','HCT','%',40,50,1),(15,'平均红细胞体积','MCV','fL',80,100,1),(16,'平均RBC血红蛋白','MCH','pg',27,34,1),(17,'血红蛋白浓度','MCHC','g/L',320,360,1),(18,'红细胞分布宽度','SD','fL',37,54,1),(19,'血小板计数','FLT','x10^9/L',125,350,1),(20,'血小板分布宽度','PDW','fL',9,17,1),(21,'平均血小板体积','MPV','fL',9,13,1),(22,'大型血小板比例','P_LCR','%',13,43,1),(23,'超敏肌钙蛋白','TnIUItr','ng/ml',0,0.03999999910593033,2),(24,'甲胎蛋白','AFP','ng/ml',0,8.100000381469727,3),(25,'癌胚抗原','CEA','ng/ml',0,0.3499999940395355,3),(26,'糖类抗原199','CA199','U/ml',0,37,3),(27,'游离T3','','pmol/L',2.630000114440918,5.699999809265137,4),(28,'游离T4','','pmol/L',9.010000228881836,19.049999237060547,4),(29,'促甲状腺激素','TSH','uIU/mL',0.3499999940395355,0.49399998784065247,4),(30,'抗甲状腺球蛋白抗体','','IU/mL',0,4.110000133514404,4),(31,'抗甲状腺球蛋氧化物酶抗体','TPO','IU/mL',0,5.610000133514404,4),(33,'凝血酶原时间','PT','秒',9,14,5),(34,'凝血酶原国际标准化比值','INR',NULL,NULL,NULL,5),(35,'凝血酶原活动度','PT%','%',NULL,NULL,5),(36,'活化部分凝血酶时间','APTT','秒',20,40,5),(37,'D-二聚体',NULL,'ug/mL',0,0.5,5),(38,'纤维蛋白原含量','FIB','g/L',2,4,5),(39,'纤维蛋白降产物','FDP','ug/mL',0,5,5),(40,'溶血指数',NULL,NULL,-1,-1,6),(41,'黄疸指数',NULL,NULL,-1,-1,6),(42,'脂血指数',NULL,NULL,-1,-1,6),(43,'总蛋白','TP','g/L',65,85,6),(44,'白蛋白',NULL,'g/L',40,55,6),(45,'球蛋白','GLB','g/L',20,40,6),(46,'白蛋白/球蛋白','A/G',NULL,1.2000000476837158,2.4000000953674316,6),(47,'前白蛋白',NULL,'mg/L',200,400,6),(48,'总胆红素','TBIL','umol/L',2,20,6),(49,'直接胆红素','D-BIL','umol/L',0,6,6),(50,'谷丙转氨酶','ALT','IU/L',9,50,6),(51,'谷草转氨酶','AST','IU/L',15,40,6),(52,'碱性磷酸酶','AKP','IU/L',35,135,6),(53,'谷氨酰转肽酶','GGT','IU/L',10,60,6),(54,'L-乳酸脱氢酶','LDH','U/L',109,245,6),(55,'胆碱酯酶','CHE','U/L',4000,12000,6),(56,'总胆汁酸','TBA','umol/L',0,10,6),(57,'腺苷脱氨酶',NULL,'U/L',4,22,6),(58,'单胺氧化酶',NULL,'U/L',0,12,6),(59,'a-L-岩藻糖苷酶',NULL,'U/L',0,40,6),(60,'葡萄糖','Glu','mmol/L',3.9000000953674316,6.099999904632568,6),(61,'尿素','UREA','mmol/L',2.5,7.099999904632568,6),(62,'肌酐','CREA','umol/L',2.5,7.099999904632568,6),(63,'尿素肌酐比',NULL,NULL,0.029999999329447746,0.1599999964237213,6),(72,'尿酸','UA','umol/L',120,430,6),(73,'胱抑素','','mg/L',0.550000011920929,1.0499999523162842,6),(74,'甘油三酯','TG','mmol/L',0.4000000059604645,1.7999999523162842,6),(75,'总胆固醇','CHO','mmol/L',3.5999999046325684,5.199999809265137,6),(76,'高密度脂蛋白','HDL-C','mmol/L',0.75,1.899999976158142,6),(77,'低密度值蛋白胆固醇','LDL-C','mmol/L',0.800000011920929,3.369999885559082,6),(78,'载脂蛋白A','APOA','g/L',1,1.600000023841858,6),(79,'载脂蛋白B','APOB','g/L',0.6000000238418579,1.2000000476837158,6),(80,'载脂蛋白A/载脂蛋白B',NULL,NULL,NULL,NULL,6),(81,'脂蛋白A',NULL,'mg/l',0,300,6),(82,'肌酸激酶','CK','U/L',24,195,6),(83,'肌酸激酶同工酶','CK-MBII','ng/mL',0,5,6),(84,'钾','K','mmol/L',3.5,5.300000190734863,6),(85,'钠','Na','mmol/L',137,147,6),(86,'氯','Cl','mmol/L',99,110,6),(87,'二氧化碳结合力',NULL,'mmol/L',22,30,6),(88,'钙','Ca','mmol/L',2.0999999046325684,2.5999999046325684,6),(89,'磷','P','mmol/L',0.800000011920929,1.600000023841858,6),(90,'镁','Mg','mmol/L',0.5299999713897705,1.1100000143051147,6),(91,'钙/肌酐','Ca/Creaa',NULL,NULL,NULL,6),(92,'磷/肌酐','P/Creaa',NULL,NULL,NULL,6),(93,'IVSD','IVSD','mm',6,11,7),(94,'LVDD(男)','LVDD','mm',36,54,7),(95,'LVDD(女)','LVDD','mm',34,52,7),(96,'LASD','LASD1','mm',25,40,7),(97,'LASD','LASD2','mm',29,50,7),(98,'RASD','RASD1','mm',25,40,7),(99,'RASD','RASD2','mm',34,50,7),(100,'MV E峰','','cm/s',50,130,7),(101,'MV A峰','','cm/s',41,87,7),(102,'主动脉瓣','','cm/s',63,130,7),(103,'二尖瓣瓣环TDI(室间隔)','',NULL,NULL,NULL,7),(104,'EF%','EF%','cm/s',55,77,7),(105,'FS%','FS%','cm/s',34,48,7),(106,'SV(ml)','SV','cm/s',41,95,7),(107,'EDV(ml)','EDV','cm/s',64,132,7);
/*!40000 ALTER TABLE `test_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `test_type`
--

DROP TABLE IF EXISTS `test_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `test_type` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) DEFAULT NULL COMMENT '化验项目名',
  `englishName` varchar(45) DEFAULT NULL COMMENT '化验项目英文名',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `test_type`
--

LOCK TABLES `test_type` WRITE;
/*!40000 ALTER TABLE `test_type` DISABLE KEYS */;
INSERT INTO `test_type` VALUES (0,'其他',NULL),(1,'血常规',NULL),(2,'肌钙蛋白',NULL),(3,'AFP.CEA.CA199','AFP.CEA.CA199'),(4,'甲功五项',NULL),(5,'凝血象,D-二聚体/FDP',NULL),(6,'肝功,肾功,血脂,血糖,电解质,心肌酶谱',NULL),(7,'超声',NULL);
/*!40000 ALTER TABLE `test_type` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2019-05-31 15:39:19
