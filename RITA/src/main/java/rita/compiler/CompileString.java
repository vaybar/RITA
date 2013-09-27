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

package rita.compiler;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.eclipse.jdt.internal.compiler.tool.EclipseCompiler;


/**
 * La clase CompileString tiene la responsabilidad de compilar el código Java generado
 * por RITA y administrar los errores encontrados.
 *  
 * @author Vanessa Aybar Rosales
 * */
public class CompileString {

	/**
	 * Nombre de la clase a compilar*/
	private static String clazzName;
	
	/**
	 * La variable diagnostics contiene todos los errores encontrados durante el 
	 * proceso de compilación */
	public static List<Diagnostic<? extends JavaFileObject>> diagnostics = new ArrayList<Diagnostic<? extends JavaFileObject>>();

	/** 
	 * Esta variable se actualiza ante la busqueda de un diagnostic en particular
	 * por posicion */
	public static Diagnostic<? extends JavaFileObject> diagnosticSelected;

	private CompileString() {
	}

	/**
	 * El método compile crea el archivo compilado a partir de un codigo fuente
	 * y lo deja en un directorio determinado
	 * 
	 * @param sourceCode
	 *            el codigo fuente
	 * @param className
	 *            el nombre que debería llevar la clase
	 * @param packageName
	 *            nombre del paquete
	 * @param outputDirectory
	 *            directorio donde dejar el archivo compilado
	 * */
	public static final Collection<File> compile(File sourceCode, File outputDirectory) throws Exception {
		diagnostics = new ArrayList<Diagnostic<? extends JavaFileObject>>();
		JavaCompiler compiler = new EclipseCompiler();
		
		System.out.println(sourceCode);

		DiagnosticListener<JavaFileObject> listener = new DiagnosticListener<JavaFileObject>() {
			@Override
			public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
				if (diagnostic.getKind().equals(
						javax.tools.Diagnostic.Kind.ERROR)) {
					System.err.println("=======================ERROR==========================");
					System.err.println("Tipo: " + diagnostic.getKind());
					System.err.println("mensaje: " + diagnostic.getMessage(Locale.ENGLISH));
					System.err.println("linea: " + diagnostic.getLineNumber());
					System.err.println("columna: " + diagnostic.getColumnNumber());
					System.err.println("CODE: " + diagnostic.getCode());
					System.err.println("posicion: de " + diagnostic.getStartPosition() + " a " + diagnostic.getEndPosition());
					System.err.println(diagnostic.getSource());
					diagnostics.add(diagnostic);
				}
			}

		};

		StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
		/* guardar los .class y  directorios (packages) en un directorio separado; de esta manera
		 * si se genera mas de 1 clase (porque p.ej. hay inner classes en el codigo) podemos identificar
		 * a todas las clases resultantes de esta compilacion
		 */
		File tempOutput = createTempDirectory();
		fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Arrays.asList(tempOutput));
		Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjects(sourceCode);
		try {
			if(compiler.getTask(null, fileManager, listener, null, null, compilationUnits).call()) {
				// copiar .class y  directorios (packages) generados por el compilador en tempOutput al directorio final
				FileUtils.copyDirectory(tempOutput, outputDirectory);
				// a partir de los paths de los archivos .class generados en el dir temp, modificarlos para que sean relativos a outputDirectory
				Collection<File> compilationResults = new ArrayList<File>();
				final int tempPrefix = tempOutput.getAbsolutePath().length();
				for(File classFile: FileUtils.listFiles(tempOutput, new SuffixFileFilter(".class"), TrueFileFilter.INSTANCE)) {
					compilationResults.add(new File(outputDirectory,classFile.getAbsolutePath().substring(tempPrefix)));
				}
				// retornar los paths de todos los .class generados
				return compilationResults;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error al realizar el compilado");
		} finally {
			FileUtils.deleteDirectory(tempOutput);
			fileManager.close();
		}
        return null;
	}

	private static File createTempDirectory() throws IOException {
		final File temp = File.createTempFile("rita-", null);
		// ...borrarlo como archivo...
		if(!(temp.delete())) {
	        throw new IOException("Could not delete temp file: " + temp.getAbsolutePath());
		}
		// ... y crearlo como directorio
		if(!temp.mkdir()) {
	        throw new IOException("Could not create temp directory: " + temp.getAbsolutePath());
	    }
	    return temp;	
	}
	
	static Iterable<JavaSourceFromString> getJavaSourceFromString(String code) {
		final JavaSourceFromString jsfs;
		jsfs = new JavaSourceFromString(clazzName, code);
		return new Iterable<JavaSourceFromString>() {
			public Iterator<JavaSourceFromString> iterator() {
				return new Iterator<JavaSourceFromString>() {
					boolean isNext = true;

					public boolean hasNext() {
						return isNext;
					}

					public JavaSourceFromString next() {
						if (!isNext)
							throw new NoSuchElementException();
						isNext = false;
						return jsfs;
					}

					public void remove() {
						throw new UnsupportedOperationException();
					}
				};
			}
		};
	}

	public static boolean containsPosition(int pos) {
		for (Diagnostic<? extends JavaFileObject> d : diagnostics) {
			if (d.getLineNumber() == pos) {
				diagnosticSelected = d;
				return true;
			}
		}
		diagnosticSelected = null;
		return false;
	}

	public static String getDiagnosticSelectedText() {
		if (diagnosticSelected != null) {
			String[] msg = diagnosticSelected.getMessage(Locale.ENGLISH).split(
					":" + diagnosticSelected.getLineNumber() + ":");
			String msgResult;
			if (msg.length > 1) {
				msgResult = msg[1];
			} else {
				msgResult = diagnosticSelected.getMessage(Locale.ENGLISH);
			}
			return "Linea:" + diagnosticSelected.getLineNumber() + "-"
					+ "Col.:" + diagnosticSelected.getColumnNumber() + ":"
					+ msgResult;
		} else
			return "";
	}

	public static boolean hasError() {
		return diagnostics.size() > 0;
	}
}

class JavaSourceFromString extends SimpleJavaFileObject {
	final String code;

	JavaSourceFromString(String name, String code) {
		super(URI.create("string:///" + name.replace('.', '/')
				+ Kind.SOURCE.extension), Kind.SOURCE);
		this.code = code;
	}

	public CharSequence getCharContent(boolean ignoreEncodingErrors) {
		return code;
	}
}