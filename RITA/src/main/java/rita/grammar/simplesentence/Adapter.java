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
import rita.util.RitaUtilities;

public class Adapter extends Command {

	@Override
	public List<StringBuffer> render(RenderableBlock rBlock)
			throws IncompleteSentenceException {

	    List<StringBuffer> returnedRenderList = new ArrayList<StringBuffer>();
	    List<BlockConnector> tempList = new ArrayList<BlockConnector>();
	    StringBuffer sb0 = new StringBuffer("(" + rBlock.getBlock().getProperty("vm-value-type") + ") ");

	    returnedRenderList.add(sb0);

	    for (BlockConnector bc: rBlock.getBlock().getSockets())
	    	tempList.add(bc);
	    
   	    if (rBlock.getBlock().getNumSockets() > 0 &&
   	    		rBlock.getBlock().getSocketAt(0).hasBlock()) 
   	    	if (!RitaUtilities.isSimpleAdaptable(Block.getBlock(rBlock.getBlock().getSocketAt(0).getBlockID()))) {  	    	
   	    	
   	    		returnedRenderList.add(new StringBuffer("("));
	    		returnedRenderList.addAll(JavaCodeGenerator.generateCode(tempList));
	    		returnedRenderList.add(new StringBuffer(")"));
   	    	}
   	    	else
   		    	returnedRenderList.addAll(JavaCodeGenerator.generateCode(tempList));
   	    else
   	    	throw new IncompleteSentenceException(); 	    
	    	    
	    
   	    return returnedRenderList;
	}
}