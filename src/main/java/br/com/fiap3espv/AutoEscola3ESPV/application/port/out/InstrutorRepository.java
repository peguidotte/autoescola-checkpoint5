package br.com.fiap3espv.AutoEscola3ESPV.application.port.out;

import br.com.fiap3espv.AutoEscola3ESPV.application.core.domain.Instrutor;
import br.com.fiap3espv.AutoEscola3ESPV.shared.vo.enumeration.Especialidade;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import java.util.*;

public interface InstrutorRepository extends JpaRepository<Instrutor, Long> {
    Page<Instrutor> findAllByAtivoTrue(Pageable pageable);
    boolean existsByIdAndAtivoTrue(Long id);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from Instrutor i where i.id = :id")
    Optional<Instrutor> buscarParaAgendamento(Long id);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from Instrutor i where i.ativo = true and (:especialidade is null or i.especialidade = :especialidade) order by i.id")
    List<Instrutor> buscarCandidatos(Especialidade especialidade);
}
