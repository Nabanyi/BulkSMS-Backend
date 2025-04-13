package com.sms.demo.service;

import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.sms.demo.requestobjs.SmsRequest;

@Service
public class SmsService {

	private static final String API_KEY = "mTZD06YEjOx0y317qipdttkS0"; 
    private static final String SMS_URL = "https://api.mnotify.com/api/sms/quick?key=" + API_KEY;
    private static final String BALANCE_URL = "https://api.mnotify.com/api/balance/sms?key=" + API_KEY;
    
    private final RestTemplate restTemplate;
    
    public SmsService() {
        this.restTemplate = new RestTemplate();
    }
    
    // Method to send SMS
    public Map<String, Object> sendSms(SmsRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<SmsRequest> entity = new HttpEntity<>(request, headers);
        
        ResponseEntity<Map<String, Object>> response = 
                restTemplate.exchange(SMS_URL, HttpMethod.POST, entity, new ParameterizedTypeReference<>() {});

        return response.getBody();
    }

    // Method to check SMS balance
    public Map<String, Object> checkSmsBalance() {        
        ResponseEntity<Map<String, Object>> response = 
                restTemplate.exchange(BALANCE_URL, HttpMethod.POST, null, new ParameterizedTypeReference<>() {});
        return response.getBody();
    }
}
