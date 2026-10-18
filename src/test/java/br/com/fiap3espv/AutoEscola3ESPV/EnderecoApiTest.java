package br.com.fiap3espv.AutoEscola3ESPV;

import br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.response.endereco.DadosConsultaCep;
import br.com.fiap3espv.AutoEscola3ESPV.application.port.out.ConsultaCep;
import br.com.fiap3espv.AutoEscola3ESPV.exception.type.ServicoExternoException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithMockUser
class EnderecoApiTest extends BaseApiTest {
    @MockitoBean ConsultaCep consulta;
    @Test void retornaEnderecoConsultado() throws Exception {
        when(consulta.consultar("01001000")).thenReturn(new DadosConsultaCep("01001000", "Praça da Sé", "", "Sé", "São Paulo", "SP"));
        mvc.perform(get("/endereco/cep/01001000")).andExpect(status().isOk()).andExpect(jsonPath("$.cidade").value("São Paulo"));
    }
    @Test void validaCepAntesDeChamarServico() throws Exception {
        mvc.perform(get("/endereco/cep/123")).andExpect(status().isBadRequest()); verifyNoInteractions(consulta);
    }
    @Test void cepInexistenteRetorna404() throws Exception {
        when(consulta.consultar("99999999")).thenThrow(new EntityNotFoundException("CEP não encontrado"));
        mvc.perform(get("/endereco/cep/99999999")).andExpect(status().isNotFound());
    }
    @Test void indisponibilidadeRetorna502() throws Exception {
        when(consulta.consultar("01001000")).thenThrow(new ServicoExternoException("ViaCEP indisponível"));
        mvc.perform(get("/endereco/cep/01001000")).andExpect(status().isBadGateway());
    }
}
