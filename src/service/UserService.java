package service;

import database.User;

public class UserService implements ApplicationService<User> {
       private static UserService instance;
       private volatile User user;
       
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
