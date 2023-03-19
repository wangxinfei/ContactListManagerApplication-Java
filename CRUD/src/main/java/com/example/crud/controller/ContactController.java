package com.example.crud.controller;

import com.example.crud.model.Contact;
import com.example.crud.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api")
public class ContactController {

    @Autowired
    ContactRepository contactRepository;

    @GetMapping("/contacts/getAll")
    public ResponseEntity<List<Contact>> getAllContacts(@RequestParam(required = false) String phoneNumber) {
        try {
            List<Contact> contacts = new ArrayList<Contact>();

            if (phoneNumber == null)
                contactRepository.findAll().forEach(contacts::add);
            else
                contactRepository.findByPhoneNumberContaining(phoneNumber).forEach(contacts::add);

            if (contacts.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(contacts, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/contacts/getById/{id}")
    public ResponseEntity<Contact> getContactById(@PathVariable("id") long id) {
        Optional<Contact> contactData = contactRepository.findById(id);

        if (contactData.isPresent()) {
            return new ResponseEntity<>(contactData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/contacts/createContact")
    public ResponseEntity<Contact> createContact(@RequestBody Contact contact) {
        try {

            // Check if a contact with the same phone number already exists
            List<Contact> existingContacts = contactRepository.findByPhoneNumberContaining(contact.getPhoneNumber());
            if (!existingContacts.isEmpty()) {
                // A contact with the same phone number already exists, so return an error response
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }

            // No contact with the same phone number exists, so save the new contact
            Contact _contact = contactRepository
                    .save(new Contact(contact.getFirstName(),
                            contact.getLastName(),
                            contact.getPhoneNumber(),
                            contact.getEmail(),
                            contact.getAddress(),
                            true));
            return new ResponseEntity<>(_contact, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/contacts/updateContact/{id}")
    public ResponseEntity<Contact> updateContact(@PathVariable("id") long id, @RequestBody Contact contact) {
        Optional<Contact> contactData = contactRepository.findById(id);

        // Check if a contact with the same phone number already exists
        List<Contact> existingContacts = contactRepository.findByPhoneNumberContaining(contact.getPhoneNumber());
        if (!existingContacts.isEmpty()) {
            // A contact with the same phone number already exists, so return an error response
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        if (contactData.isPresent()) {
            Contact _contact = contactData.get();
            _contact.setFirstName(contact.getFirstName());
            _contact.setLastName(contact.getLastName());
            _contact.setPhoneNumber(contact.getPhoneNumber());
            _contact.setEmail(contact.getEmail());
            _contact.setAddress(contact.getAddress());
            _contact.setExist(true);
            return new ResponseEntity<>(contactRepository.save(_contact), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/contacts/deleteContact/{id}")
    public ResponseEntity<HttpStatus> deleteContact(@PathVariable("id") long id) {
        try {
            contactRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/contacts/deleteAll")
    public ResponseEntity<HttpStatus> deleteAllContacts() {
        try {
            contactRepository.deleteAll();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping("/contacts/exist")
    public ResponseEntity<List<Contact>> findByExist() {
        try {
            List<Contact> contacts = contactRepository.findByExist(true);

            if (contacts.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(contacts, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}