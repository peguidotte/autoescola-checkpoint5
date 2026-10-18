package br.com.fiap3espv.AutoEscola3ESPV.application.port.out;

import br.com.fiap3espv.AutoEscola3ESPV.application.core.domain.Instrucao;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import java.time.LocalDateTime;
import java.util.Optional;

public interface InstrucaoRepository extends JpaRepository<Instrucao, Long> {
    @Query("""
        select count(i) > 0 from Instrucao i where i.instrutor.id = :idInstrutor
        and i.dataHora > :limiteInferior and i.dataHora < :limiteSuperior and i.motivoCancelamento is null
        and (:ignorarId is null or i.id <> :ignorarId)
        """)
    boolean existeConflitoDeHorario(Long idInstrutor, LocalDateTime limiteInferior, LocalDateTime limiteSuperior, Long ignorarId);

    default boolean instrutorOcupado(Long idInstrutor, LocalDateTime dataHora, Long ignorarId) {
        // Duas aulas de uma hora conflitam quando seus intervalos se sobrepõem.
        return existeConflitoDeHorario(idInstrutor, dataHora.minusHours(1), dataHora.plusHours(1), ignorarId);
    }
    @Query("""
        select count(i) from Instrucao i where i.aluno.id = :idAluno
        and i.dataHora >= :inicio and i.dataHora < :fim and i.motivoCancelamento is null
        and (:ignorarId is null or i.id <> :ignorarId)
        """)
    long contarInstrucoesNoDia(Long idAluno, LocalDateTime inicio, LocalDateTime fim, Long ignorarId);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from Instrucao i where i.id = :id")
    Optional<Instrucao> buscarParaAlteracao(Long id);
}
