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

package rita.util;

import renderable.RenderableBlock;
import rita.rule.OperatorRule;
import codeblocks.Block;
import codeblocks.BlockConnector;
import codeblocks.BlockLink;


/**
 * <code>RulesUtilities</code> contains statics methods used by linking
 * rules. 
 *
 * @author: Benencia
 */
public class RulesUtilities {
	
	/*
	 * Whenever a plug changes its kind, this method should be called to
	 * ensure that the blocks maintains theirs type consistency. 
	 */
	public static void checkConnectedSocketConsistency(Block block) {	
		if (block.getPlug()!=null && block.getPlug().hasBlock()) {
			BlockConnector socket = findSocketForPlug(Block.getBlock(block.getPlug().getBlockID()), block);

			if (socket!=null && !block.getPlugKind().equals(socket.getKind())) {
				Block newBlock = Block.getBlock(block.getPlug().getBlockID());
				if (socket.initKind().equals("poly")) {										
					// Be wary, recursive code coming					
					if (RitaUtilities.isArithmeticOperator(newBlock))
						OperatorRule.updatePlug(block, newBlock, block.getPlug(), findSocketForPlug(newBlock, block));
					else if (RitaUtilities.isMathFunction(newBlock)) {
						// In this case we only update the socket, not the plug
						socket.setKind(block.getPlugKind());
						// But if it is abs... then we must also update the plug
						if (RitaUtilities.isAbsFunction(newBlock))
							newBlock.setPlugKind(block.getPlugKind());
					}
					checkConnectedSocketConsistency(newBlock);
				}				
				else
					disconnectBlocks(block, Block.getBlock(block.getPlug().getBlockID()), block.getPlug(), socket);				
			}
		}
	}
	
	/*
	 * Given two blocks, this method search the socket that connects them
	 *  
	 */
	public static BlockConnector findSocketForPlug(Block socketBlock, Block plugBlock) {		
		for (BlockConnector bc:socketBlock.getSockets())
			if (bc.hasBlock() && (bc.getBlockID() == plugBlock.getBlockID()))
				return bc;	
		
		return null;
	}

	/*
	 * This method encapsulates the needed logic for
	 * disconnecting two blocks
	 */
	public static void disconnectBlocks(Block block1, Block block2,
			BlockConnector socket1, BlockConnector socket2) {
		final int offset = 20;
		BlockLink link = BlockLink.getBlockLink(block1, block2, socket1, socket2);
		link.disconnect();
		BlockLink.brokenSound.play();
		
		RenderableBlock.getRenderableBlock(block2.getBlockID()).blockDisconnected(socket2,block1);
		
		RenderableBlock render = RenderableBlock.getRenderableBlock(block1.getBlockID());
		render.setLocation((int)render.getLocation().getX()+offset, (int)render.getLocation().getY());
	}	
}
