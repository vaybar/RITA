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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Esta clase toma el codigo fuente (.java) de una clase que representa un robot de Robocode,
 * y modifica el codigo fuente para que toda la salida por <code>println()</code> aparezca en una ventana aparte (consola).<br>
 * La consola se implementa como una clase interna del robot, autocontenida, sin dependencias de otros jars, que debe llamarse <code>ConsolaRobot</code>.<br>
 * El color del texto de la ventana coincidira con el color del cuerpo del robot, a menos que el robot no tenga
 * un color determinado, en cuyo caso el texto ser� negro.<br>
 * Si el robot no contiene <code>println()</code>, entonces el codigo fuente no es modificado.<br>
 * <b>IMPORTANTE:</b>Esta clase asume que el codigo fuente de la clase interna <code>ConsolaRobot</code> que implementa a la consola esta guardado
 *  en un archivo llamado <tt>"clase-consola-robot.txt"</tt> accesible directamente desde el classpath.<br>
 * Para utilizar esta clase:
 * <pre>
 * ...
 * // el codigo fuente en java de la clase del robot
 * String textoJavaClaseRobot = "public class AnyRobot extends JuniorRobot { public void run() { } }";
 * // aqui cargamos el codigo de la clase interna que implementa la consola.
 * AgregadorDeConsola transformador = new AgregadorDeConsola.getInstance();
 * // aqui modificamos el codigo fuente para que utilice la ventana de consola
 * textoModificadoClaseRobot = transformador.transformar(textoJavaClaseRobot);
 * </pre>
 */
public class AgregadorDeConsola {
	
	private static final String CODIGO_CLASE_CONSOLA_ROBOT = "clase-consola-robot.txt";
	private static final String WHITE = "WHITE";
	private static final String BLACK = "BLACK";
	private static final String RUN = "run";
	private static final String PRINTLN = "System.out.println";
	private String codigoJavaClaseConsolaRobot = null;
	

	private static final class SINGLETON {
		static final AgregadorDeConsola INSTANCE = new AgregadorDeConsola();
	}
	
	public static AgregadorDeConsola getInstance() { return SINGLETON.INSTANCE; }
	
	private AgregadorDeConsola() { 
		InputStream is = null;
		try {
			is = this.getClass().getResourceAsStream("/"+CODIGO_CLASE_CONSOLA_ROBOT);
			leerTextoClaseConsolaRobotDesde(is);
		} catch(Exception ignorada) { }
		finally {
			if(is!=null) {
				try {
					is.close();
				} catch (IOException ignorada) { }
			}
		}
	}
	
	/**
	 * Leer el codigo fuente de la clase interna que implementa la ventana de la consola.<br>
	 * La clase <b>debe llamarse</b> <code>ConsolaRobot</code> <b>obligatoriamente</b>, y debe ser declarada como:
	 * <pre>
	 * static final class ConsolaRobot {
	 * ...
	 * }
	 * </pre>
	 * El codigo fuente de la consola <b>no debe</b> depender de ningun otro jar mas alla de los que ya depende Robocode, ni debe necesitar de <code>import</code>s extra.
	 * @param is Un stream desde el que leer el codigo fuente .java de la consola. <b>IMPORTANTE:</b> Este metodo nunca cierra este stream, debe cerrarse desde el metodo donde fue creado.
	 * @param encoding Encoding de unicode del codigo fuente.
	 * @throws IOException Si no pudo leer el codigo de la consola con exito.
	 * @throws UnsupportedEncodingException Si el encoding no es valido o no esta soportado.
	 */
	private void leerTextoClaseConsolaRobotDesde(InputStream is, String encoding) throws IOException, UnsupportedEncodingException {
		  char[] buf = new char[4096];
		  Reader r = new InputStreamReader(is, encoding); //
		  StringBuilder s = new StringBuilder();
		  while (true) {
		    int n = r.read(buf);
		    if (n < 0) {
		      break;
		    }
		    s.append(buf, 0, n);
		  }
		  if(s.length()==0) {
			  throw new IOException("El codigo de la clase esta vacio");
		  }
		  codigoJavaClaseConsolaRobot = s.toString() + "\n";
	}
	
