package com.pmattioli.diffresolver.api.model;

import java.util.Objects;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class DiffApiResponse {

    private Integer length;
    private String message;
    private String offsets;

    public DiffApiResponse(){}

    public DiffApiResponse(Integer length, String offsets) {
        this.length = length;
        this.offsets = offsets;
    }

    public DiffApiResponse(String message) {
        this.message = message;
    }

    public Integer getLength() {
        return length;
    }

    public String getMessage() {
        return message;
    }

    public String getOffsets() {
        return offsets;
    }

    @Override
    public boolean equals(Object o) {

        if (o == this) return true;
        if (!(o instanceof DiffApiResponse)) {
            return false;
        }
        DiffApiResponse diffResult = (DiffApiResponse) o;
        return Objects.equals(this.length, diffResult.length) &&
                Objects.equals(this.message, diffResult.message) &&
                Objects.equals(this.offsets, diffResult.offsets);
    }

    @Override
    public int hashCode() {
        return Objects.hash(length, message, offsets);
    }

    @Override public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
