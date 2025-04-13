package com.sms.demo.service;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sms.demo.dto.RefreshTokenDTO;
import com.sms.demo.dto.UserDTO;
import com.sms.demo.entity.CustomUserDetails;
import com.sms.demo.entity.User;
import com.sms.demo.exception.ResourceNotFoundException;
import com.sms.demo.repository.UserRepository;
import com.sms.demo.requestobjs.AuthRequest;
import com.sms.demo.requestobjs.RegisterRequest;
import com.sms.demo.requestobjs.UpdatePassword;
import com.sms.demo.requestobjs.UserUpdateRequest;
import com.sms.demo.utils.JwtUtil;

@Service
public class AuthService {
	@Autowired
    private AuthenticationManager authenticationManager;
	@Autowired
    private UserDetailsService userDetailsService;
	@Autowired
    private JwtUtil jwtUtil;
	@Autowired
    private UserRepository userRepository;
	@Autowired
    private PasswordEncoder passwordEncoder;
	
    //login
    public UserDTO authenticate(AuthRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(request.getUsername());
        String accessToken = jwtUtil.generateAccessToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);
        
        UserDTO userDTO = new UserDTO();
        userDTO.setId(userDetails.getId());
        userDTO.setUsername(userDetails.getUsername());
        userDTO.setRole(userDetails.getRole());
        userDTO.setFirstName(userDetails.getFirstName());
        userDTO.setMiddleName(userDetails.getMiddleName());
        userDTO.setLastName(userDetails.getLastName());
        userDTO.setEmail(userDetails.getEmail());
        userDTO.setPhone(userDetails.getPhone());
        userDTO.setAddress(userDetails.getAddress());
        userDTO.setStatus(userDetails.getStatus());
        userDTO.setToken(accessToken);
        userDTO.setRefreshToken(refreshToken);

        return userDTO;
    }
    
    // Refresh access token using refresh token
    public RefreshTokenDTO refreshAccessToken(RefreshTokenDTO refreshToken) {
        if (!jwtUtil.validateRefreshToken(refreshToken.getRefreshToken())) {
            throw new RuntimeException("Invalid or expired refresh token");
        }

        String username = jwtUtil.extractUsername(refreshToken.getRefreshToken());
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        String newAccessToken = jwtUtil.generateAccessToken(userDetails);
        RefreshTokenDTO refreshTokenDTO2 = new RefreshTokenDTO(newAccessToken);
        return refreshTokenDTO2;
    }
    
    // User Registration
    public void register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already taken!");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // Hash password
        user.setRole(request.getRole());
        user.setAddress(request.getAddress());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setMiddleName(request.getMiddleName());
        user.setPhone(request.getPhone());
        user.setStatus(1);

        userRepository.save(user);
        //String token = jwtUtil.generateToken(user);
        //return new AuthResponse(token);
    }
    
    // User update
    public void updateUser(UserUpdateRequest request) {
    	Integer userIdInteger = getUserId();
    	User user = userRepository.findById(userIdInteger.longValue()).orElseThrow(() -> new ResourceNotFoundException("Failed to load user with Id:"+userIdInteger));

        user.setUsername(request.getUsername());
        user.setAddress(request.getAddress());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setMiddleName(request.getMiddleName());
        user.setPhone(request.getPhone());

        userRepository.save(user);
    }
    
    // User password update
    public void updatePassword(UpdatePassword request) {
    	Integer userIdInteger = getUserId();
        User user = userRepository.findById(userIdInteger.longValue()).orElseThrow(() -> new ResourceNotFoundException("Failed to load user with Id:"+userIdInteger));

        if(!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
        	throw new RuntimeException("Your current password is incorrect");
        }
        
        if(!request.getNewPassword().equals(request.getConfirmPassword())) {
        	throw new RuntimeException("Please confirm your new password");
        }
        
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
    
    private Integer getUserId() {
		CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return userDetails.getId().intValue();
	}
}

