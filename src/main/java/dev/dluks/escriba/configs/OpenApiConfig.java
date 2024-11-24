package dev.dluks.escriba.configs;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${server.port}")
    private String serverPort;

    @Bean
    public OpenAPI escribaOpenAPI() {
        Server devServer = new Server()
                .url("http://localhost:" + serverPort)
                .description("Development server");

        Contact contact = new Contact()
                .name("Diogo Oliveira")
                .email("dluks82@gmail.com")
                .url("https://dluks.dev");

        Info info = new Info()
                .title("Escriba API")
                .description("API para gerenciamento de cartórios")
                .version("1.0.0")
                .contact(contact)
                .license(new License()
                        .name("Apache 2.0")
                        .url("https://www.apache.org/licenses/LICENSE-2.0"));

        return new OpenAPI()
                .servers(List.of(devServer))
                .info(info);

    }
}
