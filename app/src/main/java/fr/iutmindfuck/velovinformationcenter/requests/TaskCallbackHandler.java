package fr.iutmindfuck.velovinformationcenter.requests;


public interface TaskCallbackHandler {
    void onTaskStart();
    void onTaskCompleted(Object data);
}
