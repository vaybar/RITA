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

package rita.grammar;

import java.util.HashMap;
import java.util.Map;

/**
 * Esta clase realiza la conversion de los tipos indicados en el archivo de
 * configuración a tipos de datos Java
 * 
 * @author Vanessa Aybar Rosales
 * */
public class TypeConverter {

	private static Map<String, String> types = new HashMap<String, String>();
	static {
		types.put("null", "void");
		// poly : caso return polimorfico y sin valor asociado
		types.put("poly", "void");
		types.put("string", "String");
		types.put("number", "int");
		types.put("double", "double");
		types.put("boolean", "Boolean");
	}

	private TypeConverter() {
	}

	public static String get(String key) {
		return types.get(key);
	}

}
