package service;

import restaurant.Kitchen;

public class KitchenService implements ApplicationService<Kitchen> {

       private static KitchenService instance;
       private Kitchen kitchen;
       
       private KitchenService() {
    	   this.kitchen = new Kitchen();
       }
       
	   public static KitchenService getInstance() {
	        if (instance == null) {
	            instance = new KitchenService();
	        }
	        return instance;
	    }
	   
	   public void setObject(Kitchen newKitchen) {
	    	this.kitchen = newKitchen;
	    }

	    public Kitchen getObject() {
	    	return this.kitchen;
	    }
	       
	    public void clearObject() {
	    	this.kitchen = null;
	    }
}



