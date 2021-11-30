package com.example.pkginstalla11yservice;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import java.util.List;

public class MyA11yService extends AccessibilityService {

    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mHandler.removeCallbacksAndMessages(null);
        return super.onUnbind(intent);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Util.debug("onAccessibilityEvent = " + event.toString());

        switch (event.getEventType()) {
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                String className = event.getClassName() == null ? "" : event.getClassName().toString();
                if (className.contains("com.android.settings.SubSettings") ||
                        className.contains("AccessibilitySettingsActivity")) {
                    // performGlobalAction(GLOBAL_ACTION_BACK);

                } else if (className.contains("AdbInstallActivity")) {
                    // Xiaomi's MIUI-V12.0.6.0.QJECNXM
                    // com.miui.permcenter.install.AdbInstallActivity
                    Util.info("Hit page " + className);
                    handleMIUI();

                } else if (className.contains("AccountVerifyActivity")) {
                    // vivo's OriginOS1.0 PD2106B_A_1.8.5
                    // com.android.packageinstaller/.PackageInstallerActivity
                    // com.bbk.account/.activity.AccountVerifyActivity
                    Util.info("Hit page " + className);
                    handlePasswordForOriginOS();
                } else if (className.contains("PackageInstallerActivity")
                        && Build.MANUFACTURER.toLowerCase().equals("vivo")) {
                    // Check that the Continue button finishes rendering every 1 seconds
                    Util.info("Hit page " + className);
                    mHandler.postDelayed(new Runnable() {
                        int count = 0;

                        @Override
                        public void run() {
                            count++;
                            Util.debug("Loop count " + count);
                            boolean successful = handleContinueForOriginOS();
                            if (successful) {
                                mHandler.removeCallbacks(this); // Loop cancel
                            } else if (count < 19) {
                                mHandler.postDelayed(this, 1800); // Loop execution
                            } else {
                                mHandler.removeCallbacks(this);
                                Toast.makeText(getApplicationContext(), getString(R.string.node_not_found, getString(R.string.continue_button)), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, 1800);

                } else if (className.contains("AccountActivity")) {
                    // OPPO's ColorOS V7.2 PERM00_11_A.05_621b26ad
                    // com.android.packageinstaller/.OppoPackageInstallerActivity
                    // com.coloros.safecenter/.verification.login.AccountActivity
                    // com.coloros.safecenter.verification.login.h
                    Util.info("Hit page " + className);
                    handlePasswordForColorOS();
                } else if (className.contains("OppoPackageInstallerActivity")) {
                    // Check that the Installation button finishes rendering every 1 seconds
                    Util.info("Hit page " + className);
                    mHandler.postDelayed(new Runnable() {
                        int count = 0;

                        @Override
                        public void run() {
                            count++;
                            Util.debug("Loop count " + count);
                            boolean successful = handleInstallationForColorOS(); // TODO: Not find installation button
                            if (successful) {
                                mHandler.removeCallbacks(this); // Loop cancel
                            } else if (count < 19) {
                                mHandler.postDelayed(this, 1800); // Loop execution
                            } else {
                                mHandler.removeCallbacks(this);
                                Toast.makeText(getApplicationContext(), getString(R.string.node_not_found, getString(R.string.installation_button)), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, 1800);
                }

                break;
//            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
//                break;
//            case AccessibilityEvent.TYPE_VIEW_CLICKED:
//                break;
        }
    }

    @Override
    public void onInterrupt() {
    }

    private boolean handleInstallationForColorOS() {
        Util.debug("handleInstallationForColorOS");

        AccessibilityNodeInfo root = getRootInActiveWindow();
        List<AccessibilityNodeInfo> nodeInfos = root.findAccessibilityNodeInfosByViewId("android:id/button1");
        if (nodeInfos == null || nodeInfos.size() == 0) {
            Util.error(getString(R.string.node_not_found, getString(R.string.installation_button)));
            return false;
        }
        AccessibilityNodeInfo buttonNode = nodeInfos.get(0);
        buttonNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        buttonNode.recycle();
        return true;
    }

    private void handlePasswordForColorOS() {
        Util.debug("handlePasswordForColorOS");

        String password = Util.getPassword(getApplicationContext());
        if (password != null && password.isEmpty()) {
            Util.error(getString(R.string.password_empty));
            Toast.makeText(getApplicationContext(), R.string.password_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        AccessibilityNodeInfo root = getRootInActiveWindow();

        // Find EditText node
        List<AccessibilityNodeInfo> nodeInfos = root.findAccessibilityNodeInfosByViewId("com.coloros.safecenter:id/et_login_passwd_edit");
        for (AccessibilityNodeInfo nodeInfo : nodeInfos) {
            if (nodeInfo.isEditable()) {
                Bundle arguments = new Bundle();
                arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, password);
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
                nodeInfo.recycle();
            }
        }

        // Find Button node
        nodeInfos = root.findAccessibilityNodeInfosByViewId("android:id/button1");
        for (AccessibilityNodeInfo nodeInfo : nodeInfos) {
            if (nodeInfo.isClickable()) {
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                nodeInfo.recycle();
            }
        }
    }

    private boolean handleContinueForOriginOS() {
        Util.debug("handleContinueForOriginOS");

        AccessibilityNodeInfo root = getRootInActiveWindow();
        // A11yServiceHelper.getInstance().dfsNode(root, 0);

        List<AccessibilityNodeInfo> nodeInfos = root.findAccessibilityNodeInfosByViewId("android:id/button1");
        if (nodeInfos == null || nodeInfos.size() == 0) {
            Util.error(getString(R.string.node_not_found, getString(R.string.continue_button)));
            return false;
        }
        AccessibilityNodeInfo buttonNode = nodeInfos.get(0);
        buttonNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        buttonNode.recycle();
        return true;
    }

    private void handlePasswordForOriginOS() {
        Util.debug("handlePasswordForOriginOS");

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
        editTextNode.recycle();

        // Find Button node and click it.
        nodeInfos = root.findAccessibilityNodeInfosByViewId("android:id/button1");
        if (nodeInfos == null || nodeInfos.size() == 0) {
            Util.error(getString(R.string.node_not_found, getString(R.string.ok_button)));
            Toast.makeText(getApplicationContext(), getString(R.string.node_not_found, getString(R.string.ok_button)), Toast.LENGTH_SHORT).show();
            return;
        }
        AccessibilityNodeInfo buttonNode = nodeInfos.get(0);
        buttonNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        buttonNode.recycle();
    }

    private void handleMIUI() {
        Util.debug("handleMIUI");
        AccessibilityNodeInfo root = getRootInActiveWindow();
        List<AccessibilityNodeInfo> nodeInfos = root.findAccessibilityNodeInfosByViewId("android:id/button2");
        boolean nodeFound = false;
        for (AccessibilityNodeInfo nodeInfo : nodeInfos) {
            if (nodeInfo.isClickable()) {
                nodeFound = true;
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                nodeInfo.recycle();
            }
        }
        if (!nodeFound) {
            Util.error(getString(R.string.node_not_found, "Button"));
            Toast.makeText(getApplicationContext(), getString(R.string.node_not_found, "Button"), Toast.LENGTH_SHORT).show();
        }
    }
}
