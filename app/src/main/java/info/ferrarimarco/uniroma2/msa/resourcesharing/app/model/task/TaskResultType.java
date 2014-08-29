package info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task;

public enum TaskResultType {
    SUCCESS,
    FAILURE,

    // For UserIdCheckAsyncTask
    USER_ID_OK,
    USER_ID_NOT_FREE;
}
