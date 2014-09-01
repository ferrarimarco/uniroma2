package info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.event;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.UserTaskResult;

public class UserLocalRegistrationCompletedEvent extends AbstractUserEvent {

    public UserLocalRegistrationCompletedEvent(UserTaskResult result) {
        super(result);
    }
}
