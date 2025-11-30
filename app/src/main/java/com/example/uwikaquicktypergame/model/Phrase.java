package com.example.uwikaquicktypergame.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable; // Penting untuk mengirim objek antar activity

public class Phrase implements Serializable {
    @SerializedName("id")
    private String id;

    @SerializedName("text")
    private String text;

    @SerializedName("sequence_number")
    private int sequenceNumber;

    @SerializedName("multiplier") // Nama di JSON adalah multiplier
    private float multiplier;

    // Getters
    public String getId() { return id; }
    public String getText() { return text; }
    public int getSequenceNumber() { return sequenceNumber; }
    public float getMultiplier() { return multiplier; }
}
