param(
    [string]$BaseUrl = 'http://localhost:8080',
    [string]$Login = 'admin@autoescola.local',
    [string]$Senha = 'AutoEscola@123',
    [switch]$SkipExternal
)
$ErrorActionPreference = 'Stop'

function Assert-Condition($Condition, [string]$Message) {
    if (-not $Condition) { throw $Message }
}
function Invoke-Api([string]$Method, [string]$Path, $Payload = $null) {
    $arguments = @{ Method = $Method; Uri = "$BaseUrl$Path"; Headers = $script:Auth }
    if ($null -ne $Payload) {
        $arguments.ContentType = 'application/json; charset=utf-8'
        $arguments.Body = [Text.Encoding]::UTF8.GetBytes(($Payload | ConvertTo-Json -Depth 8))
    }
    Invoke-RestMethod @arguments
}

$credentials = @{ login = $Login; senha = $Senha } | ConvertTo-Json
$authentication = Invoke-RestMethod -Method Post -Uri "$BaseUrl/login" -ContentType 'application/json' -Body ([Text.Encoding]::UTF8.GetBytes($credentials))
$script:Auth = @{ Authorization = "Bearer $($authentication.tokenJWT)" }
Assert-Condition ($authentication.tokenJWT.Length -gt 20) 'Login não retornou JWT'

$spec = Invoke-RestMethod -Uri "$BaseUrl/v3/api-docs"
Assert-Condition ($null -ne $spec.paths.'/instrucao') 'Swagger sem instruções'
$cors = Invoke-WebRequest -Method Options -Uri "$BaseUrl/instrutor" -Headers @{
    Origin = 'http://localhost:5173'
    'Access-Control-Request-Method' = 'POST'
    'Access-Control-Request-Headers' = 'Authorization,Content-Type'
} -UseBasicParsing
Assert-Condition (($cors.Headers['Access-Control-Allow-Origin'] -join '') -eq 'http://localhost:5173') 'Preflight CORS falhou'

$suffix = [DateTimeOffset]::UtcNow.ToUnixTimeMilliseconds().ToString()
$document = $suffix.Substring($suffix.Length - 11)
$address = @{ logradouro = 'Praça da Sé'; numero = '10'; complemento = 'Sala 1'; bairro = 'Sé'; cidade = 'São Paulo'; uf = 'SP'; cep = '01001000' }
$aluno = Invoke-Api POST '/aluno' @{ nome = "Aluno smoke $suffix"; telefone = '11999999999'; email = "aluno$suffix@example.com"; cpf = $document; endereco = $address }
$instrutor = Invoke-Api POST '/instrutor' @{ nome = "Instrutor smoke $suffix"; telefone = '11999999999'; email = "instrutor$suffix@example.com"; cnh = $document; especialidade = 'CARROS'; endereco = $address }
$usuario = Invoke-Api POST '/usuario' @{ login = "smoke$suffix"; senha = 'SenhaSmoke@123'; papel = 'USER' }

foreach ($resource in @('aluno', 'instrutor', 'usuario')) {
    $null = Invoke-Api GET "/$resource"
}
$null = Invoke-Api GET "/aluno/$($aluno.id)"
$null = Invoke-Api GET "/instrutor/$($instrutor.id)"
$null = Invoke-Api GET "/usuario/$($usuario.id)"
$null = Invoke-Api PUT '/aluno' @{ id = $aluno.id; nome = 'Aluno atualizado pelo smoke' }
$null = Invoke-Api PUT '/instrutor' @{ id = $instrutor.id; nome = 'Instrutor atualizado pelo smoke' }
$null = Invoke-Api PUT '/usuario' @{ id = $usuario.id; login = "smoke-atualizado$suffix" }

$scheduledDate = (Get-Date).Date.AddDays(7).AddHours(10)
while ($scheduledDate.DayOfWeek -eq [DayOfWeek]::Sunday) { $scheduledDate = $scheduledDate.AddDays(1) }
$instrucao = Invoke-Api POST '/instrucao' @{
    idAluno = $aluno.id; idInstrutor = $instrutor.id; dataHora = $scheduledDate.ToString('dd/MM/yyyy - HH:mm')
}
$null = Invoke-Api GET "/instrucao/$($instrucao.id)"
$null = Invoke-Api GET '/instrucao'
$null = Invoke-Api PUT '/instrucao' @{ id = $instrucao.id; dataHora = $scheduledDate.AddHours(1).ToString('dd/MM/yyyy - HH:mm') }
$null = Invoke-Api DELETE "/instrucao/$($instrucao.id)?motivo=ALUNO_DESISTIU"
$cancelada = Invoke-Api GET "/instrucao/$($instrucao.id)"
Assert-Condition ($cancelada.status -eq 'CANCELADA') 'Cancelamento não foi persistido'

$null = Invoke-Api DELETE "/aluno/$($aluno.id)"
$null = Invoke-Api DELETE "/instrutor/$($instrutor.id)"
$null = Invoke-Api DELETE "/usuario/$($usuario.id)"

if (-not $SkipExternal) {
    $endereco = Invoke-Api GET '/endereco/cep/01001000'
    Assert-Condition ($endereco.cidade -eq 'São Paulo') 'ViaCEP não retornou São Paulo'
}
Write-Output 'OK: autenticação, Swagger, CORS, CRUDs e agenda verificados.'
if (-not $SkipExternal) { Write-Output 'OK: consulta real ao ViaCEP verificada.' }
