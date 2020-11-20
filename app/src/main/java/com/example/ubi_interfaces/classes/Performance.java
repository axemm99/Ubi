package com.example.ubi_interfaces.classes;

import com.google.firebase.Timestamp;

import java.util.Date;
import java.util.List;


public class Performance {

    // Por o set e get mais simplificados
    private Timestamp date;
    private Boolean reqPass;
    private int totalParticipants;
    private String location;
    private String password;
    private String adminId;
    private int duration;
    private List<String> participantsId;
    private String id;
    private String picture;
//    private boolean active;

    public Performance (Timestamp date, boolean reqPass, int totalParticipants, String location, String picture, String password, List<String> participantsId) {
        this.date = date;
        this.reqPass = reqPass;
        this.totalParticipants = totalParticipants;
        this.location = location;
        this.picture = picture;
        this.password = password;
        this.participantsId = participantsId;
    }


    public Performance () {

    }

    // Setters
    public void setDate(Timestamp date) {
        this.date = date;
    }

    public void setReqPass(Boolean reqPass) {
        this.reqPass = reqPass;
    }

//    public void setTotalParticipants(int totalParticipants) {
//        this.totalParticipants = totalParticipants;
//    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setParticipants(String[] participants) {
        for(int i = 0; i< participants.length; i++) {
            this.participantsId.add(participants[i]);
        }
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPicture(String picture) { this.picture = picture; }

    public void addParticipantId(String id) {
        if(Integer.parseInt(id) > 0) {
            this.participantsId.add(id);
        }
    }


    // Getters
    public String getLocation() {
        return location;
    }

    public String getPassword() {
        return password;
    }

    public String getAdminId() {
        return adminId;
    }

    public int getDuration() {
        return duration;
    }

    public List<String> getParticipantsId() {
        return participantsId;
    }

    public String getId() {
        return id;
    }


    public Date getDate() { return date.toDate(); }

    public Boolean getReqPass() {
        if(this.password.equals("")) this.reqPass = false;
        else this.reqPass = true;

        return this.reqPass;
    }

    public int getTotalParticipants() {
        return this.totalParticipants;
    }


    public String getPicture() { return picture; }

}
