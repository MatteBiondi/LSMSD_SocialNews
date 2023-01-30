package it.unipi.lsmsd.socialnews.dao.mongodb;

import it.unipi.lsmsd.socialnews.dao.exception.SocialNewsDataAccessException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class MongoTest {

    MongoConnection conn;

    @BeforeClass
    public void setUp(){
        conn = MongoConnection.getConnection();
    }

    @AfterClass
    public void tearDown(){
        MongoConnection.getConnection().close();
    }

    @Test
    public void testConnection(){
     assert conn.ping();
    }

    @Test
    public void testDAO(){

    }
}
