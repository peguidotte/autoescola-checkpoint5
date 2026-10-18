package br.com.fiap3espv.AutoEscola3ESPV.application.core.domain;

import br.com.fiap3espv.AutoEscola3ESPV.shared.vo.endereco.Endereco;
import jakarta.persistence.*;
import lombok.*;

@Entity(name = "Aluno") @Table(name = "alunos")
@Getter @NoArgsConstructor @AllArgsConstructor @EqualsAndHashCode(of = "id")
public class Aluno {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String telefone;
    private String email;
    private String cpf;
    @Embedded private Endereco endereco;
    @Column(columnDefinition = "tinyint")
    private boolean ativo = true;

    public void atualizar(String nome, String telefone, String email, Endereco endereco) {
        if (nome != null && !nome.isBlank()) this.nome = nome;
        if (telefone != null && !telefone.isBlank()) this.telefone = telefone;
        if (email != null && !email.isBlank()) this.email = email;
        if (endereco != null) this.endereco = endereco;
    }
    public void excluir() { ativo = false; }
}
