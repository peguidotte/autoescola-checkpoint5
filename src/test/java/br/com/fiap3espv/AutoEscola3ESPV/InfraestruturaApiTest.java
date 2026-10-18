package br.com.fiap3espv.AutoEscola3ESPV;

import br.com.fiap3espv.AutoEscola3ESPV.shared.vo.enumeration.Papel;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import java.util.Map;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class InfraestruturaApiTest extends BaseApiTest {
    @Test void swaggerEOpenApiPublicosDocumentamTodasAsEntidades() throws Exception {
        mvc.perform(get("/swagger-ui/index.html")).andExpect(status().isOk());
        mvc.perform(get("/v3/api-docs")).andExpect(status().isOk())
            .andExpect(jsonPath("$.paths['/aluno'].post").exists()).andExpect(jsonPath("$.paths['/instrutor'].get").exists())
            .andExpect(jsonPath("$.paths['/usuario'].put").exists()).andExpect(jsonPath("$.paths['/instrucao/{id}'].delete").exists())
            .andExpect(jsonPath("$.paths['/endereco/cep/{cep}'].get").exists())
            .andExpect(jsonPath("$.components.securitySchemes['bearer-key'].scheme").value("bearer"));
        mvc.perform(get("/v3/api-docs.yaml")).andExpect(status().isOk());
    }
    @Test void healthPublicoERecursoProtegido() throws Exception {
        mvc.perform(get("/health-check")).andExpect(status().isOk());
        mvc.perform(get("/aluno")).andExpect(status().isUnauthorized());
    }
    @Test void corsPermitePreflightComAuthorizationEContentType() throws Exception {
        mvc.perform(options("/instrutor").header("Origin", "http://localhost:5173")
            .header("Access-Control-Request-Method", "POST").header("Access-Control-Request-Headers", "Authorization,Content-Type"))
            .andExpect(status().isOk()).andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"))
            .andExpect(header().string("Access-Control-Allow-Headers", "Authorization, Content-Type"));
    }
    @Test void corsBloqueiaOrigemDesconhecida() throws Exception {
        mvc.perform(options("/instrutor").header("Origin", "https://nao-permitida.example")
            .header("Access-Control-Request-Method", "POST")).andExpect(status().isForbidden());
    }
    @Test @WithMockUser void corsExpoeLocationNaRespostaReal() throws Exception {
        mvc.perform(get("/instrutor").header("Origin", "http://127.0.0.1:5500"))
            .andExpect(status().isOk()).andExpect(header().string("Access-Control-Expose-Headers", "Location"));
    }
    @Test void loginGeraJwtQueAutorizaAcessoReal() throws Exception {
        var user = usuario(Papel.ADMIN);
        var response = mvc.perform(body(post("/login"), Map.of("login", user.getLogin(), "senha", "Senha@123")))
            .andExpect(status().isOk()).andReturn();
        String token = payload(response).get("tokenJWT").asText();
        mvc.perform(get("/usuario").header("Authorization", "Bearer " + token)).andExpect(status().isOk());
        user.excluir(); usuarios.flush();
        mvc.perform(get("/usuario").header("Authorization", "Bearer " + token)).andExpect(status().isUnauthorized());
    }
    @Test void senhaIncorretaEUsuarioInexistenteRetornam401() throws Exception {
        var user = usuario(Papel.USER);
        mvc.perform(body(post("/login"), Map.of("login", user.getLogin(), "senha", "errada"))).andExpect(status().isUnauthorized());
        mvc.perform(body(post("/login"), Map.of("login", "inexistente", "senha", "errada"))).andExpect(status().isUnauthorized());
    }
    @Test void tokenMalformadoNaoViraErro500() throws Exception {
        mvc.perform(get("/instrutor").header("Authorization", "Bearer invalido")).andExpect(status().isUnauthorized());
        mvc.perform(get("/instrutor").header("Authorization", "Basic invalido")).andExpect(status().isUnauthorized());
    }
}
