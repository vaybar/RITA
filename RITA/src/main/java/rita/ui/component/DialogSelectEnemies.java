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

package rita.ui.component;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import mode.Training;

import org.apache.commons.io.FileUtils;

import rita.battle.Batalla;
import rita.settings.HelperEditor;
import rita.settings.Language;
import rita.settings.Settings;
import rita.ui.component.exception.NoEnemiesException;
import rita.util.RobotWithPositionTemp;
import rita.widget.SourceCode;

/**
 * Esta clase representa la ventana de di�logo que permite seleccionar
 * mediante checkbox los robots que se enfrentar�n en el campo de batalla.
 * 
 * @author Vanessa Aybar Rosales
 * */
public class DialogSelectEnemies extends CloseableJDialog {
	private static final long serialVersionUID = -3463604350524085848L;

	private static final String pattern = Pattern.quote(System
			.getProperty("file.separator"));
	private JCheckBox randomMode;
	private RobotNameClickedEvent robotNameClicked;
	private JPanel panelSelectRobots;
	private JButton btnOK;

	private JSpinner roundsNumberSpinner;
	// posicion de mi robot
	private JButton buttonPos;
	private ImageIcon icon;
	private static Map<String, RobotWithPositionTemp> mapRobotsTemp = new HashMap<String, RobotWithPositionTemp>();

	private static DialogSelectEnemies dialogSelectEnemies = null;

	private static boolean canSelectPosition = false;

	private static List<Component> positionComponents = new ArrayList<Component>();

	public static void clean(){
		dialogSelectEnemies = null;
		mapRobotsTemp=new HashMap<String, RobotWithPositionTemp>();
		canSelectPosition=false;
		positionComponents= new ArrayList<Component>();
	}
	
	private class RobotNameClickedEvent implements ActionListener {
		JButton okButton;

		public RobotNameClickedEvent(JButton disableEnableBtn) {
			okButton = disableEnableBtn;
		}

		int selectionCount = 0;

		@Override
		public void actionPerformed(ActionEvent evt) {
			JCheckBox check = (JCheckBox) evt.getSource();
			if (check.isSelected()) {
				++selectionCount;
				if (!okButton.isEnabled()) {
					okButton.setEnabled(true);
				}
				if (canSelectPosition)
					((JButton) ((JPanel) check.getParent()).getComponent(1))
							.setEnabled(true);
			} else {
				--selectionCount;
				if (selectionCount == 0) {
					okButton.setEnabled(false);
				}
				((JButton) ((JPanel) check.getParent()).getComponent(1))
						.setEnabled(false);
			}
		}

	}

	/**
	 * Create the application.
	 */
	private DialogSelectEnemies() throws NoEnemiesException {
		initialize();
	}

	public static DialogSelectEnemies getInstance() throws NoEnemiesException {
		if (dialogSelectEnemies == null)
			dialogSelectEnemies = new DialogSelectEnemies();
		else
			dialogSelectEnemies.setVisible(true);
		return dialogSelectEnemies;
	}

