package com.sms.demo.utils;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;

import com.sms.demo.entity.CustomUserDetails;

@Configuration
public class Helper {

	public Helper() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Integer getUserId() {
		CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return userDetails.getId().intValue();
	}
}
