package com.abatef.fastc2.enums;

public enum FilterOption {
    // Original options
    AVAILABLE,
    SHORTAGE,
    UNAVAILABLE_SHORTAGE,
    UNAVAILABLE,
    EXPIRES_AFTER_N,
    STOCK_OVER_N,
    STOCK_UNDER_N,
    OUT_OF_STOCK,
    EXPIRED,

    // New expiry options
    APPROACHING_EXPIRY,  // Use N as days threshold
    NOT_EXPIRED,

    // Drug category/type options
    BY_FORM,

    // Price options
    PRICE_BELOW_N,
    PRICE_ABOVE_N,
    PRICE_BETWEEN,  // Use N as lower bound and additional param as upper bound
    DISCOUNTED      // Drugs with discount in ReceiptItem
}
