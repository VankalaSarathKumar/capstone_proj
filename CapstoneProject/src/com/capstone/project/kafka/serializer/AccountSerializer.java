package com.capstone.project.kafka.serializer;


import com.capstone.project.domain.Account;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;

public class AccountSerializer implements Serializer<Account> {
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(String topic, Account account) {
        System.out.println("serializing account with accountnumber " + account.getaccountnumber());
        byte[] array = null;
        try {
            array = objectMapper.writeValueAsBytes(account);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return array;
    }
}
