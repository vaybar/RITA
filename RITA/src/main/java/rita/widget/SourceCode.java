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

package rita.widget;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Collection;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;
import javax.tools.Diagnostic;
import javax.tools.Diagnostic.Kind;

import mode.Competition;
import mode.Mode;
import mode.Training;

import org.apache.commons.io.IOUtils;

import renderable.RenderableBlock;
import rita.battle.Batalla;
import rita.battle.BatallaConfig;
import rita.compiler.CompileString;
import rita.settings.HelperEditor;
import rita.settings.Language;
import rita.settings.Settings;
import rita.ui.component.DialogNewRobot;
import rita.ui.component.DialogPleaseWait;
import rita.ui.component.DialogSelectEnemies;
import rita.ui.component.DialogStartBattle;
import rita.ui.component.MessageDialog;
import rita.ui.component.MessageDialog.MessageType;
import rita.ui.component.exception.NoEnemiesException;
import rita.ui.sourcecodepane.ReadOnlySourceCodePane;
import rita.widget.ScreenHelper.ScreenSize;
import workspace.Workspace;
import workspace.WorkspaceEvent;
import workspace.WorkspaceListener;
import workspace.WorkspaceWidget;
import controller.WorkspaceController;

/* TODO: Ver de usar http://code.google.com/p/jxtextpane/ para mostrar el codigo java con colores */
public class SourceCode extends JPanel implements MouseListener,
		WorkspaceWidget, ComponentListener, WorkspaceListener {
	private static final long serialVersionUID = 328149080275L;

	private final SourceCodeEnlargerTimer enlarger;
	private static int MIN_WIDTH = 106;
	/** the default height of a source code view button */
	private static int MIN_HEIGHT = 40;
	/** this.width */
	private static int CURRENT_WIDTH = MIN_WIDTH;
	/** this.height */
	private static int CURRENT_HEIGHT = MIN_HEIGHT;

	private static int BUTTON_HEIGHT = MIN_HEIGHT;

	private static int MAX_WIDTH;
	private static int MAX_HEIGHT;

	static {
		MAX_WIDTH = 320;
		MAX_HEIGHT = 340 + BUTTON_HEIGHT;
		/*
		 * ajustar ancho y alto con valores de acuerdo a si el tama�o de la
		 * pantalla es com�n o si es la de una netbook
		 */
		if (ScreenHelper.getSize().equals(ScreenSize.SMALL)) {
			MAX_WIDTH -= MAX_WIDTH / 5;
			MAX_HEIGHT -= MAX_HEIGHT / 5;
		}
	}
	/* distancia del lado derecho del widget al lado derecho de la ventana */
	private static final int OFFSET_FROM_RIGHT = 50;
	/* distancia del lado inferior del widget al lado inferior de la ventana */
	private static final int OFFSET_FROM_BOTTOM = 150;

	private static final Color COLOR_EVENT_HANDLER = new Color(149, 45, 45),
			COLOR_MOVEMENT = new Color(171, 80, 108),
			COLOR_ACTIONS = new Color(236, 45, 45), COLOR_INFO = new Color(236,
					45, 236), COLOR_COLORIZE = new Color(236, 105, 45),
			COLOR_TEXT = new Color(74, 145, 48), COLOR_VALUE = new Color(236,
					45, 236), COLOR_LOGIC = new Color(236, 161, 45),
			COLOR_MATH = new Color(56, 60, 210), COLOR_METHOD = new Color(141,
					45, 141), COLOR_VARS = new Color(149, 52, 105),
			COLOR_EVENT = new Color(100, 100, 236);

	/** color de fondo de la linea de codigo conteniendo un error */
	private static final Color ErrorLineColor = new Color(255, 128, 128);

	private ReadOnlySourceCodePane paneJavaCode;
	private boolean minimized = true;
	private JButton codeButton;
	private JButton compileButton;
	private JButton compiteButton;
	private boolean expanded = false;
	private Font smallButtonFont;

	final File basePathRobots = new File(Settings.getRobotsPath());

	private JFrame frameParent;

	private static final class SINGLETON {
		private static final SourceCode INSTANCE = new SourceCode();
	}

	private void prepareCodeRegion() {
		paneJavaCode = new ReadOnlySourceCodePane();
		paneJavaCode.setFont(paneJavaCode.getFont().deriveFont(12.0f));

		// definir la colorizacion de la sintaxis de Java, coincidiendo con los
		// colores de los bloques de RITA
		final ReadOnlySourceCodePane.RegExpHashMap syntax = new ReadOnlySourceCodePane.RegExpHashMap();
		// PERERATARRAGONA
		syntax.put("run", COLOR_EVENT_HANDLER);
		syntax.put("onBattleEnded", COLOR_EVENT_HANDLER);
		syntax.put("onBulletHit", COLOR_EVENT_HANDLER);
		syntax.put("onBulletHitBullet", COLOR_EVENT_HANDLER);
		syntax.put("onBulletMissed", COLOR_EVENT_HANDLER);
		syntax.put("onHitByBullet", COLOR_EVENT_HANDLER);
		syntax.put("onHitRobot", COLOR_EVENT_HANDLER);
		syntax.put("onHitWall", COLOR_EVENT_HANDLER);
		syntax.put("onRobotDeath", COLOR_EVENT_HANDLER);
		syntax.put("onRoundEnded", COLOR_EVENT_HANDLER);
		syntax.put("onScannedRobot", COLOR_EVENT_HANDLER);
		syntax.put("onWin", COLOR_EVENT_HANDLER);

		syntax.put("event", COLOR_EVENT);

		syntax.put("ahead", COLOR_MOVEMENT);
		syntax.put("back", COLOR_MOVEMENT);
		syntax.put("turnAheadLeft", COLOR_MOVEMENT);
		syntax.put("turnAheadRight", COLOR_MOVEMENT);
		syntax.put("turnBackLeft", COLOR_MOVEMENT);
		syntax.put("turnBackRight", COLOR_MOVEMENT);
		syntax.put("turnLeft", COLOR_MOVEMENT);
		syntax.put("turnRight", COLOR_MOVEMENT);
		syntax.put("turnTo", COLOR_MOVEMENT);

		syntax.put("turnGunLeft", COLOR_ACTIONS);
		syntax.put("turnGunRight", COLOR_ACTIONS);
		syntax.put("turnGunTo", COLOR_ACTIONS);
		syntax.put("bearGunTo", COLOR_ACTIONS);
		syntax.put("doNothing", COLOR_ACTIONS);
		syntax.put("fire", COLOR_ACTIONS);
		syntax.put("resume", COLOR_ACTIONS);
		syntax.put("scan", COLOR_ACTIONS);
		syntax.put("setAdjustGunForRobotTurn", COLOR_ACTIONS);
		syntax.put("setAdjustRadarForGunTurn", COLOR_ACTIONS);
		syntax.put("setAdjustRadarForRobotTurn", COLOR_ACTIONS);
		syntax.put("stop", COLOR_ACTIONS);
		syntax.put("turnRadarLeft", COLOR_ACTIONS);
		syntax.put("turnRadarRight", COLOR_ACTIONS);

		syntax.put("energy", COLOR_INFO);
		syntax.put("fieldHeight", COLOR_INFO);
		syntax.put("fieldWidth", COLOR_INFO);
		syntax.put("gunBearing", COLOR_INFO);
		syntax.put("gunHeading", COLOR_INFO);
		syntax.put("gunReady", COLOR_INFO);
		syntax.put("heading", COLOR_INFO);
		syntax.put("hitByBulletAngle", COLOR_INFO);
		syntax.put("hitByBulletBearing", COLOR_INFO);
		syntax.put("hitRobotAngle", COLOR_INFO);
		syntax.put("hitRobotBearing", COLOR_INFO);
		syntax.put("hitWallAngle", COLOR_INFO);
		syntax.put("hitWallBearing", COLOR_INFO);
		syntax.put("others", COLOR_INFO);
		syntax.put("robotX", COLOR_INFO);
		syntax.put("robotY", COLOR_INFO);
		syntax.put("scannedAngle", COLOR_INFO);
		syntax.put("scannedBearing", COLOR_INFO);
		syntax.put("scannedDistance", COLOR_INFO);

		syntax.put("getBattleFieldHeight", COLOR_INFO);
		syntax.put("getBattleFieldWidth", COLOR_INFO);
		syntax.put("getEnergy", COLOR_INFO);
		syntax.put("getGunCoolingRate", COLOR_INFO);
		syntax.put("getGunHeading", COLOR_INFO);
		syntax.put("getGunHeat", COLOR_INFO);
		syntax.put("getHeading", COLOR_INFO);
		syntax.put("getHeight", COLOR_INFO);
		syntax.put("getName", COLOR_INFO);
		syntax.put("getNumRounds", COLOR_INFO);
		syntax.put("getOthers", COLOR_INFO);
		syntax.put("getRadarHeading", COLOR_INFO);
		syntax.put("getRoundNum", COLOR_INFO);
		syntax.put("getWidth", COLOR_INFO);
		syntax.put("getX", COLOR_INFO);
		syntax.put("getY", COLOR_INFO);

		paneJavaCode.setOperatorColor(COLOR_MATH);
		paneJavaCode.setNumberColor(COLOR_VALUE);
		paneJavaCode.setStringLiteralColor(COLOR_VALUE);

		syntax.put("print(\\w{2})", COLOR_TEXT);

		syntax.put("while", COLOR_LOGIC);
		syntax.put("if", COLOR_LOGIC);
		syntax.put("for", COLOR_LOGIC);

		syntax.put("true", COLOR_VALUE);
		syntax.put("false", COLOR_VALUE);

		syntax.put("setColors", COLOR_COLORIZE);
		syntax.put("setAllColors", COLOR_COLORIZE);
		syntax.put("setBodyColor", COLOR_COLORIZE);
		syntax.put("setBulletColor", COLOR_COLORIZE);
		syntax.put("setColors", COLOR_COLORIZE);
		syntax.put("setGunColor", COLOR_COLORIZE);
		syntax.put("setRadarColor", COLOR_COLORIZE);
		syntax.put("setScanColor", COLOR_COLORIZE);

		syntax.put("Integer", COLOR_VARS);
		syntax.put("String", COLOR_VARS);

		paneJavaCode.setKeywordColors(syntax);

		paneJavaCode.setBackground(Color.WHITE);
		paneJavaCode.getWrappingContainerWithLines().setBounds(0,
				BUTTON_HEIGHT, MAX_WIDTH, MAX_HEIGHT - BUTTON_HEIGHT);

	}

	private SourceCode() {
		this.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		this.setLocation(OFFSET_FROM_RIGHT - MIN_WIDTH, OFFSET_FROM_BOTTOM
				- BUTTON_HEIGHT);
		this.setSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
		this.setPreferredSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));

		this.setLayout(null);
		smallButtonFont = this.getFont().deriveFont(Font.PLAIN, 9.0f);

		this.enlarger = new SourceCodeEnlargerTimer();
		createHideCodeButton();
		createCompileButton();
		createCompiteButton();
		prepareCodeRegion();
		add(codeButton);
		add(compileButton);
		add(compiteButton);
		add(paneJavaCode.getWrappingContainerWithLines());

		Workspace.getInstance().addComponentListener(this);

	}

	private void createHideCodeButton() {

		this.codeButton = new JButton(
				createImageIcon("/images/sourcecode/source_code_small.png"));
		codeButton.addActionListener(new SourceCodeEnlargerTimer());
		codeButton.addMouseListener(this);
		codeButton.setBounds(0, 0, MIN_WIDTH, BUTTON_HEIGHT);
		codeButton.setFont(smallButtonFont);
		codeButton.setAlignmentX(LEFT_ALIGNMENT);
		codeButton.setText(Language.get("codeButton.title.open"));
		codeButton.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0,
				Color.BLACK));
	}

	/**
	 * @return El path del archivo con el codigo fuente java del robot
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private File saveSourceCode() throws FileNotFoundException, IOException {
		final String fullPath = Settings.getRobotsPath() + File.separator
				+ HelperEditor.currentRobotPackage.replace(".", File.separator);

		String fileSource = HelperEditor.currentRobotName + ".java";
		File javaSourceFile = new File(fullPath, fileSource);
		writeSourceFile(javaSourceFile, this.getText());
		return javaSourceFile;
	}

	private void writeSourceFile(File javaSourceFile, String javaSource)
			throws FileNotFoundException, IOException {
		OutputStream bos = new FileOutputStream(javaSourceFile);
		try {
			bos.write(javaSource.getBytes());
		} finally {
			bos.close();
		}
	}

	private String readSourceFile(File javaSourceFile)
			throws FileNotFoundException, IOException {
		FileInputStream inputStream = new FileInputStream(javaSourceFile);
		try {
			return IOUtils.toString(inputStream);
		} finally {
			IOUtils.closeQuietly(inputStream);
		}
	}

	private void createCompiteButton() {
		this.compiteButton = new JButton(
				createImageIcon("/images/sourcecode/compite_battle.png"));
		this.compiteButton
				.setToolTipText(Language.get("compiteButton.tooltip"));
		this.compiteButton.setBounds(MIN_WIDTH*2, 0, MAX_WIDTH - (MIN_WIDTH * 2), BUTTON_HEIGHT);
		/*this.compileButton.setBounds(MIN_WIDTH, 0, MAX_WIDTH - (MIN_WIDTH * 2),
				BUTTON_HEIGHT);*/
		this.compiteButton.setFont(smallButtonFont);
		this.compiteButton.setAlignmentX(LEFT_ALIGNMENT);
		this.compiteButton.setText(Language.get("compiteButton.title"));

		this.compiteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					// guardar el codigo fuente
					File sourcePath = saveSourceCode();
					// COMPILA EN EL DIRECTORIO POR DEFAULT + LA RUTA DEL
					// PACKAGE
					final Collection<File> inFiles = createClassFiles(sourcePath);
					if (inFiles != null) {
							this.connectingAndSendingToBattle(inFiles);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					System.out.println("Error al crear el dialogo de envio de robot");
				}
			}

			private void connectingAndSendingToBattle(final Collection<File> inFiles) {
				//ventana enviando robots...inFiles
				final DialogPleaseWait dpw=new DialogPleaseWait(frameParent, "Enviando su robot....");
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							
							dpw.closeDialog();
							HelperEditor.setCurrentMode(Competition.getInstance());
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									DialogStartBattle dsb=new DialogStartBattle(frameParent, inFiles);		
								}
							});
						} catch (Exception e) {
							// TODO Vane: Mostrar ventana de error en la conexion
							e.printStackTrace();
						}
						
					}
				}).start();
				
			}
		});

	}

	private void createCompileButton() {
		ImageIcon imgIcon = new ImageIcon(getClass().getResource(
				"/images/sourcecode/bytecode.png"));
		this.compileButton = new JButton(imgIcon);
		this.compileButton
				.setToolTipText(Language.get("compileButton.tooltip"));
		this.compileButton.setBounds(MIN_WIDTH, 0, MAX_WIDTH - (MIN_WIDTH * 2),
				BUTTON_HEIGHT);
		this.compileButton.setFont(smallButtonFont);
		this.compileButton.setAlignmentX(LEFT_ALIGNMENT);
		this.compileButton.setText(Language.get("compileButton.title"));

		this.compileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					// guardar el codigo fuente
					File sourcePath = saveSourceCode();
					// COMPILA EN EL DIRECTORIO POR DEFAULT + LA RUTA DEL
					// PACKAGE
					Collection<File> inFiles = createClassFiles(sourcePath);
					if (inFiles != null) {
						/*
						 * transformar el codigo fuente, que no tiene errores,
						 * para que los println aparezcan en una ventana. La
						 * transformaci�n no deberia generar errores.
						 */
						writeSourceFile(
								sourcePath,
								AgregadorDeConsola.getInstance().transformar(
										readSourceFile(sourcePath)));
						// volver a compilar, ahora con el codigo transformado

						inFiles = createClassFiles(sourcePath);
						if (inFiles != null) {
							HelperEditor.setCurrentMode(Training.getInstance());
							createJarFile(inFiles);

							System.out.println("INSTALLPATH="
									+ Settings.getInstallPath());
							System.out.println("SE ENVIA ROBOT:"
									+ HelperEditor.currentRobotPackage + "."
									+ HelperEditor.currentRobotName);

							// si quiere seleccionar enemigos
							if (Settings.getProperty("level.default").equals(
									Language.get("level.four"))) {
								try {
									DialogSelectEnemies.getInstance();
								} catch (NoEnemiesException e2) {
									new MessageDialog(Language
											.get("robot.noEnemies"),
											MessageType.ERROR);
								}
								return;
							} else {
								callBatalla(null, null,Training.getInstance());
							}
						} else {
							System.out
									.println("Error en codigo transformado por AgregadorDeConsola");
						}
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}

			/*
			 * crea un jar con todas las clases del robot. el nombre del jar es
			 * el nombre del robot
			 */
			private void createJarFile(Collection<File> inFiles)
					throws FileNotFoundException, IOException {
				File jarFile = new File(basePathRobots,
						HelperEditor.currentRobotName + ".jar");
				if (jarFile.exists()) {
					jarFile.delete();
				}
				System.out.println("Path del JAR ==" + jarFile);
				jarFile.createNewFile();
				FileOutputStream fos = new FileOutputStream(jarFile);
				BufferedOutputStream bo = new BufferedOutputStream(fos);

				Manifest manifest = new Manifest();
				manifest.getMainAttributes().put(
						Attributes.Name.MANIFEST_VERSION, "1.0");
				JarOutputStream jarOutput = new JarOutputStream(fos, manifest);
				int basePathLength = basePathRobots.getAbsolutePath().length() + 1; // +1
																					// para
																					// incluir
																					// al
																					// "/"
																					// final
				byte[] buf = new byte[1024];
				int anz;
				try {
					// para todas las clases...
					for (File inFile : inFiles) {
						BufferedInputStream bi = new BufferedInputStream(
								new FileInputStream(inFile));
						try {
							String relative = inFile.getAbsolutePath()
									.substring(basePathLength);
							// copia y agrega el archivo .class al jar
							JarEntry je2 = new JarEntry(relative);
							jarOutput.putNextEntry(je2);
							while ((anz = bi.read(buf)) != -1) {
								jarOutput.write(buf, 0, anz);
							}
							jarOutput.closeEntry();
						} finally {
							try {
								bi.close();
							} catch (IOException ignored) {
							}
						}
					}
				} finally {
					try {
						jarOutput.close();
					} catch (IOException ignored) {
					}
					try {
						fos.close();
					} catch (IOException ignored) {
					}
					try {
						bo.close();
					} catch (IOException ignored) {
					}
				}
			}
		});
		compileButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				e.getComponent().setCursor(
						Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				e.getComponent().setCursor(Cursor.getDefaultCursor());
			}
		});

	}

	/**
	 * Recibe un archivo conteniendo codigo fuente java, y crea el .class
	 * correspondiente
	 * 
	 * @param sourcePath
	 *            El archivo .java
	 * @return Un archivo conteniendo el path al .class generado, o null si no
	 *         fue posible compilar porque hubo errores en el codigo fuente.
	 */
	private Collection<File> createClassFiles(File sourcePath)
			throws Exception, IOException {
		Collection<File> f = CompileString.compile(sourcePath, basePathRobots);
		if (CompileString.hasError()) {
			int cantErrores = 0;
			for (Diagnostic<?> diag : CompileString.diagnostics) {
				if (!diag.getKind().equals(Kind.WARNING)) {
					int line = (int) diag.getLineNumber();
					int col = (int) diag.getColumnNumber();
					if (line > 0 && col > 0) {
						highlightCode(line, col);
						cantErrores++;
					}
				}
			}
			if (cantErrores > 0) {
				new MessageDialog(Language.get("compile.error"),
						MessageType.ERROR);
			}
			return null;
		} else {
			return f;
		}
	}

	protected static String[] getWindowsCommand(Integer roundsNumber,
			String initialPosition, Mode mode) {
		

		String[] cmd = new String[14];
		cmd[0] = "cmd.exe";
		cmd[1] = "/C";
		cmd[2] = "java";
		cmd[3] = "-cp";
		try {
			cmd[4] = Settings.getPathTo("lib") + File.separator + "rita.jar;"
					+ Settings.getPathTo("lib") + File.separator
					+ "robocode.jar;" + Settings.getPathTo("lib")
					+ File.separator + "robocode.ui-1.7.3.6.jar";
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cmd[5] = "-Xmx512M";
		/*if (mode.equals("training")) {
			cmd[6] = "";
		} else
			cmd[6] = "-DRANDOMSEED=10";*/
		cmd[6] = HelperEditor.getCurrentMode().getRandomMode();
		cmd[7] = "rita.battle.Batalla";
		cmd[8] = Settings.getInstallPath(); // 1er argumento
		cmd[9] = HelperEditor.currentRobotPackage + "."
				+ HelperEditor.currentRobotName; // 2do argumento
		cmd[10] = BatallaConfig.chooseEnemy(Settings
				.getProperty("level.default")); // 3er argumento
		cmd[11] = roundsNumber != null ? roundsNumber.toString() : Integer
				.toString(Batalla.NUMBER_OF_ROUNDS); // 4to argumento Número de
														// rondas
		cmd[12] = initialPosition;
		cmd[13] = mode.toString();
		return cmd;
	}

	protected static String[] getUnixCommand(Integer roundsNumber,
			String initialPosition, Mode mode) {
		String[] cmd = new String[12];
		cmd[0] = "java";
		cmd[1] = "-cp";
		cmd[2] = Settings.getInstallPath() + "lib/rita.jar:"
				+ Settings.getInstallPath() + "lib/robocode.jar:"
				+ Settings.getInstallPath() + "lib/robocode.ui-1.7.3.5.jar";
		cmd[3] = "-Xmx512M";
		cmd[4] = HelperEditor.getCurrentMode().getRandomMode();
		cmd[5] = "rita.battle.Batalla";
		cmd[6] = Settings.getInstallPath(); // 1er argumento
		cmd[7] = HelperEditor.currentRobotPackage + "."
				+ HelperEditor.currentRobotName; // 2do argumento
		cmd[8] = BatallaConfig.chooseEnemy(Settings
				.getProperty("level.default")); // 3er argumento
		cmd[9] = roundsNumber != null ? roundsNumber.toString() : Integer
				.toString(Batalla.NUMBER_OF_ROUNDS); // 4to argumento Número de
														// rondas
		cmd[10] = initialPosition;
		cmd[11] = mode.toString();
		return cmd;
	}

	public static void callBatalla(Integer roundsNumber, String initialPosition, Mode mode) {
		String[] cmd;
		final String os = System.getProperty("os.name");
		if (os != null && os.toLowerCase().contains("windows")) {
			cmd = getWindowsCommand(roundsNumber, initialPosition, mode);
		} else {
			cmd = getUnixCommand(roundsNumber, initialPosition, mode);
		}

		System.out.print("[");
		for (String s : cmd) {
			System.out.print(s + " ");
		}
		System.out.println("]");
		Process proc;
		InputStreamReader isr = null;
		BufferedReader br = null;
		try {
			proc = Runtime.getRuntime().exec(cmd);
			isr = new InputStreamReader(proc.getErrorStream());
			br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null) {
				System.out.println("ERROR" + ">" + line);
			}
			// any errors???
			int exitVal = proc.waitFor();
			System.out.println("ExitValue: " + exitVal);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException ignored) {
				}
			} else if (isr != null) {
				try {
					isr.close();
				} catch (IOException ignored) {
				}
			}
		}
	}

	/** Component Listeners **/
	public void componentHidden(ComponentEvent e) {
	}

	public void componentMoved(ComponentEvent e) {
	}

	public void componentResized(ComponentEvent e) {
		/*
		 * no importa que nos digan, el angulo inferior derecho del componente
		 * no se mueve!
		 */
		Point location = new Point(Workspace.getInstance().getWidth(),
				Workspace.getInstance().getHeight());
		location.translate(-this.getWidth() - OFFSET_FROM_RIGHT,
				-this.getHeight() - OFFSET_FROM_BOTTOM);
		this.setLocation(location);
	}

	public void componentShown(ComponentEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
		if (minimized) {
			WorkspaceController.refreshCodeRegion(null);
			codeButton.setText(Language.get("codeButton.title.close"));
			expanded = true;
			enlarger.expand();
		} else {
			codeButton.setText(Language.get("codeButton.title.open"));
			expanded = false;
			enlarger.shrink();
		}
		minimized = !minimized;
	}

	public void mouseDragged(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
		this.loadCursor(e);
	}

	public void mouseExited(MouseEvent e) {
		this.loadCursor(e);
	}

	public void loadCursor(MouseEvent e) {
		e.getComponent().setCursor(
				Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	}

	private void maximizeSourceCode() {
		if (this.getParent() != null) {
			this.setBounds(this.getParent().getWidth() - MAX_WIDTH
					- OFFSET_FROM_RIGHT, this.getParent().getHeight()
					- MAX_HEIGHT - OFFSET_FROM_BOTTOM, MAX_WIDTH, MAX_HEIGHT);
		}
	}

	public void repositionSourceCode(int count) {
		if (this.getParent() != null) {
			this.setBounds(this.getParent().getWidth() - CURRENT_WIDTH
					- OFFSET_FROM_RIGHT, this.getParent().getHeight()
					- CURRENT_HEIGHT - OFFSET_FROM_BOTTOM, CURRENT_WIDTH,
					CURRENT_HEIGHT);
		}
	}

	/**
	 * This animator is responsible for enlarging or shrinking the size of the
	 * MiniMap when expand() or shrink() is called, respectively.
	 */
	private class SourceCodeEnlargerTimer implements ActionListener {
		/** Growth count */
		private int count;
		/** Internal Timer used to animate the opening/closing of source view */
		private javax.swing.Timer timer;

		private final int NUM_STEPS = 20;
		/** absolute value of width growth */
		private final int WIDTH_GROWTH_PER_STEP = (MAX_WIDTH - MIN_WIDTH)
				/ NUM_STEPS;
		/** absolute value of height Growth */
		private final int HEIGHT_GROWTH_PER_STEP = (MAX_HEIGHT - MIN_HEIGHT)
				/ NUM_STEPS;
		/**
		 * Indicates whether the source code view is/was expanding (true) or
		 * shrinking (false)
		 */
		private boolean expand;

		/**
		 * Constuctors an animator that can enlarge or skrink the source code
		 * view
		 */
		public SourceCodeEnlargerTimer() {
			count = 0;
			this.expand = true;
			timer = new Timer(5, this);
		}

		/**
		 * expands/shrinks the source code view untill count is 0 or 15. At 0,
		 * the map is smallest as possible and at 15, the map is largest as
		 * possible
		 */
		public void actionPerformed(ActionEvent e) {
			if (count <= 0) {
				timer.stop();
			} else if (count >= NUM_STEPS) {
				timer.stop();
				maximizeSourceCode();
			} else {
				if (expand) {
					count = count + 1;
				} else {
					count = count - 1;
				}
				CURRENT_WIDTH = MIN_WIDTH + count * WIDTH_GROWTH_PER_STEP;

				// para pantalla normal
				// if (ScreenHelper.getSize().equals(ScreenSize.NORMAL))
				CURRENT_HEIGHT = MIN_HEIGHT + count * HEIGHT_GROWTH_PER_STEP;
				// para pantalla peque�a se ajusta distinto
				// if (ScreenHelper.getSize().equals(ScreenSize.SMALL))
				// MAPHEIGHT = DEFAULT_HEIGHT + (count - 5) * dy * 2
				// - (MAPHEIGHT / 5);
				repositionSourceCode(count);
				repaint();
			}
		}

		/**
		 * enlarge this source code view
		 */
		public void expand() {
			this.expand = true;
			count++;
			this.timer.start();
		}

		/**
		 * shrinks this minimap
		 */
		public void shrink() {
			count--;
			this.expand = false;
			this.timer.start();
		}
	}

	public String getText() {
		return paneJavaCode.getText();
	}

	public void setText(String formatted) {
		paneJavaCode.setText(formatted);
	}

	// para indicar en rojo una linea de error.
	public void highlightCode(int line, int col) {

		try {
			// Convertir a una posicion X/Y

			paneJavaCode.getHighlighter().addHighlight(
					paneJavaCode.getLineStartOffset(line - 1),
					paneJavaCode.getLineEndOffset(line - 1),
					new DefaultHighlightPainter(ErrorLineColor));
		} catch (BadLocationException e) {
			e.printStackTrace();
		}

	}

	public void highlightCode(RenderableBlock renderableBlock) {
		try {
			paneJavaCode.getHighlighter().removeAllHighlights();
			int startPos = 0;
			// encontrar la posicion en funcion del numero de repeticion del
			// LABEL
			String labelToFind = renderableBlock.getBlock().getBlockLabel();
			for (int i = 0; i < renderableBlock.getLabelInstanceNumber(); i++) {
				if (startPos > 0)
					startPos += labelToFind.length();
				startPos = paneJavaCode.getText()
						.indexOf(labelToFind, startPos);
			}

			int lastPos = startPos
					+ renderableBlock.getBlock().getBlockLabel().length();
			paneJavaCode.getHighlighter().addHighlight(startPos, lastPos,
					new DefaultHighlightPainter(Color.ORANGE));
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void addBlock(RenderableBlock block) {
	}

	@Override
	public void addBlocks(Collection<RenderableBlock> blocks) {
	}

	@Override
	public void blockDragged(RenderableBlock block) {
	}

	@Override
	public void blockDropped(RenderableBlock block) {
	}

	@Override
	public void blockEntered(RenderableBlock block) {
	}

	@Override
	public void blockExited(RenderableBlock block) {
	}

	@Override
	public Iterable<RenderableBlock> getBlocks() {
		return null;
	}

	@Override
	public JComponent getJComponent() {
		return this;
	}

	@Override
	public void removeBlock(RenderableBlock block) {

	}

	public void setInconsistentCode(boolean error) {
		if (error) {
			paneJavaCode.setBackground(Color.GRAY);
			compileButton.setEnabled(false);
		} else {
			paneJavaCode.setBackground(Color.WHITE);
			compileButton.setEnabled(true);
		}
	}

	@Override
	public void workspaceEventOccurred(WorkspaceEvent event) {
		update();
	}

	public void update() {
		if (!minimized) {
			WorkspaceController.refreshCodeRegion(null);
		}
	}

	public static SourceCode getInstance() {
		return SINGLETON.INSTANCE;
	}

	public boolean isExpanded() {
		return expanded;
	}

	/** Returns an ImageIcon, or null if the path was invalid. */
	protected static ImageIcon createImageIcon(String path) {
		java.net.URL imgURL = DialogNewRobot.class.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			System.err.println(Language.get("robot.error.fileNotFound") + " "
					+ path);
			return null;
		}
	}

	public void setParent(JFrame frame) {
		this.frameParent=frame;		
	}
}
