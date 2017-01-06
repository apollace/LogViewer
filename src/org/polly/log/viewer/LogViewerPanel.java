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

import javax.swing.JScrollPane;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.polly.log.viewer.engine.SearchEngine.Callback;
import org.polly.widget.HighlightPanel;
import org.polly.widget.QueryPanel;
import org.polly.widget.SplitPane;
import org.polly.widget.TextField;
import org.polly.widget.WidgetContainer;

import javax.swing.border.TitledBorder;
import javax.swing.text.Caret;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.awt.Dimension;
import java.awt.GridLayout;

public class LogViewerPanel extends JPanel {
	private static final long serialVersionUID = 5148533260083281584L;

	private List<LinePainter> lineHighligters = new ArrayList<LinePainter>();

	private JTextArea txtrLogview = new JTextArea() {
		private static final long serialVersionUID = 1L;

		public void setText(String t) {
			resetHighlightedLines();
			super.setText(t);
		};
	};

	private WidgetContainer<QueryPanel> queriesContainer = new WidgetContainer<QueryPanel>(
			new WidgetContainer.WidgetFactory() {
				@Override
				public JPanel getWidgetPanel() {
					return new QueryPanel();
				}
			}, "query", "Select each line that matches with at least one of the follow queries");

	private WidgetContainer<QueryPanel> ignoreQueriesContainer = new WidgetContainer<QueryPanel>(
			new WidgetContainer.WidgetFactory() {
				@Override
				public JPanel getWidgetPanel() {
					return new QueryPanel();
				}
			}, "ignore",
			"Ignore each line that matches with at least one of the follow queries, ignore wins over matches");

	private static int highlighterCounter = 0;
	private WidgetContainer<HighlightPanel> highlightContainer = new WidgetContainer<HighlightPanel>(
			new WidgetContainer.WidgetFactory() {
				@Override
				public JPanel getWidgetPanel() {
					HighlightPanel highlightPanel = new HighlightPanel();
					highlightPanel.setHighlightCallback(new HighlightPanel.HighlightCallback() {
						private String highlightKey = String.valueOf(highlighterCounter++);

						@Override
						public void highlight(Color color, String[] queries) {
							occurrenceHighlighter.setHighlightByColor(highlightKey, color, queries);
							occurrenceHighlighter.refresh();
						}
					});
					return highlightPanel;
				}
			}, "highlight", "Highlight using the selected color each occurrence of given strings");

