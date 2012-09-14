package com.machinelinking.enricher;

import com.machinelinking.extractor.CategoryExtractor;
import com.machinelinking.extractor.FreebaseExtractor;
import com.machinelinking.extractor.IssueExtractor;
import com.machinelinking.extractor.LinkExtractor;
import com.machinelinking.extractor.ReferenceExtractor;
import com.machinelinking.extractor.SectionExtractor;
import com.machinelinking.extractor.TemplateMappingExtractor;
import com.machinelinking.extractor.TemplateOccurrencesExtractor;
import com.machinelinking.splitter.InfoboxSplitter;
import com.machinelinking.splitter.TableSplitter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class WikiEnricherFactory {

    public static final String FLAG_SEPARATOR = ",";
    public static final String FLAG_NEGATION  = "-";

    public static final Flag Online     = new DefaultFlag("Online"    , "Enable external services enrichment");
    public static final Flag Validate   = new DefaultFlag("Validate"  , "Validate parser content");
    public static final Flag Extractors = new DefaultFlag("Extractors", "Apply Extractors on content");
    public static final Flag Splitters  = new DefaultFlag("Splitters" , "Apply Splitters on content");
    public static final Flag Structure  = new DefaultFlag("Structure" , "Produces the full WikiText Structure expressed in JSON");

    private static WikiEnricherFactory instance;

    public static WikiEnricherFactory getInstance() {
        if(instance == null) instance = new WikiEnricherFactory();
        return instance;
    }

    private final Map<String,Flag> flagsMap = new HashMap<>();
    private final List<Flag>       flagList = new ArrayList<>();
    private final Flag[]           flags;

    private WikiEnricherFactory() {
        registerFlag(Online);
        registerFlag(Validate);
        registerFlag(Extractors);
        registerFlag(Splitters);
        registerFlag(Structure);
        flags = flagList.toArray( new Flag[flagList.size()] );
        Arrays.sort(flags);
    }

    public Flag[] getDefinedFlags() {
        return flags;
    }

    public Flag getFlagById(String id) {
        final Flag found = flagsMap.get(id);
        if(found == null) throw new IllegalArgumentException( String.format("Cannot find flag [%s].", id) );
        return found;
    }

    public Flag[] toFlags(String flagsStr, Flag[] defaultFlags) {
        if(flagsStr == null || flagsStr.trim().length() == 0) return defaultFlags;
        final String[] flagNames = flagsStr.split(FLAG_SEPARATOR);
        final Set<Flag> flags = new HashSet<>( Arrays.asList(defaultFlags) );
        Flag flag;
        for(String flagName : flagNames) {
            if(flagName.startsWith(FLAG_NEGATION)) {
                flag = getFlagById( flagName.substring(FLAG_NEGATION.length()) );
                flags.remove(flag);
            } else {
                flag = getFlagById(flagName);
                flags.add(flag);

            }
        }
        return flags.toArray( new Flag[flags.size()] );
    }

    public WikiEnricher createFullyConfiguredInstance(Flag... flags) {
        final Set<Flag> flagsSet = new HashSet<>(Arrays.asList(flags));
        final WikiEnricher enricher = new WikiEnricher();

        // Extractors.
        if(flagsSet.contains(Extractors)) {
            enricher.addExtractor(new IssueExtractor());
            enricher.addExtractor(new SectionExtractor());
            enricher.addExtractor(new LinkExtractor());
            enricher.addExtractor(new ReferenceExtractor());
            enricher.addExtractor(new TemplateOccurrencesExtractor());
            enricher.addExtractor(new CategoryExtractor());
            if (flagsSet.contains(Online)) {
                enricher.addExtractor(new TemplateMappingExtractor());
                enricher.addExtractor(new FreebaseExtractor());
            }
        }

        // Splitters.
        if (flagsSet.contains(Splitters)) {
            enricher.addSplitter(new InfoboxSplitter());
            enricher.addSplitter(new TableSplitter());
        }

        enricher.setValidate    ( flagsSet.contains(Validate)  );
        enricher.setProduceStructure(flagsSet.contains(Structure));

        return enricher;
    }

    public WikiEnricher createFullyConfiguredInstance(String flagsStr, Flag[] defaultFlags) {
        return createFullyConfiguredInstance( toFlags(flagsStr, defaultFlags) );
    }

    private void registerFlag(Flag f) {
        flagList.add(f);
        flagsMap.put(f.getId(), f);
    }

}
