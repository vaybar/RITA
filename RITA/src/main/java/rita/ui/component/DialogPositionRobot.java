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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import rita.settings.HelperEditor;
import rita.settings.Language;
import rita.util.Orientation;
import rita.util.RobotWithPositionTemp;

public class DialogPositionRobot extends CloseableJDialog {

	private JLabel gralLabel;
	private JPanel areaSelectionPosition;
	private JPanel panelButtonSelectionArea = new JPanel();

	private String unselected = Language.get("button.positionUnselected");
	private String selected = Language.get("button.positionSelected");

	private ButtonWithPosition oneButton;
	private ButtonWithPosition twoButton;
	private ButtonWithPosition threeButton;
	private ButtonWithPosition fourButton;
	private ButtonWithPosition fiveButton;
	private ButtonWithPosition sixButton;
	private ButtonWithPosition sevenButton;
	private ButtonWithPosition eigthButton;
	private ButtonWithPosition nineButton;

	private ButtonGroup buttonGroup;
	private JRadioButton radioNorth;
	private JRadioButton radioSouth;
	private JRadioButton radioEast;
	private JRadioButton radioWest;
	private JRadioButton[] radioArray = new JRadioButton[4];

	private ButtonWithPosition[] buttonsArray = new ButtonWithPosition[9];
	private JLabel labelSelectXY = new JLabel(
			Language.get("robotsPositioning.writexy"));
	private JLabel xCoordText = new JLabel("X");
	private JLabel yCoordText = new JLabel("Y");

	private JTextField xCoordValue = new JTextField("0");
	private JTextField yCoordValue = new JTextField("0");
	private JPanel panelXY = new JPanel();
	private JPanel panelButtons = new JPanel();
	private JPanel panelSouth = new JPanel();
	private JLabel labelOrientarion = new JLabel(
			Language.get("robotsPositioning.orientation"));

	private JPanel panelOrientation = new JPanel();
	private JButton okButton = new JButton(Language.get("ok"));
	private JButton cancelButton = new JButton(Language.get("cancel"));
	private RobotWithPositionTemp robotTemp;

	public DialogPositionRobot(RobotWithPositionTemp robotTemp) {
		initialize(robotTemp);
	}

