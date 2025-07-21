package com.mam.vm.entities;

import lombok.Getter;

@Getter
public enum Coin {
    FIVE(5),
    TEN(10),
    TWENTY(20),
    FIFTY(50),
    HUNDRED(100);
    
    private final int value;
    
    Coin(int value) {
        this.value = value;
    }
    
    public static Coin fromValue(int value) {
        for (Coin coin : values()) {
            if (coin.getValue() == value) {
                return coin;
            }
        }
        throw new IllegalArgumentException("Invalid coin value: " + value + ". Accepted values: 5, 10, 20, 50, 100");
    }
}