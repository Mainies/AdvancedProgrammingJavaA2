package restaurant;

import exceptions.*;
import java.util.HashMap;
import java.util.Scanner;


public class PointOfService {
	// Core class used to interface with the user. Holds methods to take input and send to kitchen class for cooking
	
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
	}
	
	//getter method for ability to change PointOfService price
	public Burrito getBurrito() {
		return this.burrito;
	}
	/*
	public void openingHours(Kitchen kitchen) {
		//recursive menu calling function that calls an input handler and a function to take a valid input
		and perform desired output
	    PointOfService pos = new PointOfService();
	    boolean isOpen = true;
	    while (isOpen) {
	            String optionSelect = getValidMenuOption(pos);
	            isOpen = doSelectedOption(optionSelect, pos, kitchen);
	    }
	    pos.closingTime(kitchen); 
	}
	*/
	/*
	private String getValidMenuOption(PointOfService pos) {
	    while (true) {
	        try {
	            pos.printMenu();
	            String optionSelect = PointOfService.readUserInput();
	            PointOfService.validateMenuInput(optionSelect);
	            return optionSelect;
	        } catch (MenuSelectException e) {
	            System.out.println("Please enter a valid input.");
	        }
	    }
	}
	*/
	
	/*
	private boolean doSelectedOption(String optionSelect, PointOfService pos, Kitchen kitchen) {
		// Switch currently capable of Building an Order, Printing Sales Report, Update Prices
	    switch (optionSelect) {
	    case "a":
	        pos.orderFood(kitchen);
	        return true;
	    case "b":
	        pos.printSales();
	        return true;
	    case "c":
	        pos.updatePrice();
	        return true;
	    default:
	    	//case when "d" to exit loop
	        return false;
	    }
	}
	*/
	
	/*
	private void orderFood(Kitchen kitchen) {
		//Takes an order and returns the cooking time.
		try {
            Order current = this.buildOrder();
            int timeForCook = kitchen.cookTime(current);
            current.printOrder();
            System.out.printf("Estimated time to cook: %d minutes.%n", timeForCook);
            double endSale = this.checkout(current);
            this.cashier(endSale);
            this.updatePOS(current);
        } catch (Exception e) {
            System.out.println("Error processing order: " + e.getMessage());
        }
	}
	*/
	
	
	private void updateSaleQuantities(Order order) {
		this.soldItems.put("Fries", (this.soldItems.get("Fries") + order.getFries()));
		this.soldItems.put("Burrito", (this.soldItems.get("Burrito") + order.getBurritos()));
		this.soldItems.put("Soda", (this.soldItems.get("Soda") + order.getSodas()));
		this.soldItems.put("Meals", (this.soldItems.get("Meals") + order.getMeals()));
	}
	
	/*
	
	public void updateAppSoldItems(Order order) {
		// Method for accessing through AppService
		this.updateSaleQuantities(order);
	}
	*/
	
	private void updateSales(Order order) {
		// Gets current price for each food item in order and adds to sales total.
		// Allows for changing sales totals.
		double sale = this.totalSales;
		sale += order.getFries() * this.fries.getPrice();
		sale += order.getBurritos() * this.burrito.getPrice();
		sale += order.getSodas() * this.soda.getPrice();
		sale -= order.getMeals() * 3;
		this.totalSales = sale;
	}
	/*
	public void updateAppDailySales(Order order) {
		// Method for app service
		this.updateSales(order);
	}
	*/
	
	
	public void updatePOS(Order order) {
		// Calls 2 methods to handle updating quantity of food sold and monetary value.
		this.updateSaleQuantities(order);
		this.updateSales(order);
	}
	
	
	public void newSale(String item, int price) {
		soldItems.put(item, (soldItems.get(item) + price));
	}
	
	/*
	public static String readUserInput() {
		Scanner scanner = new Scanner(System.in);
		return scanner.nextLine().toLowerCase();
	}
	*/
	/*
	public int getQuantity() {
		// Gets quantity of food item required by user and holds methods for handling input exceptions.
	    String input;
	    int quantity;
	    while (true) {
	        try {
	            System.out.println("How many would you like:");
	            input = PointOfService.readUserInput(); 
	            PointOfService.validateNumber(input); 
	            quantity = Integer.parseInt(input); 
	            PointOfService.wholeNumber(quantity); 
	            break; 
	        } catch (NotANumberException e) {
	            System.out.println("Please enter a valid number.");
	        } catch (NotWholeNumber e) {
	            System.out.println("Cannot enter a decimal. Please try again.");
	        } catch (NumberFormatException e) {
	            System.out.println("Please enter a valid whole number.");
	        }
	    }
	    return quantity;
	}
	*/
	
	/*
	 public Order buildOrder() {
	        Order currentOrder = new Order(0, 0, 0);  
	        boolean complete = false;
	        while (!complete) {
	            printOrderOptions();
	            String option = getUserFoodSelection();
	            if (option.equals("5")) { 
	                complete = true;
	            } else {
	                processOrderItem(option, currentOrder); 
	            }
	        }
	        return currentOrder;
	    }
	*/
	
	/*
    private String getUserFoodSelection() {
        while (true) {
            try {
                String option = readUserInput(); 
                validateFoodInput(option); 
                return option;
            } catch (FoodSelectException e) {
                System.out.println("Invalid selection. Please enter the correct corresponding number.");
            }
        }
    }
    */
	
	/*
    private void processOrderItem(String option, Order currentOrder) {
        if (option.equals("4")) { 
            handleMealItem(currentOrder);
        } else {
            int quantity = getValidQuantity();
            currentOrder.addToOrder(option, quantity);
        }
    }

    private void handleMealItem(Order currentOrder) {
        int quantity = getValidQuantity();
        updateMealItems(quantity, currentOrder);
    }
	
    private int getValidQuantity() {
        while (true) {
            try {
                int quantity = getQuantity(); 
                validateQuantity(quantity); 
                return quantity;
            } catch (InvalidNegativeNumber | NotWholeNumber e) {
                System.out.println(e.getMessage() + " Please enter a positive whole number.");
            }
        }
    }
    */
	/*
    private void updateMealItems(int quantity, Order currentOrder) {
        currentOrder.setBurritos(currentOrder.getBurritos() + quantity);
        currentOrder.setFries(currentOrder.getFries() + quantity);
        currentOrder.setSodas(currentOrder.getSodas() + quantity);
        currentOrder.setMeals(currentOrder.getMeals() + quantity);
    }
    */

	private double checkout(Order order) {
		//Calculates total price for current sale.
		double sale = 0.00;
		sale += order.getFries() * this.fries.getPrice();
		sale += order.getBurritos() * this.burrito.getPrice();
		sale += order.getSodas() * this.soda.getPrice();
		sale -= order.getMeals() * 3;
		return sale;
	}
	
	public double calculateSale(Order order) {
		return this.checkout(order);
	}
	
	/*
	private void cashier(double saleamount) {
	    // Allows the PointOfService class to accept the appropriate amount of money and return change. 
	    double money = 0.00;
	    System.out.printf("Your total is $%.2f.%n", saleamount);
	    System.out.println("Please enter amount to pay: ");

	    while (true) {
	        try {
	            String input = PointOfService.readUserInput();
	            PointOfService.validateNumber(input); 
	            money = Double.parseDouble(input);

	            if (money < saleamount) {
	                System.out.println("Sorry, that is not enough money. Please enter a sufficient amount:");
	            } else {
	                break; 
	            }
	        } catch (NotANumberException e) {
	            System.out.println("Invalid input. Please enter a valid number:");
	        }
	    }

	    double change = money - saleamount;
	    System.out.printf("Your change is $%.2f. Thank you.%n", change);
	}
	*/
	/*
	public void updatePrice() {
	    String option = getValidFoodOption();
	    double correctPrice = getValidPrice();

	    updateFoodPrice(option, correctPrice);
	}
	*/
	/*
	private String getValidFoodOption() {
	    while (true) {
	        try {
	            this.printFoodOptions();
	            String option = PointOfService.readUserInput();
	            PointOfService.validateFoodUpdate(option);
	            return option;
	        } catch (NotAFoodItem e) {
	            System.out.println("Please enter the correct corresponding number.");
	        }
	    }
	}

	private double getValidPrice() {
	    while (true) {
	        try {
	            System.out.println("Please enter the new price: ");
	            String price = PointOfService.readUserInput();
	            PointOfService.validateNumber(price);
	            double correctPrice = Double.parseDouble(price);
	            PointOfService.negativeInput(correctPrice);
	            return correctPrice;
	        } catch (NotANumberException e) {
	            System.out.println("Please enter a valid number.");
	        } catch (InvalidNegativeNumber e) {
	            System.out.println("Entered number cannot be negative. Please try again.");
	        }
	    }
	}

	private void updateFoodPrice(String option, double correctPrice) {
	    switch (option) {
	    case "1":
	        this.burrito.setPrice(correctPrice);
	        break;
	    case "2":
	        this.fries.setPrice(correctPrice);
	        break;
	    case "3":
	        this.soda.setPrice(correctPrice);
	        break;
	    default:
	        System.out.println("Invalid food selection.");
	        updatePrice(); // Consider removing this recursive call if possible, or replacing with a different strategy.
	    }
	}
	*/

	/*
	private void closingTime(Kitchen kitchen) {
		//Method for closing Point of Service and restaurant. Prints fries in kitchen and outputs final sales total.
		System.out.println("End of Day Totals.");
		System.out.printf("Number of Fries to throw out: %d.%n", kitchen.cooked.get("Fries"));
		this.printSales();
		System.out.println("Thank you for coming to Burrito King.");
	}
	*/
	
	/*
	// Printers
	
	private void printMenu() {
		// Prints generic option menu to user
		System.out.printf("%-10s%n", "=".repeat(60));
		System.out.println("Burrito King");
		System.out.printf("%-10s%n", "=".repeat(60));
		System.out.println("a) Order");
		System.out.println("b) Show sales report");
		System.out.println("c) Update Prices");
		System.out.printf("\t d) Exit%n");
		System.out.println("Enter your selection: ");
	}
	
	public void printSales() {
	    System.out.printf("%n%-10s%n", "=".repeat(60));
	    System.out.println("Burrito King Sales Report");
	    System.out.printf("%-12s%n", "=".repeat(40));
	    System.out.printf("%-12s| %-10s%n", "Item", "Quantity");
	    System.out.printf("%-12s%n", "=".repeat(40));
	    System.out.printf("%-12s| %-10d%n", "Burritos", this.soldItems.get("Burrito"));
	    System.out.printf("%-12s| %-10d%n", "Fries", this.soldItems.get("Fries"));
	    System.out.printf("%-12s| %-10d%n", "Soda", this.soldItems.get("Soda"));
	    System.out.printf("%-12s| %-10d%n", "Meals", this.soldItems.get("Meals"));
	    System.out.printf("%-12s%n", "=".repeat(40));
	    System.out.printf("Total Sales | %.2f%n", this.totalSales);
	    System.out.printf("%-12s%n", "=".repeat(40));
	}
	
	private void printOrderOptions() {
		// Prints food item option select for user.
		System.out.printf("\t Please select a food item.%n");
		System.out.println("1. Burrito");
		System.out.println("2. Fries");
		System.out.println("3. Soda");
		System.out.println("4. Meal Deal (1x Burrito, Fries, Soda)");
		System.out.println("5. Checkout");
	}
	
	private void printFoodOptions() {
		System.out.printf("\t Please select a food item.%n");
		System.out.println("1. Burrito");
		System.out.println("2. Fries");
		System.out.println("3. Soda");
	}
	*/
	
	// Exceptions Handling Section
	
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
