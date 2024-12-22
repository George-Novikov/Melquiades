package com.georgen.melquiades.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.IntStream;

public class Serializer {

    /** Thread-safe wrapper (Bill Pugh Singleton). Do not refactor. */
    private static class Holder {
        private static final int DEFAULT_POOL_SIZE = 10;
        private static final ObjectMapper SERIALIZER = newMapper();
        private static final ConcurrentLinkedDeque<ObjectMapper> POOL = initPool();
    }

    public static String serialize(Object object) throws JsonProcessingException {
        ObjectMapper mapper = Holder.POOL.pollFirst();
        if (mapper == null) mapper = newMapper();
        String json = mapper.writeValueAsString(object);
        Holder.POOL.offerLast(mapper);
        System.out.println("Serializer pool size after serializing: " + Holder.POOL.size());
        return json;
    }

    public static <T> T deserialize(String json, Class<T> javaClass) throws JsonProcessingException {
        ObjectMapper mapper = Holder.POOL.pollFirst();
        if (mapper == null) mapper = newMapper();
        T object = mapper.readValue(json, javaClass);
        Holder.POOL.offerLast(mapper);
        System.out.println("Serializer pool size after deserializing: " + Holder.POOL.size());
        return object;
    }

    private static ObjectMapper newMapper(){
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        return mapper;
    }

    private static ConcurrentLinkedDeque<ObjectMapper> initPool(){
        ConcurrentLinkedDeque<ObjectMapper> deque = new ConcurrentLinkedDeque<>();

        IntStream.range(0, Holder.DEFAULT_POOL_SIZE).forEach(i -> {
            deque.offerLast(newMapper());
        });

        return deque;
    }
}
