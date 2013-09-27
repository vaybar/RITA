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

package rita.settings;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.FileUtils;

/**
 * Esta clase administra las variables que son almacenadas en el sistema, como
 * el nivel en el que va a trabajar el usuario y cualquier otro parametro de
 * configuracion RITA.
 * 
 * @author Vanessa Aybar Rosales
 */
public class Settings {
	public static final String CONFIG_DIR = "RITA";
	
    protected static final String USER_HOME_SYSPROPERTY = "user.home";

    private static String installPath;
	private static String robotsPath;
	private static String mvnResourcesPath=File.separator+"src"+File.separator+"main"+File.separator+"resources";
	private static final String RITA_CONFIG_FILE = "//support"+File.separator+"rita.properties";
	// private static final String LANGUAGE = "JAVA";
	private static final Properties props = new Properties();
	private static final Set<String> userPropNames = new HashSet<String>();

	private Settings() {
	}

	public static String getMvnResourcesPath() {
		return mvnResourcesPath;
	}

	public static void setMvnResourcesPath(String mvnResourcesPath) {
		Settings.mvnResourcesPath = mvnResourcesPath;
	}

	private static final void loadProperties() throws FileNotFoundException, IOException {
		FileReader propsReader = null, userPropsReader = null;
		try {
			File propsFile = new File(installPath,  RITA_CONFIG_FILE);
			if(propsFile.exists()) {
				propsReader = new FileReader(propsFile); 
				// primero: cargar los valores por defecto
				props.load(propsReader);
			}
			else { //lo carga de la estructura del maven
				File propsFile2 = new File(installPath+mvnResourcesPath,  RITA_CONFIG_FILE);
				propsReader = new FileReader(propsFile2); 
				// primero: cargar los valores por defecto
				props.load(propsReader);
			}
			
			// segundo: sobreescribir con los valores del usuario (si existen)
			Properties userProps = new Properties();
			File userPropsFile = new File(getUserConfigPath(), RITA_CONFIG_FILE);
			if(userPropsFile.exists()) {
				userPropsReader = new FileReader(userPropsFile);
				userProps.load(userPropsReader);
				userPropNames.clear();
				for(Entry<Object, Object> kv : userProps.entrySet()) {
					props.setProperty((String)kv.getKey(), (String)kv.getValue());
					userPropNames.add((String)kv.getKey());
				}
			}
		} finally {
			if(propsReader!=null) {
				try { propsReader.close(); } catch(IOException ignored) { }
			}
			if(userPropsReader!=null) {
				try { userPropsReader.close(); } catch(IOException ignored) { }
			}
		}
	}

	public static String getRobotsPath() {
		return robotsPath;
	}

	public static void saveAndCloseFile() throws SecurityException, IOException {
		if(!validateSettings()) {
			resetSettings();
		}
		processPackageName(Settings.getProperty("defaultpackage"));
		FileWriter propsFile = null;
		Properties userProps = new Properties();
		try {
			for(Entry<Object, Object> kv : props.entrySet()) {
				if(userPropNames.contains((String)kv.getKey())) {
					userProps.setProperty((String)kv.getKey(), (String)kv.getValue());
				}
			}
			// si el directorio de configuracion del usuario no existe => crearlo
			File ritaConfigDir;
			if(RITA_CONFIG_FILE.indexOf(File.separator)!=-1) {
				ritaConfigDir = new File(getUserConfigPath(),RITA_CONFIG_FILE.substring(0,RITA_CONFIG_FILE.lastIndexOf(File.separator)));
			} else {
				ritaConfigDir = getUserConfigPath();
			}
			if(!ritaConfigDir.exists()) {
				FileUtils.forceMkdir(ritaConfigDir);
			}
			propsFile = new FileWriter(new File(getUserConfigPath(), RITA_CONFIG_FILE));
			userProps.store(propsFile, null);
		} finally {
			if(propsFile!=null) {
				try {
					propsFile.close();
				} catch (IOException ignored) { }
			}
		}

	}

	public static void setProperty(String key, String value) {
		props.put(key, value);
		userPropNames.add(key);
	}

	public static String getProperty(String key) {
//		System.out.println("Se obtiene..."+props.getProperty(key));
		return props.getProperty(key);
	}

	public static void setInstallPath(String newInstallPath) throws FileNotFoundException, IOException {
		installPath = newInstallPath;
		setRobotsPath(newInstallPath);
		loadProperties();
	}

