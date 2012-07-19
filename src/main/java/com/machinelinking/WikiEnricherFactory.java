package com.machinelinking;

import com.machinelinking.extractor.FreebaseExtractor;
import com.machinelinking.extractor.IssueExtractor;
import com.machinelinking.extractor.LinkExtractor;
import com.machinelinking.extractor.ReferenceExtractor;
import com.machinelinking.extractor.SectionExtractor;
import com.machinelinking.extractor.TemplateMappingExtractor;
import com.machinelinking.extractor.TemplateNameExtractor;
import com.machinelinking.splitter.InfoboxSplitter;
import com.machinelinking.splitter.TableSplitter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class WikiEnricherFactory {

    public enum Flag {
        Offline {
            @Override
            public String description() {
                return "Prevent lookup external service enrichment.";
            }
        },
        Validate {
            @Override
            public String description() {
                return "Validate parser content";
            }
        },
        Extractors {
            @Override
            public String description() {
                return "Apply Extractors on content";
            }
        },
        Splitters {
            @Override
            public String description() {
                return "Apply Splitters on content";
            }
        },
        Structure {
            @Override
            public String description() {
                return "Produces the full WikiText Structure expressed in JSON";
            }
        };

        public abstract String description();
    }

    private static WikiEnricherFactory instance;

    public static WikiEnricherFactory getInstance() {
        if(instance == null) instance = new WikiEnricherFactory();
        return instance;
    }

    private WikiEnricherFactory() {}

    public WikiEnricher createFullyConfiguredInstance(Flag... flags) {
        final Set<Flag> flagsSet = new HashSet<>(Arrays.asList(flags));
        final WikiEnricher enricher = new WikiEnricher();

        // Extractors.
        if(flagsSet.contains(Flag.Extractors)) {
            enricher.addExtractor(new IssueExtractor());
            enricher.addExtractor(new SectionExtractor());
            enricher.addExtractor(new LinkExtractor());
            enricher.addExtractor(new ReferenceExtractor());
            enricher.addExtractor(new TemplateNameExtractor());
            if (flagsSet.contains(Flag.Offline)) {
                enricher.addExtractor(new TemplateMappingExtractor());
                enricher.addExtractor(new FreebaseExtractor());
            }
        }

        // Splitters.
        if (flagsSet.contains(Flag.Splitters)) {
            enricher.addSplitter(new InfoboxSplitter());
            enricher.addSplitter(new TableSplitter());
        }

        enricher.setValidate    ( flagsSet.contains(Flag.Validate)  );
        enricher.setProduceStructure(flagsSet.contains(Flag.Structure));

        return enricher;
    }

}
