package com.abatef.fastc2.services;

import com.abatef.fastc2.dtos.pharmacy.PharmacyInfo;
import com.abatef.fastc2.dtos.user.UserCreationRequest;
import com.abatef.fastc2.dtos.user.UserInfo;
import com.abatef.fastc2.enums.UserRole;
import com.abatef.fastc2.enums.ValueType;
import com.abatef.fastc2.exceptions.DuplicateValueException;
import com.abatef.fastc2.exceptions.NonExistingValueException;
import com.abatef.fastc2.models.User;
import com.abatef.fastc2.repositories.UserRepository;
import com.google.firebase.auth.FirebaseToken;

import org.modelmapper.ModelMapper;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            ModelMapper modelMapper,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    private void uniqueUser(UserCreationRequest request) throws DuplicateValueException {
        if (userRepository.existsUserByUsername(request.getUsername())) {
            throw new DuplicateValueException(ValueType.USERNAME, request.getUsername());
        }
        if (userRepository.existsUserByEmail(request.getEmail())) {
            throw new DuplicateValueException(ValueType.EMAIL, request.getEmail());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public User registerUser(UserCreationRequest request) throws DuplicateValueException {
        uniqueUser(request);
        User user = modelMapper.map(request, User.class);
        if (request.getUsername() == null || request.getUsername().isEmpty()) {
            user.setUsername(request.getEmail());
        }
        if (request.getManagedUser()) {
            user.setManagedUser(true);
            user.setRole(UserRole.CASHIER);
        } else {
            user.setRole(UserRole.OWNER);
        }
        user.setFbUid(null);
        user.setFbUser(false);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user = userRepository.save(user);
        return user;
    }

    @Transactional
    public User updateUserPassword(User user, String newPassword) throws NonExistingValueException {
        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }

    @Transactional
    public User registerByFirebaseIfNotExist(FirebaseToken fbToken)
            throws NonExistingValueException {
        String uid = fbToken.getUid();
        Optional<User> optionalUser = userRepository.getUserByFbUid(uid);
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        }

        User user = new User();
        user.setFbUser(true);
        user.setFbUid(uid);
        user.setEmail(fbToken.getEmail());
        user.setUsername(fbToken.getEmail());
        user.setPassword(passwordEncoder.encode("password"));
        user.setName(fbToken.getName());
        user.setPhone("NO_PHONE");
        user = userRepository.save(user);
        return user;
    }

    public UserInfo updateUserInfo(@AuthenticationPrincipal User user, UserInfo userInfo) {
        if (userInfo.getName() == null || userInfo.getName().isEmpty()) {
            userInfo.setName(user.getName());
        }
        if (userInfo.getPhone() == null || userInfo.getPhone().isEmpty()) {
            userInfo.setPhone(user.getPhone());
        }
        if (userInfo.getEmail() == null || userInfo.getEmail().isEmpty()) {
            userInfo.setEmail(user.getEmail());
        }
        user = userRepository.save(user);
        return modelMapper.map(userInfo, UserInfo.class);
    }

    public User getUserByUsername(String username) throws NonExistingValueException {
        Optional<User> optionalUser = userRepository.getUserByUsername(username);
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        }
        throw new NonExistingValueException(ValueType.USERNAME, username);
    }

    public User getUserByEmail(String email) throws NonExistingValueException {
        Optional<User> optionalUser = userRepository.getUserByEmail(email);
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        }
        throw new NonExistingValueException(ValueType.EMAIL, email);
    }

    public User getUserById(Integer id) throws NonExistingValueException {
        Optional<User> optionalUser = userRepository.getUserById(id);
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        }
        throw new NonExistingValueException(ValueType.ID, id.toString());
    }

    public UserInfo me(@AuthenticationPrincipal User user) {
        return modelMapper.map(user, UserInfo.class);
    }

    public PharmacyInfo getPharmacyInfoByUser(@AuthenticationPrincipal User user) {
        List<PharmacyInfo> pharmacyInfos =
                user.getPharmacies() != null
                        ? user.getPharmacies().stream()
                                .map(ph -> modelMapper.map(ph, PharmacyInfo.class))
                                .toList()
                        : Collections.emptyList();
        return pharmacyInfos.isEmpty() ? null : pharmacyInfos.getFirst();
    }

    @Transactional
    public void deleteUser(@AuthenticationPrincipal User user) {
        userRepository.delete(user);
    }
}
