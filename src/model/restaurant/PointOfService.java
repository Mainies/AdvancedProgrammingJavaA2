package model.restaurant;

import exceptions.*;
import java.util.HashMap;

interface priceCalculator{
	double checkout(Order order, boolean vip);
}

public class PointOfService implements priceCalculator {
	/* Core class used to maintain pricing for a user. Holds methods to take input and send to kitchen class for cooking as per functionality in assignment 1
	 * https://github.com/Mainies/AdvProgA1
	 * Instantiates a new food item on opening to access attributes for sales
	 * Changes from Assignment 1 to methods take in arguments directly to their relevant methods
	 * In place of 
	 */
	
	//accessing Burrito,fries and soda attributes encapsulated in class
	private Burrito burrito = new Burrito();
	private Fries fries = new Fries();
	private Soda soda = new Soda();
	public double totalSales = 0.00;
	
	
	//Not relevant for assginment 2 directly, but may allow further functionality from a manager perspective
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
	
	@Override
	public double checkout(Order order, boolean vip) {
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
	
	/* Exception Handling makes it easy to access custom
	 * exceptions from src/exceptions.
	 * Custom extensions the same as in assignment 1
	 */

	
	public void negativeInput(int input) throws InvalidNegativeNumber{
		if (input < 0) {
			throw new InvalidNegativeNumber(input);
		}
	}
	
	public void negativeInput(double input) throws InvalidNegativeNumber{
		if (input < 0) {
			throw new InvalidNegativeNumber(input);
		}
	}
	
	public void wholeNumber(double input) throws NotWholeNumber {
	    int intPart = (int) input;
	    if (input != intPart) { throw new NotWholeNumber(input);}
	}
	
	public void wholeNumber(int input) throws NotWholeNumber {
	    int intPart = (int) input;
	    if (input != intPart) {throw new NotWholeNumber(input);
	    }
	}
	
	public void validateNumber(String input) throws NotANumberException {
	    try {
	        Double.parseDouble(input);
	    } catch (NumberFormatException e) {throw new NotANumberException(input);}
	}
	
    public void validateQuantity(int quantity) throws InvalidNegativeNumber, NotWholeNumber {
        negativeInput(quantity); 
        wholeNumber(quantity);  
    }

	
}	
