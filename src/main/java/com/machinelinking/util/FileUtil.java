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

package com.machinelinking.util;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

/**
 * File utility functions.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class FileUtil {

    public static final String FILE_PREFIX = "file://";

    public static String getExtension(String path) {
        final int extIndex = path.lastIndexOf(".");
        return extIndex != -1 ? path.substring(extIndex + 1) : null;
    }

    public static InputStream openDecompressedInputStream(String resource) throws IOException {
        final InputStream resourceInputStream;
        if (resource.startsWith(FILE_PREFIX)) {
            resourceInputStream = new FileInputStream(resource.substring(FILE_PREFIX.length()));
        } else {
            resourceInputStream = FileUtil.class.getResourceAsStream(resource);
        }
        final String ext = getExtension(resource);
        return openDecompressedInputStream(resourceInputStream, ext);
    }

    public static InputStream openDecompressedInputStream(File file) throws IOException {
        final String ext = getExtension(file.getPath());
        return openDecompressedInputStream( new FileInputStream(file), ext );
    }

    public static BufferedInputStream openDecompressedInputStream(InputStream is, String ext) throws IOException {
        final InputStream decompressInputStream;
        switch (ext) {
            case "gz":
                decompressInputStream = new GZIPInputStream(is);
                break;
            case "bz2":
                decompressInputStream = new BZip2CompressorInputStream(is);
                break;
            default:
                throw new IllegalArgumentException("Unsupported extension: " + ext);
        }
        return new BufferedInputStream(decompressInputStream);
    }

    private FileUtil(){}

}
