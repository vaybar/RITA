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

/**
 * <code>DeclarationUseRule</code> chequea si el getter o setter de una variable
 * puede conectarse con otro bloque en funcion de si el socket/plug coinciden y
 * si el alcance es el correcto.
 * 
 * @author Vanessa Aybar Rosales
 * */
public class DeclarationUseRule implements LinkRule {

	/**
	 * Retorna true si el uso de la variable via getter o setter tiene el
	 * alcance correcto, de otro modo devuelve false.
	 * 
	 * @param block1
	 *            el asociado <code>bloque</code> de socket1
	 * @param block2
	 *            el asociado <code>bloque</code> de socket2
	 * @param socket1
	 *            un <code>Socket</code> o plug de block1
	 * @param socket2
	 *            un <code>Socket</code> o plug de block2
	 * @return true si los bloques pueden conectarse; false en caso contrario
	 */
	public boolean canLink(Block block1, Block block2, BlockConnector socket1,
			BlockConnector socket2) {

		if (block1.getGenusName().startsWith(BlockStub.GETTER_STUB)
				|| block1.getGenusName().startsWith(BlockStub.SETTER_STUB)) { // PERERATARRAGONA
			Block parent = ((BlockStub) block1).getParent();
			// el block original es accesible de manera global
			if (!parent.isProcedureParamBlock()
					&& BlockUtilities.isGlobalDecl(parent)) {
//				if (block2 != null && block2.getGenusName().equals("print")) {
//					block2.getSocketAt(0).setKind(
//							parent.getSocketAt(0).getKind());
//					return true;
//				}
				return true;

			} else {
				if (parent.isProcedureParamBlock()) {
					// cambio el start para que sea el bloque donde empieza el
					// método
					Block method = Block
							.getBlock(parent.getPlug().getBlockID());
					if (method != null)
						parent = method;

				}

				// si tiene before connector quiere decir que esta en un metodo
				// de robocode o propio
				// me fijo si desde la declaracion de la variable se contiene al
				// bloque con el que se
				// quiere enganchar el getter o setter
				if ((parent != block2 && BlockUtilities.containsBlock(parent,
						block2)) || BlockUtilities.isGlobalDecl(parent))
					return true;
				else {
					if (parent == block2)
						return false;
					else {
						new MessageDialog(Language.get("error.useDecl"),
								MessageType.ERROR);
						BlockLink.boingSound.play();
						return false;
					}
				}
			}
		}

		return false;

	}

	public boolean isMandatory() {
		return false;
	}

}
