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

import renderable.RenderableBlock;
import rita.grammar.control.For;
import rita.grammar.control.If;
import rita.grammar.control.While;


/**
 * Esta clase contiene la forma básica y simple de cada estructura de control java que se admite 
 * en el sistema
 * 
 * @author Vanessa Aybar Rosales
 * */
public class SentenceMap {

	private static Map<String,Command> templateSentence=new HashMap<String,Command>();
	
	static{
		templateSentence.put(SentenceConstants.IF, new If());
		templateSentence.put(SentenceConstants.WHILE, new While());
		templateSentence.put(SentenceConstants.FOR, new For());
	}
	
	private SentenceMap(){
		
	}
	
	/* 
	 * Dada una clave (definidas en SentenceConstants) devuelve el template básico que
	 * le corresponde
	 * */
	public static Command getTemplateSentence(int key){
		return templateSentence.get(key);
	}
	
	/* 
	 * Dada una clave como String, devuelve el template básico que
	 * le corresponde. Modifica el String original que recibe para asegurar 
	 * que este todo en mayuscula.
	 * */
	public static Command getTemplateSentence(String key){
		String newKey=key.toUpperCase();
		return templateSentence.get(newKey);
	}
	
	/* 
	 * Dada una clave como String, devuelve el template básico que
	 * le corresponde. Modifica el String original que recibe para asegurar 
	 * que este todo en mayuscula.
	 * */
	public static Command getTemplateSentence(RenderableBlock rBlock){
		String newKey=rBlock.getBlock().getBlockLabel().toUpperCase();
		return templateSentence.get(newKey);
	}
	
	
	
}
