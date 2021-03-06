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
package org.owasp.jbrofuzz.fuzz.io;

import java.io.File;

import org.owasp.jbrofuzz.JBroFuzz;
import org.owasp.jbrofuzz.ui.JBroFuzzWindow;
import org.owasp.jbrofuzz.version.JBroFuzzPrefs;


/**
 * <p>
 * Class responsible for saving a fuzzing session based on the file already
 * opened.
 * </p>
 * <p>
 * If no file is identified, a file save-as is performed.
 * </p>
 * 
 * @author daemonmidi@gmail.com, subere@uncon.org
 * @version 2.5
 * @since 1.2
 */
public class SaveSession {

	public SaveSession(final JBroFuzzWindow mWindow) throws Exception {

		// Set the fuzzing tab as the one showing
		mWindow.setTabShow(JBroFuzzWindow.ID_PANEL_FUZZING);
		if (JBroFuzz.PREFS.get(JBroFuzzPrefs.DBSETTINGS[11].getId(), "").toLowerCase().trim().equals("couchdb")){
			// DTOCreator dtoC = new DTOCreator();
		//	CouchDBHandler couchHandler = new CouchDBHandler();
			if (JBroFuzz.PREFS.get(JBroFuzzPrefs.DBSETTINGS[12].getId(), "").length() <= 0  || JBroFuzz.PREFS.get(JBroFuzzPrefs.DBSETTINGS[12].getId(), "").equals("")){
				
				/*
				//start from scratch
				String dbName = "";
				String documentId = "";
				Date dat = new Date();
				long sessionid = dat.getTime();
				SessionDTO session = dtoC.createSessionDTO(mWindow, sessionid);
				CouchDBMapper cdbMapper = new CouchDBMapper();
				JSONObject document = cdbMapper.toCouch(session);
				String dbNameReal = couchHandler.createDB(dbName);
				couchHandler.createOrUpdateDocument(dbNameReal, documentId, document);
				*/
			}
			else if (JBroFuzz.PREFS.get(JBroFuzzPrefs.DBSETTINGS[12].getId(), "").length() > 0 && !JBroFuzz.PREFS.get(JBroFuzzPrefs.DBSETTINGS[12].getId(), "").equals("")){
				//use provided dbName
				/*
				Date dat = new Date();
				String documentId = dat.getYear() + "_" + dat.getMonth() + "_" + dat.getDay() + "_" + dat.getHours() + ":" + dat.getMinutes();
				long sessionid = dat.getTime();
				SessionDTO session = dtoC.createSessionDTO(mWindow, sessionid);
				CouchDBMapper cdbMapper = new CouchDBMapper();
				JSONObject document = cdbMapper.toCouch(session);
				couchHandler.createOrUpdateDocument(JBroFuzz.PREFS.get(JBroFuzzPrefs.DBSETTINGS[12].getId(), ""), documentId, document);
				*/
			}
			else{
				throw new Exception("No DB Name provided");
			}
		}
		else if(JBroFuzz.PREFS.get(JBroFuzzPrefs.DBSETTINGS[11].getId(), "").toLowerCase().trim().equals("sqlite")){
			// write to SQLite in case somebody did sessin to file and now decides to go sqlite for whatever reason
			/*
			DTOCreator dtoC = new DTOCreator();
			SQLiteHandler sqlH = new SQLiteHandler();
			sqlH.setUpDB();
			String dbName = JBroFuzz.PREFS.get(JBroFuzzPrefs.DBSETTINGS[12].getId(), "");
			if (dbName.length() == 0 || dbName.equals("")){
				Date dat = new Date();
				dbName = String.valueOf(dat.getTime());
			}
			Connection con = sqlH.getConnection(dbName);
			SessionDTO session = dtoC.createSessionDTO(mWindow, -1);
			sqlH.store(session, con);
			*/
		}
		else {
			// If there is a file already opened, save there
			if (mWindow.isCurrentFileOpened()) {

				final File myFile = mWindow.getCurrentFileOpened();
				Save.writeFile(myFile, mWindow);
				mWindow.setOpenFileTo(myFile);

				final String parentDir = myFile.getParent();
				if (parentDir != null) {
					JBroFuzz.PREFS
							.put(JBroFuzzPrefs.DIRS[2].getId(), parentDir);
				}

			} else {
				// If no file is open, create a new 'Save As' session
				new SaveAsSession(mWindow);

			}	
		}
	}
}