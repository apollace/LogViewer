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

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import javax.swing.JLabel;
import java.awt.Font;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ErrorConsole extends JDialog {
	private static final long serialVersionUID = 5054695404403791272L;

	private final JPanel contentPanel = new JPanel();

	private JTextArea txtrError = new JTextArea();
	private PrintStream ps;

	/**
	 * Create the dialog.
	 */
	public ErrorConsole() {
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		ps = new PrintStream(new OutputStream() {

			private StringBuilder message = new StringBuilder();

			@Override
			public void write(int b) throws IOException {
				message.append((char) b);
				txtrError.setText(message.toString());
				ErrorConsole.this.setVisible(true);
			}
		});
		System.setErr(ps);

		setTitle("Ops...");
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBackground(Color.WHITE);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JLabel lbls = new JLabel(":-S");
			lbls.setFont(new Font("Dialog", Font.BOLD, 32));
			contentPanel.add(lbls, BorderLayout.NORTH);
		}

		txtrError.setEditable(false);
		contentPanel.add(txtrError, BorderLayout.CENTER);
		{
			JTextArea txtrIAmVery = new JTextArea();
			txtrIAmVery.setEditable(false);
			txtrIAmVery.setLineWrap(true);
			txtrIAmVery.setText("I am very sorry for this problem, if you "
					+ "like this software please send me the error "
					+ "description, reported above this message, using "
					+ "github. My account is apollace, I'm waiting for "
					+ "your help :)");
			contentPanel.add(txtrIAmVery, BorderLayout.SOUTH);
		}

		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						ErrorConsole.this.setVisible(false);
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
	}

}