	private Callback cb = new Callback() {
		Semaphore lock = new Semaphore(1);
		int bufferSize = 0;
		StringBuffer sb = new StringBuffer();

		long MAX_WAIT = 3000;
		Date lastUpdate = new Date();

		private void flush() {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					StringBuffer sbLocal = null;
					try {
						lock.acquire();
						sbLocal = sb;
						sb = new StringBuffer();
						lock.release();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					Caret c = txtrLogview.getCaret();
					txtrLogview.append(sbLocal.toString());
					txtrLogview.setCaret(c);
					occurrenceHighlighter.refresh();
				}
			});
		}

		@Override
		public void callback(String string) {
			sb.append(string).append('\n');
			bufferSize += 1;
			bufferSize = bufferSize % 10;

			Date now = new Date();
			long diff = now.getTime() - lastUpdate.getTime();
			if (bufferSize == 0 || diff > MAX_WAIT) {
				flush();
				lastUpdate = now;
			}
		}

		@Override
		public void searchIsFinish() {
			flush();
		}
	};

	private LogPaneHighlighter occurrenceHighlighter = new LogPaneHighlighter(txtrLogview);
	private TextField txtTimeOffset;
	private TextField txtTimeFormat;
	private TextField txtStarttime;
	private TextField txtSearch;

	/**
	 * Create the application.
	 */
	public LogViewerPanel() {
		setPreferredSize(new Dimension(800, 700));
		initialize();
	}

	public Callback getCallback() {
		txtrLogview.setText("");
		return cb;
	}

	public String getTimeFormat() {
		String format = "";
		if (txtTimeFormat.getText().trim().length() > 0) {
			format = txtTimeFormat.getText().trim();
		}

		return format;
	}

	public String getStartTime() {
		String time = "";
		if (txtStarttime.getText().trim().length() > 0) {
			time = txtStarttime.getText().trim();
		}

		return time;
	}

	public int getTimeOffset() {
		int offset = 0;
		if (txtTimeOffset.getText().trim().length() > 0) {
			offset = Integer.valueOf(txtTimeOffset.getText());
		}

		return offset;
	}

	public String[] getIgnore() {
		List<String> list = new LinkedList<String>();
		for (QueryPanel p : ignoreQueriesContainer.getWidgets()) {
			String[] queries = p.getQueries();
			if (queries == null)
				continue;

			for (String q : queries) {
				list.add(q);
			}
		}

		String[] result = new String[list.size()];
		return list.toArray(result);
	}

	public String[] getMustContain() {
		List<String> list = new LinkedList<String>();
		for (QueryPanel p : queriesContainer.getWidgets()) {
			String[] queries = p.getQueries();
			if (queries == null)
				continue;

			for (String q : queries) {
				list.add(q);
			}
		}

		String[] result = new String[list.size()];
		return list.toArray(result);
	}

	public void search() {
		String lookingFor = txtSearch.getText();
		int index = txtrLogview.getText().indexOf(lookingFor, txtrLogview.getCaretPosition());
		txtrLogview.requestFocus();
		if (index >= 0) {
			txtrLogview.select(index, index + lookingFor.length());
		} else {
			index = txtrLogview.getText().indexOf(lookingFor);
			if (index >= 0) {
				txtrLogview.select(index, index + lookingFor.length());
			}
		}

		txtrLogview.repaint();
	}

	public void highlightCurrentLine(Graphics g) {
		lineHighligters.add(new LinePainter(txtrLogview, Color.yellow, txtrLogview.getCaretPosition()));
	}

	public void resetHighlightedLines() {
		for (LinePainter lp : lineHighligters) {
			lp.stopHighlight();
			lp.reset();
		}

		lineHighligters.clear();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		setLayout(new BorderLayout(0, 0));

		final SplitPane splitPane = new SplitPane();
		splitPane.setDividerSize(24);
		splitPane.setResizeWeight(0.7);
		splitPane.setOneTouchExpandable(true);
		add(splitPane);

		JScrollPane contentPanel = new JScrollPane();
		splitPane.setLeftComponent(contentPanel);
		txtrLogview.setEditable(false);
		txtrLogview.setFont(new Font("Monospaced", Font.PLAIN, 12));
		txtrLogview.setTabSize(4);
		contentPanel.setViewportView(txtrLogview);

		JScrollPane optionsScrollPane = new JScrollPane();
		splitPane.setRightComponent(optionsScrollPane);

		final JPanel optionsPane = new JPanel();
		optionsScrollPane.setViewportView(optionsPane);
		splitPane.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				optionsPane.revalidate();
			}
		});
		GridBagLayout gbl_optionsPane = new GridBagLayout();
		gbl_optionsPane.columnWidths = new int[] { 0, 0 };
		gbl_optionsPane.rowHeights = new int[] { 0, 0, 0, 0, 0, 0 };
		gbl_optionsPane.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
		gbl_optionsPane.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		optionsPane.setLayout(gbl_optionsPane);

		JPanel mustContainPane = new JPanel();
		mustContainPane.setLayout(new GridLayout(0, 1, 0, 0));

		mustContainPane.add(queriesContainer);
		GridBagConstraints gbc_mustContainPane = new GridBagConstraints();
		gbc_mustContainPane.fill = GridBagConstraints.BOTH;
		gbc_mustContainPane.insets = new Insets(0, 0, 5, 0);
		gbc_mustContainPane.gridx = 0;
		gbc_mustContainPane.gridy = 0;
		optionsPane.add(mustContainPane, gbc_mustContainPane);

		JPanel ignorePane = new JPanel();
		ignorePane.add(ignoreQueriesContainer);
		GridBagConstraints gbc_ignorePane = new GridBagConstraints();
		gbc_ignorePane.fill = GridBagConstraints.BOTH;
		gbc_ignorePane.insets = new Insets(0, 0, 5, 0);
		gbc_ignorePane.gridx = 0;
		gbc_ignorePane.gridy = 1;
		optionsPane.add(ignorePane, gbc_ignorePane);

		JPanel highlight2Pane = new JPanel();
		GridBagConstraints gbc_highlight2Pane = new GridBagConstraints();
		gbc_highlight2Pane.fill = GridBagConstraints.BOTH;
		gbc_highlight2Pane.insets = new Insets(0, 0, 5, 0);
		gbc_highlight2Pane.gridx = 0;
		gbc_highlight2Pane.gridy = 2;
		optionsPane.add(highlight2Pane, gbc_highlight2Pane);
		highlight2Pane.setLayout(new BorderLayout(0, 0));
		highlight2Pane.add(highlightContainer);

		JPanel searchPane = new JPanel();
		searchPane.setBorder(new TitledBorder(null, "Search", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_searchPane = new GridBagConstraints();
		gbc_searchPane.fill = GridBagConstraints.BOTH;
		gbc_searchPane.insets = new Insets(0, 0, 5, 0);
		gbc_searchPane.gridx = 0;
		gbc_searchPane.gridy = 3;
		optionsPane.add(searchPane, gbc_searchPane);
		searchPane.setLayout(new GridLayout(0, 1, 0, 0));

		txtSearch = new TextField();
		searchPane.add(txtSearch);
		txtSearch.setColumns(10);

		JPanel timePanel = new JPanel();
		timePanel.setBorder(new TitledBorder(null, "Time", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_timePanel = new GridBagConstraints();
		gbc_timePanel.fill = GridBagConstraints.BOTH;
		gbc_timePanel.gridx = 0;
		gbc_timePanel.gridy = 4;
		optionsPane.add(timePanel, gbc_timePanel);
		GridBagLayout gbl_timePanel = new GridBagLayout();
		gbl_timePanel.columnWidths = new int[] { 0, 0, 0 };
		gbl_timePanel.rowHeights = new int[] { 0, 0, 0, 0, 0 };
		gbl_timePanel.columnWeights = new double[] { 1.0, 1.0, Double.MIN_VALUE };
		gbl_timePanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		timePanel.setLayout(gbl_timePanel);

		JLabel lblNewLabel = new JLabel("Time offset");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		timePanel.add(lblNewLabel, gbc_lblNewLabel);

		JLabel lblTimeFormat = new JLabel("Time format");
		GridBagConstraints gbc_lblTimeFormat = new GridBagConstraints();
		gbc_lblTimeFormat.insets = new Insets(0, 0, 5, 0);
		gbc_lblTimeFormat.gridx = 1;
		gbc_lblTimeFormat.gridy = 0;
		timePanel.add(lblTimeFormat, gbc_lblTimeFormat);

		txtTimeOffset = new TextField();
		txtTimeOffset.setText("0");
		GridBagConstraints gbc_txtTimeOffset = new GridBagConstraints();
		gbc_txtTimeOffset.insets = new Insets(0, 0, 5, 5);
		gbc_txtTimeOffset.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtTimeOffset.gridx = 0;
		gbc_txtTimeOffset.gridy = 1;
		timePanel.add(txtTimeOffset, gbc_txtTimeOffset);
		txtTimeOffset.setColumns(10);

		txtTimeFormat = new TextField();
		txtTimeFormat.setText("HH:mm:ss.SSS");
		GridBagConstraints gbc_txtTimeFormat = new GridBagConstraints();
		gbc_txtTimeFormat.insets = new Insets(0, 0, 5, 0);
		gbc_txtTimeFormat.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtTimeFormat.gridx = 1;
		gbc_txtTimeFormat.gridy = 1;
		timePanel.add(txtTimeFormat, gbc_txtTimeFormat);
		txtTimeFormat.setColumns(10);

		JLabel lblStartTime = new JLabel("Start time");
		GridBagConstraints gbc_lblStartTime = new GridBagConstraints();
		gbc_lblStartTime.gridwidth = 2;
		gbc_lblStartTime.insets = new Insets(0, 0, 5, 5);
		gbc_lblStartTime.gridx = 0;
		gbc_lblStartTime.gridy = 2;
		timePanel.add(lblStartTime, gbc_lblStartTime);

		txtStarttime = new TextField();
		txtStarttime.setText("");
		GridBagConstraints gbc_txtStarttime = new GridBagConstraints();
		gbc_txtStarttime.gridwidth = 2;
		gbc_txtStarttime.insets = new Insets(0, 0, 0, 5);
		gbc_txtStarttime.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtStarttime.gridx = 0;
		gbc_txtStarttime.gridy = 3;
		timePanel.add(txtStarttime, gbc_txtStarttime);
		txtStarttime.setColumns(10);
		splitPane.setDividerLocation(550);
	}
}
