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
package org.polly.widget;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextArea;
import javax.swing.JTextPane;

/**
 * This class is a common {@link JTextPane} with added a description. The
 * description will be showed only when there is nothing inside the text box, at
 * first user event the description disappears when the text box is empty the
 * description appears
 * 
 * @author Alessandro Pollace
 */
public class TextAreaWithDescription extends JTextArea {
	private static final long serialVersionUID = 4852639826864644501L;

	private Color originalForeground;
	private boolean originalLineWrap;
	private String description;

	private void setDescription() {
		this.originalForeground = this.getForeground();
		this.setForeground(Color.gray);
		this.setText(this.description);
		this.originalLineWrap = this.getLineWrap();
		this.setLineWrap(true);
	}

	private void removeDescription() {
		this.setForeground(this.originalForeground);
		this.setText("");
		this.setLineWrap(this.originalLineWrap);
	}

	public TextAreaWithDescription(String description) {
		super();
		this.description = description;

		addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent arg0) {
				if (TextAreaWithDescription.this.getText().trim().length() == 0) {
					TextAreaWithDescription.this.setDescription();
				}
			}

			@Override
			public void focusGained(FocusEvent arg0) {
				if (TextAreaWithDescription.this.getText().length() == 0) {
					TextAreaWithDescription.this.removeDescription();
				}

			}
		});

		setDescription();
	}

	@Override
	public String getText() {
		if (super.getText().equals(this.description)) {
			return "";
		}
		return super.getText();
	}
}
