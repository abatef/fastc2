package com.abatef.fastc2.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class FirebaseAuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal;
    private final String fbToken;

    public FirebaseAuthenticationToken(String fbToken) {
        super(null);
        this.fbToken = fbToken;
        this.principal = null;
        setAuthenticated(false);
    }

    public FirebaseAuthenticationToken(
            Object principal, String fbToken, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.fbToken = fbToken;
        super.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return fbToken;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }
}
