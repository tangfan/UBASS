#!/bin/bash

source `pwd`/sysconfig.sh

#创建数据库
create_db_sql="CREATE DATABASE IF NOT EXISTS ${DBNAME} DEFAULT CHARACTER SET utf8;"

mysql -h${HOSTNAME}  -P${PORT}  -u${USERNAME} -p${PASSWORD} -e "${create_db_sql}"

#创建表
create_heatmap_stat="CREATE TABLE IF NOT EXISTS heatmap_stat ( sysid VARCHAR (25) NULL COMMENT '系统号', url VARCHAR (100) NULL COMMENT '页面url', mx INT NULL COMMENT '鼠标点击的X坐标', my INT NULL COMMENT '鼠标点击的Y坐标', browsername VARCHAR (25) NULL COMMENT '浏览器名称', browserversion VARCHAR (25) NULL COMMENT '浏览器版本', province VARCHAR (25) NULL COMMENT '省份', city VARCHAR (25) NULL COMMENT '城市', widthheight VARCHAR (25) NULL COMMENT '分辨率', counter INT NULL COMMENT '数值', statdate VARCHAR (15) NULL COMMENT '统计日期', PRIMARY KEY (sysid, url, mx, my, browsername, browserversion, province, city, widthheight, statdate)) ENGINE = MyISAM DEFAULT CHARSET = utf8;"

mysql -h${HOSTNAME}  -P${PORT}  -u${USERNAME} -p${PASSWORD} ${DBNAME} -e"${create_heatmap_stat}"