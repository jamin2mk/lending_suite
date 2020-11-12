package ru.bpmink.bpm.api.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import com.google.common.io.ByteSource;
import com.google.common.io.Closeables;
import com.google.common.io.Resources;
import com.google.gson.JsonElement;

import ru.bpmink.bpm.api.impl.BpmClientFactory;
import ru.bpmink.bpm.model.auth.Authentication;

public class LogOperation {

	public static void main(String[] args) {

//		AuthenticationClientTest authClient = new AuthenticationClientTest();
//		authClient.initializeClient();		

		// login
//		Authentication auth = authClient.login();		
//		String csrfToken = auth.getCsrfToken();
		String csrfToken = "eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2MDQ5MjM4NTQsInN1YiI6ImJwbWFkbWluIn0.QP_qT3bpO_m5xeuDR5kTos1qfSn_tZscwWHY3meYFeM";
		System.out.println(csrfToken);
		String hCsrfToken = csrfToken;
		String qModifiedAfter = "2020-11-09T15:44:41Z";
		String qModifiedBefore = "2020-11-09T15:44:41Z";
		String qProjectFilter = "LOSBIDV";

		LogOperation client = new LogOperation();
		client.initializeClient();
//		client.exposedItemsFetch();
//		String searchInstances = client.searchInstances(hCsrfToken, qModifiedAfter, qModifiedBefore, qProjectFilter);

		String pPiid = "23919";
		String qSystemId = "1c19c7dc-b7bd-48e7-9d0b-632b857c8992";
		JsonElement instanceDetail = client.getInstanceDetail(hCsrfToken, pPiid, qSystemId);
		System.out.println("TEST API");
		System.out.println(instanceDetail.getAsJsonObject().get("creationTime"));

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

	public JsonElement searchInstances(String hCsrfToken, String qModifiedAfter, String qModifiedBefore,
			String qProjectFilter) {

		JsonElement entity = null;

		try {
			LogOperation client = new LogOperation();
			client.initializeClient();

			entity = bpmClient.getLogClient().getInstances(hCsrfToken, qModifiedAfter, qModifiedBefore, qProjectFilter);
//			logger.info(entity);

			client.closeClient();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return entity;
	}

	public JsonElement getInstanceDetail(String hCsrfToken, String pPiid, String qSystemId) {

		JsonElement entity = null;

		try {
			LogOperation client = new LogOperation();
			client.initializeClient();

			entity = bpmClient.getLogClient().getInstanceDetail(hCsrfToken, pPiid, qSystemId);
//			logger.info(entity);
			client.closeClient();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return entity;
	}
}
