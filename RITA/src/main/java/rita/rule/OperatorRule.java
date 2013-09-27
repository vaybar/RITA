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
import rita.util.RulesUtilities;

/**
 * <code>OperatorRule</code> checks if the received middle-operator can
 * be used with the received parameter type, and if it can then it
 * updates the plug kind of the operator using the followings rules:
 *
 *  - If it's a sum and one of the parameters is a string, then the
 *    operator returns a string
 *  - If the operands are numbers and at least one of them is a double,
 *    then the operator returns a double
 *  - Otherwise, the operator returns a number
 *
 * @author: Benencia
 */
public class OperatorRule implements LinkRule {

	@Override
	public boolean canLink(Block block1, Block block2, BlockConnector socket1,
			BlockConnector socket2) {

		if ((RitaUtilities.isArithmeticOperator(block2) && RitaUtilities.isArithmeticOperand(block1))
			|| (RitaUtilities.isSumOperator(block2) && RitaUtilities.isStringBlock(block1))) {
			updatePlug(block1, block2, socket1, socket2);
			RulesUtilities.checkConnectedSocketConsistency(block2);			
			return true;
		}

		return false;
	}

	/*
	 * This method encapsulates the needed logic for updating
	 * the middle operator plug.
	 */
	public static void updatePlug(Block block1, Block block2, BlockConnector socket1,
			BlockConnector socket2) {

		if (!block2.getSocketAt(0).hasBlock() && !block2.getSocketAt(1).hasBlock())
			// If there aren't any plugged blocks, then the return type will be
			// the one of the soon-to-be plugged block
			block2.setPlugKind(block1.getPlugKind());
		else {
			String s0 = null, s1 = null;

			if (socket2 == block2.getSocketAt(0))
				s0 = block1.getPlugKind();
			else
				s1 = block1.getPlugKind();

			// Only update the value when needed
			if (s0 == null && block2.getSocketAt(0).hasBlock())
				s0 = Block.getBlock(block2.getSocketAt(0).getBlockID()).getPlugKind();

			if (s1 == null && block2.getSocketAt(1).hasBlock())
				s1 = Block.getBlock(block2.getSocketAt(1).getBlockID()).getPlugKind();

			// If at least one of the parameters is a string, then the result type must also be string
			if (s0 != null && s1 != null)
				if (s0.equals("string") || s1.equals("string"))
					block2.setPlugKind("string");
				else
					// If at least one of the numbers is double, then the result type must also be double
					if (s0.equals("double") || s1.equals("double"))
						block2.setPlugKind("double");
					else
						block2.setPlugKind("number");
			else
				// Only one of the strings is null
				if (s0 != null)
					block2.setPlugKind(s0);
				else
					block2.setPlugKind(s1);
		}
	}
	
	@Override
	public boolean isMandatory() {
		return false;
	}
}
