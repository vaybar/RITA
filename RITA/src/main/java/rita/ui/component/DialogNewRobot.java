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

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.Font;
import javax.swing.JButton;
import org.eclipse.wb.swing.FocusTraversalOnArray;

import rita.settings.HelperEditor;
import rita.settings.Language;
import rita.util.RitaUtilities;

import java.awt.Component;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import controller.WorkspaceController;

public class DialogNewRobot extends OpenOrCreateRobotBaseDialog {
	private static final long serialVersionUID = -6994868693235385044L;

	private WorkspaceController workspace;
	private JLabel errorMsg;
	private JTextField robotNameField;
	private JButton btnNewRobot;
	private JButton btnCancel;
	private JLabel typeRobot;
	
	/**
	 * Crear una ventana de dialogo modal.
	 */
	public DialogNewRobot(java.awt.Frame parent, WorkspaceController wc) {
		super(parent,true);
		workspace = wc;
		initialize();
	}

	protected JTextField getRobotNameField() {
		return robotNameField;
	}
	
	
	protected JButton getCreateRobotButton() {
		return btnNewRobot;
	}
	
	protected WorkspaceController getWorkspaceController() {
		return workspace;
	}

	/**
	 * Initializa el contenido de la ventana de dialogo.
	 */
	private void initialize() {
		this.setTitle("Crear un nuevo robot");
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		JLabel iconNew = new JLabel("");
		iconNew.setIcon(createImageIcon("/images/icons/new-gift.png"));
		
		btnNewRobot = new JButton(Language.get("createRobot"));
		this.getRootPane().setDefaultButton(btnNewRobot);
		// btnNewRobot comienza disabled porque al comienzo el nombre del nuevo robot esta vacio
		btnNewRobot.setEnabled(false);
		
		JLabel lblNewRobot = new JLabel(Language.get("newRobotName"));
		lblNewRobot.setFont(lblNewRobot.getFont().deriveFont(Font.BOLD, 14.0f));
		
		robotNameField = new JTextField();
		robotNameField.setColumns(20);
		
		//seleccionar el tipo de robot
		
		bgroup.add(juniorButton);
		bgroup.add(advancedButton);
		
		btnCancel = new JButton(Language.get("cancel"));
		
		errorMsg = new JLabel("");
		errorMsg.setIconTextGap(0);
		errorMsg.setInheritsPopupMenu(false);
		errorMsg.setVisible(false);
		errorMsg.setOpaque(true);
		errorMsg.setHorizontalTextPosition(SwingConstants.CENTER);
		errorMsg.setHorizontalAlignment(SwingConstants.CENTER);
		errorMsg.setRequestFocusEnabled(false);
		errorMsg.setForeground(Color.WHITE);
		errorMsg.setBackground(Color.RED);

		GroupLayout groupLayout = new GroupLayout(this.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(iconNew)
					.addGap(10)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(btnNewRobot, GroupLayout.PREFERRED_SIZE, 148, GroupLayout.PREFERRED_SIZE)
							.addGap(18)
							.addComponent(btnCancel))
						.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
							.addComponent(lblNewRobot, GroupLayout.DEFAULT_SIZE, 182, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(robotNameField, GroupLayout.PREFERRED_SIZE, 152, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup()
								.addComponent(juniorButton, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
								.addComponent(advancedButton, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
								.addGap(80)
							))
					.addContainerGap())
				.addComponent(errorMsg, GroupLayout.DEFAULT_SIZE, 422, Short.MAX_VALUE)
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(iconNew)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(12)
							.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblNewRobot)
								.addComponent(robotNameField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))))
					.addGap(12)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(juniorButton, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
								.addComponent(advancedButton, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE))
					.addGap(12)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnNewRobot)
						.addComponent(btnCancel))
					.addPreferredGap(ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
					.addComponent(errorMsg))
		);
		
		attachEventHandlers(this);
		
		this.getContentPane().setLayout(groupLayout);
		this.getContentPane().setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[]{robotNameField, juniorButton, advancedButton, btnNewRobot, btnCancel}));
		this.setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[]{btnNewRobot, lblNewRobot, iconNew, this.getContentPane()}));		
		this.pack();
		PositionCalc.centerDialog(this);
		this.setVisible(true);
	}

	protected void attachEventHandlers(final CloseableJDialog dlg) {
		super.attachEventHandlers();
		// event handler para el boton "cancelar", particular a esta ventana
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				dlg.closeDialog();
			}			
		});
	}
	
	protected void whenRobotNameAlreadyTakenError() {
		errorMsg.setText(Language.get("error.robotName"));
		errorMsg.setVisible(true);
	}
	
	protected void clearError() {
		errorMsg.setVisible(false);
	}
	
	protected boolean isErrorVisible() {
		return errorMsg.isVisible();
	}
	
	//metodo reescrito pereratarragona
	
}
	

