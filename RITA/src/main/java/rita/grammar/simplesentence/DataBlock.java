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

package rita.grammar.simplesentence;

import java.util.ArrayList;
import java.util.List;

import renderable.RenderableBlock;
import rita.grammar.Command;
import rita.grammar.exception.IncompleteSentenceException;

/**
 * Esta clase representa a un bloque de datos simple
 *  
 * @author Vanessa Aybar Rosales
 * */
public class DataBlock extends Command{

	@Override
	public List<StringBuffer> render(RenderableBlock rBlock)
			throws IncompleteSentenceException {
		List<StringBuffer> returnedRenderList = new ArrayList<StringBuffer>();
		StringBuffer sb0 = null;
		if (rBlock.getBlock().getProperty("vm-value-type")!=null && rBlock.getBlock().getProperty("vm-value-type").equals("String"))
			sb0 = new StringBuffer("\""+rBlock.getBlock().getBlockLabel()+"\"");
		else
			sb0 = new StringBuffer(rBlock.getBlock().getBlockLabel());
		
		
		String valueType = rBlock.getBlock().getProperty("vm-value-type");
		if (valueType!=null){ //PERERATARRAGONA
			if (valueType.contains("Color.")){
				sb0 = new StringBuffer(valueType);
			} else {
				if (valueType.equals("String")){
					sb0 = new StringBuffer("\""+rBlock.getBlock().getBlockLabel()+"\"");
				} else {
					sb0 = new StringBuffer(rBlock.getBlock().getBlockLabel());
				}
			}
		} else {
			sb0 = new StringBuffer(rBlock.getBlock().getBlockLabel());
		}
		
		returnedRenderList.add(sb0);
		return returnedRenderList;
	}

}
