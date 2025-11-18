package br.gov.caixa.infrastructure.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import io.quarkus.jackson.ObjectMapperCustomizer;
import jakarta.inject.Singleton;
import org.hibernate.Hibernate;

@Singleton
public class JacksonConfig implements ObjectMapperCustomizer {

    public static final String FILTER_NOT_INITIALIZED_FIELDS = "ignoreNotInitializedFields";

    @Override
    public void customize(ObjectMapper objectMapper) {
        FilterProvider filters = new SimpleFilterProvider()
            .addFilter(FILTER_NOT_INITIALIZED_FIELDS, new IgnoreInitializedFieldsFilter());
        objectMapper.setFilterProvider(filters);
    }

    static class IgnoreInitializedFieldsFilter extends SimpleBeanPropertyFilter{
        @Override
        public void serializeAsField(Object pojo, JsonGenerator jgen, SerializerProvider provider, PropertyWriter writer) throws Exception {
            if(Hibernate.isPropertyInitialized(pojo, writer.getName())){
                writer.serializeAsField(pojo, jgen, provider);
            }
        }
    }
}
