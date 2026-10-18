package br.com.fiap3espv.AutoEscola3ESPV;

import br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.request.instrucao.DadosAgendamento;
import br.com.fiap3espv.AutoEscola3ESPV.application.core.specification.agendamento.*;
import br.com.fiap3espv.AutoEscola3ESPV.exception.type.ValidacaoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.time.*;
import static org.assertj.core.api.Assertions.*;

class RegrasAgendamentoTest {
    private final LocalDateTime segunda = LocalDateTime.of(2030, 1, 7, 9, 0);
    private DadosAgendamento dados(LocalDateTime hora) { return new DadosAgendamento(1L, 1L, null, hora); }
    @ParameterizedTest @ValueSource(ints = {6, 9, 20}) void aceitaHorarioDeFuncionamento(int hora) {
        assertThatCode(() -> new ValidadorHorarioFuncionamento().validar(dados(segunda.withHour(hora)))).doesNotThrowAnyException();
    }
    @ParameterizedTest @ValueSource(ints = {0, 5, 21, 23}) void rejeitaHorarioForaDoExpediente(int hora) {
        assertThatThrownBy(() -> new ValidadorHorarioFuncionamento().validar(dados(segunda.withHour(hora)))).isInstanceOf(ValidacaoException.class);
    }
    @Test void rejeitaDomingoEAceitaSabado() {
        assertThatThrownBy(() -> new ValidadorHorarioFuncionamento().validar(dados(segunda.minusDays(1)))).isInstanceOf(ValidacaoException.class);
        assertThatCode(() -> new ValidadorHorarioFuncionamento().validar(dados(segunda.minusDays(2)))).doesNotThrowAnyException();
    }
    @Test void permiteHorarioFracionadoMasAulaDeveTerminarAteAsVinteEUma() {
        var validador = new ValidadorHorarioFuncionamento();
        assertThatCode(() -> validador.validar(dados(segunda.withHour(19).withMinute(30)))).doesNotThrowAnyException();
        assertThatCode(() -> validador.validar(dados(segunda.withHour(20)))).doesNotThrowAnyException();
        assertThatThrownBy(() -> validador.validar(dados(segunda.withHour(20).withMinute(1)))).isInstanceOf(ValidacaoException.class);
    }
    @Test void antecedenciaDeExatosTrintaMinutosEPermitida() {
        var clock = Clock.fixed(segunda.atZone(ZoneId.of("America/Sao_Paulo")).toInstant(), ZoneId.of("America/Sao_Paulo"));
        var validador = new ValidadorHorarioAntecedencia(clock);
        assertThatCode(() -> validador.validar(dados(segunda.plusMinutes(30)))).doesNotThrowAnyException();
        assertThatThrownBy(() -> validador.validar(dados(segunda.plusMinutes(30).minusNanos(1)))).isInstanceOf(ValidacaoException.class);
    }
}
