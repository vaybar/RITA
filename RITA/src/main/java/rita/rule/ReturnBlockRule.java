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

import java.util.ArrayList;
import java.util.List;

import renderable.BlockUtilities;
import rita.settings.Language;
import rita.ui.component.MessageDialog;
import rita.ui.component.MessageDialog.MessageType;

import workspace.WorkspaceEvent;
import workspace.WorkspaceListener;
import codeblocks.Block;
import codeblocks.BlockConnector;
import codeblocks.BlockLink;
import codeblocks.BlockStub;
import codeblocks.LinkRule;
import codeblocks.BlockConnector.PositionType;

/**
 * <code>ReturnBlockRule</code> chequea si un bloque de tipo return puede ser
 * conectado a otro evaluando si el socket/plug corresponde y además de acuerdo
 * a qué otros tipos de datos ya devuelve el método al que se desea adjuntar.
 * 
 * @author Vanessa Aybar Rosales
 */
public class ReturnBlockRule implements LinkRule, WorkspaceListener {

	/**
	 * Retorna true si los dos sockets de los dos bloques pueden conectarse de
	 * acuerdo al tipo del socket; false en caso contrario. El primer socket
	 * debería ser un de tipo POLY en caso de corresponder a un bloque Return.
	 * El nuevo bloque serà aceptado si todas las sentencias de retorno son del
	 * mismo tipo. Sino, todos los bloques return existentes no deberían tener
	 * un tipo de valor de retorno asociado para que se devuelva true.
	 * 
	 * @param block1
	 *            El asociado <code>Block</code> de socket1
	 * @param block2
	 *            El asociado <code>Block</code> de socket2
	 * @param socket1
	 *            Un <code>Socket</code> o plug de block1
	 * @param socket2
	 *            Un <code>Socket</code> o plug de block2
	 * @return true si los sockets de los dos bloques pueden conectarse; false
	 *         en caso contrario
	 */
	public boolean canLink(Block block1, Block block2, BlockConnector socket1,
			BlockConnector socket2) {
		// Vanessa
		// Al unir un bloque con otro.
		// Caso 1: El segundo bloque tiene como raiz un Custom Method. Hay que
		// verificar que no haya incompatibilidad
		// si existe return
		// Caso 2: El segundo bloque es un Metodo propio de robocode, por lo
		// pronto no se permiten return
		List<String> resultReturn = new ArrayList<String>();
		Long methodId = 0L;
		// CASO 1
		Block start = Block.getStarterMethod(block2);
//		if (block2.isProcedureDeclBlock())
//			return false;
		if (start != null && start.isMethodDeclBlock()
				&& includesReturn(block1)) {
			new MessageDialog(Language.get("returnRobocodeError"),
					MessageType.ERROR);
			BlockLink.boingSound.play();
			return false;
		}
		if (start != null && start.isMethodDeclBlock()) {
			return false;
		}
		if (block2.isProcedureDeclBlock() && block1.isReturnBlock()) {
			methodId = block2.getBlockID();
			if (methodId > 0)
				BlockStub.parentConnectorsChanged(methodId);
			return true;
		}
		if (start == null) {
			if (block1.isReturnBlock())
				new MessageDialog(Language.get("returnWithoutStartError"),
						MessageType.ERROR);
			return false;
		} else {
			BlockUtilities.getReturnTypeList(start, resultReturn);
			BlockUtilities.getReturnTypeList(block1, resultReturn);
			// verifico que el plug tenga un tipo de retorno acorde con los ya
			// ingresados
			if (block1.getPlugKind() != null
					&& (block1.getPlugKind().equals("boolean")
							|| block1.getPlugKind().equals("number") || block1
							.getPlugKind().equals("string"))
					&& (socket1.getPositionType().equals(PositionType.SINGLE) || socket1
							.getPositionType().equals(PositionType.MIRROR))
					&& block2.isReturnBlock())
				resultReturn.add(block1.getPlugKind());

			boolean allEqual = allEqual(resultReturn);
			if (allEqual) {

				methodId = start.getBlockID();
				if (block2.isReturnBlock()) {
					block2.getSocketAt(0).setKind(block1.getPlugKind());
					BlockStub.parentPlugChanged(methodId, block1.getPlugKind());
					return true;
				}
				if (methodId > 0 && resultReturn.size() > 0) {
					BlockStub.parentPlugChanged(methodId, resultReturn.get(0));
					//BlockStub.parentConnectorsChanged(methodId);
					return true;
				}
				if (block1.isReturnBlock() && resultReturn.size() <= 1) {
					return true;
				}
			} else {
				if (!allEqual && resultReturn.size() > 0) {
					new MessageDialog(Language.get("returnTypeError"),
							MessageType.ERROR);
					BlockLink.boingSound.play();
					return false;
				}

			}
		}
		return false;
	}

	public boolean isMandatory() {
		return false;
	}


	/**
	 * Verifica si todos los tipos de la lista son iguales. La idea es verificar que un método tiene todos 
	 * los bloques de tipo return con el mismo tipo. A menos que aún el usuario no haya decidido que es lo
	 * que devuelve el método, siempre el return tiene un tipo asociado distinto de "poly".
	 * */
	private static boolean allEqual(List<String> resultReturn) {
		String type = null;
		for (String s : resultReturn) {
			if (type == null)
				type = s;
			if (!type.equals(s))
				return false;
		}
		return true;
	}


	/**
	 * Este método devuelve true en caso que a partir de un determinado bloque 
	 * y realizando una búsqueda recursiva se encuentre algún bloque de tipo "return".
	 * Retorna falso en caso contrario.
	 * */
	private boolean includesReturn(Block start) {
		boolean result = false;
		if (start != null) {
			if (start.isReturnBlock()) {
				return true;
			}
			Block after = Block.getBlock(start.getAfterBlockID());
			if (includesReturn(after))
				return true;
			else {
				Iterable<BlockConnector> sockets = start.getSockets();

				for (BlockConnector bc : sockets) {
					if (includesReturn(Block.getBlock(bc.getBlockID())))
						return true;
				}
			}
		}
		return result;
	}

	
	/**
	 * Este método verifica que los bloques involucrados:
	 * -correspondan a un "return"
	 * -estén conectados a un "return"
	 * -pertenecìan a un mètodo en cuyo èl mètodo deberìa verificar 
	 * si cambió el tipo de retorno.
	 * */
	@Override
	public void workspaceEventOccurred(WorkspaceEvent e) {
		if (e.getEventType() == WorkspaceEvent.BLOCKS_DISCONNECTED) {
			Block disconnectedBlock = Block.getBlock(e.getSourceLink()
					.getPlugBlockID());
			Block parent = Block.getBlock(e.getSourceLink().getSocketBlockID());

			Block method = Block.getStarterMethod(parent);
			
			if (method!=null && method.isProcedureDeclBlock()) {
				if (parent != null) {
					if (parent.isReturnBlock()) { //si le saco el valor al return
						parent.getSocketAt(0).setKind("poly");
						BlockUtilities.verificarTipoReturn(disconnectedBlock, method, true);
					} else {
						// el parent no es un bloque return
						// chequear los return del principio del metodo
						// si hay alguno, dejar como está..
						// sino, retorna void!
						if (method != null && method.isProcedureDeclBlock()
								&& method.hasStubs()) {
							BlockUtilities.verificarTipoReturn(disconnectedBlock, method,false);

						}

					}

				}
			}
		}
	}

}
