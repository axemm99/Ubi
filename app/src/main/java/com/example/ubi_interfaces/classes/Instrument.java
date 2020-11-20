package com.example.ubi_interfaces.classes;

public class Instrument {

    private int instrumentId;
    private String name;
    private String sound;
    private String image;

    public Instrument() {

    }

    public Instrument(int instrumentId, String name, String sound, String image) {

        this.instrumentId = instrumentId;
        this.name = name;
        this.sound = sound;
        this.image = image;
    }

    /* getters */
    public int getInstrumentId() {
        return this.instrumentId;
    }
    public String getName() {
        return this.name;
    }
    public String getSound() {
        return this.sound;
    }
    public String getImage() {
        return this.image;
    }
}
