package com.machinelinking.extractor;

import com.machinelinking.serializer.Serializer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class IssueExtractor extends Extractor {

    private List<Issue> issues = null;

    public IssueExtractor() {
        super("issues");
    }

    @Override
    public void parseWarning(String msg, int row, int col) {
        if(issues == null) issues = new ArrayList<Issue>();
        issues.add( new Issue(Issue.Type.Warning, msg, row, col) );
    }

    @Override
    public void parseError(Exception e, int row, int col) {
        if(issues == null) issues = new ArrayList<Issue>();
        issues.add( new Issue(Issue.Type.Warning, e.getMessage(), row, col) );
    }

    @Override
    public void flushContent(Serializer serializer) {
        if(issues == null) {
            serializer.value(null);
            return;
        }
        serializer.openList();
        for(Issue issue : issues) {
            issue.serialize(serializer);
        }
        serializer.closeList();
        issues.clear();
    }

    @Override
    public void reset() {
        if(issues != null) issues.clear();
    }
}
