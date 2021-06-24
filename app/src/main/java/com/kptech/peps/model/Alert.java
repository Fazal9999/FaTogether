package com.kptech.peps.model;

public class Alert {

    private String alertAbout;
    private String alertFrom;
    private String alertFromPic;
    private String alertPreview;
    private String alertTimeStamp;
    private String alertType;

    public Alert() { }

    public String getAlertAbout() {
        return alertAbout;
    }

    public void setAlertAbout(String alertAbout) {
        this.alertAbout = alertAbout;
    }

    public String getAlertFrom() {
        return alertFrom;
    }

    public void setAlertFrom(String alertFrom) {
        this.alertFrom = alertFrom;
    }

    public String getAlertFromPic() {
        return alertFromPic;
    }

    public void setAlertFromPic(String alertFromPic) {
        this.alertFromPic = alertFromPic;
    }

    public String getAlertPreview() {
        return alertPreview;
    }

    public void setAlertPreview(String alertPreview) {
        this.alertPreview = alertPreview;
    }

    public String getAlertTimeStamp() {
        return alertTimeStamp;
    }

    public void setAlertTimeStamp(String alertTimeStamp) {
        this.alertTimeStamp = alertTimeStamp;
    }

    public String getAlertType() {
        return alertType;
    }

    public void setAlertType(String alertType) {
        this.alertType = alertType;
    }

}
