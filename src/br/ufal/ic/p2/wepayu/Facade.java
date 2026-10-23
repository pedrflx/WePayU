package br.ufal.ic.p2.wepayu;

import br.ufal.ic.p2.wepayu.Exception.*;
import br.ufal.ic.p2.wepayu.models.empregados.Empregado;
import br.ufal.ic.p2.wepayu.folha.FolhaDePagamento;
import br.ufal.ic.p2.wepayu.persistencia.PersistenciaXML;
import br.ufal.ic.p2.wepayu.sistema.FabricaEmpregado;
import br.ufal.ic.p2.wepayu.sistema.Historico;
import br.ufal.ic.p2.wepayu.sistema.RepositorioEmpregados;
import br.ufal.ic.p2.wepayu.sistema.Sindicato;
import br.ufal.ic.p2.wepayu.utils.Conversor;
import br.ufal.ic.p2.wepayu.sistema.AgendasDePagamento;

public class Facade
{

    private PersistenciaXML persistencia = new PersistenciaXML("dados.xml");
    private RepositorioEmpregados repositorio;
    private Historico historico;
    private Sindicato sindicato;
    private AgendasDePagamento agendas = new AgendasDePagamento();
    private boolean encerrado = false;

    /*
        construtor roda sempre que o EasyAccept cria uma Facade nova, uma para cada arquiv de teste
        carrega os empregads salvos no dados.xml e guarda essa lista como o primeiro estado do histórico
        não chama salvarEstado para o histórico não começar com um estado a mais
    */

    public Facade() throws Exception
    {
        repositorio = new RepositorioEmpregados(persistencia.carregar());
        historico = new Historico(repositorio.getEmpregados());
        sindicato = new Sindicato(repositorio);
    }

    /*
        salvarEstado
        (aTUALIZAÇÃO) pede ao histórico para guardar uma cópia da lista atual, é chamado depois de cada comando que altera alguma coisa
        comandos que dão erro param antes de chegar aqui, por isso não entram no undo
    */

    private void salvarEstado()
    {
        historico.salvar(repositorio.getEmpregados());
    }

    private void verificarSistemaAtivo() throws WePayUException
    {
        if (encerrado)
        {
            throw new SistemaEncerradoException();
        }
    }

    /*
        undo
        volta para o estado anterior do histórico
        se já estiver no primeiro estado lança NenhumComandoParaDesfazerException
    */

    public void undo() throws Exception
    {
        verificarSistemaAtivo();
        repositorio.restaurar(historico.desfazer());    // o histórico devolve a lista daquele momento, e o repositório passa a usar ela
    }

    /*
        redo
        avança para o próximo estado do histórico, refazendo o que o undo desfez
        se já estiver no último estado lança NenhumComandoParaRefazerException
    */

    public void redo() throws Exception
    {
        verificarSistemaAtivo();

        repositorio.restaurar(historico.refazer());
    }

    /*
        zerarSistema
        apaga todos os empregados e deixa o sistema ativo de novo
        entra no histórico como um comando normal, então também pode ser desfeito com undo
    */

    public void zerarSistema()
    {
        repositorio.zerar();
        agendas.zerar();
        encerrado = false;

        salvarEstado();
    }

    /*
        encerrarSistema
        salva os empregados no dados.xml e marca o sistema como encerrado
        é o que permite os testes _1 encontrarem os dados quando uma Facade nova for criada
    */

    public void encerrarSistema() throws Exception
    {
        persistencia.salvar(repositorio.getEmpregados());
        encerrado = true;
    }


    private String adicionarEmpregado(Empregado novo)
    {
        String id = repositorio.adicionar(novo);
        salvarEstado();

        return id;
    }

    public String getNumeroDeEmpregados() throws Exception
    {
        return String.valueOf(repositorio.contar());
    }

    public String getEmpregadoPorNome(String nome, String indice) throws Exception
    {
        return repositorio.buscarIdPorNome(nome, indice);
    }

    public String criarEmpregado(String nome, String endereco, String tipo, String salario) throws Exception
    {
        return adicionarEmpregado(FabricaEmpregado.criar(nome, endereco, tipo, salario));
    }

    public String criarEmpregado(String nome, String endereco, String tipo, String salario, String comissao) throws Exception
    {
        return adicionarEmpregado(FabricaEmpregado.criar(nome, endereco, tipo, salario, comissao));
    }

        /*
        removerEmpregado
        (atualizado)  o repositório coloca null na posição do empregado em vez de apagar, assim os ids dos outros empregados não mudam
    */

