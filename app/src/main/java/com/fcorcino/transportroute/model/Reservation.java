package com.fcorcino.transportroute.model;

import java.io.Serializable;
import java.sql.Date;

public class Reservation implements Serializable {
    private String reservationId, stopFromId, stopToId, status, userId, turnId;
    private Date departureDate, checkInDate, payDate;
    private float price, amount;
    private int quantityOfPerson;

    public String getReservationId() {
        return reservationId;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }

    public String getStopFromId() {
        return stopFromId;
    }

    public void setStopFromId(String stopFromId) {
        this.stopFromId = stopFromId;
    }

    public String getStopToId() {
        return stopToId;
    }

    public void setStopToId(String stopToId) {
        this.stopToId = stopToId;
    }

    public int getQuantityOfPerson() {
        return quantityOfPerson;
    }

    public void setQuantityOfPerson(int quantityOfPerson) {
        this.quantityOfPerson = quantityOfPerson;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTurnId() {
        return turnId;
    }

    public void setTurnId(String turnId) {
        this.turnId = turnId;
    }

    public Date getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(Date departureDate) {
        this.departureDate = departureDate;
    }

    public Date getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(Date checkInDate) {
        this.checkInDate = checkInDate;
    }

    public Date getPayDate() {
        return payDate;
    }

    public void setPayDate(Date payDate) {
        this.payDate = payDate;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }
}
