package model.service;

import model.database.User;

public class UserService implements ApplicationService<User> {
	/* applciation service for user service*/
       private static UserService instance;
       
       private User user;
       
       private UserService() {}
       
	   public static UserService getInstance() {
	        if (instance == null) {
	            instance = new UserService();
	        }
	        return instance;
	    }
	   
	   public void setObject(User newUser) {
	    	this.user = newUser;
	    }

	    public User getObject() {
	    	return this.user;
	    }
	       
	    public void clearObject() {
	    	this.user = null;
	    }


}
