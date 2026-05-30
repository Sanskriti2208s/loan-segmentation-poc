package com.poc.segmentation.bean;

import com.poc.segmentation.model.LoanData;
import com.poc.segmentation.model.LoanData.RowType;
import com.poc.segmentation.model.Segment;
import com.poc.segmentation.model.SegmentField;
import com.poc.segmentation.model.SegmentGroup;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * CDI view-scoped backing bean for the Loan Segmentation page.
 *
 * <p>Responsibilities:
 * <ul>
 *   <li>Owns the list of available fields shown in the left panel.</li>
 *   <li>Manages segment groups / segments in the middle panel.</li>
 *   <li>Provides pre-built sample report data for the right panel table.</li>
 *   <li>Handles all AJAX actions (add / remove fields, add segment, etc.).</li>
 * </ul>
 */
@Named("segmentationBean")
@ViewScoped
public class SegmentationBean implements Serializable {

    private static final long serialVersionUID = 1L;

    // ------------------------------------------------------------------ //
    //  Navigation / tab state                                              //
    // ------------------------------------------------------------------ //

    /** Toggles between "Fields" and "Segments" sub-view in the left panel. */
    private boolean showFields = true;

    // ------------------------------------------------------------------ //
    //  Dataset selectors                                                   //
    // ------------------------------------------------------------------ //

    private String       selectedDataset     = "Dataset 1";
    private List<String> datasets;

    private String       selectedDatasetFile = "DATACOI3.csv";
    private List<String> datasetFiles;

    private LocalDate    cutOffDate;

    // ------------------------------------------------------------------ //
    //  Left panel – available fields                                       //
    // ------------------------------------------------------------------ //

    private List<SegmentField> allFields;
    private String             fieldSearchTerm = "";

    // ------------------------------------------------------------------ //
    //  Middle panel – segment groups                                       //
    // ------------------------------------------------------------------ //

    private List<SegmentGroup> segmentGroups;
    private String             selectedGroupName = "Segment Group 1";

    /** The segment that receives fields when the user clicks in the left panel. */
    private Segment activeSegment;

    // ------------------------------------------------------------------ //
    //  Report options                                                      //
    // ------------------------------------------------------------------ //

    private boolean includeEmptySegments = true;
    private boolean autoRefresh          = true;

    // ------------------------------------------------------------------ //
    //  Report data                                                         //
    // ------------------------------------------------------------------ //

    private List<LoanData> reportData;

    // ================================================================== //
    //  Initialisation                                                      //
    // ================================================================== //

    @PostConstruct
    public void init() {
        initDatasets();
        initFields();
        initSegmentGroups();
        initReportData();
    }

    private void initDatasets() {
        datasets      = Arrays.asList("Dataset 1", "Dataset 2", "Dataset 3");
        datasetFiles  = Arrays.asList("DATACOI3.csv", "DATACOI2.csv", "DATACOI1.csv");
        cutOffDate    = LocalDate.of(2024, 7, 31);
    }

    private void initFields() {
        allFields = new ArrayList<>();
        String[] names = {
            "As of Date",        "Delinquency Status", "Escrow Flag",
            "Guarantor",         "Loan Age",           "Occupancy",
            "Original Term",     "Program Type",       "Property Type",
            "Servicing Fee",     "State",              "Current Interest Rate"
        };
        for (String name : names) {
            allFields.add(new SegmentField(name));
        }
    }

    private void initSegmentGroups() {
        segmentGroups = new ArrayList<>();

        // --- Segment Group 1 (pre-populated to match the reference UI) ---
        SegmentGroup group1 = new SegmentGroup("Segment Group 1");

        Segment seg1 = new Segment("Segment 1");
        seg1.addField(new SegmentField("Delinquency Status"));
        seg1.addField(new SegmentField("Escrow Flag"));
        seg1.addField(new SegmentField("Loan Age"));

        Segment seg2 = new Segment("Segment 2");
        seg2.addField(new SegmentField("Property Type"));
        seg2.addField(new SegmentField("State"));

        group1.addSegment(seg1);
        group1.addSegment(seg2);
        segmentGroups.add(group1);

        // --- Segment Group 2 (empty – for demonstration) ---
        segmentGroups.add(new SegmentGroup("Segment Group 2"));

        // Segment 1 is the default active target
        activeSegment = seg1;

        // Sync the "inSegment" highlights on the left-panel field list
        syncFieldInSegmentFlags();
    }

