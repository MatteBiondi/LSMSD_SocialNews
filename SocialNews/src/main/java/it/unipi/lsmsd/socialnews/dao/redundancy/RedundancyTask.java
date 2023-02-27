package it.unipi.lsmsd.socialnews.dao.redundancy;

import java.io.Serializable;

public class RedundancyTask implements Serializable {

    private TaskType operationType;
    private String identifier; //postId or reporterId based on operationType

    public RedundancyTask(TaskType operationType, String identifier) {
        this.operationType = operationType;
        this.identifier = identifier;
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

    @Override
    public String toString() {
        return "RedundancyTask{" +
                "operationType=" + operationType +
                ", postId='" + identifier + '\'' +
                '}';
    }
}

