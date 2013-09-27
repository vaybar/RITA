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
 * Esta clase representa a los métodos de la API de Robocode que son sobreescritos por el
 * usuario de la aplicacion (onHitByBullet, onHilWall, etc)
 * 
 * @author Vanessa Aybar Rosales
 * */
public class Method extends Command {

//	Block myBlock;
	public Method(Block block) {
//		myBlock=block;
		this.setStringSentence(block.getBlockLabel());
	}

	@Override
	public List<StringBuffer> render(RenderableBlock rBlock) throws IncompleteSentenceException {
		
		StringBuffer sb1 = new StringBuffer();
		StringBuffer sb2 = new StringBuffer();
		List<StringBuffer> returnedRenderList=new ArrayList<StringBuffer>();
		List<BlockConnector> tempList = new ArrayList<BlockConnector>();
//		String stringArgs = "";
//		for (String s : this.getArgs()) {
//			stringArgs += s + ",";
//		}
//		if (stringArgs.length() > 0)
//			stringArgs = stringArgs.substring(0, stringArgs.length() - 1);

		sb1=new StringBuffer("public "+ rBlock.getBlock().getReturnType()+ " " + rBlock.getBlock().getProperty("vm-cmd-name") + "(){");
		
		sb2=new StringBuffer("}");
				
		returnedRenderList.add(sb1);
		for (BlockConnector bc: rBlock.getBlock().getSockets()){
			tempList.add(bc);
			
		}
		returnedRenderList.addAll(JavaCodeGenerator.generateCode(tempList));
		returnedRenderList.add(sb2);
		
		return returnedRenderList;

		
	}

}