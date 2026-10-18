package br.com.fiap3espv.AutoEscola3ESPV;

import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithMockUser(roles = "ADMIN")
class InstrutorApiTest extends BaseApiTest {
    @Test void crudCompletoComExclusaoLogica() throws Exception {
        var result = mvc.perform(body(post("/instrutor"), Map.of("nome", "Instrutora Márcia", "telefone", "11999999999", "email", "marcia@teste.com", "cnh", "12345678901", "especialidade", "CARROS", "endereco", enderecoJson())))
            .andExpect(status().isCreated()).andExpect(header().exists("Location")).andReturn();
        long id = payload(result).get("id").asLong();
        mvc.perform(get("/instrutor/{id}", id)).andExpect(status().isOk()).andExpect(jsonPath("$.nome").value("Instrutora Márcia"));
        mvc.perform(get("/instrutor")).andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(1));
        mvc.perform(body(put("/instrutor"), Map.of("id", id, "nome", "Márcia atualizada"))).andExpect(status().isOk()).andExpect(jsonPath("$.nome").value("Márcia atualizada"));
        mvc.perform(delete("/instrutor/{id}", id)).andExpect(status().isNoContent());
        assertThat(instrutores.findById(id).orElseThrow().isAtivo()).isFalse();
        mvc.perform(get("/instrutor")).andExpect(jsonPath("$.totalElements").value(0));
    }
    @Test void enderecoObrigatorioEValidacaoDeCnh() throws Exception {
        mvc.perform(body(post("/instrutor"), Map.of("nome", "Teste", "telefone", "11999999999", "email", "teste@teste.com", "cnh", "1", "especialidade", "CARROS"))).andExpect(status().isBadRequest());
    }
    @Test void cnhOuEmailDuplicadoRetorna409() throws Exception {
        var instrutor = instrutor();
        mvc.perform(body(post("/instrutor"), Map.of("nome", "Outro", "telefone", "11999999999", "email", instrutor.getEmail(), "cnh", instrutor.getCnh(), "especialidade", "CARROS", "endereco", enderecoJson()))).andExpect(status().isConflict());
    }
    @Test void idsInexistentesRetornam404() throws Exception {
        mvc.perform(get("/instrutor/999999")).andExpect(status().isNotFound());
        mvc.perform(body(put("/instrutor"), Map.of("id", 999999, "nome", "Nome"))).andExpect(status().isNotFound());
        mvc.perform(delete("/instrutor/999999")).andExpect(status().isNotFound());
    }
    @Test @WithMockUser(roles = "USER") void bloqueiaEscritaParaUsuarioComum() throws Exception {
        mvc.perform(delete("/instrutor/{id}", instrutor().getId())).andExpect(status().isForbidden());
        mvc.perform(get("/instrutor")).andExpect(status().isOk());
    }
}
