package com.example.uwikaquicktypergame.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class StageDetail implements Serializable {
    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("difficulty")
    private String difficulty;

    @SerializedName("phrases")
    private List<Phrase> phrases;

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDifficulty() { return difficulty; }
    public List<Phrase> getPhrases() { return phrases; }
}
