package com.hibiscusmc.hmcrewards.data.serialize;

import org.jetbrains.annotations.NotNull;

public interface DnReader {
    boolean hasMoreValuesOrEntries();

    @NotNull DnType readType();

    void readObjectStart();

    @NotNull String readName();

    void readObjectEnd();

    void readArrayStart();

    void readArrayEnd();

    @NotNull String readStringValue();

    int readIntValue();

    boolean readBooleanValue();

    double readDoubleValue();

    float readFloatValue();

    long readLongValue();

    void skipValue();
}
