# Evidências da conferência por HTTP

Verificação do JAR corrigido em 18/10/2026, com banco H2 temporário e dados fictícios. Todas as 41 verificações abaixo foram aprovadas. Os testes Maven reproduzem os contratos e também verificam limites e concorrência.

| Regra | Esperado | Observado | Resultado |
|---|---|---|---|
| Cadastro com número e complemento opcionais | 201 para aluno e instrutor | Ambos retornaram 201 | Aprovado |
| Endereço obrigatório no cadastro de aluno | 400 | 400 | Aprovado |
| Endereço obrigatório no cadastro de instrutor | 400 | 400 | Aprovado |
| E-mail de aluno imutável | Alteração impedida | HTTP 400; email persistido=pessoa1001@example.com | Aprovado |
| E-mail e especialidade de instrutor imutáveis | Alterações impedidas | HTTP 400; email persistido=pessoa1002@example.com; especialidade=CARROS | Aprovado |
| CPF de aluno imutável | CPF preservado | HTTP 400; cpf persistido=00000001001 | Aprovado |
| CNH de instrutor imutável | CNH preservada | HTTP 400; cnh persistida=00000001002 | Aprovado |
| Atualização de nome, telefone e endereço de aluno | 200 e campos atualizados | 200 | Aprovado |
| Atualização de nome, telefone e endereço de instrutor | 200 e campos atualizados | 200 | Aprovado |
| CNH na listagem de instrutores | nome, email, cnh e especialidade | cnh, email, especialidade, id, nome | Aprovado |
| Paginação padrão e ordenação de aluno | 10 registros por página, nome crescente | size=10; registros=10; nomes=['Nome atualizado', 'Ordenação 00', 'Ordenação 01', 'Ordenação 02', 'Ordenação 03', 'Ordenação 04', 'Ordenação 05', 'Ordenação 06', 'Ordenação 07', 'Ordenação 08'] | Aprovado |
| Paginação padrão e ordenação de instrutor | 10 registros por página, nome crescente | size=10; registros=10; nomes=['Nome atualizado', 'Ordenação 00', 'Ordenação 01', 'Ordenação 02', 'Ordenação 03', 'Ordenação 04', 'Ordenação 05', 'Ordenação 06', 'Ordenação 07', 'Ordenação 08'] | Aprovado |
| Exclusão lógica de aluno | Registro preservado e inativo | DELETE 204; GET 200; ativo=False | Aprovado |
| Exclusão lógica de instrutor | Registro preservado e inativo | DELETE 204; GET 200; ativo=False | Aprovado |
| Permitir duas instruções no mesmo dia para o aluno | Primeira e segunda: 201 | primeira=201; segunda=201; detalhe=None | Aprovado |
| Bloquear mais de duas instruções no dia | Terceira: 400 | terceira=400 | Aprovado |
| Conflito de data/hora do instrutor | 400 | 400; Instrutor ocupado na data/hora informada | Aprovado |
| Bloquear aluno inativo | 400 | 400; Instrução não pode ser agendada para aluno inativo | Aprovado |
| Bloquear instrutor inativo | 400 | 400; Instrução não pode ser agendada com instrutor inativo | Aprovado |
| Instrutor opcional sem exigir outro campo de seleção | 201, com algum instrutor disponível | 201; None | Aprovado |
| Seleção de instrutor disponível por especialidade | 201 e instrutor escolhido | 201; idInstrutor=6 | Aprovado |
| Funcionamento: Domingo | 400 | 400; Tentativa de agendamento fora do horário de funcionamento | Aprovado |
| Funcionamento: Antes de 6h | 400 | 400; Tentativa de agendamento fora do horário de funcionamento | Aprovado |
| Funcionamento: Aula iniciando às 21h | 400 | 400; Tentativa de agendamento fora do horário de funcionamento | Aprovado |
| Funcionamento: Abertura às 6h | 201 | 201 | Aprovado |
| Funcionamento: Última aula às 20h | 201 | 201 | Aprovado |
| Início de aula em horário fracionado | 201: aula de 14h30 a 15h30 dentro do expediente | 201 | Aprovado |
| Motivo obrigatório em todo cancelamento | Rejeitar ausência de motivo | DELETE sem motivo=400; motivo persistido=None | Aprovado |
| Motivo permitido: ALUNO_DESISTIU | 204 | 204 | Aprovado |
| Motivo permitido: INSTRUTOR_CANCELOU | 204 | 204 | Aprovado |
| Motivo permitido: OUTROS | 204 | 204 | Aprovado |
| Cancelamento com antecedência inferior a 24h | 400 | 400; Cancelamento ou reagendamento exige antecedência mínima de 24 horas | Aprovado |
| Cadastrar usuário sem expor senha | 201, senha ausente na resposta | HTTP 201; campos=['ativo', 'id', 'login', 'papel'] | Aprovado |
| Login do usuário cadastrado com JWT | 200 e token | 200 | Aprovado |
| Autenticação em recursos de negócio | 401 sem JWT | 401 | Aprovado |
| Apenas ADMIN: Cadastrar usuários | 403 para USER | 403 | Aprovado |
| Apenas ADMIN: Listar usuários | 403 para USER | 403 | Aprovado |
| Apenas ADMIN: Alterar perfil de usuários | 403 para USER | 403 | Aprovado |
| Apenas ADMIN: Excluir usuários | 403 para USER | 403 | Aprovado |
| Usuário comum pode alterar a própria senha | 204 na rota da própria senha | PUT /usuario/senha=204; rotas documentadas=['/usuario', '/usuario/senha', '/usuario/{id}'] | Aprovado |
| Troca de senha vale no próximo login | Senha antiga 401; nova 200 | antiga=401; nova=200 | Aprovado |
