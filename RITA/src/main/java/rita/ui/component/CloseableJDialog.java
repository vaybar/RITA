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

import javax.swing.JDialog;

public class CloseableJDialog extends JDialog {

	private static final long serialVersionUID = -8742072874108728060L;

	public CloseableJDialog() {
		super();
	}

	public CloseableJDialog(Dialog owner) {
		super(owner);
	}
	
	public CloseableJDialog(Dialog owner, boolean modal) {
		super(owner, modal);
	}
	
	public CloseableJDialog(Dialog owner, String title) {
		super(owner, title);
	}

	public CloseableJDialog(Dialog owner, String title, boolean modal) {
		super(owner, title, modal);
	}

	public CloseableJDialog(Dialog owner, String title, boolean modal, GraphicsConfiguration gc) {
		super(owner, title, modal, gc);
	}

	public CloseableJDialog(Frame parent) {
		super(parent);
	}

	public CloseableJDialog(Frame parent, boolean modal) {
		super(parent, modal);
	}

	public CloseableJDialog(Frame parent, String title, boolean modal) {
		super(parent, title, modal);
	}

	public CloseableJDialog(Frame parent, String title, boolean modal, GraphicsConfiguration gc) {
		super(parent, title, modal, gc);
	}

	public CloseableJDialog(Window parent) {
		super(parent);
	}

	public CloseableJDialog(Window parent, Dialog.ModalityType modalityType) {
		super(parent, modalityType);
	}

	public CloseableJDialog(Window parent, String title, Dialog.ModalityType modalityType) {
		super(parent, title, modalityType);
	}

	public CloseableJDialog(Window parent, String title, Dialog.ModalityType modalityType, GraphicsConfiguration gc) {
		super(parent, title, modalityType, gc);
	}

	public void closeDialog() {
		this.setVisible(false);
		this.dispose();
	}

}
