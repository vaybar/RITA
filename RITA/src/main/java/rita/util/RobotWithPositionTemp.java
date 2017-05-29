package rita.util;

public class RobotWithPositionTemp {
	private String robotName;
	private int x;
	private int y;
	private int orientation;
	private String selectedPackage;

	public RobotWithPositionTemp(String robotName, int x, int y, int orient, String currentRobotPackage) {
		this.robotName = robotName;
		this.x = x;
		this.y = y;
		this.orientation = orient;
		this.selectedPackage=currentRobotPackage;
	}

	public String getRobotName() {
		return robotName;
	}

	public void setRobotName(String robotName) {
		this.robotName = robotName;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setOrientation(Orientation orientationParam) {
		this.orientation = orientationParam.getValue();

	}

	public int getOrientation() {
		return orientation;
	}

	public String getSelectedPackage() {
		return selectedPackage;
	}

	public void setSelectedPackage(String selectedPackage) {
		this.selectedPackage = selectedPackage;
	}

	@Override
	public boolean equals(Object obj) {

		return this.getRobotName().equals(((RobotWithPositionTemp) obj).getRobotName());// && this.getSelectedPackage().equals(((RobotWithPositionTemp) obj).getSelectedPackage());
	}

	@Override
	public int hashCode() {
		return this.getRobotName().hashCode();
	}
	
	
	
	

}