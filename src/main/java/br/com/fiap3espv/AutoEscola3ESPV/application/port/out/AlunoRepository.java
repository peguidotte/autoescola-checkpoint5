package br.com.fiap3espv.AutoEscola3ESPV.application.port.out;

import br.com.fiap3espv.AutoEscola3ESPV.application.core.domain.Aluno;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import java.util.Optional;

public interface AlunoRepository extends JpaRepository<Aluno, Long> {
    Page<Aluno> findAllByAtivoTrue(Pageable pageable);
    boolean existsByIdAndAtivoTrue(Long id);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Aluno a where a.id = :id")
    Optional<Aluno> buscarParaAgendamento(Long id);
}
