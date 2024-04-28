package exceptions;

public class InvalidNegativeNumber extends Exception {
	public InvalidNegativeNumber(double number){
		super("Number cannot be negative. You entered: " + number);
	}
	
	InvalidNegativeNumber(int number){
		super("Number cannot be negative. You entered: " + number);
	}
}
