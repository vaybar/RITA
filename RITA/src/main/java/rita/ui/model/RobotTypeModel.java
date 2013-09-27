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

package rita.ui.model;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataListener;

/**
 * Esta clase contiene los tipos de Robots que pueden ser implementados por el
 * usuario. Actualmente no se usa, era usado para seleccionar un tipo de robot.  
 * 
 * @author Vanessa Aybar Rosales
 */
public class RobotTypeModel implements ComboBoxModel {

	private List<String> items = new ArrayList<String>();
	private List<ListDataListener> listeners = new ArrayList<ListDataListener>();
	private String selectedItem;

	public RobotTypeModel(List<String> data) {
		items = data;

	}

	@Override
	public Object getSelectedItem() {
		return this.selectedItem;
	}

	@Override
	public void setSelectedItem(Object anItem) {
		this.selectedItem = (String) anItem;

	}

	@Override
	public void addListDataListener(ListDataListener l) {
		listeners.add(l);

	}

	@Override
	public Object getElementAt(int index) {
		return items.get(index);
	}

	@Override
	public int getSize() {
		return items.size();
	}

	@Override
	public void removeListDataListener(ListDataListener l) {
		listeners.remove(l);

	}

	public boolean containsSelectedItem(Object item) {
		return items.contains((String) item);
	}

}
