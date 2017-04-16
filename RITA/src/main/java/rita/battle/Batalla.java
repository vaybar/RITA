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

import java.util.Calendar;

import mode.Competition;
import mode.Training;
import net.sf.robocode.core.Container;
import net.sf.robocode.ui.dialog.ResultsDialog;
import rita.settings.HelperEditor;
import robocode.control.BattleSpecification;
import robocode.control.BattlefieldSpecification;
import robocode.control.RobocodeEngine;
import robocode.control.RobotSpecification;
import robocode.control.events.BattleAdaptor;
import robocode.control.events.BattleCompletedEvent;
import robocode.control.events.BattleErrorEvent;
import robocode.control.events.BattleMessageEvent;

/**
 * La clase Batalla dispara la ejecuci?n del motor de Robocode. En el campo de
 * batalla se ubicar?n los robots seleccionados para combartir
 * 
 * @author Vanessa Aybar Rosales
 * */
public class Batalla implements Runnable {

	public static String installPath;
	public static String robotName;

	public static String enemiesSelected;

	public static Integer roundsNumber;

	public static String initialPosition;
	public static String mode;
	

	public static final int NUMBER_OF_ROUNDS = 5;
	public static final int MAX_NUMBER_OF_ROUNDS = 5;
	public static final int MIN_NUMBER_OF_ROUNDS = 1;

	// seleccionados por RITA por defecto para los 3 niveles
	private Batalla() {

	}

	/**
	 * Ejecucion principal de una Batalla en RITA
	 * 
	 * @param arg
	 *            [0] corresponde al path donde esta instalada la aplicacion, es
	 *            necesario para que el motor empiece a buscar robots a partir
	 *            de este path
	 * @param arg
	 *            [1] nombre del robot actual
	 * @param arg
	 *            [2] nivel de batalla seleccionado
	 * @param arg
	 *            [3] enemigos en el campo de batalla, podria no existir este
	 *            parametro
	 * @param arg
	 *            [3] posiciones iniciales de todos los robots en el campo de
	 *            batalla, podra no existir este parametro
	 * */
	public static void main(String... args) {
		installPath = args[0];
		robotName = args[1];
		// if (args.length > 2)
		enemiesSelected = args[2];

		try {
			roundsNumber = Integer.parseInt(args[3]);
			initialPosition = args[4];
			mode = args[5];
		} catch (NumberFormatException e) {
			roundsNumber = Batalla.NUMBER_OF_ROUNDS;
		}
		Batalla batalla = new Batalla();
		new Thread(batalla).run();
	}

	public void run() {
		RobocodeEngine engine = new RobocodeEngine(new java.io.File(installPath)); // buscar
																					// robots
																					// en
																					// el
																					// directorio
																					// de
																					// instalacion

		// engine.addBattleListener(new BattleObserver());
		// Setup the battle specification

		BattlefieldSpecification battlefield = new BattlefieldSpecification(800, 600); // 800x600

		RobotSpecification[] selectedRobots = null;
		if (enemiesSelected != null && enemiesSelected.split(",").length > 1) {
			selectedRobots = engine.getLocalRepository(enemiesSelected);
		} else
			selectedRobots = engine.getLocalRepository(robotName + "*");

		// prueba para enviar 5 batallas de 1 round por si se quieren cambiar
		// parámetros..
		// roundsNumber=1;
		BattleSpecification battleSpec = null;
		if (mode.equals(Training.getInstance().toString()))
			battleSpec = new BattleSpecification(roundsNumber, battlefield, selectedRobots);
		else 
			battleSpec = new BattleSpecification(1, battlefield, selectedRobots);
		// Show the Robocode battle view
		engine.setVisible(true);
		// Run our specified battle and let it run till it is over

		// TODO Como segundo parámetro se le pueden pasar las posiciones
		// iniciales separadas por coma.
		// Tres valores por robot.
		if (initialPosition != null) {
			String[] initPosParts = initialPosition.split("-");
			if (mode.equals(Competition.getInstance().toString())){
				for (int i = 0; i < roundsNumber; i++) {
					System.out.println("posiciones iniciales..." + initPosParts[i]);
					engine.runBattle(battleSpec, initPosParts[i], true);
				}

			} else {
				engine.runBattle(battleSpec, initPosParts[0], true);
			}
		}

		else
			engine.runBattle(battleSpec, true);
		// Cleanup our RobocodeEngine
		// TODO No finalizar la ventana, tratar de que quede con un dialog
		// chico.
		engine.close();

		// Make sure that the Java VM is shut down properly
		System.exit(0);

	}

}

//
// Our private battle listener for handling the battle event we are interested
// in.
//

class BattleObserver extends BattleAdaptor {

	// Called when the battle is completed successfully with battle results
	public void onBattleCompleted(BattleCompletedEvent event) {
		// System.out.println("-- Battle has completed --");
		//
		// // Print out the sorted results with the robot names
		// System.out.println("Battle results:");
		// for (robocode.BattleResults result : e.getSortedResults()) {
		// System.out.println("  " + result.getTeamLeaderName() + ": "
		// + result.getScore());
		// }

		final ResultsDialog dialog = Container.getComponent(ResultsDialog.class);

		dialog.setup(event.getSortedResults(), event.getBattleRules().getNumRounds());
		// System.exit(0);
		// packCenterShow(dialog, true);

	}

	// // Called when the game sends out an information message during the
	// battle
	// public void onBattleMessage(BattleMessageEvent e) {
	// System.out.println("Msg> " + e.getMessage());
	// }
	//
	// // Called when the game sends out an error message during the battle
	// public
	// public void onBattleError(BattleErrorEvent e) {
	// System.out.println("Err> " + e.getError());
	// }
}
