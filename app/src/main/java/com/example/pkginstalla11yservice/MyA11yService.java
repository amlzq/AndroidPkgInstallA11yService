package com.example.pkginstalla11yservice;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

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

                if (className.contains("AdbInstallActivity")) {
                    // com.miui.permcenter.install.AdbInstallActivity MIUI
                    handleMIUI();
                } else {

                }

                break;
            case AccessibilityEvent.TYPE_VIEW_CLICKED:

                break;
        }
    }

    @Override
    public void onInterrupt() {

    }

    private void handleMIUI() {
        AccessibilityNodeInfo root = getRootInActiveWindow();
        List<AccessibilityNodeInfo> nodeInfos = root.findAccessibilityNodeInfosByViewId("android:id/button2");
        Util.print("nodeInfos size = " + nodeInfos.size());
        for (AccessibilityNodeInfo nodeInfo : nodeInfos) {
            if (nodeInfo.isClickable()) {
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }
    }
}
