package com.abatef.fastc2.exceptions;

import lombok.Getter;

@Getter
public class ReceiptNotFoundException extends RuntimeException {
    private final int receiptId;

    public ReceiptNotFoundException(int receiptId) {
        this.receiptId = receiptId;
    }
}
