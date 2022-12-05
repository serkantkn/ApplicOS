package com.serkantken.applicos.models;

import java.io.Serializable;

public class AppModel implements Serializable
{
    String appName, packageName;
    int appIcon;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(int appIcon) {
        this.appIcon = appIcon;
    }
}
