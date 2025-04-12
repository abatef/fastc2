package com.abatef.fastc2.services;

import com.abatef.fastc2.models.shift.Shift;
import com.abatef.fastc2.repositories.ShiftRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShiftService {
    private final ShiftRepository shiftRepository;

    public ShiftService(ShiftRepository shiftRepository) {
        this.shiftRepository = shiftRepository;
    }

    @Transactional
    public Shift create(Shift shift) {
        return shiftRepository.save(shift);
    }

    public Shift getById(Integer id) {
        return shiftRepository.findById(id).get();
    }
}
