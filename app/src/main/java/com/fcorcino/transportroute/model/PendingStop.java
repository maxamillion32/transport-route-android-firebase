package com.fcorcino.transportroute.model;

import java.io.Serializable;

public class PendingStop implements Serializable {
    private static final long serialVersionUID = 1219941358681722474L;
    private String stopName, stopType;
    private int peopleWaiting;

    public String getStopName() {
        return stopName;
    }

    public void setStopName(String stopName) {
        this.stopName = stopName;
    }

    public int getPeopleWaiting() {
        return peopleWaiting;
    }

    public void setPeopleWaiting(int peopleWaiting) {
        this.peopleWaiting = peopleWaiting;
    }

    public String getStopType() {
        return stopType;
    }

    public void setStopType(String stopType) {
        this.stopType = stopType;
    }
}
