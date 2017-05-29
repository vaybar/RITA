package rita.battle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Collection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import rita.net.Protocol;
import rita.settings.HelperEditor;
import rita.settings.Settings;
import rita.ui.component.DialogStartBattle;
import rita.util.RobotWithPositionTemp;

/**
 * Esta clase administra la persistencia de la informacion de los robots en
 * archivos en formato ZIP para ser enviados/recibidos por la red
 * */
public class ZIPRobotProccessor {

	private static String DEFAULT_DATOS_NAME = "datos";
	private static String TXT_EXT = ".txt";
	private static String ZIP_NAME = "mirobot.zip";
	private static String RECEIVED_ZIP_NAME = "received";
	private static String RECEIVED_ZIP_EXT = ".zip";
	public static String PREFIX_MSG = "MSG:";
	

	private static String getFullPathDatos(){
		return System.getProperty("java.io.tmpdir") + File.separator + DEFAULT_DATOS_NAME+HelperEditor.currentRobotName + TXT_EXT;
	}
	
	/**
	 * -recibe un ZIP como un byte[] -el ZIP contiene al menos 2 archivos, uno
	 * data.txt con el nombre del robot y las coordinadas donde se posicionara
	 * al principio de la batalla, otro con el contenido (class, binario) que
	 * corresponde al código del robot
	 * */
	public static void proccess(byte[] data) {
		
		String msg = new String(data);
		String[] dataArr=msg.split(":");
		if (dataArr[0].equals(PREFIX_MSG.substring(0, PREFIX_MSG.length()-1))){			
			DialogStartBattle.updateMessage(dataArr[1]);
			
		} else {
				FileOutputStream fos;
				String fullZipName = System.getProperty("java.io.tmpdir") + File.separator + RECEIVED_ZIP_NAME+ System.currentTimeMillis()+ RECEIVED_ZIP_EXT;
				try {
	
					// lee el contenido en un archivo de nombre ficticio "mirobot.zip"
					fos = new FileOutputStream(fullZipName);
	
					fos.write(data);
					fos.close();
					FileInputStream fis = new FileInputStream(fullZipName);
	
					ZipInputStream zis = new ZipInputStream(fis);
					// get the zipped file list entry
					ZipEntry ze = zis.getNextEntry();
					File newFile = null;
					int contFile = 0;
					String datosRobot=null;
					while (ze != null) {
						// me quedo con la última parte del nombre porque la entrada
						// comprende todo el path desde donde se armo el ZIP
						String fileName = ze.getName().substring(ze.getName().lastIndexOf("/") + 1);
						// el DEFAULT_DATOS_NAME me dara los datos de posicionamiento
						// del robot
						if (!fileName.substring(0,5).equals(DEFAULT_DATOS_NAME)) {
							// el nuevo archivo se ubicará en la carpeta de robots
							contFile++;
							newFile = new File(Settings.getRobotsPath() + File.separator + fileName);
	
						} else {
							// solo en el temporal porque son los datos del robot
							newFile = new File(System.getProperty("java.io.tmpdir") + File.separator + fileName);
							datosRobot=fileName;
							contFile++;
						}
	
						System.out.println("file unzip : " + newFile.getAbsoluteFile());
						// si es necesario creo directorios
						new File(newFile.getParent()).mkdirs();
	
						// escribo el contenido de esta entrada en el ZIP
						FileOutputStream fosR = new FileOutputStream(newFile);
						byte[] buffer = new byte[1024];
	
						int len;
						while ((len = zis.read(buffer)) > 0) {
							fosR.write(buffer, 0, len);
						}
	
						fosR.close();
						ze = zis.getNextEntry();
					}
	
					zis.closeEntry();
					zis.close();
					String[] dataRobot=null;
					switch(contFile){
						case 0: System.out.println("Ërror en la cantidad de archivos recibidos!!!!"); break;
						case 1: dataRobot = readRobotsDataInMemory(datosRobot);break;
						case 2: 
							dataRobot = readRobotsDataInMemory(datosRobot);
							moveRobotToDir(dataRobot);
							break;
					}
	
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			
			
		}
		
		
	}

	// muevo el codigo del robot a el directorio de acuerdo a su paquete
	private static void moveRobotToDir(String[] dataRobot) {
		InputStream inStream = null;
		OutputStream outStream = null;

		try {
			// por las dudas no exista
			File dirDest = new File(Settings.getRobotsPath() + File.separator + dataRobot[1]);
			dirDest.mkdirs();

			File afile = new File(Settings.getRobotsPath() + File.separator + dataRobot[0] + ".class");
			File bfile = new File(Settings.getRobotsPath() + File.separator + dataRobot[1] + File.separator
					+ dataRobot[0] + ".class");

			inStream = new FileInputStream(afile);
			outStream = new FileOutputStream(bfile);

			byte[] buffer = new byte[1024];

			int length;
			// copy the file content in bytes
			while ((length = inStream.read(buffer)) > 0) {
				outStream.write(buffer, 0, length);
			}

			inStream.close();
			outStream.close();

			// delete the original file
			afile.delete();

			System.out.println("File is copied successful!");

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static String[] readRobotsDataInMemory(String datosRobot) throws FileNotFoundException, IOException {
		String dataRobot[] = new String[2];
		// leo los datos que vinieron en el archivo de datos del robot y dejo
		// esos datos en memoria
		FileReader fileReader = new FileReader(System.getProperty("java.io.tmpdir") + File.separator
				+ datosRobot);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		StringBuilder stringB = new StringBuilder();
		String line = "";
		if ((line = bufferedReader.readLine()) != null)
			;

		fileReader.close();
		System.out.println("Contents of file:");
		System.out.println(stringB.toString());
		String nombreRobotRcv = "";
		int xRcv = 0;
		int yRcv = 0;
		int orientRcv = 0;
		String packRcv = null;
		if (line.length() > 0) {
			String[] splitProtocol = line.split(":");
			if (splitProtocol[0].equals(Protocol.ADD.getText()) || splitProtocol[0].equals(Protocol.UPDATE.getText())) {
				// formato: nombre-x-y-orient
				String[] lineSpl = splitProtocol[1].split("-");
				nombreRobotRcv = lineSpl[0];
				xRcv = Integer.parseInt(lineSpl[1]);
				yRcv = Integer.parseInt(lineSpl[2]);
				orientRcv = Integer.parseInt(lineSpl[3]);
				packRcv = lineSpl[4];
				dataRobot[0] = nombreRobotRcv;
				dataRobot[1] = packRcv;
				RobotsToBattle.addRobotToTheList(new RobotWithPositionTemp(nombreRobotRcv, xRcv, yRcv, orientRcv,
						packRcv));

				DialogStartBattle.updateRobot(Protocol.getByValue(splitProtocol[0]), nombreRobotRcv);
			} else {
				if (splitProtocol[0].equals(Protocol.REMOVE.getText())) {
					// String[] idents=splitProtocol[1].split("-");
					/* RobotsToBattle.removeRobot(idents[0], idents[1]); */
					RobotsToBattle.removeRobot(splitProtocol[1], null);
					// queda pendiente el model para diferencie por paquete
					DialogStartBattle.updateRobot(Protocol.REMOVE, splitProtocol[1]);
				}
			}

		}

		return dataRobot;
	}

	/**
	 * crea un zip con un archivo conteniendo los datos propios del robot y los
	 * archvos class (compilados). En realidad lo más común será 1 sólo archivo
	 */
	public static String zipToSend(Protocol prot, RobotWithPositionTemp rTemp, Collection<File> inFiles)
			throws IOException {
		String pathDatos = createDataFile(prot, rTemp);
		FileOutputStream fos = new FileOutputStream(System.getProperty("java.io.tmpdir") + File.separator + ZIP_NAME);
		ZipOutputStream zos = new ZipOutputStream(fos);
		
		FileInputStream fis = new FileInputStream(pathDatos);
//		ZipEntry zipEntry = new ZipEntry(pathDatos.replace('\\', '/'));
		ZipEntry zipEntry = new ZipEntry(pathDatos.substring(pathDatos.lastIndexOf("\\")+1));//replace('\\', '/'));

		
		zos.putNextEntry(zipEntry);
		int length;
		byte[] buffer = new byte[1024];
		while ((length = fis.read(buffer)) > 0) {
			zos.write(buffer, 0, length);
		}

		zos.closeEntry();
		fis.close();

		if (inFiles != null) {
			for (File f : inFiles) {
//				zipEntry = new ZipEntry(f.getAbsolutePath().replace('\\', '/'));
				String fPath=f.getAbsolutePath();
				zipEntry = new ZipEntry(fPath.substring(fPath.lastIndexOf("\\")+1));//replace('\\', '/'));

				zos.putNextEntry(zipEntry);
				FileInputStream fisR = new FileInputStream(f.getAbsolutePath());

				while ((length = fisR.read(buffer)) > 0) {
					zos.write(buffer, 0, length);
				}
				zos.closeEntry();
				fisR.close();
			}
			
		}
		zos.close();
		fos.close();

		return System.getProperty("java.io.tmpdir") + File.separator + ZIP_NAME;

	}

	/**
	 * se crea el archivo con los datos del robot con el siguiente formato:
	 * nombre|X|Y|orientacion
	 * @return path del archivo creado
	 */
	private static String createDataFile(Protocol prot, RobotWithPositionTemp rTemp) throws IOException {
		String path=getFullPathDatos();
		FileOutputStream fos = new FileOutputStream(path);
		StringBuilder datosCompletos = new StringBuilder(prot.getText() + ":" + rTemp.getRobotName());

		if (prot == Protocol.ADD || prot == Protocol.UPDATE) {
			datosCompletos.append("-");
			datosCompletos.append(rTemp.getX());
			datosCompletos.append("-");
			datosCompletos.append(rTemp.getY());
			datosCompletos.append("-");
			datosCompletos.append(rTemp.getOrientation());
			datosCompletos.append("-");
			datosCompletos.append(rTemp.getSelectedPackage());
		}
		System.out.println("DATOS A ENVIAR" + datosCompletos);
		fos.write(datosCompletos.toString().getBytes());
		fos.close();
		return path;
	}

	public static void main(String[] args) {

		// byte[] data = //aqui un zip para probar...
		// proccess(data);
	}

}
