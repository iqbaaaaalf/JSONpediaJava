/*
 * JSONpedia - Convert any MediaWiki document to JSON.
 *
 * Written in 2014 by Michele Mostarda <mostarda@fbk.eu>.
 *
 * To the extent possible under law, the author has dedicated all copyright and related and
 * neighboring rights to this software to the public domain worldwide.
 * This software is distributed without any warranty.
 *
 * You should have received a copy of the CC BY Creative Commons Attribution 4.0 Internationa Public License.
 * If not, see <https://creativecommons.org/licenses/by/4.0/legalcode>.
 */

package com.machinelinking.extractor;

import com.machinelinking.pagestruct.Ontology;
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
        super(Ontology.ISSUES_FIELD);
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
