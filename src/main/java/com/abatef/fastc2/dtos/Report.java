package com.abatef.fastc2.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Report {
    private Double revenue;
    private Double profit;
    private Integer numberOfReceipts;
    private Double medianReceipt;
}
