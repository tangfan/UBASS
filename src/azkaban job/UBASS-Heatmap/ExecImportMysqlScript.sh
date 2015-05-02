#!/bin/bash

source `pwd`/sysconfig.sh

while [ "$BEGINDATE" -le "$ENDDATE" ];do 
    STATDATE=`date -d @$BEGINDATE +"%Y-%m-%d"`;  
    BEGINDATE=$((BEGINDATE+86400));


    echo "INFO: #################### [begin execute $SHNAME $STATDATE] ####################"
    source `pwd`/$SHNAME $STATDATE
    
    eval insert_sql="$"${FILENAME}_insert;
    eval select_sql="$"${FILENAME}_select;

    selectSub1="exportDataToMySQL.*"
    selectSub2=" FROM ( "${select_sql}" ) exportDataToMySQL "

    exeSQL="SELECT dboutput ('jdbc:mysql://$HOSTNAME:$PORT/$DBNAME','$USERNAME','$PASSWORD','$insert_sql', $selectSub1) $selectSub2"
    echo "INFO: $exeSQL"
    hive -e"$exeSQL"
    
    echo "INFO: #################### [end execute $SHNAME $STATDATE] ####################"

done