	private void initialize(RobotWithPositionTemp robotTemp) {
		this.robotTemp = robotTemp;
		gralLabel = new JLabel(Language.get("position.set") + " "
				+ robotTemp.getRobotName());
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		okButton.addActionListener(new OkAction());
		cancelButton.addActionListener(new CancelAction());
		// this.setTitle(Language.get("positionOf") + " "
		// + robotTemp.getRobotName());

		this.setTitle(Language.get("title.position.set") + " "
				+ robotTemp.getRobotName());

		this.getContentPane().setLayout(new BorderLayout(6, 6));

		oneButton = new ButtonWithPosition(1, unselected,
				HelperEditor.getBattleFieldX(), HelperEditor.getBattleFieldY());
		twoButton = new ButtonWithPosition(2, unselected,
				HelperEditor.getBattleFieldX(), HelperEditor.getBattleFieldY());
		threeButton = new ButtonWithPosition(3, unselected,
				HelperEditor.getBattleFieldX(), HelperEditor.getBattleFieldY());
		fourButton = new ButtonWithPosition(4, unselected,
				HelperEditor.getBattleFieldX(), HelperEditor.getBattleFieldY());
		fiveButton = new ButtonWithPosition(5, unselected,
				HelperEditor.getBattleFieldX(), HelperEditor.getBattleFieldY());
		sixButton = new ButtonWithPosition(6, unselected,
				HelperEditor.getBattleFieldX(), HelperEditor.getBattleFieldY());
		sevenButton = new ButtonWithPosition(7, unselected,
				HelperEditor.getBattleFieldX(), HelperEditor.getBattleFieldY());
		eigthButton = new ButtonWithPosition(8, unselected,
				HelperEditor.getBattleFieldX(), HelperEditor.getBattleFieldY());
		nineButton = new ButtonWithPosition(9, unselected,
				HelperEditor.getBattleFieldX(), HelperEditor.getBattleFieldY());

		panelButtonSelectionArea.setLayout(new GridLayout(3, 3));
		panelButtonSelectionArea.add(oneButton);
		panelButtonSelectionArea.add(twoButton);
		panelButtonSelectionArea.add(threeButton);
		panelButtonSelectionArea.add(fourButton);
		panelButtonSelectionArea.add(fiveButton);
		panelButtonSelectionArea.add(sixButton);
		panelButtonSelectionArea.add(sevenButton);
		panelButtonSelectionArea.add(eigthButton);
		panelButtonSelectionArea.add(nineButton);

		buttonsArray[0] = oneButton;
		buttonsArray[1] = twoButton;
		buttonsArray[2] = threeButton;
		buttonsArray[3] = fourButton;
		buttonsArray[4] = fiveButton;
		buttonsArray[5] = sixButton;
		buttonsArray[6] = sevenButton;
		buttonsArray[7] = eigthButton;
		buttonsArray[8] = nineButton;

		SelectionAreaListener seleccionAreaListener = new SelectionAreaListener();
		for (int i = 0; i < buttonsArray.length; i++) {
			buttonsArray[i].addActionListener(seleccionAreaListener);
		}

		buttonGroup = new ButtonGroup();
		radioNorth = new JRadioButton(Language.get("robots.heading.NORTH"));
		radioSouth = new JRadioButton(Language.get("robots.heading.SOUTH"));
		radioEast = new JRadioButton(Language.get("robots.heading.EAST"));
		radioWest = new JRadioButton(Language.get("robots.heading.WEST"));

		// radioNorth.setSelected(true);
		radioArray[0] = radioNorth;
		radioArray[1] = radioSouth;
		radioArray[2] = radioEast;
		radioArray[3] = radioWest;

		radioNorth.addActionListener(new RadioOrientationListener(
				Orientation.NORTH));
		radioSouth.addActionListener(new RadioOrientationListener(
				Orientation.SOUTH));
		radioEast.addActionListener(new RadioOrientationListener(
				Orientation.EAST));
		radioWest.addActionListener(new RadioOrientationListener(
				Orientation.WEST));

		setButtonAndOrientationAsSelected(robotTemp);

		buttonGroup.add(radioNorth);
		buttonGroup.add(radioSouth);
		buttonGroup.add(radioEast);
		buttonGroup.add(radioWest);

		panelOrientation.add(radioNorth);
		panelOrientation.add(radioSouth);
		panelOrientation.add(radioEast);
		panelOrientation.add(radioWest);

		Dimension dimxy = new Dimension(40, 30);
		if (robotTemp.getX() >= 0)
			xCoordValue.setText(String.valueOf(robotTemp.getX()));
		else
			xCoordValue.setText("");
		xCoordValue.setInputVerifier(new MyInputVerifierX());
		xCoordValue.setPreferredSize(dimxy);
		if (robotTemp.getY() >= 0)
			yCoordValue.setText(String.valueOf(robotTemp.getY()));
		else
			yCoordValue.setText("");
		yCoordValue.setInputVerifier(new MyInputVerifierY());
		yCoordValue.setPreferredSize(dimxy);
		panelXY.setLayout(new FlowLayout());
		panelXY.add(labelSelectXY);
		panelXY.add(xCoordText);
		panelXY.add(xCoordValue);
		panelXY.add(yCoordText);
		panelXY.add(yCoordValue);

		panelButtons.add(okButton);
		panelButtons.add(cancelButton);
		areaSelectionPosition = new JPanel();
		areaSelectionPosition.setLayout(new FlowLayout());
		areaSelectionPosition.add(gralLabel);
		areaSelectionPosition.add(panelButtonSelectionArea);

		this.add(areaSelectionPosition, BorderLayout.NORTH);
		this.add(panelXY, BorderLayout.CENTER);

		JPanel mainPanelOrientation = new JPanel(new FlowLayout());
		mainPanelOrientation.add(labelOrientarion);
		mainPanelOrientation.add(panelOrientation);

		panelSouth.setLayout(new GridLayout(4, 1));

		panelSouth.add(new JSeparator());
		panelSouth.add(mainPanelOrientation);
		panelSouth.add(new JSeparator());
		panelSouth.add(panelButtons);

		this.add(panelSouth, BorderLayout.SOUTH);

		this.pack();
		this.setModal(true);
		PositionCalc.centerDialog(this);
		this.setVisible(true);
	}

