package unitTests;

import static org.junit.Assert.*;

import org.junit.*;
import exceptions.*;
import exceptions.MenuSelectException;
import exceptions.NotANumberException;
import model.restaurant.Order;
import model.restaurant.PointOfService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

public class PointOfServiceTest {
	private PointOfService pos = new PointOfService();

	
	@Before
	public void setUp() {
		// reset everything, prices and food items
		pos = new PointOfService();
	}

	@Test
	public void updateSalesTestmeal() {
		//check update sales with a meal
		Order order = new Order(1, 1, 1, 1);
		pos.updatePOS(order, true);
		assertEquals((int) pos.soldItems.get("Fries"), 1);
		assertEquals((int) pos.soldItems.get("Meals"), 1);
		assertEquals(pos.totalSales, 12.0, 0.00001);
	}
	
	@Test
	public void updateSalesTestnoMeal(){
		//check update sales without a meal
		Order order = new Order(1, 1, 1);
		pos.updatePOS(order, true);
		assertEquals((int) pos.soldItems.get("Fries"), 1);
		assertEquals(pos.totalSales, 15.0, 0.00001);
	}
	
	@Test
	public void updatePriceCheck() {
		pos.getBurrito().setPrice(5);
		Order order = new Order(1, 0, 0);
		pos.updatePOS(order, true);
		assertEquals(pos.totalSales, 5.0, 0.00001);
	}
	
	@Test
	public void updatePriceSalesMemory() {
		//check update sales without a meal
		Order order = new Order(1, 1, 1);
		pos.updatePOS(order, true);
		pos.getBurrito().setPrice(5);
		order = new Order(1, 0, 0);
		pos.updatePOS(order, true);
		assertEquals(pos.totalSales, 20.0, 000001);
		assertEquals((int) pos.soldItems.get("Burrito"), 2);
	}
	
	@Test (expected = NotANumberException.class)
    public void testValidateNumberThrowsException() throws Exception{
        PointOfService.validateNumber("not a number");
    }
	
	
	@Test
    public void testValidateMenuInput() throws Exception{
		//validate the menu input for 3 different cases
        assertThrows(MenuSelectException.class, () -> {
            PointOfService.validateMenuInput("not a number");
        });
        assertThrows(MenuSelectException.class, () -> {
            PointOfService.validateMenuInput("f");
        });
        assertThrows(MenuSelectException.class, () -> {
        	//number not in a menu range
            PointOfService.validateMenuInput("7");
        });
	}
	/*
	@Test
	public void testGetQuantity() {
        String simulatedUserInput = "5\n"; // User inputs '5' as the quantity
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out)); //avoid the output printing statement pausing at the next function
        System.setIn(new ByteArrayInputStream(simulatedUserInput.getBytes()));
        int result = pos.getQuantity();
        assertEquals(5, result);
    }
    */
	
	@After
	public void tearDown() {
		System.setIn(System.in);
		System.setOut(System.out);
	}
}
