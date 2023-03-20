# ContactListManagerApplication-Java

This project demonstrates how to build a contact list manager application that provides a CRUD (Create,  Read, Update, Delete) functionality to manage contacts using Spring Boot and H2 in-memory database. Spring Data JPA is used to leverage built-in methods for performing CRUD operations.

To enable H2 DB related configuration, the ``` @EnableJpaRepositories ``` annotation is used on the main class. This annotation reads properties from the ``` application.properties ``` file, which contains configuration for the H2 database. With this setup, we can easily perform CRUD operations on the in-memory database using Spring Data JPA.

## Prerequisites 
- Java
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Maven](https://maven.apache.org/guides/index.html)
- [H2 Database](https://www.h2database.com/html/main.html)
- [Lombok](https://objectcomputing.com/resources/publications/sett/january-2010-reducing-boilerplate-code-with-project-lombok)


## Tools
- Eclipse or IntelliJ IDEA (or any preferred IDE) with embedded Maven
- Maven (version >= 3.6.0)
- Postman (or any RESTful API testing tool)
