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
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Collection;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import mode.Competition;
import rita.battle.RobotsToBattle;
import rita.battle.ZIPRobotProccessor;
import rita.net.NetworkConnection;
import rita.net.Protocol;
import rita.settings.HelperEditor;
import rita.settings.Language;
import rita.util.RandomSelectorPosition;
import rita.util.RobotWithPositionTemp;
import rita.widget.SourceCode;
import codeblockutil.Sound;
import codeblockutil.SoundManager;

/**
 * Ventana modal que muestra el listado de robots disponibles y da la opción de
 * ejecutar la batalla.
 */
public class DialogStartBattle extends CloseableJDialog {
	private static final long serialVersionUID = -6477626952671868293L;

	private String groupText = Language.get("grupo");
	private String competidoresText = Language.get("compitencon");
/*	private JComboBox<String> groupList = new JComboBox<String>(NetworkConnection.grupos);
*/	private JLabel robotsCompLabel = new JLabel(competidoresText + " " + HelperEditor.currentRobotName + " (sin Grupo):");
	private static JLabel jlMessages = new JLabel(" " );
	

	private static DefaultListModel<String> robotsModel = new DefaultListModel<String>();
	private static JList<String> robotsList = new JList<String>(robotsModel);
	private JScrollPane scrollPaneRobots = new JScrollPane(robotsList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

	private static JButton buttonStart = null;
	// private StringBuilder robots = new StringBuilder("");
	private Dialog theDialog = null;
	private static Collection<File> compiledFiles = null;
	private static RobotWithPositionTemp myRobotTemp = null;
	private int MAX_BATTLE = 3;
	
	private ActionListener listenerBotonNumber = new ListenerBotonNumber();
	
	private JToggleButton tButton1= new JToggleButton("1");
	private JToggleButton tButton2= new JToggleButton("2");
	private JToggleButton tButton3= new JToggleButton("3");
	private JToggleButton tButton4= new JToggleButton("4");
	private JToggleButton tButton5= new JToggleButton("5");
	private JToggleButton tButton6= new JToggleButton("6");
	private JToggleButton tButton7= new JToggleButton("7");
	private JToggleButton tButton8= new JToggleButton("8");
	private JToggleButton tButton9= new JToggleButton("9");
	private JToggleButton tButton10= new JToggleButton("10");

	
	private static Sound clickButtonSound = SoundManager.loadSound("/sound/button.wav");

	//panel superior
	final JPanel groupPanel = new JPanel();
	final JPanel buttonNumberPanel = new JPanel();

	public DialogStartBattle(JFrame parent, final Collection<File> inFiles) {
		super(parent);
		this.setResizable(true);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setLayout(new BorderLayout());
		this.setTitle(Language.get("acompetir"));
		compiledFiles = inFiles;
		JLabel groupLabel = new JLabel(groupText);
		JButton groupButton = new JButton(Language.get("elegir"));
		jlMessages.setText("");
		jlMessages.setSize(200, 30);
		jlMessages.setForeground(Color.CYAN);
		
		
		groupLabel.setFont(groupLabel.getFont().deriveFont(Font.BOLD, 14));

		
		configureButtons();
		
		
		
		
		groupPanel.setLayout(new BorderLayout());
		groupPanel.add(groupLabel, BorderLayout.NORTH);
		groupPanel.add(buttonNumberPanel, BorderLayout.CENTER);
		
		
		final JPanel northPanel = new JPanel();
		northPanel.setLayout(new BorderLayout());
		northPanel.add(jlMessages, BorderLayout.NORTH);
		northPanel.add(groupPanel, BorderLayout.CENTER);
		
		// lo guardo para poder referenciarlo desde el listener
		theDialog = this;

		// este botón debería habilitarse sólo si hay más de 1 competidor.
		buttonStart = new JButton(Language.get("startB"));
		buttonStart.setEnabled(false);
		buttonStart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					NetworkConnection.sendMessage(ZIPRobotProccessor.PREFIX_MSG + HelperEditor.currentRobotName + " ha comenzado la batalla!");
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				
				startBattle();
				
				RobotsToBattle.clearList();
				robotsModel.clear();

				NetworkConnection.disconnectAndClose();
				
				
				
			}
		});

		//panel del medio
		final JPanel robotsPanel = new JPanel(new BorderLayout());
		scrollPaneRobots.setSize(150, 300);
		robotsPanel.add(robotsCompLabel, BorderLayout.NORTH);
		robotsPanel.add(scrollPaneRobots, BorderLayout.CENTER);
		
		//panel inferior
		final JPanel lastPanel = new JPanel();
		lastPanel.add(buttonStart);

		if (NetworkConnection.isSelectedGroup()) {
			/*groupList.setSelectedItem(NetworkConnection.getSelectedGroup());
			groupList.setEnabled(false);*/
			groupButton.setText(Language.get("cambiar"));
			if (robotsModel.size()>0){
				buttonStart.setEnabled(true);
			} else buttonStart.setEnabled(false);
		} 
		/*else {
			robotsPanel.setVisible(false);
		}*/
		/*groupButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (((JButton) e.getSource()).getText().equals(Language.get("elegir"))) {

					// si es elegir asocia al grupo seleccionado
					groupList.setEnabled(false);

					myRobotTemp = RandomSelectorPosition.findPosition(HelperEditor.currentRobotName,
							HelperEditor.currentRobotPackage, (int) System.currentTimeMillis() % 10000);

					try {
						Protocol protocol = null;
						if (NetworkConnection.isSelectedGroup()) {
							//si ya estaba en un grupo, tengo que avisar primero que me desconecté de este grupo
							protocol = Protocol.REMOVE;
							RobotsToBattle.clearList();
							robotsModel.clear();
							String zipFilename = ZIPRobotProccessor.zipToSend(protocol, myRobotTemp, null);
							NetworkConnection.sendRobot(zipFilename);
							//Thread.sleep(5000);
						} 
						protocol = Protocol.ADD;
						RobotsToBattle.addRobotToTheList(myRobotTemp);
						
						 * Si ya hay un grupo seteado...... -indicar en el
						 * archivo de datos remove: nombre del Robot (para
						 * primero avisar al grupo anterior que debe eliminar al
						 * robot Isa) ...además...si los nombres de los robots
						 * tuvieran checks...podría dar la posibilidad de
						 * eliminar alguno de la batalla a mano Sino el
						 * protocolo sera join: y todos los datos del robot
						 
						NetworkConnection.setMulticastGroup((String) groupList.getSelectedItem());

						String zipFilename = ZIPRobotProccessor.zipToSend(protocol, myRobotTemp, inFiles);

						
						 * NetworkConnection.setMulticastGroup((String)groupList.
						 * getSelectedItem());
						 
						//NetworkConnection.start((String) groupList.getSelectedItem());
						
						NetworkConnection.sendRobot(zipFilename);
					
					} catch (IOException e1) {
						JOptionPane opts = new JOptionPane(Language.get("error.proccess"));
						e1.printStackTrace();
					} catch (Exception e1) {
						// para los errores de red
						JOptionPane opts = new JOptionPane(Language.get("error.net"));
						e1.printStackTrace();
					}

					((JButton) e.getSource()).setText(Language.get("cambiar"));
					robotsPanel.setVisible(true);
					PositionCalc.centerDialog(theDialog);
					pack();
				} else {
					// si el boton es para cambiar de grupo entonces
					groupList.setEnabled(true);
					((JButton) e.getSource()).setText(Language.get("elegir"));

				}

			}
		});*/

		robotsCompLabel.setBorder(new EmptyBorder(12, 12, 12, 12));
		robotsCompLabel.setFont(robotsCompLabel.getFont().deriveFont(Font.BOLD, 14));
		robotsCompLabel.setIcon(new ImageIcon(DialogStartBattle.class.getResource("/images/icons/rita.png")));
		robotsCompLabel.setHorizontalAlignment(SwingConstants.CENTER);

		for (RobotWithPositionTemp r : RobotsToBattle.getInstance().getListRobotsToBattle()) {
			if (!robotsModel.contains(r.getRobotName()) && !r.getRobotName().equals(HelperEditor.currentRobotName))
				robotsModel.addElement(r.getRobotName());
		}
		
		
		
		
		
		this.addWindowListener(new WindowAdapter() 
		{
		  /*public void windowClosed(WindowEvent e)
		  {
		    System.out.println("jdialog window closed event received");
		  }*/

		  public void windowClosing(WindowEvent e)
		  {
		    NetworkConnection.disconnectAndClose();
		  }
		});

		this.getContentPane().add(northPanel, BorderLayout.NORTH);
		this.getContentPane().add(robotsPanel, BorderLayout.CENTER);
		this.getContentPane().add(lastPanel, BorderLayout.SOUTH);
		this.pack();
		this.toFront();
		PositionCalc.centerDialog(this);
		this.setVisible(true);
	}

	private void configureButtons() {
		tButton1.setBackground(Color.decode("#FDFD96"));
		tButton2.setBackground(Color.decode("#FFB347"));
		tButton3.setBackground(Color.decode("#FF6961"));
		tButton4.setBackground(Color.decode("#F49AC2"));
		tButton5.setBackground(Color.decode("#9F00FF"));
		tButton6.setBackground(Color.decode("#779ECB"));
		tButton7.setBackground(Color.decode("#77DD77"));
		tButton8.setBackground(Color.decode("#002366"));
		tButton9.setBackground(Color.decode("#836953"));
		tButton10.setBackground(Color.WHITE);
				
		
		tButton1.addActionListener(listenerBotonNumber);
		tButton2.addActionListener(listenerBotonNumber);
		tButton3.addActionListener(listenerBotonNumber);
		tButton4.addActionListener(listenerBotonNumber);
		tButton5.addActionListener(listenerBotonNumber);
		tButton6.addActionListener(listenerBotonNumber);
		tButton7.addActionListener(listenerBotonNumber);
		tButton8.addActionListener(listenerBotonNumber);
		tButton9.addActionListener(listenerBotonNumber);
		tButton10.addActionListener(listenerBotonNumber);
		
		tButton1.setSize(50, 20);
		tButton2.setSize(50, 20);
		tButton3.setSize(50, 20);
		tButton4.setSize(50, 20);
		tButton5.setSize(50, 20);
		tButton6.setSize(50, 20);
		tButton7.setSize(50, 20);
		tButton8.setSize(50, 20);
		tButton9.setSize(50, 20);
		tButton10.setSize(50, 20);
		
		
		
		buttonNumberPanel.setLayout(new GridLayout(2, 5));
		buttonNumberPanel.add(tButton1);
		buttonNumberPanel.add(tButton2);
		buttonNumberPanel.add(tButton3);
		buttonNumberPanel.add(tButton4);
		buttonNumberPanel.add(tButton5);
		buttonNumberPanel.add(tButton6);
		buttonNumberPanel.add(tButton7);
		buttonNumberPanel.add(tButton8);
		buttonNumberPanel.add(tButton9);
		buttonNumberPanel.add(tButton10);
	}

	private void startBattle() {
		StringBuilder selectedRobots = new StringBuilder();
		// el robot del usuario siempre participa en la batalla
		/*
		 * selectedRobots.append(HelperEditor.currentRobotPackage + "." +
		 * HelperEditor.currentRobotName); selectedRobots.append("*,");
		 */
		String robotName;
		// recorrer los checkboxes de los enemigos seleccionados
		StringBuilder initialPositions = new StringBuilder("");

		for (RobotWithPositionTemp rTemp : RobotsToBattle.getInstance().getListRobotsToBattle()) {
			robotName = rTemp.getRobotName();
			selectedRobots.append(rTemp.getSelectedPackage() + "." + robotName);
			if (robotName.contains("sample.")) {
				selectedRobots.append(',');
			} else {
				selectedRobots.append("*,");
			}
			positionAsStringBuilder(initialPositions, rTemp);

		}

		if (initialPositions.length() > 1) {
			initialPositions.deleteCharAt(initialPositions.length() - 1); // borra
																			// la
																			// ultima
																			// coma
		}

		closeDialog();
		HelperEditor.setEnemies(selectedRobots.toString());

		for (int i = 0; i < MAX_BATTLE-1; i++) {
			int[] factors = { Calendar.DAY_OF_WEEK, Calendar.DAY_OF_YEAR, Calendar.YEAR };
			initialPositions.append("-");
			initialPositions.append(recalculate(factors[i]));

		}
		SourceCode.callBatalla(MAX_BATTLE, initialPositions.toString(), Competition.getInstance());

	}

	private StringBuilder recalculate(int factorParam) {
		int factor = Calendar.getInstance().get(factorParam);
		StringBuilder initialPositions = new StringBuilder();
		for (RobotWithPositionTemp rTemp : RobotsToBattle.getInstance().getListRobotsToBattle()) {
			positionAsStringBuilder(initialPositions,
					RandomSelectorPosition.findPosition(rTemp.getRobotName(), rTemp.getSelectedPackage(), factor));

		}
		return initialPositions;
	}

	private void positionAsStringBuilder(StringBuilder initialPositions, RobotWithPositionTemp rt) {
		initialPositions.append("(" + rt.getX() + ",");
		initialPositions.append(rt.getY() + ",");
		initialPositions.append(rt.getOrientation() + "),");
	}

	private void removeRobotFromGroup() throws IOException, Exception {
		Protocol protocol;
		myRobotTemp= new RobotWithPositionTemp(HelperEditor.currentRobotName, 0, 0, 0, HelperEditor.currentRobotPackage);
		//si ya estaba en un grupo, tengo que avisar primero que me desconecté de este grupo
		protocol = Protocol.REMOVE;
		RobotsToBattle.clearList();
		robotsModel.clear();
		String zipFilename = ZIPRobotProccessor.zipToSend(protocol, myRobotTemp, null);
		NetworkConnection.sendRobot(zipFilename);
		//Thread.sleep(5000);
	}

	private void addRobotToGroup(String selectedText) throws IOException, Exception {
		Protocol protocol;
		myRobotTemp = RandomSelectorPosition.findPosition(HelperEditor.currentRobotName,
				HelperEditor.currentRobotPackage, (int) System.currentTimeMillis() % 10000);

		protocol = Protocol.ADD;
		RobotsToBattle.addRobotToTheList(myRobotTemp);
		/*
		 * Si ya hay un grupo seteado...... -indicar en el
		 * archivo de datos remove: nombre del Robot (para
		 * primero avisar al grupo anterior que debe eliminar al
		 * robot Isa) ...además...si los nombres de los robots
		 * tuvieran checks...podría dar la posibilidad de
		 * eliminar alguno de la batalla a mano Sino el
		 * protocolo sera join: y todos los datos del robot
		 */
		NetworkConnection.setMulticastGroup(selectedText);

		String zipFilename = ZIPRobotProccessor.zipToSend(protocol, myRobotTemp, compiledFiles);

		/*
		 * NetworkConnection.setMulticastGroup((String)groupList.
		 * getSelectedItem());
		 */
		//NetworkConnection.start((String) groupList.getSelectedItem());
		
		NetworkConnection.sendRobot(zipFilename);
	}

	public static void updateRobot(Protocol protocol, String nombreRobotRcv) {
		if (protocol == Protocol.ADD || protocol == Protocol.UPDATE) {
			if (!robotsModel.contains(nombreRobotRcv))
				robotsModel.addElement(nombreRobotRcv);
			if (protocol == Protocol.ADD) {
				// solo si recibí un ADD vuelvo a enviar los datos de mi robot
				// para que el nuevo en el grupo se actualice
				String zipFilename;
				try {
					zipFilename = ZIPRobotProccessor.zipToSend(Protocol.UPDATE, myRobotTemp, compiledFiles);
					NetworkConnection.sendRobot(zipFilename);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			buttonStart.setEnabled(true);

		}
		if (protocol == Protocol.REMOVE) {
			if (robotsModel.removeElement(nombreRobotRcv))
				System.out.println("SE ELIMINO A:" + nombreRobotRcv);
			if (robotsModel.isEmpty())
				buttonStart.setEnabled(false);

		}

	}

	public static void updateMessage(String data) {
		jlMessages.setText(data);
	}
	
	
	
	private class ListenerBotonNumber implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			JToggleButton jtBut=(JToggleButton) e.getSource();
			if (jtBut.isSelected()) {
				System.out.println("boton seleccionado!!!!");
				
				//deselecciono el resto de los botones de numero de grupo
				String selectedText=jtBut.getText();
				JToggleButton jtButTemp=null;
				for (Component jc: buttonNumberPanel.getComponents()){
					jtButTemp=((JToggleButton)jc);
					if (!jtButTemp.getText().equals(selectedText)){
						jtButTemp.setSelected(false);
					}
					
				}
				jlMessages.setText("");
				clickButtonSound.play();

				String textTemp=robotsCompLabel.getText();
				robotsCompLabel.setText(textTemp.substring(0, textTemp.indexOf("(")-1) + " (Grupo " + selectedText + ") :" );

				try {
					if (NetworkConnection.isSelectedGroup()) {
						removeRobotFromGroup();
					} 
					
					addRobotToGroup(selectedText);
				
				} catch (IOException e1) {
					JOptionPane opts = new JOptionPane(Language.get("error.proccess"));
					e1.printStackTrace();
				} catch (Exception e1) {
					// para los errores de red
					JOptionPane opts = new JOptionPane(Language.get("error.net"));
					e1.printStackTrace();
				}

				
				PositionCalc.centerDialog(theDialog);
				pack();
			} else {
				// si el boton es para cambiar de grupo entonces
				System.out.println("boton deseleccionado!!!!");
				String textTemp=robotsCompLabel.getText();
				robotsCompLabel.setText(textTemp.substring(0, textTemp.indexOf("(")-1) + " (sin Grupo) :" );

					try {
						removeRobotFromGroup();
					} catch (IOException e1) {
						e1.printStackTrace();
					} catch (Exception e1) {

						e1.printStackTrace();
					}

				NetworkConnection.disconnectAndClose();
				

			}

		}
	};

	
	
	

}
