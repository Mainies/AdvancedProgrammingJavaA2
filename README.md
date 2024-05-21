Assignment 2 - Advanced Programming

This assignment contains the repository for assignment 2. The last commit to this repository should be considered my submission for assginment and following the submission and succesful completion of this course I will also port this code to my own personal GitHub repository.

Tech-Stack:
- Program Developped using Eclipse IDE
- Java Development Kit Version: 17.0.x
- JavaFX version:
- SQLite Database
- JDBC driver version:
- GUI developed using SceneBuilder (fxml files)
- Minor CSS styling in application.css

Functionality:
All major functionality. Minor Adjustments from Specifications
- Time for Picking up orders is specfied by using the systems date and time instead of the user (user has to wait till order is ready in real time)
- Users can select which specific orders they want to report instead of which information.

Usage:
Application run from 
'./application/src/main.java'


Design:
"programDesign.pdf" contains a visual guide for the Model-View-Controller architecture.
"databaseDesign.pdf" shows the SQLite database structure and the SQL queries used to create these tables in SQLite3 can be seen in program files.

OOP design highlights:
- singleton design for AppService interface that is implemented by fetches one recurrent User, Order, Kitchen and PointOfService as required.
- (TO EDIT) make Restaurant Interface that implements a PointOfService, and Kitchen??? PointOfService demonstrates the Facade pattern by hiding complexities of getting food prices from food class, cooking times, calculating the prices and discounts.
- Connect is all handled through a single class that provides simple methods for making connections and sending/receiving data from the SQL-lite database. Data is send received as it is finalised (i.e. order fully confirmed). This was made for stability.




