package br.ufal.ic.p2.wepayu;

import br.ufal.ic.p2.wepayu.Exception.*;
import br.ufal.ic.p2.wepayu.models.Banco;
import br.ufal.ic.p2.wepayu.models.Correios;
import br.ufal.ic.p2.wepayu.models.EmMaos;
import br.ufal.ic.p2.wepayu.models.Empregado;
import br.ufal.ic.p2.wepayu.models.EmpregadoAssalariado;
import br.ufal.ic.p2.wepayu.models.EmpregadoComissionado;
import br.ufal.ic.p2.wepayu.models.EmpregadoHorista;
import br.ufal.ic.p2.wepayu.models.FolhaDePagamento;
import br.ufal.ic.p2.wepayu.models.MembroSindicato;
import br.ufal.ic.p2.wepayu.models.MetodoPagamento;
import br.ufal.ic.p2.wepayu.persistencia.PersistenciaXML;
import br.ufal.ic.p2.wepayu.utils.Conversor;
import java.util.ArrayList;
import java.util.Date;

public class Facade
{

    private PersistenciaXML persistencia = new PersistenciaXML("dados.xml");
    private ArrayList<Empregado> empregados;
    private ArrayList<ArrayList<Empregado>> historico = new ArrayList<>();
    private int estadoAtual = 0;
    private boolean encerrado = false;

    /*
        construtor roda sempre que o EasyAccept cria uma Facade nova, uma para cada arquiv de teste
        carrega os empregads salvos no dados.xml e guarda essa lista como o primeiro estado do histórico
        não chama salvarEstado para o histórico não começar com um estado a mais
    */

    public Facade() throws Exception
    {
        empregados = persistencia.carregar();
        historico.add(copiarEmpregados(empregados));
    }

    private ArrayList<Empregado> copiarEmpregados(ArrayList<Empregado> origem)
    {
        ArrayList<Empregado> copia = new ArrayList<>();
        for (Empregado empregado : origem)
        {
            if (empregado == null)
            {
                copia.add(null);
            }
            else
            {
                copia.add(empregado.clonar());
            }
        }
        return copia;
    }

    /*
        salvarEstado
        guarda uma cópia da lista atual no histórico, é chamado depois de cada comando que altera alguma coisa
        se houve undo antes, apaga os estados que estavam na frente para o redo não refazer algo que não vale mais
    */

    private void salvarEstado()
    {
        while (historico.size() > estadoAtual + 1)
        {
            historico.remove(historico.size() - 1);
        }

        historico.add(copiarEmpregados(empregados));
        estadoAtual++;
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

        if (estadoAtual <= 0)
        {
            throw new NenhumComandoParaDesfazerException();
        }

        estadoAtual--;
        empregados = copiarEmpregados(historico.get(estadoAtual));
    }

    /*
        redo
        avança para o próximo estado do histórico, refazendo o que o undo desfez
        se já estiver no último estado lança NenhumComandoParaRefazerException
    */

    public void redo() throws Exception
    {
        verificarSistemaAtivo();

        if (estadoAtual >= historico.size() - 1)
        {
            throw new NenhumComandoParaRefazerException();
        }

        estadoAtual++;
        empregados = copiarEmpregados(historico.get(estadoAtual));
    }

    /*
        zerarSistema
        apaga todos os empregados e deixa o sistema ativo de novo
        entra no histórico como um comando normal, então também pode ser desfeito com undo
    */

    public void zerarSistema()
    {
        empregados = new ArrayList<>();
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
        persistencia.salvar(empregados);
        encerrado = true;
    }

    /*
        buscarPosicao
        transforma o id que vem do teste na posição do empregado na lista
        o id é a posição + 1, então o empregado de id 1 fica na posição 0
        lança IdentificacaoEmpregadoNulaException se o id vier vazio e EmpregadoNaoExisteException se não for número, estiver fora da lista ou o empregado tiver sido removido
    */

    private int buscarPosicao(String emp) throws WePayUException
    {
        Conversor.validarTexto(emp, new IdentificacaoEmpregadoNulaException());

        int id = Conversor.converterInteiro(emp, new EmpregadoNaoExisteException());

        if (id <= 0 || id > empregados.size() || empregados.get(id - 1) == null)
        {
            throw new EmpregadoNaoExisteException();
        }

        return id - 1;
    }

    private Empregado buscarEmpregado(String emp) throws WePayUException
    {
        return empregados.get(buscarPosicao(emp));
    }

    private String adicionarEmpregado(Empregado novo)
    {
        empregados.add(novo);
        salvarEstado();

        return String.valueOf(empregados.size());
    }

    /*
        converterTipo
        cria um empregado novo do tipo pedido copiando os dados comuns do empregado atual
        é usado para trocar o tipo, já que um objeto não pode mudar de classe
        lança TipoInvalidoException se o tipo não for horista, assalariado ou comissionado
    */

    private Empregado converterTipo(Empregado empregado, String tipo) throws WePayUException
    {
        if (tipo.equals("horista")) return new EmpregadoHorista(empregado);
        if (tipo.equals("assalariado")) return new EmpregadoAssalariado(empregado);
        if (tipo.equals("comissionado")) return new EmpregadoComissionado(empregado);
        throw new TipoInvalidoException();
    }

