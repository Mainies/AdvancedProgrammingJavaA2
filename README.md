Assignment 2 - Advanced Programming

This assignment contains the repository for assignment 2. The last commit to this repository before the due date should be considered my submission for assignment. Following this course's submission and successful completion, I will also port this code to my own personal GitHub repository.

Tech-Stack:
- Program Developed using Eclipse IDE
- Java Development Kit Version: 17
- JavaFX version: 22.0.1
- Database: SQLite
- JDBC driver version: 3.27.21.1 from https://mvnrepository.com/artifact/org.xerial/sqlite-jdbc
- GUI developed using SceneBuilder and FXML files
- Very minor CSS styling in "/src/application.css"

Functionality/Choices:
All major functionality and exception handling per assignment specifications.
Exception handling is managed through the view/controller. This keeps the model safe from user volatility.
The time for Picking up orders is specified by using the system date and time instead of the user's (the user has to wait until the order is ready in real-time (instead of inputting a pickup time). I thought learning about the Java system date and time was a bit more interesting than another string error-handling task.

Usage:
Application run in Eclipse IDE from file:
'/src/application/main.java' 

Design:
"programDesign.pdf" contains a visual guide for the Model-View-Controller architecture.
"databaseDesign.pdf" shows the SQLite database structure, and the SQL queries used to create these tables in SQLite3 can be seen in program files.

OOP design highlights:
The application follows the Model-View-Controller design pattern. Relevant controllers handle data sent to and from the model. Due to the constraints of FXML, separate controllers were made across different files as the FXML files cannot access non-public classes.
- A singleton design for the AppService interface is implemented by fetching one recurrent User, Order, Kitchen, and PointOfService as required. 
The connection to the database is handled through a single Connect class that provides simple methods for making connections and sending/receiving data from the SQL-lite database. Data is sent and received as it is finalised (i.e., the order is fully confirmed). This was made for stability and allows for returning to a state after the program crashes.
- Food -> {Burrito, Fries and Soda} and User -> {NormalUser, VIPUser} demonstrate use of abstract data types and polymorphism.
- Note: While I recognise that the factory pattern is used in creating a TableColumn in FXML, this is implemented by the Java libraries (PropertyValueFactory and setCellValueFactory), which are standard methods in the FX library and thus do not contribute towards my OO choices.




