package com.machinelinking.render;

import org.codehaus.jackson.JsonNode;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class ReferenceNodeRender implements NodeRender {

    public static final int DEFAULT_IMAGE_WIDTH = 110;

    public static final String[] IMAGE_EXT = new String[] {"jpg", "png"};

    private static final String FILE_PREFIX = "File:";

    private static final String ALT_PREFIX = "alt=";

    private static final Map<String,String> REFERENCE_DIV_ATTR = new HashMap<String,String>(){{
        put("style", "background-color: #C971AE");
    }};

    @Override
    public boolean acceptNode(JsonContext context, JsonNode node) {
        return true;
    }

    @Override
    public void render(JsonContext context, RootRender rootRender, JsonNode node, HTMLWriter writer)
    throws IOException {
        final String label       = node.get("label").asText();
        final String description = node.get("content").asText().trim();
        final String[] labelSections = label.split(".");
        if(labelSections.length == 2) {
            writeHTMLURL(
                    String.format("http://%s.wikipedia.org/wiki/%s", labelSections[0], labelSections[1]),
                    description,
                    writer
            );
        } else if(labelSections.length == 0) {
            writeHTMLURL(
                    String.format("http://en.wikipedia.org/wiki/%s", label),
                    description.length() == 0 ? label : description,
                    writer
            );
        } else {
            throw new IllegalArgumentException("Invalid label: " + label);
        }
    }

    private void writeHTMLURL(String target, String label, HTMLWriter writer) throws IOException {
         writer.openTag("div", REFERENCE_DIV_ATTR);
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

    private boolean isImage(String url) {
        final String urlLower = url.toLowerCase();
        for(String ext : IMAGE_EXT) {
            if(urlLower.endsWith("." + ext)) {
                return true;
            }
        }
        return false;
    }

    private String imageURLToResource(String fileImg) {
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
        final String digest =  md5bytesToHex( messageDigest.digest() );
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

    private String md5bytesToHex(byte[] md5) {
        StringBuilder sb = new StringBuilder();
        for (byte aMd5 : md5) {
            sb.append(Integer.toString((aMd5 & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

}
