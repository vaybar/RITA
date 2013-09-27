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
import codeblocks.BlockConnector;

/**
 * Esta clase representa a un operador de modo infijo
 * 
 * @author Vanessa Aybar Rosales
 * */
public class InfixOperator extends Operator {

	public InfixOperator() {
	}

	@Override
	public List<StringBuffer> render(RenderableBlock rBlock)
			throws IncompleteSentenceException {

		StringBuffer sbCommand = new StringBuffer();

		List<StringBuffer> returnedRenderList = new ArrayList<StringBuffer>();
		List<StringBuffer> tempCondition1 = new ArrayList<StringBuffer>();
		List<StringBuffer> tempCondition2 = new ArrayList<StringBuffer>();

		sbCommand.append(rBlock.getBlock().getProperty("vm-cmd-name"));

		try {

			tempCondition1 = JavaCodeGenerator.generateCode(RenderableBlock.getRenderableBlock(rBlock.getBlock().getSocketAt(0).getBlockID()));
			tempCondition2 = JavaCodeGenerator.generateCode(RenderableBlock.getRenderableBlock(rBlock.getBlock().getSocketAt(1).getBlockID()));
			
			//las dos partes (izquierda y derecha) del operador deben tener algun valor para que el operador se renderice
			if (tempCondition1.size()==0 || tempCondition2.size()==0) 
				throw new IncompleteSentenceException();
			
			if (tempCondition1.size() > 1) {
				returnedRenderList.add(new StringBuffer(" ( "));
				returnedRenderList.addAll(tempCondition1);
				returnedRenderList.add(new StringBuffer(" ) "));
			} else{
				returnedRenderList.add(new StringBuffer(" "));
				returnedRenderList.addAll(tempCondition1);
				returnedRenderList.add(new StringBuffer(" "));
			}

			returnedRenderList.add(sbCommand);

			if (tempCondition2.size() > 1) {
				returnedRenderList.add(new StringBuffer(" ( "));
				returnedRenderList.addAll(tempCondition2);
				returnedRenderList.add(new StringBuffer(" ) "));
			} else{
				returnedRenderList.add(new StringBuffer(" "));
				returnedRenderList.addAll(tempCondition2);
				returnedRenderList.add(new StringBuffer(" "));
			}

		} catch (Exception e) {
			throw new IncompleteSentenceException();
		}

		return returnedRenderList;
	}
}
