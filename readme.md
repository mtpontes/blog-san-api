## 🖥️ About the project

Blog-san is an simple platform for sharing thoughts and
ideas through blog posts. It provides an interactive space for
readers to engage with content and authors to showcase their
work.

--- 

## ⚙️ Functionalities
- ✅ User registration;
- ✅ CRUD for publications and comments;
- ✅ Public acces for readers, but without interactions with publications and other users;
- ✅ Integrations between publications to comments and comments to comments;

---

## 🛠️ Tecnologies 

- [Lombok](https://projectlombok.org/)
- [Spring Boot](https://spring.io/projects/spring-boot)
- [MySQL Connector/J](https://dev.mysql.com/downloads/connector/j/)
- [Java JWT (Auth0)](https://github.com/auth0/java-jwt)
- [Springdoc OpenAPI](https://springdoc.org/)


## How to use

### Run

- Clone this repository
- Run the command `mvn spring-boot:run` in the root directory or
import the Maven project and run it in your IDE
- Access the URL http://localhost:8080

### Note

- All `GET` request endpoints are accessible without authentication.


---
### Register

- To create posts and comments, you need to register:

**POST:** `/auth/register`   
**Content-Type: application/json**
```
{
	"login": "newUser",
	"password": "newPassword",
	"name": "Example Name",
	"email": "example@email.com"
}
```

By default, all users are created with the `USER` role. 
To become an `ADMIN`, you need to `change your role` attribute in the database. There is an endpoint capable of creating users with the ADMIN role, but only users with the ADMIN role can access it.

---
### Login 

- After registering, you need to authenticate:

**POST:** `/auth/login`   
**Content-Type: application/json**
```
{
	"login": "newUser",
	"password": "newPassword",
}
```

**You will receive a token:**
```
{
  "token": "seu_token_de_acesso"
}
```
Which will be your 'free pass' to create posts and comments

## Using the access token
After receiveing the successful access token, you need to include the header for your future requests. The access token must be passed as parte of the "Authorization" title. 

**Header example:**

```
Authorization: Bearer your_access_token
```

---

### Spring Doc
- To access the complete project documentation, implemented with SpringDoc OpenApi, visit http://localhost:8080/swagger-ui/index.html when the application is running.

### Postman
- You can also import my set of requests into Postman. There you have all the endpoints with all the necessary URL parameters and body details to interact with the API.
