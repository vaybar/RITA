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

package rita.rule;

import codeblocks.Block;
import codeblocks.BlockConnector;
import codeblocks.LinkRule;

import rita.util.RitaUtilities;

/**
 * <code>ComparatorRule</code> checks if the received comparator can
 * be used with to compare the received and current parameters.
 *
 * @author: Benencia
 */
public class ComparatorRule implements LinkRule {

	@Override
	public boolean canLink(Block block1, Block block2, BlockConnector socket1,
			BlockConnector socket2) {

		if ((RitaUtilities.isComparator(block2) && RitaUtilities.isArithmeticOperand(block1))) {
			// Only returns true when all the parameters are arithmetic. i.e. It can't return
			// true if one operand of the comparator is String and the other is double.
			BlockConnector theOtherSocket;

			if (block2.getSocketAt(0) == socket1)
				theOtherSocket = block2.getSocketAt(1);
			else
				theOtherSocket = block2.getSocketAt(0);

			if (theOtherSocket.hasBlock() &&
				!(RitaUtilities.isArithmeticOperand(Block.getBlock(theOtherSocket.getBlockID()))))
				return false;

			return true;
		}

		return false;
	}

	@Override
	public boolean isMandatory() {
		return false;
	}

}
