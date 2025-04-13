package com.sms.demo.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sms.demo.dto.ContactGetDTO;
import com.sms.demo.entity.Contact;
import com.sms.demo.exception.ResourceNotFoundException;
import com.sms.demo.repository.ContactRepository;
import com.sms.demo.requestobjs.ContactCreateRequest;
import com.sms.demo.utils.Helper;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContactService {

	@Autowired
    private ContactRepository contactRepository;
	
	@Autowired
	private Helper helper;
	
	public List<ContactGetDTO> findAll() {
		List<Contact> contacts = contactRepository.findByUserId(helper.getUserId());
		List<ContactGetDTO> resultList = contacts.stream().map((contact) -> {
			ContactGetDTO contactGetDTO = new ContactGetDTO();
			BeanUtils.copyProperties(contact, contactGetDTO);
			return contactGetDTO;
		}).collect(Collectors.toList());
		
		return resultList;
    }
	
	public ContactGetDTO findContact(Integer contactId) {
		Contact contact = contactRepository.findSingleContact(helper.getUserId(), contactId);
		ContactGetDTO contactGetDTO = new ContactGetDTO();
		BeanUtils.copyProperties(contact, contactGetDTO);
		return contactGetDTO;
    }
	
	public ContactGetDTO create(ContactCreateRequest dto) {
        Contact contact = new Contact();
        BeanUtils.copyProperties(dto, contact);
        contact.setUserId(helper.getUserId());
        Contact createdContact = contactRepository.save(contact);
        
        ContactGetDTO returnObj = new ContactGetDTO();
        BeanUtils.copyProperties(createdContact, returnObj);
        return returnObj;
    }
	
	public ContactGetDTO update(ContactCreateRequest dto, Integer contactId) {
		Contact contact = contactRepository.findById(contactId).orElseThrow(() -> new ResourceNotFoundException("No Contact found for the Id:"+contactId));
		
        BeanUtils.copyProperties(dto, contact);
        Contact createdContact = contactRepository.save(contact);
        
        ContactGetDTO returnObj = new ContactGetDTO();
        BeanUtils.copyProperties(createdContact, returnObj);
        return returnObj;
    }
	
	public void deleteContact(Integer contactId) {
		Contact contact = contactRepository.findById(contactId).orElseThrow(() -> new ResourceNotFoundException("No Contact found for the Id:"+contactId));
		contactRepository.delete(contact);
    }
}
