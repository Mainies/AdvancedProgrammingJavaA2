package model.restaurant;

import java.util.*;

interface IKitchenCapacity{
	int calculateFriesCookTime(int fries);
	int cookTime(Order order);
	int calculateBurritosCookTime(int numBurritos); 
}
public class Kitchen implements IKitchenCapacity{
		/* Main class to hold currently cooked fries and calculate cooking time*/
		
		public HashMap<String, Integer> cooked = new HashMap<String, Integer>();
	    
	    public Kitchen() {
	    	//Opening the kitchen with no food cooked.
	        cooked.put("Fries", 0);
	        cooked.put("Burritos", 0); 
	        cooked.put("Soda", 0); 
	    }
	    
	    
	    public static void main(String[] args) {
	        Order order = new Order(3, 5, 1);
	        Kitchen kitchen = new Kitchen();
	        System.out.println("Total cook time: " + kitchen.cookTime(order) + " minutes");
	        System.out.println("End of day leftover fries: " + kitchen.endOfDay());
	    }
	    
	    
	    public int cookTime(Order order) {
	    	// Calculates the cooking time required to find the limiting factor. Returns the highest cooking time.
	        int friesCookTime = calculateFriesCookTime(order.getFries());
	        int burritosCookTime = calculateBurritosCookTime(order.getBurritos());
	        return Math.max(friesCookTime, burritosCookTime);
	    }
	    
	    @Override
		public int calculateFriesCookTime(int numFries) {
	    	// Finds if more fries need to be cooked for the order, and then calculates cooking time based on batches of 5.
	    	Fries fries = new Fries();
	        int availableFries = cooked.get("Fries");
	        int totalFriesNeeded = numFries - availableFries; 
	        if (totalFriesNeeded <= 0) {
	            cooked.put("Fries", -totalFriesNeeded); 
	            return 0;
	        }
	        if (totalFriesNeeded > 0) {
	        	System.out.println("Cooking more fries. Please be patient.");
	        }
	        int batchesNeeded = (int) Math.ceil(totalFriesNeeded / 5.0);
	        int cookTime = batchesNeeded * fries.getCookTime();
	        int leftoverFries = batchesNeeded * 5 - totalFriesNeeded;
	        // Put excess fries from batch into cooked for next order.
	        cooked.put("Fries", leftoverFries); 
	        return cookTime;
	    }
	    
	    @Override
		public int calculateBurritosCookTime(int numBurritos) {
	    	// Simple ceiling equation for each burrito cook time/2
	    	Burrito burrito = new Burrito();
	        int cookTimePerPair = burrito.getCookTime(); 
	        int pairs = (int) Math.ceil(numBurritos / 2.0);
	        return pairs * cookTimePerPair;
	    }

	    public int endOfDay() {
	        int leftoverFries = cooked.get("Fries");
	        return leftoverFries; 
	    }
}


