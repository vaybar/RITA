package rita.util;

public enum Orientation {

	NORTH(0), SOUTH(180), EAST(270), WEST(90);

	int value;

	Orientation(int i) {
		value = i;
	}

	public int getValue() {
		return value;
	}

}