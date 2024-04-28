package exceptions;

public class NotAFoodItem extends Exception {
	public NotAFoodItem (String message) {
		super(message);
	}
}
