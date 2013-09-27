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
import workspace.WorkspaceEvent;
import workspace.WorkspaceListener;
import codeblocks.Block;
import codeblocks.BlockConnector;
import codeblocks.BlockConnector.PositionType;
import codeblocks.BlockLink;
import codeblocks.BlockStub;
import codeblocks.LinkRule;

/**
 * <code>ParameterRule</code> verifica si los bloques a conectar corresponden
 * segun el tipo donde ademàs se verifica que sea un bloque parámetro y no un
 * tipo de dato
 * 
 * @author Vanessa Aybar Rosales
 */
public class ParameterRule implements LinkRule, WorkspaceListener {

	/**
	 * Retorna true si los bloques pueden conectarse, no sólo por la forma, sino
	 * que un parámetro sólo debería conectarse en la definición de un método.
	 * Caso contrio devuelve false.
	 * 
	 * @param block1
	 *            El asociado <code>Block</code> de socket1
	 * @param block2
	 *            El asociado <code>Block</code> de socket2
	 * @param socket1
	 *            Un <code>Socket</code> o plug de block1
	 * @param socket2
	 *            Un <code>Socket</code> o plug de block2
	 * @return true si los dos sockets pueden conectarse; false en caso
	 *         contrario
	 */
	public boolean canLink(Block block1, Block block2, BlockConnector socket1,
			BlockConnector socket2) {

		if (block2.isCommandBlock() && block1.isProcedureParamBlock()) {
			new MessageDialog(Language.get("parameterRuleOutOfMethodError"),
					MessageType.ERROR);
			BlockLink.boingSound.play();
			return false;
		}
		if (block2.isProcedureDeclBlock()
				&& !block1.isProcedureParamBlock()
				&& (!(socket1.getPositionType().equals(PositionType.TOP) || socket1
						.getPositionType().equals(PositionType.BOTTOM)))) {
			new MessageDialog(Language.get("parameterRuleError"),
					MessageType.ERROR);
			BlockLink.boingSound.play();
			return false;
		} else {
			if (block2.isProcedureDeclBlock() && block1.isProcedureParamBlock()
					&& socket1.getPositionType().equals(PositionType.SINGLE)
					&& socket2.getPositionType().equals(PositionType.SINGLE)) {
				socket2.setKind(socket1.getKind());
				return true;
			}
		}

		return false;
	}

	public boolean isMandatory() {
		return false;
	}

	@Override
	public void workspaceEventOccurred(WorkspaceEvent event) {
		if (event.getEventType() == WorkspaceEvent.BLOCKS_DISCONNECTED) {
			Block disconnectedBlock = Block.getBlock(event.getSourceLink()
					.getPlugBlockID());
			if (disconnectedBlock.isProcedureParamBlock()) {
				disconnectAllParamStubs(disconnectedBlock, disconnectedBlock.getPlugKind());
				BlockStub.parentConnectorsChanged(event.getSourceLink().getSocketBlockID());

			}

		}
	}

	private void disconnectAllParamStubs(Block parent, String type) {
		BlockUtilities.disconnectStubs(parent, type);
	}

		
}
