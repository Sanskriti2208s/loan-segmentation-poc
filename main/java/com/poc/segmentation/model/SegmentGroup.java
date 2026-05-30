package com.poc.segmentation.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A named group that contains one or more {@link Segment} instances.
 * Corresponds to the "Segment Group 1" dropdown in the middle panel.
 */
public class SegmentGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private final List<Segment> segments = new ArrayList<>();

    public SegmentGroup(String name) {
        this.name = name;
    }

    public void addSegment(Segment segment) {
        segments.add(segment);
    }

    public String        getName()     { return name; }
    public void          setName(String name) { this.name = name; }
    public List<Segment> getSegments() { return segments; }
}
