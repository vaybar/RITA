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

package rita.listener;

import renderable.RenderableBlock;
import rita.widget.SourceCode;
import workspace.Page;
import workspace.WorkspaceEvent;
import workspace.WorkspaceListener;
import codeblocks.Block;

/**
 * Esta clase es un listener del Workspace, se creó con la finalidad de resaltar el código Java
 * cuando un usuario selecciona uno de los bloques
 * 
 * @author Vanessa Aybar Rosales
 * */
//TODO Vanessa: revisar esta funcionalidad, los casos base funcionan.
public class CodeStyleManager implements WorkspaceListener {

	@Override
	public void workspaceEventOccurred(WorkspaceEvent event) {
    	if(event.getEventType() == WorkspaceEvent.BLOCK_SELECTED_HIGHLIGHT_CODE){
        	if(event.getSourceWidget() instanceof Page){
        		Page page = (Page)event.getSourceWidget();
        		Block block = Block.getBlock(event.getSourceBlockID());
        		
//        		SourceCode.highlightCode(RenderableBlock.getRenderableBlock(block.getBlockID()));
//        		if(block.hasStubs()){
//                    for(BlockStub stub : block.getFreshStubs()){
//                        this.addDynamicBlock(
//                        		new FactoryRenderableBlock(this, stub.getBlockID()),
//                        		page.getPageDrawer());
//                    }
//                }

        	}
    	}
	}

}
