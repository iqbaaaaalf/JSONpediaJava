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

package com.machinelinking.pipeline;

import com.machinelinking.extractor.AbstractExtractor;
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
 * Factory for {@link WikiPipeline} instances.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class WikiPipelineFactory {

    public static final String FLAG_SEPARATOR = ",";
    public static final String FLAG_NEGATION  = "-";

    public static final Flag Extractors = new DefaultFlag("Extractors"      , "Enable all available data extractors");
    public static final Flag Linkers    = new DefaultFlag("Linkers"         ,
            "Enable linking of current entity to external resources(Freebase, DBpedia)"
    );
    public static final Flag Splitters  = new DefaultFlag("Splitters"       , "Enable all available data splitters");
    public static final Flag Structure  = new DefaultFlag("Structure"       , "Produces the full WikiText DOM");
    public static final Flag Validate   = new DefaultFlag("Validate"        , "Validate the WikiText structure");

    public static final Flag[] DEFAULT_FLAGS = new Flag[] {Extractors};

    private static WikiPipelineFactory instance;

    public static WikiPipelineFactory getInstance() {
        if(instance == null) instance = new WikiPipelineFactory();
        return instance;
    }

    private final Map<String,Flag> flagsMap = new HashMap<>();
    private final List<Flag>       flagList = new ArrayList<>();
    private final Flag[]           flags;

    private WikiPipelineFactory() {
        registerFlag(Linkers);
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

    /**
     * Converts a flag string to a list of {@link Flag} having care of the default {@link Flag}s.
     *
     * @param flagsStr
     * @param defaultFlags
     * @return a new configured instance.
     */
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

    /**
     * Converts a flag string to a list of {@link Flag} having care of the default {#DEFAULT_FLAGS}.
     *
     * @param flagsStr
     * @return a new configured instance.
     */
    public Flag[] toFlags(String flagsStr) {
        return toFlags(flagsStr, DEFAULT_FLAGS);
    }

    /**
     * Creates a {@link WikiPipeline} based on the given list of {@link Flag}s.
     *
     * @param flags
     * @return a new configured instance.
     */
    public WikiPipeline createFullyConfiguredInstance(Flag... flags) {
        final Set<Flag> flagsSet = new HashSet<>(Arrays.asList(flags));
        final WikiPipeline enricher = new WikiPipeline();

        // Issue extractor is always active.
        enricher.addExtractor(new IssueExtractor());

        // Extractors.
        if(flagsSet.contains(Extractors)) {
            enricher.addExtractor(new AbstractExtractor());
            enricher.addExtractor(new SectionExtractor());
            enricher.addExtractor(new LinkExtractor());
            enricher.addExtractor(new ReferenceExtractor());
            enricher.addExtractor(new TemplateOccurrencesExtractor());
            enricher.addExtractor(new CategoryExtractor());
            if (flagsSet.contains(Linkers)) {
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

    public WikiPipeline createFullyConfiguredInstance(String flagsStr, Flag[] defaultFlags) {
        return createFullyConfiguredInstance( toFlags(flagsStr, defaultFlags) );
    }

    private void registerFlag(Flag f) {
        flagList.add(f);
        flagsMap.put(f.getId(), f);
    }

}