    public void removerEmpregado(String emp) throws Exception
    {
        repositorio.remover(emp);
        salvarEstado();
    }

    public String getAtributoEmpregado(String emp, String atributo) throws Exception
    {
        return repositorio.buscar(emp).getAtributo(atributo);
    }

    /*
        lancaCartao
        repassa o cartão de ponto para o empregado
        se ele não for horista, a versão do Empregado lança EmpregadoNaoEhHoristaException
        só salva o estado se deu certo, então comandos com erro não entram no undo
    */

    public void lancaCartao(String emp, String data, String horas) throws Exception
    {
        repositorio.buscar(emp).registrarCartao(data, horas);
        salvarEstado();
    }

    public String getHorasNormaisTrabalhadas(String emp, String dataInicial, String dataFinal) throws Exception
    {
        return repositorio.buscar(emp).getHorasNormaisTrabalhadas(dataInicial, dataFinal);
    }

    public String getHorasExtrasTrabalhadas(String emp, String dataInicial, String dataFinal) throws Exception
    {
        return repositorio.buscar(emp).getHorasExtrasTrabalhadas(dataInicial, dataFinal);
    }

    public void lancaVenda(String emp, String data, String valor) throws Exception
    {
        repositorio.buscar(emp).registrarVenda(data, valor);
        salvarEstado();
    }

    public String getVendasRealizadas(String emp, String dataInicial, String dataFinal) throws Exception
    {
        return repositorio.buscar(emp).getVendasRealizadas(dataInicial, dataFinal);
    }

    public void alteraEmpregado(String emp, String atributo, String valor) throws Exception
    {
        Empregado empregadoTemporario = repositorio.buscar(emp);

        // trocar o tipo cria outro objeto, então quem faz é a fábrica. o resto o próprio empregado resolve
        if (atributo.equals("tipo"))
        {
            repositorio.substituir(emp, FabricaEmpregado.converterTipo(empregadoTemporario, valor));
        }
        else if (atributo.equals("agendaPagamento"))
        {
            empregadoTemporario.setAgendaPagamento(agendas.buscar(valor));
        }
        else
        {
            empregadoTemporario.alterarAtributo(atributo, valor);
        }

        salvarEstado();
    }

    public void alteraEmpregado(String emp, String atributo, String valor, String ext) throws Exception
    {
        Empregado empregadoTemporario = repositorio.buscar(emp);

        if (!atributo.equals("tipo"))
        {
            throw new AtributoNaoExisteException();
        }

        repositorio.substituir(emp, FabricaEmpregado.converterTipo(empregadoTemporario, valor, ext));
        salvarEstado();
    }

    public void alteraEmpregado(String emp, String atributo, String valor, String idSindicato, String taxaSindical) throws Exception
    {
        Empregado empregadoTemporario = repositorio.buscar(emp);

        if (!atributo.equals("sindicalizado"))
        {
            throw new AtributoNaoExisteException();
        }

        sindicato.filiar(empregadoTemporario, valor, idSindicato, taxaSindical);
        salvarEstado();
    }

    public void alteraEmpregado(String emp, String atributo, String valor, String banco, String agencia, String contaCorrente) throws Exception
    {
        repositorio.buscar(emp).alterarAtributo(atributo, valor, banco, agencia, contaCorrente);
        salvarEstado();
    }

    public void lancaTaxaServico(String membro, String data, String valor) throws Exception
    {
        sindicato.buscarMembro(membro).registrarTaxa(data, valor);
        salvarEstado();
    }

    public String getTaxasServico(String emp, String dataInicial, String dataFinal) throws Exception
    {
        return repositorio.buscar(emp).getTaxasServico(dataInicial, dataFinal);
    }

    // monta a folha do dia só com quem recebe nessa data
    private FolhaDePagamento montarFolha(String data) throws WePayUException
    {
        FolhaDePagamento folha = new FolhaDePagamento(Conversor.converterData(data, new DataInvalidaException()));
        folha.adicionarQuemRecebe(repositorio.getEmpregados());

        return folha;
    }

    public String totalFolha(String data) throws Exception
    {
        return Conversor.formatarValor(montarFolha(data).calcularTotal());
    }

    /*
        rodaFolha
        monta a folha com quem recebe nessa data e grava o relatório no arquivo de saída
        entra no histórico do undo como os outros comandos
    */

    public void rodaFolha(String data, String saida) throws Exception
    {
        montarFolha(data).gravar(saida);
        salvarEstado();
    }

    public void criarAgendaDePagamentos(String descricao) throws Exception
    {
        agendas.criar(descricao);
    }
}
