package info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.event;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.UserTaskResult;

public class UserDeletionCompletedEvent extends AbstractUserEvent {
    public UserDeletionCompletedEvent(UserTaskResult result) {
        super(result);
    }
}
