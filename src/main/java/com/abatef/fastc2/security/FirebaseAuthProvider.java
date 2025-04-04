package com.abatef.fastc2.security;

import com.abatef.fastc2.models.User;
import com.abatef.fastc2.services.UserService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class FirebaseAuthProvider implements AuthenticationProvider {

    private final UserService userService;

    public FirebaseAuthProvider(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String token = (String) authentication.getCredentials();
        if (token == null) {
            return null;
        }

        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
            User user = userService.registerByFirebaseIfNotExist(decodedToken);
            return new UsernamePasswordAuthenticationToken(user, token, user.getAuthorities());
        } catch (FirebaseAuthException e) {
            throw new BadCredentialsException("Invalid Firebase token", e);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return FirebaseAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