	/**
	 * Initialize the contents of the frame.
	 * 
	 * @throws NoEnemiesException
	 */
	private void initialize() throws NoEnemiesException {
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.getContentPane().setLayout(new BorderLayout(6, 6));
		this.setTitle(Language.get("selectEnemies"));
		randomMode = new JCheckBox(Language.get("robotsBattleMode.random"));
		randomMode.setSelected(true);
		randomMode.addActionListener(new SelectUnselectPositionAction());

		icon = new ImageIcon(
				DialogSelectEnemies.class
						.getResource("/images/icons/target.jpg"));
		

		JPanel panelRobots = new JPanel();
		panelRobots.setBorder(new EmptyBorder(0, 12, 12, 0));
		panelRobots.setLayout(new BorderLayout(0, 0));

		JPanel panelSelectAllNone = new JPanel();
		panelSelectAllNone.setBorder(new EmptyBorder(0, 12, 0, 12));
		panelRobots.add(panelSelectAllNone, BorderLayout.EAST);
		this.getContentPane().add(panelRobots, BorderLayout.CENTER);
		
		GridBagLayout gbl_panelSelectAllNone = new GridBagLayout();
		gbl_panelSelectAllNone.columnWidths = new int[] { 0, 0 };
		gbl_panelSelectAllNone.rowHeights = new int[] { 0, 0, 8 };
		gbl_panelSelectAllNone.columnWeights = new double[] { 0.0,
				Double.MIN_VALUE };
		gbl_panelSelectAllNone.rowWeights = new double[] { 0.0, 0.0,
				Double.MIN_VALUE };
		panelSelectAllNone.setLayout(gbl_panelSelectAllNone);

		JButton btnSelectAll = new JButton(Language.get("selectAllEnemies"));
		GridBagConstraints gbc_btnSelectAll = new GridBagConstraints();
		gbc_btnSelectAll.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnSelectAll.insets = new Insets(0, 0, 5, 0);
		gbc_btnSelectAll.gridx = 0;
		gbc_btnSelectAll.gridy = 0;
		panelSelectAllNone.add(btnSelectAll, gbc_btnSelectAll);

		JButton btnSelectNone = new JButton(Language.get("selectNoEnemies"));
		GridBagConstraints gbc_btnSelectNone = new GridBagConstraints();
		gbc_btnSelectNone.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnSelectNone.gridx = 0;
		gbc_btnSelectNone.gridy = 1;
		panelSelectAllNone.add(btnSelectNone, gbc_btnSelectNone);

		/*
		 * btnInitialPositionsConfig = new
		 * JButton(Language.get("selectRobotsInitialPositions"));
		 * GridBagConstraints gbc_btnInitialPositionsConfig = new
		 * GridBagConstraints(); gbc_btnSelectNone.fill =
		 * GridBagConstraints.HORIZONTAL; gbc_btnSelectNone.gridx = 0;
		 * gbc_btnSelectNone.gridy = 1;
		 * panelSelectAllNone.add(btnInitialPositionsConfig,
		 * gbc_btnInitialPositionsConfig);
		 * btnInitialPositionsConfig.addActionListener(new ActionListener() {
		 * public void actionPerformed(ActionEvent evt) {
		 * selectRobotsInitialPositions(); } });
		 */

		JLabel label = new JLabel(
				String.format(Language.get("youtRobotAgainst"),
						HelperEditor.currentRobotName));
		label.setBorder(new EmptyBorder(12, 12, 0, 12));
		label.setIcon(new ImageIcon(DialogSelectEnemies.class
				.getResource("/images/icons/compite2.png")));
		label.setVerticalAlignment(SwingConstants.TOP);
		label.setFont(label.getFont().deriveFont(Font.BOLD, 14));

		
		panelSelectRobots = new JPanel();
		panelSelectRobots.setLayout(new GridLayout(0, 2));
		panelSelectRobots.setAutoscrolls(true);

		
		panelRobots.add(label,BorderLayout.NORTH);
		panelRobots.add(panelSelectRobots, BorderLayout.CENTER);

		// posicion de mi robot
		JPanel panelSetMyRobotPosition = new JPanel();
		// panelRobots.add(panelSetMyRobotPosition, BorderLayout.PAGE_END);
		panelSetMyRobotPosition.setLayout(new FlowLayout());
		JLabel ownPosition=new JLabel(Language.get("myRobotPosition")
				+ " " + HelperEditor.currentRobotName);
		ownPosition.setFont(new Font("sansserif", Font.BOLD, 12));
		panelSetMyRobotPosition.add(ownPosition);
		// Posicion de mi robot
		buttonPos = new JButton(icon);
		buttonPos.addActionListener(new PosicionRobotAction(
				HelperEditor.currentRobotName)); // el nombre aqui es solo a
													// modo informativo
		buttonPos.setPreferredSize(new Dimension(30, 30));
		panelSetMyRobotPosition.add(buttonPos);
		positionComponents.add(buttonPos);

		// Matías
		JPanel panelSelectRoundsNumber = new JPanel();
		// panelRobots.add(panelSelectRoundsNumber, BorderLayout.SOUTH);
		panelSelectRoundsNumber.setLayout(new FlowLayout());
		panelSelectRoundsNumber.add(new JLabel(Language
				.get("selectRoundsNumber")));
//		this.roundsNumberTextField = new JTextField(
//				Integer.toString(Batalla.NUMBER_OF_ROUNDS), 5);
		this.roundsNumberSpinner = new JSpinner(new SpinnerNumberModel(Batalla.NUMBER_OF_ROUNDS, Batalla.MIN_NUMBER_OF_ROUNDS, Batalla.MAX_NUMBER_OF_ROUNDS, 1));
		

		panelSelectRoundsNumber.add(this.roundsNumberSpinner);

		JPanel panelSouth = new JPanel();
		panelSouth.setLayout(new GridLayout(4, 1));
		
		panelSouth.add(panelSetMyRobotPosition);
		//this.getContentPane().add(randomMode, BorderLayout.SOUTH);
		panelSouth.add(randomMode);
		panelSouth.add(new JSeparator());
		panelSouth.add(panelSelectRoundsNumber);
		panelRobots.add(panelSouth, BorderLayout.SOUTH);

		JPanel panelOkCancel = new JPanel();
		this.getContentPane().add(panelOkCancel, BorderLayout.SOUTH);
		panelOkCancel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 12));

		btnOK = new JButton(Language.get("selectStartBattle"));
		btnOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				startBattle(panelSelectRobots);
			}
		});
		// comienza deshabilitado hasta que se seleccione 1 enemigo
		// btnOK.setEnabled(false);
		panelOkCancel.add(btnOK);

		JButton btnCancel = new JButton(Language.get("cancel"));
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				closeDialog();
			}
		});
		panelOkCancel.add(btnCancel);

		btnSelectAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				selectChecks(panelSelectRobots, true);
				// btnOK.setEnabled(true);
			}
		});

		btnSelectNone.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				selectChecks(panelSelectRobots, false);
				// btnOK.setEnabled(false);
			}
		});

		// TODO Agregar funcionalidad de elegir las posiciones iniciales de los
		// robots.
		// TODO Averiguar como pasar las posiciones para que se elijan de manera
		// random, probar (0,0,0)

		try {
			robotNameClicked = new RobotNameClickedEvent(btnOK);
			addRobotsToPanel(panelSelectRobots);

			this.setResizable(false);
			this.pack();
			PositionCalc.centerDialog(this);
			enablePositionOptions(false);
			this.setVisible(true);
		} catch (FileNotFoundException e) {
			throw new NoEnemiesException(e.getMessage());
		}

	}

	/**
	 * Recorre el directorio robots y agrega al panel a todos los robots
	 * (.class) instalados
	 * 
	 * @throws FileNotFoundException
	 *             si no hay .class en el dir File(Settings.getInstallPath(),
	 *             "robots")
	 */
	private void addRobotsToPanel(JPanel panel) throws FileNotFoundException {
		if (Settings.getInstallPath() == null) {
			throw new FileNotFoundException(
					"La carpeta de robots no pudo ser encontrara porque InstallPath no esta definido");
		}
		File robotDir = new File(Settings.getInstallPath(), "robots");
		if (!robotDir.isDirectory() || !robotDir.canRead()) {
			//otra carpeta posible MVN
			robotDir = new File(Settings.getInstallPath()+Settings.getMvnResourcesPath(), "robots");
			if (!robotDir.isDirectory() || !robotDir.canRead()) 
			throw new FileNotFoundException("La carpeta de robots " + robotDir
					+ " no existe o no tiene permisos de lectura");
		}
		boolean robotFound = false;
		// recorrer directorio robots y traer todos los robots instalados
		JCheckBox cb;
		JButton buttonPos;

		/*
		 * buscar recursivametne a todos los .class en InstallPath() + "robots",
		 * excluyendo a las inner clases, porque asumimos que son clases de
		 * implementacion. Las reconocemos porque tienen '$' en el nombre de la
		 * clase.
		 */

		for (File f : FileUtils.listFiles(robotDir, new String[] { "class" },
				true)) {
			if (f.getName().endsWith(".class")
					&& f.getName().indexOf('$') == -1) {
				String[] splitPath = f.getParent().split(pattern);
				/*
				 * si el path del robot tiene directorio/nombrerobot.class => el
				 * nombre del robot es "directorio.nombrerobot", sino es solo
				 * "nombrerobot"
				 */
				if (splitPath.length > 0) {
					cb = new JCheckBox(splitPath[splitPath.length - 1]
							+ "."
							+ f.getName().substring(0,
									f.getName().lastIndexOf('.')), false);
				} else {
					cb = new JCheckBox(f.getName().substring(0,
							f.getName().lastIndexOf('.')), false);
				}

				JPanel panelCheckButton = new JPanel();
				buttonPos = new JButton(icon);
				buttonPos.addActionListener(new PosicionRobotAction(cb
						.getText()));
				buttonPos.setPreferredSize(new Dimension(30, 30));
				positionComponents.add(buttonPos);

				cb.addActionListener(robotNameClicked);
				cb.setPreferredSize(new Dimension(160, 30));
				panelCheckButton.add(cb);

				panelCheckButton.add(buttonPos);
				panel.add(panelCheckButton);
				robotFound = true;
			}
		}
		if (!robotFound) {
			throw new FileNotFoundException(
					"La carpeta de robots no contiene robots");
		}
	}

	

	private class PosicionRobotAction implements ActionListener {

		RobotWithPositionTemp robotTemp;

		public PosicionRobotAction(String robotName) {
			if (mapRobotsTemp.get(robotName) == null) {
				robotTemp = new RobotWithPositionTemp(robotName, -1, -1,
						-1,null);
				mapRobotsTemp.put(robotName, robotTemp);
			}
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			new DialogPositionRobot(robotTemp);
		}

	}

	/** seleccionar o deseleccionar a todos los checkboxes adentro de un panel */
	private void selectChecks(JPanel panelChecks, boolean b) {
		for (Component comp : panelChecks.getComponents()) {
			if (comp instanceof JPanel){
				for (Component compInt : ((JPanel)comp).getComponents()) {
					if (compInt instanceof JCheckBox)
						((JCheckBox) compInt).setSelected(b);
				}
			}
		}

	}

	/**
	 * el usuario selecciono los robots para competir, encontrar cuales
	 * selecciono y comenzar la batalla
	 */
	private void startBattle(JPanel checks) {
		Integer roundsNumber;
		try {
			roundsNumber = (Integer)this.roundsNumberSpinner.getValue();
		} catch (NumberFormatException e) {
			roundsNumber = Batalla.NUMBER_OF_ROUNDS;
		}
		StringBuilder selectedRobots = new StringBuilder();
		// el robot del usuario siempre participa en la batalla
		selectedRobots.append(HelperEditor.currentRobotPackage + "."
				+ HelperEditor.currentRobotName);
		selectedRobots.append("*,");
		String robotName;
		// recorrer los checkboxes de los enemigos seleccionados
		StringBuilder initialPositions = new StringBuilder("");
		// posicion de mi robot
		RobotWithPositionTemp miRobotPos = mapRobotsTemp
				.get(HelperEditor.currentRobotName);
		positionAsStringBuilder(initialPositions, miRobotPos);

		RobotWithPositionTemp rt = null;
		for (Component compPanel : checks.getComponents()) {
			for (Component comp : ((JPanel) compPanel).getComponents()) {
				if (comp instanceof JCheckBox
						&& ((JCheckBox) comp).isSelected()) {
					robotName = ((JCheckBox) comp).getText();
					/*
					 * "sample" es el paquete de los que vienen con Robocode; si
					 * el robot no es de ese paquete, entonces el nombre debe
					 * estar seguido por un asterisco
					 */
					selectedRobots.append(robotName);
					if (robotName.contains("sample.")) {
						selectedRobots.append(',');
					} else {
						selectedRobots.append("*,");
					}
					rt = mapRobotsTemp.get(robotName);
					positionAsStringBuilder(initialPositions, rt);
				}
			}
		}
		if (initialPositions.length() > 1) {
			initialPositions.deleteCharAt(initialPositions.length() - 1); // borra
																			// la
																			// ultima
																			// coma

		}

		HelperEditor.setEnemies(selectedRobots.toString());
		closeDialog();
		SourceCode.callBatalla(Integer.valueOf(roundsNumber),
				initialPositions.toString(), Training.getInstance());
	}

	private void positionAsStringBuilder(StringBuilder initialPositions,
			RobotWithPositionTemp rt) {
		if (canSelectPosition) {
			if (rt.getX() >= 0)
				initialPositions.append("(" + rt.getX() + ",");
			else
				initialPositions.append("(?,");
			if (rt.getY() >= 0)
				initialPositions.append(rt.getY() + ",");
			else
				initialPositions.append("?,");
			if (rt.getOrientation() >= 0)
				initialPositions.append(rt.getOrientation() + "),");
			else
				initialPositions.append("?),");
		} else
			initialPositions.append("(?,?,?),");

	}

	private final class SelectUnselectPositionAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			JCheckBox check = (JCheckBox) e.getSource();
			if (check.isSelected()) {
				enablePositionOptions(false);
			} else {
				enablePositionOptions(true);
			}

		}

	}

	private void enablePositionOptions(boolean enable) {
		if (!enable) {
			canSelectPosition = false;
			for (Component comp : positionComponents) {
				comp.setEnabled(false);
			}
		} else {
			canSelectPosition = true;
			buttonPos.setEnabled(true);
			for (Component compPanel : panelSelectRobots.getComponents()) {
				JPanel parent = (JPanel) compPanel;
				// para cada panel la primer componente es el check y el segundo
				// es el boton
				JCheckBox check = (JCheckBox) parent.getComponent(0);
				if (check.isSelected()) {
					JButton button = (JButton) parent.getComponent(1);
					button.setEnabled(true);
				}
			}
		}
	}

	

}