    /**
     * Builds a flat list of {@link LoanData} rows that represent the
     * hierarchical report structure (group → sub-group → detail).
     */
    private void initReportData() {
        reportData = new ArrayList<>();

        // ── Interest range 3.00–4.50 ──────────────────────────────────────
        add(RowType.INTEREST_GROUP,      "3.00–4.50", null,        null,   961,  "363098387.00");
        add(RowType.DELINQUENCY_SUBGROUP,"3.00–4.50", "30 Days+",  null,    18,    "5959948.00");
        add(RowType.DETAIL,              "3.00–4.50", "30 Days+",  ">0.00", 18,    "5959948.00");
        add(RowType.DELINQUENCY_SUBGROUP,"3.00–4.50", "60 Days+",  null,    12,    "4137723.00");
        add(RowType.DETAIL,              "3.00–4.50", "60 Days+",  ">0.00", 12,    "4137723.00");
        add(RowType.DELINQUENCY_SUBGROUP,"3.00–4.50", "90 Days+",  null,     1,     "293172.00");
        add(RowType.DETAIL,              "3.00–4.50", "90 Days+",  ">0.00",  1,     "293172.00");
        add(RowType.DELINQUENCY_SUBGROUP,"3.00–4.50", "Current",   null,   930,  "352707544.00");
        add(RowType.DETAIL,              "3.00–4.50", "Current",   ">0.00",930,  "352707544.00");

        // ── Interest range 4.51–5.50 ──────────────────────────────────────
        add(RowType.INTEREST_GROUP,      "4.51–5.50", null,        null,   742,  "218654923.00");
        add(RowType.DELINQUENCY_SUBGROUP,"4.51–5.50", "30 Days+",  null,    24,    "8421556.00");
        add(RowType.DETAIL,              "4.51–5.50", "30 Days+",  ">0.00", 24,    "8421556.00");
        add(RowType.DELINQUENCY_SUBGROUP,"4.51–5.50", "60 Days+",  null,     8,    "2893410.00");
        add(RowType.DETAIL,              "4.51–5.50", "60 Days+",  ">0.00",  8,    "2893410.00");
        add(RowType.DELINQUENCY_SUBGROUP,"4.51–5.50", "Current",   null,   710,  "207339957.00");
        add(RowType.DETAIL,              "4.51–5.50", "Current",   ">0.00",710,  "207339957.00");

        // ── Interest range 5.51–6.50 ──────────────────────────────────────
        add(RowType.INTEREST_GROUP,      "5.51–6.50", null,        null,   385,   "92156342.00");
        add(RowType.DELINQUENCY_SUBGROUP,"5.51–6.50", "30 Days+",  null,    11,    "3102887.00");
        add(RowType.DETAIL,              "5.51–6.50", "30 Days+",  ">0.00", 11,    "3102887.00");
        add(RowType.DELINQUENCY_SUBGROUP,"5.51–6.50", "Current",   null,   374,   "89053455.00");
        add(RowType.DETAIL,              "5.51–6.50", "Current",   ">0.00",374,   "89053455.00");
    }

    /** Convenience builder to keep initReportData() readable. */
    private void add(RowType type, String rate, String delinq,
                     String escrow, int count, String upb) {
        reportData.add(new LoanData(type, rate, delinq, escrow,
                                    count, new BigDecimal(upb)));
    }

    // ================================================================== //
    //  Left-panel actions                                                  //
    // ================================================================== //

    public void showFieldsView()    { showFields = true;  }
    public void showSegmentsView()  { showFields = false; }

    /**
     * Adds the clicked field to {@link #activeSegment}.
     * If the field is already present in that segment a warning toast is shown.
     */
    public void addFieldToActiveSegment(SegmentField field) {
        if (activeSegment == null) {
            addMessage(FacesMessage.SEVERITY_WARN,
                       "No active segment",
                       "Click 'Search for field to add' inside a segment first.");
            return;
        }
        if (activeSegment.containsField(field.getName())) {
            addMessage(FacesMessage.SEVERITY_INFO,
                       "Already added",
                       "\"" + field.getName() + "\" is already in " + activeSegment.getName() + ".");
            return;
        }
        activeSegment.addField(new SegmentField(field.getName()));
        syncFieldInSegmentFlags();
        addMessage(FacesMessage.SEVERITY_INFO,
                   "Field added",
                   "\"" + field.getName() + "\" → " + activeSegment.getName());
    }

    // ================================================================== //
    //  Middle-panel actions                                               //
    // ================================================================== //

    public void removeFieldFromSegment(Segment segment, SegmentField field) {
        segment.removeField(field);
        syncFieldInSegmentFlags();
        // Keep activeSegment valid
        if (activeSegment != null && !getActiveGroup().getSegments().contains(activeSegment)) {
            activeSegment = null;
        }
        addMessage(FacesMessage.SEVERITY_INFO,
                   "Field removed",
                   "\"" + field.getName() + "\" removed from " + segment.getName() + ".");
    }

