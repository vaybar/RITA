package rita.util;

import rita.settings.HelperEditor;

public class RandomSelectorPosition {

	private static int[] orientaciones = { 0, 90, 180, 270 };

	public static RobotWithPositionTemp findPosition(String robotName, String currentRobotPackage, int factor) {
		int MAX_X = HelperEditor.getBattleFieldX();
		int MAX_Y = HelperEditor.getBattleFieldY();
		
		int orientSelected =  Math.abs(((int) (factor * robotName.charAt(2) * robotName.length())) % 4);

		int xSelected = Math.abs((int) (factor * robotName.charAt(2) * robotName.length()) % MAX_X);
		int ySelected =  Math.abs((int) (factor * robotName.charAt(2) * robotName.length()) % MAX_Y);

		return new RobotWithPositionTemp(robotName, xSelected, ySelected, orientaciones[orientSelected], currentRobotPackage);
	}

}
