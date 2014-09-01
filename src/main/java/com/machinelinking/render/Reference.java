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

package com.machinelinking.render;

import com.machinelinking.util.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class to manage <i>WikiMedia</i> references.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class Reference {

    public static final int DEFAULT_IMAGE_WIDTH = 110;

    public static final String[] IMAGE_EXT = new String[] {"jpg", "png"};

    private static final String FILE_PREFIX = "File:";

    private static final String ALT_PREFIX = "alt=";

    private static final Map<String,String> REFERENCE_NODE_ATTR = new HashMap<String,String>(){{
        put("class", "reference");
    }};

    public static boolean isImage(String url) {
        final String urlLower = url.toLowerCase();
        for(String ext : IMAGE_EXT) {
            if(urlLower.endsWith("." + ext)) {
                return true;
            }
        }
        return false;
    }

    public static String imageURLToResource(String fileImg) {
        final int fileStart = fileImg.lastIndexOf('/') + 1;
        final String file = fileImg.substring(fileStart + FILE_PREFIX.length());
        final MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException nsae) {
            throw new IllegalStateException(nsae);
        }

        final String filename = file.replaceAll(" ", "_");
        messageDigest.update(filename.getBytes());
        final String digest = StringUtils.md5bytesToHex( messageDigest.digest() );
        final String urlEncFile;
        final String folder;
        try {
            urlEncFile = URLEncoder.encode(filename, "utf-8");
            folder = String.format(
                    "%c/%c%c/%s/%dpx-%s",
                    digest.charAt(0),
                    digest.charAt(0), digest.charAt(1),
                    urlEncFile,
                    DEFAULT_IMAGE_WIDTH,
                    urlEncFile
            );
        } catch (UnsupportedEncodingException uee) {
            throw new IllegalStateException(uee);
        }

        final String location = folder.endsWith(".png") ? "en" : "commons";
        return String.format("http://upload.wikimedia.org/wikipedia/%s/thumb/%s", location, folder);
    }

    public static void writeReferenceHTML(String target, String label, HTMLWriter writer) throws IOException {
         writer.openTag("span", REFERENCE_NODE_ATTR);
        if( isImage(target) ) {
            final String[] descSections = label.split("\\|");
            writer.key(descSections[descSections.length -1]);
            String alt = "";
            for(String descSection : descSections) {
                if(descSection.startsWith(ALT_PREFIX)) {
                    alt = descSection.substring(ALT_PREFIX.length());
                    break;
                }
            }
            writer.image(imageURLToResource(target), alt);
        } else {
            writer.anchor(target, label, true);
        }
        writer.closeTag();
    }

    private Reference() {}

}
