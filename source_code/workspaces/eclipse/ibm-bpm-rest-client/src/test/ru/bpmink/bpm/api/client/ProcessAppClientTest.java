package ru.bpmink.bpm.api.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.io.ByteSource;
import com.google.common.io.Closeables;
import com.google.common.io.Resources;

import ru.bpmink.bpm.api.impl.BpmClientFactory;
import ru.bpmink.bpm.model.common.RestRootEntity;
import ru.bpmink.bpm.model.other.exposed.ExposedItems;
import ru.bpmink.bpm.model.other.exposed.Item;
import ru.bpmink.bpm.model.process.ProcessDetails;
import ru.bpmink.bpm.model.process.ProcessState;

public class ProcessAppClientTest {
	public static void main(String[] args) {
		ProcessAppClientTest client = new ProcessAppClientTest();
		client.initializeClient();
//		client.exposedItemsFetch();
//		client.login();
		client.getProcessInstance();
		try {
			client.closeClient();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	
	private BpmClient bpmClient;
    private Logger logger = LoggerFactory.getLogger(getClass());
    //Exposed metadata for 'Standard HR Open New Position' business process definition.
    private Item hiringSampleMetadata;

    @BeforeClass
    public void prepareData() {
        initializeClient();
        fetchExposedProcess();

    }

    private void fetchExposedProcess() {
        //Default sample app 'Hiring Sample' have bpd(Process) with name 'Standard HR Open New Position'
        //We need to fetch it before all tests for reuse it's id's and preventing additional rest call for each test.
        hiringSampleMetadata = bpmClient.getExposedClient().getItemByName("Standard HR Open New Position");
    }

    @SuppressWarnings("Duplicates")
    private void initializeClient() {
        final URL url = Resources.getResource("server.properties");
        final ByteSource byteSource = Resources.asByteSource(url);
        final Properties properties = new Properties();

        InputStream inputStream = null;
        try {
            inputStream = byteSource.openBufferedStream();
            properties.load(inputStream);
            final String serverUrl = properties.getProperty("secure.url");
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

    @Test
    public void getProcessInstance() {
        /*
            So, for processes:
            BpdId is exposed item itemId (item.getItemId())
            ProcessAppId is exposed item processAppId (item.getProcessAppId())
            SnapshotId is exposed item snapshotId (item.getSnapshotId())
            BranchId is exposed item branchId (item.getBranchId())
        */

        RestRootEntity<ProcessDetails> startedProcessInstance = bpmClient.getClient().listProcessAppsSecure();
        logger.info("FINISHED!");
//        logger.info(startedProcessInstance.describe());
    }
}
