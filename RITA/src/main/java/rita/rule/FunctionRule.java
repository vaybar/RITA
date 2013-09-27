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

import rita.util.RitaUtilities;
import rita.util.RulesUtilities;
import codeblocks.Block;
import codeblocks.BlockConnector;
import codeblocks.LinkRule;

public class FunctionRule implements LinkRule {

	@Override
	public boolean canLink(Block block1, Block block2, BlockConnector socket1,
			BlockConnector socket2) {

		if ((RitaUtilities.isMathFunction(block2) && RitaUtilities.isArithmeticOperand(block1))) {
			// Some functions need special arrangements
			socket2.setKind(socket1.getKind());

			if (RitaUtilities.isAbsFunction(block2)) {
				block2.setPlugKind(block1.getPlugKind());
				if (block2.getPlug().hasBlock())
					RulesUtilities.checkConnectedSocketConsistency(block2);
			}

			return true;
		}

		return false;
	}	

	@Override
	public boolean isMandatory() {
		return false;
	}

}
