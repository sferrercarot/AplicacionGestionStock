package com.example.aplicaciongestionstockimprenta;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OdooUser {
    private int id;

    @SerializedName("groups_id")
    private List<List<Object>> groupsId;

    public int getId() {
        return id;
    }

    public List<List<Object>> getGroupsId() {
        return groupsId;
    }
}