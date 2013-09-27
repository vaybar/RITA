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
import rita.grammar.JavaCodeGenerator;
import rita.grammar.TypeConverter;
import rita.grammar.exception.IncompleteSentenceException;
import codeblocks.BlockConnector;

/**
 * Esta clase representa a la declaracion de una variable
 * 
 * @author Vanessa Aybar Rosales
 * */
public class VariableDeclaration extends Command {

	@Override
	public List<StringBuffer> render(RenderableBlock rBlock)
			throws IncompleteSentenceException {

		List<StringBuffer> rightSide = null;
		try {
			rightSide = JavaCodeGenerator.generateCode(RenderableBlock
					.getRenderableBlock(rBlock.getBlock().getSocketAt(0)
							.getBlockID()));
		} catch (Exception e) {
			throw new IncompleteSentenceException();
		}

		List<StringBuffer> returnedRenderList = new ArrayList<StringBuffer>();

		StringBuffer sb0 = new StringBuffer("");
		if (rBlock.getBlock().getSocketAt(0) != null)
			sb0.append(TypeConverter.get(rBlock.getBlock().getSocketAt(0)
					.getKind()));
		StringBuffer sbBlank = new StringBuffer(" ");
		StringBuffer sb1 = new StringBuffer(rBlock.getBlock().getBlockLabel());
		StringBuffer sb2 = new StringBuffer("=");
		StringBuffer sb3 = new StringBuffer(";");

		returnedRenderList.add(sb0);
		returnedRenderList.add(sbBlank);
		returnedRenderList.add(sb1);
		returnedRenderList.add(sbBlank);
		returnedRenderList.add(sb2);
		returnedRenderList.add(sbBlank);
		if (rightSide != null && rightSide.size() > 0
				&& rightSide.get(rightSide.size() - 1).toString().equals(";"))
			rightSide.remove(rightSide.size() - 1);
		returnedRenderList.addAll(rightSide);
		returnedRenderList.add(sb3);

		return returnedRenderList;
	}

}
