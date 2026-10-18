package br.com.fiap3espv.AutoEscola3ESPV;

import br.com.fiap3espv.AutoEscola3ESPV.application.core.domain.Instrucao;
import br.com.fiap3espv.AutoEscola3ESPV.application.port.out.*;
import br.com.fiap3espv.AutoEscola3ESPV.application.service.AgendaDeInstrucoes;
import br.com.fiap3espv.AutoEscola3ESPV.exception.type.ValidacaoException;
import br.com.fiap3espv.AutoEscola3ESPV.shared.vo.enumeration.MotivoCancelamento;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.*;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class CancelamentoTest {
    private final Clock clock = Clock.fixed(Instant.parse("2030-01-07T12:00:00Z"), ZoneId.of("America/Sao_Paulo"));
    private final InstrucaoRepository repository = mock(InstrucaoRepository.class);
    private final AgendaDeInstrucoes agenda = new AgendaDeInstrucoes(repository,
        mock(AlunoRepository.class), mock(InstrutorRepository.class), List.of(), clock);

    @Test void permiteCancelamentoComExatasVinteEQuatroHoras() {
        ReflectionTestUtils.setField(agenda, "antecedenciaCancelamento", Duration.ofHours(24));
        var instrucao = new Instrucao(1L, null, null, LocalDateTime.now(clock).plusHours(24));
        when(repository.buscarParaAlteracao(1L)).thenReturn(Optional.of(instrucao));
        agenda.cancelar(1L, MotivoCancelamento.ALUNO_DESISTIU);
        assertThat(instrucao.getMotivoCancelamento()).isEqualTo(MotivoCancelamento.ALUNO_DESISTIU);
        assertThat(instrucao.getCanceladaEm()).isEqualTo(LocalDateTime.now(clock));
    }

    @Test void rejeitaUmInstanteAbaixoDoPrazo() {
        ReflectionTestUtils.setField(agenda, "antecedenciaCancelamento", Duration.ofHours(24));
        var instrucao = new Instrucao(1L, null, null, LocalDateTime.now(clock).plusHours(24).minusNanos(1));
        when(repository.buscarParaAlteracao(1L)).thenReturn(Optional.of(instrucao));
        assertThatThrownBy(() -> agenda.cancelar(1L, MotivoCancelamento.OUTROS)).isInstanceOf(ValidacaoException.class);
        assertThat(instrucao.getMotivoCancelamento()).isNull();
    }
}
