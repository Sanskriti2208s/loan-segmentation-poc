package com.poc.segmentation.model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Represents a single row in the Report Preview data table.
 *
 * <p>Three row types are used to render the hierarchical layout seen in the
 * reference UI (interest-rate group → delinquency sub-group → detail row):
 * <pre>
 *  INTEREST_GROUP      3.00–4.50           961   363,098,387.00
 *  DELINQUENCY_SUBGROUP            30 Days+  18     5,959,948.00
 *  DETAIL                    &gt;0.00          18     5,959,948.00
 * </pre>
 */
public class LoanData implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum RowType {
        INTEREST_GROUP,
        DELINQUENCY_SUBGROUP,
        DETAIL
    }

    private final RowType     rowType;
    private final String      interestRateRange;
    private final String      delinquencyDesc;
    private final String      escrowFilter;
    private final Integer     activeLoanCount;
    private final BigDecimal  currentUPB;

    public LoanData(RowType rowType,
                    String interestRateRange,
                    String delinquencyDesc,
                    String escrowFilter,
                    Integer activeLoanCount,
                    BigDecimal currentUPB) {
        this.rowType          = rowType;
        this.interestRateRange = interestRateRange;
        this.delinquencyDesc  = delinquencyDesc;
        this.escrowFilter     = escrowFilter;
        this.activeLoanCount  = activeLoanCount;
        this.currentUPB       = currentUPB;
    }

    // ---------------------------------------------------------------
    // Boolean helpers used in rendered="#{row.interestGroup}" EL
    // ---------------------------------------------------------------

    public boolean isInterestGroup() {
        return rowType == RowType.INTEREST_GROUP;
    }

    public boolean isDelinquencySubgroup() {
        return rowType == RowType.DELINQUENCY_SUBGROUP;
    }

    public boolean isDetail() {
        return rowType == RowType.DETAIL;
    }

    /**
     * Returns a CSS class name applied to each &lt;tr&gt; via
     * p:dataTable rowStyleClass="#{row.cssClass}".
     */
    public String getCssClass() {
        switch (rowType) {
            case INTEREST_GROUP:      return "row-interest-group";
            case DELINQUENCY_SUBGROUP: return "row-delinquency";
            case DETAIL:              return "row-detail";
            default:                  return "";
        }
    }

    // ---------------------------------------------------------------
    // Getters
    // ---------------------------------------------------------------

    public RowType    getRowType()           { return rowType; }
    public String     getInterestRateRange() { return interestRateRange; }
    public String     getDelinquencyDesc()   { return delinquencyDesc; }
    public String     getEscrowFilter()      { return escrowFilter; }
    public Integer    getActiveLoanCount()   { return activeLoanCount; }
    public BigDecimal getCurrentUPB()        { return currentUPB; }
}
