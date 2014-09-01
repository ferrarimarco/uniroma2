package info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.event;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.User;

public class RegisteredUserAvailableEvent {

    private User registeredUser;

    public RegisteredUserAvailableEvent(User registeredUser) {

        this.registeredUser = registeredUser;
    }

    public User getRegisteredUser() {
        return registeredUser;
    }
}
