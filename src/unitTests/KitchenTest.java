package unitTests;

import org.junit.*;

import model.restaurant.*;

import static org.junit.Assert.*;

public class KitchenTest{
	Kitchen kitchen = new Kitchen();
	
	@Before
	public void setUp(){
		Kitchen kitchen = new Kitchen();
		kitchen.cooked.clear();
		kitchen.cooked.put("Fries", 0);
        kitchen.cooked.put("Burritos", 0);
        kitchen.cooked.put("Soda", 0);
	}
	
	@Test
    public void kitchenStartsWithNoFoodCooked() {
        assertEquals(0, (int) kitchen.cooked.get("Fries"));
        assertEquals(0, (int) kitchen.cooked.get("Burritos"));
        assertEquals(0, (int) kitchen.cooked.get("Soda"));
        System.out.println("New Opened Kitchen is Empty and Passes OH&S Standards");
    }
    
	@Test
    public void cookTimeForMoreBurritos(){
        BaseOrder order = new BaseOrder(3, 2, 0);
        int expectedFriesCookTime = 8;
        int expectedBurritosCookTime = 18;
        int cookTime = kitchen.cookTime(order);

        assertEquals((int) Math.max(expectedFriesCookTime, expectedBurritosCookTime), cookTime);
        System.out.println("Cook OrderTime Burritos Correct");
    }
	
	@Test
    public void cookTimeForMoreFries(){
        BaseOrder order = new BaseOrder(1, 6, 0);
        int expectedFriesCookTime = 16;
        int expectedBurritosCookTime = 9;
        int cookTime = kitchen.cookTime(order);

        assertEquals((int) Math.max(expectedFriesCookTime, expectedBurritosCookTime), cookTime);
        System.out.println("Cook OrderTime Fries Correct");
    }
	
	@Test
    public void checkLeftOverFries(){
        BaseOrder order = new BaseOrder(3, 2, 0);
        int cookTime = kitchen.cookTime(order);
        assertEquals((int) kitchen.cooked.get("Fries"), 3);
    }
	
	@Test
    public void cookTimeWithFries(){
		kitchen.cooked.put("Fries", 2);
        BaseOrder order = new BaseOrder(0, 2, 0);
        int expectedFriesCookTime = 0;
        int expectedBurritosCookTime = 0;
        int cookTime = kitchen.cookTime(order);
        assertEquals((int) Math.max(expectedFriesCookTime, expectedBurritosCookTime), cookTime);
        System.out.println("Fries Tray with Cooking TimeSuccessful");
	}
	
    @Test
    public void endOfDayReportsLeftoverFriesCorrectly() {
        kitchen.cooked.put("Fries", 5); 
        int leftovers = kitchen.endOfDay();
        assertEquals(5, leftovers);
        System.out.println("FriesTray Works");
    }
}
	
