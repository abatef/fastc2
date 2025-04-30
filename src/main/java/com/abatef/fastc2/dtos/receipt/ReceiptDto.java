package com.abatef.fastc2.dtos.receipt;

import com.abatef.fastc2.dtos.user.UserDto;
import com.abatef.fastc2.enums.ReceiptStatus;
import com.abatef.fastc2.models.shift.Shift;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptDto {
    private Integer id;
    private UserDto cashier;
    private Shift shift;
    private Instant createdAt;
    private Instant updatedAt;
    List<ReceiptItemDto> items = new ArrayList<>();
    private Float total;
    private ReceiptStatus status;
}
