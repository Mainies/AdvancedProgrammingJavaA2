package service;

import database.User;
import restaurant.Kitchen;
import restaurant.Order;
import restaurant.PointOfService;

public class ApplicationService {
    private static ApplicationService instance;
    private User currentUser;
    private Order currentOrder;
    private final PointOfService pos;
    private final Kitchen kitchen;

    private ApplicationService() {
    	this.pos = new PointOfService();
    	this.kitchen = new Kitchen();
    }

    public static ApplicationService getInstance() {
        if (instance == null) {
            instance = new ApplicationService();
        }
        return instance;
    }

    public void setUser(User user) {
        this.currentUser = user;
    }

    public User getUser() {
        return currentUser;
    }

    public void setOrder(Order order) {
        this.currentOrder = order;
    }

    public Order getOrder() {
        return currentOrder;
    }

	public PointOfService getPos() {
		return pos;
	}

	public Kitchen getKitchen() {
		return kitchen;
	}
	
	 //Method for Logout
    public void clearUser() {
        this.currentUser = null;
    }

}
