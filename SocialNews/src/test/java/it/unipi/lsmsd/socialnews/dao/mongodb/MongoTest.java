package it.unipi.lsmsd.socialnews.dao.mongodb;

import it.unipi.lsmsd.socialnews.dao.redundancy.RedundancyUpdater;
import it.unipi.lsmsd.socialnews.service.util.Statistic;
import it.unipi.lsmsd.socialnews.service.util.Util;
import it.unipi.lsmsd.socialnews.servlet.admin.UsersServlet;
import it.unipi.lsmsd.socialnews.threading.ServiceWorkerPool;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MongoTest {

    MongoConnection conn;

    @BeforeClass
    public void setUp(){
        conn = MongoConnection.getConnection();

        Properties properties = new Properties();
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("service.properties");
        try {
            properties.load(inputStream);
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load properties");
        }
        Util.configure(properties);
        UsersServlet.setPageSize(Integer.valueOf(properties.getProperty("listUserPageSize")));
        Statistic.configure(
                Integer.valueOf(properties.getProperty("defaultWindowSize")),
                Integer.valueOf(properties.getProperty("defaultLastN")),
                properties.getProperty("defaultUnitOfTime")
        );
        ServiceWorkerPool.getPool();
        RedundancyUpdater.getInstance();
    }

    @AfterClass
    public void tearDown(){
        MongoConnection.getConnection().close();
    }

    @Test
    public void testConnection(){
     assert conn.ping();
    }

}
