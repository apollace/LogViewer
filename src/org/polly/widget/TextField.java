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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.plaf.basic.BasicTextFieldUI;
import javax.swing.text.JTextComponent;

public class TextField extends JTextField {
	private static final long serialVersionUID = -7599476632507162246L;

	public static class RoundedFieldUI extends BasicTextFieldUI {
		private int round = 3;
		private int distanceFromFreviousMaterial = 7;
		private int textSpacing = 7;

		public void installUI(JComponent c) {
			super.installUI(c);

			c.setOpaque(false);

			int s = distanceFromFreviousMaterial + 1 + textSpacing;
			c.setBorder(BorderFactory.createEmptyBorder(s, s, s, s));
		}

		protected void paintSafely(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			Shape border = getBorderShape();

			Stroke os = g2d.getStroke();

			for (int i = 0; i < distanceFromFreviousMaterial; i++) {
				g2d.setStroke(new BasicStroke(i * 2));
				g2d.setPaint(new Color(0, 0, 0, 5));
				g2d.draw(border);
			}
			g2d.setStroke(os);

			g2d.setPaint(Color.WHITE);
			g2d.fill(border);

			super.paintSafely(g);
		}

		private Shape getBorderShape() {
			JTextComponent component = getComponent();
			if (round > 0) {
				return new RoundRectangle2D.Double(distanceFromFreviousMaterial, distanceFromFreviousMaterial,
						component.getWidth() - distanceFromFreviousMaterial * 2 - 1,
						component.getHeight() - distanceFromFreviousMaterial * 2 - 1, round * 2, round * 2);
			} else {
				return new Rectangle2D.Double(distanceFromFreviousMaterial, distanceFromFreviousMaterial,
						component.getWidth() - distanceFromFreviousMaterial * 2 - 1,
						component.getHeight() - distanceFromFreviousMaterial * 2 - 1);
			}
		}
	}

	public TextField() {
		//setBorder(BorderFactory.createCompoundBorder(getBorder(), BorderFactory.createEmptyBorder(7, 7, 7, 7)));
		//setUI(new RoundedFieldUI());
		setBorder(MaterialBorder.getInstance());
	}
}
