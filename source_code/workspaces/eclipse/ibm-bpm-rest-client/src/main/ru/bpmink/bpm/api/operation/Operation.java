package ru.bpmink.bpm.api.operation;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteSource;
import com.google.common.io.Closeables;
import com.google.common.io.Resources;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import ru.bpmink.bpm.api.client.BpmClient;
import ru.bpmink.bpm.api.impl.BpmClientFactory;

public class Operation {

	private static BpmClient bpmClient;
	private static Logger logger = LoggerFactory.getLogger(Operation.class);

	public static void initializeClient() {

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

	public static void closeClient() throws IOException {
		Closeables.close(bpmClient, true);
	}

	public static JsonArray searchInstances(String hCsrfToken, Date qModifiedAfter, Date qModifiedBefore,
			String qProjectFilter, String qSeachFilter, String qUserFilter, String qStatusFilter) {

		JsonArray entity = null;
		try {
			initializeClient();

			entity = bpmClient.getLogClient().getInstances(hCsrfToken, qModifiedAfter, qModifiedBefore, qProjectFilter,
					qSeachFilter, qUserFilter, qStatusFilter);

			logger.info(entity.toString());

			closeClient();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return entity;
	}

	public static JsonObject getInstanceDetail(String hCsrfToken, String pPiid, String qSystemId) {

		JsonObject entity = null;
		try {
			initializeClient();
			entity = bpmClient.getLogClient().getInstanceDetail(hCsrfToken, pPiid, qSystemId).getAsJsonObject();
			closeClient();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return entity;
	}
}
