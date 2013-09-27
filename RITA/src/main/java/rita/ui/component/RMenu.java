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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import rita.settings.HelperEditor;
import rita.settings.Language;
import rita.util.RitaUtilities;

import workspace.Workspace;
import controller.WorkspaceController;

/**
 * Esta clase representa el menú superior de la aplicación
 * 
 * @author Vanessa Aybar Rosales
 * */
public class RMenu {
	// Barra de menu principal
	private static final JMenuBar menuBar = new JMenuBar();
	private static final JMenu menuRobots = new JMenu(Language.get("robots"));
	private static final JMenu menuEdit = new JMenu(Language.get("edit"));
	private static final JMenu menuInf = new JMenu(Language.get("information"));
//	private static final JMenu menuSetting = new JMenu(Language.get("setting"));
	// private static final JMenu menuHelp = new JMenu(Language.get("help"));

	// Desplegable File
	private static final JMenuItem menuNew = new JMenuItem(Language.get("new"),
			KeyEvent.VK_N);

	private static final JMenuItem menuOpen = new JMenuItem(Language
			.get("open"), KeyEvent.VK_A);
	private static final JMenuItem menuSave = new JMenuItem(Language
			.get("saveAll")+" "
			+ HelperEditor.currentRobotName, KeyEvent.VK_G);

	// Settings submenu
	private static final JMenuItem menuPreferences = new JMenuItem(Language
			.get("preferences"), KeyEvent.VK_P);
	private static final JMenuItem menuAbout = new JMenuItem(Language
			.get("about"),KeyEvent.VK_C);

	// Desplegable Edit
	private static final JMenuItem menuUndo = new JMenuItem(Language
			.get("undo"), KeyEvent.VK_D);
//	private static final JMenuItem menuRedo = new JMenuItem(Language
//			.get("redo"), KeyEvent.VK_R);

	public RMenu(final JFrame parentWindow, final WorkspaceController wc) {
		menuBar.add(menuRobots);
		menuBar.add(menuEdit);
		menuBar.add(menuInf);
//		menuBar.add(menuSetting);
		// menuBar.add(menuHelp);

		menuRobots.add(menuNew);
		menuRobots.add(menuOpen);

		menuEdit.add(menuUndo);
//		menuEdit.add(menuRedo);

		menuEdit.add(menuPreferences);
		menuInf.add(menuAbout);
		
		
		final WorkspaceController wcTemp = wc;
		
		menuNew.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				boolean opened = wcTemp.robotInWorkspace();
				boolean confirmNew = true;
				if (opened) {
					// hay un robot abierto, confirmar que lo quiere descartar
					confirmNew = RitaUtilities.confirmWindow(Language
							.get("confirm.title"), Language.get("confirm.newmessage"));
				}
				if (confirmNew) {
					// mostrar ventana que pide el nombre del nuevo robot
					@SuppressWarnings("unused")
					DialogNewRobot rTypeDialog = new DialogNewRobot(parentWindow,wcTemp);
				}
			}
		});

		menuSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				wcTemp.saveWorkspaceToFile();
			}
		});
		menuSave.setEnabled(false);
		menuOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean opened = wcTemp.robotInWorkspace();
				boolean confirmOpen = false;
				if (opened) {
					confirmOpen = RitaUtilities.confirmWindow(Language
							.get("confirm.title"), Language.get("confirm.message"));
				} else {confirmOpen =true;}
				if (confirmOpen) {
					openRobot(wcTemp);
				}
			}

			

		});

		menuUndo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Workspace.getInstance().undo();
			}
		});

//		menuRedo.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				Workspace.getInstance().redo();
//			}
//		});

		menuPreferences.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new DialogSettings2(parentWindow);
			}
		});
		menuUndo.setEnabled(false);
		menuPreferences.setEnabled(false);

		menuAbout.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new DialogAbout(parentWindow);
			}
		});
	}

	public JMenuBar getMenuBar() {
		return menuBar;

	}

	public static final void enableSaveWorkspace() {
		menuSave.setEnabled(true);
		menuUndo.setEnabled(true);
		menuPreferences.setEnabled(true);

	}
	
	public static final void openRobot(final WorkspaceController wcTemp) {
		if (wcTemp.openFileInWorkspace()!=JFileChooser.CANCEL_OPTION) {
			enableSaveWorkspace();
			update();
		}
	}


	public static void update() {
		menuRobots.add(menuSave);
		menuUndo.setEnabled(true);
		menuPreferences.setEnabled(true);
		if(!HelperEditor.currentRobotName.isEmpty()) {
			menuSave.setText(Language.get("saveAll") + " "+HelperEditor.currentRobotName);
			menuSave.setEnabled(true);
		} else {
			menuSave.setEnabled(false);			
		}
	}

}
