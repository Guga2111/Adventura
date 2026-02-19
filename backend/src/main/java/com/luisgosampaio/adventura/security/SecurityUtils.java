package com.luisgosampaio.adventura.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.security.Principal;

public class SecurityUtils {

    private SecurityUtils() {}

    public static Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (Long) authentication.getDetails();
    }

    public static Long getUserIdFromPrincipal(Principal principal) {
        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) principal;
        return (Long) auth.getDetails();
    }

    public static void verifyAuthenticatedUser(Long requestedUserId) {
        Long authenticatedUserId = getAuthenticatedUserId();
        if (!authenticatedUserId.equals(requestedUserId)) {
            throw new AccessDeniedException("Authenticated user does not match the requested user");
        }
    }
}