	private class CancelAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			closeDialog();
		}

	}

	private void setButtonAndOrientationAsSelected(
			RobotWithPositionTemp robotTemp) {
		if (robotTemp == null) {
			for (int i = 0; i < buttonsArray.length; i++) {
				selectButton(buttonsArray[i], false);
			}
		} else {
			verifyButtonSelection(robotTemp.getX(), robotTemp.getY());
			verifyRadioOrientation(robotTemp);
		}
	}

	private void verifyButtonSelection(int x, int y) {
		for (int i = 0; i < buttonsArray.length; i++) {
			if (x == buttonsArray[i].getXCoord()
					&& y == buttonsArray[i].getYCoord())
				selectButton(buttonsArray[i], true);

			else {
				selectButton(buttonsArray[i], false);
			}
		}
	}

	private void verifyRadioOrientation(RobotWithPositionTemp robotTempParam) {
		if (Orientation.NORTH.getValue() == robotTempParam.getOrientation()) {
			selectRadioOrientation(radioNorth);
		}
		if (Orientation.SOUTH.getValue() == robotTempParam.getOrientation()) {
			selectRadioOrientation(radioSouth);
		}
		if (Orientation.EAST.getValue() == robotTempParam.getOrientation()) {
			selectRadioOrientation(radioEast);
		}
		if (Orientation.WEST.getValue() == robotTempParam.getOrientation()) {
			selectRadioOrientation(radioWest);
		}

	}

	private void selectRadioOrientation(JRadioButton radioSelected) {
		for (int i = 0; i < radioArray.length; i++) {
			if (radioArray[i] == radioSelected) {
				radioArray[i].setSelected(true);
			} else
				radioArray[i].setSelected(false);
		}

	}

	private void selectButton(JButton jButton, boolean isSelected) {
		if (isSelected) {
			jButton.setBackground(Color.WHITE);
			jButton.setText(selected);

		} else {
			jButton.setBackground(Color.GRAY);
			jButton.setText(unselected);
		}
	}

	private class OkAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (xCoordValue.getText().length() > 0)
				robotTemp.setX(Integer.parseInt(xCoordValue.getText()));
			else
				robotTemp.setX(-1);
			if (yCoordValue.getText().length() > 0)
				robotTemp.setY(Integer.parseInt(yCoordValue.getText()));
			else
				robotTemp.setY(-1);
			closeDialog();
		}

	}

	private class SelectionAreaListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			ButtonWithPosition selected = (ButtonWithPosition) e.getSource();
			setButtonAndOrientationAsSelected(null);
			selectButton(selected, true);
			selected.updateXYEntry(selected.getXCoord(), selected.getYCoord());

		}

	}

	private class ButtonWithPosition extends JButton {
		private int xCoord;
		private int yCoord;

		private int numberInGrid;

		public ButtonWithPosition(int number, String text, int areaX, int areaY) {
			this.setText(text);
			this.numberInGrid = number;
			this.calculatePixelPosition(areaX, areaY);

		}

		private void calculatePixelPosition(int areaX, int areaY) {

			// TODO hacer el calculo segun su numero en la grilla
			int sliceX = (areaX / (3 * 3));
			int sliceY = (areaY / (3 * 3));

			int numberInGridXTemp = (numberInGrid % 3 > 0) ? (numberInGrid % 3)
					: 3;
			int resultX = (numberInGridXTemp * 3 * sliceX) - sliceX - sliceX
					/ 2;
			this.setXCoord(resultX);

			int resultY = 0;

			switch (numberInGrid) {
			case 1:
			case 2:
			case 3:
				resultY = areaY - sliceY - sliceY / 2;
				break;

			case 4:
			case 5:
			case 6:
				resultY = areaY - (sliceY * 5) - sliceY / 2;
				break;

			case 7:
			case 8:
			case 9:
				resultY = areaY - (sliceY * 8) - sliceY / 2;
				break;
			}

			this.setYCoord(resultY);
		}

		private void updateXYEntry(int resultX, int resultY) {
			xCoordValue.setText(String.valueOf(resultX));
			yCoordValue.setText(String.valueOf(resultY));
		}

		public int getXCoord() {
			return xCoord;
		}

		public void setXCoord(int xCoord) {
			this.xCoord = xCoord;
		}

		public int getYCoord() {
			return yCoord;
		}

		public void setYCoord(int yCoord) {
			this.yCoord = yCoord;
		}

	}

	private class MyInputVerifierX extends InputVerifier {
		@Override
		public boolean verify(JComponent input) {
			String text = ((JTextField) input).getText();
			try {
				if (text.trim().equals("")) {
					verifyButtonSelection(-1, -1);
					return true;
				}
				Integer value = new Integer(text);
				verifyButtonSelection(Integer.parseInt(text),
						Integer.parseInt(yCoordValue.getText()));
				boolean result = value >= 0
						&& value < HelperEditor.getBattleFieldX();
				if (!result) {
					Toolkit.getDefaultToolkit().beep();
				}
				return result;
			} catch (NumberFormatException e) {
				Toolkit.getDefaultToolkit().beep();
				return false;
			}
		}
	}

	private class MyInputVerifierY extends InputVerifier {
		@Override
		public boolean verify(JComponent input) {
			String text = ((JTextField) input).getText();
			try {
				if (text.trim().equals("")) {
					verifyButtonSelection(-1, -1);
					return true;
				}

				Integer value = new Integer(text);
				verifyButtonSelection(Integer.parseInt(xCoordValue.getText()),
						Integer.parseInt(text));
				boolean result = value >= 0
						&& value < HelperEditor.getBattleFieldY();
				if (!result)
					Toolkit.getDefaultToolkit().beep();
				return result;
			} catch (NumberFormatException e) {
				Toolkit.getDefaultToolkit().beep();
				return false;
			}
		}
	}

	private class RadioOrientationListener implements ActionListener {

		Orientation orientation;

		public RadioOrientationListener(Orientation orientation) {
			this.orientation = orientation;
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			JRadioButton radio = (JRadioButton) arg0.getSource();
			if (radio.isSelected()) {
				robotTemp.setOrientation(orientation);
			}
		}

	}

}
