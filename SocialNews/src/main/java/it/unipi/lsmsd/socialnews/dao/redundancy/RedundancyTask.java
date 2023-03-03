package it.unipi.lsmsd.socialnews.dao.redundancy;

import java.io.Serializable;

public class RedundancyTask implements Serializable {

    private TaskType operationType;

    //postId or reporterId based on operationType
    private String identifier;

    //Not negative integer counter
    private int counter;

    public RedundancyTask(TaskType operationType, String identifier) {
        this(operationType, identifier, 1);
    }

    public RedundancyTask(TaskType operationType, String identifier, int counter) {
        this.operationType = operationType;
        this.identifier = identifier;

        if (counter == 0)
            counter = 1;
        this.counter = counter<0? -counter : counter;
    }

    public TaskType getOperationType() {
        return operationType;
    }

    public void setOperationType(TaskType operationType) {
        this.operationType = operationType;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        if (counter == 0)
            counter = 1;
        this.counter = counter<0? -counter : counter;
    }

    @Override
    public String toString() {
        return "RedundancyTask{" +
                "operationType=" + operationType +
                ", postId='" + identifier + '\'' +
                '}';
    }
}

