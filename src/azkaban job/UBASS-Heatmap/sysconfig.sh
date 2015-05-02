#!/bin/bash
HOSTNAME="172.17.138.67"
PORT="3307"
USERNAME="root"
PASSWORD="123456"
DBNAME="ubass_stat"

yesterday="`date -d yesterday +"%Y-%m-%d"`"  
WORKINGPATH=`pwd`
MYSQLOUTPUTPATH="/home/hadoop/output"
WAREHOUSEPATH="/user/hive/warehouse"
SHNAME=$1
FILENAME=$(echo ${SHNAME:6:`expr \`expr index $SHNAME .\` - 7`})
CVSOUTPUTPATH=ubass/output/upop_new/$FILENAME/

if [ $# -eq 1 ]; then
    # yesterday
    BEGINDATE=`date -d "$yesterday" +%s`
    ENDDATE=`date -d "$yesterday" +%s`
elif [ $# -eq 2 ]; then
    beginDateInput=$(echo "$2"|awk -F '/' '{print $1}')
    endDateInput=$(echo "$2"|awk -F '/' '{print $2}')
    if [ -z $beginDateInput ] && [ -z $endDateInput ]; then
        # yesterday
        BEGINDATE=`date -d "$yesterday" +%s`
        ENDDATE=`date -d "$yesterday" +%s`
    elif [ -z $beginDateInput ] || [ -z $endDateInput ]; then
        # The specified day
        if [ -z $beginDateInput ]; then
              BEGINDATE=`date -d "$endDateInput" +%s`
              ENDDATE=`date -d "$endDateInput" +%s`
        else
             BEGINDATE=`date -d "$beginDateInput" +%s`
             ENDDATE=`date -d "$beginDateInput" +%s`
        fi
    else
        # A specified period of date
        BEGINDATE=`date -d "$beginDateInput" +%s`
        ENDDATE=`date -d "$endDateInput" +%s`
    fi
else
    echo "ERROR: error parameters"
fi
