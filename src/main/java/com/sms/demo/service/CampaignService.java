package com.sms.demo.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sms.demo.repository.CampaignRepository;
import com.sms.demo.repository.ContactRepository;
import com.sms.demo.requestobjs.CampaignCreateRequest;
import com.sms.demo.utils.Helper;
import com.sms.demo.dto.CampaignGetDto;
import com.sms.demo.entity.Campaign;
import com.sms.demo.entity.Contact;

@Service
public class CampaignService {
	
	@Autowired
    private CampaignRepository repository;
	@Autowired
	private ContactRepository contactRepository;
	@Autowired
	private Helper helper;

    public List<CampaignGetDto> findAll() {
    	List<Contact> contacts = contactRepository.findByUserId(helper.getUserId());
    	Map<Integer, String> contactsMap = contacts.stream().collect(Collectors.toMap(Contact::getId, Contact::getName));
    	
        List<Campaign> campaigns = repository.findByUserId(helper.getUserId());
        List<CampaignGetDto> resuList = campaigns.stream().map((campaign) -> {
        	CampaignGetDto dto = new CampaignGetDto();
        	BeanUtils.copyProperties(campaign, dto);
        	dto.setContactName(contactsMap.get(campaign.getContact()));
        	return dto;
        }).collect(Collectors.toList());
        
        return resuList;
    }
    
    public CampaignGetDto findCampaign(Integer campaignId) {
        Campaign campaign = repository.findByIdAndUserId(helper.getUserId(), campaignId);
        CampaignGetDto dto = new CampaignGetDto();
    	BeanUtils.copyProperties(campaign, dto);
        return dto;
    }

    public CampaignGetDto create(CampaignCreateRequest request) {
        Campaign campaign = new Campaign();
        campaign.setUserId(helper.getUserId());
        BeanUtils.copyProperties(request, campaign);
        Campaign createdCampaign = repository.save(campaign);
        
        CampaignGetDto dto = new CampaignGetDto();
        BeanUtils.copyProperties(createdCampaign, dto);
        return dto;
    }
    
    public CampaignGetDto update(CampaignCreateRequest request, Integer campaignId) {
    	Campaign campaign = repository.findByIdAndUserId(helper.getUserId(), campaignId);    	
        BeanUtils.copyProperties(request, campaign);
        repository.save(campaign);
        
        CampaignGetDto dto = new CampaignGetDto();
        BeanUtils.copyProperties(campaign, dto);
        return dto;
    }
    
    public void updateStatus(Integer campId, String status) {
    	Campaign campaign = repository.findCampaign(campId);
    	campaign.setStatus(status);
    	repository.save(campaign);
    }
    
    public void deleteCampaign(Integer campaignId) {
        Campaign campaign = repository.findByIdAndUserId(helper.getUserId(), campaignId);
        repository.delete(campaign);
    }
}
