package com.gustavolyra.e_commerce_api.config.security;

import com.gustavolyra.e_commerce_api.repositories.UserRepository;
import com.gustavolyra.e_commerce_api.services.TokenGeneratorService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Configuration
public class SecurityFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;
    private final TokenGeneratorService tokenGeneratorService;

    public SecurityFilter(UserRepository userRepository, TokenGeneratorService tokenGeneratorService) {
        this.userRepository = userRepository;
        this.tokenGeneratorService = tokenGeneratorService;
    }


    @Transactional(readOnly = true)
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var token = getTokenFromRequest(request);
        if (token != null && tokenGeneratorService.validateToken(token) != null) {
            var user = userRepository.findByEmail(token).orElseThrow(() -> new UsernameNotFoundException("User not Found"));
            var authorities = user.getAuthorities();
            var authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }


    private String getTokenFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            return null;
        }
        return token.replace("Bearer ", "");
    }


}
