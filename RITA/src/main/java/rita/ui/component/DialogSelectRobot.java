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

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;

import rita.settings.Language;
import controller.WorkspaceController;

/** 
 * Ventana de dialogo para crear un nuevo robot o seleccionar uno existente.
 */
public class DialogSelectRobot extends OpenOrCreateRobotBaseDialog {
	private static final long serialVersionUID = -1797128517732800299L;

	JTextField robotNameField;
	JButton btnNewRobot;
	JButton btnOpenRobot;
	JLabel errorMsg;
	
	WorkspaceController workspace;
	/**
	 * Crear una ventana de dialogo modal.
	 */
	public DialogSelectRobot(java.awt.Frame parent, WorkspaceController wc) {
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
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.setTitle(Language.get("selectOrCreate"));
		JLabel iconNew = new JLabel("");
		iconNew.setIcon(createImageIcon("/images/icons/new-gift.png"));
		
		JLabel iconOpen = new JLabel("");
		iconOpen.setIcon(createImageIcon("/images/icons/open-box.png"));
		
		JLabel lblNewRobot = new JLabel(Language.get("createWithName"));

		Font headerFont = lblNewRobot.getFont().deriveFont(Font.BOLD,14.0f);

		lblNewRobot.setFont(headerFont);
		
		JLabel lblOpenRobot = new JLabel(Language.get("openAlreadyCreated"));
		
		lblOpenRobot.setFont(headerFont);
	
		btnNewRobot = new JButton(Language.get("createRobot"));
		btnNewRobot.setEnabled(false); // inicialmente disabled, porque el robot aun no tiene nombre
		
		robotNameField = new JTextField("",20);
		
		bgroup.add(juniorButton);
		bgroup.add(advancedButton);

		btnOpenRobot = new JButton(Language.get("openButton"));
		
		JLabel lbltoStart = new JLabel(Language.get("toStart"));
		lbltoStart.setFont(headerFont);
		
		JLabel lblOEllipsis = new JLabel(Language.get("or"));
		lblOEllipsis.setVerticalAlignment(SwingConstants.TOP);
		lblOEllipsis.setFont(headerFont);
		
		JSeparator separator = new JSeparator();
		JLabel lblOr_ellipsis = new JLabel(Language.get("or"));
		lblOr_ellipsis.setFont(headerFont);
		
		errorMsg = new JLabel(Language.get("error.robotName"));
		errorMsg.setOpaque(true);
		errorMsg.setVisible(false); // el mensaje de error arranca oculto
		errorMsg.setHorizontalTextPosition(SwingConstants.CENTER);
		errorMsg.setHorizontalAlignment(SwingConstants.CENTER);
		errorMsg.setForeground(Color.WHITE);
		errorMsg.setBackground(Color.RED);

		GroupLayout groupLayout = new GroupLayout(this.getContentPane());

		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(lbltoStart)
						.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
							.addGroup(groupLayout.createSequentialGroup()
								.addComponent(iconOpen)
								.addPreferredGap(ComponentPlacement.UNRELATED)
								.addComponent(lblOpenRobot)
								.addPreferredGap(ComponentPlacement.UNRELATED)
								.addComponent(btnOpenRobot, GroupLayout.PREFERRED_SIZE, 131, GroupLayout.PREFERRED_SIZE))
							.addGroup(groupLayout.createSequentialGroup()
								.addComponent(iconNew)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
									.addComponent(lblNewRobot)
									.addGroup(groupLayout.createSequentialGroup()
										.addGap(6)
										.addComponent(robotNameField, GroupLayout.PREFERRED_SIZE, 152, GroupLayout.PREFERRED_SIZE)
									.addGroup(groupLayout.createSequentialGroup()	
										.addPreferredGap(ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
										.addComponent(btnNewRobot, GroupLayout.PREFERRED_SIZE, 148, GroupLayout.PREFERRED_SIZE)))))
							.addGroup(groupLayout.createSequentialGroup()
										.addGap(40)
										.addComponent(juniorButton, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
										.addComponent(advancedButton, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
										
								.addPreferredGap(ComponentPlacement.RELATED)
							)			
							.addComponent(separator, GroupLayout.PREFERRED_SIZE, 400, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblOr_ellipsis)))
					.addContainerGap(24, Short.MAX_VALUE))
				.addComponent(errorMsg, GroupLayout.DEFAULT_SIZE, 414, Short.MAX_VALUE)
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(lbltoStart)
					.addGap(6)
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(lblNewRobot)
							.addGap(18)
							.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE, false)
								.addComponent(btnNewRobot)
								.addComponent(robotNameField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
						.addComponent(iconNew))
				.addGap(12)	
				.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(juniorButton, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
								.addComponent(advancedButton, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE))
					.addGap(20)
					.addComponent(separator, GroupLayout.PREFERRED_SIZE, 4, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(lblOr_ellipsis)
					.addGap(7)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(12)
							.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblOpenRobot)
								.addComponent(btnOpenRobot)))
						.addGroup(groupLayout.createSequentialGroup()
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(iconOpen)))
					.addPreferredGap(ComponentPlacement.RELATED, 24, Short.MAX_VALUE)
					.addComponent(errorMsg))
		);

		attachEventHandlers(workspace);
	
		this.getContentPane().setLayout(groupLayout);
		this.setResizable(false);
		this.pack();
		PositionCalc.centerDialog(this);
		this.setVisible(true);
	}

	@Override
	protected void whenRobotNameAlreadyTakenError() {
		errorMsg.setText(Language.get("error.robotName"));
		errorMsg.setVisible(true);
	}

	@Override
	protected void clearError() {
		errorMsg.setVisible(false);
	}

	@Override
	protected boolean isErrorVisible() {
		return errorMsg.isVisible();
	}

	protected void attachEventHandlers(final WorkspaceController wc) {
		super.attachEventHandlers();
		btnOpenRobot.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				DialogSelectEnemies.clean();
				wc.openFileInWorkspace();

				closeDialog();
			}
		});
		btnNewRobot.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {

				closeDialog();
			}			
		});


	}
	
}