    private MetodoPagamento converterMetodoPagamento(String metodo) throws WePayUException
    {
        if (metodo.equals("emMaos")) return new EmMaos();
        if (metodo.equals("correios")) return new Correios();
        throw new MetodoPagamentoInvalidoException();
    }

    private Empregado buscarMembro(String idMembro)
    {
        for (Empregado empregado : empregados)
        {
            if (empregado != null && empregado.pertenceAoSindicato(idMembro))
            {
                return empregado;
            }
        }
        return null;
    }

    private String completarCentavos(String valor)
    {
        if (!valor.contains(","))
        {
            return valor + ",00";
        }
        return valor;
    }

    public String getNumeroDeEmpregados() throws Exception
    {
        int count = 0;
        for (Empregado empregado : empregados)
        {
            if (empregado != null) count++;
        }
        return String.valueOf(count);
    }

    public String getEmpregadoPorNome(String nome, String indice) throws Exception
    {
        int ind = Conversor.converterInteiro(indice, new IndiceInvalidoException());

        int count = 0;
        for (int i = 0; i < empregados.size(); i++)
        {
            if (empregados.get(i) != null && empregados.get(i).getNome().equals(nome))
            {
                count++;
                if (count == ind) return String.valueOf(i + 1);
            }
        }
        throw new EmpregadoNaoEncontradoPorNomeException();
    }

    public String criarEmpregado(String nome, String endereco, String tipo, String salario) throws Exception
    {
        if (tipo.equals("horista")) return adicionarEmpregado(new EmpregadoHorista(nome, endereco, salario));
        if (tipo.equals("assalariado")) return adicionarEmpregado(new EmpregadoAssalariado(nome, endereco, salario));
        if (tipo.equals("comissionado")) throw new TipoNaoAplicavelException();
        throw new TipoInvalidoException();
    }

    public String criarEmpregado(String nome, String endereco, String tipo, String salario, String comissao) throws Exception
    {
        if (tipo.equals("comissionado")) return adicionarEmpregado(new EmpregadoComissionado(nome, endereco, salario, comissao));
        if (tipo.equals("horista") || tipo.equals("assalariado")) throw new TipoNaoAplicavelException();
        throw new TipoInvalidoException();
    }

    /*
        removerEmpregado
        coloca null na posição do empregado em vez de apagar, assim os ids dos outros empregados não mudam
    */

    public void removerEmpregado(String emp) throws Exception
    {
        empregados.set(buscarPosicao(emp), null);
        salvarEstado();
    }

    public String getAtributoEmpregado(String emp, String atributo) throws Exception
    {
        Empregado empregadoTemporario = buscarEmpregado(emp);

        if (atributo.equals("nome"))
        {
            return empregadoTemporario.getNome();
        }
        else if (atributo.equals("endereco"))
        {
            return empregadoTemporario.getEndereco();
        }
        else if (atributo.equals("tipo"))
        {
            return empregadoTemporario.getTipo();
        }
        else if (atributo.equals("sindicalizado"))
        {
            return empregadoTemporario.getSindicalizado();
        }
        else if (atributo.equals("idSindicato"))
        {
            return empregadoTemporario.getIdSindicato();
        }
        else if (atributo.equals("taxaSindical"))
        {
            return completarCentavos(empregadoTemporario.getTaxaSindical());
        }
        else if (atributo.equals("salario"))
        {
            return completarCentavos(empregadoTemporario.getSalario());
        }
        else if (atributo.equals("comissao"))
        {
            return empregadoTemporario.getComissao();
        }
        else if (atributo.equals("metodoPagamento"))
        {
            return empregadoTemporario.getMetodoPagamento().getNome();
        }
        else if (atributo.equals("banco"))
        {
            return empregadoTemporario.getMetodoPagamento().getBanco();
        }
        else if (atributo.equals("agencia"))
        {
            return empregadoTemporario.getMetodoPagamento().getAgencia();
        }
        else if (atributo.equals("contaCorrente"))
        {
            return empregadoTemporario.getMetodoPagamento().getContaCorrente();
        }

        throw new AtributoNaoExisteException();
    }

    /*
        lancaCartao
        repassa o cartão de ponto para o empregado
        se ele não for horista, a versão do Empregado lança EmpregadoNaoEhHoristaException
        só salva o estado se deu certo, então comandos com erro não entram no undo
    */

    public void lancaCartao(String emp, String data, String horas) throws Exception
    {
        buscarEmpregado(emp).registrarCartao(data, horas);
        salvarEstado();
    }

    public String getHorasNormaisTrabalhadas(String emp, String dataInicial, String dataFinal) throws Exception
    {
        return buscarEmpregado(emp).getHorasNormaisTrabalhadas(dataInicial, dataFinal);
    }

    public String getHorasExtrasTrabalhadas(String emp, String dataInicial, String dataFinal) throws Exception
    {
        return buscarEmpregado(emp).getHorasExtrasTrabalhadas(dataInicial, dataFinal);
    }

