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

package rita.ui.component.exception;

public class NoEnemiesException extends Exception {
	private static final long serialVersionUID = 2265164779065314928L;

	public	NoEnemiesException(String desc) {
		super(desc);
	}

	public	NoEnemiesException(String desc, Exception cause) {
		super(desc, cause);
	}

	public	NoEnemiesException(Exception cause) {
		super(cause);
	}
}
