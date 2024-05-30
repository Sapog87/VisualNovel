package com.sapog87.visual_novel.app.security;

import com.sapog87.visual_novel.app.entity.User;
import com.sapog87.visual_novel.app.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final CustomUserDetailsService userDetailsService;
    private final SecurityContextRepository securityContextRepository;

    public TokenAuthenticationFilter(TokenService tokenService, CustomUserDetailsService userDetailsService, SecurityContextRepository securityContextRepository) {
        this.tokenService = tokenService;
        this.userDetailsService = userDetailsService;
        this.securityContextRepository = securityContextRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        String token = request.getParameter("token");
        if (token != null && tokenService.validateToken(token)) {
            User user = tokenService.getUserFromToken(token);

            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getExternalUserId().toString());
            if (userDetails != null) {
                TokenAuthentication authentication = new TokenAuthentication(token, userDetails, true);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                securityContextRepository.saveContext(SecurityContextHolder.getContext(), request, response);
            }
        }

        filterChain.doFilter(request, response);
    }
}