    /** Sets which segment receives newly clicked fields from the left panel. */
    public void setActiveSegment(Segment segment) {
        this.activeSegment = segment;
        addMessage(FacesMessage.SEVERITY_INFO,
                   "Active segment",
                   segment.getName() + " is now the target. Click a field on the left to add it.");
    }

    public void addSegment() {
        SegmentGroup group = getActiveGroup();
        if (group == null) { return; }
        int nextIndex = group.getSegments().size() + 1;
        Segment newSeg = new Segment("Segment " + nextIndex);
        group.addSegment(newSeg);
        activeSegment = newSeg;
        addMessage(FacesMessage.SEVERITY_INFO, "Segment added",
                   newSeg.getName() + " added to " + group.getName() + ".");
    }

    public void clearAllSegments() {
        SegmentGroup group = getActiveGroup();
        if (group == null) { return; }
        group.getSegments().clear();
        activeSegment = null;
        syncFieldInSegmentFlags();
        addMessage(FacesMessage.SEVERITY_WARN, "Cleared",
                   "All segments removed from " + group.getName() + ".");
    }

    /** Placeholder – in production this would stream a CSV/Excel response. */
    public void downloadReport() {
        addMessage(FacesMessage.SEVERITY_INFO, "Download",
                   "Report download triggered (implement FacesContext response streaming).");
    }

    // ================================================================== //
    //  Helpers                                                             //
    // ================================================================== //

    /**
     * Returns the currently selected {@link SegmentGroup}, or the first group
     * if none matches.
     */
    public SegmentGroup getActiveGroup() {
        return segmentGroups.stream()
                            .filter(g -> g.getName().equals(selectedGroupName))
                            .findFirst()
                            .orElse(segmentGroups.isEmpty() ? null : segmentGroups.get(0));
    }

    public List<String> getSegmentGroupNames() {
        return segmentGroups.stream()
                            .map(SegmentGroup::getName)
                            .collect(Collectors.toList());
    }

    /**
     * Returns the subset of {@link #allFields} whose name contains
     * {@link #fieldSearchTerm} (case-insensitive).  Returns all fields when
     * the search term is blank.
     */
    public List<SegmentField> getFilteredFields() {
        if (fieldSearchTerm == null || fieldSearchTerm.isBlank()) {
            return allFields;
        }
        String term = fieldSearchTerm.strip().toLowerCase();
        return allFields.stream()
                        .filter(f -> f.getName().toLowerCase().contains(term))
                        .collect(Collectors.toList());
    }

    /**
     * Scans every segment in every group and marks each entry in
     * {@link #allFields} as in-use (highlighted in the left panel).
     */
    private void syncFieldInSegmentFlags() {
        Set<String> used = new HashSet<>();
        for (SegmentGroup g : segmentGroups) {
            for (Segment s : g.getSegments()) {
                for (SegmentField f : s.getFields()) {
                    used.add(f.getName());
                }
            }
        }
        allFields.forEach(f -> f.setInSegment(used.contains(f.getName())));
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(severity, summary, detail));
    }

    // ================================================================== //
    //  Getters / setters                                                   //
    // ================================================================== //

    public boolean isShowFields()                        { return showFields; }
    public void    setShowFields(boolean v)              { showFields = v; }

    public String       getSelectedDataset()             { return selectedDataset; }
    public void         setSelectedDataset(String v)     { selectedDataset = v; }
    public List<String> getDatasets()                    { return datasets; }

    public String       getSelectedDatasetFile()         { return selectedDatasetFile; }
    public void         setSelectedDatasetFile(String v) { selectedDatasetFile = v; }
    public List<String> getDatasetFiles()                { return datasetFiles; }

    public LocalDate getCutOffDate()                     { return cutOffDate; }
    public void      setCutOffDate(LocalDate v)          { cutOffDate = v; }

    public String getFieldSearchTerm()                   { return fieldSearchTerm; }
    public void   setFieldSearchTerm(String v)           { fieldSearchTerm = v; }

    public List<SegmentGroup> getSegmentGroups()         { return segmentGroups; }

    public String getSelectedGroupName()                 { return selectedGroupName; }
    public void   setSelectedGroupName(String v)         { selectedGroupName = v; }

    public Segment getActiveSegment()                    { return activeSegment; }

    public boolean isIncludeEmptySegments()              { return includeEmptySegments; }
    public void    setIncludeEmptySegments(boolean v)    { includeEmptySegments = v; }

    public boolean isAutoRefresh()                       { return autoRefresh; }
    public void    setAutoRefresh(boolean v)             { autoRefresh = v; }

    public List<LoanData> getReportData()                { return reportData; }
}
