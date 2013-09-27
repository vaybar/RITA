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
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Method;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import rita.settings.Language;

import workspace.Workspace;

/**
 * Esta clase muestra los mensajes del sistema. Son de 2 tipos: ERROR, que se
 * muestran en rojo y WARN que se muestran en amarillo
 * 
 * @author Vanessa Aybar Rosales
 * */
public class MessageDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 2053536319916445536L;

	private static final int DISPLAY_TIME_MSECS = 4000;
	private static final int FADE_STEP_MSECS = 120;

	public enum MessageType {
		ERROR, INFO
	}

	private final Timer timerShow = new Timer(DISPLAY_TIME_MSECS, this);
	private final Timer timerFade = new Timer(FADE_STEP_MSECS, this);

	private int count = 0;

	private static final int WIDTH = 100; // 300
	private static final int HEIGHT = 80; // 80
	private static ImageIcon img = null;
	private static ImageIcon imgWARN = null;
	private static Method awtSetWindowOpacity;
	private static Method useWindowOpacity;
	
	static {
		try {
			// metodo para cambiar transparencia de ventana en Java 7
			Class<?> windowClass = Class.forName("java.awt.Window");
			useWindowOpacity = windowClass.getMethod("setOpacity", float.class);
		} catch (Exception tryJava6) {		
			try {
				// metodo para cambiar transparencia de ventana desde Java 1.6u10
				Class<?> awtUtilitiesClass = Class.forName("com.sun.awt.AWTUtilities");
				awtSetWindowOpacity = awtUtilitiesClass.getMethod("setWindowOpacity", Window.class, float.class);
			} catch (Exception ignored) {		}
		}
	}
	
	public MessageDialog(JFrame frameP, String title) {
		super(frameP, title);
		initialize(title, Workspace.getInstance().getBlockCanvas().getWidth(), Workspace.getInstance().getBlockCanvas().getHeight(), MessageType.INFO);
	}

	public MessageDialog(JFrame frameP, String title, MessageType type) {
		super(frameP, title);
		initialize(title, Workspace.getInstance().getBlockCanvas().getWidth(), Workspace.getInstance().getBlockCanvas().getHeight(), type);
	}
	public MessageDialog(String title, MessageType type) {
		super((JFrame)null, type == MessageType.INFO ? Language.get("information") : Language.get("error"));
		initialize(title, Workspace.getInstance().getBlockCanvas().getWidth(), Workspace.getInstance().getBlockCanvas().getHeight(), type);
	}

	private void initialize(String str, int maxX, int maxY, MessageType type) {
		final JPanel panel = new JPanel();
		final JLabel text = new JLabel();
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		if (imgWARN == null) {
			imgWARN = new ImageIcon(getClass().getResource("/images/rita_yellow.jpg"));
		}

		// customizacion para Error
		if (type.equals(MessageType.ERROR)) {
			panel.setBackground(Color.RED);
			img = new ImageIcon(getClass().getResource("/images/rita_red.jpg"));
			text.setForeground(Color.WHITE);
			text.setBackground(Color.RED);
		}

		// customizacion para Info
		if (type.equals(MessageType.INFO)) {
			panel.setBackground(Color.YELLOW);
			img = new ImageIcon(getClass().getResource("/images/rita_yellow.jpg"));
			text.setForeground(Color.BLACK);
			text.setBackground(Color.YELLOW);
		}
		panel.setSize(WIDTH, HEIGHT);
		text.setText("<html>" + Language.get("RITA.says") + "<br>" + str + "</html>");
		text.setFont(new Font("Arial", Font.BOLD, 11));

		text.setIcon(img);
		panel.add(text);

		this.add(panel);
		this.setResizable(false);
		this.setSize(WIDTH, HEIGHT);
		this.setAlwaysOnTop(true);
		this.setLocation(maxX / 2 - (WIDTH / 2), (maxY - HEIGHT) / 2);
		this.repaint();
		this.validate();
		this.pack();
		this.setVisible(true);
		timerFade.addActionListener(this);
		timerShow.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent startHideEvt) {
				// fin del tiempo de mostrar el mensaje; comienza el fade out
				timerShow.stop();
				timerFade.start();
			}
			
		});
		timerShow.start();

		addWindowListener(new WindowAdapter() {
			// handler cuando el usuario cierra la ventana
			public void windowClosing(WindowEvent evt) {
				closeDialog();
			}
		});

	}

	private void closeDialog() {
		this.timerShow.stop();
		this.timerFade.stop();
		this.setVisible(false);
		this.dispose();
	}

	private static void setTranslucency(Window window, int factor) {
		try {
			if(useWindowOpacity!=null) {
				useWindowOpacity.invoke(window, Float.valueOf(0.10f * factor));
			} else if(awtSetWindowOpacity!=null) {
				awtSetWindowOpacity.invoke(null, window, Float.valueOf(0.10f * factor));			
			}
		} catch (Exception ignored) { }
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// fade out la ventana cada vez un poquito mas cada FADE_STEP_MSECS milisegundos
		count++;
		setTranslucency(this, 10 - count);
		if (count > 9) {
			this.closeDialog();
		} else {
			this.repaint();
		}
	}

}