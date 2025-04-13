package com.sms.demo.config;

import java.io.IOException;

import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class RequestLoggingFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, jakarta.servlet.ServletException { // Updated exception
        HttpServletRequest req = (HttpServletRequest) request;
        System.out.println("Request URL: " + req.getRequestURL());
        System.out.println("Content-Type: " + req.getContentType());
        chain.doFilter(request, response);
    }
}
