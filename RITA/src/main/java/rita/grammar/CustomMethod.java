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

package rita.grammar;

import java.util.ArrayList;
import java.util.List;

import renderable.RenderableBlock;
import rita.grammar.exception.IncompleteSentenceException;
import codeblocks.Block;
import codeblocks.BlockConnector;

/**
 * Esta clase representa una llamada a los métodos de la API robocode.
 * 
 * @author Vanessa Aybar Rosales
 * */
public class CustomMethod extends Command {

//	Block myBlock;
	public CustomMethod(Block block) {
//		myBlock=block;
		this.setStringSentence(block.getBlockLabel());
	}

	/*
	 * Currently the String args ar not parameter, are just the command lines inside the block
	 * */
	
	@Override
	public List<StringBuffer> render(RenderableBlock rBlock) throws IncompleteSentenceException {
		
		
//		StringBuffer sb1 = new StringBuffer();
		
		StringBuffer sb2 = new StringBuffer();
		List<StringBuffer> returnedRenderList=new ArrayList<StringBuffer>();
//		String stringArgs = "";
//		for (String s : this.getArgs()) {
//			stringArgs += s + ",";
//		}
//		if (stringArgs.length() > 0)
//			stringArgs = stringArgs.substring(0, stringArgs.length() - 1);

		String result="";
		StringBuffer sb1=new StringBuffer("public "+ this.getReturnType(rBlock)+ " "+rBlock.getBlock().getBlockLabel()+ " (");
//		rBlock.setStartLineOfCode(sb1);
//		rBlock.setEndLineOfCode(sb2);
		
		
		//block connectors are the args in case of Custom Method
//		List<StringBuffer> fromParser=new ArrayList<StringBuffer>();
		for (BlockConnector bc: rBlock.getBlock().getSockets()){
			if (bc.hasBlock()){
				result += TypeConverter.get(bc.getKind()) + " ";
				result += Block.getBlock(bc.getBlockID()).getBlockLabel();
//				fromParser.addAll(JavaCodeGenerator.generateCode(RenderableBlock.getRenderableBlock(bc.getBlockID())));
//				fromParser.add(new StringBuffer(","));
				result +=",";
			}
		}
//		if (fromParser.size()>0){
//			fromParser.remove(fromParser.size()-1);
//		}
		if (result.endsWith(",")){
			result=result.substring(0, result.length()-1);
		}
		sb2=new StringBuffer(")");
		

		List<StringBuffer> listBody=new ArrayList<StringBuffer>(); 
		if (rBlock.getBlock().hasAfterConnector()){
			listBody.add(new StringBuffer("{"));
			RenderableBlock tempRBlock=RenderableBlock.getRenderableBlock(rBlock.getBlock().getAfterBlockID());
			if  (tempRBlock!=null){
				listBody.addAll(JavaCodeGenerator.generateCode(tempRBlock));
			}
			listBody.add(new StringBuffer("}"));
		}
//		Block.getBlock(bc.getBlockID());
//		tempList.add(bc);

		returnedRenderList.add(sb1);
		returnedRenderList.add(new StringBuffer(result));
		returnedRenderList.add(sb2);
		if (listBody.size()>0){
			returnedRenderList.addAll(listBody);
		} 
		
		return returnedRenderList;
	}

	private String getReturnType(RenderableBlock rBlock) {
		//search the first line of code that is a RETURN BLOCK
		String s=getReturnTypeRecursive(rBlock);
		if (s.equals(""))
			return TypeConverter.get("null");
		else return s;
	}

	/*retorna "" si no encuentra un tipo valido de return. caso contrario, devuelve el tipo de retorno correspondiente.*/
	private String getReturnTypeRecursive(RenderableBlock rBlock) {
		String sTemp="";
		if (rBlock.getBlock()!=null && rBlock.getBlock().getAfterBlockID()!=-1){
			RenderableBlock tempRBlock=RenderableBlock.getRenderableBlock(rBlock.getBlock().getAfterBlockID());
			while (tempRBlock!=null && sTemp.equals("")){
				if (tempRBlock.getBlock().isReturnBlock() && tempRBlock.getBlock().getSocketAt(0)!=null){
					
					sTemp=TypeConverter.get(tempRBlock.getBlock().getSocketAt(0).getKind());
					return sTemp;
				}
				for (BlockConnector bc:tempRBlock.getBlock().getSockets()){
					if (bc.getBlockID()!=Block.NULL){ 
						sTemp=getReturnTypeRecursive(RenderableBlock.getRenderableBlock(bc.getBlockID()));
						if (!sTemp.equals(""))
							return sTemp;
					}
				}
				tempRBlock=RenderableBlock.getRenderableBlock(tempRBlock.getBlock().getAfterBlockID());
			}
		}
		return sTemp;
	}

}


