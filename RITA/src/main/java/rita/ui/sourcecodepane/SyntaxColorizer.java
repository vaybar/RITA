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

package rita.ui.sourcecodepane;

import javax.swing.text.DocumentFilter;

/**
 *
 * @author richet
 */
public abstract class SyntaxColorizer extends DocumentFilter {

    public final static String ALL_OPERANDS = ",;:.!?{}()[]<>+-*/=\\%&|^~$@#";
    private String operands = ALL_OPERANDS;
    public final static String ALL_QUOTES = "'\"`";
    private String quotes = ALL_QUOTES;

    public abstract boolean isTokenSeparator(char character);

    public abstract boolean isKeyword(String token);

    /**
     * @return the operands
     */
    public String getOperands() {
        return operands;
    }

    /**
     * @param operands the operands to set
     */
    public void setOperands(String operands) {
        this.operands = operands;
    }

    /**
     * @return the quotes
     */
    public String getQuotes() {
        return quotes;
    }

    /**
     * @param quotes the quotes to set
     */
    public void setQuotes(String quotes) {
        this.quotes = quotes;
    }
}
