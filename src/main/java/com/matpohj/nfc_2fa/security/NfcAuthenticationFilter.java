package com.matpohj.nfc_2fa.security;

import com.matpohj.nfc_2fa.model.User;
import com.matpohj.nfc_2fa.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class NfcAuthenticationFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;
    private List<RequestMatcher> staticResourceMatchers = new ArrayList<>();

    public NfcAuthenticationFilter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void setStaticResourceMatchers(RequestMatcher... matchers) {
        this.staticResourceMatchers = Arrays.asList(matchers);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Skip filter for static resources
        return staticResourceMatchers.stream().anyMatch(matcher -> matcher.matches(request));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        if (path.startsWith("/login") || path.equals("/api/nfc/validate") ||
                path.startsWith("/api/test") || path.startsWith("/h2-console") ||
                path.startsWith("/verify-nfc") || path.equals("/nfc-verification") ||
                path.startsWith("/api/verification/status")) {
            filterChain.doFilter(request, response);
            return;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            boolean isAdmin = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch(authority -> authority.equals("ROLE_ADMIN"));

            if (isAdmin) {
                HttpSession session = request.getSession();
                Boolean nfcVerified = (Boolean) session.getAttribute("nfcVerified");

                if (nfcVerified == null || !nfcVerified) {
                    response.sendRedirect("/nfc-verification");
                    return;
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}