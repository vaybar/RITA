package codeblocks;

import rita.settings.Language;
import rita.ui.component.MessageDialog;
import rita.ui.component.MessageDialog.MessageType;
import codeblocks.BlockConnector.PositionType;

/**
 * <code>SocketRule</code> checks if the two sockets being matched can connect
 * simply by checking if the socket/plug match in kind.
 * 
 */
public class SocketRule implements LinkRule {

	/**
	 * Returns true if the two sockets of the two blocks can link by matching
	 * their socket kind; false if not. Both sockets must be empty to return
	 * true.
	 * 
	 * @param block1
	 *            the associated <code>Block</code> of socket1
	 * @param block2
	 *            the associated <code>Block</code> of socket2
	 * @param socket1
	 *            a <code>Socket</code> or plug of block1
	 * @param socket2
	 *            a <code>Socket</code> or plug of block2
	 * @return true if the two sockets of the two blocks can link; false if not
	 */
	public boolean canLink(Block block1, Block block2, BlockConnector socket1,
			BlockConnector socket2) {
		// if (block2.isProcedureDeclBlock() && !block1.isProcedureParamBlock()
		// && socket1.getPositionType().equals(PositionType.SINGLE) &&
		// socket2.getPositionType().equals(PositionType.SINGLE)) {
		// new MessageDialog("Debe asociarse un parámetro!");
		// BlockLink.boingSound.play();
		// return false;
		// }
		
		if (block1.isProcedureParamBlock()) {
			return false;
		}
		if (block1.getGenusName().startsWith(BlockStub.GETTER_STUB)|| block1.getGenusName().startsWith(BlockStub.SETTER_STUB)){
			return false;
		}
		
		if (block1.isVariableDeclBlock()){
			return false;
		}
		
		if (block2.isCommandBlock() && block2.getGenusName().equals("print")) {
			block2.getSocketAt(0).setKind(block1.getPlugKind());
			return true;
		}

		if (block2.getGenusName().equals("string-append")) {
			socket2.setKind(block1.getPlugKind());
			return true;
		}

		// Make sure that none of the sockets are connected,
		// and that exactly one of the sockets is a plug.
		if (socket1.hasBlock()
				|| socket2.hasBlock()
				|| !((block1.hasPlug() && block1.getPlug() == socket1) ^ (block2
						.hasPlug() && block2.getPlug() == socket2)))
			return false;

		// If they both have the same kind, then they can connect
		if (socket1.getKind().equals(socket2.getKind())){
			//PERERATARRAGONA
			if(!block1.isMethodWithEvent() && !block1.isEventMethod() && !block2.isEventMethod() ){
				return true;
			}
		}
		else {
			if (!socket2.getKind().equals("poly")) {
				new MessageDialog(Language.get("error.tipoIncorrecto"),MessageType.ERROR);
				BlockLink.boingSound.play();
				return false;
			}

		}
		return false;

	}

	public boolean isMandatory() {
		return false;
	}
}
