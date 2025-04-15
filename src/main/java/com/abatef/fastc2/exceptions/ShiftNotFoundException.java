package com.abatef.fastc2.exceptions;

import com.abatef.fastc2.models.shift.Shift;
import lombok.Getter;

@Getter
public class ShiftNotFoundException extends RuntimeException {
    private final Integer shiftId;
    public ShiftNotFoundException(Integer shiftId) {
      this.shiftId = shiftId;
    }
}
