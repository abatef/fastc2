package com.abatef.fastc2.repositories;

import com.abatef.fastc2.models.pharmacy.OrderItem;
import com.abatef.fastc2.models.pharmacy.OrderItemId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, OrderItemId> {}
