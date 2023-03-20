package com.example.crud.repository;

import java.util.List;

import com.example.crud.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    /*List<Contact> findByExist(boolean exist);*/
    List<Contact> findByFirstName(String firstName);
    List<Contact> findByPhoneNumberContaining(String phoneNumber);
}