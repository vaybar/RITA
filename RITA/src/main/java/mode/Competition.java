package mode;

public class Competition extends Mode {
	private static Competition mode = new Competition();

	@Override
	public String getRandomMode() {
		return "-DRANDOMSEED=10";
	}

	private Competition() {

	}

	public static Competition getInstance() {
		return mode;
	}
	
	public boolean isCompetition(){
		return true;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "competition";
	}

}
