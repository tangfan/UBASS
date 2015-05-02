#!/bin/sh
#查看spark各节点下 找出有sparkstreaming listen端口的主机
netstat -apn|grep 33333
#将该主机ip配置到spark.conf中的a1.sinks.hdfs01.hostname
#启动flume agent
/home/admin/upa/flume-1.5.2-bin/bin/flume-ng agent -n a1 -c /home/admin/upa/flume-1.5.2-bin/conf -f /home/admin/upa/flume-1.5.2-bin/conf/spark.conf -Dflume.root.logger=DEBUG,console
#-Dflume.monitoring.type=http -Dflume.monitoring.port=34545 &
curl -X POST -d '[{ "headers" :{"type" : "1"},"body" : "idoall_TEST1"}]' http://172.17.138.27:55555

