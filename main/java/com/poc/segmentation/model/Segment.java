package com.poc.segmentation.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A named segment that holds a list of {@link SegmentField} criteria
 * (e.g. "Segment 1" containing "Delinquency Status", "Escrow Flag", "Loan Age").
 */
public class Segment implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private final List<SegmentField> fields = new ArrayList<>();

    public Segment(String name) {
        this.name = name;
    }

    /** Adds a field only if one with the same name is not already present. */
    public void addField(SegmentField field) {
        if (!containsField(field.getName())) {
            fields.add(field);
        }
    }

    public void removeField(SegmentField field) {
        fields.removeIf(f -> f.getName().equals(field.getName()));
    }

    public boolean containsField(String fieldName) {
        return fields.stream().anyMatch(f -> f.getName().equals(fieldName));
    }

    public String            getName()   { return name; }
    public void              setName(String name) { this.name = name; }
    public List<SegmentField> getFields() { return fields; }
}