	/**
	 * Leer el codigo fuente de la clase interna que implementa la ventana de la consola.<br>
	 * La clase <b>debe llamarse</b> <code>ConsolaRobot</code> <b>obligatoriamente</b>, y debe ser declarada como:
	 * <pre>
	 * static final class ConsolaRobot {
	 * ...
	 * }
	 * </pre>
	 * El codigo fuente de la consola <b>debe</b> estar en UTF-8 y <b>no debe</b> depender de ningun otro jar mas alla de los que ya depende Robocode, ni debe necesitar de <code>import</code>s extra.
	 * @param is Un stream desde el que leer el codigo fuente .java de la consola. <b>IMPORTANTE:</b> Este metodo nunca cierra este stream, debe cerrarse desde el metodo donde fue creado.
	 * @throws IOException Si no pudo leer el codigo de la consola con exito.
	 */
	private void leerTextoClaseConsolaRobotDesde(InputStream is) throws IOException {
	  try {
		  this.leerTextoClaseConsolaRobotDesde(is,"UTF-8");
	  } catch (UnsupportedEncodingException ignorada) { /* porque UTF-8 existe seguro */ }
	}

	private static void findAndReplace(StringBuilder source, int start, String from, String to) {
		 StringBuffer sb = new StringBuffer(source.subSequence(0, start));
		 Pattern p = Pattern.compile(from);
		 Matcher m = p.matcher(source.substring(start));
		 while (m.find()) {
		     m.appendReplacement(sb, to);
		 }
		 m.appendTail(sb);
		 source.setLength(0);
		 source.append(sb.toString());
	}
	
	/**
	 * Este metodo inserta el codigo de la clase interna ConsolaRobot y modifica el codigo fuente recibido para que todos los <code>println()</code> salgan por la consola en vez de stdout.<br>
     * Si el robot no contiene <code>println()</code>, entonces el codigo fuente no es modificado.
	 * @param textoClaseRobot Codigo fuente Java valido de un Robot de Robocode.
	 * @return El codigo fuente transformado.
	 * @throws ParseException si ocurrio algun error al parsear el codigo recibido como parametro.
	 * @throws IllegalStateException si olvido llamar a <code>leerTextoClaseConsolaRobotDesde()</code> antes de llamar a este metodo, o si <code>leerTextoClaseConsolaRobotDesde()</code> fallo.
	 */
	public String transformar(String textoClaseRobot) throws ParseException,IllegalStateException {
		// agregar la consola solamente si el codigo del robot incluye llamadas a System.out.println()
		if(textoClaseRobot.indexOf(PRINTLN)!=-1) {
			// chequeo de seguridad
			if(codigoJavaClaseConsolaRobot==null) {
				throw new IllegalStateException("El archivo "+CODIGO_CLASE_CONSOLA_ROBOT+" conteniendo el codigo de la clase que implementa la consola debe encontrarse en el classpath, y debe contener una clase interna llamada ConsolaRobot.");
			}
			// buscar la llave que indica el comienzo de la clase que define al robot
			int idxLlaveDeComienzoClaseRobot = buscarComienzoClase(textoClaseRobot);
			if(idxLlaveDeComienzoClaseRobot!=-1) {
				// insertar el codigo de la clase interna "ConsolaRobot" luego de la llave de comienzo de la clase
				StringBuilder textoClaseRobotConConsola = new StringBuilder(textoClaseRobot);
				textoClaseRobotConConsola.insert(idxLlaveDeComienzoClaseRobot+1,'\n');
				textoClaseRobotConConsola.insert(idxLlaveDeComienzoClaseRobot+2, codigoJavaClaseConsolaRobot);				
				textoClaseRobotConConsola.insert(idxLlaveDeComienzoClaseRobot+2+codigoJavaClaseConsolaRobot.length(),'\n');

				// buscar la llave que indica el comienzo del metodo run() dentro de la clase que define al robot
				int idxLlaveDeComienzoRun = buscarComienzoMetodoRun(textoClaseRobotConConsola);
				if(idxLlaveDeComienzoRun!=-1) {
					int comienzoBodyRun = idxLlaveDeComienzoRun+1;
					// buscar comienzo de la proxima linea despues de "run() {"
					while(comienzoBodyRun<textoClaseRobotConConsola.length() && Character.isWhitespace(textoClaseRobotConConsola.charAt(comienzoBodyRun))) { ++comienzoBodyRun; } 
					if(comienzoBodyRun<textoClaseRobotConConsola.length()) {
						String fgColor, bgColor;
						// buscar el color del cuerpo del robot; el texto de la consola tendra el mismo color
						int idxColorConsola = textoClaseRobotConConsola.indexOf("setColors(");
						if(idxColorConsola!=-1) {
							// ver si conseguimos el setColors correcto, por si hubiera otra clase con el mismo metodo
							if(textoClaseRobotConConsola.charAt(idxColorConsola-1)=='.') {
								if(!"this.".equals(textoClaseRobotConConsola.substring(idxColorConsola-5,idxColorConsola-2))) {
									idxColorConsola=-1;
								}
							}
						}							
						if(idxColorConsola!=-1) {
							fgColor = textoClaseRobotConConsola.substring(idxColorConsola+10,textoClaseRobotConConsola.indexOf(",",idxColorConsola+10));
							if("null".equals(fgColor)) { // el color del cuerpo del robot puede ser null
								fgColor = BLACK;
							}
						} else {							
							fgColor = BLACK;
						}
						// seleccionar un color de fondo de la consola con alto contraste
						if(fgColor.equalsIgnoreCase(WHITE) || fgColor.equalsIgnoreCase("YELLOW") || fgColor.equalsIgnoreCase("LIGHT_GRAY") || fgColor.equalsIgnoreCase("PINK")) {
							bgColor = BLACK;
						} else {
							bgColor = WHITE;
						}
						
						// buscar la llave que marca el fin del metodo run()
						int idxLlaveDeFinRun = buscarFinMetodo(textoClaseRobotConConsola,idxLlaveDeComienzoRun);
						if(idxLlaveDeFinRun!=-1) {
							// insertar el cierre de la consola antes de terminar
//							textoClaseRobotConConsola.insert(idxLlaveDeFinRun, "\n}finally{_consola.close();}\n");
							// insertar la llamada que crea la ventana de la consola luego de la llave de comienzo del metodo "run"
							textoClaseRobotConConsola.insert(comienzoBodyRun, "\n  _consola = ConsolaRobot.abrirConsola(100,this.getClass().getSimpleName(),\""+fgColor+"\",\""+bgColor+"\");\n");
							// reemplazar todos los println() por escritura en la ventana de consola
							findAndReplace(textoClaseRobotConConsola,idxLlaveDeComienzoClaseRobot, PRINTLN, "_consola.println");
							return textoClaseRobotConConsola.toString();
						} else {
							throw new ParseException("No fue posible encontrar la llave que indica el final del metodo run(), que debe existir",idxLlaveDeComienzoRun);							
						}
					}
				} else {
					throw new ParseException("No fue posible encontrar la llave que indica el comienzo del metodo run(), que debe existir",0);
				}
			} else {
				throw new ParseException("No fue posible encontrar la llave que indica el comienzo de la clase",0);
			}
		}
		return textoClaseRobot;
	}

