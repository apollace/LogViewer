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
import javax.swing.JTextArea;
import javax.swing.UIManager;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionEvent;

public class WidgetContainer<T extends Component> extends JPanel {
	private static final long serialVersionUID = 2120950893553289059L;

	private JPanel panelForWidgets = new JPanel();
	private JPanel panelForButton = new JPanel(new FlowLayout());
	private JButton btnAddWidget = null;
	private List<T> widgets = new ArrayList<T>();
	private WidgetFactory factory = null;

	public interface WidgetFactory {
		public JPanel getWidgetPanel();
	}

	/**
	 * Create the panel.
	 */
	public WidgetContainer(WidgetFactory factory, String buttonCaption, String description) {
		setLayout(new BorderLayout());
		setBorder(MaterialBorder.getInstance());

		btnAddWidget = new JButton("Add " + buttonCaption);
		panelForButton.add(btnAddWidget);

		JTextArea textArea = new JTextArea();
		textArea.setText(description);
		textArea.setWrapStyleWord(true);
		textArea.setLineWrap(true);
		textArea.setOpaque(false);
		textArea.setEditable(false);
		textArea.setFocusable(false);
		textArea.setBackground(UIManager.getColor("Label.background"));
		textArea.setFont(UIManager.getFont("Label.font"));
		textArea.setBorder(UIManager.getBorder("Label.border"));

		add(textArea, BorderLayout.NORTH);
		add(panelForWidgets, BorderLayout.CENTER);
		add(panelForButton, BorderLayout.SOUTH);

		btnAddWidget.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (WidgetContainer.this.factory != null) {
					widgets.add((T) WidgetContainer.this.factory.getWidgetPanel());

					panelForWidgets.removeAll();
					panelForWidgets.setLayout(new GridLayout(widgets.size(), 0, 0, 0));
					for (Component widget : widgets) {
						panelForWidgets.add(widget);
					}
				}

				java.awt.Component comp = WidgetContainer.this;
				while (comp != null) {
					comp.invalidate();
					comp.validate();
					comp = comp.getParent();
				}
			}
		});

		this.factory = factory;
		panelForWidgets.setLayout(new GridLayout(1, 0, 0, 0));

		T widget = (T) factory.getWidgetPanel();
		widgets.add(widget);
		panelForWidgets.add(widget);
	}

	public List<T> getWidgets() {
		return widgets;
	}

}
