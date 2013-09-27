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
import renderable.RenderableBlock;
import rita.settings.Language;
import rita.ui.component.MessageDialog;
import rita.ui.component.MessageDialog.MessageType;
import rita.util.RitaUtilities;
import workspace.WorkspaceEvent;
import workspace.WorkspaceListener;
import codeblocks.Block;
import codeblocks.BlockConnector;
import codeblocks.BlockConnector.PositionType;
import codeblocks.BlockLink;
import codeblocks.BlockStub;
import codeblocks.LinkRule;

/**
 * <code>DeclarationRule</code> chequea si un socket y un plug que quieren ser
 * encastrados pueden conectarse, verificando sus tipos. El bloque de la
 * izquierda corresponde a la declaracion de una variable.
 * 
 * @author Vanessa Aybar Rosales
 */
public class DeclarationRule implements LinkRule, WorkspaceListener {

	/**
	 * Devuelve true si los dos bloques pueden conectarse, y false en caso
	 * contrario. En el caso de una declaracion, siempre acepta un tipo nuevo ya
	 * que el tipo original es polimorfico y se adapta al tipo que quiera
	 * definir el usuario. En caso de que el tipo de la variable cambie, los
	 * stubs asociados (getters y setters) se actualizan
	 * 
	 * @param block1
	 *            el asociado <code>bloque</code> de socket1
	 * @param block2
	 *            el asociado <code>bloque</code> de socket2
	 * @param socket1
	 *            un <code>Socket</code> o Plug de block1
	 * @param socket2
	 *            un <code>Socket</code> o Plug de block2
	 * @return true si los bloques pueden conectarse; false en caso contrario
	 */
	public boolean canLink(Block block1, Block block2, BlockConnector socket1,
			BlockConnector socket2) {

		// si quiero conectar una declaracion de variable, tengo que chequear
		// que los stubs esten dentro del alcance
		// si no lo estan, desconectarlos
		if (block1.isVariableDeclBlock()) {
			if (block1.hasStubs()) {
				updateScopeStubs(block1, block2);
			} else
				return true;
		}

		

		// si quiero enganchar un valor a una declaracion tengo que actualizar
		// los stubs de modo que pierdan los enganches
		if (block2.isVariableDeclBlock()
				&& !block1.getGenusName().contains("-param-") // block1.isDataBlock()
																// &&
				&& (RitaUtilities.isSimple(socket1) && (socket1
						.getPositionType().equals(PositionType.SINGLE) || socket1
						.getPositionType().equals(PositionType.MIRROR)))) {
			updateConnectorStubs(block2, block1);
			socket2.setKind(socket1.getKind());
			return true;
		}

		if (block2.isVariableDeclBlock() && block1.isProcedureParamBlock()) {
			new MessageDialog(Language.get("declaracionSimpleError"),
					MessageType.ERROR);
			BlockLink.boingSound.play();
			return false;
		}
		return false;
	}

	private void updateScopeStubs(Block block1, Block block2) {
		Iterable<Long> stubs = BlockStub.getStubsOfParent(block1.getBlockID());
		// desconecto a cada stub que encuentro
		boolean disconnect = false;
		for (Long bsID : stubs) {
			Block bs = Block.getBlock(bsID);
			boolean pass = true;
			// verifica si el bloque actual contiene a getter/setter de la
			// variable en cuestion
			// o si el lugar a donde muevo la declaracion no es un bloque global
			pass = !BlockUtilities.containsBlock(block1, bs)
					&& !BlockUtilities.isGlobalDecl(block2);

			if (pass) {
				// desconectar el Block Stub que corresponde a un GETTER
				// sirve cuando una declaracion es movida de lugar, por tanto el
				// alcance puede ser inaccesible para los getter/setter pueden

				// desconectar el bs que corresponde a un SETTER
				if (bs.getGenusName().startsWith(BlockStub.SETTER_STUB)
						&& (RenderableBlock.getRenderableBlock(bs.getBlockID()) != null && RenderableBlock
								.getRenderableBlock(bs.getBlockID())
								.isShowing())) {
					// remuevo el before connector

					Block beforeBlock = Block.getBlock(bs.getBeforeBlockID());
					if (beforeBlock != null
							&& (!beforeBlock.getBlockID().equals(Block.NULL))) {
						disconnect = true;
						BlockLink.getBlockLink(bs, beforeBlock,
								bs.getBeforeConnector(),
								beforeBlock.getAfterConnector()).disconnect();
					}

					// remuevo el after connector
					Block afterBlock = Block.getBlock(bs.getAfterBlockID());
					if (afterBlock != null
							&& afterBlock.getBlockID() != Block.NULL) {
						disconnect = true;
						BlockLink.getBlockLink(bs, afterBlock,
								bs.getAfterConnector(),
								afterBlock.getBeforeConnector()).disconnect();
					}

				}
				if (bs.getGenusName().startsWith(BlockStub.GETTER_STUB)
						&& (RenderableBlock.getRenderableBlock(bs.getBlockID()) != null && RenderableBlock
								.getRenderableBlock(bs.getBlockID())
								.isShowing())) {
					disconnect = disconnectGetterStub(bs) || disconnect;
				}

			}
		}
		if (disconnect)
			BlockLink.brokenSound.play();
	}

