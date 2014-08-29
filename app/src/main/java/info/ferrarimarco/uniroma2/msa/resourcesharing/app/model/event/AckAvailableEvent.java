package info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.event;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.TaskResultType;

public class AckAvailableEvent {

    private String messageIdToAck;
    private TaskResultType result;

    public AckAvailableEvent(String messageIdToAck, TaskResultType result) {
        this.messageIdToAck = messageIdToAck;
        this.result = result;
    }

    public String getMessageIdToAck() {
        return messageIdToAck;
    }

    public TaskResultType getResult() {
        return result;
    }
}
