package com.hibiscusmc.hmcrewards.util;

public interface Service {
    void start();

    default void stop() {
    }
}
