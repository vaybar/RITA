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

import codeblocks.Block;
import codeblocks.BlockConnector;

import renderable.RenderableBlock;
import rita.grammar.Command;
import rita.grammar.JavaCodeGenerator;
import rita.grammar.exception.IncompleteSentenceException;

/**
 * Esta clase representa al método setter de una variable
 * 
 * @author Vanessa Aybar Rosales
 * */
public class Setter extends Command {

	@Override
	public List<StringBuffer> render(RenderableBlock rBlock)
			throws IncompleteSentenceException {
		List<StringBuffer> returnedRenderList = new ArrayList<StringBuffer>();
		String[] str = rBlock.getBlock().getBlockLabel().split("[ ]");
		StringBuffer sb1 = new StringBuffer(str[1]);
		StringBuffer sb2 = new StringBuffer(" = ");
		returnedRenderList.add(sb1);
		returnedRenderList.add(sb2);
		BlockConnector bc = rBlock.getBlock().getSocketAt(0);

		RenderableBlock rBlockValue = RenderableBlock.getRenderableBlock(bc
				.getBlockID());
		if (rBlockValue != null)
			returnedRenderList.addAll(JavaCodeGenerator
					.generateCode(rBlockValue));
		else
			throw new IncompleteSentenceException();
		if (returnedRenderList.size() > 0
				&& !returnedRenderList.get(returnedRenderList.size() - 1)
						.toString().equals(";")) {
			returnedRenderList.add(new StringBuffer(";"));
		}

		return returnedRenderList;
	}

}
