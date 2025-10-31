
package com.capstone.kafka.partitioner;


import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.serialization.StringSerializer;
import java.util.Map;

public class AccountTypePartitioner implements Partitioner {

    @Override
    public void configure(Map<String, ?> configs) {
       
    }

    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        String accountType = (String) key;  // assuming key is account type (CA, SB, RD, LOAN)
        int partition = 0;

        
        switch (accountType) {
            case "CA":
                partition = 0;
                break;
            case "SB":
                partition = 1;
                break;
            case "RD":
                partition = 2;
                break;
            case "LOAN":
                partition = 3;
                break;
            default:
                throw new IllegalArgumentException("Invalid account type: " + accountType);
        }

        return partition;
    }

    @Override
    public void close() {
        
    }
}

