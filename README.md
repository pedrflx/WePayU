# WePayU

Sistema de folha de pagamento desenvolvido para a disciplina de Programação 2.
Implementa as user stories 1 a 10 e passa em todos os testes de aceitação e persistência (us1 a us10 e us1_1 a us6_1, us9_1 e us10_1).

## Estratégia de desenvolvimento

O projeto foi feito em três fases.

Na primeira, o objetivo foi fazer o sistema funcionar. A lógica ficou concentrada na Facade, de forma mais estruturada,
e cada user story foi implementada e conferida com os testes antes de passar para a próxima.

Com todos os testes passando, a segunda fase foi reorganizar o código aos poucos, rodando os testes a cada mudança
para garantir que nada quebrasse:

- exceções personalizadas para cada erro, todas filhas de WePayUException
- validações repetidas centralizadas na classe Conversor
- Empregado abstrato com EmpregadoHorista, EmpregadoAssalariado e EmpregadoComissionado, trocando os if de tipo por polimorfismo
- hierarquias para lançamentos, formas de pagamento e seções da folha de pagamento
- undo/redo com ArrayList e persistência dos dados em XML
- por fim, comentários explicando as classes e os métodos principais

A terceira fase começou pelo feedback do Milestone 1 e depois seguiu para as user stories 9 e 10,
sempre no mesmo ritmo: uma mudança de cada vez e todos os testes rodando depois de cada uma:

- a Facade passou a só repassar os comandos
- as regras foram para classes próprias no package sistema (RepositorioEmpregados, Historico,
- FabricaEmpregado, Sindicato e AgendasDePagamento)
- leitura e alteração de atributos passaram a ser feitas pelo próprio Empregado
- a FolhaDePagamento passou a calcular o total e a gravar o próprio arquivo
- correção da troca de tipo para comissionado sem comissão, que deixava a comissão nula
- models dividido em subpackages e a folha de pagamento separada em package próprio
- persistência com DTOs: o XML guarda só objetos de dados, e na leitura os empregados são recriados
  pelos construtores e métodos normais, passando pelas validações; assim os modelos não têm mais
  construtor vazio nem setters sem validação
- agendas de pagamento (US 9 e 10): hierarquia AgendaPagamento com AgendaSemanal, AgendaMensal
  e AgendaMensalUltimoDia
- cada agenda calcula os dias de pagamento, o período e a parte fixa do salário,
  e cada tipo de empregado só define qual é a sua agenda padrão

## Estrutura

- models: guarda as classes do sistema, divididas em subpackages
  - empregados: Empregado e os três tipos de empregado
  - lancamentos: cartões de ponto, resultados de venda e taxas de serviço
  - pagamento: formas de pagamento (em mãos, correios e banco)
  - sindicato: filiação do empregado ao sindicato
  - agendas: agendas de pagamento
- folha: guarda a FolhaDePagamento e as seções do relatório
- sistema: guarda as classes que a Facade usa para executar os comandos (repositório de empregados, histórico do undo/redo, fábrica de empregados, sindicato e agendas disponíveis)
- Exception: guarda as exceções criadas
- utils: guarda a classe Conversor (que executa validações e formatações)
- persistencia: guarda a classe PersistenciaXML (que salva e carrega os dados) e os DTOs usados no arquivo XML
