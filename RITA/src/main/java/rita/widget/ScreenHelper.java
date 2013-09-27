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

package rita.widget;

import java.awt.Dimension;

public class ScreenHelper {

	private static double diag;

	static {
		Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		int pixelPerInch = java.awt.Toolkit.getDefaultToolkit()
				.getScreenResolution();
		// double height=screen.getHeight()/pixelPerInch;
		// double width=screen.getWidth()/pixelPerInch;
		// double x=Math.pow(height,2);
		// double y=Math.pow(width,2);
		diag = Math.sqrt(Math.pow(screen.getWidth(), 2)
				+ Math.pow(screen.getHeight(), 2))
				/ pixelPerInch;
	}

	private ScreenHelper(){
		
	}
	
	public static ScreenSize getSize() {
		if (diag < 14)
			return ScreenSize.SMALL;
		return ScreenSize.NORMAL;
	}

	/**
	 * ScreenSize indica si el tamaño corresponde a una netbook o a una laptop o
	 * computador de tamaño razonable
	 * */
	public enum ScreenSize {
		SMALL, NORMAL
	}
}
