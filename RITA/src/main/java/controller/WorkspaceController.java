package controller;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.FontUIResource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.barenca.jastyle.ASFormatter;
import net.barenca.jastyle.FormatterHelper;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import rita.grammar.JavaCodeGenerator;
import rita.rule.ComparatorRule;
import rita.rule.DeclarationRule;
import rita.rule.DeclarationUseRule;
import rita.rule.FunctionRule;
import rita.rule.MethodWithEventRule;
import rita.rule.OperatorRule;
import rita.rule.ParameterRule;
import rita.rule.ReturnBlockRule;
import rita.settings.HelperEditor;
import rita.settings.Language;
import rita.settings.RobocodeSetting;
import rita.settings.Settings;
import rita.ui.component.DialogPleaseWait;
import rita.ui.component.DialogSelectEnemies;
import rita.ui.component.DialogSelectRobot;
import rita.ui.component.DialogSettings2;
import rita.ui.component.RMenu;
import rita.util.RitaUtilities;
import rita.widget.SourceCode;
import rita.widget.Splash;
import workspace.Page;
import workspace.SearchBar;
import workspace.SearchableContainer;
import workspace.TrashCan;
import workspace.Workspace;
import codeblocks.BlockConnectorShape;
import codeblocks.BlockGenus;
import codeblocks.BlockLinkChecker;
import codeblocks.CommandRule;
import codeblocks.SocketRule;

/**
 * 
 * The WorkspaceController is the starting point for any program using Open
 * Blocks. It contains a Workspace (the block programming area) as well as the
 * Factories (the palettes of blocks), and is responsible for setting up and
 * laying out the overall window including loading some WorkspaceWidgets like
 * the TrashCan.
 * 
 * @author Ricarose Roque
 */
public class WorkspaceController {

	private static String LANG_DEF_FILEPATH;

	private static Element langDefRoot;

	// flags
	private boolean isWorkspacePanelInitialized = false;

	/** The single instance of the Workspace Controller **/
	protected static Workspace workspace;

	protected static JPanel workspacePanel;

	// flag to indicate if a new lang definition file has been set
	private boolean langDefDirty = true;

	// flag to indicate if a workspace has been loaded/initialized
	private boolean workspaceLoaded = false;

	// menu principal superior de la aplicacion
	public static RMenu rMenu;

	// componente comprendido por el panel que contiene el c�digo Java
	// generado
	public static SourceCode sourceCode = null;

	/**
	 * Constructs a WorkspaceController instance that manages the interaction
	 * with the codeblocks.Workspace
	 * 
	 */

	public WorkspaceController() {
		workspace = Workspace.getInstance();
		// verificar que settings es valido, sino todo podria explotar mas tarde
		if (!Settings.validateSettings()) {
			new DialogSettings2(frame);
		}
	}


	// //////////////////
	// LANG DEF FILE //
	// //////////////////

