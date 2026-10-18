# Autoescola — Checkpoint 5

[![Validar checkpoint 5](https://github.com/peguidotte/autoescola-checkpoint5/actions/workflows/ci.yml/badge.svg)](https://github.com/peguidotte/autoescola-checkpoint5/actions/workflows/ci.yml)

API REST em Java 17 e Spring Boot para cadastro de alunos, instrutores e usuários, com agendamento, reagendamento e cancelamento de instruções. Desenvolvida para a disciplina **SOA e WebServices**, a partir do projeto de aula `AutoEscola3ESPV 15.zip`.

## Integrantes

| Nome | RM |
|---|---|
| Leonardo Correa de Mello | 555573 |
| Felipe Soares Xavier | 556931 |
| Pedro Visconti Guidotte | 556630 |
| Herbert de Sousa Vilela | 555701 |
| Gabriel Figueira Flora | 556476 |

## Entregas do checkpoint

- Consumo do **ViaCEP** por HTTP, com validação de CEP, limites de tempo e tratamento de falhas.
- Documentação automática **OpenAPI 3 e Swagger UI**, com autenticação Bearer JWT.
- **CORS** integrado ao Spring Security, incluindo preflight e o cabeçalho `Authorization`.
- Testes automatizados para **Aluno, Instrutor, Usuário e Instrução**, além de integração externa, regras, autenticação, documentação e concorrência.
- CRUD completo das quatro entidades e regras dos checkpoints anteriores identificadas no material fornecido.
- Pipeline GitHub Actions com Java 17/21 e H2, mais Java 17 e MySQL 8.4.

O detalhamento da verificação está em [Critérios de aceite](docs/criterios-de-aceite.md).

## Executar em poucos passos

É necessário um **JDK 17 ou superior**, com `JAVA_HOME` configurado. O Maven Wrapper acompanha o projeto e baixa o Maven na primeira execução. Não é necessário instalar MySQL para o perfil local.

```powershell
git clone https://github.com/peguidotte/autoescola-checkpoint5.git
cd autoescola-checkpoint5
.\mvnw.cmd clean verify
.\mvnw.cmd spring-boot:run
```

No Linux/macOS, use `./mvnw` no lugar de `.\mvnw.cmd`.

O perfil padrão `dev` cria um banco H2 persistente em `data/` e um administrador de demonstração:

| Campo | Valor no perfil dev |
|---|---|
| Login | `admin@autoescola.local` |
| Senha | `AutoEscola@123` |
| API | `http://localhost:8080` |
| Swagger | `http://localhost:8080/swagger-ui/index.html` |
| OpenAPI JSON | `http://localhost:8080/v3/api-docs` |
| OpenAPI YAML | `http://localhost:8080/v3/api-docs.yaml` |

Essas credenciais e a chave JWT padrão são exclusivas de desenvolvimento. O perfil `mysql` exige configuração explícita do segredo e da senha do banco. `ADMIN_LOGIN` e `ADMIN_PASSWORD` substituem as credenciais na criação inicial; não alteram um usuário já existente.

## Testar a API

1. Faça `POST /login` com o corpo abaixo.
2. Copie `tokenJWT` da resposta.
3. No Swagger, clique em **Authorize** e cole apenas o token.
4. Cadastre um aluno e um instrutor antes de agendar uma instrução.

```json
{
  "login": "admin@autoescola.local",
  "senha": "AutoEscola@123"
}
```

Também é possível executar o fluxo completo por PowerShell, com a API já iniciada:

```powershell
.\scripts\smoke.ps1
```

O script autentica, verifica Swagger/CORS, exercita os quatro CRUDs e consulta o ViaCEP real. Use `-SkipExternal` para verificar apenas os fluxos locais quando não houver acesso à internet. Os testes Maven sempre simulam a API externa e não dependem da disponibilidade do ViaCEP.

## Endpoints e permissões

Todos os recursos abaixo exigem `Authorization: Bearer <token>`, exceto login, health check e documentação.

| Recurso | Operações | Permissão |
|---|---|---|
| `/login` | POST | Público |
| `/health-check` | GET | Público |
| `/aluno` | POST, GET, PUT | Leitura: ADMIN/USER; escrita: ADMIN |
| `/aluno/{id}` | GET, DELETE | Leitura: ADMIN/USER; exclusão: ADMIN |
| `/instrutor` | POST, GET, PUT | Leitura: ADMIN/USER; escrita: ADMIN |
| `/instrutor/{id}` | GET, DELETE | Leitura: ADMIN/USER; exclusão: ADMIN |
| `/usuario` | POST, GET, PUT | ADMIN |
| `/usuario/{id}` | GET, DELETE | ADMIN |
| `/instrucao` | POST, GET, PUT | ADMIN/USER |
| `/instrucao/{id}` | GET, DELETE | ADMIN/USER |
| `/instrucao/{id}/cancelamento` | POST | ADMIN/USER |
| `/endereco/cep/{cep}` | GET | ADMIN/USER |

Os PUTs recebem o `id` no corpo. Na atualização de uma instrução, `dataHora` é obrigatória; omitir aluno e instrutor mantém os participantes. Informar apenas uma especialidade seleciona um instrutor disponível dessa especialidade. As listagens são paginadas (`page`, `size`, `sort`), com alunos/instrutores/usuários ativos. Instruções canceladas continuam no histórico.

## Exemplos de corpos

Aluno:

```json
{
  "nome": "José da Silva",
  "telefone": "11999999999",
  "email": "jose@example.com",
  "cpf": "12345678901",
  "endereco": {
    "logradouro": "Praça da Sé",
    "numero": "10",
    "complemento": "Sala 1",
    "bairro": "Sé",
    "cidade": "São Paulo",
    "uf": "SP",
    "cep": "01001000"
  }
}
```

Para um instrutor, use o mesmo formato de endereço e os campos `nome`, `telefone`, `email`, `cnh` e `especialidade`. As especialidades aceitas são `MOTOS`, `CARROS`, `VANS` e `CAMINHOES`. O CPF tem 11 dígitos e a CNH, de 9 a 11 dígitos, preservando o contrato da aula.

Agendamento com instrutor escolhido:

```json
{
  "idAluno": 1,
  "idInstrutor": 1,
  "dataHora": "21/10/2030 - 10:00"
}
```

Agendamento por especialidade:

```json
{
  "idAluno": 1,
  "especialidade": "CARROS",
  "dataHora": "21/10/2030 - 10:00"
}
```

Use uma data futura e IDs existentes. A API também aceita os aliases `id_aluno`, `id_instrutor` e `data_hora` usados no material da aula. Datas seguem `dd/MM/yyyy - HH:mm`, no fuso `America/Sao_Paulo`.

Cancelamento: `DELETE /instrucao/1?motivo=ALUNO_DESISTIU`, ou `POST /instrucao/1/cancelamento` com `{"motivo":"ALUNO_DESISTIU"}`. Motivos: `ALUNO_DESISTIU`, `INSTRUTOR_CANCELOU` e `OUTROS`. O DELETE assume `OUTROS` quando o parâmetro é omitido.

Usuário: `POST /usuario` com `{"login":"operador","senha":"SenhaForte@123","papel":"USER"}`. Senhas recebem hash BCrypt e nunca aparecem nas respostas. O último administrador ativo não pode ser excluído ou rebaixado.

## Regras da agenda

- Funcionamento de segunda a sábado, das 6h às 21h. Último início permitido: 20h.
- Aulas de uma hora, começando em hora inteira.
- Agendamento com antecedência mínima de 30 minutos.
- Aluno e instrutor devem existir e estar ativos.
- Apenas uma instrução não cancelada por aluno em cada dia.
- Instrutor não pode ter duas instruções no mesmo horário.
- Quando o instrutor não é informado, a especialidade é obrigatória e a seleção considera apenas instrutores ativos e livres.
- Cancelamento mantém motivo e data no histórico, liberando o horário e o limite diário.
- Reagendamento passa pelas mesmas validações e não considera a própria instrução como conflito.
- Transações com bloqueio de registros impedem reservas simultâneas conflitantes.

**Regra assumida:** o enunciado menciona outro anexo de regras de negócio, que não está entre os arquivos fornecidos. As regras acima foram extraídas do código; para cancelamento, ausente na base, foi adotada antecedência de **24 horas**. O mesmo prazo vale para alterar uma reserva existente, evitando contornar o cancelamento por reagendamento. A configuração `CANCELAMENTO_ANTECEDENCIA` permite ajustar esse prazo, por exemplo `24h` ou `30m`.

## Usar MySQL

Crie um banco MySQL 8 e exporte as variáveis antes de executar:

```powershell
$env:SPRING_PROFILES_ACTIVE = 'mysql'
$env:DB_URL = 'jdbc:mysql://localhost:3306/autoescola3espv?createDatabaseIfNotExist=true'
$env:DB_USERNAME = 'root'
$env:DB_PASSWORD = 'sua-senha-do-banco'
$env:JWT_SECRET = 'seu-segredo-aleatorio-longo'
$env:BOOTSTRAP_ENABLED = 'true'
$env:ADMIN_LOGIN = 'admin@autoescola.local'
$env:ADMIN_PASSWORD = 'sua-senha-inicial-forte'
.\mvnw.cmd spring-boot:run
```

O arquivo [.env.example](.env.example) documenta as variáveis; Spring não lê `.env` automaticamente. Flyway executa as migrações e Hibernate valida o esquema. V1 a V5 foram preservadas da aula; V6 adiciona papel/estado dos usuários, unicidade de login, histórico de cancelamento e índices. Se uma base anterior contiver logins duplicados, é necessário reconciliá-los antes de aplicar a restrição única.

## CORS e integração externa

As origens locais autorizadas são `http://localhost:5500`, `http://127.0.0.1:5500`, `http://localhost:5173` e `http://127.0.0.1:5173`. Defina `CORS_ALLOWED_ORIGINS` com origens exatas, separadas por vírgula, sem aspas internas. A configuração permite `Authorization`, `Content-Type`, `Accept` e `Origin`, além de expor `Location`. A API utiliza Bearer JWT e não cookies de sessão.

O endpoint `/endereco/cep/01001000` chama `https://viacep.com.br/ws/01001000/json/`. CEP deve conter oito dígitos. A integração limita conexão a 2 segundos e leitura a 3 segundos; URL e tempos são configuráveis. CEP inexistente retorna 404; indisponibilidade, timeout e resposta inválida do provedor retornam 502. A consulta é independente dos cadastros, permitindo preencher o endereço sem tornar o CRUD dependente de um serviço externo.

## Testes e organização

```powershell
.\mvnw.cmd clean verify
```

O perfil `test` usa H2 em memória, migrações reais e rollback para isolamento. Há testes HTTP de integração das quatro entidades, casos positivos e negativos, conflitos de agendamento, exclusão lógica, cancelamento, permissões, login com JWT real, CORS, Swagger e respostas do ViaCEP. Os testes de concorrência executam duas transações independentes. O relatório de cobertura é gerado em `target/site/jacoco/index.html` e os resultados JUnit em `target/surefire-reports/`.

O projeto mantém o pacote e a divisão em controllers/DTOs, serviços, regras de agendamento, repositórios, domínio e configurações da aula. O ZIP continha uma migração parcial para outro modelo de persistência; os imports e o mapeamento de instrutores foram normalizados para um único modelo JPA, compatível com as relações de aluno/instrução e com as migrações existentes.

Referências técnicas: [ViaCEP](https://viacep.com.br/), [springdoc-openapi](https://springdoc.org/), [Spring Boot](https://docs.spring.io/spring-boot/4.0/).
