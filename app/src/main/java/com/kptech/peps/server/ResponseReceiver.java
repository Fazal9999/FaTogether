package com.kptech.peps.server;

/**
 * Created by BXDC46 on 12/15/2014.
 */
public interface ResponseReceiver {

    public void onSuccess(int code, Object result);

    public void onError(String error);
}
