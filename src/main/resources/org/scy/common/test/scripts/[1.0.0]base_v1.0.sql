-- ========================================================
-- 数据库脚本测试
-- 单行注释以“--”开头，多行注释以“/*”开头并且“*/”结尾，
-- 日期：<2017-06-09>
-- ========================================================

-- 如果数据库不存在，则新建数据库 <2017-06-09 17:30:00>
-- CREATE DATABASE IF NOT EXISTS testdb;
-- GO

-- USE testdb;

-- 创建字典表
drop table if exists sys_dictionary;
CREATE TABLE sys_dictionary (
    id INT NOT NULL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- 创建用户表
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id INT NOT NULL PRIMARY KEY,
    name VARCHAR(30) NULL
);

-- 用户表添加日期 <2017-08-14 18:07:00>
alter table `sys_user` Add column createDate BIGINT null;
