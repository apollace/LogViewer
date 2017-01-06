/**
 * This file belonging to LogViewer an open source tool to search and trace
 * information contained in your logs.  
 * Copyright (C) 2016  Alessandro Pollace
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.polly.log.viewer;

import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.text.*;

/*
 *  Track the movement of the Caret by painting a background line at the
 *  current caret position.
 */
public class LinePainter extends LineHighligter implements CaretListener,
		MouseListener, MouseMotionListener {
	private JTextComponent component;
	private Object highlightTag;

	/*
	 * The line color will be calculated automatically by attempting to make the
	 * current selection lighter by a factor of 1.2.
	 * 
	 * @param component text component that requires background line painting
	 */
	public LinePainter(JTextComponent component) {
		this(component, component.getSelectionColor(), null);
	}

	public LinePainter(JTextComponent component, Color color) {
		this(component, color, null);
	}

	/*
	 * Manually control the line color
	 * 
	 * @param component text component that requires background line painting
	 * 
	 * @param color the color of the background line
	 */
	public LinePainter(JTextComponent component, Color color,
			Integer caretPosition) {
		super(color, caretPosition);
		this.component = component;

		// Add listeners so we know when to change highlighting

		component.addCaretListener(this);
		component.addMouseListener(this);
		component.addMouseMotionListener(this);

		// Turn highlighting on by adding a dummy highlight

		startHighlight();
	}

	public void stopHighlight() {
		component.removeCaretListener(this);
		component.removeMouseListener(this);
		component.removeMouseMotionListener(this);

		this.component.getHighlighter().removeHighlight(highlightTag);
	}

	public void startHighlight() {
		try {
			highlightTag = this.component.getHighlighter().addHighlight(0, 0,
					this);
		} catch (BadLocationException ble) {
			ble.printStackTrace();
		}
	}

	// Implement CaretListener

	public void caretUpdate(CaretEvent e) {
		reset();
	}

	// Implement MouseListener

	public void mousePressed(MouseEvent e) {
		reset();
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	// Implement MouseMotionListener

	public void mouseDragged(MouseEvent e) {
		reset();
	}

	public void mouseMoved(MouseEvent e) {
	}
}