package com.abatef.fastc2.dtos.drug;

import com.abatef.fastc2.dtos.pharmacy.PharmacyDto;
import com.abatef.fastc2.dtos.user.UserDto;
import com.abatef.fastc2.enums.OrderStatus;

import java.time.Instant;

public class DrugOrderDto {
    private Integer id;
    private DrugDto drug;
    private PharmacyDto pharmacy;
    private UserDto orderedBy;
    private Integer required;
    private OrderStatus status;
    private Instant createdAt;
    private Instant updatedAt;
}
