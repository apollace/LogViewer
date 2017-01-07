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
package org.polly.log.viewer.engine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.polly.log.viewer.matcher.LogMatcher;
import org.polly.log.viewer.matcher.SimpleMatcher;

public class FileSearchEngine implements SearchEngine {
	// This constant is used for memory reason to prevent an abnormal memory
	// usage
	final static int MAX_NUM_OF_RESULT = 2000;

	private Callback callback;
	private String[] mustContainQueries;
	private String[] ignoreQueries;
	private File[] sources;

	int offset;
	int startDateFormatLen;
	SimpleDateFormat sdf;
	Date startTime;
	boolean isTimeAdviseSent = false;

	private LogMatcher matcher = new SimpleMatcher();

	private boolean checkTime(String log) {
		if (sdf == null) {
			return true;
		}

		if (log.length() < offset + startDateFormatLen) {
			return false;
		}

		String logDateString = log.substring(offset, offset
				+ startDateFormatLen);

		Date logDate;
		try {
			logDate = sdf.parse(logDateString);
		} catch (ParseException e) {
			if (!isTimeAdviseSent) {
				System.out
						.println("Please check your log, it sems without time but it is expected ["
								+ log + "]");
				isTimeAdviseSent = true;
			}
			return false;
		}

		long logTime = logDate.getTime();
		long startTime = this.startTime.getTime();
		return startTime <= logTime;
	}

	private void fileSearch(File file) throws IOException {
		FileReader fr = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(fr);

		String line;
		int numberOfCallback = 0;
		while ((line = bufferedReader.readLine()) != null
				&& numberOfCallback < MAX_NUM_OF_RESULT) {

			if (!checkTime(line)) {
				continue;
			}

			if (mustContainQueries != null && mustContainQueries.length > 0) {
				boolean hasLineMatched = false;
				for (String query : mustContainQueries) {
					if (matcher.match(query, line)) {
						hasLineMatched = true;
						break;
					}
				}
				if (!hasLineMatched) {
					continue;
				}
			}

			boolean isLineToIgnore = false;
			if (ignoreQueries != null)
				for (String query : ignoreQueries) {
					if (matcher.match(query, line)) {
						isLineToIgnore = true;
						break;
					}
				}

			if (isLineToIgnore) {
				continue;
			}

			numberOfCallback++;
			callback.callback(line);
		}

		bufferedReader.close();
	}

	@Override
	public void setCallback(Callback callback) {
		this.callback = callback;
	}

	@Override
	public void setMustContain(String[] queries) {
		mustContainQueries = queries;
	}

	@Override
	public void setIgnore(String[] queries) {
		ignoreQueries = queries;
	}

	@Override
	public void setSource(String sourceLink) {
		String files[] = sourceLink.split("\\$;\\$");
		sources = new File[files.length];

		for (int i = 0; i < files.length; i++) {
			sources[i] = new File(files[i]);
		}
	}

	@Override
	public void setStartTime(int offset, String format, String startTime) {
		sdf = new SimpleDateFormat(format);
		try {
			this.startTime = sdf.parse(startTime);
		} catch (ParseException e) {
			System.out.println("Please check your start time format [" + format
					+ "] and start time [" + startTime + "]");
			startDateFormatLen = 0;
			sdf = null;
			return;
		}

		this.offset = offset;
		startDateFormatLen = format.length();
	}

	@Override
	public void search() {
		new Thread() {
			public void run() {
				for (File source : sources) {
					if (!source.exists()) {
						return;
					}

					try {
						if (source.isFile()) {
							fileSearch(source);
						} else {
							for (File file : source.listFiles()) {
								if (file.isFile()) {
									fileSearch(file);
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						callback.searchIsFinish();
					}
				}
			}
		}.start();
	}

	@Override
	public double getSearchProgress() {
		return 0.0;
	}

}
