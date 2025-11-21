package br.gov.caixa.infrastructure.config;

import io.quarkus.test.junit.QuarkusTest;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class ApplicationConfigTest {

    @Test
    void openApiDefinitionAnnotationPresentWithExpectedValues() {
        OpenAPIDefinition definition = ApplicationConfig.class.getAnnotation(OpenAPIDefinition.class);
        assertNotNull(definition, "@OpenAPIDefinition deve estar presente em ApplicationConfig");

        Info info = definition.info();
        assertEquals("API de Investimentos da Caixa", info.title());
        assertEquals("1.0.0", info.version());
        assertTrue(info.description().contains("investimentos"));
    }
}
