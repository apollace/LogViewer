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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.Timer;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;

/**
 * This class is used to highlight some specific content inside a
 * {@link JTextComponent}
 * 
 * You can use this class to highlight the occurrences of a specific word, you
 * can choose the highlight color.
 * 
 * @author Alessandro Pollace
 */
public class LogPaneHighlighter {
	private JTextComponent comp;
	Color lineHighlight = new Color(0, 162, 232, 200);
	private LinePainter linePainter;

	private Map<String, Entry<Color, String[]>> subStringsByColor = new HashMap<String, Entry<Color, String[]>>();

	private Timer timer = null;
	private ActionListener taskPerformer = new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			refresh();
			timer = null;
		}
	};

	public LogPaneHighlighter(JTextComponent comp) {
		this.comp = comp;
		linePainter = new LinePainter(comp, lineHighlight);
	}

	private void highlight(Color color, String subString) {
		Highlighter h = comp.getHighlighter();
		if (subString.length() <= 0)
			return;

		String text = comp.getText();

		int startIndex = 0;
		int len = subString.length();
		while ((startIndex = text.indexOf(subString, startIndex)) >= 0) {
			try {
				h.addHighlight(startIndex, startIndex + len, new DefaultHighlighter.DefaultHighlightPainter(color));
			} catch (BadLocationException ble) {
				ble.printStackTrace();
			}

			startIndex += len;
		}
	}

	public void refresh() {
		Highlighter h = comp.getHighlighter();
		h.removeAllHighlights();
		for (Entry<String, Entry<Color, String[]>> entry : subStringsByColor.entrySet()) {
			for (String subString : entry.getValue().getValue()) {
				highlight(entry.getValue().getKey(), subString);
			}
		}
		linePainter.startHighlight();
	}

	public void setHighlightByColor(String key, Color color, String[] subStrings) {
		subStringsByColor.put(key, new AbstractMap.SimpleEntry<Color, String[]>(color, subStrings));

		int delay = 500;
		if (timer == null) {
			timer = new Timer(delay, taskPerformer);
			timer.setRepeats(false);
			timer.start();
		} else {
			timer.restart();
		}
	}

}
