# Verificação do Checkpoint 5

Este documento relaciona o enunciado fornecido às implementações e às verificações reproduzíveis no repositório. A suíte é executada por `./mvnw clean verify` ou `.\mvnw.cmd clean verify`.

Validação local em 18/10/2026: build com Java 17, suíte H2 aprovada, suíte MySQL 8.4 aprovada, JAR executável iniciado e roteiro HTTP completo aprovado, incluindo consulta real ao ViaCEP. O GitHub Actions reproduz a suíte final em H2 com Java 17/21 e em MySQL com Java 17; os relatórios ficam disponíveis nos artefatos de cada execução.

| Critério do enunciado | Implementação | Verificação |
|---|---|---|
| Java 17 ou superior | Compilação com `release 17` e Maven Wrapper | Build com JDK 17 e matriz CI Java 17/21 |
| Consumo de API externa | `ConsultaCep`, `ViaCepClient`, `HttpClientConfig`, `EnderecoController` | `ViaCepClientTest` e `EnderecoApiTest`: sucesso, validação, inexistente, timeout, HTTP 500 e JSON inválido |
| Swagger automático | `SpringDocConfig`, anotações e springdoc | `InfraestruturaApiTest`: UI pública, JSON/YAML gerados, quatro entidades e Bearer JWT |
| CORS | `CorsConfig` conectado à cadeia de segurança | Preflight com Authorization/Content-Type, origem negada e Location exposto |
| Testes de cada entidade | `AlunoApiTest`, `InstrutorApiTest`, `UsuarioApiTest`, `InstrucaoApiTest` | CRUDs reais por MockMvc, persistência e migrações, erros de validação e duplicidade |
| CRUDs dos checkpoints anteriores | Endpoints `/aluno`, `/instrutor`, `/usuario`, `/instrucao` | Criação 201/Location, leitura/listagem, alteração, exclusão lógica e histórico |
| Agendamento e cancelamento | `AgendaDeInstrucoes` e validadores de agendamento | Regras de funcionamento, antecedência, participantes, limite diário, disponibilidade, cancelamento e reagendamento |
| Reservas concorrentes | Bloqueios transacionais de aluno e instrutor | `ConcorrenciaAgendaTest`: uma reserva de instrutor aceita em duas transações e duas aulas de aluno aceitas em três transações simultâneas |
| Autenticação preservada | BCrypt, JWT e perfis ADMIN/USER | Login e JWT reais, credenciais inválidas, token inválido e usuário desativado |
| Nomes dos integrantes no README | Tabela com os cinco nomes e RMs informados | Inspeção do README |
| Código completo em GitHub público | Repositório `peguidotte/autoescola-checkpoint5` | Clone público e execução do GitHub Actions |

## Escopo das regras verificadas

As regras foram conferidas também com `CRUD - Auto-Escola - Completo - 3ESPV.docx` e `Checkpoint 4 - Enunciado - 3ESPV.docx`, fornecidos posteriormente. O código e os testes foram ajustados ao limite de duas aulas diárias, aos dados cadastrais imutáveis, à CNH na listagem, ao motivo obrigatório e à troca da própria senha pelo usuário. A antecedência de 24 horas é confirmada pelo documento do CRUD. A matriz completa e as divergências corrigidas estão em [Conferência de conformidade](conformidade-cp3-cp4.md).

O enunciado solicita que um integrante envie o link no Teams. O link público fica disponível no README; a submissão no Teams é uma etapa acadêmica externa ao desenvolvimento e à publicação deste repositório.
