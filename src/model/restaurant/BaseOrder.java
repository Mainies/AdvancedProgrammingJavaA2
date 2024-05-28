package model.restaurant;

public class BaseOrder {
	/* Public class used to hold various food quantities
	 * Extended by Order which holds JavaFX attributes to interact with FX elements
 	* Order is used to retrieve quantities of food to be cooked or processed for sales
 	* Implemention based on Order data type in assignment 1
	* https://github.com/Mainies/AdvProgA1
	* Some changes, extra attributes, dates, prices linked the order and order status
 	*/
	
	//Attributes
	private int numBurritos;
	private int numFries;
	private int numSoda;
	private int numMeals = 0;
	private String dateCreated;
	private String datePickedUp;
	private double price;
	private int orderNum;
	private String status;
	
	
	public BaseOrder(int Burritos, int Fries, int Soda) {
		//instantiates a new order to build on.
		numBurritos = Burritos;
		numFries = Fries;
		numSoda = Soda;
		numMeals = 0;
	}
	
	public BaseOrder(int Burritos, int Fries, int Soda, int Meals) {
		//instantiates a new order to build on.
		numBurritos = Burritos;
		numFries = Fries;
		numSoda = Soda;
		numMeals = Meals;
	}
		
	public void printOrder() {
		//Printing method for order.
		System.out.printf("Your order is %d Burritos, %d Fries and %d Sodas.%n", 
				this.numBurritos, this.numFries, this.numSoda);
		if (this.numMeals > 0) {
			System.out.printf("You have ordered %d meal deal.%n", this.numMeals);
		}
	}
	
	public String getOrder() {
		//Method for returning string form of order. Useful for implmenting information to users
		//Creation of method for view controller
		String order = String.format("Your order is %d Burritos, %d Fries and %d Sodas.%n", 
				this.numBurritos, this.numFries, this.numSoda);
		if (this.numMeals > 0) {
			order = order + String.format("You have ordered %d meal deal", this.numMeals);
		}
		return order;
	}
	
	//Section for Getters and Setters of Interactive Attributes
	public int getBurritos() {
		return numBurritos;
	}
	
	public int getFries() {
		return numFries;
	}
	
	public int getSodas() {
		return numSoda;
	}
	
	public int getMeals() {
		return numMeals;
	}
	
	public void setBurritos(int burritos) {
		numBurritos = burritos;
	}
	
	public void setFries(int fries) {
		numFries = fries;
	}

	
	public void setSodas(int sodas) {
		numSoda = sodas;
	}
	
	public void setMeals(int meals) {
		numMeals = meals;
	}

	public String getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(String dateCreated) {
		this.dateCreated = dateCreated;
	}

	public String getDatePickedUp() {
		return datePickedUp;
	}

	public void setDatePickedUp(String datePickedUp) {
		this.datePickedUp = datePickedUp;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public void setOrderNum(int orderNum) {
		this.orderNum = orderNum;
	}
	
	public int getOrderNum() {
		return this.orderNum;	
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
