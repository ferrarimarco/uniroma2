package info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.event;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.UserTaskResult;

public class AbstractUserEvent {
    private UserTaskResult result;

    public AbstractUserEvent(UserTaskResult result) {
        this.result = result;
    }

    public UserTaskResult getResult() {
        return result;
    }
}
