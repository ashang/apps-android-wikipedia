package org.wikipedia.editing.summaries;

import java.util.*;

public class EditSummary {
    public static final EditSummaryPersistanceHelper persistanceHelper = new EditSummaryPersistanceHelper();
    private final String summary;
    private final Date lastUsed;

    public EditSummary(String summary, Date lastUsed) {
        this.summary = summary;
        this.lastUsed = lastUsed;
    }

    public String getSummary() {
        return summary;
    }

    public Date getLastUsed() {
        return lastUsed;
    }
}
