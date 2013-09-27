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
import javax.swing.JLabel;
import java.awt.BorderLayout;
import javax.swing.SwingConstants;
import javax.swing.ImageIcon;
import java.awt.Font;
import javax.swing.border.EmptyBorder;

/** Ventana de dialogo muy simple para decir "estoy haciendo esto y aquello, por favor espere".
 * Esta ventana NO puede ser modal, porque bloquearia a todo lo que se quiere hacer despues de mostrar el cartel. */
public class DialogPleaseWait extends CloseableJDialog {
	private static final long serialVersionUID = -6477626952671868293L;

	public DialogPleaseWait(JFrame parent, String labelText) {
		super(parent);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		JLabel label = new JLabel(labelText);
		label.setBorder(new EmptyBorder(12, 12, 12, 12));
		label.setFont(label.getFont().deriveFont(Font.BOLD, 14));
		label.setIcon(new ImageIcon(DialogPleaseWait.class.getResource("/images/icons/rita.png")));
		label.setHorizontalAlignment(SwingConstants.CENTER);
		this.getContentPane().add(label, BorderLayout.CENTER);
		this.pack();
		this.toFront();
		PositionCalc.centerDialog(this);
		this.setVisible(true);
	}
}
