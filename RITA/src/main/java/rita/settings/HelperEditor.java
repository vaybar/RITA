/*
 * Copyright 2013 Vanessa Aybar Rosales
 * This file is part of RITA.
 * RITA is free software: you can redistribute it and/or modify it under the 
 * terms of the GNU General Public License as published by the Free Software 
 * Foundation, either version 3 of the License, or (at your option) any later 
 * version.
 * RITA is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS 
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with 
 * RITA. If not, see http://www.gnu.org/licenses/.
 */

package rita.settings;

import mode.Mode;

/**
 * HelperEditor almacena informacion sobre el robot siendo editado actualmente.
 * Más adelante podría almacenar información sobre el equipo (team).
 * 
 * @author Vanessa Aybar Rosales
 */
public class HelperEditor {
	// valores por defecto
	public static String defaultPageName="defaultPage";
	public static String currentRobotName="";
	public static String currentRobotPackage="";
	public static String currentRobotType="advanced";
	public static Mode CURRENT_MODE;
//	public static String currentTeamName="";
//	public static HashMap<String, String> currentsRobotsInTeam= new HashMap<String, String>();


	public static String enemies="";
	
	private HelperEditor(){
		
	}

	public static void setCurrentRobotType(String type){
		currentRobotType = type;
	}
	
	/*Remove all the stored information*/
	public static void clean(){
//		currentRobotType="";
		currentRobotName="";
//		currentTeamName="";
//		currentsRobotsInTeam= new HashMap<String, String>();
	}

	public static void setEnemies(String selectedString) {
		enemies=selectedString;
		
	}

	public static int getBattleFieldY() {
		// TODO devolver el valor real que corresponde
		return 600;
	}
	
	public static int getBattleFieldX() {
		// TODO devolver el valor real que corresponde
		return 800;
	}
	
	public static void setCurrentMode(Mode m){
		CURRENT_MODE=m;
	}
	
	public static Mode getCurrentMode(){
		return CURRENT_MODE;
	}

	/*public static boolean selectedGroup() {
		return selectedGroup!=null;
	}

	public static void setSelectedGroup(String selectedGroup) {
		HelperEditor.selectedGroup = selectedGroup;
	}
	*/
	
	
}
