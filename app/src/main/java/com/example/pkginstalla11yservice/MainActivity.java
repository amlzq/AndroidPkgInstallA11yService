package com.example.pkginstalla11yservice;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {
    final int REQUEST_ID_ACCESSIBILITY_SETTINGS = 10;

    TextView mA11yServiceState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textView = findViewById(R.id.text_view);
        textView.setText("Running on Android " + Build.VERSION.SDK_INT + " , manufacturer " + Build.MANUFACTURER);

        findViewById(R.id.check_a11y_service_state).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean enabled = A11yServiceHelper.getInstance().isEnabled(getApplicationContext(), MyA11yService.class);
                if (enabled) {
                    mA11yServiceState.setText("Accessibility Service enabled " + enabled);
                } else {
                    Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    startActivityForResult(intent, REQUEST_ID_ACCESSIBILITY_SETTINGS);
                }
            }
        });
        mA11yServiceState = findViewById(R.id.a11y_service_state);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ID_ACCESSIBILITY_SETTINGS) {
            boolean enabled = A11yServiceHelper.getInstance().isEnabled(this, MyA11yService.class);
            mA11yServiceState.setText("Accessibility Service enabled " + enabled);
        }
    }
}
