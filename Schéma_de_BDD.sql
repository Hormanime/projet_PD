-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3306
-- Generation Time: May 04, 2025 at 05:47 PM
-- Server version: 9.1.0
-- PHP Version: 8.3.14

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `bd_seance`
--

-- --------------------------------------------------------

--
-- Table structure for table `client`
--

DROP TABLE IF EXISTS `client`;
CREATE TABLE IF NOT EXISTS `client` (
  `id_client` int NOT NULL AUTO_INCREMENT,
  `nom` varchar(50) NOT NULL,
  `prenom` varchar(50) NOT NULL,
  `fonction` enum('client','admin') NOT NULL DEFAULT 'client',
  `annee_fin_abonnement` int DEFAULT NULL,
  `mois_fin_abonnement` varchar(20) DEFAULT NULL,
  `jour_fin_abonnement` int DEFAULT NULL,
  `limite_reservation` int DEFAULT NULL,
  `nombre_reservations` int DEFAULT NULL,
  PRIMARY KEY (`id_client`)
) ENGINE=MyISAM AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `client`
--

INSERT INTO `client` (`id_client`, `nom`, `prenom`, `fonction`, `annee_fin_abonnement`, `mois_fin_abonnement`, `jour_fin_abonnement`, `limite_reservation`, `nombre_reservations`) VALUES
(1, 'peter', 'mark', 'client', 2025, 'Mai', 6, 4, 0),
(7, 'jordy', 'mananuk', 'client', 2025, 'Mai', 2, 4, 0),
(6, 'jimy', 'jonh', 'client', 2025, 'Avril', 30, 4, 0),
(8, 'eren', 'yegar', 'client', 2025, 'Mai', 4, 4, 0),
(10, 'gc', 'gaya', 'admin', NULL, NULL, NULL, NULL, NULL),
(11, 'partik', 'lepère', 'admin', NULL, NULL, NULL, NULL, NULL);

--
-- Triggers `client`
--
DROP TRIGGER IF EXISTS `before_client_insert`;
DELIMITER $$
CREATE TRIGGER `before_client_insert` BEFORE INSERT ON `client` FOR EACH ROW BEGIN
    IF NEW.fonction = 'admin' THEN
        SET NEW.annee_fin_abonnement = NULL,
            NEW.mois_fin_abonnement = NULL,
            NEW.jour_fin_abonnement = NULL,
            NEW.limite_reservation = NULL,
            NEW.nombre_reservations = NULL;
    END IF;
END
$$
DELIMITER ;
DROP TRIGGER IF EXISTS `before_client_update`;
DELIMITER $$
CREATE TRIGGER `before_client_update` BEFORE UPDATE ON `client` FOR EACH ROW BEGIN
    IF NEW.fonction = 'admin' THEN
        SET NEW.annee_fin_abonnement = NULL,
            NEW.mois_fin_abonnement = NULL,
            NEW.jour_fin_abonnement = NULL,
            NEW.limite_reservation = NULL,
            NEW.nombre_reservations = NULL;
    END IF;
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `coach`
--

DROP TABLE IF EXISTS `coach`;
CREATE TABLE IF NOT EXISTS `coach` (
  `id_coach` int NOT NULL,
  `code_coach` int DEFAULT NULL,
  `nom` varchar(50) DEFAULT NULL,
  `prenom` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id_coach`),
  UNIQUE KEY `code_coach` (`code_coach`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `coach`
--

INSERT INTO `coach` (`id_coach`, `code_coach`, `nom`, `prenom`) VALUES
(1, 45, 'louis', 'patter'),
(2, 76, 'jack', 'dark'),
(3, 785, 'daniel', 'blondi');

-- --------------------------------------------------------

--
-- Table structure for table `coach_seance`
--

DROP TABLE IF EXISTS `coach_seance`;
CREATE TABLE IF NOT EXISTS `coach_seance` (
  `id_coach_seance` int NOT NULL AUTO_INCREMENT,
  `code_coach1` int DEFAULT NULL,
  `code_coach2` int DEFAULT NULL,
  `code_coach3` int DEFAULT NULL,
  `code_seance` int DEFAULT NULL,
  PRIMARY KEY (`id_coach_seance`),
  KEY `code_coach1` (`code_coach1`),
  KEY `code_coach2` (`code_coach2`),
  KEY `code_coach3` (`code_coach3`),
  KEY `code_seance` (`code_seance`)
) ENGINE=MyISAM AUTO_INCREMENT=36 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `coach_seance`
--

INSERT INTO `coach_seance` (`id_coach_seance`, `code_coach1`, `code_coach2`, `code_coach3`, `code_seance`) VALUES
(1, 45, 76, 785, 1),
(2, 45, 76, 785, 2),
(3, 45, 76, 785, 3),
(4, 45, 76, 785, 4),
(5, 45, 76, 785, 5),
(6, 45, 76, 785, 6),
(7, 45, 76, 785, 7),
(8, 45, 76, 785, 8),
(9, 45, 76, 785, 9),
(10, 45, 76, 785, 10),
(11, 45, 76, 785, 11),
(12, 45, 76, 785, 12),
(13, 45, 76, 785, 13),
(14, 45, 76, 785, 14),
(15, 45, 76, 785, 15),
(16, 45, 76, 785, 16),
(17, 45, 76, 785, 17),
(18, 45, 76, 785, 18),
(19, 45, 76, 785, 19),
(20, 45, 76, 785, 20),
(21, 45, 76, 785, 21),
(22, 45, 76, 785, 22),
(23, 45, 76, 785, 23),
(24, 45, 76, 785, 24),
(25, 45, 76, 785, 25),
(26, 45, 76, 785, 26),
(27, 45, 76, 785, 27),
(28, 45, 76, 785, 28),
(29, 45, 76, 785, 29),
(30, 45, 76, 785, 30),
(31, 45, 76, 785, 31),
(32, 45, 76, 785, 32),
(33, 45, 76, 785, 33),
(34, 45, 76, 785, 34),
(35, 45, 76, 785, 35);

-- --------------------------------------------------------

--
-- Table structure for table `reservation`
--

DROP TABLE IF EXISTS `reservation`;
CREATE TABLE IF NOT EXISTS `reservation` (
  `id_reservation` int NOT NULL AUTO_INCREMENT,
  `id_client` int NOT NULL,
  `code_seance` int NOT NULL,
  `date_reservation` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_reservation`),
  UNIQUE KEY `unique_reservation` (`id_client`,`code_seance`),
  KEY `code_seance` (`code_seance`)
) ENGINE=MyISAM AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `seance`
--

