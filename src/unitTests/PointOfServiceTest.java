package unitTests;

import static org.junit.Assert.*;

import org.junit.*;
import exceptions.NotANumberException;
import model.restaurant.BaseOrder;
import model.restaurant.Order;
import model.restaurant.PointOfService;

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
		BaseOrder order = new BaseOrder(1, 1, 1, 1);
		pos.updatePOS(order, true);
		assertEquals((int) pos.soldItems.get("Fries"), 1);
		assertEquals((int) pos.soldItems.get("Meals"), 1);
		assertEquals(pos.totalSales, 12.0, 0.00001);
	}
	
	@Test
	public void updateSalesTestnoMeal(){
		//check update sales without a meal
		BaseOrder order = new BaseOrder(1, 1, 1);
		pos.updatePOS(order, true);
		assertEquals((int) pos.soldItems.get("Fries"), 1);
		assertEquals(pos.totalSales, 15.0, 0.00001);
	}
	
	@Test
	public void updatePriceCheck() {
		pos.getBurrito().setPrice(5);
		BaseOrder order = new BaseOrder(1, 0, 0);
		pos.updatePOS(order, true);
		assertEquals(pos.totalSales, 5.0, 0.00001);
	}
	
	@Test
	public void updatePriceSalesMemory() {
		//check update sales without a meal
		BaseOrder order = new BaseOrder(1, 1, 1);
		pos.updatePOS(order, true);
		pos.getBurrito().setPrice(5);
		order = new BaseOrder(1, 0, 0);
		pos.updatePOS(order, true);
		assertEquals(pos.totalSales, 20.0, 000001);
		assertEquals((int) pos.soldItems.get("Burrito"), 2);
	}
	
	@Test (expected = NotANumberException.class)
    public void testValidateNumberThrowsException() throws Exception{
        pos.validateNumber("not a number");
    }
	
	@After
	public void tearDown() {
		System.setIn(System.in);
		System.setOut(System.out);
	}
}
