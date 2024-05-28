package model.restaurant;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class Order extends BaseOrder{
	/* Child class from Base order.
	 * Extension/Separation due to unit testing having challenges with javafx boolean property
	 * This extension was created to separate functionality from order to boolean properties
	 */
	public Order(int Burritos, int Fries, int Soda) {
		super(Burritos, Fries, Soda);
	}
	
	public Order(int Burritos, int Fries, int Soda, int Meals) {
		super(Burritos, Fries, Soda, Meals);
	}

	/* Public class used to hold various food quantities
 	* Order is used to retrieve quantities of food to be cooked or processed for sales
 	* Implemention based on Order data type in assignment 1
	* https://github.com/Mainies/AdvProgA1
	* Some changes, extra attributes, dates, prices linked the order and order status
 	*/
		
	/*Section was implemented for allowing orders to be selected or not for the
	 * use of printing only selected orders per the assignment 2 specifications
	 */
	
	//BooleanProperty and Simple Boolean property are part of JavaFX Library
	private BooleanProperty selected = new SimpleBooleanProperty(false);

    public BooleanProperty selectedProperty() {
        return selected;
    }

    public Boolean getSelected() {
        return selected.get();
    }

    public void setSelected(Boolean selected) {
        this.selected.set(selected);
    }
	
}
