package br.gov.caixa.infrastructure.config;

import jakarta.ws.rs.core.Application;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                title = "API de Investimentos da Caixa",
                version = "1.0.0",
                description = "API para consulta de investimentos, simulações, produtos recomendados e telemetria."
        )
)
public class ApplicationConfig extends Application {
}
