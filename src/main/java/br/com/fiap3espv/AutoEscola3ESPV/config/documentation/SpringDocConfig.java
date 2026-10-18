package br.com.fiap3espv.AutoEscola3ESPV.config.documentation;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.*;
import org.springframework.context.annotation.*;
@Configuration
public class SpringDocConfig {
    @Bean public OpenAPI customOpenAPI() {
        return new OpenAPI().info(new Info().title("Autoescola Checkpoint 5").version("1.0.0")
            .description("CRUD de alunos, instrutores, usuários e instruções. Faça POST /login e informe o token em Authorize. " +
                "ADMIN gerencia cadastros; USER consulta alunos/instrutores e gerencia a agenda. Datas no fuso America/Sao_Paulo."))
            .components(new Components().addSecuritySchemes("bearer-key", new SecurityScheme()
                .type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
            .addSecurityItem(new SecurityRequirement().addList("bearer-key"));
    }
}
