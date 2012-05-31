package at.brandl.rb.client;

public enum Room {
	Festsaal(3), Bewegungsraum(2), Sekundaria(3), Primaria(1), Werkstatt(0), Buero(
			1), Schloss(-1);

	private int floor;

	private Room(int pFloor) {
		floor = pFloor;
	}

	public int getFloor() {
		return floor;
	}
}
