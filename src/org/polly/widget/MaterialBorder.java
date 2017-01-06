/**
 * This file belonging to LogViewer an open source tool to search and trace
 * information contained in your logs.  
 * Copyright (C) 2017  Alessandro Pollace
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
package org.polly.widget;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.border.AbstractBorder;

/**
 * A border with a drop shadow intended to be used as the outer border of
 * popups. Can paint the screen background if used with heavy-weight popup
 * windows.
 *
 * @author Karsten Lentzsch
 * @author Andrej Golovnin
 * @version $Revision: 1.6 $
 *
 * @see ShadowPopup
 * @see ShadowPopupFactory
 */
final class MaterialBorder extends AbstractBorder {
	private static final long serialVersionUID = 1L;

	/**
	 * The drop shadow needs 5 pixels at the bottom and the right hand side.
	 */
	private static final int DARK_FACTOR = 3;
	private static final int SHADOW_SIZE = 7;
	private static final int PADDING_SIZE = 7;

	/**
	 * The singleton instance used to draw all borders.
	 */
	private static MaterialBorder instance = new MaterialBorder();

	// Instance Creation *****************************************************

	/**
	 * Returns the singleton instance used to draw all borders.
	 */
	public static MaterialBorder getInstance() {
		return instance;
	}

	/**
	 * Paints the border for the specified component with the specified position
	 * and size.
	 */
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		Color baseColor = c.getParent().getBackground();

		for (int i = 0; i < SHADOW_SIZE; i++) {
			g.setColor(baseColor);
			g.drawRect(i, i, width - i * 2 - 1, height - i * 2 - 1);

			int red = baseColor.getRed() > DARK_FACTOR ? baseColor.getRed() - DARK_FACTOR : 0;
			int green = baseColor.getGreen() > DARK_FACTOR ? baseColor.getGreen() - DARK_FACTOR : 0;
			int blue = baseColor.getBlue() > DARK_FACTOR ? baseColor.getBlue() - DARK_FACTOR : 0;
			baseColor = new Color(red, green, blue);
		}

		baseColor = c.getBackground();
		for (int i = SHADOW_SIZE; i < PADDING_SIZE; i++) {
			g.setColor(baseColor);
			g.drawRect(i, i, width - i * 2 - 1, height - i * 2 - 1);
		}
	}

	/**
	 * Returns the insets of the border.
	 */
	public Insets getBorderInsets(Component c) {
		return new Insets(SHADOW_SIZE + PADDING_SIZE, SHADOW_SIZE + PADDING_SIZE, SHADOW_SIZE + PADDING_SIZE,
				SHADOW_SIZE + PADDING_SIZE);
	}

	/**
	 * Reinitializes the insets parameter with this Border's current Insets.
	 * 
	 * @param c
	 *            the component for which this border insets value applies
	 * @param insets
	 *            the object to be reinitialized
	 * @return the <code>insets</code> object
	 */
	public Insets getBorderInsets(Component c, Insets insets) {
		insets.left = insets.top = insets.right = insets.bottom = SHADOW_SIZE + PADDING_SIZE;
		return insets;
	}

}