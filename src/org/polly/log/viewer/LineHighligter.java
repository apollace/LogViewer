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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;

import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;

public class LineHighligter implements Highlighter.HighlightPainter {
	Color color;
	JTextComponent component = null;
	Rectangle highlighted = null;
	Integer caretPosition = null;

	public LineHighligter(Color color, Integer caretPosition) {
		this.color = color;
		this.caretPosition = caretPosition;
	}

	public LineHighligter(Color color) {
		this(color, null);
	}

	@Override
	public void paint(Graphics g, int p0, int p1, Shape bounds, JTextComponent c) {
		component = c;
		try {
			int carPos = c.getCaretPosition();
			if (caretPosition != null) {
				carPos = caretPosition;
			}
			Rectangle r = c.modelToView(carPos);
			g.setColor(color);
			g.fillRect(0, r.y, c.getWidth(), r.height);
			highlighted = r;
		} catch (BadLocationException ble) {
			System.out.println(ble);
		}
	}

	public void reset() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					if (highlighted == null || component == null)
						return;

					int offset = component.getCaretPosition();
					Rectangle currentView = component.modelToView(offset);

					// Remove the highlighting from the previously highlighted
					// line
					component.repaint(0, highlighted.y, component.getWidth(),
							highlighted.height);
					highlighted = currentView;
				} catch (BadLocationException ble) {
				}
			}
		});
	}

}
