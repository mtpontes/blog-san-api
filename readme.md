## Blog-san API

Blog-san is a simple REST API project, with the intention of practicing, CRUD, mapping and entity relationships. There, users can create an account, publish, comment and respond.

## 🛠️ Tecnologies 

- [Spring Boot](https://spring.io/projects/spring-boot)
- [Docker](https://www.docker.com)
- [Test Containers](https://testcontainers.com)
- [Java JWT](https://github.com/auth0/java-jwt)
- [MySQL](https://dev.mysql.com/downloads/connector/j/)
- [Springdoc OpenAPI](https://springdoc.org/)

## ⚙️ Functionalities
- [x] User registration;
- [x] Authentication and authorization;
- [x] CRUD for publications and comments;
- [x] Public acces for readers, but without interactions with publications and other users;
- [x] Relationships between publications to comments and comments to comments;

## 📖 How to use
<details><summary>Clique para expandir</summary>


### Details

The application is configured to connect to MySQL via port 3306.

### Environment variables:

#### Database

| ENV | DEFAULT VALUE | DESCRIPTION |
| ---------- | --- | ------------- |
| `DB_USERNAME` | root | Database username |
| `DB_PASSWORD` | root | Database password |

#### Security
| ENV | DEFAULT VALUE | DESCRIPTION |
| ---------- | --- | ------------- |
| `JWT_SECRET` | secret | JWT token secret |



### Prerequisites
- Java 17
- MySQL 8.0


### Run
- Clone this repository

- Run the in the root directory or:
  - Linux:

        ./mvnw spring-boot:run
  
  - Windows:

        mvnw.cmd spring-boot:run

### Documentation

The documentation can be accessed after deploying the application via the URL http://localhost:8080/swagger-ui/index.html#/

You can also import my set of requests into Postman. There you have all the endpoints with all the necessary URL parameters and body details to interact with the API.

[<img src="https://run.pstmn.io/button.svg" alt="Run In Postman" style="width: 128px; height: 32px;">](https://app.getpostman.com/run-collection/31232249-755011b3-0b0f-4120-9699-7677b4c10832?action=collection%2Ffork&source=rip_markdown&collection-url=entityId%3D31232249-755011b3-0b0f-4120-9699-7677b4c10832%26entityType%3Dcollection%26workspaceId%3Daae15406-ac2a-4087-8c9e-47072e8aa119)

## Examples

### Note
- All `GET` request endpoints are accessible without authentication.

---

By default, all users are created with the USER role, these users can only create comments on posts. To become an ADMIN and be able to create posts, you can use the system's default ADMIN user:

#### Default user ADMIN
- **login**: root
- **password**: root

This way you can authenticate as ADMIN to have the freedom to create posts and even other ADMINs in the system.

---

### Register

To create posts and comments, you need to register:

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
---

### Login 

After registering, you need to authenticate:

**POST:** `/auth/login`   
**Content-Type: application/json**

```
{
	"login": "root",
	"password": "root",
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

#### These are basic examples, and you can explore other endpoints as needed. Be sure to replace the dummy values with actual data from your development environment.