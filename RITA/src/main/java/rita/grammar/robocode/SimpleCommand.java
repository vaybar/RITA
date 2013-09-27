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

package rita.grammar.robocode;

import java.util.ArrayList;
import java.util.List;

import renderable.RenderableBlock;
import rita.grammar.Command;
import rita.grammar.JavaCodeGenerator;
import rita.grammar.exception.IncompleteSentenceException;
import codeblocks.Block;
import codeblocks.BlockConnector;

/**
 * Esta clase representa a una invocación a un método disponible en la API de
 * Robocode
 * 
 * @author Vanessa Aybar Rosales
 * */
public class SimpleCommand extends Command {

	public SimpleCommand(Block block) {
		this.setStringSentence(block.getBlockLabel());
	}

	@Override
	public List<StringBuffer> render(RenderableBlock rBlock)
			throws IncompleteSentenceException {
		StringBuffer sb1 = new StringBuffer();
		StringBuffer sb2 = new StringBuffer();
		List<StringBuffer> returnedRenderList = new ArrayList<StringBuffer>();
		List<StringBuffer> tempListSB = new ArrayList<StringBuffer>();
		List<BlockConnector> tempList = null;
		sb1.append(rBlock.getBlock().getProperty("vm-cmd-name") + "(");
		sb2.append(");");
		returnedRenderList.add(sb1);
		int start = 0;
		for (BlockConnector bc : rBlock.getBlock().getSockets()) {
			tempList = new ArrayList<BlockConnector>();
			tempList.add(bc);
			if (bc.getBlockID() != -1) {
				tempListSB = JavaCodeGenerator.generateCode(RenderableBlock.getRenderableBlock(bc.getBlockID()));
			} else {
				throw new IncompleteSentenceException();
			}	
			if (start > 0) {
				returnedRenderList.add(new StringBuffer(","));
			}
			start++;
			returnedRenderList.addAll(tempListSB);
		}
		returnedRenderList.add(sb2);

		return returnedRenderList;
	}
}
