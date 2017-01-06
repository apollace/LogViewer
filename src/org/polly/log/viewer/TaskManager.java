package org.polly.log.viewer;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.management.timer.TimerMBean;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Window.Type;
import java.text.NumberFormat;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class TaskManager extends JFrame {
	private static final long serialVersionUID = -8170772416115919357L;
	private JPanel contentPane;
	private static JTextArea textArea = new JTextArea();

	class RunCheck extends TimerTask {

		@Override
		public void run() {
			Runtime runtime = Runtime.getRuntime();
			NumberFormat format = NumberFormat.getInstance();

			StringBuilder sb = new StringBuilder();
			long maxMemory = runtime.maxMemory();
			long allocatedMemory = runtime.totalMemory();
			long freeMemory = runtime.freeMemory();

			sb.append("free memory MB: " + format.format(freeMemory / 1024 / 1024) + " ");
			sb.append("allocated memory MB: " + format.format(allocatedMemory / 1024 / 1024) + " ");
			sb.append("max memory MB: " + format.format(maxMemory / 1024 / 1024) + " ");
			sb.append(
					"total free memory MB: " + format.format((freeMemory + (maxMemory - allocatedMemory)) / 1024 / 1024) + "\n");

			textArea.append(sb.toString());
		}
	}
	
	RunCheck task = new RunCheck();

	/**
	 * Create the frame.
	 */
	public TaskManager() {
		setAlwaysOnTop(true);
		setTitle("Task manager");
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		contentPane.add(textArea, BorderLayout.CENTER);
		textArea.setEditable(false);

		Timer t = new Timer();
		t.schedule(task, 5000, 5000);
	}

}
