package de.pt400c.defaultsettings;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;

public class PrivateJSON {
	
	public static transient final long serialVersionUID = 498123L;
	public HashMap<String, String> currentHash = new HashMap<String, String>();
	public String targetProfile = "!NEW!";
	public String currentProfile = "!NEW!";
	public String privateIdentifier = null;
	
	public void save(File persistentLocation) {
		try (FileWriter writer = new FileWriter(persistentLocation)) {
            FileUtil.gson.toJson(this, writer);
        } catch (IOException e) {
        	DefaultSettings.log.log(Level.SEVERE, "Exception at processing startup: ", e);
        }
	}
}