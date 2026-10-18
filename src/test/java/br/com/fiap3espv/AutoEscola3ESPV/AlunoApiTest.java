package br.com.fiap3espv.AutoEscola3ESPV;

import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithMockUser(roles = "ADMIN")
class AlunoApiTest extends BaseApiTest {
    @Test void crudCompletoComExclusaoLogica() throws Exception {
        var result = mvc.perform(body(post("/aluno"), Map.of("nome", "José da Silva", "telefone", "11999999999", "email", "jose@teste.com", "cpf", "12345678901", "endereco", enderecoJson())))
            .andExpect(status().isCreated()).andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.nome").value("José da Silva")).andReturn();
        long id = payload(result).get("id").asLong();
        mvc.perform(get("/aluno/{id}", id)).andExpect(status().isOk()).andExpect(jsonPath("$.endereco.cidade").value("São Paulo"));
        mvc.perform(get("/aluno")).andExpect(status().isOk()).andExpect(jsonPath("$.content[0].id").value(id));
        mvc.perform(body(put("/aluno"), Map.of("id", id, "nome", "José atualizado"))).andExpect(status().isOk()).andExpect(jsonPath("$.nome").value("José atualizado"));
        mvc.perform(delete("/aluno/{id}", id)).andExpect(status().isNoContent());
        assertThat(alunos.findById(id).orElseThrow().isAtivo()).isFalse();
        mvc.perform(get("/aluno")).andExpect(jsonPath("$.totalElements").value(0));
    }
    @Test void validaCamposObrigatoriosEEndereco() throws Exception {
        mvc.perform(body(post("/aluno"), Map.of("nome", "", "email", "invalido", "cpf", "1"))).andExpect(status().isBadRequest()).andExpect(jsonPath("$.erros").isArray());
    }
    @Test void duplicidadeDeCpfOuEmailRetorna409() throws Exception {
        var aluno = aluno();
        mvc.perform(body(post("/aluno"), Map.of("nome", "Outro", "telefone", "11999999999", "email", aluno.getEmail(), "cpf", aluno.getCpf(), "endereco", enderecoJson()))).andExpect(status().isConflict());
    }
    @Test void idsInexistentesRetornam404() throws Exception {
        mvc.perform(get("/aluno/999999")).andExpect(status().isNotFound());
        mvc.perform(body(put("/aluno"), Map.of("id", 999999, "nome", "Nome"))).andExpect(status().isNotFound());
        mvc.perform(delete("/aluno/999999")).andExpect(status().isNotFound());
    }
    @Test @WithMockUser(roles = "USER") void usuarioComumNaoAlteraCadastro() throws Exception {
        mvc.perform(body(post("/aluno"), Map.of("nome", "Teste", "telefone", "11", "email", "teste@teste.com", "cpf", "12345678901", "endereco", enderecoJson()))).andExpect(status().isForbidden());
        mvc.perform(get("/aluno")).andExpect(status().isOk());
    }
}
