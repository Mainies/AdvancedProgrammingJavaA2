package service;

public interface ApplicationService<T> {
	/* Interface for various application service tools
	 * Implements singleton pattern for getting and setting relevant parts of model
	 */

    static ApplicationService<?> getInstance() {
    	//Standard singleton getInstance()
    	throw new UnsupportedOperationException("This method should be implemented by the singleton service class.");
    }

    public default void setObject() {
    	//Set current object of instance to new object
    	throw new UnsupportedOperationException("This method should be implemented by the class.");
    }

    public default T getObject() {
    	//Get current object from service instnace
    	throw new UnsupportedOperationException("This method should be implemented by the class.");
    }
       
    public default void clearObject() {
    	//clear object. Allows for order and users to change if needed
    	throw new UnsupportedOperationException("This method should be implemented by the class.");
    }

}
