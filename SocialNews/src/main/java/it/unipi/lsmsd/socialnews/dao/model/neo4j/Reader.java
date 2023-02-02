package it.unipi.lsmsd.socialnews.dao.model.neo4j;

import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;


@NodeEntity
public class Reader {
    @Id
    @Property(name="reader_id")
    private String readerId;

    public Reader() {
    }

    public Reader(String readerId) {
        this.readerId = readerId;
    }

    public String getReaderId() {
        return readerId;
    }

    public void setReaderId(String readerId) {
        this.readerId = readerId;
    }


    @Override
    public String toString() {
        return "Reader{" +
                "readerId='" + readerId + '\'' +
                '}';
    }
}
