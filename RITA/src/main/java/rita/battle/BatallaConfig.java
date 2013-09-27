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

package rita.battle;

import rita.settings.HelperEditor;
import rita.settings.Language;

public class BatallaConfig {
	private static String[] level1;
	private static String[] level2;
	private static String[] level3;

	static final String level1_tmp = "sample.RamFire|sample.Tracker"; // Settings.getProperty("level1");
	static final String level2_tmp = "sample.SpinBot";// Settings.getProperty("level2");
	static final String level3_tmp = "sample.VelociRobot";// Settings.getProperty("level3");
	
	static {
		level1 = parseRobots(level1_tmp);
		level2 = parseRobots(level2_tmp);
		level3 = parseRobots(level3_tmp);
	}

	private static String[] parseRobots(String level_tmp) {
		String[] robots=level_tmp.split("[|]");
		return robots;
	}

	/**
	 * El método chooseEnemy retorna los nombres de los adversarios de acuerdo
	 * al nivel seleccionado en la seccion Preferencias
	 * */
	public static String chooseEnemy(String level) {
		System.out.println("LEVEL ...." + level);
		if (level.equals(Language.get("level.zero")))
			return null;
		if (level.equals(Language.get("level.one")))
			return chooseRandom(level1);
		if (level.equals(Language.get("level.two")))
			return chooseRandom(level2);
		if (level.equals(Language.get("level.three")))
			return chooseRandom(level3);
		if (level.equals(Language.get("level.four")))
			return HelperEditor.enemies;
		return null;
	}

	/**
	 * El método chooseRandom elije de manera Random entre los robots, ya sea de
	 * nivel Facil, Medio o Dificil
	 * */
	private static String chooseRandom(String[] robots) {
		double d = Math.random();
		int pos = (int) (robots.length * d);
		return robots[pos];
	}

}
