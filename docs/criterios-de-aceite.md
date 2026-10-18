# Verificação do Checkpoint 5

Este documento relaciona o enunciado fornecido às implementações e às verificações reproduzíveis no repositório. A suíte é executada por `./mvnw clean verify` ou `.\mvnw.cmd clean verify`.

Validação local em 18/10/2026: build com Java 17, suíte H2 aprovada com 63 testes, suíte MySQL 8.4 aprovada, JAR executável iniciado e roteiro HTTP completo aprovado, incluindo consulta real ao ViaCEP. O GitHub Actions reproduz a suíte final em H2 com Java 17/21 e em MySQL com Java 17; os relatórios ficam disponíveis nos artefatos de cada execução.

| Critério do enunciado | Implementação | Verificação |
|---|---|---|
| Java 17 ou superior | Compilação com `release 17` e Maven Wrapper | Build com JDK 17 e matriz CI Java 17/21 |
| Consumo de API externa | `ConsultaCep`, `ViaCepClient`, `HttpClientConfig`, `EnderecoController` | `ViaCepClientTest` e `EnderecoApiTest`: sucesso, validação, inexistente, timeout, HTTP 500 e JSON inválido |
| Swagger automático | `SpringDocConfig`, anotações e springdoc | `InfraestruturaApiTest`: UI pública, JSON/YAML gerados, quatro entidades e Bearer JWT |
| CORS | `CorsConfig` conectado à cadeia de segurança | Preflight com Authorization/Content-Type, origem negada e Location exposto |
| Testes de cada entidade | `AlunoApiTest`, `InstrutorApiTest`, `UsuarioApiTest`, `InstrucaoApiTest` | CRUDs reais por MockMvc, persistência e migrações, erros de validação e duplicidade |
| CRUDs dos checkpoints anteriores | Endpoints `/aluno`, `/instrutor`, `/usuario`, `/instrucao` | Criação 201/Location, leitura/listagem, alteração, exclusão lógica e histórico |
| Agendamento e cancelamento | `AgendaDeInstrucoes` e sete validadores | Regras de funcionamento, antecedência, participantes, limite diário, disponibilidade, cancelamento e reagendamento |
| Reservas concorrentes | Bloqueios transacionais de aluno e instrutor | `ConcorrenciaAgendaTest`: uma reserva aceita e outra recusada em duas transações simultâneas |
| Autenticação preservada | BCrypt, JWT e perfis ADMIN/USER | Login e JWT reais, credenciais inválidas, token inválido e usuário desativado |
| Nomes dos integrantes no README | Tabela com os cinco nomes e RMs informados | Inspeção do README |
| Código completo em GitHub público | Repositório `peguidotte/autoescola-checkpoint5` | Clone público e execução do GitHub Actions |

## Escopo das regras verificadas

As sete regras de agendamento foram recuperadas do ZIP. O anexo de regras citado no enunciado não foi fornecido; por isso, a antecedência de 24 horas para cancelamento/reagendamento é uma hipótese documentada e configurável. Ela não é apresentada como texto do professor.

O enunciado solicita que um integrante envie o link no Teams. O link público fica disponível no README; a submissão no Teams é uma etapa acadêmica externa ao desenvolvimento e à publicação deste repositório.
