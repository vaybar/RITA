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

import java.util.HashMap;
import java.util.Map;
/**
 * Esta clase se usa para mantener un registro del código que se va generando,
 * de modo que pueda buscarse un texto en particular, por ejemplo, cuando se selecciona
 * un bloque y se pretende resaltar a que corresponde en el código fuente.
 * Por lo pronto no se está usando.
 * 
 * @author Vanessa Aybar Rosales
 * */
public class LabelMap {

	private static Map<String, Integer> labelCount=new HashMap<String, Integer>();
	
	private LabelMap(){
		
	}
	
	public static int getNextNumber(String key){
		Integer result=labelCount.get(key);
		if (result==null){
			labelCount.put(key, 1);
			return labelCount.get(key);
		}
		labelCount.put(key, result++);
		return result;
	}
	
}
