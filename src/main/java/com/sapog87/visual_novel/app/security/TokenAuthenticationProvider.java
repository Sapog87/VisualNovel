package com.sapog87.visual_novel.app.security;

import com.sapog87.visual_novel.app.entity.User;
import com.sapog87.visual_novel.app.service.TokenService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class TokenAuthenticationProvider implements AuthenticationProvider {

    private final TokenService tokenService;
    private final CustomUserDetailsService userDetailsService;

    public TokenAuthenticationProvider(TokenService tokenService, CustomUserDetailsService userDetailsService) {
        this.tokenService = tokenService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String token = (String) authentication.getCredentials();
        if (tokenService.validateToken(token)) {
            User user = tokenService.getUserFromToken(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getExternalUserId().toString());
            return new TokenAuthentication(token, userDetails, true);
        }
        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return TokenAuthentication.class.isAssignableFrom(authentication);
    }
}
