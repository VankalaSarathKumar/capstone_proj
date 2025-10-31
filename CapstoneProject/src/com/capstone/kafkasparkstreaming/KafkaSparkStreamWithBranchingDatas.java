

package com.capstone.kafkasparkstreaming;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.streaming.Trigger;
import static org.apache.spark.sql.functions.*;

public class KafkaSparkStreamWithBranchingDatas {

    public static void main(String[] args) {

        SparkSession spark = SparkSession.builder()
            .appName("data-set-streaming-app")
            .master("local[*]")
            .getOrCreate();

        spark.sparkContext().setLogLevel("WARN");

        // Kafka source
        Dataset<Row> kafkaDS = spark.readStream()
            .format("kafka")
            .option("kafka.bootstrap.servers", "localhost:9092")
            .option("subscribe", "account-topic")
            .load()
            .selectExpr("CAST(value AS STRING)");

        
        Dataset<Row> kafkaJsonDS = kafkaDS.select(from_json(col("value"), Utility.AccountSchema()).as("data"));
        Dataset<Row> kafkaAccDS = kafkaJsonDS.select("data.*");

        // File source
        Dataset<Row> fileAccDS = spark.readStream()
            .format("json")
            .schema(Utility.AccountSchema()) // Ensure schema matches Kafka schema
            .load("c:/capstone");

        // Combine both sources
        Dataset<Row> combinedDS = kafkaAccDS.union(fileAccDS);

       
        Dataset<Row> caDS = combinedDS.where("accounttype='CA'").select("accountnumber", "customerid", "branch");
        Dataset<Row> sbDS = combinedDS.where("accounttype='SB'").select("accountnumber", "customerid", "branch");
        Dataset<Row> rdDS = combinedDS.where("accounttype='RD'").select("accountnumber", "customerid", "branch");
        Dataset<Row> loanDS = combinedDS.where("accounttype='LOAN'").select("accountnumber", "customerid", "branch");

       
        Dataset<Row> accountCounts = combinedDS
            .groupBy("branch", "accounttype")
            .agg(count("accountnumber").alias("account_count"));

        StreamingQuery caStreamingQuery = null;
        StreamingQuery sbStreamingQuery = null;
        StreamingQuery rdStreamingQuery = null;
        StreamingQuery loanStreamingQuery = null;
        StreamingQuery jdbcStreamingQuery = null;

        try {
           
            caStreamingQuery = caDS.writeStream()
                .trigger(Trigger.ProcessingTime(30, TimeUnit.SECONDS))
                .format("csv")
                .option("checkpointLocation", "c:/cacheckpoint")
                .option("header", true)
                .start("c:/capstone_ca_out");

           
            sbStreamingQuery = sbDS.writeStream()
                .trigger(Trigger.ProcessingTime(30, TimeUnit.SECONDS))
                .format("json")
                .option("checkpointLocation", "c:/sbcheckpoint")
                .start("c:/capstone_sb_out");

           
            rdStreamingQuery = rdDS.writeStream()
                .trigger(Trigger.ProcessingTime(30, TimeUnit.SECONDS))
                .format("avro")
                .option("checkpointLocation", "c:/rdcheckpoint")
                .start("c:/capstone_rd_out");

            
            loanStreamingQuery = loanDS.writeStream()
                .trigger(Trigger.ProcessingTime(30, TimeUnit.SECONDS))
                .format("parquet")
                .option("checkpointLocation", "c:/loancheckpoint")
                .start("c:/capstone_loan_out");

           
            jdbcStreamingQuery = accountCounts.writeStream()
                .outputMode("complete")
                .trigger(Trigger.ProcessingTime(30, TimeUnit.SECONDS))
                .foreachBatch((batchDF, batchId) -> {
                    try {
                        batchDF.write()
                            .mode(SaveMode.Append)
                            .format("jdbc")
                            .option("url", "jdbc:mysql://localhost:3306/trainingdb")
                            .option("dbtable", "account_tb1")
                            .option("user", "root")
                            .option("password", "Password@1")
                            .save();
                    } catch (Exception e) {
                        System.err.println("Error writing batch to MySQL: " + e.getMessage());
                    }
                })
                .option("checkpointLocation", "c:/jdbc_checkpoint")
                .start();

            System.out.println("streaming started");

            
            Thread.sleep(10 * 60 * 1000);

            // Stop the streams after the time period
            caStreamingQuery.stop();
            sbStreamingQuery.stop();
            rdStreamingQuery.stop();
            loanStreamingQuery.stop();
            jdbcStreamingQuery.stop();

        } catch (InterruptedException | TimeoutException e) {
            e.printStackTrace();
        }

        System.out.println("streaming stopped");
    }
}

