package com.example.demo.enums;

public enum AccountStatus {
    DISABLED((byte)-1),
    UNVERIFIED((byte)0),
    ACTIVE((byte)1);

    private final byte code;

    AccountStatus(byte code) {
        this.code = code;
    }

    public byte getCode() {
        return code;
    }

    public static AccountStatus fromCode(byte code) {
        for (AccountStatus status : AccountStatus.values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid account status code: " + code);
    }
    
}
