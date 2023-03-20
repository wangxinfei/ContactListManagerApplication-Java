package com.example.crud.controller;

import com.example.crud.model.Contact;
import com.example.crud.repository.ContactRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.ArgumentMatchers.any;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class ContactControllerTest {
    @Mock
    ContactRepository contactRepository;

    @InjectMocks
    ContactController contactController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllContacts() {
        // Given
        List<Contact> expectedContacts = new ArrayList<>();
        expectedContacts.add(new Contact("Alan", "Wang", "555-555-1234", "alan.wang@example.com", "123 Main St"));
        expectedContacts.add(new Contact("Betty", "Wang", "555-555-5678", "betty.wang@example.com", "456 Main St"));
        when(contactRepository.findAll()).thenReturn(expectedContacts);

        // When
        ResponseEntity<List<Contact>> responseEntity = contactController.getAllContacts(null);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedContacts, responseEntity.getBody());
    }

    @Test
    void testGetContactById() {
        // Given
        Long contactId = 1L;
        Contact expectedContact = new Contact("Alan", "Wang", "555-555-1234", "alan.wang@example.com", "123 Main St");
        when(contactRepository.findById(contactId)).thenReturn(Optional.of(expectedContact));

        // When
        ResponseEntity<Contact> responseEntity = contactController.getContactById(contactId);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedContact, responseEntity.getBody());
    }


    @Test
    void testGetContactsByFirstName() {
        // Given
        String firstName = "John";
        List<Contact> expectedContacts = new ArrayList<>();
        expectedContacts.add(new Contact("Alan", "Wang", "555-555-1234", "alan.wang@example.com", "123 Main St"));
        when(contactRepository.findByFirstName(firstName)).thenReturn(expectedContacts);

        // When
        ResponseEntity<List<Contact>> responseEntity = contactController.getContactsByFirstName(firstName);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedContacts, responseEntity.getBody());
    }


    @Test
    void testGetContactsByPhoneNumber() {
        // Given
        String phoneNumber = "555-555-1234";
        List<Contact> expectedContacts = new ArrayList<>();
        expectedContacts.add(new Contact("Alan", "Wang", "555-555-1234", "alan.wang@example.com", "123 Main St"));
        when(contactRepository.findByPhoneNumberContaining(phoneNumber)).thenReturn(expectedContacts);

        // When
        ResponseEntity<List<Contact>> responseEntity = contactController.getContactsByPhoneNumber(phoneNumber);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedContacts, responseEntity.getBody());
    }


    @Test
    void testCreateContact() {
        // Given
        Contact contact = new Contact("Alan", "Wang", "555-555-1234", "alan.wang@example.com", "123 Main St");
        when(contactRepository.save(any(Contact.class))).thenReturn(contact);

        // When
        ResponseEntity<Contact> responseEntity = contactController.createContact(contact);

        // Then
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(contact, responseEntity.getBody());
    }


    @Test
    void testUpdateContact() {
        // Given
        Long contactId = 1L;
        Contact existingContact = new Contact("Alan", "Wang", "555-555-1234", "alan.wang@example.com", "123 Main St");
        existingContact.setId(contactId);
        Contact updatedContact = new Contact("Alan", "Wang", "555-555-5555", "alan.wang@example.com", "123 Main St");
        updatedContact.setId(contactId);
        when(contactRepository.findById(contactId)).thenReturn(Optional.of(existingContact));
        when(contactRepository.save(any(Contact.class))).thenReturn(updatedContact);

        // When
        ResponseEntity<Contact> responseEntity = contactController.updateContact(contactId, updatedContact);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(updatedContact, responseEntity.getBody());
    }
}