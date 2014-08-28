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
import com.machinelinking.exporter.CSVExporterReport;
import com.machinelinking.exporter.DefaultCSVExporter;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;

/**
 * Commandline utility for {@link com.machinelinking.exporter.CSVExporter}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class exporter {

    @Parameter(
            names = {"--prefix", "-p"},
            description = "Page prefix",
            converter = CLIUtils.URLConverter.class,
            required = true
    )
    private URL prefix;

    @Parameter(
            names = {"--in", "-i"},
            description = "Input file",
            converter = CLIUtils.FileConverter.class,
            required = true,
            validateValueWith = CLIUtils.ExistingFile.class
    )
    private File in;

    @Parameter(
            names = {"--out", "-o"},
            description = "Output file",
            required = true,
            converter = CLIUtils.FileConverter.class
    )
    private File out;

    @Parameter(
            names = {"--threads", "-t"},
            description = "Number of threads",
            validateValueWith = CLIUtils.NumOfThreadsValidator.class
    )
    private int numOfThreads = 0; // Num of threads based on CPU cores.

    public static void main(String[] args) {
        exporter cli = new exporter();
        System.exit( cli.run(args) );
    }

    public int run(String[] args) {
        final JCommander commander = new JCommander(this);
        int exitCode;
        try {
            commander.parse(args);
            final DefaultCSVExporter exporter = new DefaultCSVExporter();
            exporter.setThreads(numOfThreads);
            final CSVExporterReport report = exporter.export(prefix, in, out);
            System.out.println(report);
            exitCode = 0;
        } catch (ParameterException pe) {
            System.err.println(pe.getMessage());
            commander.usage();
            exitCode = 1;
        } catch (FileNotFoundException fnfe) {
            System.err.println("Cannot find input file.");
            exitCode = 2;
        } catch (Exception e) {
            System.err.println("Error while exporting CSV.");
            e.printStackTrace();
            exitCode = 3;
        }
        return exitCode;
    }

}