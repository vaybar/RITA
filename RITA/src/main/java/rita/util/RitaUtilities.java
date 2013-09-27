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

package rita.util;

import java.util.HashSet;
import java.util.Set;

import javax.swing.JOptionPane;

import codeblocks.Block;
import codeblocks.BlockConnector;

public class RitaUtilities {

    /*
     * Set containing the operators names that can be used with "double" and
     * "number" types. It's only initialized when used.
     * Implemented by student: Benencia
     */
    @SuppressWarnings("serial")
	private static Set<String> arithmeticOperators = new HashSet<String>() {{
		add("sum");
		add("difference");
		add("product");
		add("quotient");
	}};;

    @SuppressWarnings("serial")
	private static Set<String> comparators = new HashSet<String>() {{
		add("equals");
		add("not-equals");
		add("lessthan");
		add("lessthanorequalto");
		add("greaterthan");
		add("greaterthanorequalto");
	}};;

    @SuppressWarnings("serial")
	private static Set<String> mathFunctions = new HashSet<String>() {{
		add("asin");
		add("sin");
		add("atan");
		add("tan");
		add("acos");
		add("cos");
		add("random");
		add("abs");
		add("sqrt");
		add("toDegrees");
		add("toRadians");
	}};;


	public static final boolean isSimple(BlockConnector bc) {
		return bc.getKind().equals("boolean") || bc.getKind().equals("string")
				|| bc.getKind().equals("number") || bc.getKind().equals("double");
	}

	/**
	 * Muestra una ventana de confirmaci�n con opciones OK y Cancel
	 *
	 * @param title
	 * */
	public static final boolean confirmWindow(String title, String message) {
		int result = JOptionPane.showConfirmDialog(null, message, title,
				JOptionPane.OK_CANCEL_OPTION);
		return result != JOptionPane.CANCEL_OPTION;
	}

	public static String normalizeRobotName(String roboName) {
		if(!roboName.isEmpty()) {
			StringBuilder newName = new StringBuilder(roboName.length());
			newName.append(Character.toUpperCase(roboName.charAt(0)));
			char c;
			for(int i=1;i<roboName.length();i++) {
				c=roboName.charAt(i);
				if(isValidCharForRobotName(c)) {
					newName.append(Character.toLowerCase(c));
				} else {
					newName.append('_');
				}
			}
			return newName.toString();
		} return roboName;
	}

	public static boolean isValidCharForRobotName(char c) {
		return Character.isLetterOrDigit(c) || c=='_' || c=='-';
	}

	/**
	 * Returns whether the block is an arithmetic operator.
	 *
	 * @return "true" if is an arithmetic operator, "false" otherwise.
	 *
	 * Method implemented by student: Benencia
	 */
    public static boolean isArithmeticOperator(Block block) {
    	return (arithmeticOperators.contains(block.getGenusName()));
    }

	/**
	 * Returns whether the block is an arithmetic operand.
	 *
	 * @return "true" if is an arithmetic operand, "false" otherwise.
	 *
	 * Method implemented by student: Benencia
	 */
    public static boolean isArithmeticOperand(Block block) {
	return (block.getPlugKind() != null &&
			(block.getPlugKind().equals("number") ||
					block.getPlugKind().equals("double")));
    }

	/**
	 * Returns whether the block is the sum operator.
	 *
	 * @return "true" if is the sum operator, "false" otherwise.
	 *
	 * Method implemented by student: Benencia
	 */
    public static boolean isSumOperator(Block block) {
    	return (block.getGenusName().equals("sum"));
    }

	/**
	 * Returns whether the block is a string.
	 *
	 * @return "true" if is a string, "false" otherwise.
	 *
	 * Method implemented by student: Benencia
	 */
    public static boolean isStringBlock(Block block) {
    	return (block.getGenusName().equals("string"));
    }

	/**
	 * Returns whether the block is a comparator.
	 *
	 * @return "true" if is a comparator, "false" otherwise.
	 *
	 * Method implemented by student: Benencia
	 */
    public static boolean isComparator(Block block) {
    	return (comparators.contains(block.getGenusName()));
    }

	/**
	 * Returns whether the block is a mathematical function.
	 *
	 * @return "true" if is a mathematical function, "false" otherwise.
	 *
	 * Method implemented by student: Benencia
	 */
    public static boolean isMathFunction(Block block) {
		return (mathFunctions.contains(block.getGenusName()));
    }

	public static boolean isAbsFunction(Block block) {
		return (block.getGenusName().equals("abs"));
	}
	
	/**
	 * Returns whether the block is a simple adaptable block.
	 * If it is not simple, then parentheses should be added to
	 * the cast. 
	 * @return "true" if is simple, "false" otherwise.
	 *
	 * Method implemented by student: Benencia
	 */
	public static boolean isSimpleAdaptable(Block block) {
		return (block.getGenusName().equals("number") ||
				block.getGenusName().equals("double"));
	}
}
