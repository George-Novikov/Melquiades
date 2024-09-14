package com.georgen.melquiades.util;

import com.georgen.melquiades.api.Operation;

import java.lang.reflect.Method;

public class OperationExtractor {
    public static Operation extract(StackTraceElement stackTraceElement){
        try {
            String className = stackTraceElement.getClassName();
            Class javaClass = Class.forName(className);

            String methodName = stackTraceElement.getMethodName();
            Method method = javaClass.getDeclaredMethod(methodName);

            boolean isAccessible = method.isAccessible();
            method.setAccessible(true);

            Operation operation = method.getAnnotation(Operation.class);
            method.setAccessible(isAccessible);

            return operation;
        } catch (Exception e){
            return null;
        }
    }
}
