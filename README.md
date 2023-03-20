# ContactListManagerApplication-Java

This project demonstrates how to build a contact list manager application that provides a CRUD (Create,  Read, Update, Delete) functionality to manage contacts using Spring Boot and H2 in-memory database. Spring Data JPA is used to leverage built-in methods for performing CRUD operations.

To enable H2 DB related configuration, the ``` @EnableJpaRepositories ``` annotation is used on the main class. This annotation reads properties from the ``` application.properties ``` file, which contains configuration for the H2 database. With this setup, we can easily perform CRUD operations on the in-memory database using Spring Data JPA.

## Prerequisites 
- Java
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Maven](https://maven.apache.org/guides/index.html)
- [H2 Database](https://www.h2database.com/html/main.html)


## Tools
- IntelliJ IDEA (or any preferred IDE) with embedded Maven
- Maven (version >= 3.6.0)
- Postman (or any RESTful API testing tool)

###  Build and Run application

Command in terminal
> **```mvn spring-boot:run```** it will run application as spring boot application
> 
Or
> run main method from `CrudApplication.java` as spring boot application.  

### Postman - API Endpoints

- #### CRUD Operations

    > **POST Mapping** http://localhost:2222/api/contacts/createContact  - Add new Contact 
    
     Request Body in JSON
     ```
        {
            "firstName": "Alan",
            "lastName": "Wang",
            "phoneNumber": "123-456-7890",
            "email": "alan.wang@example.com",
            "address": "1st Avenue"
        }
        
     ```
     
    > **GET Mapping** http://localhost:2222/api/contacts/getAll  - Get all Contacts
    
    > **GET Mapping** http://localhost:2222/api/contacts/getById/{{id}}  - Get a Contact by ID
    
    > **GET Mapping** http://localhost:2222/api/contacts/getByFirstName  - Get Contacts by firstName
    
    > **GET Mapping** http://localhost:2222/api/contacts/getByPhoneNumber/{{phoneNumber}}  - Get a Contact by phoneNumber
       
    
    > **PUT Mapping** http://localhost:2222/api/contacts/updateContact/{{id}}  - Update existing Contact by a given ID 
                                                       
     Request Body in JSON
     ```
        {
            "firstName": "Betty",
            "lastName": "Wang",
            "phoneNumber": "123-456-7890",
            "email": "betty.wang@example.com",
            "address": "21st Avenue"
        }
     ```
    
    > **DELETE Mapping** http://localhost:2222/api/contacts/deleteContact/{{id}}  - Delete Contact by ID
    
    > **DELETE Mapping** http://localhost:2222/api/contacts/deleteAll  - Delete all Contacts
