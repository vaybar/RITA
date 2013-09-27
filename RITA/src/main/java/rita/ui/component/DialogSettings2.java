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


import javax.swing.JFrame;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.JTextField;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.Font;
import javax.swing.JRadioButton;
import javax.swing.JButton;

import rita.settings.Language;
import rita.settings.Settings;
import rita.ui.component.MessageDialog.MessageType;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Enumeration;

public class DialogSettings2 extends CloseableJDialog {

	private static final long serialVersionUID = -9135168518021367720L;

	private JTextField pkgField;
	ButtonGroup enemyGroup;

	public DialogSettings2(JFrame parent) {
		super(parent);
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		this.setTitle(Language.get("preferences"));
		this.setBounds(100, 100, 450, 353);
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.setModal(true);
		
		JLabel lblIcon = new JLabel("");
		lblIcon.setIcon(new ImageIcon(DialogSettings2.class.getResource("/images/icons/prefs.png")));

		Font headingFont = lblIcon.getFont().deriveFont(Font.BOLD, 14);
		
		
		JLabel lblPackage = new JLabel(Language.get("settings.defaultpackage"));
		lblPackage.setFont(headingFont);
		
		pkgField = new JTextField();
		pkgField.setColumns(20);
		if (Settings.getProperty("defaultpackage") != null	&& Settings.getProperty("defaultpackage").length() > 0) {
			pkgField.setText(Settings.getProperty("defaultpackage"));
		}
		
		JLabel lblDifficulty = new JLabel(Language.get("settings.level"));
		lblDifficulty.setFont(headingFont);
		
		enemyGroup = new ButtonGroup();
		
		//JRadioButton optMyself = new JRadioButton( Language.get("level.zero"));
		
		JRadioButton optEasy = new JRadioButton( Language.get("level.one"));
		
		JRadioButton optMed = new JRadioButton( Language.get("level.two"));
		
		JRadioButton optHard = new JRadioButton( Language.get("level.three"));
		
		JRadioButton optSelectEnemy = new JRadioButton( Language.get("level.four"));
		
		//enemyGroup.add(optMyself);
		enemyGroup.add(optEasy);
		enemyGroup.add(optMed);
		enemyGroup.add(optHard);
		enemyGroup.add(optSelectEnemy);
		
		JRadioButton selectedLevel = (JRadioButton) getSelectedButton(enemyGroup,Settings.getProperty("level.default"));
		if(selectedLevel!=null) {
			selectedLevel.setSelected(true);
		} else {
			optMed.setSelected(true);
		}
		
		final JButton btnSave = new JButton(Language.get("save"));
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				saveSettings();
			}
		});

		// deshabilitar "guardar" si no hay nombre de grupo de robot
		pkgField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent keyEvt) {
				btnSave.setEnabled(!pkgField.getText().isEmpty());
			}
		});

		/* estado inicial del boton "guardar" -- si no hay nombre de grupo => no se puede guardar 
		 * hasta que el usuario escriba uno */
		btnSave.setEnabled(!pkgField.getText().isEmpty());
		
		GroupLayout groupLayout = new GroupLayout(this.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addComponent(lblIcon)
							.addGap(18)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(optEasy)
//								.addComponent(optMyself)
								.addComponent(optMed)
								.addComponent(optHard)
								.addComponent(optSelectEnemy)))
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(73)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(pkgField, GroupLayout.PREFERRED_SIZE, 127, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblPackage)
								.addComponent(lblDifficulty))))
					.addGap(92))
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(139)
					.addComponent(btnSave, GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
					.addGap(148))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(lblIcon)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(lblPackage)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(pkgField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addGap(38)
							.addComponent(lblDifficulty)
//							.addComponent(optMyself)
							.addComponent(optEasy)))
					.addComponent(optMed)
					.addComponent(optHard)
					.addComponent(optSelectEnemy)
					.addGap(18)
					.addComponent(btnSave)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		this.getContentPane().setLayout(groupLayout);
		this.getRootPane().setDefaultButton(btnSave);
		this.pack();
		PositionCalc.centerDialog(this);
		this.setVisible(true);
	}

	public String getSelectedButtonText(ButtonGroup buttonGroup) {
        for (Enumeration<AbstractButton> buttons = buttonGroup.getElements(); buttons.hasMoreElements();) {
            AbstractButton button = buttons.nextElement();
            if (button.isSelected()) {
                return button.getText();
            }
        }
        return null;
    }
	
	public AbstractButton getSelectedButton(ButtonGroup buttonGroup, String selectedText) {
        for (Enumeration<AbstractButton> buttons = buttonGroup.getElements(); buttons.hasMoreElements();) {
            AbstractButton button = buttons.nextElement();
            if(button.getText().equalsIgnoreCase(selectedText)) {
                return button;
            }
        }
        return null;
    }

	private void saveSettings()  {
		Settings.setProperty("defaultpackage", pkgField.getText());
		String levelSelected = getSelectedButtonText(enemyGroup);
		Settings.setProperty("level.default", levelSelected);
		try {
			Settings.saveAndCloseFile();
			this.closeDialog();
		} catch(Exception e) {
			new MessageDialog(Language.get("verificarPermiso"), MessageType.ERROR);
			e.printStackTrace();
			System.err.println("VERIFIQUE QUE TIENE PERMISO DE ESCRITURA EN LA CARPETA");
		}	
	}
	
	
}
