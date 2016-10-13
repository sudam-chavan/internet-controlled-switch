package com.controller.cloud;

/**
 *  Callback listener for cloud API calls
 */
public interface CallbackListener {

    void onSuccess(String message);
    void onError(Throwable throwable);
}
