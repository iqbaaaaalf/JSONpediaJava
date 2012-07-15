package com.machinelinking.parser;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class WikiPediaUtils {

    public static final String INFOBOX_TEMPLATE_NAME = "infobox";

    public static String getInfoBoxName(String templateName) {
        final String[] parts = templateName.split("\\s");
        return parts.length >= 2 && INFOBOX_TEMPLATE_NAME.equalsIgnoreCase(parts[0]) ? parts[0] : null;
    }

    private WikiPediaUtils() {}

}
