package org.anuran.model;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class Test {

    private String data;

    public Test() {
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
