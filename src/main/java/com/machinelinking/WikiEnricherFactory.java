package com.machinelinking;

import com.machinelinking.extractor.FreebaseExtractor;
import com.machinelinking.extractor.IssueExtractor;
import com.machinelinking.extractor.LinkExtractor;
import com.machinelinking.extractor.ReferenceExtractor;
import com.machinelinking.extractor.SectionExtractor;
import com.machinelinking.extractor.TemplateMappingExtractor;
import com.machinelinking.splitter.InfoboxSplitter;
import com.machinelinking.splitter.TableSplitter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class WikiEnricherFactory {

    public static String ONLINE_FLAG     = "online";
    public static String VALIDATE_FLAG   = "validate";
    public static String EXTRACTORS_FLAG = "extractors";
    public static String SPLITTERS_FLAG  = "splitters";

    private static WikiEnricherFactory instance;

    public static WikiEnricherFactory getInstance() {
        if(instance == null) instance = new WikiEnricherFactory();
        return instance;
    }

    private WikiEnricherFactory() {}

    public WikiEnricher createFullyConfiguredInstance(String... flags) {
        final Set<String> flagsSet = new HashSet<>(Arrays.asList(flags));
        final WikiEnricher enricher = new WikiEnricher();

        // Extractors.
        if(flagsSet.contains(EXTRACTORS_FLAG)) {
            enricher.addExtractor(new IssueExtractor());
            enricher.addExtractor(new SectionExtractor());
            enricher.addExtractor(new LinkExtractor());
            enricher.addExtractor(new ReferenceExtractor());
            if (flagsSet.contains(ONLINE_FLAG)) {
                enricher.addExtractor(new TemplateMappingExtractor());
                enricher.addExtractor(new FreebaseExtractor());
            }
        }

        // Splitters.
        if (flagsSet.contains(SPLITTERS_FLAG)) {
            enricher.addSplitter(new InfoboxSplitter());
            enricher.addSplitter(new TableSplitter());
        }

        enricher.setValidate( flagsSet.contains(VALIDATE_FLAG) );

        return enricher;
    }

}
