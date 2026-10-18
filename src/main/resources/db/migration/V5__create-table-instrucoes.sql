create table instrucoes(
    id bigint not null auto_increment,
    id_aluno bigint not null,
    id_instrutor bigint not null,
    data_hora datetime not null,

    primary key(id),
    constraint fk_instrucoes_id_aluno foreign key(id_aluno) references alunos(id),
    constraint fk_instrucoes_id_instrutor foreign key(id_instrutor) references instrutores(id)
);