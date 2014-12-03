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

/**
 * Provides generic String utility methods.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class StringUtils {

    public static String md5bytesToHex(byte[] md5) {
        StringBuilder sb = new StringBuilder();
        for (byte aMd5 : md5) {
            sb.append(Integer.toString((aMd5 & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

    public static String stripTags(String in) {
        char c;
        int insideTag = 0;
        final StringBuilder out = new StringBuilder();
        for(int i = 0; i < in.length(); i++) {
            c = in.charAt(i);
            if(c == '<') {
                insideTag++;
            } else if(c == '>') {
                insideTag--;
            }
            else if(insideTag == 0) {
                out.append(c);
            }
        }
        return out.toString();
    }

    private StringUtils() {}

}
