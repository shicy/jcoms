-- ========================================================
-- 数据库脚本测试
-- 单行注释以“--”开头，多行注释以“/*”开头并且“*/”结尾，
-- 日期：<2017-06-09>
-- ========================================================

-- 如果数据库不存在，则新建数据库 <2017-06-09 17:30:00>
CREATE DATABASE IF NOT EXISTS testdb;
GO

-- 创建用户表
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id INT NOT NULL PRIMARY KEY,
    name VARCHAR(30) NULL
);
