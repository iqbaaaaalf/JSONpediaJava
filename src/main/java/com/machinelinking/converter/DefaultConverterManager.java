package com.machinelinking.converter;

import com.machinelinking.filter.JSONObjectFilter;
import com.machinelinking.pagestruct.PageStructConsts;
import org.codehaus.jackson.JsonNode;

import java.util.HashMap;
import java.util.HashSet;
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
        final String type = data.get(PageStructConsts.TYPE_FIELD).asText();
        if(type == null) throw new IllegalArgumentException(
            String.format("data object must define a %s field", PageStructConsts.TYPE_FIELD)
        );
        final Set<FilterToConverter> filtersToConverters = typeToFilters.get(type);
        if(filtersToConverters == null) return null;
        for(FilterToConverter filterToConverter : filtersToConverters) {
            if(filterToConverter.filter.match(data)) {
                return filterToConverter.converter;
            }
        }
        return null;
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
