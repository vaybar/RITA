package mode;

public class Training extends Mode {

	private static Training mode = new Training(); 
	
	@Override
	public String getRandomMode() {
		return "";
	}
	
	
	private Training(){
		
	} 
	
	public static Training getInstance(){
		return mode;
	}
	
	public boolean isTraining(){
		return true;
	}


	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "training";
	}

}
