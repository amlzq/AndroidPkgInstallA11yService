package com.example.pkginstalla11yservice;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MyA11yService extends AccessibilityService {

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Util.print("onAccessibilityEvent = " + event.toString());

        String className = event.getClassName() == null ? "" : event.getClassName().toString();

        switch (event.getEventType()) {
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:

                if (className.contains("com.android.settings.SubSettings") ||
                        className.contains("AccessibilitySettingsActivity")) {
                    // performGlobalAction(GLOBAL_ACTION_BACK);

                } else if (className.contains("AdbInstallActivity")) {
                    // Xiaomi's MIUI-V12.0.6.0.QJECNXM
                    // com.miui.permcenter.install.AdbInstallActivity
                    handleMIUI();

                } else if (className.contains("AccountVerifyActivity")) {
                    // vivo's OriginOS1.0 PD2106B_A_1.8.5
                    // com.android.packageinstaller/.PackageInstallerActivity
                    // com.bbk.account/.activity.AccountVerifyActivity
                    handlePasswordForOriginOS();
                } else if (className.contains("PackageInstallerActivity")
                        && Build.MANUFACTURER.toLowerCase().equals("vivo")) {
                    // Check that the Continue button finishes rendering every 1 seconds
                    final Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            boolean result = handleContinueForOriginOS();
                            if (result) timer.cancel();
                        }
                    }, 1000);

                }

                break;
            case AccessibilityEvent.TYPE_VIEW_CLICKED:

                break;
        }
    }

    @Override
    public void onInterrupt() {

    }

    private boolean handleContinueForOriginOS() {
        Util.print("handleContinueForOriginOS");

        AccessibilityNodeInfo root = getRootInActiveWindow();
        // A11yServiceHelper.getInstance().dfsNode(root, 0);

        List<AccessibilityNodeInfo> nodeInfos = root.findAccessibilityNodeInfosByViewId("android:id/button1");
        if (nodeInfos == null || nodeInfos.size() == 0) {
            Util.error(getString(R.string.node_not_found, getString(R.string.continue_button)));
            Toast.makeText(getApplicationContext(), getString(R.string.node_not_found, getString(R.string.continue_button)), Toast.LENGTH_SHORT).show();
            return false;
        }
        AccessibilityNodeInfo buttonNode = nodeInfos.get(0);
        buttonNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        return true;
    }

    private void handlePasswordForOriginOS() {
        Util.print("handlePasswordForOriginOS");

        String password = Util.getPassword(getApplicationContext());
        if (password != null && password.isEmpty()) {
            Util.error(getString(R.string.password_empty));
            Toast.makeText(getApplicationContext(), R.string.password_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        AccessibilityNodeInfo root = getRootInActiveWindow();
        // A11yServiceHelper.getInstance().dfsNode(root, 0);

        // Find EditText node and set password text
        List<AccessibilityNodeInfo> nodeInfos = root.findAccessibilityNodeInfosByViewId("com.bbk.account:id/edit_Text");
        if (nodeInfos == null || nodeInfos.size() == 0) {
            Util.error(getString(R.string.node_not_found, "EditText"));
            Toast.makeText(getApplicationContext(), getString(R.string.node_not_found, "EditText"), Toast.LENGTH_SHORT).show();
            return;
        }

        AccessibilityNodeInfo editTextNode = nodeInfos.get(0);
        Bundle arguments = new Bundle();
        arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, password);
        editTextNode.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);

        // Find Button node and click it.
        nodeInfos = root.findAccessibilityNodeInfosByViewId("android:id/button1");
        if (nodeInfos == null || nodeInfos.size() == 0) {
            Util.error(getString(R.string.node_not_found, getString(R.string.ok_button)));
            Toast.makeText(getApplicationContext(), getString(R.string.node_not_found, getString(R.string.ok_button)), Toast.LENGTH_SHORT).show();
            return;
        }
        AccessibilityNodeInfo buttonNode = nodeInfos.get(0);
        buttonNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
    }

    private void handleMIUI() {
        Util.print("handleMIUI");
        AccessibilityNodeInfo root = getRootInActiveWindow();
        List<AccessibilityNodeInfo> nodeInfos = root.findAccessibilityNodeInfosByViewId("android:id/button2");
        boolean nodeFound = false;
        for (AccessibilityNodeInfo nodeInfo : nodeInfos) {
            if (nodeInfo.isClickable()) {
                nodeFound = true;
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }
        if (!nodeFound) {
            Util.error(getString(R.string.node_not_found, "Button"));
            Toast.makeText(getApplicationContext(), getString(R.string.node_not_found, "Button"), Toast.LENGTH_SHORT).show();
        }
    }
}
