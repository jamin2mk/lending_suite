package ru.bpmink.bpm.api.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.io.ByteSource;
import com.google.common.io.Closeables;
import com.google.common.io.Resources;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import ru.bpmink.bpm.api.impl.BpmClientFactory;
import ru.bpmink.bpm.model.auth.Authentication;
import ru.bpmink.bpm.model.common.RestRootEntity;

public class AuthenticationClientTest {
	
	public static void main(String[] args) {
		
		/* initializes args for test - begin */
		args = new String[] {"TSA2"};
		
		if(args.length == 0) {
			System.out.println("Please enter [container] for sync.");
			return; 
		}
		
		String container = args[0];
		
		JsonParser jsonParser = new JsonParser();
		
		AuthenticationClientTest client = new AuthenticationClientTest();
		client.initializeClient();		
		
		// login
		Authentication auth = client.login();		
		String csrfToken = auth.getCsrfToken();
		
		// get versions
		String versionInfo = client.getAllVersion(container, csrfToken);
		JsonObject rootTree = jsonParser.parse(versionInfo).getAsJsonObject();
		JsonArray versionArray = rootTree.getAsJsonArray("versions");
		JsonArray activeVersions = new JsonArray();
		
		System.out.println("\n[INFO] Full versions of " + container + ": ");
		System.out.println(versionArray);
		
		for (JsonElement item : versionArray) {
			boolean isActive = item.getAsJsonObject().get("active").getAsBoolean();
			if(isActive) {
				activeVersions.add(item);
			}
		}		
		
		int lastIndex = activeVersions.size() - 1;
		if(lastIndex < 1) {
			System.out.println("\n[ERROR] Sync is failed! Can't sync environment variables cause by have only one version found.");
			return;
		}
		
		String syncVersion = versionArray.get(lastIndex).getAsJsonObject().get("version").getAsString();
		String preVersion = versionArray.get(lastIndex-1).getAsJsonObject().get("version").getAsString();
		
		// sync
		System.out.println("\n>>>>>>>>>>>>> BEGIN SYNC version ["+syncVersion+"] by ["+preVersion+"] >>>>>>>>>>>>>>>>");
		String syncInfo = client.syncEnvironmentVariable(container, preVersion, syncVersion, csrfToken);
		String url = jsonParser.parse(syncInfo).getAsJsonObject().get("url").getAsString();
		
		// check sync progress
		String state = "running";
		String ProgressInfo = null;
		while (state.equalsIgnoreCase("running")) {
			System.out.println();
			ProgressInfo = client.checkySyncProgress(csrfToken, url);
			state = jsonParser.parse(ProgressInfo).getAsJsonObject().get("state").getAsString();
			
			if(!state.equalsIgnoreCase("running")) {
				System.out.println("\n>>>>>>>>>>>>> SYNC IS SUCCESSFUL! <<<<<<<<<<<<<");
				System.out.println(ProgressInfo);
			}
		}
		
		// get env_vars of v1
		String envVars_v1 = client.retrieveEnvironmentVariable(container, preVersion, csrfToken);		
		JsonArray v1List = jsonParser.parse(envVars_v1).getAsJsonObject().get("pairs").getAsJsonArray();
		
		System.out.println();
		String envVars_v2 = client.retrieveEnvironmentVariable(container, syncVersion, csrfToken);		
		JsonArray v2List = jsonParser.parse(envVars_v2).getAsJsonObject().get("pairs").getAsJsonArray();
		
		System.out.println("\n---------------------------------------[WARNING]-------------------------------------------");
		
		System.out.println("VARIABLES CAN'T SYNC CAUSE BY NOT EXIST IN ["+preVersion+"]");
		for (JsonElement item : v2List) {
			if(!v1List.contains(item)) {
				System.out.println(item.toString());
			}
		}
		
		System.out.println("\nVARIABLES CAN'T SYNC CAUSE BY NOT EXIST IN ["+syncVersion+"]");
		int i = 0;
		for (JsonElement item : v1List) {
			if(!v2List.contains(item)) {
				System.out.println(item.toString());
				i++;
			}
		}
		if(i == 0) {
			System.out.println("Không có");
		}
		
		System.out.println("-------------------------------------------------------------------------------------------");
		
		
		
		
		
//		client.retrieveSnapshots("LOSBIDV", auth.getCsrfToken());
//		client.setEnvironmentVariable("TSA2", v2, auth.getCsrfToken(), env_vars);		
		
		try {
			client.closeClient();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private BpmClient bpmClient;
    private Logger logger = LoggerFactory.getLogger(getClass());
    
    @BeforeClass
    public void initializeClient() {
        final URL url = Resources.getResource("server.properties");
        final ByteSource byteSource = Resources.asByteSource(url);
        final Properties properties = new Properties();

        InputStream inputStream = null;
        try {
            inputStream = byteSource.openBufferedStream();
            properties.load(inputStream);
            final String serverUrl = properties.getProperty("default.url");
            final String user = properties.getProperty("default.user");
            final String password = properties.getProperty("default.password");
            bpmClient = BpmClientFactory.createClient(serverUrl, user, password);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Closeables.closeQuietly(inputStream);
        }
    }
    
    @AfterClass
    public void closeClient() throws IOException {
        Closeables.close(bpmClient, true);
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class)
    public Authentication login() {
    	Authentication entity = bpmClient.getAuthenticationClient().login();
    	logger.info(entity.describe());
    	return entity;
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class)
    public String retrieveEnvironmentVariable(String container, String version, String csrfToken) {
    	String entity = bpmClient.getAuthenticationClient2(container, version).retrieveEnvironmentVariables(csrfToken);
//    	logger.info(entity);
    	return entity;
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class)
    public String syncEnvironmentVariable(String container, String version, String targetVersion, String csrfToken) {
    	String entity = bpmClient.getAuthenticationClient3(container, version, targetVersion).syncEnvironmentVariables(csrfToken);
    	logger.info(entity);
    	return entity;
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class)
    public String retrieveSnapshots(String container, String csrfToken) {
    	String entity = bpmClient.getAuthenticationClient4(container).retrieveSnapshots(csrfToken);
    	logger.info(entity);
    	return entity;
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class)
    public String setEnvironmentVariable(String container, String version, String csrfToken, String env_vars) {
    	String entity = bpmClient.getAuthenticationClient2(container, version).setEnvironmentVariables(csrfToken, env_vars);
    	logger.info(entity);
    	return entity;
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class)
    public String checkySyncProgress(String csrfToken, String url) {
    	String entity = bpmClient.getAuthenticationClient5(url).getWithToken(csrfToken);
    	return entity;
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class)
    public String getAllVersion(String container, String csrfToken) {
    	String entity = bpmClient.getAuthenticationClient6(container).getWithToken(csrfToken);
    	logger.info(entity);
    	return entity;
    }
}
