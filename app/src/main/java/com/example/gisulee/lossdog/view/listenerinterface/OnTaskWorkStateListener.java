package com.example.gisulee.lossdog.view.listenerinterface;

public abstract class OnTaskWorkStateListener {
    public void onTaskExecute(){};
    public void onTaskPost(String resultCode, int totalCount){};
    public void onTaskPost(){};
}
