/**
 * JbroFuzz 2.5
 *
 * JBroFuzz - A stateless network protocol fuzzer for web applications.
 * 
 * Copyright (C) 2007 - 2010 subere@uncon.org
 *
 * This file is part of JBroFuzz.
 * 
 * JBroFuzz is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * JBroFuzz is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with JBroFuzz.  If not, see <http://www.gnu.org/licenses/>.
 * Alternatively, write to the Free Software Foundation, Inc., 51 
 * Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * 
 * Verbatim copying and distribution of this entire program file is 
 * permitted in any medium without royalty provided this notice 
 * is preserved. 
 * 
 */
package org.owasp.jbrofuzz.graph.utils;

import java.io.File;


import org.owasp.jbrofuzz.JBroFuzz;
import org.owasp.jbrofuzz.graph.FileSystemTreeNode;
import org.owasp.jbrofuzz.graph.GraphingPanel;
import org.owasp.jbrofuzz.version.JBroFuzzPrefs;

public class JohnyWalker implements Walker {

	private FileSystemTreeNode master;

	private final File directory;

	private final GraphingPanel gPanel;
	
	private int fileCount, dirCount;

	public JohnyWalker(final GraphingPanel gPanel)  {

		// Get the directory location from preferences
		final boolean saveElsewhere = JBroFuzz.PREFS.getBoolean(JBroFuzzPrefs.DIRS[1].getId(), true);
		// Use the user directory if the box is not ticked, under: "Preferences"->"Directory Locations"
		final String dirString;
		if(saveElsewhere) {
			dirString = JBroFuzz.PREFS.get(JBroFuzzPrefs.DIRS[0].getId(), System.getProperty("user.dir"));
		} else {
			dirString = System.getProperty("user.dir");
		}		

		directory = new File(dirString + File.separator + "jbrofuzz"
				+ File.separator + "fuzz");
		this.gPanel = gPanel;

		if (directory.canRead()) {
			master = new FileSystemTreeNode(directory.getName());
			master.setAsDirectory();
		} else {
			this.gPanel.toConsole("Cannot read: " + directory.getPath());
		}

		fileCount = 0;
		dirCount = 0;

	}

	public FileSystemTreeNode getFileSystemTreeNode() {
		return master;
	}

	public int getMaximum() {

		return directory.listFiles().length;

	}
	
	private void listAllFiles(final File directory, final FileSystemTreeNode parent) {

		if (!directory.canRead()) {
			gPanel.toConsole("Could not read: " + directory.getPath());
			return;
		}

		if (gPanel.isStoppedEnabled()) {
			return;
		}

		dirCount++;

		final File[] children = directory.listFiles();

		for (final File f : children) {

			final FileSystemTreeNode node = new FileSystemTreeNode(f.getName());

			if (f.isDirectory()) {
				node.setAsDirectory();
				parent.add(node);
				dirCount++;
				listAllFiles(f, node);
			} else if (!f.isDirectory()) {
				parent.add(node);
				fileCount++;
			}
		}

	}

	public void run() {
		listAllFiles(directory, master);
		gPanel.toConsole("Total Files: " + fileCount);
		gPanel.toConsole("Total Directories: " + dirCount);

	}

}
