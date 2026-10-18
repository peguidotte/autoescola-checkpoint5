package br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@io.swagger.v3.oas.annotations.security.SecurityRequirements
@RestController
@RequestMapping("/health-check")
public class HealthCheckController {
    @GetMapping
    public String healthCheck() {
        return "Verificação de integridade da API Auto Escola 3ESPV ok!";
    }
}