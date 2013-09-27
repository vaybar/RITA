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

package rita.ui.component;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;

/**
 * Esta clase se encarga de calcular la posicion central en pantalla, de modo de
 * poder ubicarla centrada
 * 
 * @author Vanessa Aybar Rosales
 * */
public class PositionCalc {
	public static void centerDialog(Window w) {
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension screenSize = tk.getScreenSize();

		int screenHeight = screenSize.height;
		int screenWidth = screenSize.width;
		w.setLocation((int) ((screenWidth / 2) - (w.getSize().getWidth() / 2)),
				(int) ((screenHeight / 2) - (w.getSize().getHeight() / 2)));
	}
}
