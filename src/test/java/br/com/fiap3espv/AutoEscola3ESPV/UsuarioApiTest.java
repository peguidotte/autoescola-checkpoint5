package br.com.fiap3espv.AutoEscola3ESPV;

import br.com.fiap3espv.AutoEscola3ESPV.shared.vo.enumeration.Papel;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithMockUser(roles = "ADMIN")
class UsuarioApiTest extends BaseApiTest {
    @Test void crudCompletoSemExporSenha() throws Exception {
        var result = mvc.perform(body(post("/usuario"), Map.of("login", "novo", "senha", "Senha@123", "papel", "USER")))
            .andExpect(status().isCreated()).andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.senha").doesNotExist()).andReturn();
        long id = payload(result).get("id").asLong();
        assertThat(encoder.matches("Senha@123", usuarios.findById(id).orElseThrow().getSenha())).isTrue();
        mvc.perform(get("/usuario/{id}", id)).andExpect(status().isOk()).andExpect(jsonPath("$.login").value("novo"));
        mvc.perform(get("/usuario")).andExpect(status().isOk()).andExpect(jsonPath("$.content[0].senha").doesNotExist());
        mvc.perform(body(put("/usuario"), Map.of("id", id, "login", "atualizado", "senha", "NovaSenha@123")))
            .andExpect(status().isOk()).andExpect(jsonPath("$.login").value("atualizado"));
        assertThat(encoder.matches("NovaSenha@123", usuarios.findById(id).orElseThrow().getSenha())).isTrue();
        mvc.perform(delete("/usuario/{id}", id)).andExpect(status().isNoContent());
        assertThat(usuarios.findById(id).orElseThrow().isAtivo()).isFalse();
        mvc.perform(get("/usuario")).andExpect(jsonPath("$.totalElements").value(0));
    }
    @Test void rejeitaSenhaCurta() throws Exception {
        mvc.perform(body(post("/usuario"), Map.of("login", "novo", "senha", "123", "papel", "USER"))).andExpect(status().isBadRequest());
    }
    @Test void rejeitaLoginDuplicado() throws Exception {
        var usuario = usuario(Papel.USER);
        mvc.perform(body(post("/usuario"), Map.of("login", usuario.getLogin(), "senha", "Senha@123", "papel", "USER"))).andExpect(status().isConflict());
    }
    @Test void idsInexistentesRetornam404() throws Exception {
        mvc.perform(get("/usuario/999999")).andExpect(status().isNotFound());
        mvc.perform(body(put("/usuario"), Map.of("id", 999999, "login", "nome"))).andExpect(status().isNotFound());
        mvc.perform(delete("/usuario/999999")).andExpect(status().isNotFound());
    }
    @Test void preservaUltimoAdministrador() throws Exception {
        var admin = usuario(Papel.ADMIN);
        mvc.perform(delete("/usuario/{id}", admin.getId())).andExpect(status().isBadRequest());
        mvc.perform(body(put("/usuario"), Map.of("id", admin.getId(), "papel", "USER"))).andExpect(status().isBadRequest());
    }
    @Test @WithMockUser(roles = "USER") void usuarioComumNaoGerenciaUsuarios() throws Exception {
        mvc.perform(get("/usuario")).andExpect(status().isForbidden());
    }
}
