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

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.IValueValidator;
import com.beust.jcommander.ParameterException;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Resources utility for <b>CLI</b> classes.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class CLIUtils {

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

    public static class ValidURL implements IValueValidator<String> {
        public void validate(String name, String url)
        throws ParameterException {
            try {
                new URL(url);
            } catch (MalformedURLException murle) {
                throw new ParameterException( String.format("Invalid URL [%s]", url) );
            }
        }
    }

    public static class ValidHost implements IValueValidator<String> {
        public void validate(String name, String host)
        throws ParameterException {
            try {
                new URL(String.format("http://%s/", host));
            } catch (MalformedURLException murle) {
                throw new ParameterException( String.format("Invalid host [%s]", host) );
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

    public static class PortValidator implements IValueValidator<Integer> {
        public void validate(String name, Integer v)
        throws ParameterException {
            if(v < 0 || v > 65535) throw new ParameterException(
                    "Invalid port, must be >= 0 && <= 65535"
            );
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
