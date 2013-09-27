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

import java.util.ArrayList;
import java.util.List;

import codeblocks.Block;
import codeblocks.BlockConnector;

import renderable.RenderableBlock;
import rita.grammar.exception.IncompleteSentenceException;
/**
 * Esta clase representa a un bloque que equivale a la sentencia "return" de java
 * 
 * @author Vanessa Aybar Rosales
 * */
public class ReturnBlock extends Command {

	@Override
	public List<StringBuffer> render(RenderableBlock rBlock)
			throws IncompleteSentenceException {
		StringBuffer sb1 = new StringBuffer("return ");
		StringBuffer sb2 = new StringBuffer(";");
		List<StringBuffer> returnedRenderList = new ArrayList<StringBuffer>();
		List<StringBuffer> fromParser = new ArrayList<StringBuffer>();
		BlockConnector bc = rBlock.getBlock().getSocketAt(0);
		if (bc.hasBlock()) {
			fromParser.addAll(JavaCodeGenerator.generateCode(RenderableBlock
					.getRenderableBlock(bc.getBlockID())));
		}
		
		returnedRenderList.add(sb1);
		returnedRenderList.addAll(fromParser);
		returnedRenderList.add(sb2);
		return returnedRenderList;
	}

}
