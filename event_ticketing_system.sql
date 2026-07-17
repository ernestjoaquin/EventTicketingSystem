-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3307
-- Generation Time: Jul 17, 2026 at 04:39 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `event_ticketing_system`
--

-- --------------------------------------------------------

--
-- Table structure for table `bookings`
--

CREATE TABLE `bookings` (
  `booking_id` int(11) NOT NULL,
  `customer_id` int(11) NOT NULL,
  `booking_date` datetime DEFAULT current_timestamp(),
  `total_amount` decimal(10,2) NOT NULL DEFAULT 0.00,
  `status` enum('PENDING','CONFIRMED','CANCELLED') NOT NULL DEFAULT 'PENDING'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `bookings`
--

INSERT INTO `bookings` (`booking_id`, `customer_id`, `booking_date`, `total_amount`, `status`) VALUES
(1, 2, '2026-07-17 10:11:33', 800.00, 'CONFIRMED');

-- --------------------------------------------------------

--
-- Table structure for table `booking_items`
--

CREATE TABLE `booking_items` (
  `item_id` int(11) NOT NULL,
  `booking_id` int(11) NOT NULL,
  `event_id` int(11) NOT NULL,
  `seat_id` int(11) NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `status` varchar(20) DEFAULT 'ACTIVE'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `booking_items`
--

INSERT INTO `booking_items` (`item_id`, `booking_id`, `event_id`, `seat_id`, `price`, `status`) VALUES
(1, 1, 3, 9, 800.00, 'ACTIVE');

-- --------------------------------------------------------

--
-- Table structure for table `categories`
--

CREATE TABLE `categories` (
  `category_id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `description` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `categories`
--

INSERT INTO `categories` (`category_id`, `name`, `description`) VALUES
(1, 'Concert', 'Live music concerts and performances'),
(2, 'Conference', 'Business and tech conferences'),
(3, 'Sports', 'Sporting events and matches'),
(4, 'Theater', 'Theatrical plays and musicals');

-- --------------------------------------------------------

--
-- Table structure for table `events`
--

CREATE TABLE `events` (
  `event_id` int(11) NOT NULL,
  `title` varchar(200) NOT NULL,
  `description` text DEFAULT NULL,
  `event_datetime` datetime NOT NULL,
  `venue_id` int(11) NOT NULL,
  `category_id` int(11) NOT NULL,
  `total_seats` int(11) NOT NULL DEFAULT 0,
  `available_seats` int(11) NOT NULL DEFAULT 0,
  `image_url` varchar(255) DEFAULT NULL,
  `created_at` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `events`
--

INSERT INTO `events` (`event_id`, `title`, `description`, `event_datetime`, `venue_id`, `category_id`, `total_seats`, `available_seats`, `image_url`, `created_at`) VALUES
(1, 'Summer Music Fest', 'A night of live bands and top artists.', '2026-08-15 19:00:00', 1, 1, 100, 100, NULL, '2026-07-17 09:23:01'),
(2, 'Tech Innovators Conference', 'Talks from leading tech innovators.', '2026-09-05 09:00:00', 3, 2, 60, 60, NULL, '2026-07-17 09:23:01'),
(3, 'Championship Basketball Finals', 'The biggest basketball showdown of the year.', '2026-08-28 18:30:00', 2, 3, 80, 79, NULL, '2026-07-17 09:23:01');

-- --------------------------------------------------------

--
-- Table structure for table `payments`
--

CREATE TABLE `payments` (
  `payment_id` int(11) NOT NULL,
  `booking_id` int(11) NOT NULL,
  `amount` decimal(10,2) NOT NULL,
  `method` varchar(50) DEFAULT 'Credit Card',
  `status` enum('PENDING','SUCCESS','FAILED') NOT NULL DEFAULT 'PENDING',
  `paid_at` datetime DEFAULT NULL,
  `transaction_id` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `payments`
--

INSERT INTO `payments` (`payment_id`, `booking_id`, `amount`, `method`, `status`, `paid_at`, `transaction_id`) VALUES
(1, 1, 800.00, 'Credit Card', 'SUCCESS', '2026-07-17 02:11:34', 'TXN-1784254294008');

-- --------------------------------------------------------

--
-- Table structure for table `seats`
--

CREATE TABLE `seats` (
  `seat_id` int(11) NOT NULL,
  `event_id` int(11) NOT NULL,
  `section` varchar(50) NOT NULL DEFAULT 'General',
  `seat_number` varchar(20) NOT NULL,
  `price` decimal(10,2) NOT NULL DEFAULT 0.00,
  `status` enum('AVAILABLE','RESERVED','SOLD') NOT NULL DEFAULT 'AVAILABLE'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `seats`
--

INSERT INTO `seats` (`seat_id`, `event_id`, `section`, `seat_number`, `price`, `status`) VALUES
(1, 1, 'General', 'S-001', 1500.00, 'AVAILABLE'),
(2, 2, 'General', 'S-001', 2500.00, 'AVAILABLE'),
(3, 3, 'General', 'S-001', 800.00, 'AVAILABLE'),
(4, 1, 'General', 'S-002', 1500.00, 'AVAILABLE'),
(5, 2, 'General', 'S-002', 2500.00, 'AVAILABLE'),
(6, 3, 'General', 'S-002', 800.00, 'AVAILABLE'),
(7, 1, 'General', 'S-003', 1500.00, 'AVAILABLE'),
(8, 2, 'General', 'S-003', 2500.00, 'AVAILABLE'),
(9, 3, 'General', 'S-003', 800.00, 'SOLD'),
(10, 1, 'General', 'S-004', 1500.00, 'AVAILABLE'),
(11, 2, 'General', 'S-004', 2500.00, 'AVAILABLE'),
(12, 3, 'General', 'S-004', 800.00, 'AVAILABLE'),
(13, 1, 'General', 'S-005', 1500.00, 'AVAILABLE'),
(14, 2, 'General', 'S-005', 2500.00, 'AVAILABLE'),
(15, 3, 'General', 'S-005', 800.00, 'AVAILABLE'),
(16, 1, 'General', 'S-006', 1500.00, 'AVAILABLE'),
(17, 2, 'General', 'S-006', 2500.00, 'AVAILABLE'),
(18, 3, 'General', 'S-006', 800.00, 'AVAILABLE'),
(19, 1, 'General', 'S-007', 1500.00, 'AVAILABLE'),
(20, 2, 'General', 'S-007', 2500.00, 'AVAILABLE'),
(21, 3, 'General', 'S-007', 800.00, 'AVAILABLE'),
(22, 1, 'General', 'S-008', 1500.00, 'AVAILABLE'),
(23, 2, 'General', 'S-008', 2500.00, 'AVAILABLE'),
(24, 3, 'General', 'S-008', 800.00, 'AVAILABLE'),
(25, 1, 'General', 'S-009', 1500.00, 'AVAILABLE'),
(26, 2, 'General', 'S-009', 2500.00, 'AVAILABLE'),
(27, 3, 'General', 'S-009', 800.00, 'AVAILABLE'),
(28, 1, 'General', 'S-010', 1500.00, 'AVAILABLE'),
(29, 2, 'General', 'S-010', 2500.00, 'AVAILABLE'),
(30, 3, 'General', 'S-010', 800.00, 'AVAILABLE'),
(31, 1, 'General', 'S-011', 1500.00, 'AVAILABLE'),
(32, 2, 'General', 'S-011', 2500.00, 'AVAILABLE'),
(33, 3, 'General', 'S-011', 800.00, 'AVAILABLE'),
(34, 1, 'General', 'S-012', 1500.00, 'AVAILABLE'),
(35, 2, 'General', 'S-012', 2500.00, 'AVAILABLE'),
(36, 3, 'General', 'S-012', 800.00, 'AVAILABLE'),
(37, 1, 'General', 'S-013', 1500.00, 'AVAILABLE'),
(38, 2, 'General', 'S-013', 2500.00, 'AVAILABLE'),
(39, 3, 'General', 'S-013', 800.00, 'AVAILABLE'),
(40, 1, 'General', 'S-014', 1500.00, 'AVAILABLE'),
(41, 2, 'General', 'S-014', 2500.00, 'AVAILABLE'),
(42, 3, 'General', 'S-014', 800.00, 'AVAILABLE'),
(43, 1, 'General', 'S-015', 1500.00, 'AVAILABLE'),
(44, 2, 'General', 'S-015', 2500.00, 'AVAILABLE'),
(45, 3, 'General', 'S-015', 800.00, 'AVAILABLE'),
(46, 1, 'General', 'S-016', 1500.00, 'AVAILABLE'),
(47, 2, 'General', 'S-016', 2500.00, 'AVAILABLE'),
(48, 3, 'General', 'S-016', 800.00, 'AVAILABLE'),
(49, 1, 'General', 'S-017', 1500.00, 'AVAILABLE'),
(50, 2, 'General', 'S-017', 2500.00, 'AVAILABLE'),
(51, 3, 'General', 'S-017', 800.00, 'AVAILABLE'),
(52, 1, 'General', 'S-018', 1500.00, 'AVAILABLE'),
(53, 2, 'General', 'S-018', 2500.00, 'AVAILABLE'),
(54, 3, 'General', 'S-018', 800.00, 'AVAILABLE'),
(55, 1, 'General', 'S-019', 1500.00, 'AVAILABLE'),
(56, 2, 'General', 'S-019', 2500.00, 'AVAILABLE'),
(57, 3, 'General', 'S-019', 800.00, 'AVAILABLE'),
(58, 1, 'General', 'S-020', 1500.00, 'AVAILABLE'),
(59, 2, 'General', 'S-020', 2500.00, 'AVAILABLE'),
(60, 3, 'General', 'S-020', 800.00, 'AVAILABLE'),
(61, 1, 'General', 'S-021', 1500.00, 'AVAILABLE'),
(62, 2, 'General', 'S-021', 2500.00, 'AVAILABLE'),
(63, 3, 'General', 'S-021', 800.00, 'AVAILABLE'),
(64, 1, 'General', 'S-022', 1500.00, 'AVAILABLE'),
(65, 2, 'General', 'S-022', 2500.00, 'AVAILABLE'),
(66, 3, 'General', 'S-022', 800.00, 'AVAILABLE'),
(67, 1, 'General', 'S-023', 1500.00, 'AVAILABLE'),
(68, 2, 'General', 'S-023', 2500.00, 'AVAILABLE'),
(69, 3, 'General', 'S-023', 800.00, 'AVAILABLE'),
(70, 1, 'General', 'S-024', 1500.00, 'AVAILABLE'),
(71, 2, 'General', 'S-024', 2500.00, 'AVAILABLE'),
(72, 3, 'General', 'S-024', 800.00, 'AVAILABLE'),
(73, 1, 'General', 'S-025', 1500.00, 'AVAILABLE'),
(74, 2, 'General', 'S-025', 2500.00, 'AVAILABLE'),
(75, 3, 'General', 'S-025', 800.00, 'AVAILABLE'),
(76, 1, 'General', 'S-026', 1500.00, 'AVAILABLE'),
(77, 2, 'General', 'S-026', 2500.00, 'AVAILABLE'),
(78, 3, 'General', 'S-026', 800.00, 'AVAILABLE'),
(79, 1, 'General', 'S-027', 1500.00, 'AVAILABLE'),
(80, 2, 'General', 'S-027', 2500.00, 'AVAILABLE'),
(81, 3, 'General', 'S-027', 800.00, 'AVAILABLE'),
(82, 1, 'General', 'S-028', 1500.00, 'AVAILABLE'),
(83, 2, 'General', 'S-028', 2500.00, 'AVAILABLE'),
(84, 3, 'General', 'S-028', 800.00, 'AVAILABLE'),
(85, 1, 'General', 'S-029', 1500.00, 'AVAILABLE'),
(86, 2, 'General', 'S-029', 2500.00, 'AVAILABLE'),
(87, 3, 'General', 'S-029', 800.00, 'AVAILABLE'),
(88, 1, 'General', 'S-030', 1500.00, 'AVAILABLE'),
(89, 2, 'General', 'S-030', 2500.00, 'AVAILABLE'),
(90, 3, 'General', 'S-030', 800.00, 'AVAILABLE'),
(91, 1, 'General', 'S-031', 1500.00, 'AVAILABLE'),
(92, 2, 'General', 'S-031', 2500.00, 'AVAILABLE'),
(93, 3, 'General', 'S-031', 800.00, 'AVAILABLE'),
(94, 1, 'General', 'S-032', 1500.00, 'AVAILABLE'),
(95, 2, 'General', 'S-032', 2500.00, 'AVAILABLE'),
(96, 3, 'General', 'S-032', 800.00, 'AVAILABLE'),
(97, 1, 'General', 'S-033', 1500.00, 'AVAILABLE'),
(98, 2, 'General', 'S-033', 2500.00, 'AVAILABLE'),
(99, 3, 'General', 'S-033', 800.00, 'AVAILABLE'),
(100, 1, 'General', 'S-034', 1500.00, 'AVAILABLE'),
(101, 2, 'General', 'S-034', 2500.00, 'AVAILABLE'),
(102, 3, 'General', 'S-034', 800.00, 'AVAILABLE'),
(103, 1, 'General', 'S-035', 1500.00, 'AVAILABLE'),
(104, 2, 'General', 'S-035', 2500.00, 'AVAILABLE'),
(105, 3, 'General', 'S-035', 800.00, 'AVAILABLE'),
(106, 1, 'General', 'S-036', 1500.00, 'AVAILABLE'),
(107, 2, 'General', 'S-036', 2500.00, 'AVAILABLE'),
(108, 3, 'General', 'S-036', 800.00, 'AVAILABLE'),
(109, 1, 'General', 'S-037', 1500.00, 'AVAILABLE'),
(110, 2, 'General', 'S-037', 2500.00, 'AVAILABLE'),
(111, 3, 'General', 'S-037', 800.00, 'AVAILABLE'),
(112, 1, 'General', 'S-038', 1500.00, 'AVAILABLE'),
(113, 2, 'General', 'S-038', 2500.00, 'AVAILABLE'),
(114, 3, 'General', 'S-038', 800.00, 'AVAILABLE'),
(115, 1, 'General', 'S-039', 1500.00, 'AVAILABLE'),
(116, 2, 'General', 'S-039', 2500.00, 'AVAILABLE'),
(117, 3, 'General', 'S-039', 800.00, 'AVAILABLE'),
(118, 1, 'General', 'S-040', 1500.00, 'AVAILABLE'),
(119, 2, 'General', 'S-040', 2500.00, 'AVAILABLE'),
(120, 3, 'General', 'S-040', 800.00, 'AVAILABLE'),
(121, 1, 'General', 'S-041', 1500.00, 'AVAILABLE'),
(122, 2, 'General', 'S-041', 2500.00, 'AVAILABLE'),
(123, 3, 'General', 'S-041', 800.00, 'AVAILABLE'),
(124, 1, 'General', 'S-042', 1500.00, 'AVAILABLE'),
(125, 2, 'General', 'S-042', 2500.00, 'AVAILABLE'),
(126, 3, 'General', 'S-042', 800.00, 'AVAILABLE'),
(127, 1, 'General', 'S-043', 1500.00, 'AVAILABLE'),
(128, 2, 'General', 'S-043', 2500.00, 'AVAILABLE'),
(129, 3, 'General', 'S-043', 800.00, 'AVAILABLE'),
(130, 1, 'General', 'S-044', 1500.00, 'AVAILABLE'),
(131, 2, 'General', 'S-044', 2500.00, 'AVAILABLE'),
(132, 3, 'General', 'S-044', 800.00, 'AVAILABLE'),
(133, 1, 'General', 'S-045', 1500.00, 'AVAILABLE'),
(134, 2, 'General', 'S-045', 2500.00, 'AVAILABLE'),
(135, 3, 'General', 'S-045', 800.00, 'AVAILABLE'),
(136, 1, 'General', 'S-046', 1500.00, 'AVAILABLE'),
(137, 2, 'General', 'S-046', 2500.00, 'AVAILABLE'),
(138, 3, 'General', 'S-046', 800.00, 'AVAILABLE'),
(139, 1, 'General', 'S-047', 1500.00, 'AVAILABLE'),
(140, 2, 'General', 'S-047', 2500.00, 'AVAILABLE'),
(141, 3, 'General', 'S-047', 800.00, 'AVAILABLE'),
(142, 1, 'General', 'S-048', 1500.00, 'AVAILABLE'),
(143, 2, 'General', 'S-048', 2500.00, 'AVAILABLE'),
(144, 3, 'General', 'S-048', 800.00, 'AVAILABLE'),
(145, 1, 'General', 'S-049', 1500.00, 'AVAILABLE'),
(146, 2, 'General', 'S-049', 2500.00, 'AVAILABLE'),
(147, 3, 'General', 'S-049', 800.00, 'AVAILABLE'),
(148, 1, 'General', 'S-050', 1500.00, 'AVAILABLE'),
(149, 2, 'General', 'S-050', 2500.00, 'AVAILABLE'),
(150, 3, 'General', 'S-050', 800.00, 'AVAILABLE'),
(151, 1, 'General', 'S-051', 1500.00, 'AVAILABLE'),
(152, 2, 'General', 'S-051', 2500.00, 'AVAILABLE'),
(153, 3, 'General', 'S-051', 800.00, 'AVAILABLE'),
(154, 1, 'General', 'S-052', 1500.00, 'AVAILABLE'),
(155, 2, 'General', 'S-052', 2500.00, 'AVAILABLE'),
(156, 3, 'General', 'S-052', 800.00, 'AVAILABLE'),
(157, 1, 'General', 'S-053', 1500.00, 'AVAILABLE'),
(158, 2, 'General', 'S-053', 2500.00, 'AVAILABLE'),
(159, 3, 'General', 'S-053', 800.00, 'AVAILABLE'),
(160, 1, 'General', 'S-054', 1500.00, 'AVAILABLE'),
(161, 2, 'General', 'S-054', 2500.00, 'AVAILABLE'),
(162, 3, 'General', 'S-054', 800.00, 'AVAILABLE'),
(163, 1, 'General', 'S-055', 1500.00, 'AVAILABLE'),
(164, 2, 'General', 'S-055', 2500.00, 'AVAILABLE'),
(165, 3, 'General', 'S-055', 800.00, 'AVAILABLE'),
(166, 1, 'General', 'S-056', 1500.00, 'AVAILABLE'),
(167, 2, 'General', 'S-056', 2500.00, 'AVAILABLE'),
(168, 3, 'General', 'S-056', 800.00, 'AVAILABLE'),
(169, 1, 'General', 'S-057', 1500.00, 'AVAILABLE'),
(170, 2, 'General', 'S-057', 2500.00, 'AVAILABLE'),
(171, 3, 'General', 'S-057', 800.00, 'AVAILABLE'),
(172, 1, 'General', 'S-058', 1500.00, 'AVAILABLE'),
(173, 2, 'General', 'S-058', 2500.00, 'AVAILABLE'),
(174, 3, 'General', 'S-058', 800.00, 'AVAILABLE'),
(175, 1, 'General', 'S-059', 1500.00, 'AVAILABLE'),
(176, 2, 'General', 'S-059', 2500.00, 'AVAILABLE'),
(177, 3, 'General', 'S-059', 800.00, 'AVAILABLE'),
(178, 1, 'General', 'S-060', 1500.00, 'AVAILABLE'),
(179, 2, 'General', 'S-060', 2500.00, 'AVAILABLE'),
(180, 3, 'General', 'S-060', 800.00, 'AVAILABLE'),
(181, 1, 'General', 'S-061', 1500.00, 'AVAILABLE'),
(182, 3, 'General', 'S-061', 800.00, 'AVAILABLE'),
(183, 1, 'General', 'S-062', 1500.00, 'AVAILABLE'),
(184, 3, 'General', 'S-062', 800.00, 'AVAILABLE'),
(185, 1, 'General', 'S-063', 1500.00, 'AVAILABLE'),
(186, 3, 'General', 'S-063', 800.00, 'AVAILABLE'),
(187, 1, 'General', 'S-064', 1500.00, 'AVAILABLE'),
(188, 3, 'General', 'S-064', 800.00, 'AVAILABLE'),
(189, 1, 'General', 'S-065', 1500.00, 'AVAILABLE'),
(190, 3, 'General', 'S-065', 800.00, 'AVAILABLE'),
(191, 1, 'General', 'S-066', 1500.00, 'AVAILABLE'),
(192, 3, 'General', 'S-066', 800.00, 'AVAILABLE'),
(193, 1, 'General', 'S-067', 1500.00, 'AVAILABLE'),
(194, 3, 'General', 'S-067', 800.00, 'AVAILABLE'),
(195, 1, 'General', 'S-068', 1500.00, 'AVAILABLE'),
(196, 3, 'General', 'S-068', 800.00, 'AVAILABLE'),
(197, 1, 'General', 'S-069', 1500.00, 'AVAILABLE'),
(198, 3, 'General', 'S-069', 800.00, 'AVAILABLE'),
(199, 1, 'General', 'S-070', 1500.00, 'AVAILABLE'),
(200, 3, 'General', 'S-070', 800.00, 'AVAILABLE'),
(201, 1, 'General', 'S-071', 1500.00, 'AVAILABLE'),
(202, 3, 'General', 'S-071', 800.00, 'AVAILABLE'),
(203, 1, 'General', 'S-072', 1500.00, 'AVAILABLE'),
(204, 3, 'General', 'S-072', 800.00, 'AVAILABLE'),
(205, 1, 'General', 'S-073', 1500.00, 'AVAILABLE'),
(206, 3, 'General', 'S-073', 800.00, 'AVAILABLE'),
(207, 1, 'General', 'S-074', 1500.00, 'AVAILABLE'),
(208, 3, 'General', 'S-074', 800.00, 'AVAILABLE'),
(209, 1, 'General', 'S-075', 1500.00, 'AVAILABLE'),
(210, 3, 'General', 'S-075', 800.00, 'AVAILABLE'),
(211, 1, 'General', 'S-076', 1500.00, 'AVAILABLE'),
(212, 3, 'General', 'S-076', 800.00, 'AVAILABLE'),
(213, 1, 'General', 'S-077', 1500.00, 'AVAILABLE'),
(214, 3, 'General', 'S-077', 800.00, 'AVAILABLE'),
(215, 1, 'General', 'S-078', 1500.00, 'AVAILABLE'),
(216, 3, 'General', 'S-078', 800.00, 'AVAILABLE'),
(217, 1, 'General', 'S-079', 1500.00, 'AVAILABLE'),
(218, 3, 'General', 'S-079', 800.00, 'AVAILABLE'),
(219, 1, 'General', 'S-080', 1500.00, 'AVAILABLE'),
(220, 3, 'General', 'S-080', 800.00, 'AVAILABLE'),
(221, 1, 'General', 'S-081', 1500.00, 'AVAILABLE'),
(222, 1, 'General', 'S-082', 1500.00, 'AVAILABLE'),
(223, 1, 'General', 'S-083', 1500.00, 'AVAILABLE'),
(224, 1, 'General', 'S-084', 1500.00, 'AVAILABLE'),
(225, 1, 'General', 'S-085', 1500.00, 'AVAILABLE'),
(226, 1, 'General', 'S-086', 1500.00, 'AVAILABLE'),
(227, 1, 'General', 'S-087', 1500.00, 'AVAILABLE'),
(228, 1, 'General', 'S-088', 1500.00, 'AVAILABLE'),
(229, 1, 'General', 'S-089', 1500.00, 'AVAILABLE'),
(230, 1, 'General', 'S-090', 1500.00, 'AVAILABLE'),
(231, 1, 'General', 'S-091', 1500.00, 'AVAILABLE'),
(232, 1, 'General', 'S-092', 1500.00, 'AVAILABLE'),
(233, 1, 'General', 'S-093', 1500.00, 'AVAILABLE'),
(234, 1, 'General', 'S-094', 1500.00, 'AVAILABLE'),
(235, 1, 'General', 'S-095', 1500.00, 'AVAILABLE'),
(236, 1, 'General', 'S-096', 1500.00, 'AVAILABLE'),
(237, 1, 'General', 'S-097', 1500.00, 'AVAILABLE'),
(238, 1, 'General', 'S-098', 1500.00, 'AVAILABLE'),
(239, 1, 'General', 'S-099', 1500.00, 'AVAILABLE'),
(240, 1, 'General', 'S-100', 1500.00, 'AVAILABLE');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `user_id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `email` varchar(150) NOT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `role` enum('CUSTOMER','ADMIN') NOT NULL DEFAULT 'CUSTOMER',
  `created_at` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`user_id`, `name`, `email`, `phone`, `password`, `role`, `created_at`) VALUES
(1, 'System Admin', 'admin@ticketing.com', '09171234567', 'JAvlGPq9JyTdtvBO6x2llnRI1+gxwIyPqCKAn3THIKk=', 'ADMIN', '2026-07-17 09:23:01'),
(2, 'ernest joaquin', 'ernest@gmail.com', '0999822382', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', 'CUSTOMER', '2026-07-17 10:00:46');

-- --------------------------------------------------------

--
-- Table structure for table `venues`
--

CREATE TABLE `venues` (
  `venue_id` int(11) NOT NULL,
  `name` varchar(150) NOT NULL,
  `address` varchar(255) DEFAULT NULL,
  `capacity` int(11) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `venues`
--

INSERT INTO `venues` (`venue_id`, `name`, `address`, `capacity`) VALUES
(1, 'SM Mall of Asia Arena', 'Pasay City, Metro Manila', 20000),
(2, 'Araneta Coliseum', 'Cubao, Quezon City', 16000),
(3, 'Bacolod Convention Center', 'Bacolod City, Negros Occidental', 3000);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `bookings`
--
ALTER TABLE `bookings`
  ADD PRIMARY KEY (`booking_id`),
  ADD KEY `customer_id` (`customer_id`);

--
-- Indexes for table `booking_items`
--
ALTER TABLE `booking_items`
  ADD PRIMARY KEY (`item_id`),
  ADD KEY `booking_id` (`booking_id`),
  ADD KEY `event_id` (`event_id`),
  ADD KEY `seat_id` (`seat_id`);

--
-- Indexes for table `categories`
--
ALTER TABLE `categories`
  ADD PRIMARY KEY (`category_id`);

--
-- Indexes for table `events`
--
ALTER TABLE `events`
  ADD PRIMARY KEY (`event_id`),
  ADD KEY `venue_id` (`venue_id`),
  ADD KEY `category_id` (`category_id`);

--
-- Indexes for table `payments`
--
ALTER TABLE `payments`
  ADD PRIMARY KEY (`payment_id`),
  ADD KEY `booking_id` (`booking_id`);

--
-- Indexes for table `seats`
--
ALTER TABLE `seats`
  ADD PRIMARY KEY (`seat_id`),
  ADD KEY `event_id` (`event_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`user_id`),
  ADD UNIQUE KEY `email` (`email`);

--
-- Indexes for table `venues`
--
ALTER TABLE `venues`
  ADD PRIMARY KEY (`venue_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `bookings`
--
ALTER TABLE `bookings`
  MODIFY `booking_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `booking_items`
--
ALTER TABLE `booking_items`
  MODIFY `item_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `categories`
--
ALTER TABLE `categories`
  MODIFY `category_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `events`
--
ALTER TABLE `events`
  MODIFY `event_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `payments`
--
ALTER TABLE `payments`
  MODIFY `payment_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `seats`
--
ALTER TABLE `seats`
  MODIFY `seat_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=256;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `venues`
--
ALTER TABLE `venues`
  MODIFY `venue_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `bookings`
--
ALTER TABLE `bookings`
  ADD CONSTRAINT `bookings_ibfk_1` FOREIGN KEY (`customer_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE;

--
-- Constraints for table `booking_items`
--
ALTER TABLE `booking_items`
  ADD CONSTRAINT `booking_items_ibfk_1` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`booking_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `booking_items_ibfk_2` FOREIGN KEY (`event_id`) REFERENCES `events` (`event_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `booking_items_ibfk_3` FOREIGN KEY (`seat_id`) REFERENCES `seats` (`seat_id`) ON DELETE CASCADE;

--
-- Constraints for table `events`
--
ALTER TABLE `events`
  ADD CONSTRAINT `events_ibfk_1` FOREIGN KEY (`venue_id`) REFERENCES `venues` (`venue_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `events_ibfk_2` FOREIGN KEY (`category_id`) REFERENCES `categories` (`category_id`) ON DELETE CASCADE;

--
-- Constraints for table `payments`
--
ALTER TABLE `payments`
  ADD CONSTRAINT `payments_ibfk_1` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`booking_id`) ON DELETE CASCADE;

--
-- Constraints for table `seats`
--
ALTER TABLE `seats`
  ADD CONSTRAINT `seats_ibfk_1` FOREIGN KEY (`event_id`) REFERENCES `events` (`event_id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
