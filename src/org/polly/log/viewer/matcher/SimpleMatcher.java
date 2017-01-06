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
 * This is a very simple implementation of {@link LogMatcher}. This class verify
 * if the given query is contained into the log matcher.
 * 
 * @author Alessandro Pollace
 * 
 */
public class SimpleMatcher implements LogMatcher {

	final static String regex_token = "regex:";
	final static String case_insensitive_token = "icase:";
	final static String ignore_white_token = "iwhite:";

	@Override
	public boolean match(String query, String log) {
		if (query.trim().length() <= 0) {
			return false;
		}

		if (query.startsWith(regex_token)) {
			String regex = "^.*" + query.substring(regex_token.length()) + ".*$";
			return log.matches(regex);
		}

		if (query.startsWith(ignore_white_token)) {
			log = log.replaceAll("\\s", "");
			query = query.substring(ignore_white_token.length());
			query = query.replaceAll("\\s", "");
		}

		if (query.startsWith(case_insensitive_token)) {
			log = log.toUpperCase();
			query = query.substring(case_insensitive_token.length());
			query = query.toUpperCase();
		}

		return log.contains(query);
	}

}
