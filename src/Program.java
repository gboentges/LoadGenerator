import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URL;
import java.time.Instant;

public class Program extends Thread {
	public static int CurrentRPS = 0;
	public static String configFile;

	public static void main(String[] args) {
		configFile = "config.json";
		
		// Checks if arguments are given otherwise default to config.json
		if(args.length > 0) {
			configFile = args[0];
		}
		
		// We create threads to run HTTP load generator asynchronously 
		Program thread = new Program();
		thread.start();
	}
	
	public void run() {
		// Creates config class and verifies values
		Config config = new Config(configFile);
		if (config.verifyConfig()) {
			generateLoad(config);
		}
		
		System.out.println("Target RPS = "+config.getTargetRPS());
		System.out.println("Current RPS = "+CurrentRPS);
	}
	
	// Creates the HTTP Request and sends it
	public static void generateLoad(Config config) {
		//Create the expected request payload
		String utc = Instant.now().toString();
		String payload = "{ \"name\": \"GABRIELA_BOENTGES\", \"date\": \""+utc+"\", \"requests_sent\": \"REQUESTS_THIS_SESSION\" }";
		
		try {
			// Create connection to the server URL
			URL url = new URL (config.getServerURL()+"="+config.getKey());
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			
			// Set up connection that we expect input and output as well as POST method
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			// Set up header property that content is JSON and response will be JSON
			connection.setRequestProperty("Content-Type", "application/json; utf-8");
			//connection.setRequestProperty("X-Api-Key", config.getKey());
			connection.setRequestProperty("Accept", "application/json");
			
			//Sends request to server URL
			OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
			writer.write(payload);
			
			// Checks that there was no error in server response
			connection.getResponseCode();
			InputStream stream = connection.getErrorStream();
			if (stream == null) {
				//Read response from server
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				
				// We want to keep the connection open for a second
				long start = System.currentTimeMillis();
				long end = start + 1*1000;
				
				String line;
				while((line = reader.readLine()) != null && System.currentTimeMillis() < end) {
					// If response payload is successful = true, we will add it to current RPS
					if(line.equals("true")) {
						CurrentRPS += 1;
					}
				}
				
				reader.close();
			} else {
				// If error in server response, it will print it out 
				System.out.println("Server Response Status "+connection.getResponseCode());
			}
			
			// We disconnect from URL
			connection.disconnect();	
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
