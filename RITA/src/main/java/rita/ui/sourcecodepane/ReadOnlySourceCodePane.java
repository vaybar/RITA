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

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.HashMap;

import javax.swing.event.ChangeListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.EditorKit;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;

import rita.compiler.CompileString;
import rita.ui.sourcecodepane.impl.DocumentFilterChain;
import rita.ui.sourcecodepane.impl.LineNumbersTextPane;
import rita.ui.sourcecodepane.impl.LineWrapEditorKit;

	
/** Top level class to handle code syntax and advanced edition features.
 * @author richet
 */
public class ReadOnlySourceCodePane extends LineNumbersTextPane  {
	private static final long serialVersionUID = -3544744057846780895L;

	/** Un caret que no hace nada: ni se dibuja, ni notifica eventos, ni permite seleccion de texto */
	private static final class NullCaret implements Caret {

		@Override
		public void addChangeListener(ChangeListener l) { 	}

		@Override
		public void deinstall(JTextComponent c) { }

		@Override
		public int getBlinkRate() { return 0;	}

		@Override
		public int getDot() { return 0; }

		@Override
		public Point getMagicCaretPosition() { return new Point(0,0);	}

		@Override
		public int getMark() { return 0; }

		@Override
		public void install(JTextComponent c) { }

		@Override
		public boolean isSelectionVisible() { return false; }

		@Override
		public boolean isVisible() { return false; }

		@Override
		public void moveDot(int dot) {	}

		@Override
		public void paint(Graphics g) {	}

		@Override
		public void removeChangeListener(ChangeListener l) { }

		@Override
		public void setBlinkRate(int rate) { }

		@Override
		public void setDot(int dot) { }

		@Override
		public void setMagicCaretPosition(Point p) { }

		@Override
		public void setSelectionVisible(boolean v) { }

		@Override
		public void setVisible(boolean v) { }
		
	}
	
    /** Here is a way to handle regexp on keywords.*/
    public static class RegExpHashMap extends HashMap<String,Color> {
		private static final long serialVersionUID = -8035466064840217610L;

		public boolean keyAsRegexp = true;

        @Override
        public boolean containsKey(Object o) {
            if (keyAsRegexp) {
                for (Object regexp_key : super.keySet()) {
                    if (o.toString().matches(regexp_key.toString())) {
                        return true;
                    }
                }
                return false;
            } else {
                for (Object regexp_key : super.keySet()) {
                    if (o.toString().equals(regexp_key.toString())) {
                        return true;
                    }
                }
                return false;
            }
        }

        @Override
        public Color get(Object o) {
            if (keyAsRegexp) {
                for (Object regexp_key : super.keySet()) {
                    if (o.toString().matches(regexp_key.toString())) {
                        return super.get(regexp_key.toString());
                    }
                }
                return null;
            } else {
                for (Object regexp_key : super.keySet()) {
                    if (o.toString().equals(regexp_key.toString())) {
                        return super.get(regexp_key.toString());
                    }
                }
                return null;
            }
        }
    }

    public static int DEFAULT_FONT_SIZE = 12;
    protected static char[][] openCloseBraces = {{'(', ')'}, {'[', ']'}, {'{', '}'}/*, {'<', '>'}, {'\'', '\''}, {'"', '"'}*/};
    protected DefaultSyntaxColorizer syntaxDocumentFilter;

    public ReadOnlySourceCodePane() {
        super();
        
        setTabSize(4);
        
        this.setEditable(false);
        
        /** Esto desactiva la seleccion de texto */
        this.setCaret(new NullCaret());
        // la convencion rara de Swing es que a menos que llames a setToolTipText con un valor != null, los tooltips están deshabilitados.
        this.setToolTipText("");
        syntaxDocumentFilter = new DefaultSyntaxColorizer(this);
    }
        

    public void setNormalSourceColor(Color c) {
    	syntaxDocumentFilter.setNormalSourceColor(c);
    }
    
    public void setNumberColor(Color c) {
    	syntaxDocumentFilter.setNumberColor(c);    	
    }

    public void setOperatorColor(Color c) {
    	syntaxDocumentFilter.setOperatorColor(c);    	
    }
    
    public void setStringLiteralColor(Color c) {
    	syntaxDocumentFilter.setStringLiteralColor(c);    	
    }
    
    public void setCommentColor(Color c) {
    	syntaxDocumentFilter.setCommentColor(c);    	
    }

    public void setKeywordColors(HashMap<String, Color> keywordColors) {
        if (keywordColors == null) {
        	keywordColors = new HashMap<String, Color>();
        }
        syntaxDocumentFilter.setKeywordColor(keywordColors);
        updateDocumentFilter();
    }
    
    protected void updateDocumentFilter() {
        ((AbstractDocument) this.getDocument()).setDocumentFilter(new DocumentFilterChain(syntaxDocumentFilter));
    }
    
    @Override
    protected EditorKit createDefaultEditorKit() {
        return new LineWrapEditorKit();
    }

    @Override
    public void setFont(Font font) {
        if (font == null || font.getFamily() == null) {
            return;
        }
        super.setFont(font);
    }

    public int getLineStartOffset(int line) throws BadLocationException {
        Element map = this.getDocument().getDefaultRootElement();
        if (line < 0) {
            throw new BadLocationException("Negative line", -1);
        } else if (line >= map.getElementCount()) {
            throw new BadLocationException("There is no line #"+line, this.getDocument().getLength() + 1);
        } else {
            Element lineElem = map.getElement(line);
            return lineElem.getStartOffset();
        }
    }
    
    public int getLineEndOffset(int line) throws BadLocationException {
        Element map = this.getDocument().getDefaultRootElement();
        if (line < 0) {
            throw new BadLocationException("Negative line", -1);
        } else if (line >= map.getElementCount()) {
            throw new BadLocationException("No such line", this.getDocument().getLength() + 1);
        } else {
            Element lineElem = map.getElement(line);
            return lineElem.getEndOffset();
        }
    }

	@Override
	public String getToolTipText(MouseEvent event) {
		/* se le suma 1 porque el compilador ve las lineas desde 1 a N,
		 * mientras que el textarea las ve desde 0 a N-1 */
		if (containsError(this.getModelLine(event) + 1)) {
			String text = CompileString.getDiagnosticSelectedText();
			this.setToolTipText(text);
			return super.getToolTipText(event);
		}
		return null;
	}

	/*
	 * Este metodo devuelve el numero de linea en el text area donde ocurrio
	 * el evento.
	 */
	private int getModelLine(MouseEvent event) {
		int pos = this.viewToModel(event.getPoint());
		int x1 = 0;
		int x2 = 0;
		int iter = 0;
		for (int i = 0; i < pos;) {
			try {
				x1 = i;
				x2 = this.getLineEndOffset(iter);
				if (x1 <= pos && pos <= x2) {
					return iter;
				}
			} catch (BadLocationException e) {

				e.printStackTrace();
				return -1;
			}
			i = x2;
			iter++;
		}
		return -1;
	}

	private boolean containsError(int line) {
		return CompileString.containsPosition(line);
	}
	
}
