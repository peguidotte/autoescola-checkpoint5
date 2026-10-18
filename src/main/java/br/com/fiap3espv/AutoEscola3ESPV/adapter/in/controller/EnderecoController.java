package br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller;

import br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.response.endereco.DadosConsultaCep;
import br.com.fiap3espv.AutoEscola3ESPV.application.port.out.ConsultaCep;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/endereco") @RequiredArgsConstructor @Tag(name = "Endereços")
public class EnderecoController {
    private final ConsultaCep consulta;
    @GetMapping("/cep/{cep}") @Operation(summary = "Consultar endereço no serviço externo ViaCEP")
    public DadosConsultaCep consultar(@PathVariable @Pattern(regexp = "[0-9]{8}", message = "Informe 8 dígitos numéricos") String cep) {
        return consulta.consultar(cep);
    }
}
