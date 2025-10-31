package com.capstone.kafka.sender;

import com.capstone.project.domain.Account;
import com.capstone.project.kafka.serializer.AccountSerializer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class AccountSender {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, AccountSerializer.class);
        
        props.put("partitioner.class", "com.capstone.kafka.partitioner.AccountTypePartitioner");

        String topic = "account-topic";  
        KafkaProducer<String, Account> producer = new KafkaProducer<>(props);

       
        for (int i = 0; i < 100; i++) {
            Account account = new Account();
            account.setaccountnumber(i);
            account.setcustomerid(i + 1000);
            account.setaccounttype(i % 4 == 0 ? "CA" : i % 4 == 1 ? "SB" : i % 4 == 2 ? "RD" : "LOAN");
            account.setbranch("Branch-" + (i % 10));

            
            producer.send(new ProducerRecord<>(topic, account.getaccounttype(), account));
        }
        
        producer.close();
        System.out.println("Messages sent");
    }
}