	/**
	 * Busca la llave que marca el comienzo de la definicion de la clase java en <tt>textoClaseRobot</tt>.
	 * @return el indice en <tt>textoClaseRobot</tt> de la llave que indica el comienzo de la clase.
	 */
	private static int buscarComienzoClase(String textoClaseRobot) {
		int idx_class = textoClaseRobot.indexOf(" class ");
		if(idx_class!=-1) {
			return textoClaseRobot.indexOf('{',idx_class+7);
		}
		return -1;
	}

	/**
	 * Busca la llave que marca el comienzo de la definicion del metodo <tt>run() { ... }</tt> en tt>textoClaseRobot</tt>.
	 * @return el indice en <tt>textoClaseRobot</tt> de la llave que indica el comienzo del metodo <tt>run()</tt>.
	 */
	private static int buscarComienzoMetodoRun(StringBuilder textoClaseRobot) {
		int idx_run = textoClaseRobot.indexOf(RUN);
		while(idx_run!=-1) {
			// si es la definicion de run, esta aseguido de un parentesis
			if((idx_run+3)<textoClaseRobot.length() && (Character.isWhitespace(textoClaseRobot.charAt(idx_run+3)) || textoClaseRobot.charAt(idx_run+3)=='(')) {
				--idx_run;
				while(idx_run>0 && Character.isWhitespace(textoClaseRobot.charAt(idx_run))) {
					--idx_run;
				}
				// si es la definicion de run, esta precedido por "void"
				if(idx_run-3>0 && "void".equals(textoClaseRobot.substring(idx_run-3,idx_run+1))) {
					idx_run+=8;
					while(idx_run<textoClaseRobot.length() && textoClaseRobot.charAt(idx_run)!='{') {
						++idx_run;
					}
					return idx_run;
				}
		
			}
			idx_run = textoClaseRobot.indexOf(RUN, idx_run+4);
		}
		return -1;
	}

