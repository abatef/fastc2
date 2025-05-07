package com.abatef.fastc2.dtos.drug;

import com.abatef.fastc2.dtos.pharmacy.PharmacyDto;
import com.abatef.fastc2.dtos.user.UserDto;
import com.abatef.fastc2.enums.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DrugOrderDto {
    private Integer id;
    private String name;
    private PharmacyDto pharmacy;
    private UserDto orderedBy;
    private OrderStatus status;
    private List<OrderItemDto> orderItems;
    private Float orderTotal;
    private Instant createdAt;
    private Instant updatedAt;
}