    public void lancaVenda(String emp, String data, String valor) throws Exception
    {
        buscarEmpregado(emp).registrarVenda(data, valor);
        salvarEstado();
    }

    public String getVendasRealizadas(String emp, String dataInicial, String dataFinal) throws Exception
    {
        return buscarEmpregado(emp).getVendasRealizadas(dataInicial, dataFinal);
    }

    public void alteraEmpregado(String emp, String atributo, String valor) throws Exception
    {
        int posicao = buscarPosicao(emp);
        Empregado empregadoTemporario = empregados.get(posicao);

        if (atributo.equals("nome"))
        {
            empregadoTemporario.setNome(valor);
        }
        else if (atributo.equals("endereco"))
        {
            empregadoTemporario.setEndereco(valor);
        }
        else if (atributo.equals("tipo"))
        {
            empregados.set(posicao, converterTipo(empregadoTemporario, valor));
        }
        else if (atributo.equals("salario"))
        {
            empregadoTemporario.setSalario(valor);
        }
        else if (atributo.equals("comissao"))
        {
            empregadoTemporario.alterarComissao(valor);
        }
        else if (atributo.equals("metodoPagamento"))
        {
            empregadoTemporario.setMetodoPagamento(converterMetodoPagamento(valor));
        }
        else if (atributo.equals("sindicalizado"))
        {
            if (!valor.equals("true") && !valor.equals("false")) throw new ValorNaoBooleanoException();
            if (valor.equals("false"))
            {
                empregadoTemporario.setMembroSindicato(null);
            }
        }
        else
        {
            throw new AtributoNaoExisteException();
        }

        salvarEstado();
    }

    public void alteraEmpregado(String emp, String atributo, String valor, String ext) throws Exception
    {
        int posicao = buscarPosicao(emp);

        if (!atributo.equals("tipo"))
        {
            throw new AtributoNaoExisteException();
        }

        Empregado novo = converterTipo(empregados.get(posicao), valor);

        if (valor.equals("comissionado"))
        {
            novo.alterarComissao(ext);
        }
        else
        {
            novo.setSalario(ext);
        }

        empregados.set(posicao, novo);
        salvarEstado();
    }

    public void alteraEmpregado(String emp, String atributo, String valor, String idSindicato, String taxaSindical) throws Exception
    {
        Empregado empregadoTemporario = buscarEmpregado(emp);

        if (atributo.equals("sindicalizado"))
        {
            if (!valor.equals("true") && !valor.equals("false")) throw new ValorNaoBooleanoException();
            if (valor.equals("true"))
            {
                MembroSindicato membro = new MembroSindicato(idSindicato, taxaSindical);

                if (buscarMembro(idSindicato) != null)
                {
                    throw new IdSindicatoDuplicadoException();
                }

                empregadoTemporario.setMembroSindicato(membro);
            }
        }
        else
        {
            throw new AtributoNaoExisteException();
        }

        salvarEstado();
    }

    public void alteraEmpregado(String emp, String atributo, String valor, String banco, String agencia, String contaCorrente) throws Exception
    {
        Empregado empregadoTemporario = buscarEmpregado(emp);

        if (atributo.equals("metodoPagamento") && valor.equals("banco"))
        {
            empregadoTemporario.setMetodoPagamento(new Banco(banco, agencia, contaCorrente));
        }
        else
        {
            throw new AtributoNaoExisteException();
        }

        salvarEstado();
    }

    public void lancaTaxaServico(String membro, String data, String valor) throws Exception
    {
        Conversor.validarTexto(membro, new IdentificacaoMembroNulaException());

        Empregado empMembro = buscarMembro(membro);

        if (empMembro == null) throw new MembroNaoExisteException();

        empMembro.registrarTaxa(data, valor);
        salvarEstado();
    }

    public String getTaxasServico(String emp, String dataInicial, String dataFinal) throws Exception
    {
        return buscarEmpregado(emp).getTaxasServico(dataInicial, dataFinal);
    }

    public String totalFolha(String data) throws Exception
    {
        Date payday = Conversor.converterData(data, new DataInvalidaException());

        double totalGeral = 0;
        for (Empregado empregado : empregados)
        {
            if (empregado != null && empregado.ehDiaDePagamento(payday))
            {
                totalGeral += empregado.calcularSalarioBruto(payday);
            }
        }
        return Conversor.formatarValor(totalGeral);
    }

    /*
        rodaFolha
        monta a folha com quem recebe nessa data e grava o relatório no arquivo de saída
        entra no histórico do undo como os outros comandos
    */

    public void rodaFolha(String data, String saida) throws Exception
    {
        Date payday = Conversor.converterData(data, new DataInvalidaException());
        FolhaDePagamento folha = new FolhaDePagamento(payday);

        for (Empregado empregado : empregados)
        {
            if (empregado != null && empregado.ehDiaDePagamento(payday))
            {
                folha.adicionar(empregado);
            }
        }

        java.io.PrintWriter writer = new java.io.PrintWriter(saida, "UTF-8");
        writer.print(folha.gerarRelatorio());
        writer.close();
        salvarEstado();
    }

}
