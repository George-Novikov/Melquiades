package com.georgen.melquiades.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.IntStream;

public class Serializer {

    /** Thread-safe wrapper (Bill Pugh Singleton). Do not refactor. */
    private static class ThreadSafe {
        private static final int DEFAULT_POOL_SIZE = 10;
        private static final ConcurrentLinkedDeque<ObjectMapper> POOL = initPool();
    }

    public static String serialize(Object object) throws JsonProcessingException {
        ObjectMapper mapper = ThreadSafe.POOL.pollFirst();
        if (mapper == null) mapper = newMapper();
        String json = mapper.writeValueAsString(object);
        ThreadSafe.POOL.offerLast(mapper);
        return json;
    }

    public static <T> T deserialize(String json, Class<T> javaClass) throws JsonProcessingException {
        ObjectMapper mapper = ThreadSafe.POOL.pollFirst();
        if (mapper == null) mapper = newMapper();
        T object = mapper.readValue(json, javaClass);
        ThreadSafe.POOL.offerLast(mapper);
        return object;
    }

    public static int size(){ return ThreadSafe.POOL.size(); }

    public static void shrink(){
        ThreadSafe.POOL.clear();
        IntStream.range(0, ThreadSafe.DEFAULT_POOL_SIZE).forEach(i -> {
            ThreadSafe.POOL.offerLast(newMapper());
        });
    }

    private static ObjectMapper newMapper(){
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        return mapper;
    }

    private static ConcurrentLinkedDeque<ObjectMapper> initPool(){
        ConcurrentLinkedDeque<ObjectMapper> deque = new ConcurrentLinkedDeque<>();

        IntStream.range(0, ThreadSafe.DEFAULT_POOL_SIZE).forEach(i -> {
            deque.offerLast(newMapper());
        });

        return deque;
    }
}
