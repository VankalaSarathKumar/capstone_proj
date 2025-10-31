package com.capstone.kafkaDeserializer;



import com.capstone.project.domain.Account;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;

public class AccountDeserializer implements Deserializer<Account> {
    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public Account deserialize(String topic, byte[] bytes) {
        Account account = null;
        try {
            account = mapper.readValue(bytes, Account.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return account;
    }
}

