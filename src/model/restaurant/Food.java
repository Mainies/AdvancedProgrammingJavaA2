package model.restaurant;


public abstract class Food {
	
	/* Parent class for food items. Holds the base methods and data*/
	
	//Attributes
    private double _price;
    private int _prepTime;

    //Getters and Setters
    public double getPrice() {return _price;}

    public void setPrice(double newPrice) {_price = newPrice;}
    
    public int getCookTime() {return _prepTime;}

    public void setCookTime(int cookTime) {_prepTime = cookTime;}
    
}
