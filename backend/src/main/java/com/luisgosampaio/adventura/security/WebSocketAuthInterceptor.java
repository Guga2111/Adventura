package com.luisgosampaio.adventura.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader(SecurityConstants.AUTHORIZATION);

            if (authHeader == null || !authHeader.startsWith(SecurityConstants.BEARER)) {
                throw new MessageDeliveryException("Missing or invalid Authorization header");
            }

            String token = authHeader.replace(SecurityConstants.BEARER, "");

            try {
                DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(SecurityConstants.SECRET_KEY))
                        .build()
                        .verify(token);

                String userPrincipal = decodedJWT.getSubject();
                String role = decodedJWT.getClaim("role").asString();
                Long userId = decodedJWT.getClaim("userId").asLong();

                List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                        new SimpleGrantedAuthority("ROLE_" + role)
                );

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userPrincipal, null, authorities);
                authentication.setDetails(userId);

                accessor.setUser(authentication);
            } catch (Exception e) {
                throw new MessageDeliveryException("Invalid JWT token");
            }
        }

        return message;
    }
}