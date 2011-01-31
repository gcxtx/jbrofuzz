package org.owasp.jbrofuzz.io;

import java.io.File;

import org.owasp.jbrofuzz.JBroFuzz;
import org.owasp.jbrofuzz.db.DBAdaptor;
import org.owasp.jbrofuzz.db.DBAdaptorFactory;
import org.owasp.jbrofuzz.fuzz.MessageContainer;
import org.owasp.jbrofuzz.system.Logger;
import org.owasp.jbrofuzz.ui.JBroFuzzWindow;
import org.owasp.jbrofuzz.version.JBroFuzzPrefs;

public class StorageHandler implements StorageInterface {

	private FileHandler mFileHandler;
	private DBAdaptor mdbAdaptor;
		
	public StorageHandler() {
	
		final String dbType = JBroFuzz.PREFS.get(JBroFuzzPrefs.DBSETTINGS[11].getId(), "-1");
		
		if(dbType.equals("SQLite") || dbType.equals("CouchDB")){
			mdbAdaptor =  DBAdaptorFactory.getInstance();
			}
		else /*(dbType.equals("None") ) */{
			mFileHandler = new FileHandler();
		}
	}
	
	@Override
	public void createNewLocation() {
		// TODO Auto-generated method stub
		if(mFileHandler != null) {
			mFileHandler.createNewLocation();
		}

	}

	@Override
	public String getLocationCanonicalPath() {
		String canonicalPath = "";
		if (mFileHandler != null){
			canonicalPath =  mFileHandler.getLocationCanonicalPath() ;
		}
		return canonicalPath;
	}

	@Override
	public String getFuzzURIString(String fileName) {
		// TODO Auto-generated method stub
		String fuzzerURI = "";
		if (mFileHandler != null){
			fuzzerURI =  mFileHandler.getFuzzURIString(fileName);
		}
		return fuzzerURI;
	}

	@Override
	public String getLocationURIString() {
		String locationURI = "";
		if (mFileHandler != null){
			locationURI = mFileHandler.getLocationURIString();
		}
		return locationURI;
	}

	@Override
	public void writeFuzzFile(MessageContainer outputMessage) {
		if (mFileHandler != null) {
			mFileHandler.writeFuzzFile(outputMessage) ;
		}
		if (mdbAdaptor != null){
			mdbAdaptor.store(outputMessage);
		}
	}
	
	
	@Override
	public MessageContainer readFuzzFile(String name, String sessionId, JBroFuzzWindow mWindow){
		if (mFileHandler != null){
		//TODO read from file
			Logger.log("Reading from file not implemented yet.", 3);
		}
		if (mdbAdaptor != null){
			return mdbAdaptor.read(name, sessionId, mWindow);
		}
		return null;
	}

	@Override
	public String[] readTableRow(String sqlStatement) {
		if (mFileHandler != null){
			Logger.log("reading form file not implemented", 3);
		}
		if (mdbAdaptor != null){
			return mdbAdaptor.executeQuery(sqlStatement);
		}
		return null;
	}
}