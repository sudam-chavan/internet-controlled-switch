package com.controller.cloud;

import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cloud.artik.api.MessagesApi;
import cloud.artik.client.ApiClient;
import cloud.artik.client.ApiException;
import cloud.artik.client.Configuration;
import cloud.artik.client.auth.OAuth;
import cloud.artik.model.Action;
import cloud.artik.model.ActionArray;
import cloud.artik.model.Actions;
import cloud.artik.model.MessageIDEnvelope;
import cloud.artik.model.NormalizedMessage;
import cloud.artik.model.NormalizedMessagesEnvelope;

/**
 * Singleton helper class for Artik cloud API's
 */
public class ArtikCloudHelper {

    private static final String TAG = "##ArtikCloudHelper##";
    private static final String ARTIK_DEVICE_ID = "<device id>";
    private static final String ARTIK_DEVICE_TOKEN = "<device token>";

    /**
     * Below are the field and actions from Artik cloud device manifest. 
     * Change these according to your device manifest.
     */
    private static final String DEVICE_STATE_FIELD = "state";
    public static final String ACTION_ON = "setOn";
    public static final String ACTION_OFF = "setOff";

    private static ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    private static ArtikCloudHelper mInstance;
    private MessagesApi mApiInstance;

    private ArtikCloudHelper() {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        OAuth artikcloud_oauth = (OAuth) defaultClient.getAuthentication("artikcloud_oauth");
        artikcloud_oauth.setAccessToken(ARTIK_DEVICE_TOKEN);
        mApiInstance = new MessagesApi();
    }

    public static ArtikCloudHelper getInstance() {
        if (mInstance == null)
            mInstance = new ArtikCloudHelper();
        return mInstance;
    }

    public void sendAction(final String actionString, final CallbackListener callbackListener) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d(TAG, "sending action " + actionString);
                    Action action = new Action();
                    action.setName(actionString);
                    Actions actions = new Actions();
                    actions.setData(new ActionArray().addActionsItem(action));
                    actions.setType("action");
                    actions.setDdid(ARTIK_DEVICE_ID);
                    MessageIDEnvelope result = mApiInstance.sendActions(actions);
                    callbackListener.onSuccess(null);
                } catch (ApiException e) {
                    Log.e(TAG, "error while sending action", e);
                    callbackListener.onError(e);
                }
            }
        });
    }

    public void getRecentMessage(final CallbackListener callbackListener) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "getting most recent message");
                try {
                    NormalizedMessagesEnvelope result = mApiInstance.getLastNormalizedMessages(1,
                            ARTIK_DEVICE_ID, DEVICE_STATE_FIELD);
                    if (result != null && result.getCount() > 0) {
                        NormalizedMessage recentMessage = result.getData().get(0);
                        boolean status = (boolean) recentMessage.getData().get(DEVICE_STATE_FIELD);
                        if (status)
                            callbackListener.onSuccess("On");
                        else
                            callbackListener.onSuccess("Off");
                    }
                } catch (ApiException e) {
                    Log.e(TAG, "error while getting recent message", e);
                    callbackListener.onError(e);
                }
            }
        });
    }
}
