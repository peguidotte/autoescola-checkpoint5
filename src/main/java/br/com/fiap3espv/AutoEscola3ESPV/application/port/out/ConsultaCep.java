package br.com.fiap3espv.AutoEscola3ESPV.application.port.out;

import br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.response.endereco.DadosConsultaCep;
public interface ConsultaCep { DadosConsultaCep consultar(String cep); }
