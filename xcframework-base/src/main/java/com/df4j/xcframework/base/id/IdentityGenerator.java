package com.df4j.xcframework.base.id;

public interface IdentityGenerator<T> {

    T generate(String keyGroup, String keyName);

    default boolean validate(T value){
        return true;
    }
}
