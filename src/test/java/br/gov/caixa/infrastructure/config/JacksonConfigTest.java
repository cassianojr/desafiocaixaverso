package br.gov.caixa.infrastructure.config;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class JacksonConfigTest {

    @Inject
    JacksonConfig jacksonConfig;

    @Test
    @DisplayName("customize deve registrar FilterProvider com nome esperado")
    void customizeRegistraFiltro() {
        ObjectMapper mapper = new ObjectMapper();
        jacksonConfig.customize(mapper);
        FilterProvider fp = mapper.getSerializationConfig().getFilterProvider();
        assertNotNull(fp, "FilterProvider não deveria ser nulo");
        assertNotNull(fp.findPropertyFilter(JacksonConfig.FILTER_NOT_INITIALIZED_FIELDS, null), "Filtro esperado não encontrado");
    }

    @JsonFilter(JacksonConfig.FILTER_NOT_INITIALIZED_FIELDS)
    static class DummyModel {
        public String campo = "valor";
        public String ignorado = "x";
    }

    @Test
    @DisplayName("Filtro configurado deve permitir serialização padrão (serializeAllExcept sem exclusões)")
    void filtroSerializaTodosCampos() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        jacksonConfig.customize(mapper);
        DummyModel dm = new DummyModel();
        String json = mapper.writeValueAsString(dm);
        assertTrue(json.contains("campo"));
        assertTrue(json.contains("ignorado"));
    }

    @Test
    @DisplayName("Constante de nome de filtro permanece estável")
    void constanteFiltro() {
        assertEquals("ignoreNotInitializedFields", JacksonConfig.FILTER_NOT_INITIALIZED_FIELDS);
    }
}
