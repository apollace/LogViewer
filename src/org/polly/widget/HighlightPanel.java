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

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import java.awt.GridLayout;
import java.util.Vector;

import javax.swing.event.ChangeListener;

import javax.swing.event.ChangeEvent;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class HighlightPanel extends JPanel {
	private static final long serialVersionUID = 3628157054295125195L;
	private JTextArea txtQuery = new JTextArea();
	private final JPanel colorsPanel = new JPanel();

	private HighlightCallback highlightCallback = null;

	private JPanel selectedColor = null;
	private Vector<JPanel> colorPickers = new Vector<JPanel>();
	private Vector<Color> colors = new Vector<Color>();
	private static int lastStartColorIndex = 0;

	void selectColor(JPanel p) {
		if (selectedColor != null) {
			selectedColor.setBorder(BorderFactory.createLineBorder(selectedColor.getBackground()));
			selectedColor.setPreferredSize(new Dimension(15, 15));
			selectedColor.revalidate();
			selectedColor = p;
		} else {
			selectedColor = colorPickers.get(lastStartColorIndex);
			lastStartColorIndex++;
			lastStartColorIndex = lastStartColorIndex % colorPickers.size();
		}

		selectedColor.setBorder(BorderFactory.createLineBorder(Color.gray));
		selectedColor.setPreferredSize(new Dimension(25, 25));
		selectedColor.revalidate();
	}

	public interface HighlightCallback {
		void highlight(Color color, String[] queries);
	}

	/**
	 * Create the panel.
	 */
	public HighlightPanel() {
		setLayout(new BorderLayout(0, 0));

		colors.add(new Color(255, 255, 0, 125));

		colors.add(new Color(255, 0, 0, 125));
		colors.add(new Color(0, 255, 0, 125));
		colors.add(new Color(0, 0, 255, 125));

		colors.add(new Color(255, 0, 255, 125));
		colors.add(new Color(0, 255, 255, 125));

		colors.add(new Color(255, 125, 125, 125));
		colors.add(new Color(125, 255, 125, 125));
		colors.add(new Color(125, 125, 255, 125));

		JPanel panel = new JPanel();
		add(panel, BorderLayout.SOUTH);
		panel.setLayout(new GridLayout(1, 1, 0, 0));

		for (Color c : colors) {
			JPanel panelColor = new JPanel();
			panelColor.setBackground(c);
			panelColor.setPreferredSize(new Dimension(15, 15));
			panelColor.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					selectColor((JPanel) e.getComponent());
					highlight();
				}
			});

			colorPickers.add(panelColor);
			colorsPanel.add(panelColor);
		}
		panel.add(colorsPanel);
		selectColor(null);

		JScrollPane scrollPanel = new JScrollPane();
		scrollPanel.setViewportView(txtQuery);
		add(scrollPanel, BorderLayout.CENTER);
		txtQuery.setColumns(10);
		txtQuery.setRows(5);

	}

	public void setHighlightCallback(final HighlightCallback highlightCallback) {
		this.highlightCallback = highlightCallback;

		txtQuery.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {

			}

			@Override
			public void keyReleased(KeyEvent e) {
				highlight();
			}

			@Override
			public void keyPressed(KeyEvent e) {

			}
		});
	}

	private void highlight() {
		if (selectedColor != null && highlightCallback != null) {
			String[] queries = txtQuery.getText().split("\n");
			this.highlightCallback.highlight(selectedColor.getBackground(), queries);
		}
	}

}
