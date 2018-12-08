package com.android.bugreport.core;

public enum BugType {

    RUNTIME(1), ANR(2), NATIVE(3);

    private int mValue;

    BugType(int value) {
        this.mValue = value;
    }

    public int getValue() {
        return mValue;
    }
}
