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

import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JSplitPane;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

public class SplitPane extends JSplitPane {
	private static final long serialVersionUID = 1L;

	class ButtonDividerUI extends BasicSplitPaneUI {
		protected JButton button = new JButton("<>");

		public ButtonDividerUI() {
		}

		public BasicSplitPaneDivider createDefaultDivider() {
			BasicSplitPaneDivider divider = new BasicSplitPaneDivider(this) {

				private static final long serialVersionUID = 1L;

				public int getDividerSize() {
					if (getOrientation() == JSplitPane.HORIZONTAL_SPLIT) {
						return button.getPreferredSize().width;
					}
					return button.getPreferredSize().height;
				}
			};

			divider.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
			button.setBorder(javax.swing.BorderFactory.createEmptyBorder());
			button.setBackground(divider.getBackground());
			divider.add(button);

			return divider;
		}
	}

	public SplitPane() {
		//setUI(new ButtonDividerUI());
	}

}
