package com.abatef.fastc2.services;

import org.springframework.stereotype.Service;

@Service
public class InventoryService {
    /*
     * filter -> available, shortage, unavailable shortage, unavailable
     *
     * available -> at least one drug exists in the pharmacy
     * shortage  -> if we have 10, and we require n
     * unavailable shortage -> if we have 0 and require n
     * unavailable -> if we have 0 and require 0
     * */

}
