package com.abatef.fastc2.dtos.receipt;

import com.abatef.fastc2.dtos.user.UserInfo;
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
public class ReceiptInfo {
    List<ReceiptItemInfo> items = new ArrayList<>();
    private Integer id;
    private UserInfo cashier;
    private Shift shift;
    private Instant createdAt;
    private Instant updatedAt;
}
