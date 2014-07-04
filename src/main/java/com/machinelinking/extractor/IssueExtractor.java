package com.machinelinking.extractor;

import com.machinelinking.pagestruct.PageStructConsts;
import com.machinelinking.parser.ParserLocation;
import com.machinelinking.serializer.Serializer;

import java.util.ArrayList;
import java.util.List;

/**
 * This {@link Extractor} collects issues during extraction.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class IssueExtractor extends Extractor {

    private List<Issue> issues = null;

    public IssueExtractor() {
        super(PageStructConsts.ISSUES_FIELD);
    }

    @Override
    public void parseWarning(String msg, ParserLocation location) {
        if(issues == null) issues = new ArrayList<Issue>();
        issues.add( new Issue(Issue.Type.Warning, msg, location) );
    }

    @Override
    public void parseError(Exception e, ParserLocation location) {
        if(issues == null) issues = new ArrayList<Issue>();
        issues.add( new Issue(Issue.Type.Warning, e.getMessage(), location) );
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
