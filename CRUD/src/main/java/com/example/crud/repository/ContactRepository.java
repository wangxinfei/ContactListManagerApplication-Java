package com.example.crud.repository;

import java.util.List;

import com.example.crud.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactRepository extends JpaRepository<Contact, Long> {
    List<Contact> findByExist(boolean exist);

    List<Contact> findByPhoneNumberContaining(String phoneNumber);
}