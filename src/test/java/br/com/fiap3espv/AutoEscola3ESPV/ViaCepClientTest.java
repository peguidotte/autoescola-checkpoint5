package br.com.fiap3espv.AutoEscola3ESPV;

import br.com.fiap3espv.AutoEscola3ESPV.adapter.out.http.ViaCepClient;
import br.com.fiap3espv.AutoEscola3ESPV.exception.type.ServicoExternoException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

class ViaCepClientTest {
    private MockRestServiceServer server;
    private ViaCepClient client;
    @BeforeEach void setup() {
        var builder = RestClient.builder().baseUrl("https://viacep.com.br");
        server = MockRestServiceServer.bindTo(builder).build(); client = new ViaCepClient(builder.build());
    }
    @AfterEach void verificaChamadas() { server.verify(); }
    @Test void consomeServicoHttpEMapeiaRespostaReal() {
        server.expect(requestTo("https://viacep.com.br/ws/01001000/json/")).andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess("{\"cep\":\"01001-000\",\"logradouro\":\"Praça da Sé\",\"complemento\":\"lado ímpar\",\"bairro\":\"Sé\",\"localidade\":\"São Paulo\",\"uf\":\"SP\",\"ibge\":\"3550308\"}", MediaType.APPLICATION_JSON));
        var result = client.consultar("01001000");
        assertThat(result.cep()).isEqualTo("01001000"); assertThat(result.cidade()).isEqualTo("São Paulo");
    }
    @Test void cepInexistenteRetornaNaoEncontrado() {
        server.expect(anything()).andRespond(withSuccess("{\"erro\":true}", MediaType.APPLICATION_JSON));
        assertThatThrownBy(() -> client.consultar("99999999")).isInstanceOf(EntityNotFoundException.class);
    }
    @Test void erroHttpExternoViraErroControlado() {
        server.expect(anything()).andRespond(withServerError());
        assertThatThrownBy(() -> client.consultar("01001000")).isInstanceOf(ServicoExternoException.class);
    }
    @Test void timeoutViraErroControlado() {
        server.expect(anything()).andRespond(request -> { throw new java.net.SocketTimeoutException("timeout"); });
        assertThatThrownBy(() -> client.consultar("01001000")).isInstanceOf(ServicoExternoException.class);
    }
    @Test void respostaVaziaOuIncompletaNaoViraSucesso() {
        server.expect(anything()).andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));
        assertThatThrownBy(() -> client.consultar("01001000")).isInstanceOf(ServicoExternoException.class);
    }
    @Test void jsonInvalidoViraErroControlado() {
        server.expect(anything()).andRespond(withSuccess("{quebrado", MediaType.APPLICATION_JSON));
        assertThatThrownBy(() -> client.consultar("01001000")).isInstanceOf(ServicoExternoException.class);
    }
}
