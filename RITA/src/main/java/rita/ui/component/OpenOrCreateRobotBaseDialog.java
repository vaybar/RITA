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

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import rita.settings.Language;
import rita.settings.Settings;
import rita.util.RitaUtilities;
import controller.WorkspaceController;

public abstract class OpenOrCreateRobotBaseDialog extends CloseableJDialog {

	private static final long serialVersionUID = 8844864576318736467L;

	protected abstract JTextField getRobotNameField();
	protected abstract JButton getCreateRobotButton();
	protected abstract WorkspaceController getWorkspaceController(); 

	protected abstract void whenRobotNameAlreadyTakenError();
	protected abstract void clearError();
	protected abstract boolean isErrorVisible();
	
	JRadioButton juniorButton   = new JRadioButton(Language.get("Junior")  , true);
	JRadioButton advancedButton    = new JRadioButton(Language.get("Advanced")   , false);
	
	ButtonGroup bgroup = new ButtonGroup();

	public OpenOrCreateRobotBaseDialog() {
		super();
	}

	public OpenOrCreateRobotBaseDialog(Dialog owner) {
		super(owner);
	}
	
	public OpenOrCreateRobotBaseDialog(Dialog owner, boolean modal) {
		super(owner, modal);
	}
	
	public OpenOrCreateRobotBaseDialog(Dialog owner, String title) {
		super(owner, title);
	}

	public OpenOrCreateRobotBaseDialog(Dialog owner, String title, boolean modal) {
		super(owner, title, modal);
	}

	public OpenOrCreateRobotBaseDialog(Dialog owner, String title, boolean modal, GraphicsConfiguration gc) {
		super(owner, title, modal, gc);
	}

	public OpenOrCreateRobotBaseDialog(Frame parent) {
		super(parent);
	}

	public OpenOrCreateRobotBaseDialog(Frame parent, boolean modal) {
		super(parent, modal);
	}

	public OpenOrCreateRobotBaseDialog(Frame parent, String title, boolean modal) {
		super(parent, title, modal);
	}

	public OpenOrCreateRobotBaseDialog(Frame parent, String title, boolean modal, GraphicsConfiguration gc) {
		super(parent, title, modal, gc);
	}

	public OpenOrCreateRobotBaseDialog(Window parent) {
		super(parent);
	}

	public OpenOrCreateRobotBaseDialog(Window parent, Dialog.ModalityType modalityType) {
		super(parent, modalityType);
	}

	public OpenOrCreateRobotBaseDialog(Window parent, String title, Dialog.ModalityType modalityType) {
		super(parent, title, modalityType);
	}

	public OpenOrCreateRobotBaseDialog(Window parent, String title, Dialog.ModalityType modalityType, GraphicsConfiguration gc) {
		super(parent, title, modalityType, gc);
	}

	protected void attachEventHandlers() {
		getRobotNameField().addKeyListener(new NewRobotNameKeyListener());
		getCreateRobotButton().addActionListener(new NewRobotNameButtonListener());
	}

	
	protected class NewRobotNameKeyListener extends KeyAdapter {
		private static final int ASCII_BACKSPACE = 8;
		final JTextField robotNameField = getRobotNameField();
		
		@Override
		public void keyReleased(KeyEvent keyEvt) {
			final JTextField robotNameField = getRobotNameField();
			final JButton createRobotButton = getCreateRobotButton();
			if(isErrorVisible()) {
				clearError();
			}
			// hay letras en el campo del nombre de robot? sin nombre no se puede crear el robot
			if(!robotNameField.getText().trim().isEmpty()) {
				if(!createRobotButton.isEnabled()) {
					createRobotButton.setEnabled(true);
				}
				// tecla enter == apretar "crear robot"
				if(keyEvt.getKeyCode()==KeyEvent.VK_ENTER) {
					createRobot();
				}
			} else {
				if(createRobotButton.isEnabled()) {
					createRobotButton.setEnabled(false);
				}
			}

		}

		@Override
		public void keyTyped(KeyEvent evt) {
			 // controlar que las teclas presionadas que son permitidas son solo aquellas son validas para el nombre de un robot
			 int kc = evt.getKeyCode();
			 if(kc!=KeyEvent.VK_ENTER && kc!=KeyEvent.VK_DELETE && kc!=KeyEvent.VK_BACK_SPACE && kc!=KeyEvent.VK_SHIFT && kc!=KeyEvent.VK_CAPS_LOCK && kc!=KeyEvent.VK_ALT_GRAPH && kc!=KeyEvent.VK_BEGIN && kc!=KeyEvent.VK_END && kc!=KeyEvent.VK_INSERT && kc!=KeyEvent.VK_META) {
				 char c = evt.getKeyChar();
				 // ascii backspace  porque (al menos en windows) backspace genera keycode==0, asi que no puede ser detectada por con getKeyCode()
				 if(c!=ASCII_BACKSPACE && !RitaUtilities.isValidCharForRobotName(c)) {
				        getToolkit().beep();
				        // desaparece el evento
				        evt.consume();
				 } else {

					 if(robotNameField.getText().isEmpty()) {
						 // la 1ra letra del nombre del robot va siempre en mayusculas...
						 evt.setKeyChar(Character.toUpperCase(c));
					 } else {
						 // ... el resto del nombre del robot en minusculas
						 evt.setKeyChar(Character.toLowerCase(c));					 
					 }
				 }
			 }
		}

	}

	protected class NewRobotNameButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent evt) {
			createRobot();
		}
	}

	/*
	 * Este método crea el robot en caso que el nombre del robot no haya sido
	 * ingresado previamente o que el usuario quiera sobreescribirlo (en
	 * realidad lo sobreescribirá cuando haga "Guardar" o
	 * "Compilar y Ejecutar"). Además refresca el workspace con un robot nuevo.
	 */
	protected void createRobot() {
		boolean confirm = true;
		final JTextField robotNameField = getRobotNameField();
		if (fileAlreadyExists(robotNameField.getText())) {
			confirm = RitaUtilities.confirmWindow(
					Language.get("confirm.title"), Language.get("confirm.nameAlreadyExists"));

		}
		if (confirm) {
			//EN VEZ DE currentRobotType debe mandar el texto del selectbutton
			WorkspaceController.enableWorkspace(getWorkspaceController(),
					this.getRobotType(),
					robotNameField.getText(), null,null);
			this.closeDialog();
		}
	}

	 /** Este metodo verifica que el nombre ingresado no exista, y espera
	 * confirmación por el usuario
	 */
	protected final boolean fileAlreadyExists(String robotName) {
		final String nameWithoutExt = Settings.getInstallPath()
				+ File.separator + "robots" + File.separator
				+ Settings.getProperty("defaultpackage") + File.separator
				+ robotName;
		String fullNameSource = nameWithoutExt + ".java";
		String fullNameClass = nameWithoutExt + ".class";
		return (new File(fullNameSource).exists() || new File(fullNameClass).exists());
	}

	/** Returns an ImageIcon, or null if the path was invalid. */
	protected static ImageIcon createImageIcon(String path) {
		java.net.URL imgURL = DialogNewRobot.class.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			System.err.println(Language.get("robot.error.fileNotFound") + " " + path);
			return null;
		}
	}
	
	protected JRadioButton getJuniorButton() {
		return juniorButton;
	}
	
	protected JRadioButton getAdvancedButton() {
		return advancedButton;
	}
	
	public String getRobotType(){
		if(juniorButton.isSelected()){
			return "junior";
		}
		else{
			return "advanced";
		}
	}
	

}
