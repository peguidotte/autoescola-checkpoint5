package br.com.fiap3espv.AutoEscola3ESPV.application.core.domain;

import br.com.fiap3espv.AutoEscola3ESPV.shared.vo.endereco.Endereco;
import br.com.fiap3espv.AutoEscola3ESPV.shared.vo.enumeration.Especialidade;
import jakarta.persistence.*;
import lombok.*;

@Entity(name = "Instrutor")
@Table(name = "instrutores")
@Getter @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Instrutor {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String telefone;
    private String email;
    private String cnh;
    @Enumerated(EnumType.STRING)
    private Especialidade especialidade;
    @Embedded
    private Endereco endereco;
    @Column(columnDefinition = "tinyint")
    private boolean ativo = true;

    public void atualizarInformacoes(String nome, String telefone, String email,
                                    Especialidade especialidade, Endereco endereco) {
        if (nome != null && !nome.isBlank()) this.nome = nome;
        if (telefone != null && !telefone.isBlank()) this.telefone = telefone;
        if (email != null && !email.isBlank()) this.email = email;
        if (especialidade != null) this.especialidade = especialidade;
        if (endereco != null) this.endereco = endereco;
    }
    public void excluir() { ativo = false; }
}
