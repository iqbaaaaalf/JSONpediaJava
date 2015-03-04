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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class allows to retrieve fields hidden in classes not directly exposed by public methods.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class Probe {

    private static final Probe instance = new Probe();

    public static Probe getInstance() {
        return instance;
    }

    private final Map<String,Field[]> accessors = new HashMap<>();

    private Probe() {}

    public synchronized <C> C probePath(Object root, String path) {
        final String[] pathlist = path.split("\\.");
        final Field[] fields = accessors.get(objectPathToId(root, pathlist));
        if(fields != null) {
            return (C) follow(root, fields);
        } else {
            return (C) followAndCache(root, pathlist);
        }
    }

    private Object follow(Object root, Field[] fields){
        Object curr = root;
        for(Field field : fields) {
            try {
                curr = field.get(curr);
            } catch (IllegalAccessException iae) {
                throw new IllegalStateException();
            }
        }
        return curr;
    }

    private Object followAndCache(Object root, String[] path) {
        Field field;
        Object curr = root;
        List<Field> fields = new ArrayList<>();
        for (String part : path) {
            try {
                try {
                    field = curr.getClass().getDeclaredField(part);
                } catch (NoSuchFieldException nfe) {
                    field = curr.getClass().getSuperclass().getDeclaredField(part);
                }
                if(field == null) throw new NoSuchFieldException();
                field.setAccessible(true);
                fields.add(field);
                curr = field.get(curr);
            } catch (NoSuchFieldException nfe) {
                throw new IllegalArgumentException(
                        String.format("Cannot find field %s in path %s", part, Arrays.toString(path))
                );
            } catch (IllegalAccessException iae) {
                throw new IllegalStateException(iae);
            }
        }
        accessors.put(objectPathToId(root, path), fields.toArray(new Field[fields.size()]));
        return curr;
    }

    private String objectPathToId(Object o, String[] path) {
        return String.format("%s-%s", o.getClass(), Arrays.toString(path));
    }

}
