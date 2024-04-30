package service;

public interface ApplicationService<T> {

    static ApplicationService<?> getInstance() {
    	throw new UnsupportedOperationException("This method should be implemented by the singleton service class.");
    }

    public default void setObject() {
    	throw new UnsupportedOperationException("This method should be implemented by the class.");
    }

    public default T getObject() {
    	throw new UnsupportedOperationException("This method should be implemented by the class.");
    }
       
    public default void clearObject() {
    	throw new UnsupportedOperationException("This method should be implemented by the class.");
    }

}
