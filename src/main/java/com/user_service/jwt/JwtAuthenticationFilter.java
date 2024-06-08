package com.user_service.jwt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtHelper jwtHelper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
    	System.out.println("do internal filter gets invoked..");
        String requestHeader = request.getHeader("Authorization");
        logger.info("Header: {}", requestHeader);

        String userName = null;
        String token = null;

        if (requestHeader != null && requestHeader.toLowerCase().startsWith("bearer ")) {
            token = requestHeader.substring(7);
            System.out.println("token-"+token);
            try {
                userName = this.jwtHelper.getUsernameFromToken(token);
            } catch (IllegalArgumentException e) {
                logger.error("Illegal Argument while fetching the username: {}", e.getMessage());
            } catch (ExpiredJwtException e) {
                logger.error("Given JWT token is expired: {}", e.getMessage());
            } catch (MalformedJwtException e) {
                logger.error("Some changes have been made in the token. Invalid Token: {}", e.getMessage());
            } catch (Exception e) {
                logger.error("An error occurred while processing the token: {}", e.getMessage());
            }
        } else {
            logger.info("Invalid header value");
        }

        if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            boolean validateToken = this.jwtHelper.validateToken(token);
            if (validateToken) {
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userName, null, Collections.emptyList());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                logger.info("Token validation failed");
            }
        }

        filterChain.doFilter(request, response);
    }
}
