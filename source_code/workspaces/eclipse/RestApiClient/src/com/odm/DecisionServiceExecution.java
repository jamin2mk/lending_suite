package com.odm;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.util.Properties;

import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.Args;
import org.apache.http.util.EntityUtils;

import com.google.common.io.ByteSource;
import com.google.common.io.Closeables;
import com.google.common.io.Resources;

//import ru.bpmink.bpm.api.client.BpmClient;
//import ru.bpmink.bpm.api.impl.simple.SecuredBpmClient;
//import ru.bpmink.bpm.api.impl.simple.SimpleBpmClient;

public class DecisionServiceExecution {
	
	public static Properties getConfig(String propertiesPath) {
		
		Properties properties = new Properties();
		
		URL url = Resources.getResource("config.properties");
		ByteSource byteSource = Resources.asByteSource(url);
		
		InputStream inputStream = null;
		try {
			inputStream = byteSource.openBufferedStream();
			properties.load(inputStream);
		} catch (Exception e) {
			System.out.println(1);
			System.out.println(2);
			e.printStackTrace();			
		} finally {
			Closeables.closeQuietly(inputStream);
		}
				
		return properties;
	}
	
	public static void main(String[] args) throws ClientProtocolException, IOException {

		Properties properties = getConfig("config.properties");
		if (properties == null) {
			System.out.println("ERROR");
		}
		else {
			System.out.println(properties.getProperty("odm.rule.endpoint"));
		}
		//executeRule();
	}

	private static void executeRule() throws UnsupportedEncodingException, IOException, ClientProtocolException {
		// Replace <host> and <port>
		String endpointURI = "http://10.0.18.54:9080/DecisionService/rest/v1/SALOP_BIDV/KO_Operation";

		String contentType = "application/json";

		// Set the borrower and the loan
		String payload = "{\"Input\":\"{\\\"IsBlackList\\\":1,\\\"CustObj\\\":DTKH_01,\\\"getPosition\\\":\\\"CVCT01\\\",\\\"IsCapacity\\\":1,\\\"LVDT\\\":\\\"LVDT02\\\",\\\"Gender\\\":\\\"GT_NAM\\\",\\\"Age\\\":30,\\\"Income\\\":12345678765,\\\"IsSameAddress\\\":1}\",\"__DecisionID__\":\"string\"}";

		CloseableHttpClient client = HttpClients.createDefault();

		try {

			HttpPost httpPost = new HttpPost(endpointURI);
			httpPost.setHeader("Content-Type", contentType);
			httpPost.setHeader("Accept", contentType);
			httpPost.setEntity(new StringEntity(payload));
			CloseableHttpResponse response = client.execute(httpPost);
			try {
				if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
					System.err.println("Status Code: " + response.getStatusLine().getStatusCode());
					System.err.println("Status Line: " + response.getStatusLine());
					String responseEntity = EntityUtils.toString(response.getEntity());
					System.err.println("Response Entity: " + responseEntity);

					throw new RuntimeException("An error occurred when invoking Decision Service at: " + endpointURI
							+ "\n" + response.getStatusLine() + ": " + responseEntity);
				} else {
					String result = EntityUtils.toString(response.getEntity());
					System.out.println("Result: " + result);
				}
			} finally {
				if (response != null) {
					response.close();
				}
			}
		} finally {
			client.close();
		}
	}

	public static void testODM() {
		
		String serverUri = "http://10.0.18.54:9080";
		String user = "resadmin";
		String pass = "Hpt123456";
		
		URI uri = URI.create(serverUri);
		
	}
	
	

	
}
