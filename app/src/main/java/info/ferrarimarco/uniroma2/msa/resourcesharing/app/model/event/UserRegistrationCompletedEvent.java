package info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.event;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.UserTaskResult;

public class UserRegistrationCompletedEvent extends AbstractUserEvent {

    public UserRegistrationCompletedEvent(UserTaskResult result) {
        super(result);
    }
}
