import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Config {
	private String ServerURL = "";
    private int TargetRPS = 0;
    private String AuthKey = "";
    private String UserName = "";
	
    // Obtains the file from Program main and parses it into JSON object
	public Config (String config) {
		JSONParser jsonParser = new JSONParser();
		
		try (FileReader reader = new FileReader("config.json")) {
			Object obj = jsonParser.parse(reader);
			JSONObject jo = (JSONObject) obj;
			
			parseArguments(jo);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} 
	}
	
	// Parses the arguments in JSON and assigns them respectively 
	public void parseArguments(JSONObject jo) {
		validateServer(jo, "ServerURL");
        validateTarget(jo, "TargetRPS");
        validateAuth(jo, "AuthKey");
        validateUser(jo,"UserName");
	}
	
	public void validateServer(JSONObject jo, String obj) {
		if(jo.get(obj) != null && jo.get(obj) != "") {
			this.ServerURL = (String) jo.get(obj);
		}
	}
	
	public void validateTarget(JSONObject jo, String obj) {
		if(jo.get(obj) != "0") {
			this.TargetRPS = Integer.parseInt((String) jo.get(obj));
		}
	}
	
	public void validateAuth(JSONObject jo, String obj) {
		if(jo.get(obj) != null && jo.get(obj) != "") {
			this.AuthKey = (String) jo.get(obj);
		}
	}
	
	public void validateUser(JSONObject jo, String obj) {
		{
			this.UserName = (String) jo.get(obj);
		}
	}
	
	// Verifies that the configuration is correct
	// and all variables are assigned correctly 
	public boolean verifyConfig() {
		boolean isGood = true;
		
		if(this.AuthKey == "" || this.ServerURL == "" || this.TargetRPS == 0 || this.UserName == "") {
			isGood = false;
		}
		
		return isGood;
	}
	
	public String getServerURL() {
		return this.ServerURL;
	}
	
	public String getKey() {
		return this.AuthKey;
	}
	
	public String getUserName() {
		return this.UserName;
	}
	
	public int getTargetRPS() {
		return this.TargetRPS;
	}
}
