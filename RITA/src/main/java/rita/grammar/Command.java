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

package rita.grammar;

import java.util.ArrayList;
import java.util.List;

import renderable.RenderableBlock;
import rita.grammar.exception.IncompleteSentenceException;

import codeblocks.Block;

/**
 * Esta clase abstracta representa a todas las sentencias que pueden aparecer en código Java
 * 
 * @author Vanessa Aybar Rosales
 * */
public abstract class Command {

	protected List<String> args = new ArrayList<String>();
	private String stringSentence;
	Block block;

	public Command() {

	}

	protected Command(List<String> args) {
		this.args = args;
	}

	public abstract List<StringBuffer> render(RenderableBlock rBlock)
			throws IncompleteSentenceException;

	protected List<String> getArgs() {
		return args;
	}

	public void setArgs(List<String> args) {
		this.args = args;
	}

	public String getStringSentence() {
		return stringSentence;
	}

	public void setStringSentence(String stringSentence) {
		this.stringSentence = stringSentence;
	}

	public void setBlock(Block block) {
		this.block = block;
	}

	public boolean isCaller() {
		return false;
	}
}
