package com.example.pkginstalla11yservice;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.pm.ServiceInfo;
import android.util.Log;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

public class A11yServiceHelper {

    private static final A11yServiceHelper instance = new A11yServiceHelper();

    private A11yServiceHelper() {
    }

    public static A11yServiceHelper getInstance() {
        return instance;
    }

    public boolean isEnabled(Context context, Class<? extends AccessibilityService> serviceClass) {
        AccessibilityManager manager = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> enabledServices = manager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK);

        for (AccessibilityServiceInfo enabledService : enabledServices) {
            ServiceInfo enabledServiceInfo = enabledService.getResolveInfo().serviceInfo;
            if (enabledServiceInfo.packageName.equals(context.getPackageName()) && enabledServiceInfo.name.equals(serviceClass.getName()))
                return true;
        }

        return false;
    }

    public AccessibilityNodeInfo findNodeByClassName(AccessibilityNodeInfo nodeInfo, String... classNames) {
        if (nodeInfo == null) {
            return null;
        }
        if (nodeInfo.getChildCount() == 0) {
            return null;
        }
        for (int i = 0; i < nodeInfo.getChildCount(); i++) {
            AccessibilityNodeInfo childNodeInfo = nodeInfo.getChild(i);
            for (String className : classNames) {
                if (childNodeInfo.getClassName().toString().contains(className)) {
                    return childNodeInfo;
                }
            }
            AccessibilityNodeInfo switchOrCheckBoxNodeInfo = findNodeByClassName(childNodeInfo, classNames);
            if (switchOrCheckBoxNodeInfo != null) {
                return switchOrCheckBoxNodeInfo;
            }
        }
        return null;
    }

    public AccessibilityNodeInfo findNodeByText(AccessibilityNodeInfo nodeInfo, String... texts) {
        if (nodeInfo == null) {
            return null;
        }
        if (nodeInfo.getChildCount() == 0) {
            return null;
        }
        for (int i = 0; i < nodeInfo.getChildCount(); i++) {
            AccessibilityNodeInfo childNodeInfo = nodeInfo.getChild(i);
            String childText = childNodeInfo.getText() == null ? "" : childNodeInfo.getText().toString();
            if (!childText.isEmpty()) {
                for (String text : texts) {
                    if (childText.contains(text)) {
                        return childNodeInfo;
                    }
                }
            }
            AccessibilityNodeInfo targetNodeInfo = findNodeByText(childNodeInfo, texts);
            if (targetNodeInfo != null) {
                return targetNodeInfo;
            }
        }
        return null;
    }

    public void dfsNode(AccessibilityNodeInfo node, int num) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < num; i++) {
            builder.append("__ "); // Indentation between parent and child nodes
        }
        builder.append(num);
        Log.i("dfsNode", "AccessibilityNodeInfo = " + builder.toString() + node.toString());
        for (int i = 0; i < node.getChildCount(); i++) { // Traverse child nodes
            dfsNode(node.getChild(i), num + 1);
        }
    }

}