	private static void setRobotsPath(String string) {
		File f=new File(installPath + File.separator + "robots");
		if (f.exists())
			robotsPath = installPath + File.separator + "robots";
		else 
			robotsPath = installPath + mvnResourcesPath + File.separator + "robots";
	}

	public static String getInstallPath() {
		return installPath;
	}

	/**
	 * 
	 * @param appName Nombre de la aplicacion; asume que no hay 2 aplicaciones con el mismo nombre.<br/>
	 * <b>Importante:</b> este metodo puede retornar diferentes resultados para el mismo <i>appName</i> si
	 *  2 aplicaciones que llaman este metodo corren bajo diferentes usuarios.
	 * @return El path completo del directorio en donde se guarda la configuracion para la aplicacion llamada <tt>appName</tt>.
	 * @throws SecurityException 
     * @throws IllegalArgumentException si alguno de los parametros es <code>null</code> o vacio.
	 */
	public static File getUserConfigPath() throws SecurityException {
    	final String os = System.getProperty("os.name");
    	if(os!=null && os.toLowerCase().contains("windows")) {
    		return getWindowsConfigPath();
    	} else {
    		return getUnixConfigPath();
    	}
	}

	protected static File getWindowsConfigPath() throws SecurityException {	
		/* Es Windows Vista o Windows 7? */
		String appDataDir = System.getenv("LOCALAPPDATA");
		if(appDataDir==null) {
			/* No tiene definido LOCALAPPDATA, entonces asumimos que es Windows XP -- averiguamos LocalAppData a traves de la hubicacion del dir "Temp" */
			appDataDir = System.getenv("TEMP");
			if(appDataDir == null)
			{
			    appDataDir = System.getProperty("java.io.tmpdir");
			}
			/* quitamos "/Temp" y agregamos "/Application Data" */
			appDataDir=appDataDir.substring(0,appDataDir.lastIndexOf(java.io.File.pathSeparatorChar))+java.io.File.pathSeparator+"Application Data";
		}
		File configHomeDir = new File(appDataDir,CONFIG_DIR);
		if(!configHomeDir.exists()) {
			if(!configHomeDir.mkdir()) {
				throw new SecurityException();			
			}
		} else if(!configHomeDir.canWrite()) {
			throw new SecurityException();
		}
		return configHomeDir;
	}

	protected static File getUnixConfigPath() throws SecurityException {
		File configHomeDir = new File(System.getProperty(USER_HOME_SYSPROPERTY),"."+CONFIG_DIR);
		if(!configHomeDir.exists()) {
			if(!configHomeDir.mkdir()) {
				throw new SecurityException();			
			}
		} else if(!configHomeDir.canWrite()) {
			throw new SecurityException();
		}
		return configHomeDir;
	}

	public static final void resetSettings() {
		if(Settings.getProperty("level.default") == null || Settings.getProperty("level.default").isEmpty()) {
			Settings.getProperty(Language.get("level.two"));
		}
		if(Settings.getProperty("defaultpackage") == null || Settings.getProperty("defaultpackage").isEmpty()) { 
			Settings.setProperty("defaultpackage","robots-"+System.currentTimeMillis()%10000);			
		}
	}

	public static boolean validateSettings() {
		return (Settings.getProperty("level.default") != null && !Settings.getProperty("level.default").isEmpty() ||
			    Settings.getProperty("defaultpackage") != null && !Settings.getProperty("defaultpackage").isEmpty()); 
	}				
	
	private static void processPackageName(String pkgName) throws IOException {
		String directories = pkgName.replace(".", File.separator);
		System.out.println("creacion de directorio:" + Settings.getInstallPath() + File.separator + "robots" + File.separator + directories);
		final String fullPath = Settings.getInstallPath() + File.separator + "robots" + File.separator + directories;
		if (!new File(fullPath).exists()) {
			boolean directoriosCreados = new File(fullPath).mkdirs();
			if (!directoriosCreados) {
				throw new IOException("Error al crear directorios para el package.");
			}
		}
	}
	
	public static String getPathTo(String directory) throws FileNotFoundException{
		String path=Settings.getInstallPath()+File.separator+directory;
		if (!new File(path).exists()){
			path=Settings.getInstallPath()+Settings.getMvnResourcesPath()+File.separator+directory;
			if (!new File(path).exists()){
				throw new FileNotFoundException(directory);
			}
		}
		return path;
		
	}
		
}
