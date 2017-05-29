package mode;

public abstract class Mode {

	
	public abstract String getRandomMode();
	
	public boolean isCompetition(){
		return false;
	}
	
	public boolean isTraining(){
		return false;
	}
}
