package com.example.shubham.void_coders;

/**
 * Created by shubham on 29/12/16.
 */

public class Patient {
    String name;
    String Disease;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisease() {
        return Disease;
    }

    public void setDisease(String disease) {
        Disease = disease;
    }

    public Patient(String name, String disease) {
        this.name = name;
        Disease = disease;
    }
}
