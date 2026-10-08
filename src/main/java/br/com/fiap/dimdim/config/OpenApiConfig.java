package br.com.fiap.dimdim.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI dimDimOpenApi() {
        return new OpenAPI().info(new Info()
                .title("DimDim API")
                .version("1.0.0")
                .description("CRUD de Clientes e Pagamentos persistidos no Azure SQL Database. "
                        + "Datas retornadas em UTC."));
    }
}
