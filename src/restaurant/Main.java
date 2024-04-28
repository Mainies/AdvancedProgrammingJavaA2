package restaurant;

public class Main {

	public static void main(String[] args) {
		Kitchen kitchen = new Kitchen();
		PointOfService pos = new PointOfService();
		pos.openingHours(kitchen);
	}

}
