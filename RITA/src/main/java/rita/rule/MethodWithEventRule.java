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

import renderable.BlockUtilities;
import rita.settings.Language;
import rita.ui.component.MessageDialog;
import rita.ui.component.MessageDialog.MessageType;
import codeblocks.Block;
import codeblocks.BlockConnector;
import codeblocks.BlockLink;
import codeblocks.BlockStub;
import codeblocks.LinkRule;

public class MethodWithEventRule implements LinkRule{
	

	@Override
	public boolean canLink(Block block1, Block block2, BlockConnector socket1,
			BlockConnector socket2) {
		// PERERATARRAGONA
		// Un Event siempre se puede unir a un MethodWithEvent
		if ((block1.isMethodWithEvent() && block2.isEvent()) || (block2.isMethodWithEvent() && block1.isEvent()))	{
			if (block1.getRestriction().equals( block2.getRestriction())){
				return true;
			}
		}

		if (block1.isEventMethod()) {
			Block parent = ((BlockStub)block1).getParent();
			Block method = Block.getBlock(parent.getPlug().getBlockID());
			if (BlockUtilities.containsBlock(method, block2)){
				return true;
			}else {
				new MessageDialog(Language.get("error.eventMethodDeclaration"), MessageType.ERROR);
				BlockLink.boingSound.play();
				return false;
			}
		}
		return false;
	}

	@Override
	public boolean isMandatory() {
		return false;
	}

}
