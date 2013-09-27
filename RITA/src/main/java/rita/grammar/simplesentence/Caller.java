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
 * Esta clase representa la invocación a uno de los métodos definidos por el usuario
 * 
 * @author Vanessa Aybar Rosales
 * */
public class Caller extends Command {

	@Override
	public List<StringBuffer> render(RenderableBlock rBlock)
			throws IncompleteSentenceException {
		List<StringBuffer> returnedRenderList = new ArrayList<StringBuffer>();
		StringBuffer sb1 = new StringBuffer(rBlock.getBlock().getBlockLabel());
		StringBuffer sb2 = new StringBuffer("(");
		returnedRenderList.add(sb1);
		returnedRenderList.add(sb2);
		for (BlockConnector bc: rBlock.getBlock().getSockets()){
			if(RenderableBlock.getRenderableBlock(bc.getBlockID())==null) throw new IncompleteSentenceException();
			returnedRenderList.addAll(JavaCodeGenerator.generateCode(RenderableBlock.getRenderableBlock(bc.getBlockID())));
			returnedRenderList.add(new StringBuffer(","));
		}
		if (returnedRenderList.size()>2) //has args
			returnedRenderList.remove(returnedRenderList.size()-1);		
		StringBuffer sb3 = new StringBuffer(")");
		returnedRenderList.add(sb3);
		if (rBlock.getBlock().hasBeforeConnector()){}
			returnedRenderList.add(new StringBuffer(""));
		return returnedRenderList;
	}
	
	public boolean isCaller(){
		return true;
	}

}
