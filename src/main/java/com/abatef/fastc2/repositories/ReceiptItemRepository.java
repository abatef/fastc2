package com.abatef.fastc2.repositories;

import com.abatef.fastc2.models.pharmacy.ReceiptItem;
import com.abatef.fastc2.models.pharmacy.ReceiptItemId;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReceiptItemRepository extends JpaRepository<ReceiptItem, ReceiptItemId> {
    List<ReceiptItem> findReceiptItemsByReceipt_Id(Integer receiptId);
}
