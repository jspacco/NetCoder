-- phpMyAdmin SQL Dump
-- version 2.11.7.1
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Mar 21, 2011 at 10:20 PM
-- Server version: 5.1.53
-- PHP Version: 5.2.6

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";

--
-- Database: `netcoder`
--

-- --------------------------------------------------------

--
-- Table structure for table `changes`
--

CREATE TABLE `changes` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(10) unsigned NOT NULL,
  `problem_id` int(11) NOT NULL,
  `type` enum('insert_text','remove_text','insert_lines','remove_lines') CHARACTER SET utf8 DEFAULT NULL,
  `start_row` smallint(5) unsigned NOT NULL,
  `end_row` smallint(5) unsigned NOT NULL,
  `start_col` smallint(5) unsigned NOT NULL,
  `end_col` smallint(5) unsigned NOT NULL,
  `timestamp` int(10) unsigned NOT NULL,
  `text` mediumtext NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=ucs2 AUTO_INCREMENT=1 ;

--
-- Dumping data for table `changes`
--


-- --------------------------------------------------------

--
-- Table structure for table `problems`
--

CREATE TABLE `problems` (
  `problem_id` int(11) NOT NULL AUTO_INCREMENT,
  `testname` varchar(255) NOT NULL,
  `description` text NOT NULL,
  PRIMARY KEY (`problem_id`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=2 ;

--
-- Dumping data for table `problems`
--

INSERT INTO `problems` VALUES(1, 'sq', 'Square a number.');