	// sirve cuando el tipo conectado a la declaracion cambió, en ese caso los
	// Block Stub deben actualizar el tipo.
	private void updateConnectorStubs(Block parent, Block blockValue) {

		Iterable<Long> stubs = BlockStub.getStubsOfParent(parent.getBlockID());
		boolean disconnect = false;
		for (Long bsID : stubs) {
			Block bs = Block.getBlock(bsID);

			if (bs.getGenusName().startsWith(BlockStub.GETTER_STUB)) {
				// remuevo el plug si existe
				if (bs.getSockets().iterator().hasNext()
						&& blockValue.getPlug() != null
						&& !bs.getSocketAt(0).getKind()
								.equals(blockValue.getPlug().getKind())) {
					disconnect = disconnect || disconnectGetterStub(bs);
					bs.getSocketAt(0).setKind(blockValue.getPlug().getKind());
				}
			}
			if (bs.getGenusName().startsWith(BlockStub.SETTER_STUB)) {
				if (bs.getSocketAt(0) != null && blockValue.getPlug() != null) {
					if ((!bs.getSocketAt(0).getBlockID().equals(Block.NULL))
							&& !bs.getSocketAt(0).getKind()
									.equals(blockValue.getPlug().getKind())) {

						Block connectedBlock = Block.getBlock(bs.getSocketAt(0)
								.getBlockID());
						disconnect = true;
						BlockLink.getBlockLink(bs, connectedBlock,
								bs.getSocketAt(0), connectedBlock.getPlug())
								.disconnect();

						bs.getSocketAt(0).setKind(
								blockValue.getPlug().getKind());
					}

				}
			}
		}
		if (disconnect)
			BlockLink.brokenSound.play();
	}

	private boolean disconnectGetterStub(Block bs) {
		boolean disconnect = false;
		if (bs.getPlugBlockID() != Block.NULL) {
			Block connectedBlock = Block.getBlock(bs.getPlugBlockID());
			BlockConnector socketResult = null;
			for (BlockConnector socket : connectedBlock.getSockets()) {
				if (socket.getBlockID().equals(bs.getBlockID())) {
					socketResult = socket;
					break;
				}
			}
			if (socketResult != null) {
				disconnect = true;
				BlockLink.getBlockLink(bs, connectedBlock, bs.getPlug(),
						socketResult).disconnect();
			}

		}
		return disconnect;
	}

	public boolean isMandatory() {
		return false;
	}

	@Override
	public void workspaceEventOccurred(WorkspaceEvent event) {
		if (event.getEventType() == WorkspaceEvent.BLOCKS_DISCONNECTED) {
			Block disconnectedBlock = Block.getBlock(event.getSourceLink()
					.getSocketBlockID());
			if (disconnectedBlock.isVariableDeclBlock()) {
				disconnectedBlock.getSocketAt(0).setKind("poly");
				disconnectAllStubs(disconnectedBlock, "poly");

			}

		}
		if (event.getEventType() == WorkspaceEvent.BLOCKS_CONNECTED) {
			//si estoy conectandole algún valor a la variable, ya sea simple o compuesto
			if (Block.getBlock(event.getSourceLink().getSocketBlockID())
					.isVariableDeclBlock()) {
				Block connectedBlock = Block.getBlock(event.getSourceLink()
						.getSocketBlockID());

				connectedBlock.getSocketAt(0).setKind(
						event.getSourceLink().getPlug().getKind());
				if (connectedBlock.isVariableDeclBlock()) {
					BlockStub.parentConnectorsChanged(connectedBlock
							.getBlockID());
				}
			}
		}
	}

	private void disconnectAllStubs(Block disconnectedBlock, String type) {
		BlockUtilities.disconnectStubs(disconnectedBlock, type);

	}

}
