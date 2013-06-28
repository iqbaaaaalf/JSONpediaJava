package com.machinelinking.exporter;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.IValueValidator;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Commandline utility for {@link CSVExporter}.
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class CSVExporterCLI {

    @Parameter(
            names = {"--prefix", "-p"},
            description = "Page prefix",
            converter = URLConverter.class,
            required = true
    )
    private URL prefix;

    @Parameter(
            names = {"--in", "-i"},
            description = "Input file",
            converter = FileConverter.class,
            required = true,
            validateValueWith = ExistingFile.class
    )
    private File in;

    @Parameter(
            names = {"--out", "-o"},
            description = "Output file",
            required = true,
            converter = FileConverter.class
    )
    private File out;

    @Parameter(
            names = {"--threads", "-t"},
            description = "Number of threads",
            validateValueWith = NumOfThreadsValidator.class
    )
    private int numOfThreads = 0; // Num of threads based on CPU cores.

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
            System.out.println(pe.getMessage());
            commander.usage();
            exitCode = 1;
        } catch (FileNotFoundException fnfe) {
            throw new IllegalStateException(fnfe);
        } catch (Exception e) {
            throw new RuntimeException("Error while exporting CSV.", e);
        }
        return exitCode;
    }

    public static void main(String[] args) {
        CSVExporterCLI cli = new CSVExporterCLI();
        System.exit( cli.run(args) );
    }

    public static class FileConverter implements IStringConverter<File> {
        @Override
        public File convert(String value) {
            return new File(value);
        }
    }

    public static class URLConverter implements IStringConverter<URL> {
        @Override
        public URL convert(String value) {
            try {
                return new URL(value);
            } catch (MalformedURLException murle) {
                throw new ParameterException( String.format("Invalid prefix [%s] : must be a URL.", value) );
            }
        }
    }

    public static class ExistingFile implements IValueValidator<File> {
        public void validate(String name, File f)
        throws ParameterException {
            if (! f.exists()) {
                throw new ParameterException( String.format("File [%s] must exist.", f.getAbsolutePath()) );
            }
        }
    }

    public static class NumOfThreadsValidator implements IValueValidator<Integer> {
        public void validate(String name, Integer v)
        throws ParameterException {
            if(v < 0) throw new ParameterException(
                    "Invalid number of threads, must be >= 0. (0 => NumOfThreads == # CPU Cores)"
            );
        }
    }

}
