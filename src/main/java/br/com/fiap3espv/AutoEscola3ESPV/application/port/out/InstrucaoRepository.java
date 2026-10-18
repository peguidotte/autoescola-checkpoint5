package br.com.fiap3espv.AutoEscola3ESPV.application.port.out;

import br.com.fiap3espv.AutoEscola3ESPV.application.core.domain.Instrucao;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import java.time.LocalDateTime;
import java.util.Optional;

public interface InstrucaoRepository extends JpaRepository<Instrucao, Long> {
    @Query("""
        select count(i) > 0 from Instrucao i where i.instrutor.id = :idInstrutor
        and i.dataHora = :dataHora and i.motivoCancelamento is null
        and (:ignorarId is null or i.id <> :ignorarId)
        """)
    boolean instrutorOcupado(Long idInstrutor, LocalDateTime dataHora, Long ignorarId);
    @Query("""
        select count(i) > 0 from Instrucao i where i.aluno.id = :idAluno
        and i.dataHora >= :inicio and i.dataHora < :fim and i.motivoCancelamento is null
        and (:ignorarId is null or i.id <> :ignorarId)
        """)
    boolean alunoTemInstrucaoNoDia(Long idAluno, LocalDateTime inicio, LocalDateTime fim, Long ignorarId);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from Instrucao i where i.id = :id")
    Optional<Instrucao> buscarParaAlteracao(Long id);
}
