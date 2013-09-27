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
import rita.grammar.JavaCodeGenerator;
import rita.grammar.exception.IncompleteSentenceException;

/**
 * Esta clase representa a un operador de modo prefijo
 * 
 * @author Vanessa Aybar Rosales 
 * */
public class PrefixOperator extends Operator {

	@Override
	public List<StringBuffer> render(RenderableBlock rBlock)
			throws IncompleteSentenceException {
		StringBuffer sbCommand = new StringBuffer();

		List<StringBuffer> returnedRenderList = new ArrayList<StringBuffer>();
		List<StringBuffer> tempCondition1 = new ArrayList<StringBuffer>();

		// Operator as String
		sbCommand.append(rBlock.getBlock().getProperty("vm-cmd-name"));

		try {

			tempCondition1 = JavaCodeGenerator.generateCode(RenderableBlock
					.getRenderableBlock(rBlock.getBlock().getSocketAt(0)
							.getBlockID()));

			returnedRenderList.add(sbCommand);
			if (tempCondition1.size() > 1) {
				returnedRenderList.add(new StringBuffer(" ( "));
				returnedRenderList.addAll(tempCondition1);
				returnedRenderList.add(new StringBuffer(" ) "));
			} else {
				returnedRenderList.add(new StringBuffer(" "));
				returnedRenderList.addAll(tempCondition1);
				returnedRenderList.add(new StringBuffer(" "));
			}

		} catch (Exception e) {
			throw new IncompleteSentenceException();
		}
		return returnedRenderList;

	}

}
