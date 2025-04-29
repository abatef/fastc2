package com.abatef.fastc2.services;

import com.abatef.fastc2.repositories.DrugOrderRepository;
import org.springframework.stereotype.Service;

@Service
public class InventoryService {
    private final DrugOrderRepository drugOrderRepository;

    public InventoryService(DrugOrderRepository drugOrderRepository) {
        this.drugOrderRepository = drugOrderRepository;
    }


}
