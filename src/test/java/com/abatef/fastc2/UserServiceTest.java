package com.abatef.fastc2;

import com.abatef.fastc2.dtos.pharmacy.PharmacyInfo;
import com.abatef.fastc2.dtos.user.UserCreationRequest;
import com.abatef.fastc2.enums.UserRole;
import com.abatef.fastc2.exceptions.DuplicateValueException;
import com.abatef.fastc2.models.User;
import com.abatef.fastc2.models.pharmacy.Pharmacy;
import com.abatef.fastc2.repositories.PharmacyRepository;
import com.abatef.fastc2.repositories.UserRepository;
import com.abatef.fastc2.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private PharmacyRepository pharmacyRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldRegisterUserSuccessfully() {
        UserCreationRequest userCreationRequest = new UserCreationRequest();
        userCreationRequest.setUsername("username");
        userCreationRequest.setPassword("password");
        userCreationRequest.setEmail("mail@mail.com");
        userCreationRequest.setManagedUser(false);
        when(userRepository.existsUserByUsername("username")).thenReturn(false);
        when(userRepository.existsUserByEmail("mail@mail.com")).thenReturn(false);

        User mappedUser = new User();
        mappedUser.setUsername("username");
        mappedUser.setPassword("password");
        mappedUser.setEmail("mail@mail.com");
        mappedUser.setManagedUser(false);
        when(modelMapper.map(userCreationRequest, User.class)).thenReturn(mappedUser);
        when(passwordEncoder.encode("password")).thenReturn("encoded_password");

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User savedUser = userService.registerUser(userCreationRequest);
        assertFalse(savedUser.getManagedUser());
        assertEquals(UserRole.OWNER, savedUser.getRole());
        assertEquals("username", savedUser.getUsername());
        assertEquals("encoded_password", savedUser.getPassword());
    }

    @Test
    void shouldNotRegisterUserWhenUsernameAlreadyExists() {
        UserCreationRequest userCreationRequest = new UserCreationRequest();
        userCreationRequest.setUsername("username");
        when(userRepository.existsUserByUsername("username")).thenReturn(true);

        assertThrows(DuplicateValueException.class, () -> userService.registerUser(userCreationRequest));
    }

    @Test
    void shouldReturnPharmaciesSuccessfully() {
        User user = new User();
        user.setId(1);
        when(pharmacyRepository.getPharmaciesByOwner_Id(user.getId())).thenReturn(List.of(new Pharmacy()));
        when(modelMapper.map(any(Pharmacy.class), eq(PharmacyInfo.class))).thenReturn(new PharmacyInfo());
        PharmacyInfo pharmacyInfos = userService.getPharmacyInfoByUser(user);
        assertNotNull(pharmacyInfos);
    }

    @Test
    void shouldDeleteUserSuccessfully() {
        User user = new User();
        userService.deleteUser(user);
        verify(userRepository).delete(user);
    }

}
