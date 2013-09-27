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
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.HyperlinkEvent;

import rita.settings.Language;

import java.awt.SystemColor;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;

import javax.swing.event.HyperlinkListener;

/**
 * Esta clase representa a la ventana de información acerca de la aplicación.
 * 
 * @author Vanessa Aybar Rosales
 * */
public class DialogAbout extends JDialog {

	private static final long serialVersionUID = 838041902922227173L;

	/**
	 * Create the application.
	 */
	public DialogAbout(java.awt.Frame parent) {
		super(parent);
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		this.setTitle(Language.get("about.title"));
		this.setResizable(false);
		this.setSize(597, 375);
		this.getContentPane().setLayout(new BorderLayout(0, 0));
		
		JLabel lblIconRita = new JLabel("");
		lblIconRita.setBorder(new EmptyBorder(12, 12, 12, 12));
		lblIconRita.setIcon(new ImageIcon(DialogAbout.class.getResource("/images/rita_con_titulo.jpg")));
		this.getContentPane().add(lblIconRita, BorderLayout.WEST);
		
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setMaximumSize(new Dimension(100, 32767));
		panel.setBorder(new EmptyBorder(12, 0, 8, 12));
		this.getContentPane().add(panel);
		panel.setLayout(new BorderLayout(0, 8));
		
		JLabel title = new JLabel(Language.get("about.heading"));
		title.setForeground(new Color(0, 51, 102));
		title.setHorizontalAlignment(SwingConstants.CENTER);
		title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
		panel.add(title, BorderLayout.NORTH);
		
		JEditorPane creditsArea = new JEditorPane();
		creditsArea.setEditable(false);
		creditsArea.setContentType("text/html");
		creditsArea.setBackground(SystemColor.control);
		creditsArea.setOpaque(false);
		creditsArea.setText(Language.get("about.text"));
		creditsArea.setCaretPosition(0);
		/* si es posible, abrir links en el browser del sistema operativo */
		creditsArea.addHyperlinkListener(new HyperlinkListener() { 
			@Override
            public void hyperlinkUpdate(HyperlinkEvent evt) { 
				String urlString = evt.getURL().toExternalForm();
				Desktop desktop = Desktop.getDesktop();
				if(desktop.isSupported(java.awt.Desktop.Action.BROWSE)) {
					try {
						URI uri = new URI(urlString);
						desktop.browse(uri);
					} catch (Exception e) { }
				}
            }			
		});
		panel.add(new JScrollPane(creditsArea), BorderLayout.CENTER);
		
		JButton closeButton = new JButton(Language.get("cerrar"));
		closeButton.setSelected(true);
		closeButton.setHorizontalTextPosition(SwingConstants.CENTER);
		closeButton.setDefaultCapable(true);
		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg) {
				DialogAbout.this.setVisible(false);
				DialogAbout.this.dispose();
			}
		});
		panel.add(closeButton, BorderLayout.SOUTH);
		PositionCalc.centerDialog(this);
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.setVisible(true);
	}

}
