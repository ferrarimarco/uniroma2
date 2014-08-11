package info.ferrarimarco.uniroma2.msa.resourcesharing.app.gcm;

public enum GcmMessage {
    REGISTRATION_REQUEST("info.ferrarimarco.uniroma2.msa.resourcesharing.app.gcm.message.REGISTER"),
    NEW_RESOURCE_FROM_ME("info.ferrarimarco.uniroma2.msa.resourcesharing.app.gcm.message.CREATE_NEW_RESOURCE"),
    NEW_RESOURCE_FROM_OTHERS("info.ferrarimarco.uniroma2.msa.resourcesharing.app.gcm.message.NEW_RESOURCE_BY_OTHERS"),
    DELETE_MY_RESOURCE("info.ferrarimarco.uniroma2.msa.resourcesharing.app.gcm.message.DELETE_RESOURCE"),
    DELETE_RESOURCE_BY_OTHERS("info.ferrarimarco.uniroma2.msa.resourcesharing.app.gcm.message.DELETE_RESOURCE_BY_OTHERS"),
    MY_CURRENT_POSITION("info.ferrarimarco.uniroma2.msa.resourcesharing.app.gcm.message.CURRENT_POSITION"),
    CLEAR_RESOURCE_NOTIFICATION("info.ferrarimarco.uniroma2.msa.resourcesharing.app.gcm.message.CLEAR_RESOURCE"),
    C2D_RESPONSE("info.ferrarimarco.uniroma2.msa.resourcesharing.app.gcm.message.C2D_RESPONSE"),
    D2C_RESPONSE("info.ferrarimarco.uniroma2.msa.resourcesharing.app.gcm.message.D2C_RESPONSE");

    private String stringValue;

    private GcmMessage(String stringValue) {
        this.stringValue = stringValue;
    }

    public String getStringValue() {
        return stringValue;
    }
}
