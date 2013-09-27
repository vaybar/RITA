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

package rita.grammar.control;

import java.util.ArrayList;
import java.util.List;

import codeblocks.BlockConnector;

import renderable.RenderableBlock;
import rita.grammar.JavaCodeGenerator;
import rita.grammar.exception.IncompleteSentenceException;
/**
 * Esta clase representa a la estructura de control condicional: IF, escrito
 * en Java
 * 
 * @author Vanessa Aybar Rosales
 * */
public class If extends ControlSentence {

	public If() {

	}

	protected If(List<String> args) {
		super(args);
	}

	@Override
	public List<StringBuffer> render(RenderableBlock rBlock)
			throws IncompleteSentenceException {

		StringBuffer sb1 = new StringBuffer();
		StringBuffer sb2 = new StringBuffer();
		StringBuffer sb3 = new StringBuffer();
		StringBuffer sb4 = new StringBuffer();
		StringBuffer sb5 = new StringBuffer();

		List<StringBuffer> returnedRenderList = new ArrayList<StringBuffer>();
		List<StringBuffer> tempCondition = new ArrayList<StringBuffer>();
		List<StringBuffer> tempBody = new ArrayList<StringBuffer>();
		List<StringBuffer> tempBodyElse = new ArrayList<StringBuffer>();

		sb1.append(rBlock.getBlock().getProperty("vm-cmd-name"));

		List<BlockConnector> tempList = new ArrayList<BlockConnector>();
		tempList.add(rBlock.getBlock().getSocketAt(0));
		tempCondition = JavaCodeGenerator.generateCode(tempList);
		if (tempCondition.size() == 0) {
			throw new IncompleteSentenceException();
		}

		// bloque THEN
		tempList = new ArrayList<BlockConnector>();
		tempList.add(rBlock.getBlock().getSocketAt(1));
		tempBody = JavaCodeGenerator.generateCode(tempList);

		if (tempBody.size() > 1) {
			sb2.append(" { ");
			sb2.append(JavaCodeGenerator.nl);
			sb3.append(" } ");
			sb3.append(JavaCodeGenerator.nl);

			try {
				tempList = new ArrayList<BlockConnector>();
				tempList.add(rBlock.getBlock().getSocketAt(2));
				tempBodyElse = JavaCodeGenerator.generateCode(tempList);

				if (tempBodyElse.size() > 1) {
					sb4.append(" else { ");
					sb4.append(JavaCodeGenerator.nl);

					sb5.append(" } ");
					sb5.append(JavaCodeGenerator.nl);

				}
			} catch (IndexOutOfBoundsException e) {
				System.out.println("WARNING...no existe el bloque ELSE!");
			}

			returnedRenderList.add(sb1);
			returnedRenderList.add(new StringBuffer("("));
			returnedRenderList.addAll(tempCondition);
			returnedRenderList.add(new StringBuffer(")"));

			returnedRenderList.add(sb2);
			returnedRenderList.addAll(tempBody);
			returnedRenderList.add(sb3);
			if (tempBodyElse != null && tempBodyElse.size() > 0) {
				returnedRenderList.add(sb4);
				returnedRenderList.addAll(tempBodyElse);
				returnedRenderList.add(sb5);
			} 
		}else {
			returnedRenderList.add(sb1);
			returnedRenderList.add(new StringBuffer("("));
			returnedRenderList.addAll(tempCondition);
			returnedRenderList.add(new StringBuffer(")"));
			returnedRenderList.add(new StringBuffer(";"));	
		}


		return returnedRenderList;
	}
}
