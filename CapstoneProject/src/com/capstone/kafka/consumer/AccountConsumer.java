package com.capstone.kafka.consumer;

import com.capstone.project.domain.Account;
import com.capstone.kafkaDeserializer.AccountDeserializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class AccountConsumer {

    public static void main(String[] args) {
       
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, AccountDeserializer.class);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "account-consumer-group");
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");  // Consume from the earliest available message
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");    // Disable auto commit for better control over offsets

        
        KafkaConsumer<String, Account> consumer = new KafkaConsumer<>(properties);

        
        List<String> topics = Collections.singletonList("account-topic");
        consumer.subscribe(topics);

        
        try {
            while (true) {
                
                ConsumerRecords<String, Account> records = consumer.poll(Duration.ofSeconds(20));  // Adjust time as needed

                
                records.forEach(record -> {
                   
                    Account account = record.value();
                    System.out.println("Consumed message from partition " + record.partition() + ": " + account);
                    System.out.println("Account Number: " + account.getaccountnumber());
                    System.out.println("Customer ID: " + account.getcustomerid());
                    System.out.println("Account Type: " + account.getaccounttype());
                    System.out.println("Branch: " + account.getbranch());
                });

                
                consumer.commitAsync();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            consumer.close(); 
        }
    }
}
