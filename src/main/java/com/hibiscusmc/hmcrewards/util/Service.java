package com.hibiscusmc.hmcrewards.util;

public interface Service {
    void load();

    default void unload() {
    }
}
