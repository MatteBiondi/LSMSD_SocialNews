package it.unipi.lsmsd.socialnews.dao.mongodb;

import it.unipi.lsmsd.socialnews.dao.exception.SocialNewsDataAccessException;
import it.unipi.lsmsd.socialnews.dao.redundancy.RedundancyUpdater;
import it.unipi.lsmsd.socialnews.service.PostService;
import it.unipi.lsmsd.socialnews.service.ReporterService;
import it.unipi.lsmsd.socialnews.service.exception.SocialNewsServiceException;
import it.unipi.lsmsd.socialnews.service.implement.PostServiceImpl;
import it.unipi.lsmsd.socialnews.service.implement.ReporterServiceImpl;
import it.unipi.lsmsd.socialnews.service.util.Statistic;
import it.unipi.lsmsd.socialnews.service.util.Util;
import it.unipi.lsmsd.socialnews.servlet.admin.UsersServlet;
import it.unipi.lsmsd.socialnews.threading.ServiceWorkerPool;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
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
