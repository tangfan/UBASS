package com.unionpay.ubass.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.flume.FlumeUtils;
import org.apache.spark.streaming.flume.SparkFlumeEvent;
import scala.Tuple2;

import java.io.InputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public final class StaticWindowLoadTime {
    private StaticWindowLoadTime() {
    }

    public static class AvgCount implements Serializable {
        public AvgCount(double total, int num) {
            total_ = total;
            num_ = num;
        }

        public double total_;
        public int num_;

        public double avg() {
            return total_ / (double) num_;
        }
    }

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        if (args.length != 3) {
            System.err.println("Usage: StaticWindowLoadTime <host> <port> <interval>");
            System.exit(1);
        }

        String host = args[0];
        int port = Integer.parseInt(args[1]);
        Duration batchInterval = new Duration(Integer.parseInt(args[2]));

        SparkConf sparkConf = new SparkConf().setAppName("StaticWindowLoadTime");
        JavaStreamingContext ssc = new JavaStreamingContext(sparkConf, batchInterval);
        JavaReceiverInputDStream<SparkFlumeEvent> flumeStream = FlumeUtils.createStream(ssc, host, port);

	//只处理type为window.load的日志数据
        JavaDStream filterDStream = flumeStream.filter(new Function<SparkFlumeEvent, Boolean>() {
            @Override
            public Boolean call(SparkFlumeEvent flumeEvent) throws Exception {
                Map map = flumeEventToMap(flumeEvent);
                if ("window.load".equals((String) map.get("type"))) {
                    return true;
                }
                return false;
            }
        });

        JavaPairDStream pairDStream = filterDStream.mapToPair(new PairFunction<SparkFlumeEvent, String, Double>() {
            @Override
            public Tuple2<String, Double> call(SparkFlumeEvent flumeEvent) throws Exception {
                Map map = flumeEventToMap(flumeEvent);
                Tuple2 kv = new Tuple2((String) map.get("sysId"), Double.parseDouble((String) map.get("result")));
                return kv;
            }
        });

        pairDStream.print();

        AvgCount initial = new AvgCount(0, 0);

	//按照sysid combine统计平均时间
        pairDStream.foreachRDD(new Function<JavaPairRDD, Void>() {
            @Override
            public Void call(JavaPairRDD pairRDD) throws Exception {
                JavaPairRDD<String, AvgCount> avgCountRDD = pairRDD.combineByKey(
                        new Function<Double, AvgCount>() {
                            public AvgCount call(Double x) {
                                return new AvgCount(x, 1);
                            }
                        },
                        new Function2<AvgCount, Double, AvgCount>() {
                            public AvgCount call(AvgCount a, Double x) {
                                a.total_ += x;
                                a.num_ += 1;
                                return a;
                            }
                        },
                        new Function2<AvgCount, AvgCount, AvgCount>() {
                            public AvgCount call(AvgCount a, AvgCount b) {
                                a.total_ += b.total_;
                                a.num_ += b.num_;
                                return a;
                            }
                        });
                Map avgCountMap = avgCountRDD.collectAsMap();

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Properties prop = new Properties();
                InputStream is = this.getClass().getResourceAsStream("/conf.properties");
                prop.load(is);
                is.close();

                Class.forName(prop.getProperty("jdbc.driver"));
                String url = prop.getProperty("jdbc.url");
                String user = prop.getProperty("jdbc.user");
                String password = prop.getProperty("jdbc.password");

                Connection conn = DriverManager.getConnection(url, user, password);
                PreparedStatement psInsert = conn.prepareStatement("insert into window_load_stat values(?,?,?)");
                Iterator it = avgCountMap.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry e = (Map.Entry) it.next();
                    System.out.println(e.getKey() + ":" + ((AvgCount) e.getValue()).avg());

                    psInsert.setString(1, (String)e.getKey());
                    psInsert.setDouble(2, ((AvgCount) e.getValue()).avg());
                    psInsert.setString(3, sdf.format(new Date()));
                    psInsert.addBatch();
                }
                int[] result = psInsert.executeBatch();
                System.out.println("insert result:"+Arrays.toString(result));
                psInsert.close();
                conn.close();

                return null;
            }
        });

        ssc.start();
        ssc.awaitTermination();
    }

    private static Map flumeEventToMap(SparkFlumeEvent flumeEvent) {
        String line = new String(flumeEvent.event().getBody().array());
        line = line.substring(line.indexOf("\t") + 1);
        String[] logInfoArr = line.split("\t");
        Map map = new HashMap();
        for (int i = 0; i < logInfoArr.length; i++) {
            String[] kv = logInfoArr[i].split("=");
            if (kv.length == 2) {
                map.put(kv[0], kv[1]);
            } else if (kv.length == 1) {
                map.put(kv[0], "");
            }
        }
        return map;
    }
}


