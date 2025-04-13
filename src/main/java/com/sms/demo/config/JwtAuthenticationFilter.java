package com.sms.demo.config;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.sms.demo.utils.ApiResponse;
import com.sms.demo.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	@Autowired
    private JwtUtil jwtUtil;
	
	@Autowired
    private UserDetailsService userDetailsService;
	
	private static final AntPathMatcher pathMatcher = new AntPathMatcher();
	
	private static final List<String> OPEN_API_ENDPOINTS = List.of(
		    "/v3/api-docs",
		    "/v3/api-docs/swagger-config",
		    "/swagger-ui",
		    "/swagger-ui.html",
		    "/swagger-ui/**",
		    "/auth/login",
		    "/auth/register",
		    "/auth/refresh",
		    "/uploads/**"
		);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
    	try {
    		 String requestURI = request.getRequestURI();

		    // Bypass JWT authentication for Swagger and public API end points
		    if (OPEN_API_ENDPOINTS.stream().anyMatch(endpoint -> pathMatcher.match(endpoint, requestURI))) {
		        chain.doFilter(request, response);
		        return;
		    }
    		    
	    	String authHeader = request.getHeader("Authorization");
	
	        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
	        	handleJwtException(response, "Unauthorized: Include JWT Token in Authorization header");
	            return;
	        }
	
	        String token = authHeader.substring(7);
	        String username = jwtUtil.extractUsername(token);
	
	        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
	            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
	
	            if (jwtUtil.validateAccessToken(token, userDetails)) {
	                UsernamePasswordAuthenticationToken authentication =
	                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
	                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
	
	                SecurityContextHolder.getContext().setAuthentication(authentication);
	            }
	        }
	
	        chain.doFilter(request, response);
    	} catch (ExpiredJwtException ex) {
            handleJwtException(response, "Token expired! Please log in again.");
        } catch (MalformedJwtException | SignatureException ex) {
            handleJwtException(response, "Invalid token!");
        } catch (Exception ex) {
            handleJwtException(response, "Authentication error: " + ex.getMessage());
        }
    }
    
    private void handleJwtException(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        ObjectMapper objectMapper = new ObjectMapper();

        ApiResponse<?> errorResponse = new ApiResponse<>(false, message, null);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
