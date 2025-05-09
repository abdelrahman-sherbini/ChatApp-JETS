# ChatApp-JETS

commands : 

To convert to exe

install https://github.com/wixtoolset/wix3/releases/tag/wix3141rtm and add install-location/bin to environment path

```
mvn clean package
```

for only one child

```

mvn -pl Client clean package
mvn -pl Server clean package

```

to run 

```
mvn -pl Client clean javafx:run
mvn -pl Server clean javafx:run
```

to run Docker for sql

```
docker pull klash7/towk
docker run -d -p YourDesiredPort:3306 --name mysql-chat towk
```
Features:

* Server
 - Start/stop service
 - User statistics & info
 - DAOs & DTOs for database interaction (JDBC)
 - JAXB for configuration management

* Clients
 - Login/Signup with remembering last user login
 - Real-time chat (single/group) 
 - Real-time user info updates to friend users
 - Instant notifications (messages, friend requests, announcements)
 - Missed messages and Seen/Unseen status
 - Send files to users
 - Send friend requests to multiple users
 - Create groups with user search

Technologies & Tools:

* Programming & Data: Core Java, JDBC, MySQL
* Distributed Computing: RMI
* Email Integration: Jakarta Mail
* Chatbot: Gemini API
* XML Processing: JAXB
* Testing: Mockito, JUnit
* Project Management: Maven
* Version Control: GitHub

ITI Courses That Helped:

* Core Java - OOP, multi-threading, concurrency
* XML & XML API - Data exchange, configuration
* Advanced JavaFX - Interactive UI design
* Database Programming - Optimized data handling
* Network Programming (IO/NIO) - Real-time communication
* Maven - Dependency management
* Git - Collaboration & version control

#Demo
https://drive.google.com/file/d/11xEgsAUhM7yIg6LY0iE7x8Fk1TFPw60P/view?usp=sharing
