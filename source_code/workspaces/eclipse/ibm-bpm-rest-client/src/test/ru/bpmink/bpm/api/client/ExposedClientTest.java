package ru.bpmink.bpm.api.client;


import com.google.common.io.ByteSource;
import com.google.common.io.Closeables;
import com.google.common.io.Resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import ru.bpmink.bpm.api.impl.BpmClientFactory;
import ru.bpmink.bpm.model.auth.Authentication;
import ru.bpmink.bpm.model.common.RestEntity;
import ru.bpmink.bpm.model.common.RestRootEntity;
import ru.bpmink.bpm.model.other.exposed.ExposedItems;
import ru.bpmink.bpm.model.other.exposed.Item;
import ru.bpmink.bpm.model.other.exposed.ItemType;
import ru.bpmink.bpm.model.query.QueryList;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;


public class ExposedClientTest {

	public static void main(String[] args) {
		ExposedClientTest client = new ExposedClientTest();
		client.initializeClient();
//		client.exposedItemsFetch();
		client.login();
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
    
    @Test
    public void exposedItemsFetch() {
        RestRootEntity<ExposedItems> entity = bpmClient.getExposedClient().listItems();
        logger.info(entity.describe());
        Assert.assertFalse(entity.getPayload().getExposedItems().isEmpty(), "Default items list can't be empty");
    }

    @Test
    public void exposedItemsByTypeProcessFetch() {
        RestRootEntity<ExposedItems> entity = bpmClient.getExposedClient().listItems(ItemType.PROCESS);
        logger.info(entity.describe());
        for (Item item : entity.getPayload().getExposedItems()) {
            Assert.assertEquals(item.getItemType(), ItemType.PROCESS, "Item should match type: " + ItemType.PROCESS);
        }
    }

    @Test
    public void exposedItemsByTypeReportFetch() {
        RestRootEntity<ExposedItems> entity = bpmClient.getExposedClient().listItems(ItemType.REPORT);
        logger.info(entity.describe());
        for (Item item : entity.getPayload().getExposedItems()) {
            Assert.assertEquals(item.getItemType(), ItemType.REPORT, "Item should match type: " + ItemType.REPORT);
        }
    }

    @Test
    public void exposedItemsByTypeServiceFetch() {
        RestRootEntity<ExposedItems> entity = bpmClient.getExposedClient().listItems(ItemType.SERVICE);
        logger.info(entity.describe());
        for (Item item : entity.getPayload().getExposedItems()) {
            Assert.assertEquals(item.getItemType(), ItemType.SERVICE, "Item should match type: " + ItemType.SERVICE);
        }
    }

    @Test
    public void exposedItemsByTypeScoreBoardFetch() {
        RestRootEntity<ExposedItems> entity = bpmClient.getExposedClient().listItems(ItemType.SCOREBOARD);
        logger.info(entity.describe());
        for (Item item : entity.getPayload().getExposedItems()) {
            Assert.assertEquals(item.getItemType(), ItemType.SCOREBOARD,
                    "Item should match type: " + ItemType.SCOREBOARD);
        }
    }

    @Test
    public void exposedItemByName() {
        //Default sample app 'Hiring Sample' have bpd(Process) with name 'Standard HR Open New Position'
        Item entity = bpmClient.getExposedClient().getItemByName("Standard HR Open New Position");
        logger.info(entity.describe());
        Assert.assertNotNull(entity);
        Assert.assertEquals(entity.getItemType(), ItemType.PROCESS);
    }

    @Test
    public void exposedItemByNameNotFound() {
        //Default sample app 'Hiring Sample' have bpd(Process) with name 'Standard HR Open New Position'
        Item entity = bpmClient.getExposedClient().getItemByName(ItemType.SERVICE, "Standard HR Open New Position");
        logger.info(entity.describe());
        Assert.assertNotNull(entity);
        Assert.assertEquals(entity.getItemType(), null);
        Assert.assertEquals(entity.getId(), null);
    }

    @SuppressWarnings("ConstantConditions")
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void exposedItemByNameThrowExceptionWhenNullItemType() {
        bpmClient.getExposedClient().getItemByName(null, "Standard HR Open New Position");
        Assert.assertTrue(false, "Exception must be thrown before this assert");
    }

    @SuppressWarnings("ConstantConditions")
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void exposedItemByNameThrowExceptionWhenNullName() {
        bpmClient.getExposedClient().getItemByName(ItemType.PROCESS, null);
        Assert.assertTrue(false, "Exception must be thrown before this assert");
    }

    @SuppressWarnings("ConstantConditions")
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void exposedItemByNameThrowExceptionWhenNullItemTypeAndName() {
        bpmClient.getExposedClient().getItemByName(null, null);
        Assert.assertTrue(false, "Exception must be thrown before this assert");
    }

    @SuppressWarnings("ConstantConditions")
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void login() {
    	RestRootEntity<Authentication> entity = bpmClient.getExposedClient().login();
    	logger.info(entity.describe());
    }
}
