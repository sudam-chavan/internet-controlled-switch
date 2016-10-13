package com.controller;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.controller.cloud.ArtikCloudHelper;
import com.controller.cloud.CallbackListener;

public class SwitchActivity extends AppCompatActivity {

    private Button mOnBtn;
    private Button mOffBtn;
    private boolean mIsOn;
    private TextView mStatusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch);

        mStatusText = (TextView) findViewById(R.id.status_text);
        mOnBtn = (Button) findViewById(R.id.on_button);
        mOffBtn = (Button) findViewById(R.id.off_button);

        mOnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // set switch on
                setOn();
            }
        });
        mOffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // set switch off
                setOff();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSwitchStatus();
    }

    private void setOn() {
        if (mIsOn) {
            Toast.makeText(SwitchActivity.this, "Switch is already On",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        final ProgressDialog progressDialog = showProgressDialog("Setting switch On...");
        ArtikCloudHelper.getInstance().sendAction(ArtikCloudHelper.ACTION_ON, new CallbackListener() {
            @Override
            public void onSuccess(String response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mIsOn = true;
                        progressDialog.dismiss();
                        Toast.makeText(SwitchActivity.this, "Switch is On",
                                Toast.LENGTH_SHORT).show();
                        updateStatusText();
                    }
                });
            }

            @Override
            public void onError(final Throwable throwable) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        Toast.makeText(SwitchActivity.this, "Error " + throwable.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void setOff() {
        if (!mIsOn) {
            Toast.makeText(SwitchActivity.this, "Switch is already Off",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        final ProgressDialog progressDialog = showProgressDialog("Setting switch Off...");
        ArtikCloudHelper.getInstance().sendAction(ArtikCloudHelper.ACTION_OFF, new CallbackListener() {
            @Override
            public void onSuccess(String response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mIsOn = false;
                        progressDialog.dismiss();
                        Toast.makeText(SwitchActivity.this, "Switch is Off",
                                Toast.LENGTH_SHORT).show();
                        updateStatusText();
                    }
                });
            }

            @Override
            public void onError(final Throwable throwable) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        Toast.makeText(SwitchActivity.this, "Error " + throwable.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     * Getting switch works by getting recent message from Artik cloud.
     * Arduino program syncs switch state with Artik cloud every time its
     * state is changed.
     */
    private void getSwitchStatus() {
        final ProgressDialog progressDialog = showProgressDialog("Getting switch status...");
        ArtikCloudHelper.getInstance().getRecentMessage(new CallbackListener() {
            @Override
            public void onSuccess(final String response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        mIsOn = response.equalsIgnoreCase("on");
                        updateStatusText();
                    }
                });
            }

            @Override
            public void onError(final Throwable throwable) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        Toast.makeText(SwitchActivity.this, "Error while getting status " + throwable.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void updateStatusText() {
        String status = "Status: " + (mIsOn ? "On" : "Off");
        mStatusText.setText(status);
    }

    @NonNull
    private ProgressDialog showProgressDialog(String message) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
        return progressDialog;
    }
}
