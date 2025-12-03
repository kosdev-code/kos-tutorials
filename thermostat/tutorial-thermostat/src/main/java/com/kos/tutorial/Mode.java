/**
 * (C) Copyright 2025 Kondra. All rights reserved.
 */
package com.kos.tutorial;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Mode {
    OFF("#808080"),
    HEAT("#FF0000"),
    COOL("#0000FF");

    private final String color;
}
