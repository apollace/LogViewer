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
package org.polly.log.viewer.matcher;

/**
 * This interface is used to manage the log matchers. A log matcher implements
 * the logic required to understand when a log matches with a specific query and
 * when it doesn't match.
 * 
 * @author Alessandro Pollace
 * 
 */
public interface LogMatcher {
	/**
	 * This method verifies if the given query matches with the given log.
	 * 
	 * @param query
	 *            The query to verify
	 * @param log
	 *            The log to analyze
	 * 
	 * @return <code>true</code> if the query matches with the log,
	 *         <code>false</code> otherwise
	 */
	boolean match(String query, String log);
}
