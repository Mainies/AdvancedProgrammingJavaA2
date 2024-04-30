package service;

import restaurant.PointOfService;

public class POSService implements ApplicationService<PointOfService> {

       private static POSService instance;
       private PointOfService pos;
             
       private POSService() {
    	   this.pos = new PointOfService();
       }
       
	   public static POSService getInstance() {
	        if (instance == null) {
	            instance = new POSService();
	        }
	        return instance;
	    }
	   
	   public void setObject(PointOfService newpos) {
	    	this.pos = newpos;
	    }

	    public PointOfService getObject() {
	    	return this.pos;
	    }
	       
	    public void clearObject() {
	    	this.pos = null;
	    }
}

