package com.example.crud.controller;

import com.example.crud.model.Contact;
import com.example.crud.repository.ContactRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

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
        expectedContacts.add(new Contact("John", "Doe", "555-555-1234", "john.doe@example.com", "123 Main St"));
        expectedContacts.add(new Contact("Jane", "Doe", "555-555-5678", "jane.doe@example.com", "456 Main St"));
        when(contactRepository.findAll()).thenReturn(expectedContacts);

        // When
        ResponseEntity<List<Contact>> responseEntity = contactController.getAllContacts(null);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedContacts, responseEntity.getBody());
    }
}