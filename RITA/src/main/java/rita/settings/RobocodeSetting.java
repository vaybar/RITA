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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Esta clase almacena información de la configuración de Robocode. Por ejemplo,
 * de los nombres de archivos de configuracion para distintos tipos de robots.
 * 
 * @author Vanessa Aybar Rosales
 * */
public class RobocodeSetting {

	private static String[] robotTypes = { "junior", "advanced" };
	private static Map<String, String> className = new HashMap<String, String>();
	private static Map<String, List<String>> requiredImports = new HashMap<String, List<String>>();
	private static Map<String, String> meaningClassName = new HashMap<String, String>();
	private static Map<String, String> configFile = new HashMap<String, String>();
	private static Map<String, String> startedCode = new HashMap<String, String>();

	private RobocodeSetting() {

	}

	static {
		className.put("junior", "JuniorRobot");
		className.put("advanced", "Robot"); //PERERATARRAGONA "AdvancedRobot"
		meaningClassName
				.put("junior",
						"Es un Robot con un modelo simplificado, que evita al jugador verse abrumado por las reglas de Robocode. Ideal para pensar en el comportamiento y conceptos de programación.");
		meaningClassName
				.put("advanced",
						"Es un Robot avanzado que permite llamados no bloqueantes, eventos customizados y escribir archivos. Si no está listo, tal vez sea mejor empezar por un Robot más simple. ");

		configFile.put("junior", "lang_def_junior.xml");
		configFile.put("advanced", "lang_def_advanced.xml");
//		configFile.put("junior", "lang_def_junior.xml");
//		configFile.put("advanced", "lang_def_advanced.xml");

//		startedCode.put("junior", "support//junior_code.xml");
//		startedCode.put("advanced", "src//main//resources//support//advanced_code.xml");
		startedCode.put("junior", "junior_code.xml");
		startedCode.put("advanced", "advanced_code.xml");

		List<String> importsJunior = new ArrayList<String>();
		importsJunior.add("robocode.JuniorRobot");
		List<String> importsAdvanced = new ArrayList<String>();
		importsAdvanced.add("robocode.Robot"); //PERERATARRAGONA "robocode.AdvancedRobot"

		requiredImports.put("junior", importsJunior);
		requiredImports.put("advanced", importsAdvanced);
	}

	public static String get(String currentRobotType) {
		return className.get(currentRobotType);
	}

	public static String getMeaning(String currentRobotType) {
		return meaningClassName.get(currentRobotType);
	}

	public static String[] getRobotTypes() {
		return robotTypes;
	}

	public static String getStarterCode(String currentRobotType) {
		return startedCode.get(currentRobotType);
	}

	public static List<String> getRobocodeImports(String currentRobotType) {
		return requiredImports.get(currentRobotType);
	}

	public static String getConfig(String currentRobotType) {
		return configFile.get(currentRobotType);
	}
}
