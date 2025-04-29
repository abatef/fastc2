package com.abatef.fastc2.enums;

public enum SortOption {
    // Expiry date sorting
    EXPIRY_DATE_ASC,    // Earliest expiry first (nearest to expire)
    EXPIRY_DATE_DESC,   // Latest expiry first (furthest from expiring)

    // Price sorting
    PRICE_ASC,          // Lowest price first
    PRICE_DESC,         // Highest price first

    // Stock sorting
    STOCK_ASC,          // Lowest stock first
    STOCK_DESC          // Highest stock first
}