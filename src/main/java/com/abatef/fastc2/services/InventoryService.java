package com.abatef.fastc2.services;

import com.abatef.fastc2.repositories.OrderStatsRepository;
import org.springframework.stereotype.Service;

@Service
public class InventoryService {
    private final OrderStatsRepository orderStatsRepository;

    public InventoryService(OrderStatsRepository orderStatsRepository) {
        this.orderStatsRepository = orderStatsRepository;
    }


}
