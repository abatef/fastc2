package com.abatef.fastc2.services;

import com.abatef.fastc2.models.pharmacy.OrderItem;
import com.abatef.fastc2.models.pharmacy.OrderItemId;
import org.springframework.data.jpa.repository.JpaRepository;

interface OrderItemRepository extends JpaRepository<OrderItem, OrderItemId> {}
