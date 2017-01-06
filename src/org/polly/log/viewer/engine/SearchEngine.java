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

import java.text.SimpleDateFormat;

/**
 * This interface is used to build a log search engine. A log search engine can
 * work using the same thread of the caller or another one, the callback can be
 * called at any type after the search start.
 * 
 * @author Alessandro Pollace
 */
public interface SearchEngine {

	/**
	 * The callback used to retrieve the data.
	 * 
	 * @author Alessandro Pollace
	 */
	public interface Callback {

		/**
		 * This method is called each time that the instance of
		 * {@link SearchEngine} found something that can match with the given
		 * configuration
		 * 
		 * @param string
		 *            The log that matches with the criteria
		 */
		public void callback(String string);

		/**
		 * This method is called as a signal when the search is finish
		 */
		public void searchIsFinish();
	}

	/**
	 * Set the callback used to return the data found
	 * 
	 * @param callback
	 *            the instance of {@link Callback}
	 */
	public void setCallback(Callback callback);

	/**
	 * Set the queries that must match. all queries are in or mode this means
	 * that if a there are three queries and a log match with only one of them
	 * the callback will be called.
	 * 
	 * @param queries
	 *            the array of queries
	 */
	public void setMustContain(String[] queries);

	/**
	 * Set the queries that must ignored. all queries are in or mode this means
	 * that if a there are three queries and a log match with only one of them
	 * the callback will be ignored even when a must contain query matches.
	 * 
	 * @param queries
	 *            the array of queries
	 */
	public void setIgnore(String[] queries);

	/**
	 * Set the source link. The source link will be different in relation to the
	 * {@link SearchEngine} implementation.
	 * 
	 * @param sourceLink
	 *            The data source link
	 */
	public void setSource(String sourceLink);

	/**
	 * This method allow to set the start time, each log that has a time smaller
	 * than the start time will be ignored.
	 * 
	 * @param offset
	 *            the offset expressed in number of characters to reach the time
	 *            in the log file
	 * @param format
	 *            the format used to parse the time, the format must respect the
	 *            format expected by the {@link SimpleDateFormat}
	 * @param startTime
	 *            a string representation of start time
	 */
	public void setStartTime(int offset, String format, String startTime);

	/**
	 * This method start the search. The search may be performed using a
	 * different thread. To verify the search progress use methods
	 * isSearchFinish and getSearchProgress.
	 */
	public void search();

	/**
	 * This method return the search advancement percentage when it is
	 * available, in all other case it will return 0
	 * 
	 * @return the search advancement percentage
	 */
	public double getSearchProgress();

}
