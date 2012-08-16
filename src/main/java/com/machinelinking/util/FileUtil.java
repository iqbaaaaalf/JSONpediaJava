package com.machinelinking.util;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class FileUtil {

    public static final String FILE_PREFIX = "file://";

    public static InputStream openResource(String resource) throws IOException {
        final int extIndex = resource.lastIndexOf(".");
        final String ext = resource.substring(extIndex + 1);
        final InputStream resourceInputStream;
        if (resource.startsWith(FILE_PREFIX)) {
            resourceInputStream = new FileInputStream(resource.substring(FILE_PREFIX.length()));
        } else {
            resourceInputStream = FileUtil.class.getResourceAsStream(resource);
        }
        InputStream decompressInputStream;
        switch (ext) {
            case "gz":
                decompressInputStream = new GZIPInputStream(resourceInputStream);
                break;
            case "bz2":
                decompressInputStream = new BZip2CompressorInputStream(resourceInputStream);
                break;
            default:
                throw new IllegalArgumentException("Unsupported extension: " + ext);
        }
        return new BufferedInputStream(decompressInputStream);
    }

    private FileUtil(){}

}
