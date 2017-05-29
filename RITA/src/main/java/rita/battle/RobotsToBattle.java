package rita.battle;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import rita.util.RobotWithPositionTemp;

public class RobotsToBattle {
	private static Set<RobotWithPositionTemp> robotsToBattle = new HashSet<RobotWithPositionTemp>();

	private static RobotsToBattle rb = new RobotsToBattle();

	private RobotsToBattle() {

	}

	public static RobotsToBattle getInstance() {
		return rb;
	}

	public static void addRobotToTheList(RobotWithPositionTemp robotWithPositionTemp) {
		RobotsToBattle.removeRobot(robotWithPositionTemp.getRobotName(), robotWithPositionTemp.getSelectedPackage());
		//if (!robotsToBattle.contains(robotWithPositionTemp))
			robotsToBattle.add(robotWithPositionTemp);
	}

	public Set<RobotWithPositionTemp> getListRobotsToBattle() {
		return robotsToBattle;
	}

	public static void clearList() {
		robotsToBattle.clear();
	}

	public static void removeRobot(String name, String robotPackage) {
		robotsToBattle.remove(new RobotWithPositionTemp(name, 0, 0, 0, robotPackage));

	}

}
