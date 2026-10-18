alter table usuarios add column papel varchar(10) not null default 'USER';
alter table usuarios add column ativo boolean not null default true;
alter table usuarios add constraint uk_usuarios_login unique (login);
alter table instrucoes add column motivo_cancelamento varchar(40);
alter table instrucoes add column cancelada_em datetime;
create index idx_instrucoes_instrutor_data on instrucoes(id_instrutor, data_hora);
create index idx_instrucoes_aluno_data on instrucoes(id_aluno, data_hora);
