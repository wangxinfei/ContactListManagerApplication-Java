package com.example.crud.controller;

import com.example.crud.model.Contact;
import com.example.crud.repository.ContactRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import static org.mockito.ArgumentMatchers.any;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

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
    void testGetAllContactsWhenNonExistingPhoneNumber() {
        // Given
        String nonExistingPhoneNumber = "123-456-7890";

        // When
        ResponseEntity<List<Contact>> responseEntity = contactController.getAllContacts(Optional.of(nonExistingPhoneNumber));

        // Then
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }


    @Test
    void testGetAllContactsWhenExistingPhoneNumber() {
        // Given
        String existingPhoneNumber = "555-555-1234";
        List<Contact> expectedContacts = new ArrayList<>();
        expectedContacts.add(new Contact("Alan", "Wang", "555-555-1234", "alan.wang@example.com", "123 Main St"));
        when(contactRepository.findByPhoneNumberContaining(existingPhoneNumber)).thenReturn(expectedContacts);

        // When
        ResponseEntity<List<Contact>> responseEntity = contactController.getAllContacts(Optional.of(existingPhoneNumber));

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedContacts, responseEntity.getBody());
    }

    @Test
    void testGetAllContactsWhenNoContacts() {
        // Given
        when(contactRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        ResponseEntity<List<Contact>> responseEntity = contactController.getAllContacts(Optional.empty());

        // Then
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }



    @Test
    void testGetAllContactsWhenExistingContacts() {
        // Given
        List<Contact> expectedContacts = new ArrayList<>();
        expectedContacts.add(new Contact("John", "Doe", "555-1234", "john.doe@example.com", "123 Main St"));
        expectedContacts.add(new Contact("Jane", "Doe", "555-5678", "jane.doe@example.com", "456 Main St"));
        when(contactRepository.findAll()).thenReturn(expectedContacts);

        // When
        ResponseEntity<List<Contact>> responseEntity = contactController.getAllContacts(Optional.empty());

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedContacts, responseEntity.getBody());
    }


    @Test
    void testGetAllContactsWhenLargeAmountContacts() {
        // Given
        List<Contact> expectedContacts = new ArrayList<>();
        for (int i = 1; i <= 10000; i++) {
            expectedContacts.add(new Contact("First" + i, "Last" + i, "555-555-" + i, "first.last" + i + "@example.com", i + " Main St"));
        }
        when(contactRepository.findAll()).thenReturn(expectedContacts);

        // When
        ResponseEntity<List<Contact>> responseEntity = contactController.getAllContacts(Optional.empty());

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedContacts, responseEntity.getBody());
    }


    @Test
    void testGetAllContactsWhenExceptionThrown() {
        // Given
        when(contactRepository.findByPhoneNumberContaining(anyString())).thenThrow(new RuntimeException());

        // When
        ResponseEntity<List<Contact>> responseEntity = contactController.getAllContacts(Optional.of("555-555"));

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }


    @Test
    void testGetContactByIdWhenContactExists() {
        // Given
        long id = 1L;
        Contact contact = new Contact("John", "Doe", "555-555-1234", "john.doe@example.com", "123 Main St");
        when(contactRepository.findById(id)).thenReturn(Optional.of(contact));

        // When
        ResponseEntity<Contact> responseEntity = contactController.getContactById(id);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(contact, responseEntity.getBody());
    }


    @Test
    void testGetContactByIdWhenContactDoesNotExist() {
        // Given
        long nonExistentId = 999L;
        when(contactRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When
        ResponseEntity<Contact> responseEntity = contactController.getContactById(nonExistentId);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }


    @Test
    void testGetContactByIdWithInvalidId() {
        // Given
        long invalidId = -1;

        // When
        ResponseEntity<Contact> responseEntity = contactController.getContactById(invalidId);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }


    @Test
    void testGetContactsByFirstNameWhenContactsExist() {
        // Given
        String firstName = "Alan";
        List<Contact> expectedContacts = new ArrayList<>();
        expectedContacts.add(new Contact(firstName, "Wang", "555-555-1234", "alan.wang@example.com", "123 Main St"));
        when(contactRepository.findByFirstName(firstName)).thenReturn(expectedContacts);

        // When
        ResponseEntity<List<Contact>> responseEntity = contactController.getContactsByFirstName(firstName);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedContacts, responseEntity.getBody());
    }


    @Test
    public void testGetContactsByFirstNameWhenNoContactsExist() {
        // Given
        String firstName = "NonExistent";

        // When
        ResponseEntity<List<Contact>> response = contactController.getContactsByFirstName(firstName);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }


    @Test
    public void testGetContactsByFirstNameWhenMultipleSameContactsExist() {
        String firstName = "Alan";
        Contact contact1 = new Contact(firstName, "Wang", "555-555-1234", "alan.wang@example.com", "123 Main St");
        Contact contact2 = new Contact(firstName, "Wang", "555-555-1235", "alanwang@example.com", "123 Main St");
        List<Contact> contacts = Arrays.asList(contact1, contact2);

        when(contactRepository.findByFirstName(firstName)).thenReturn(contacts);

        ResponseEntity<List<Contact>> responseEntity = contactController.getContactsByFirstName(firstName);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(contacts, responseEntity.getBody());
    }


    @Test
    void testGetContactsByFirstNameWithInvalidFirstName() {
        // Arrange
        String invalidName = "##invalid##";

        // Act
        ResponseEntity<List<Contact>> response = contactController.getContactsByFirstName(invalidName);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }


    @Test
    void testGetContactsByFirstNameWithNullFirstName() {
        ResponseEntity<List<Contact>> response = contactController.getContactsByFirstName(null);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }


    @Test
    void testCreateContactWhenContactDoesNotExist() {
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
    void testCreateContactWhenContactAlreadyExists() {
        // Given
        Contact existingContact = new Contact("Alan", "Wang", "555-555-1234", "alan.wang@example.com", "123 Main St");
        List<Contact> existingContacts = Collections.singletonList(existingContact);
        when(contactRepository.findByPhoneNumberContaining(anyString())).thenReturn(existingContacts);
        Contact newContact = new Contact("Alan", "Wang", "555-555-1234", "alan.wang@example.com", "123 Main St");

        // When
        ResponseEntity<Contact> responseEntity = contactController.createContact(newContact);

        // Then
        assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }


    @Test
    void testCreateContactWithInvalidData() {
        // Given
        Contact contact = new Contact("", "Wang", "555-555-1234", "alan.wang@example.com", "123 Main St");

        // When
        ResponseEntity<Contact> responseEntity = contactController.createContact(contact);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }


    @Test
    void testCreateContactWithNullData() {
        // Given
        Contact contact = new Contact("", "", "", "", "");

        // When
        ResponseEntity<Contact> responseEntity = contactController.createContact(contact);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }


    @Test
    void testUpdateContactWithValidData() {
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


    @Test
    void testUpdateContactWithInvalidId() {
        // Given
        Long contactId = 1L;
        Contact updatedContact = new Contact("Alan", "Wang", "555-555-5555", "alan.wang@example.com", "123 Main St");
        updatedContact.setId(contactId);
        when(contactRepository.findById(contactId)).thenReturn(Optional.empty());

        // When
        ResponseEntity<Contact> responseEntity = contactController.updateContact(contactId, updatedContact);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    void testUpdateContactWithNullData() {
        // Given
        Long contactId = 1L;
        Contact existingContact = new Contact("Alan", "Wang", "555-555-1234", "alan.wang@example.com", "123 Main St");
        existingContact.setId(contactId);
        Contact updatedContact = new Contact(null, null, null, null, null);
        updatedContact.setId(contactId);
        when(contactRepository.findById(contactId)).thenReturn(Optional.of(existingContact));
        when(contactRepository.save(any(Contact.class))).thenReturn(updatedContact);

        // When
        ResponseEntity<Contact> responseEntity = contactController.updateContact(contactId, updatedContact);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }


    @Test
    void testUpdateContactWithDuplicatePhoneNumber() {
        // Given
        Long contactId = 1L;
        Contact existingContact = new Contact("Alan", "Wang", "555-555-1234", "alan.wang@example.com", "123 Main St");
        existingContact.setId(contactId);
        Contact updatedContact = new Contact("Alan", "Wang", "555-555-5555", "alan.wang@example.com", "123 Main St");
        updatedContact.setId(contactId);
        Contact conflictingContact = new Contact("Bob", "Smith", "555-555-5555", "bob.smith@example.com", "456 Oak St");
        when(contactRepository.findById(contactId)).thenReturn(Optional.of(existingContact));
        when(contactRepository.save(any(Contact.class))).thenAnswer(invocation -> {
            Contact contact = invocation.getArgument(0);
            if (conflictingContact.getPhoneNumber().equals(contact.getPhoneNumber())) {
                throw new DataIntegrityViolationException("Phone number must be unique");
            }
            return contact;
        });

        // When
        ResponseEntity<Contact> responseEntity = contactController.updateContact(contactId, updatedContact);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }


    @Test
    void testUpdateContactWithInvalidData() {
        // Given
        Long contactId = 1L;
        Contact existingContact = new Contact("Alan", "Wang", "555-555-1234", "alan.wang@example.com", "123 Main St");
        existingContact.setId(contactId);
        Contact updatedContact = new Contact("Alan", "Wang", "555-555-1234", "", "");
        updatedContact.setId(contactId);
        when(contactRepository.findById(contactId)).thenReturn(Optional.of(existingContact));
        when(contactRepository.save(any(Contact.class))).thenThrow(DataIntegrityViolationException.class);

        // When
        ResponseEntity<Contact> responseEntity = contactController.updateContact(contactId, updatedContact);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }


    @Test
    void testDeleteContactSuccess() {
        // Given
        Long contactId = 1L;
        doNothing().when(contactRepository).deleteById(contactId);

        // When
        ResponseEntity<HttpStatus> responseEntity = contactController.deleteContact(contactId);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
    }


    @Test
    void testDeleteNonExistentContact() {
        // Given
        Long contactId = 1L;
        doThrow(EmptyResultDataAccessException.class).when(contactRepository).deleteById(contactId);

        // When
        ResponseEntity<HttpStatus> responseEntity = contactController.deleteContact(contactId);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }


    @Test
    void testDeleteContactUnexpectedError() {
        // Given
        Long contactId = 1L;
        doThrow(RuntimeException.class).when(contactRepository).deleteById(contactId);

        // When
        ResponseEntity<HttpStatus> responseEntity = contactController.deleteContact(contactId);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }


    @Test
    void testDeleteContactWithZeroId() {
        // Given
        Long contactId = 0L;

        // When
        ResponseEntity<HttpStatus> responseEntity = contactController.deleteContact(contactId);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }


    @Test
    void testDeleteAllContacts() {
        // Given
        when(contactRepository.count()).thenReturn(10L);

        // When
        ResponseEntity<HttpStatus> responseEntity = contactController.deleteAllContacts();

        // Then
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        verify(contactRepository, times(1)).deleteAll();
    }


    @Test
    void testDeleteAllContactsWhenRepositoryIsEmpty() {
        // mock repository to return empty list of contacts
        when(contactRepository.findAll()).thenReturn(Collections.emptyList());
        // execute delete all contacts
        ResponseEntity<HttpStatus> responseEntity = contactController.deleteAllContacts();

        // verify that the response status is NO_CONTENT
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
    }


    @Test
    void testDeleteAllContactsWhenRepositoryContainsOneContact() {
        // create a contact to add to the repository
        Contact contact = new Contact("John", "Doe", "555-555-1234", "john.doe@example.com", "123 Main St");
        contact = contactRepository.save(contact);

        // delete all contacts
        ResponseEntity<HttpStatus> responseEntity = contactController.deleteAllContacts();

        // verify that the response status is NO_CONTENT
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());

        // verify that the repository is now empty
        assertEquals(0, contactRepository.count());
    }


    @Test
    void testDeleteAllContactsWithException() {
        // mock the repository's deleteAll method to throw an exception
        doThrow(new RuntimeException("Failed to delete contacts")).when(contactRepository).deleteAll();

        // call the deleteAllContacts endpoint and assert that an internal server error is returned
        ResponseEntity<HttpStatus> responseEntity = contactController.deleteAllContacts();
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

}