# WePayU

Sistema de folha de pagamento desenvolvido para a disciplina de Programação 2.
Implementa as user stories 1 a 8 e passa em todos os testes de aceitação (us1 a us8 e us1_1 a us6_1).

## Como rodar

Abrir o projeto no IntelliJ e executar a classe `Main`, que roda os testes com o EasyAccept (`lib/easyaccept.jar`).

## Estratégia de desenvolvimento

O projeto foi feito em duas fases.

Na primeira, o objetivo foi fazer o sistema funcionar. A lógica ficou concentrada na `Facade`, de forma mais estruturada,
e cada user story foi implementada e conferida com os testes antes de passar para a próxima.

Com todos os testes passando, a segunda fase foi reorganizar o código aos poucos, rodando os testes a cada mudança
para garantir que nada quebrasse:

- exceções personalizadas para cada erro, todas filhas de `WePayUException`
- validações repetidas centralizadas na classe `Conversor`
- `Empregado` abstrato com `EmpregadoHorista`, `EmpregadoAssalariado` e `EmpregadoComissionado`, trocando os `if` de tipo por polimorfismo
- hierarquias para lançamentos, formas de pagamento e seções da folha de pagamento
- undo/redo com `ArrayList` e persistência dos dados em XML
- por fim, comentários explicando as classes e os métodos principais

## Estrutura

- `models`: guarda as classes do sistema (empregados, lançamentos, formas de pagamento, sindicato e folha de pagamento)
- `Exception`: guarda as exceções criadas
- `utils`: guarda a classe `Conversor` (que executa validações e formatações)
- `persistencia`: guarda a classe `PersistenciaXML` (que salva e carrega os dados)
