package com.abatef.fastc2.dtos.drug;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderCreationRequest {
    private String name;
    private List<OrderItemRequest> items;
}
