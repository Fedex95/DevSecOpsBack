package com.tienda.kpback;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;

@OpenAPIDefinition(
    info = @Info(
        title = "Kingdom PC API",
        version = "1.0",
        description = "API para la aplicación Library Master"
    )
)
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT"
)
@SpringBootApplication
public class KpbackApplication {
    public static void main(String[] args) {
        SpringApplication.run(KpbackApplication.class, args);
    }

}
