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
import ru.bpmink.bpm.model.other.exposed.Item;
import ru.bpmink.bpm.model.process.ProcessDetails;
import ru.bpmink.bpm.model.process.ProcessState;
import ru.bpmink.bpm.model.query.QueryResultSetCount;

public class QueryClientTest {

	public static void main(String[] args) {
		QueryClientTest client = new QueryClientTest();
		client.initializeClient();
		client.countProcessInstance();
	}
	
	private BpmClient bpmClient;
    private Logger logger = LoggerFactory.getLogger(getClass());
    //Exposed metadata for 'Standard HR Open New Position' business process definition.
    private Item hiringSampleMetadata;

    @BeforeClass
    public void prepareData() {
        initializeClient();
//        fetchExposedProcess();

    }

//    private void fetchExposedProcess() {
//        //Default sample app 'Hiring Sample' have bpd(Process) with name 'Standard HR Open New Position'
//        //We need to fetch it before all tests for reuse it's id's and preventing additional rest call for each test.
//        hiringSampleMetadata = bpmClient.getProcessQueryClient().
//    }

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

    @SuppressWarnings("ConstantConditions")
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void processStartThrowExceptionWhenNullBpdId() {
        bpmClient.getProcessClient().startProcess(null, hiringSampleMetadata.getProcessAppId(), null, null, null);
        Assert.assertTrue(false, "Exception must be thrown before this assert");
    }

    @SuppressWarnings("ConstantConditions")
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void processStartThrowExceptionWhenNullOptionalIds() {
        bpmClient.getProcessClient().startProcess(hiringSampleMetadata.getItemId(), null, null, null, null);
        Assert.assertTrue(false, "Exception must be thrown before this assert");
    }
    
    @Test
    public void countProcessInstance() {
        /*
            So, for processes:
            BpdId is exposed item itemId (item.getItemId())
            ProcessAppId is exposed item processAppId (item.getProcessAppId())
            SnapshotId is exposed item snapshotId (item.getSnapshotId())
            BranchId is exposed item branchId (item.getBranchId())
        */

        RestRootEntity<QueryResultSetCount> queryResultSetCount = bpmClient.getProcessQueryClient().queryEntitiesCount();
        logger.info(queryResultSetCount.describe());

        Assert.assertNotNull(queryResultSetCount.getPayload(), "Process payload should be filled");
//        Assert.assertEquals(queryResultSetCount.getPayload().getState(), ProcessState.STATE_RUNNING,
//                "Process state should be: " + ProcessState.STATE_RUNNING);
    }
}