	/**
	 * Sets the file path for the language definition file, if the language
	 * definition file is located in
	 */
	public void setLangDefFilePath(String path) {

		LANG_DEF_FILEPATH = path;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		Document doc;
		try {
			builder = factory.newDocumentBuilder();

			try {
				// trata de cargar el XML descriptor del tipo de robot, por
				// ejemplo: junior_code.xml
				doc = builder.parse(new File("support//" + path));// builder.parse(new
				// langDefRoot = doc.getDocumentElement();
				// // set the dirty flag for the language definition file
				// // to true now that a new file has been set
				// langDefDirty = true;

			} catch (FileNotFoundException e) {
				try {
					// aqui entra s�lo cuando trata de hacer un open desde el
					// IDE
					doc = builder.parse(new File(Settings.getInstallPath()
							+ Settings.getMvnResourcesPath() + File.separator
							+ "support" + File.separator + path));

				} catch (FileNotFoundException f) {
					// carga del archivo desde el JAR
					InputStream is = getClass().getClassLoader()
							.getResourceAsStream(path);
					doc = builder.parse(new InputSource(is));
				}
			}

			langDefRoot = doc.getDocumentElement();
			// set the dirty flag for the language definition file
			// to true now that a new file has been set
			langDefDirty = true;

			Element projectRoot = doc.getDocumentElement();

			// load the canvas (or pages and page blocks if any) blocks from the
			// save file also load drawers, or any custom drawers from file. if
			// no custom
			// drawers
			// are present in root, then the default set of drawers is loaded
			// from
			// langDefRoot
			// workspace.loadWorkspaceFrom(projectRoot, langDefRoot);
			//
			// workspaceLoaded = true;

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Sets the contents of the Lang Def File to the specified String
	 * langDefContents
	 * 
	 * @param langDefContents
	 *            String contains the specification of a language definition
	 *            file
	 */
	public void setLangDefFileString(String langDefContents) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		Document doc;
		try {
			builder = factory.newDocumentBuilder();
			doc = builder.parse(new InputSource(new StringReader(
					langDefContents)));
			langDefRoot = doc.getDocumentElement();

			// set the dirty flag for the language definition file
			// to true now that a new file has been set
			langDefDirty = true;

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sets the Lang Def File to the specified File langDefFile.
	 * 
	 * @param langDefFile
	 *            File contains the specification of the a language definition
	 *            file.
	 */
	public void setLangDefFile(File langDefFile) {
		// LANG_DEF_FILEPATH = langDefFile.getCanonicalPath();

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		Document doc;
		try {
			builder = factory.newDocumentBuilder();

			doc = builder.parse(langDefFile);

			langDefRoot = doc.getDocumentElement();

			// set the dirty flag for the language definition file
			// to true now that a new file has been set
			langDefDirty = true;

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Loads all the block genuses, properties, and link rules of a language
	 * specified in the pre-defined language def file.
	 * 
	 * @param root
	 *            Loads the language specified in the Element root
	 */
	public void loadBlockLanguage(Element root) {
		// load connector shapes
		// MUST load shapes before genuses in order to initialize connectors
		// within
		// each block correctly
		BlockConnectorShape.loadBlockConnectorShapes(root);

		// load genuses
		BlockGenus.loadBlockGenera(root);

		// load rules
		BlockLinkChecker.addRule(new ParameterRule());
		BlockLinkChecker.addRule(new ReturnBlockRule());
		BlockLinkChecker.addRule(new DeclarationRule());
		BlockLinkChecker.addRule(new DeclarationUseRule());
		BlockLinkChecker.addRule(new MethodWithEventRule());

		BlockLinkChecker.addRule(new OperatorRule());
		BlockLinkChecker.addRule(new ComparatorRule());
		BlockLinkChecker.addRule(new FunctionRule());

		BlockLinkChecker.addRule(new CommandRule());
		BlockLinkChecker.addRule(new SocketRule());

		// set the dirty flag for the language definition file
		// to false now that the lang file has been loaded
		langDefDirty = false;
	}

	/**
	 * Resets the current language within the active Workspace.
	 * 
	 */
	public void resetLanguage() {
		// clear shape mappings
		BlockConnectorShape.resetConnectorShapeMappings();
		// clear block genuses
		BlockGenus.resetAllGenuses();
		// clear all link rules
		BlockLinkChecker.reset();
	}

	// //////////////////////
	// SAVING AND LOADING //
	// //////////////////////

	/**
	 * Returns the save string for the entire workspace. This includes the block
	 * workspace, any custom factories, canvas view state and position, pages
	 * 
	 * @return the save string for the entire workspace.
	 */
	// public String getSaveString() {
	// StringBuffer saveString = new StringBuffer();
	// // append the save data
	// saveString.append("<?xml version=\"1.0\" encoding=\"ISO-8859\"?>");
	// saveString.append("\r\n");
	// // dtd file path may not be correct...
	// //
	// saveString.append("<!DOCTYPE StarLogo-TNG SYSTEM \""+SAVE_FORMAT_DTD_FILEPATH+"\">");
	// // append root node
	// saveString.append("<CODEBLOCKS>");
	// saveString.append(workspace.getSaveString());
	// saveString.append("</CODEBLOCKS>");
	// return saveString.toString();
	// }

	/**
	 * Loads a fresh workspace based on the default specifications in the
	 * language definition file. The block canvas will have no live blocks.
	 */
	public void loadFreshWorkspace() {
		// need to just reset workspace (no need to reset language) unless
		// language was never loaded
		// reset only if workspace actually exists
		if (workspaceLoaded)
			resetWorkspace();

		if (langDefDirty)
			loadBlockLanguage(langDefRoot);

		workspace.loadWorkspaceFrom(null, langDefRoot);

		workspaceLoaded = true;
	}

	/**
	 * Loads the programming project from the specified file path. This method
	 * assumes that a Language Definition File has already been specified for
	 * this programming project.
	 * 
	 * @param path
	 *            String file path of the programming project to load
	 */
	public void loadProjectFromPath(String path) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		Document doc;

		try {
			builder = factory.newDocumentBuilder();

			InputStream is = getClass().getClassLoader().getResourceAsStream(
					"support" + File.separator + path);

			if (is == null) {
				// trato de cargarlo segun el path que me dan porque puede ser
				// el archivo de mi robot guardado (con toda la ruta)
				try {
					doc = builder.parse(new File(path));
				} catch (FileNotFoundException ex) {
					doc = builder.parse(new File("support" + File.separator
							+ path));
				}
			} else {

				doc = builder.parse(new InputSource(is));
			}
			Element projectRoot = doc.getDocumentElement();

			// load the canvas (or pages and page blocks if any) blocks from the
			// save file also load drawers, or any custom drawers from file. if
			// no custom
			// drawers
			// are present in root, then the default set of drawers is loaded
			// from
			// langDefRoot
			workspace.loadWorkspaceFrom(projectRoot, langDefRoot);

			workspaceLoaded = true;

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Loads the programming project specified in the projectContents. This
	 * method assumes that a Language Definition File has already been specified
	 * for this programming project.
	 * 
	 * @param projectContents
	 */
	public void loadProject(String projectContents) {
		// need to reset workspace and language (only if new language has been
		// set)

		// reset only if workspace actually exists
		if (workspaceLoaded)
			resetWorkspace();

		if (langDefDirty)
			loadBlockLanguage(langDefRoot);

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		Document doc;
		try {
			builder = factory.newDocumentBuilder();
			doc = builder.parse(new InputSource(new StringReader(
					projectContents)));
			Element root = doc.getDocumentElement();
			// load the canvas (or pages and page blocks if any) blocks from the
			// save file
			// also load drawers, or any custom drawers from file. if no custom
			// drawers
			// are present in root, then the default set of drawers is loaded
			// from
			// langDefRoot
			workspace.loadWorkspaceFrom(root, langDefRoot);

			workspaceLoaded = true;

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Loads the programming project specified in the projectContents String,
	 * which is associated with the language definition file contained in the
	 * specified langDefContents. All the blocks contained in projectContents
	 * must have an associted block genus defined in langDefContents.
	 * 
	 * If the langDefContents have any workspace settings such as pages or
	 * drawers and projectContents has workspace settings as well, the workspace
	 * settings within the projectContents will override the workspace settings
	 * in langDefContents.
	 * 
	 * NOTE: The language definition contained in langDefContents does not
	 * replace the default language definition file set by: setLangDefFilePath()
	 * or setLangDefFile().
	 * 
	 * @param projectContents
	 * @param langDefContents
	 *            String XML that defines the language of projectContents
	 */
	public void loadProject(String projectContents, String langDefContents) {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		Document projectDoc;
		Document langDoc;
		try {
			builder = factory.newDocumentBuilder();
			projectDoc = builder.parse(new InputSource(new StringReader(
					projectContents)));
			Element projectRoot = projectDoc.getDocumentElement();
			langDoc = builder.parse(new InputSource(new StringReader(
					projectContents)));
			Element langRoot = langDoc.getDocumentElement();

			// need to reset workspace and language (if langDefContents != null)
			// reset only if workspace actually exists
			if (workspaceLoaded)
				resetWorkspace();

			if (langDefContents == null)
				loadBlockLanguage(langDefRoot);
			else
				loadBlockLanguage(langRoot);
			// TODO should verify that the roots of the two XML strings are
			// valid
			workspace.loadWorkspaceFrom(projectRoot, langRoot);

			workspaceLoaded = true;

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Resets the entire workspace. This includes all blocks, pages, drawers,
	 * and trashed blocks. Also resets the undo/redo stack. The language (i.e.
	 * genuses and shapes) is not reset.
	 */
	public void resetWorkspace() {
		// clear all pages and their drawers
		// clear all drawers and their content
		// clear all block and renderable block instances
		workspace.reset();
		// clear action history
		// rum.reset();
		// clear runblock manager data
		// rbm.reset();
	}

	/**
	 * This method creates and lays out the entire workspace panel with its
	 * different components. Workspace and language data not loaded in this
	 * function. Should be call only once at application startup.
	 */
	private void initWorkspacePanel() {
		sourceCode = SourceCode.getInstance();
		workspace.addWorkspaceListener(sourceCode);
		workspace.addWidget(sourceCode, true, true);

		// add trashcan and prepare trashcan images
		ImageIcon tc = new ImageIcon(getClass()
				.getResource("/images/trash.png"));
		ImageIcon openedtc = new ImageIcon(getClass().getResource(
				"/images/trash_open.png"));
		TrashCan trash = new TrashCan(tc.getImage(), openedtc.getImage());
		workspace.addWidget(trash, true, true);

		workspacePanel = new JPanel();
		workspacePanel.setLayout(new BorderLayout());
		workspacePanel.add(workspace, BorderLayout.CENTER);

		isWorkspacePanelInitialized = true;

	}

	/**
	 * Returns the JComponent of the entire workspace.
	 * 
	 * @return the JComponent of the entire workspace.
	 */
	public JComponent getWorkspacePanel() {
		if (!isWorkspacePanelInitialized)
			initWorkspacePanel();
		return workspacePanel;
	}

	public JMenuBar getMenuBar() {
		return rMenu.getMenuBar();
	}

	/**
	 * Returns an unmodifiable Iterable of SearchableContainers
	 * 
	 * @return an unmodifiable Iterable of SearchableContainers
	 */
	public Iterable<SearchableContainer> getAllSearchableContainers() {
		return workspace.getAllSearchableContainers();
	}

	// ///////////////////////////////////
	// TESTING CODEBLOCKS SEPARATELY //
	// ///////////////////////////////////
	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event-dispatching thread.
	 */
	// private static void createAndShowGUI(WorkspaceController wc) {
	// This is the main window
	private static JFrame frame = null;

	// private static JButton saveButton;
	private static void createAndShowGUI(WorkspaceController wc) {

		System.out.println("Creating GUI...");

		// Create and set up the window.
		frame = new JFrame(Language.get("titleApp"));

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setState(Frame.NORMAL);

		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension dimension = toolkit.getScreenSize();
		frame.setSize(dimension);

		frame.setIconImage(toolkit.getImage(WorkspaceController.class
				.getResource("/images/rita_ico_frame.jpg")));

		// agregamos el menu Robocode Menu (izquierda)
		rMenu = new RMenu(frame, wc);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setJMenuBar(rMenu.getMenuBar());

		frame.setVisible(true);
	}

	// Este m�todo es llamado cuando un Robot fue seleccionado para comenzar
	public static void enableWorkspace(final WorkspaceController wc,
			final String robotType, final String robotName,
			final String robotPackage, final String filename) {
		final boolean createNewRobot = (filename == null || filename.isEmpty());
		// mostrar la ventanita de "espere mientras inicializamos el workspace"
		// ...
		final DialogPleaseWait pleaseWait = new DialogPleaseWait(frame,
				createNewRobot ? Language.get("creatingRobot")
						: Language.get("loadingRobot"));
		// ... y crear el workspaces en otro thread, para que no bloquee a la
		// interface
		DialogSelectEnemies.clean();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				workspace.removePages();
				SearchBar searchBar = workspace.getSearchBar();
				searchBar.getComponent().setEnabled(true);
				if (createNewRobot) {

					HelperEditor.currentRobotName = robotName;
					HelperEditor.currentRobotPackage = Settings
							.getProperty("defaultpackage");
					HelperEditor.currentRobotType = robotType;
					wc.setLangDefFilePath(RobocodeSetting.getConfig(robotType));
					wc.langDefDirty = true;
					wc.loadFreshWorkspace();

					// se crea un nuevo workspace con el robot minimo inicial,
					// descartando al workspace actual
					wc.loadProjectFromPath(RobocodeSetting
							.getStarterCode(robotType));
					WorkspaceController.workspace.changePageName(
							HelperEditor.defaultPageName, robotName);
					updateMenu();
				} else {
					// realiza la carga de un proyecto de robot si as� fue
					// indicado
					HelperEditor.currentRobotName = getRobotNameFromFile(filename);
					HelperEditor.currentRobotPackage = getRobotPackageFromFile(filename);
					HelperEditor.currentRobotType = getRobotTypeFromFile(filename);

					wc.setLangDefFilePath(RobocodeSetting
							.getConfig(HelperEditor.currentRobotType));
					wc.langDefDirty = true;
					wc.loadFreshWorkspace();

					wc.loadProjectFromPath(filename);
				}
				refreshCodeRegion(WorkspaceController.workspace
						.getPageNamed(HelperEditor.currentRobotName));
				for (SearchableContainer con : wc.getAllSearchableContainers()) {
					searchBar.addSearchableContainer(con);
				}
				frame.add(wc.getWorkspacePanel(), BorderLayout.CENTER);
				frame.validate();
				if (robotName != null) {
					frame.setTitle(Language.get("robot") + ":\"" + robotName
							+ "\" @ " + Language.get("titleApp"));
				} else {
					frame.setTitle(Language.get("titleApp"));
				}
				RMenu.update();
				// listo, terminamos de crear el workspace, cerrar la ventanita
				// de espera
				pleaseWait.closeDialog();
			}
		});
	}

	private static void updateMenu() {
		RMenu.update();
	}

	// REVISAR
	// actualmente no se usa pero puede servir para recuperar un tipo de robot
	// guardado en
	// el XML que describe al robot. En este m�todo se busca un atributo:
	// page-type
	@SuppressWarnings("unused")
	private static String getRobotTypeFromFile(String filename) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		Document doc;

		try {
			builder = factory.newDocumentBuilder();
			File f = new File(filename);
			doc = builder.parse(f);
			Element projectRoot = doc.getDocumentElement();
			NodeList pagesRoot = projectRoot.getElementsByTagName("Pages");
			if (pagesRoot != null) {
				/*
				 * isBlankPage denotes if the page being loaded is a default
				 * blank page in other words, the project did not specify any
				 * pages for their environment. EvoBeaker does this
				 */
				Node pagesNode = pagesRoot.item(0);
				if (pagesNode == null) {
					return null; // short-circuit exit if there's nothing to
									// load
				}
				NodeList pages = pagesNode.getChildNodes();
				Node pageNode = pages.item(0);
				if (pageNode.getNodeName().equals("Page")) { // a page entry
					Pattern attrExtractor = Pattern.compile("\"(.*)\"");
					Node opt_item = pageNode.getAttributes().getNamedItem(
							"page-type");
					if (opt_item != null) {
						Matcher nameMatcher = attrExtractor.matcher(opt_item
								.toString());
						if (nameMatcher.find()) {
							return nameMatcher.group(1);

						}
					}
				}
			}

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;

	}

	// Retorna el nombre de un robot a partir de su archivo descriptor XML
	private static String getRobotNameFromFile(String filename) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		Document doc;

		try {
			builder = factory.newDocumentBuilder();
			File f = new File(filename);
			doc = builder.parse(f);
			Element projectRoot = doc.getDocumentElement();
			NodeList pagesRoot = projectRoot.getElementsByTagName("Pages");
			if (pagesRoot != null) {
				/*
				 * isBlankPage denotes if the page being loaded is a default
				 * blank page in other words, the project did not specify any
				 * pages for their environment. EvoBeaker does this
				 */
				Node pagesNode = pagesRoot.item(0);
				if (pagesNode == null) {
					return null; // short-circuit exit if there's nothing to
									// load
				}
				NodeList pages = pagesNode.getChildNodes();
				Node pageNode = pages.item(0);
				if (pageNode.getNodeName().equals("Page")) { // a page entry
					Pattern attrExtractor = Pattern.compile("\"(.*)\"");
					Node opt_item = pageNode.getAttributes().getNamedItem(
							"page-name");
					if (opt_item != null) {
						Matcher nameMatcher = attrExtractor.matcher(opt_item
								.toString());
						if (nameMatcher.find()) {
							return nameMatcher.group(1);

						}
					}
				}
			}

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;

	}

	// Retorna el nombre del paquete de un robot a partir de su archivo
	// descriptor XML
	private static String getRobotPackageFromFile(String filename) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		Document doc;

		try {
			builder = factory.newDocumentBuilder();
			File f = new File(filename);
			doc = builder.parse(f);
			Element projectRoot = doc.getDocumentElement();
			NodeList pagesRoot = projectRoot.getElementsByTagName("Pages");
			if (pagesRoot != null) {
				/*
				 * isBlankPage denotes if the page being loaded is a default
				 * blank page in other words, the project did not specify any
				 * pages for their environment. EvoBeaker does this
				 */
				Node pagesNode = pagesRoot.item(0);
				if (pagesNode == null) {
					return null; // short-circuit exit if there's nothing to
									// load
				}
				NodeList pages = pagesNode.getChildNodes();
				Node pageNode = pages.item(0);
				if (pageNode.getNodeName().equals("Page")) { // a page entry
					Pattern attrExtractor = Pattern.compile("\"(.*)\"");
					Node opt_item = pageNode.getAttributes().getNamedItem(
							"page-package");
					if (opt_item != null) {
						Matcher nameMatcher = attrExtractor.matcher(opt_item
								.toString());
						if (nameMatcher.find()) {
							return nameMatcher.group(1);

						}
					}
				}
			}

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;

	}

	private static final void setDefaultResourceFontSize(UIDefaults defaults,
			String resourceKey, int fontSize) {
		FontUIResource font = (FontUIResource) UIManager.get(resourceKey);
		defaults.put(resourceKey,
				new FontUIResource(font.getFontName(), font.getStyle(),
						fontSize));
	}

	/**
	 * modifica el tama�o de font de todos los elementos de la UI al tama�o
	 * deseado
	 */
	private static void setUIFontSize(int fontSize) {
		UIDefaults defaults = UIManager.getDefaults();
		Enumeration<Object> keys = defaults.keys();
		Object key;
		while (keys.hasMoreElements()) {
			key = keys.nextElement();
			if ((key instanceof String) && (((String) key).endsWith(".font"))) {
				setDefaultResourceFontSize(defaults, (String) key, fontSize);
			}
		}
		setDefaultResourceFontSize(defaults, "OptionPane.messageFont", fontSize);
		setDefaultResourceFontSize(defaults, "OptionPane.buttonFont", fontSize);
	}

	private static final void setLookAndFeelThenRun() {
		JFrame.setDefaultLookAndFeelDecorated(true);
		final Locale spanishLocale = new Locale("es", "ES");
		// cambiar el locale por defecto; esto afecta a la ventana (JFrame) pero
		// no a JOptionPane
		Locale.setDefault(spanishLocale);
		// cambiar el locale por defecto del JOptionPane
		JOptionPane.setDefaultLocale(spanishLocale);
		// Crear la GUI en el event-dispatching thread
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					// L&F Substance con color gris claro de fondo, los botones
					// activos son grises
					// UIManager.setLookAndFeel("org.pushingpixels.substance.api.skin.SubstanceDustLookAndFeel");
					// L&F Substance con color gris azulado de fondo, los
					// botones activos son amarillos
					// UIManager.setLookAndFeel("org.pushingpixels.substance.api.skin.SubstanceOfficeBlack2007LookAndFeel");
					UIManager
							.setLookAndFeel("org.pushingpixels.substance.api.skin.SubstanceCeruleanLookAndFeel");
					JDialog.setDefaultLookAndFeelDecorated(true);
					setUIFontSize(14);
				} catch (Throwable e) {
					System.out
							.println("El L&F \"Substance\" no pudo ser encontrado, utilizando el L&F standard de Swing...");
				}
				WorkspaceController wc = new WorkspaceController();
				createAndShowGUI(wc);
				if (!WorkspaceController.robotInWorkspace()) {
					new DialogSelectRobot(frame, wc);
				}
			}
		});
	}

	public static void main(String[] args) throws FileNotFoundException,
			IOException {
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		new Splash();
		Settings.setInstallPath(args[0]);
		setLookAndFeelThenRun();
	}

	public static void initWithLangDefFilePath(final String langDefFilePath) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// Create a new WorkspaceController
				WorkspaceController wc = new WorkspaceController();
				wc.setLangDefFilePath(langDefFilePath);
				wc.loadFreshWorkspace();
				createAndShowGUI(wc);
			}
		});
	}

	/*
	 * Este m�todo permite seleccionar un archivo XML. Se usa para indicar el
	 * archivo descriptor XML del robot con el que se pretende trabajar.
	 */
	public int openFileInWorkspace() {
		String filename = "";
		String dir = "";
		JFileChooser fileChooser = new JFileChooser();
		FileFilter filter1 = new FileNameExtensionFilter(
				Language.get("XMLProject"), "XML", "xml");
		fileChooser.addChoosableFileFilter(filter1);

		int rVal = fileChooser.showOpenDialog(workspace);

		if (rVal == JFileChooser.APPROVE_OPTION) {
			filename = fileChooser.getSelectedFile().getName();
			dir = fileChooser.getCurrentDirectory().toString();
			int ext = filename.lastIndexOf('.');
			if (ext == -1) {
				ext = filename.length();
			}
			String robotName = RitaUtilities.normalizeRobotName(filename
					.substring(0, ext));
			WorkspaceController.enableWorkspace(this, "junior", robotName,
					null, dir + File.separator + filename);
		}
		if (rVal == JFileChooser.CANCEL_OPTION) {
			filename = "";
			dir = "";
		}
		return rVal;
	}

	/*
	 * Este m�todo abre una ventana de di�logo para que el usuario indique
	 * donde guardar su proyecto actual.
	 */
	public void saveWorkspaceToFile() {
		String filename = "";
		String dir = "";
		String currentContentToSave = workspace.getSaveString();
		JFileChooser c = new JFileChooser();
		c.setSelectedFile(new File(HelperEditor.currentRobotName + ".xml"));
		// Demonstrate "Save" dialog:
		int rVal = c.showSaveDialog(workspace);

		if (rVal == JFileChooser.APPROVE_OPTION) {
			filename = c.getSelectedFile().getName();
			dir = c.getCurrentDirectory().toString();
			try {
				// save the project
				OutputStream bos = new FileOutputStream(dir + File.separator
						+ filename);
				bos.write(currentContentToSave.getBytes());
				bos.close();

				// save the source code
				String fileSource = HelperEditor.currentRobotName + ".java";
				bos = new FileOutputStream(dir + File.separator + fileSource);
				bos.write(SourceCode.getInstance().getText().getBytes());
				bos.close();

			} catch (IOException e) {
				System.out.println(e.getMessage());
			}

		}
		if (rVal == JFileChooser.CANCEL_OPTION) {
			filename = "";
			dir = "";
		}

	}

	/*
	 * Actualiza la seccion donde se muestra el codigo fuente de lo que
	 * actualmente se est� editando.
	 * 
	 * @param page especifica la pagina sobre la cual se evaluar�n los bloques
	 * para construir el codigo fuente. En caso que no sea especificado, se toma
	 * la primer p�gina por defecto
	 */
	public static void refreshCodeRegion(Page page) {
		if (page == null && workspace.getBlockCanvas().getPages().size() > 0) {
			page = workspace.getBlockCanvas().getPages().get(0);
		}
		if (page == null) {
			SourceCode.getInstance().setText("");
			return;
		}
		String st = "";
		for (StringBuffer sb : JavaCodeGenerator.generateCode(page)) {
			st += sb.toString();
		}

		ASFormatter formatter = new ASFormatter();
		formatter.setOperatorPaddingMode(false);
		formatter.setJavaStyle();
		StringReader in = new StringReader(st);
		String formatted = FormatterHelper.format(in, formatter);
		SourceCode.getInstance().setText(formatted);

	}

	public static String getJavaCodeAsString(File sourcePath)
			throws IOException {
		FileInputStream is = new FileInputStream(sourcePath);
		Reader r = null;
		char[] buf = new char[4096];
		try {
			r = new InputStreamReader(is);
			StringBuilder sb = new StringBuilder();
			while (true) {
				int n = r.read(buf);
				if (n < 0)
					break;
				sb.append(buf, 0, n);
			}
			return sb.toString();
		} catch (UnsupportedEncodingException ignorada) {
		} finally {
			try {
				r.close();
			} catch (IOException ignorada) {
			}
		}
		return null;
	}

	/*
	 * Devuelve true o false en funcion de si se est� visualizando en el
	 * workspace un conjunto de bloque perteneciente a un robot
	 */
	public static boolean robotInWorkspace() {
		return HelperEditor.currentRobotName != null
				&& HelperEditor.currentRobotName.length() > 0;
	}

}
