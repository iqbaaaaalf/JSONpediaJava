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

package com.machinelinking.converter;

import com.machinelinking.filter.JSONObjectFilter;
import com.machinelinking.pagestruct.PageStructConsts;
import com.machinelinking.serializer.Serializer;
import com.machinelinking.util.JSONUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Default implementation for {@link com.machinelinking.converter.ConverterManager}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class DefaultConverterManager implements ConverterManager {

    private Map<String, Set<FilterToConverter>> typeToFilters = new HashMap<>();

    @Override
    public boolean addConverter(JSONObjectFilter filter, Converter converter) {
        final String type = getFilterTypeOrFail(filter);
        Set<FilterToConverter> filtersToConverters = typeToFilters.get(type);
        if(filtersToConverters == null) {
            filtersToConverters = new HashSet<>();
            typeToFilters.put(type, filtersToConverters);
        }
        return filtersToConverters.add(new FilterToConverter(filter, converter));
    }

    @Override
    public boolean removeConverter(JSONObjectFilter filter) {
        final String type = getFilterTypeOrFail(filter);
        Set<FilterToConverter> filtersToConverters = typeToFilters.get(type);
        return filtersToConverters != null && filtersToConverters.remove(new FilterToConverter(filter, null));
    }

    @Override
    public Converter getConverterForData(JsonNode data) {
        final JsonNode typeNode = data.get(PageStructConsts.TYPE_FIELD);
        if(typeNode == null) return null;
        final String type = typeNode.asText();
        final Set<FilterToConverter> filtersToConverters = typeToFilters.get(type);
        if(filtersToConverters == null) return null;
        for(FilterToConverter filterToConverter : filtersToConverters) {
            if(filterToConverter.filter.match(data)) {
                return filterToConverter.converter;
            }
        }
        return null;
    }

    @Override
    public void process(JsonNode data, Serializer serializer, Writer writer) throws ConverterException {
        try {
            visit(data, serializer, writer);
        } catch (Exception e) {
            throw new ConverterException("Error while processing data.", e);
        }
    }

    private String getFilterTypeOrFail(JSONObjectFilter filter) {
        final String type = filter.getCriteriaPattern(PageStructConsts.TYPE_FIELD);
        if(type == null)
            throw new IllegalArgumentException(
                    String.format(
                            "Invalid filter, must specify a %s match criteria",
                            PageStructConsts.TYPE_FIELD
                    )
            );
        return type;
    }

    private void visit(JsonNode node, Serializer serializer, Writer writer) throws IOException, ConverterException {
        if(node.isObject()) {
            visit((ObjectNode) node, serializer, writer);
        } else if(node.isArray()) {
            visit((ArrayNode) node, serializer, writer);
        }
    }

    private void visit(ObjectNode obj, Serializer serializer, Writer writer) throws IOException, ConverterException {
        final Converter converter = getConverterForData(obj);
        if(converter != null) {
            final Map<String,?> dataMap = JSONUtils.convertNodeToMap(obj);
            converter.convertData(dataMap, serializer, writer);
        }

        final Iterator<Map.Entry<String,JsonNode>> iter = obj.getFields();
        Map.Entry<String,JsonNode> entry;
        while(iter.hasNext()) {
            entry = iter.next();
            visit(entry.getValue(), serializer, writer);
        }
    }

    private void visit(ArrayNode arr, Serializer serializer, Writer writer) throws IOException, ConverterException {
        for(int i = 0; i < arr.size(); i++) {
            visit( arr.get(i), serializer, writer );
        }
    }

    class FilterToConverter {
        private final JSONObjectFilter filter;
        private final Converter converter;
        FilterToConverter(JSONObjectFilter filter, Converter converter) {
            if(filter == null) throw new NullPointerException();
            this.filter = filter;
            this.converter = converter;
        }

        @Override
        public boolean equals(Object obj) {
            if(obj == this) return true;
            if(obj == null) return false;
            final FilterToConverter other = (FilterToConverter) obj;
            return filter.equals(other.filter);
        }

        @Override
        public int hashCode() {
            return filter.hashCode();
        }
    }

}