DROP TABLE IF EXISTS `seance`;
CREATE TABLE IF NOT EXISTS `seance` (
  `code_seance` int NOT NULL AUTO_INCREMENT,
  `annee` int NOT NULL,
  `mois` varchar(20) NOT NULL,
  `jour` int NOT NULL,
  `heure` varchar(20) NOT NULL,
  `etat` varchar(20) NOT NULL DEFAULT 'disponible',
  `nombre_reservation` int DEFAULT '0',
  `capacite` int NOT NULL,
  PRIMARY KEY (`code_seance`)
) ENGINE=MyISAM AUTO_INCREMENT=36 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `seance`
--

INSERT INTO `seance` (`code_seance`, `annee`, `mois`, `jour`, `heure`, `etat`, `nombre_reservation`, `capacite`) VALUES
(1, 2025, 'mai', 4, '8:00', 'Disponible', 0, 1),
(2, 2025, 'mai', 4, '11:00', 'Disponible', 0, 1),
(3, 2025, 'mai', 4, '13:00', 'Disponible', 0, 1),
(4, 2025, 'mai', 4, '15:00', 'Disponible', 0, 1),
(5, 2025, 'mai', 4, '19:00', 'Disponible', 0, 1),
(6, 2025, 'mai', 4, '8:00', 'Disponible', 0, 1),
(7, 2025, 'mai', 4, '11:00', 'Disponible', 0, 1),
(8, 2025, 'mai', 4, '13:00', 'Disponible', 0, 1),
(9, 2025, 'mai', 4, '15:00', 'Disponible', 0, 1),
(10, 2025, 'mai', 4, '19:00', 'Disponible', 0, 1),
(11, 2025, 'mai', 4, '8:00', 'Disponible', 0, 1),
(12, 2025, 'mai', 4, '11:00', 'Disponible', 0, 1),
(13, 2025, 'mai', 4, '13:00', 'Disponible', 0, 1),
(14, 2025, 'mai', 4, '15:00', 'Disponible', 0, 1),
(15, 2025, 'mai', 4, '19:00', 'Disponible', 0, 1),
(16, 2025, 'mai', 4, '8:00', 'Disponible', 0, 1),
(17, 2025, 'mai', 4, '11:00', 'Disponible', 0, 1),
(18, 2025, 'mai', 4, '13:00', 'Disponible', 0, 1),
(19, 2025, 'mai', 4, '15:00', 'Disponible', 0, 1),
(20, 2025, 'mai', 4, '19:00', 'Disponible', 0, 1),
(21, 2025, 'mai', 4, '8:00', 'Disponible', 0, 1),
(22, 2025, 'mai', 4, '11:00', 'Disponible', 0, 1),
(23, 2025, 'mai', 4, '13:00', 'Disponible', 0, 1),
(24, 2025, 'mai', 4, '15:00', 'Disponible', 0, 1),
(25, 2025, 'mai', 4, '19:00', 'Disponible', 0, 1),
(26, 2025, 'mai', 4, '8:00', 'Disponible', 0, 1),
(27, 2025, 'mai', 4, '11:00', 'Disponible', 0, 1),
(28, 2025, 'mai', 4, '13:00', 'Disponible', 0, 1),
(29, 2025, 'mai', 4, '15:00', 'Disponible', 0, 1),
(30, 2025, 'mai', 4, '19:00', 'Disponible', 0, 1),
(31, 2025, 'mai', 4, '8:00', 'Disponible', 0, 1),
(32, 2025, 'mai', 4, '11:00', 'Disponible', 0, 1),
(33, 2025, 'mai', 4, '13:00', 'Disponible', 0, 1),
(34, 2025, 'mai', 4, '15:00', 'Disponible', 0, 1),
(35, 2025, 'mai', 4, '19:00', 'Disponible', 0, 1);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
