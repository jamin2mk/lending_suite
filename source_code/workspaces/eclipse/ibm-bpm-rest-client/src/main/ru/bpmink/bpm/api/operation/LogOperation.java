package ru.bpmink.bpm.api.operation;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteSource;
import com.google.common.io.Closeables;
import com.google.common.io.Resources;
import com.google.gson.JsonElement;

import ru.bpmink.bpm.api.client.BpmClient;
import ru.bpmink.bpm.api.impl.BpmClientFactory;

public class LogOperation {

	private BpmClient bpmClient;
	private Logger logger = LoggerFactory.getLogger(getClass());

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
