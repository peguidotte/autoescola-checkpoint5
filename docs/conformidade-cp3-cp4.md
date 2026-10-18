# Conferência das regras do CRUD e do Checkpoint 4

A revisão compara a API com os documentos `CRUD - Auto-Escola - Completo - 3ESPV.docx` e `Checkpoint 4 - Enunciado - 3ESPV.docx`, fornecidos depois da primeira publicação do CP5. A versão inicial apresentava divergências que não eram reveladas pelos testes baseados apenas no código da aula. A implementação e os testes foram corrigidos a partir desses documentos.

Validação da versão corrigida em 18/10/2026: **76 testes aprovados com Java 17 em H2 e MySQL 8.4**, sem falhas, erros ou testes ignorados, e **41 verificações por HTTP aprovadas** com o JAR iniciado. Os casos de conformidade estão em `ConformidadeDocumentosTest`, além dos testes atualizados da agenda e de concorrência. As [evidências por HTTP](evidencias-http.md) registram o resultado de cada verificação; o GitHub Actions repete a suíte também com Java 21.

## Divergências identificadas e corrigidas

| Regra dos documentos | Comportamento da primeira versão | Comportamento corrigido |
|---|---|---|
| Até duas instruções por aluno no dia | Rejeitava a segunda aula com HTTP 400 | Permite duas; rejeita a terceira, inclusive em requisições concorrentes |
| E-mail de aluno imutável | PUT alterava o e-mail com HTTP 200 | PUT rejeita e-mail e CPF com HTTP 400; domínio só altera nome, telefone e endereço |
| E-mail, CNH e especialidade de instrutor imutáveis | PUT alterava e-mail e especialidade | PUT rejeita os três campos com HTTP 400; domínio só altera nome, telefone e endereço |
| Usuário cadastrado pode trocar a própria senha | Todas as rotas de usuário eram restritas a ADMIN | PUT `/usuario/senha` permite ADMIN/USER autenticado, valida senha atual e altera somente o usuário do JWT |
| Motivo obrigatório para cancelar | DELETE sem motivo assumia OUTROS e cancelava | Ausência ou motivo inválido é rejeitado em ambas as rotas |
| Listagem de instrutores deve conter CNH | Campo omitido da resposta | CNH incluída no DTO de listagem |
| Escolha do instrutor é opcional | Sem instrutor, exigia especialidade como outro campo obrigatório | Sorteia entre os instrutores ativos e disponíveis; especialidade funciona como filtro opcional |

## Definições adicionais alinhadas

O documento orienta ocultar o telefone do instrutor inicialmente para posterior atualização. A API permite omiti-lo no cadastro e o formulário POST do Swagger o oculta. O PUT continua permitindo seu preenchimento. A migração V7 permite nulo nessa coluna sem modificar as migrações anteriores. Quando fornecido por um cliente existente, o telefone continua sendo aceito no cadastro.

Os documentos fixam a duração em uma hora, mas não exigem início em hora cheia. A restrição adicional foi removida: 09h30 a 10h30 é permitido, 20h01 a 21h01 é rejeitado e horários sobrepostos para o mesmo instrutor são bloqueados. Aulas que terminam exatamente no início da próxima são permitidas. O prazo de 24 horas para cancelamento está expressamente confirmado no documento; também é aplicado ao reagendamento para evitar contorná-lo.

## Matriz de conferência

| Definição | Implementação e evidência |
|---|---|
| Nome, e-mail, CNH, especialidade e endereço de instrutor obrigatórios | Bean Validation em `DadosCadastroInstrutor`; testes de cadastro inválido |
| Telefone do instrutor preenchido posteriormente | Schema do Swagger e teste de cadastro sem telefone seguido de atualização |
| Número e complemento do endereço opcionais | DTO de endereço sem NotBlank nesses dois campos; conferência HTTP de cadastro sem ambos |
| Nome, e-mail, telefone, CPF e endereço do aluno obrigatórios | Bean Validation em `DadosCadastroAluno`; testes de validação |
| Listagem de instrutores: nome, e-mail, CNH e especialidade | `DadosListagemInstrutores` e teste de resposta HTTP |
| Listagem de alunos: nome, e-mail e CPF | DTO de aluno e conferência HTTP |
| Listagens por nome crescente, dez registros por página | Pageable padrão dos dois controllers e conferência HTTP com mais de dez registros |
| Atualização apenas de nome, telefone e endereço | Dados imutáveis rejeitados na validação; métodos de domínio não os recebem |
| Exclusão de aluno e instrutor deve ser lógica | Flag ativo falsa, registro preservado e removido da listagem |
| Funcionamento de segunda a sábado, 6h a 21h | `ValidadorHorarioFuncionamento`, testes de abertura, fechamento, sábado e domingo |
| Instruções com uma hora de duração | Intervalos calculados com uma hora nas verificações de disponibilidade e funcionamento |
| Antecedência mínima de trinta minutos | Clock injetável e teste do limite exato de trinta minutos |
| Aluno e instrutor ativos | Validadores específicos e testes de participantes inativos |
| Limite diário de duas aulas do aluno | Contagem de instruções não canceladas; teste da segunda e terceira aulas e concorrência |
| Instrutor sem conflito de horário | Consulta por interseção de intervalos e bloqueio transacional |
| Instrutor escolhido aleatoriamente se omitido | Seleção aleatória de candidatos ativos e livres, com especialidade opcional |
| Cancelamento exige instrução e motivo | ID da rota, enum e validação obrigatória de motivo nas duas rotas |
| Motivos aluno desistiu, instrutor cancelou e outros | Enum `MotivoCancelamento`; conferência HTTP dos três valores |
| Cancelamento com pelo menos 24h de antecedência | Testes de 24h exatas e de um instante abaixo, mais conferência HTTP |
| Cadastro de alunos | Endpoint POST `/aluno`, persistência e testes de CRUD |
| Cadastro de usuário com senha protegida | BCrypt; teste de correspondência do hash e ausência de senha nas respostas |
| Somente ADMIN cadastra, lista, altera perfil e exclui usuários | Autorizações no controller e testes de bloqueio para USER |
| Usuário cadastrado altera a própria senha | PUT `/usuario/senha`, JWT real, senha atual, login com senha nova e preservação de outro usuário |
| Pelo menos um usuário inicial | Bootstrap do administrador no perfil dev; configuração explícita disponível no perfil MySQL |
| Acesso autenticado por JWT aos recursos de negócio | Filtro de segurança, testes de token, login e acesso sem credencial |

O CP5 mantém também ViaCEP, Swagger, CORS e os testes das quatro entidades. O README inclui os cinco integrantes e os respectivos RMs. A publicação é em repositório público; o envio do link no Teams continua sendo a etapa acadêmica do integrante responsável.
