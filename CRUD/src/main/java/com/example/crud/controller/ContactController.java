package com.example.crud.controller;

import com.example.crud.model.Contact;
import com.example.crud.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
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


    /**
     * Retrieves a list of all contacts from the database, or all contacts containing the provided phone number.
     *
     * @param phoneNumber An optional phone number to search for in the contacts list.
     * @return A ResponseEntity object containing either the list of contacts, or a NO_CONTENT status if the list is empty.
     */
    @GetMapping("/contacts")
    public ResponseEntity<List<Contact>> getAllContacts(@RequestParam(required = false) String phoneNumber) {
        try {
            List<Contact> contacts = new ArrayList<Contact>();

            // Retrieves all contacts from the database if no phone number is provided
            if (phoneNumber == null){
                contactRepository.findAll().forEach(contacts::add);
            } else {
                contactRepository.findByPhoneNumberContaining(phoneNumber).forEach(contacts::add);
            }

            // If the list is empty, return to NO_CONTENT status
            if (contacts.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(contacts, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * Retrieves a single Contact instance by ID.
     *
     * @param id the ID of the contact to retrieve
     * @return ResponseEntity<Contact> the HTTP response containing the Contact instance if it exists,
     *         or a NOT_FOUND status if it does not exist
     */
    @GetMapping("/contacts/{id}")
    public ResponseEntity<Contact> getContactById(@PathVariable("id") long id) {
        Optional<Contact> contactData = contactRepository.findById(id);

        // If the contact exists, return it with a status of OK
        if (contactData.isPresent()) {
            return new ResponseEntity<>(contactData.get(), HttpStatus.OK);
        } else { // If the contact does not exist, return a NOT_FOUND status
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
    }


    /**
     * Returns a ResponseEntity containing a list of Contact objects with the matching first name.
     *
     * @param firstName The first name to search for in the database
     * @return A ResponseEntity containing the list of matching Contact objects, or an HTTP status NO_CONTENT if none are found
     * @throws DataAccessException If an error occurs while attempting to access the database,
     *         or Exception to handle all other exceptions
     */
    @GetMapping("/contacts/getByFirstName")
    public ResponseEntity<List<Contact>> getContactsByFirstName(@RequestParam String firstName) {
        try {
            List<Contact> contacts = contactRepository.findByFirstName(firstName);

            if (contacts.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(contacts);
        } catch (DataAccessException e) {
            // handle database access error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            // handle all other exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    /**
     * Retrieves a list of contacts whose phone number contains the specified value.
     *
     * @param phoneNumber The partial or complete phone number to search for
     * @return ResponseEntity<List<Contact>>
     *         Returns a ResponseEntity object containing a list of matching contacts with an HTTP status OK,
     *         or an empty ResponseEntity with an HTTP status NO CONTENT if no matches are found,
     *         or a null ResponseEntity with an HTTP status INTERNAL SERVER ERROR if an exception occurs during processing.
     */
    @GetMapping("/contacts/{phoneNumber}")
    public ResponseEntity<List<Contact>> getContactsByPhoneNumber(@PathVariable String phoneNumber) {
        List<Contact> contacts;

        try {
            contacts = contactRepository.findByPhoneNumberContaining(phoneNumber);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        if (contacts.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(contacts);
        }
    }


    /**
     * POST endpoint for creating a new contact.
     *
     * @param contact the Contact object to be created
     * @return ResponseEntity<Contact> response containing the newly created Contact object and a status of CREATED,
     *         if a contact with the same phone number already exists, return to CONFLICT,
     *         returns a response with a null body and a status of INTERNAL_SERVER_ERROR if there is an error.
     */
    @PostMapping("/contacts")
    public ResponseEntity<Contact> createContact(@RequestBody Contact contact) {
        try {
            // Check if a contact with the same phone number already exists
            List<Contact> existingContacts = contactRepository.findByPhoneNumberContaining(contact.getPhoneNumber());

            if (!existingContacts.isEmpty()) {
                // A contact with the same phone number already exists, so return an error response
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }

            // No contact with the same phone number exists, so save the new contacts
            return ResponseEntity.status(HttpStatus.CREATED).body(contactRepository.save(contact));
        } catch (DataAccessException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * Updates the contact with the specified ID.
     * If the contact is found, updates the contact details and returns the updated contact.
     * If the contact is not found, returns a NOT_FOUND response.
     *
     * @param id the ID of the contact to update
     * @param contact the updated contact details to save
     * @return a response entity containing the updated contact if successful, or a NOT_FOUND response if the contact is not found
     */
    @PutMapping("/contacts/{id}")
    public ResponseEntity<Contact> updateContact(@PathVariable("id") long id, @RequestBody Contact contact) {
        try {
            Optional<Contact> contactData = contactRepository.findById(id);

            if (contactData.isPresent()) {
                Contact _contact = contactData.get();
                _contact.setFirstName(contact.getFirstName());
                _contact.setLastName(contact.getLastName());
                _contact.setPhoneNumber(contact.getPhoneNumber());
                _contact.setEmail(contact.getEmail());
                _contact.setAddress(contact.getAddress());

                return new ResponseEntity<>(contactRepository.save(_contact), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (EmptyResultDataAccessException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * Deletes a contact with the given ID from the database.
     *
     * @param id the ID of the contact to delete
     * @return a ResponseEntity with HTTP status NO_CONTENT if the contact was successfully deleted,
     *         or INTERNAL_SERVER_ERROR if there was an error
     */
    @DeleteMapping("/contacts/deleteContact/{id}")
    public ResponseEntity<HttpStatus> deleteContact(@PathVariable("id") long id) {
        try {
            contactRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (EmptyResultDataAccessException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    /**
     * Delete all contacts from the database.
     * @return ResponseEntity with HTTP status NO_CONTENT if all contacts were deleted successfully,
     *         or HTTP status code 500 (INTERNAL_SERVER_ERROR) if an error occurred while deleting the contacts.
     */
    @DeleteMapping("/contacts")
    public ResponseEntity<HttpStatus> deleteAllContacts() {
        try {
            contactRepository.deleteAll();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
