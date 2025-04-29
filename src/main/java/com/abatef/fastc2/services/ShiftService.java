        package com.abatef.fastc2.services;

        import com.abatef.fastc2.exceptions.ShiftNotFoundException;
        import com.abatef.fastc2.models.shift.Shift;
        import com.abatef.fastc2.repositories.ShiftRepository;

        import org.springframework.stereotype.Service;
        import org.springframework.transaction.annotation.Transactional;

        import java.util.Optional;

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

            public Shift getByIdOrThrow(Integer id) {
                Optional<Shift> optionalShift = shiftRepository.findById(id);
                if (optionalShift.isPresent()) {
                    return optionalShift.get();
                }
                throw new ShiftNotFoundException(id);
            }

            @Transactional
            public void delete(Integer id) {
                shiftRepository.deleteById(id);
            }
        }
