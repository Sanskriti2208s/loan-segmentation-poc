package com.poc.segmentation.model;

import java.io.Serializable;

/**
 * Represents a single selectable field shown in the "Add to Selection" left
 * panel (e.g. "Delinquency Status", "Loan Age").
 */
public class SegmentField implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String name;
    /** True when this field has already been added to at least one segment. */
    private boolean inSegment;

    public SegmentField(String name) {
        this.name      = name;
        this.inSegment = false;
    }

    public String  getName()      { return name; }
    public boolean isInSegment()  { return inSegment; }
    public void    setInSegment(boolean inSegment) { this.inSegment = inSegment; }
}
