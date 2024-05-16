package restaurant;

import exceptions.*;
import java.util.HashMap;
import java.util.Scanner;


public class PointOfService {
	// Core class used to maintain pricing for a user. Holds methods to take input and send to kitchen class for cooking as per functionality in assignment 1
	
	// Instantiates a new food item on opening to access attributes for sales
	
	private Burrito burrito = new Burrito();
	private Fries fries = new Fries();
	private Soda soda = new Soda();
	public double totalSales = 0.00;
	
	public HashMap<String, Integer> soldItems = new HashMap<String, Integer>();
	
	public PointOfService() {
		//At instantiation, no items are sold.
		burrito = new Burrito();
		fries = new Fries();
		soda = new Soda();
		totalSales = 0.00;
		soldItems = new HashMap<String, Integer>();
		soldItems.put("Fries", 0);
		soldItems.put("Burrito", 0);
		soldItems.put("Soda", 0);
		soldItems.put("Meals", 0);
		soldItems.put("VIP Discounts", 0);
	}
	
	//getter method for ability to change PointOfService price
	public Burrito getBurrito() {
		return this.burrito;
	}
	
	private void updateSaleQuantities(Order order, boolean vip) {
		this.soldItems.put("Fries", (this.soldItems.get("Fries") + order.getFries()));
		this.soldItems.put("Burrito", (this.soldItems.get("Burrito") + order.getBurritos()));
		this.soldItems.put("Soda", (this.soldItems.get("Soda") + order.getSodas()));
		this.soldItems.put("Meals", (this.soldItems.get("Meals") + order.getMeals()));
		if (vip) {
			this.soldItems.put("VIP Discounts", this.soldItems.get("VIP Discounts") + order.getMeals());
		}
	}
	
	public void updateAppSoldItems(Order order, boolean vip) {
		// Method for accessing through AppService
		this.updateSaleQuantities(order, vip);
	}
	
	private void updateSales(Order order, boolean vip) {
		// Gets current price for each food item in order and adds to sales total.
		// Allows for changing sales totals.
		double sale = this.totalSales;
		sale += order.getFries() * this.fries.getPrice();
		sale += order.getBurritos() * this.burrito.getPrice();
		sale += order.getSodas() * this.soda.getPrice();
		if (vip) {
			sale -= order.getMeals() * 3;
		}
		this.totalSales = sale;
	}
	
	
	public void updatePOS(Order order, boolean vip) {
		// Calls 2 methods to handle updating quantity of food sold and monetary value.
		this.updateSaleQuantities(order, vip);
		this.updateSales(order, vip);
	}
	
	
	public void newSale(String item, int price) {
		soldItems.put(item, (soldItems.get(item) + price));
	}
	

	private double checkout(Order order, boolean vip) {
		//Calculates total price for current sale.
		double sale = 0.00;
		sale += order.getFries() * this.fries.getPrice();
		sale += order.getBurritos() * this.burrito.getPrice();
		sale += order.getSodas() * this.soda.getPrice();
		if (vip) {
			sale -= order.getMeals() * 3;
		}
		return sale;
	}
	
	public double calculateSale(Order order, boolean vip) {
		return this.checkout(order, vip);
	}
	
	
	public static void validateMenuInput(String input) throws MenuSelectException{
	    if (!input.matches("[abcdeABCDE]")) {
	        throw new MenuSelectException(input);
	    }
	}
	
	public static void validateFoodInput(String input) throws FoodSelectException{
		if (!input.matches("[12345]")) {
			throw new FoodSelectException(input);
		}
	}
	
	public static void validateFoodUpdate(String input) throws NotAFoodItem{
		if (!input.matches("[123]")) {
			throw new NotAFoodItem(input);
		}
	}
	
	public static void negativeInput(int input) throws InvalidNegativeNumber{
		if (input < 0) {
			throw new InvalidNegativeNumber(input);
		}
	}
	
	public static void negativeInput(double input) throws InvalidNegativeNumber{
		if (input < 0) {
			throw new InvalidNegativeNumber(input);
		}
	}
	
	public static void wholeNumber(double input) throws NotWholeNumber {
	    int intPart = (int) input;
	    if (input != intPart) { throw new NotWholeNumber(input);}
	}
	
	public static void wholeNumber(int input) throws NotWholeNumber {
	    int intPart = (int) input;
	    if (input != intPart) {throw new NotWholeNumber(input);
	    }
	}
	
	public static void validateNumber(String input) throws NotANumberException {
	    try {
	        Double.parseDouble(input);
	    } catch (NumberFormatException e) {throw new NotANumberException(input);}
	}
	
    public void validateQuantity(int quantity) throws InvalidNegativeNumber, NotWholeNumber {
        negativeInput(quantity); 
        wholeNumber(quantity);  
    }

	
}	
