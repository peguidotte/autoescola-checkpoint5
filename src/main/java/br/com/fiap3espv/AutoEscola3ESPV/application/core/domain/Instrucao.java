package br.com.fiap3espv.AutoEscola3ESPV.application.core.domain;

import br.com.fiap3espv.AutoEscola3ESPV.shared.vo.enumeration.MotivoCancelamento;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity(name = "Instrucao") @Table(name = "instrucoes")
@Getter @NoArgsConstructor @EqualsAndHashCode(of = "id")
public class Instrucao {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "id_aluno") private Aluno aluno;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "id_instrutor") private Instrutor instrutor;
    @Column(name = "data_hora") private LocalDateTime dataHora;
    @Enumerated(EnumType.STRING) @Column(name = "motivo_cancelamento") private MotivoCancelamento motivoCancelamento;
    @Column(name = "cancelada_em") private LocalDateTime canceladaEm;

    public Instrucao(Long id, Aluno aluno, Instrutor instrutor, LocalDateTime dataHora) {
        this.id = id; this.aluno = aluno; this.instrutor = instrutor; this.dataHora = dataHora;
    }
    public void reagendar(Aluno aluno, Instrutor instrutor, LocalDateTime dataHora) {
        this.aluno = aluno; this.instrutor = instrutor; this.dataHora = dataHora;
    }
    public void cancelar(MotivoCancelamento motivo, LocalDateTime agora) {
        this.motivoCancelamento = motivo; this.canceladaEm = agora;
    }
}
