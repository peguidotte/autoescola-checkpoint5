package br.com.fiap3espv.AutoEscola3ESPV.adapter.out.http;

import br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.response.endereco.DadosConsultaCep;
import br.com.fiap3espv.AutoEscola3ESPV.application.port.out.ConsultaCep;
import br.com.fiap3espv.AutoEscola3ESPV.exception.type.ServicoExternoException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.*;
@Component @RequiredArgsConstructor
public class ViaCepClient implements ConsultaCep {
    private final RestClient viaCepRestClient;
    @Override public DadosConsultaCep consultar(String cep) {
        try {
            ViaCepResposta resposta = viaCepRestClient.get().uri("/ws/{cep}/json/", cep).retrieve().body(ViaCepResposta.class);
            if (resposta == null) throw new ServicoExternoException("ViaCEP retornou uma resposta vazia");
            if (Boolean.TRUE.equals(resposta.erro())) throw new EntityNotFoundException("CEP não encontrado");
            if (resposta.cep() == null || resposta.localidade() == null || resposta.uf() == null)
                throw new ServicoExternoException("ViaCEP retornou uma resposta incompleta");
            return new DadosConsultaCep(resposta.cep().replace("-", ""), resposta.logradouro(), resposta.complemento(),
                resposta.bairro(), resposta.localidade(), resposta.uf());
        } catch (RestClientException ex) { throw new ServicoExternoException("Não foi possível consultar o ViaCEP. Tente novamente mais tarde."); }
    }
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
    private record ViaCepResposta(String cep, String logradouro, String complemento, String bairro, String localidade, String uf, Boolean erro) {}
}
