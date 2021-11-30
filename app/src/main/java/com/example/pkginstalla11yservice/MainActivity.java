package com.example.pkginstalla11yservice;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    final int REQUEST_ID_ACCESSIBILITY_SETTINGS = 10;

    Context mContext;
    EditText mPassword;
    Button mSave;
    Button mQuery;
    TextView tipsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_main);

        TextView textView = findViewById(R.id.env_info);
        textView.setText(getString(R.string.env_info, Build.VERSION.SDK_INT, Build.MANUFACTURER));

        findViewById(R.id.check_a11y_service_state).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean enabled = A11yServiceHelper.getInstance().isEnabled(getApplicationContext(), MyA11yService.class);
                if (enabled) {
                    Toast.makeText(mContext, getString(R.string.a11y_enable_state, true), Toast.LENGTH_SHORT).show();
                    tipsView.setVisibility(View.VISIBLE);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setMessage(getString(R.string.findService, getString(R.string.app_name)));
                    builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                            startActivityForResult(intent, REQUEST_ID_ACCESSIBILITY_SETTINGS);
                        }
                    });
                    builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }
            }
        });

        mPassword = findViewById(R.id.password);
        mSave = findViewById(R.id.save);
        mQuery = findViewById(R.id.query);

        if (Build.MANUFACTURER.toLowerCase().equals("vivo")
                || Build.MANUFACTURER.toLowerCase().equals("oppo")) {
            mSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String input = mPassword.getText().toString().trim();
                    if (input.isEmpty()) {
                        Toast.makeText(mContext, "Please input password.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Util.savePassword(mContext, input);
                    Util.editClearFocus(mPassword);
                    Toast.makeText(mContext, "Succeeded", Toast.LENGTH_SHORT).show();
                }
            });
            mQuery.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    String password = Util.getPassword(mContext);
                    Toast.makeText(mContext, password, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            mPassword.setVisibility(View.GONE);
            mSave.setVisibility(View.GONE);
            mQuery.setVisibility(View.GONE);
        }

        tipsView = findViewById(R.id.tips);
        tipsView.setVisibility(View.GONE);

        findViewById(R.id.content).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Util.editClearFocus(mPassword);
            }
        });
    }

    @Override
    protected void onResume() {
        Util.debug(this.toString() + " onResume");
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        Util.debug(this.toString() + " onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ID_ACCESSIBILITY_SETTINGS) {
            boolean enabled = A11yServiceHelper.getInstance().isEnabled(this, MyA11yService.class);
            Toast.makeText(mContext, getString(R.string.a11y_enable_state, enabled), Toast.LENGTH_SHORT).show();
            tipsView.setVisibility(View.VISIBLE);
        }
    }
}
