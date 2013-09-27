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
 * Esta clase representa a la estructura de control iterativa: For, escrito
 * en Java
 * 
 * @author Vanessa Aybar Rosales
 * */
public class For extends ControlSentence {

	public For() {

	}

	protected For(List<String> args) {
		super(args);
	}

	public List<StringBuffer> render(RenderableBlock rBlock)
			throws IncompleteSentenceException {

		StringBuffer sb1 = new StringBuffer();
		StringBuffer sb2 = new StringBuffer();
		StringBuffer sb3 = new StringBuffer();

		List<StringBuffer> returnedRenderList = new ArrayList<StringBuffer>();
		List<StringBuffer> tempCondition = new ArrayList<StringBuffer>();
		List<StringBuffer> tempBody = new ArrayList<StringBuffer>();
		sb1.append(rBlock.getBlock().getProperty("vm-cmd-name"));
		
		List<BlockConnector> tempList = new ArrayList<BlockConnector>();
		tempList.add(rBlock.getBlock().getSocketAt(0));
		tempCondition = JavaCodeGenerator.generateCode(tempList);
		if (tempCondition.size() == 0) {
			throw new IncompleteSentenceException();
		}
		tempList = new ArrayList<BlockConnector>();
		tempList.add(rBlock.getBlock().getSocketAt(1));
		tempBody = JavaCodeGenerator.generateCode(tempList);

		if (tempBody.size() > 1) {
			sb2.append(" { ");
			sb2.append(JavaCodeGenerator.nl);

			sb3.append(" } ");
			sb3.append(JavaCodeGenerator.nl);

		} else {
			//como no hay body, se termina con un punto y coma el FOR
			sb2.append(";");}

		String label=rBlock.getBlock().getSocketAt(0).getLabel();
		returnedRenderList.add(sb1);
		returnedRenderList.add(new StringBuffer(" (int "+label+"=0; "+label+"<"));
		returnedRenderList.addAll(tempCondition);
		returnedRenderList.add(new StringBuffer(";"+label));
		returnedRenderList.add(new StringBuffer("++)"));
		returnedRenderList.add(sb2);
		returnedRenderList.addAll(tempBody);
		returnedRenderList.add(sb3);

		return returnedRenderList;
	}

}
