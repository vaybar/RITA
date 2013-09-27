package codeblocks;

import java.util.List;

import workspace.WorkspaceEvent;
import workspace.WorkspaceListener;

public class CommandRule implements LinkRule, WorkspaceListener {
	
	public CommandRule() {
	}

	public boolean canLink(Block block1, Block block2, BlockConnector socket1, BlockConnector socket2) {
		//Vanessa: se agregó esto para que el ReturnBlockRule lo trate!
		if (includesReturn(block1)){
			return false;
		}
		
		if (block1.getGenusName().startsWith(BlockStub.GETTER_STUB)|| block1.getGenusName().startsWith(BlockStub.SETTER_STUB)){
			return false;
		}
		
		if (!BlockConnectorShape.isCommandConnector(socket1) || !BlockConnectorShape.isCommandConnector(socket2))
			return false;
		// We want exactly one before connector
		if (socket1 == block1.getBeforeConnector())
			return !socket1.hasBlock();
		else if (socket2 == block2.getBeforeConnector())
			return !socket2.hasBlock();
		return false;
	}
	

	public boolean isMandatory() {
		return false;
	}

	public void workspaceEventOccurred(WorkspaceEvent e) {
		// TODO Auto-generated method stub
		if (e.getEventType() == WorkspaceEvent.BLOCKS_CONNECTED) {
			BlockLink link = e.getSourceLink();
			if (link.getLastBlockID() != null && link.getLastBlockID() != Block.NULL &&
				BlockConnectorShape.isCommandConnector(link.getPlug()) && BlockConnectorShape.isCommandConnector(link.getSocket())) {
				Block top = Block.getBlock(link.getPlugBlockID());
				while (top.hasAfterConnector() && top.getAfterConnector().hasBlock())
					top = Block.getBlock(top.getAfterBlockID());
				Block bottom = Block.getBlock(link.getLastBlockID());
				
				// For safety: if either the top stack is terminated, or
				// the bottom stack is not a starter, don't try to force a link
				if (!top.hasAfterConnector() || !bottom.hasBeforeConnector())
				    return;
				
				link = BlockLink.getBlockLink(top, bottom, top.getAfterConnector(), bottom.getBeforeConnector());
				link.connect();
			}
		}
		
		if (e.getEventType() == WorkspaceEvent.BLOCKS_DISCONNECTED) {
			//bloque que contiene el plug
			Block disconnectedBlock=Block.getBlock(e.getSourceLink().getPlugBlockID());
			//bloque que contiene el socket
			Block parent=Block.getBlock(e.getSourceLink().getSocketBlockID());
			BlockConnector socket=parent.getConnectorTo(e.getSourceLink().getPlugBlockID());
			//			BlockConnector plug=e.getSourceLink().getPlug();
//			Block parent = Block.getBlock(plug.getBlockID());
//			
			//parent representa al bloque que tiene el socket
			//solo en este caso se desconecta y se cambia la forma del socket. No si lo que se desconecta es el bloque de arriba
			//o abajo del parent.
			if (parent!=null && parent.getGenusName().equals("print")&& 
					disconnectedBlock.getPlug()!=null && disconnectedBlock.getPlug().getBlockID().equals(parent.getBlockID())) {
				parent.getSocketAt(0).setKind("poly");
			}
			if (parent!=null && parent.getGenusName().equals("string-append")&& 
					disconnectedBlock.getPlug()!=null && disconnectedBlock.getPlug().getBlockID().equals(parent.getBlockID())) {
				parent.getSocketAt(0).setKind("poly");
			}
			//solo si es que se quiere desconectar un bloque que esta enganchado a la declaracion de una variable
			//ya que tambien se puede querer desconectar un bloque arriba o abajo de la declaracion
			if (parent!=null && parent.getGenusName().equals("declaration") && 
					disconnectedBlock.getPlug()!=null && disconnectedBlock.getPlug().getBlockID().equals(parent.getBlockID())) {
				parent.getSocketAt(0).setKind("poly");
				disconnectedBlock.blockDisconnected(parent.getSocketAt(0), parent);
			}

		}
	}

	
	
	private boolean includesReturn(Block start) {
		boolean result=false;
		if (start != null) {
			if (start.isReturnBlock()) {
			return true;
			}
			Block after=Block.getBlock(start.getAfterBlockID());
			if (includesReturn(after)) return true;
			else {
			Iterable<BlockConnector> sockets = start.getSockets();

			for (BlockConnector bc : sockets) {
				if (includesReturn(Block.getBlock(bc.getBlockID()))) return true;
			}
			}
		}
		return result;
	}
}
