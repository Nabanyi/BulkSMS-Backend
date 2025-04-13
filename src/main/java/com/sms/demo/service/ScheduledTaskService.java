package com.sms.demo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sms.demo.entity.Campaign;
import com.sms.demo.entity.Contact;
import com.sms.demo.exception.ResourceNotFoundException;
import com.sms.demo.repository.CampaignRepository;
import com.sms.demo.repository.ContactRepository;
import com.sms.demo.requestobjs.SmsRequest;


@Service
public class ScheduledTaskService {
	
	private final CampaignRepository campaignRepository;
    private final ContactRepository contactRepository;
    private final ObjectMapper objectMapper;
    private final SmsService smsService;
    
    @Autowired
    private CampaignService campaignService;
    
	public ScheduledTaskService(CampaignRepository campaignRepository, ContactRepository contactRepository, SmsService smsService) {
		this.campaignRepository = campaignRepository;
        this.contactRepository = contactRepository;
        this.objectMapper = new ObjectMapper();
        this.smsService = smsService;
	}

	@Scheduled(fixedRate = 60000) // Runs every 1 minute
    public void runEveryMinute() {
        System.out.println("Running scheduled task: " + System.currentTimeMillis());

        List<Campaign> savedCampaigns = campaignRepository.findByStatus("Pending");
        if (savedCampaigns.isEmpty()) {
            System.out.println("No scheduled campaigns for today");
            return;
        }

        for (Campaign campaign : savedCampaigns) {
        	campaignService.updateStatus(campaign.getId(), "In-Progress");
        	
            Contact contact = contactRepository.findById(campaign.getContact())
                    .orElseThrow(() -> new ResourceNotFoundException("No contacts found for the campaign selected"));

            try {
            	List<String> header = objectMapper.readValue(contact.getHeader(), new TypeReference<>() {});
            	int phoneIndex = header.indexOf(campaign.getPhoneColumn());
                List<List<String>> data = objectMapper.readValue(contact.getContent(), new TypeReference<>() {});
                for (List<String> row : data) {
                	String phone = row.get(phoneIndex);
                	String formattedMessage = replacePlaceholders(campaign.getMessage(), header, row);
                	//sendSMS(phone, formattedMessage, campaign.getScheduledDate());
				}

            } catch (JsonMappingException e) {
                System.err.println("Error mapping JSON: " + e.getMessage());
            } catch (JsonProcessingException e) {
                System.err.println("Error processing JSON: " + e.getMessage());
            }
            
            campaignService.updateStatus(campaign.getId(), "Completed");
        }
    }
	
	public static String replacePlaceholders(String message, List<String> header, List<String> contact) {
        for (int i = 0; i < header.size(); i++) {
            String placeholder = "\\{" + header.get(i).toUpperCase() + "\\}";
            message = message.replaceAll(placeholder, contact.get(i));
        }
        return message;
    }
	
	private void sendSMS(String phone, String message, LocalDateTime scheduledDateTime) {
		 boolean isScheduled = scheduledDateTime != null;
		 
		SmsRequest smsRequest = new SmsRequest(
		        List.of(phone),
		        "DCAGhana",
		        message,
		        isScheduled,
		        isScheduled ? scheduledDateTime.toString() : ""
		    );
		
		Map<String, Object> responseMap = smsService.sendSms(smsRequest);
		System.out.println("Response: " + responseMap);
	}
}
