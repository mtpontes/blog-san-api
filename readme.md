## 🖥️ About the project

Blog-san is a simple REST API project, with the intention of practicing, CRUD, mapping and entity relationships. There, users can create an account, publish, comment and respond.

--- 

## ⚙️ Functionalities
- ✅ User registration;
- ✅ Authentication and authorization;
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

**Response:**

```
{
  "token": "your_access_token"
}
```

This access token will be your 'free pass' to create posts and comments

---


### Using the access token

After receiveing the successful access token, you need to include the header for your future requests. The access token must be passed as parte of the "Authorization" title. 

**Header example:**

```
Authorization: Bearer your_access_token
```

---

### Publication creation

**POST:** `/publications`

```
{
  "description": "Publication content",
  "imageLink": "link_for_image"
}
```

**Response:**

```
{
    "publicationId": 1,
    "userId": 1,
    "nameUser": "Your User Name",
    "description": "Publication content",
    "imageLink": "link_for_image",
    "date": "2024-02-10 19:13"
}
```
---

### Comment creation

**POST:** `/publications/{publicationId/comments`

```
{
	"publicationId": 1,
	"text": "Comment example"
}
```

**Response:**
```
{
    "commentId": 1,
    "userId": 1,
    "text": "Comment example",
    "date": "2024-02-10 19:10",
    "edited": false
}
```
---

### These are basic examples, and you can explore other endpoints as needed. Be sure to replace the dummy values with actual data from your development environment.


---

### Spring Doc
- To access the complete project documentation, implemented with SpringDoc OpenApi, visit http://localhost:8080/swagger-ui/index.html when the application is running.

### Postman
You can also import my set of requests into Postman. There you have all the endpoints with all the necessary URL parameters and body details to interact with the API.

[<img src="https://run.pstmn.io/button.svg" alt="Run In Postman" style="width: 128px; height: 32px;">](https://app.getpostman.com/run-collection/31232249-755011b3-0b0f-4120-9699-7677b4c10832?action=collection%2Ffork&source=rip_markdown&collection-url=entityId%3D31232249-755011b3-0b0f-4120-9699-7677b4c10832%26entityType%3Dcollection%26workspaceId%3Daae15406-ac2a-4087-8c9e-47072e8aa119)
