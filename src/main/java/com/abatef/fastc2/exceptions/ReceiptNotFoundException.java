package com.abatef.fastc2.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class ReceiptNotFoundException extends RuntimeException {
    private final int receiptId;
    public ReceiptNotFoundException(int receiptId) {
        this.receiptId = receiptId;
    }
}
