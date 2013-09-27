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

package rita.ui.sourcecodepane.impl;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.StyledTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;

/** Component integrating scrollpane and left-side line numbers. Appears like a JXTextPane for easy replacement of former JXTextPane
@author richet (heavily inspired by many searches on the web)
 */
public class LineNumbersTextPane extends StyledTextPane {
	private static final long serialVersionUID = 7307633706618552614L;

	private boolean displayLineNumbers = true;
    protected JSplitPane topContainerPane;
    protected JScrollPane scrollPane;
    protected LineNumbersSidePane linenumbers;

    public Rectangle modelToScrollView(int pos) throws BadLocationException {
        Rectangle offset = super.modelToView(pos);
        offset.translate(-scrollPane.getViewport().getViewPosition().x, -scrollPane.getViewport().getViewPosition().y);
        return offset;
    }

    public int getNumberOfLines() {
        return ((LineWrapEditorKit) getEditorKit()).number_of_lines;
    }

    @Override
    public final void setEditorKit(EditorKit kit) {
        if (kit instanceof LineWrapEditorKit) {
            super.setEditorKit(kit);
            ((LineWrapEditorKit) kit).setWrap(false);
        } else {
            throw new IllegalArgumentException("Must be WrapEditorKit");
        }
    }

    @Override
    protected EditorKit createDefaultEditorKit() {
        LineWrapEditorKit kit = new LineWrapEditorKit();
        kit.setWrap(false);
        return kit;
    }
    
    public LineNumbersTextPane() {
        scrollPane = new JScrollPane(this);

        scrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {

            public void adjustmentValueChanged(AdjustmentEvent e) {
                updateLineNumberView();
            }
        });

        topContainerPane = new JSplitPane();
        topContainerPane.setBorder(null);
        topContainerPane.setDividerSize(0);
        topContainerPane.setRightComponent(scrollPane);
        linenumbers = new LineNumbersSidePane(this);
        topContainerPane.setLeftComponent(linenumbers);

        addKeyListener(new KeyAdapter() {

            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                updateLineNumberDivider();
            }
        });

        addMouseWheelListener(new MouseAdapter() {

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.isControlDown()) {
                    if (e.getWheelRotation() > 0) {
                        setFont(getFont().deriveFont(getFont().getSize2D() - 1.0f));
                    } else {
                        setFont(getFont().deriveFont(getFont().getSize2D() + 1.0f));
                    }
                    updateLineNumberDivider();
                } else {
                    for (MouseWheelListener m : scrollPane.getMouseWheelListeners()) {
                        m.mouseWheelMoved(e);
                    }
                }
            }
        });
    }

    @Override
    public void setCaretPosition(int position) {
        super.setCaretPosition(position);
        updateLineNumberDivider();
    }

    @Override
    public void setText(String t) {
        super.setText(t);
        updateLineNumberDivider();
        updateLineNumberView();
    }

    public void updateLineNumberDivider() {
        if (!isDisplayLineNumbers()) {
            return;
        }
        int width = linenumbers._editor.getFontMetrics(linenumbers._editor.getFont()).charWidth('0');
        int div = (int) (width * (Math.floor(Math.log10(((LineWrapEditorKit) getEditorKit()).number_of_lines + 1)) + 3));
        topContainerPane.setDividerLocation(div);
    }

    public void updateLineNumberView() {
        // We need to properly convert the points to match the viewport
        // Read docs for viewport
        viewStart = viewToModel(scrollPane.getViewport().getViewPosition());
        // starting pos  in document
        viewEnd = viewToModel(new Point(scrollPane.getViewport().getViewPosition().x,
                scrollPane.getViewport().getViewPosition().y + scrollPane.getViewport().getExtentSize().height));
        // end pos in doc
        if (!isDisplayLineNumbers()) {
            return;
        }
        linenumbers.repaint();
    }

    /** Este es el componente de Swing que contiene a una instancia de LineNumbersTextPane. */
    public Container getWrappingContainerWithLines() {
        return topContainerPane;
    }

    /**
     * @return the displayLineNumbers
     */
    public boolean isDisplayLineNumbers() {
        return displayLineNumbers;
    }

    /**
     * @param displayLineNumbers the displayLineNumbers to set
     */
    public void setDisplayLineNumbers(boolean displayLineNumbers) {
        this.displayLineNumbers = displayLineNumbers;
        if (!displayLineNumbers) {
            topContainerPane.setDividerLocation(0);
        }
    }
    protected int viewStart;
    protected int viewEnd;

    @Override
    public void setFont(Font font) {
        super.setFont(font);
        if (linenumbers != null) {
            linenumbers.fontChanged();
            linenumbers.repaint();
        }
    }

    public class LineNumbersSidePane extends JPanel {
		private static final long serialVersionUID = -398959970697739550L;

		LineNumbersTextPane _editor;
        private boolean updateFont;
        private int fontHeight;
        private int fontDesc;
        private int starting_y;

        public LineNumbersSidePane(LineNumbersTextPane editor) {
            _editor = editor;
            this.setForeground(Color.GRAY);
        }

        void fontChanged() {
            updateFont = true;
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);

            if (!isDisplayLineNumbers()) {
                return;
            }

            // translate offsets to lines
            Document doc = _editor.getDocument();
            int startline = doc.getDefaultRootElement().getElementIndex(viewStart) + 1;
            int endline = doc.getDefaultRootElement().getElementIndex(viewEnd) + 1;

            g.setFont(_editor.getFont());
            if (updateFont) {
                fontHeight = g.getFontMetrics(_editor.getFont()).getHeight();
                fontDesc = g.getFontMetrics(_editor.getFont()).getDescent();
                updateFont = false;
            }
            try {
                starting_y = _editor.modelToView(viewStart).y - scrollPane.getViewport().getViewPosition().y + fontHeight - fontDesc;
            } catch (BadLocationException e1) {
                e1.printStackTrace();
            }
            for (int line = startline, y = starting_y; line <= endline; y += fontHeight, line++) {
                g.drawString(" " + Integer.toString(line), 0, y);
            }
        }
    }
}
