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

package com.machinelinking.cli;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.machinelinking.storage.elasticsearch.ElasticJSONStorage;
import com.machinelinking.storage.elasticsearch.ElasticJSONStorageFactory;
import com.machinelinking.storage.elasticsearch.ElasticSelector;
import com.machinelinking.storage.elasticsearch.faceting.DefaultElasticFacetConfiguration;
import com.machinelinking.storage.elasticsearch.faceting.DefaultElasticFacetManager;
import com.machinelinking.storage.elasticsearch.faceting.ElasticFacetManager;
import com.machinelinking.storage.elasticsearch.faceting.EnrichedEntityFacetConverter;
import com.machinelinking.storage.elasticsearch.faceting.FacetLoadingReport;

import java.io.File;

/**
 * CLI interface to run the {@link com.machinelinking.storage.elasticsearch.faceting.DefaultElasticFacetManager}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class facetloader {

    @Parameter(
            names = {"--source", "-s"},
            description = "Source storage URI",
            required = true
    )
    private String source;

    @Parameter(
            names = {"--destination", "-d"},
            description = "Destination storage URI",
            required = true
    )
    private String destination;

    @Parameter(
            names = {"--limit", "-l"},
            description = "Document limit",
            required = true
    )
    private int limit;

    @Parameter(
            names = {"--conf", "-c"},
            description = "Faceting config",
            validateValueWith = CLIUtils.ExistingFile.class,
            required = true
    )
    private File conf;

    public static void main(String[] args) {
        facetloader cli = new facetloader();
        System.exit( cli.run(args) );
    }

    public int run(String[] args) {
          final JCommander commander = new JCommander(this);
          int exitCode;
          try {
              commander.parse(args);
              final ElasticJSONStorageFactory factory = new ElasticJSONStorageFactory();
              final ElasticJSONStorage fromStorage = factory.createStorage(factory.createConfiguration(source));
              final ElasticJSONStorage facetStorage = factory.createStorage(factory.createConfiguration(destination));
              facetStorage.deleteCollection();
              final DefaultElasticFacetConfiguration configuration = new DefaultElasticFacetConfiguration(
                      conf,
                      limit,
                      fromStorage,
                      facetStorage
              );
              final ElasticFacetManager manager = new DefaultElasticFacetManager(configuration);
              final ElasticSelector selector = new ElasticSelector();
              final FacetLoadingReport report = manager.loadFacets(selector, new EnrichedEntityFacetConverter());
              System.out.println(report);
              exitCode = 0;
          } catch (ParameterException pe) {
              System.err.println(pe.getMessage());
              commander.usage();
              exitCode = 1;
          } catch (Exception e) {
              System.err.println("Error while loading faceting index.");
              e.printStackTrace();
              exitCode = 3;
          }
          return exitCode;
      }


}