	/** 
	 * Algoritmo que busca el final del metodo, contando llaves de apertura y cierre.
	 * Asume que hay 1 llave ya encontrada (la de comienzo del metodo).
	 * Se no cuenta llaves dentro de comentarios, strings y constantes de caracteres.
	 * @param textoClaseRobot Texto java de una clase que contiene al metodo al cual buscarle el final.
	 * @param idxLlaveDeComienzoMetodo Indice dentro de <tt>textoClaseRobot</tt> de la llave que indica el comienzo del metodo.
	 * @return El indice en <tt>textoClaseRobot</tt> de la llave de final del metodo, o -1 si no pudo encontrar la llave.
	 */
	private static int buscarFinMetodo(StringBuilder textoClaseRobot, int idxLlaveDeComienzoMetodo) {
		 // El algoritmo para encontrar el fin del bloque es asi:
		 // enclosingBlocks = 1;
		 // do {
		 // si /* => saltear hasta */
		 // si // => saltear hasta '\n'
		 // si " => saltear hasta " que no este precedido de \
		 // si ' => saltear hasta '
		 // si { => ++enclosingBlocks
		 // si } => --enclosingBlocks 
		 // } while enclosingBlocks!=0
		 // si enclosingBlocks > 0 => retorna pos actual else retorna -1
		
		// chequeo de seguridad: asumimos que textoClaseRobot[idxLlaveDeComienzoMetodo] apunta a la 1ra llave del metodo; si esto no es cierto, el resto no anda
		if(idxLlaveDeComienzoMetodo<0 || idxLlaveDeComienzoMetodo> textoClaseRobot.length() || textoClaseRobot.charAt(idxLlaveDeComienzoMetodo)!='{') {
			throw new IllegalArgumentException("textoClaseRobot.charAt(idxLlaveDeComienzoMetodo) debe ser '{'");
		}
		int idxActual = idxLlaveDeComienzoMetodo+1;
		char[] buf = {'\0' , '\0' };
		int enclosingBlocks = 1;
		buf[1] = textoClaseRobot.charAt(idxActual);
		do {
			switch(buf[1]) {
			case '*':
				if(buf[0]=='/') {
					// comentario que comienza con /*
					idxActual = textoClaseRobot.indexOf("*/",idxActual+1)+1;
					buf[1]=textoClaseRobot.charAt(idxActual);
				}
				break;

			case '/':
				if(buf[0]=='/') {
					/* comentario que comienza con // */
					idxActual = textoClaseRobot.indexOf("\n",idxActual+1);
					buf[1]=textoClaseRobot.charAt(idxActual);
				}
				break;

			case '"':
				// comienzo de string, buscar final del string
				do {
					idxActual = textoClaseRobot.indexOf("\"",idxActual+1);
				} while(idxActual!=-1 && idxActual<textoClaseRobot.length() && textoClaseRobot.charAt(idxActual-1)=='\\');
				if(idxActual!=-1) {
					buf[1]=textoClaseRobot.charAt(idxActual);
				} else {
					idxActual=textoClaseRobot.length();
				}
				break;

			case '\'':
				// comienzo de constante char, buscar final de la constante
				idxActual = textoClaseRobot.indexOf("\'",idxActual+1);
				if(idxActual!=-1) {
					buf[1]=textoClaseRobot.charAt(idxActual);
				} else {	
					idxActual=textoClaseRobot.length();
				}
				break;

			case '{':
				++enclosingBlocks;
				break;

			case '}':
				--enclosingBlocks;
				break;
			}
			++idxActual;
			if(idxActual<textoClaseRobot.length()) {
				buf[0]=buf[1];
				buf[1]= textoClaseRobot.charAt(idxActual);
			} else { 
				break;
			}
		} while (enclosingBlocks > 0);
		// si encontramos la llave de cierre entonces retornamos su indice, y sino -1
		return (idxActual<=textoClaseRobot.length() ? idxActual-1: -1);
	}

}
