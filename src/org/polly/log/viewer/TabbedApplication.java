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

import java.awt.Component;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;

import java.awt.BorderLayout;

import org.polly.log.viewer.engine.FileSearchEngine;
import org.polly.log.viewer.engine.SearchEngine;
import org.polly.log.viewer.engine.SearchEngine.Callback;
import org.polly.widget.InfoConsole;
import org.polly.widget.TextField;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;

import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.JLabel;

import java.awt.Insets;

import javax.swing.JButton;

public class TabbedApplication {
	// static ErrorConsole errorConsole = new ErrorConsole();
	static InfoConsole infoConsole = new InfoConsole();

	private JFrame frame;
	private int tabCount = 0;
	private JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
	private TextField txtFolder;

	private JMenuItem mntmScan = new JMenuItem("Scan");
	private JButton button = new JButton("Scan");

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TabbedApplication window = new TabbedApplication();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void scan() {
		if (tabbedPane.getSelectedIndex() == -1) {
			return;
		}

		Component cmp = tabbedPane.getComponent(tabbedPane.getSelectedIndex());
		if (!(cmp instanceof LogViewerPanel)) {
			return;
		}

		final LogViewerPanel lvp = (LogViewerPanel) cmp;

		SearchEngine se = new FileSearchEngine();
		if (txtFolder.getText().trim().length() == 0) {
			return;
		}
		se.setSource(txtFolder.getText());

		String[] mustQueries = lvp.getMustContain();
		if (mustQueries.length > 0 && mustQueries[0].trim().length() > 0) {
			se.setMustContain(mustQueries);
		}

		String[] ignoreQueries = lvp.getIgnore();
		if (ignoreQueries.length > 0 && ignoreQueries[0].trim().length() > 0) {
			se.setIgnore(ignoreQueries);
		}

		int offset = lvp.getTimeOffset();
		String format = lvp.getTimeFormat();
		String time = lvp.getStartTime();
		if (format.length() > 0 && time.length() > 0) {
			se.setStartTime(offset, format, time);
		}

		button.setEnabled(false);
		mntmScan.setEnabled(false);
		se.setCallback(new Callback() {
			Callback cb = lvp.getCallback();

			@Override
			public void searchIsFinish() {
				cb.searchIsFinish();
				button.setEnabled(true);
				mntmScan.setEnabled(true);
			}

			@Override
			public void callback(String string) {
				cb.callback(string);
			}
		});
		se.search();
	}

	/**
	 * Create the application.
	 */
	public TabbedApplication() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("LogViewer");
		frame.setBounds(100, 100, 900, 650);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		addPanel();

		frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);

		JToolBar toolBar = new JToolBar();
		frame.getContentPane().add(toolBar, BorderLayout.NORTH);

		JLabel label = new JLabel("Log folder");
		toolBar.add(label);

		txtFolder = new TextField();
		txtFolder.setMargin(new Insets(7, 7, 7, 7));
		txtFolder.setColumns(10);
		toolBar.add(txtFolder);

		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				scan();
			}
		});
		toolBar.add(button);

		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		mntmScan.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				scan();
			}
		});
		mntmScan.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
		mnFile.add(mntmScan);

		JSeparator separator = new JSeparator();
		mnFile.add(separator);

		JMenuItem mntmAddPane = new JMenuItem("Add pane");
		mntmAddPane.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addPanel();
			}
		});
		mntmAddPane.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
				InputEvent.CTRL_MASK));
		mnFile.add(mntmAddPane);

		JMenuItem mntmClosePane = new JMenuItem("Close pane");
		mntmClosePane.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removePanel();
			}
		});
		mntmClosePane.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W,
				InputEvent.CTRL_MASK));
		mnFile.add(mntmClosePane);

		JMenuItem mntmHighlightCurrentLine = new JMenuItem(
				"Highlight current line");
		mntmHighlightCurrentLine.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (tabbedPane.getSelectedIndex() == -1) {
					return;
				}

				Component cmp = tabbedPane.getComponent(tabbedPane
						.getSelectedIndex());
				if (!(cmp instanceof LogViewerPanel)) {
					return;
				}

				final LogViewerPanel lvp = (LogViewerPanel) cmp;
				lvp.highlightCurrentLine(frame.getGraphics());
			}
		});
		
		JSeparator separator_1 = new JSeparator();
		mnFile.add(separator_1);
		
		JMenuItem mntmSearchNextIn = new JMenuItem("Search next in log viewer");
		mntmSearchNextIn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (tabbedPane.getSelectedIndex() == -1) {
					return;
				}

				Component cmp = tabbedPane.getComponent(tabbedPane
						.getSelectedIndex());
				if (!(cmp instanceof LogViewerPanel)) {
					return;
				}

				final LogViewerPanel lvp = (LogViewerPanel) cmp;
				lvp.search();
			}
		});
		mntmSearchNextIn.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));
		mnFile.add(mntmSearchNextIn);
		
		JSeparator separator_2 = new JSeparator();
		mnFile.add(separator_2);
		mntmHighlightCurrentLine.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, InputEvent.CTRL_MASK));
		mnFile.add(mntmHighlightCurrentLine);

		JMenuItem mntmRemoveLineHighlights = new JMenuItem(
				"Remove line highlights");
		mntmRemoveLineHighlights.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (tabbedPane.getSelectedIndex() == -1) {
					return;
				}

				Component cmp = tabbedPane.getComponent(tabbedPane
						.getSelectedIndex());
				if (!(cmp instanceof LogViewerPanel)) {
					return;
				}

				final LogViewerPanel lvp = (LogViewerPanel) cmp;
				lvp.resetHighlightedLines();
			}
		});
		mntmRemoveLineHighlights.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_ESCAPE, 0));
		mnFile.add(mntmRemoveLineHighlights);
		
		JSeparator separator_3 = new JSeparator();
		mnFile.add(separator_3);
		
		JMenuItem mntmTaskManager = new JMenuItem("Task Manager");
		mntmTaskManager.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				TaskManager taskMan = new TaskManager();
				taskMan.setVisible(true);
			}
		});
		mnFile.add(mntmTaskManager);
	}

	private void addPanel() {
		Component comp = new LogViewerPanel();
		tabbedPane.addTab("Log " + ++tabCount, comp);
		tabbedPane.setSelectedComponent(comp);
	}

	private void removePanel() {
		if (tabbedPane.getSelectedIndex() != -1) {
			tabbedPane.remove(tabbedPane.getSelectedIndex());
		}
	}

}
