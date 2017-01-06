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
import javax.swing.JCheckBox;
import java.awt.GridLayout;
import javax.swing.event.ChangeListener;

import org.polly.log.viewer.QueryGenerator;

import javax.swing.event.ChangeEvent;

public class QueryPanel extends JPanel implements QueryGenerator {
	private static final long serialVersionUID = 3628157054295125195L;
	private JTextArea txtQuery = new JTextArea();

	private JCheckBox chckbxRegularExpression = new JCheckBox("Regular expression");
	private JCheckBox chckbxIgnoreWhiteSpaces = new JCheckBox("Ignore white spaces");
	private JCheckBox chckbxCaseSensitive = new JCheckBox("Case sensitive");
	private JCheckBox chckbxEnable = new JCheckBox("Enable");

	private void selectCheckBoxes() {
		boolean isEnabled = chckbxEnable.isSelected();
		boolean isRegEx = chckbxRegularExpression.isSelected();

		txtQuery.setEnabled(isEnabled);
		chckbxRegularExpression.setEnabled(isEnabled);
		chckbxCaseSensitive.setEnabled(isEnabled && !isRegEx);
		chckbxIgnoreWhiteSpaces.setEnabled(isEnabled && !isRegEx);
	}

	/**
	 * Create the panel.
	 */
	public QueryPanel() {
		setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		add(panel, BorderLayout.SOUTH);
		panel.setLayout(new GridLayout(2, 2, 0, 0));
		chckbxRegularExpression.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				selectCheckBoxes();
			}
		});
		panel.add(chckbxRegularExpression);

		panel.add(chckbxCaseSensitive);

		panel.add(chckbxIgnoreWhiteSpaces);
		chckbxIgnoreWhiteSpaces.setSelected(true);

		chckbxEnable.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				selectCheckBoxes();
			}
		});
		panel.add(chckbxEnable);
		chckbxEnable.setSelected(true);

		JScrollPane scrollPanel = new JScrollPane();
		scrollPanel.setViewportView(txtQuery);
		add(scrollPanel, BorderLayout.CENTER);
		txtQuery.setColumns(10);
		txtQuery.setRows(5);

	}

	@Override
	public String[] getQueries() {
		boolean isEnabled = chckbxEnable.isSelected();
		if (!isEnabled) {
			return null;
		}

		boolean isRegEx = chckbxRegularExpression.isSelected();
		boolean isCaseSensitive = chckbxCaseSensitive.isSelected();
		boolean isWhiteSpaceToIgnore = chckbxIgnoreWhiteSpaces.isSelected();

		String[] queries = txtQuery.getText().split("\n");
		for (int i = 0; i < queries.length; i++) {
			if (isRegEx && queries[i].trim().length() > 0) {
				queries[i] = "regex:" + queries[i];
			} else {
				// Remember for query reason the icase cannot be located before
				// of iwhite token
				if (!isCaseSensitive && queries[i].trim().length() > 0)
					queries[i] = "icase:" + queries[i];

				if (isWhiteSpaceToIgnore && queries[i].trim().length() > 0)
					queries[i] = "iwhite:" + queries[i];

			}
		}

		return queries;
	}

}
