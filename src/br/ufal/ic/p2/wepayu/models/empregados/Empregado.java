package br.ufal.ic.p2.wepayu.models.empregados;

import br.ufal.ic.p2.wepayu.Exception.*;
import br.ufal.ic.p2.wepayu.folha.FolhaDePagamento;
import br.ufal.ic.p2.wepayu.folha.SecaoFolha;
import br.ufal.ic.p2.wepayu.models.agendas.AgendaPagamento;
import br.ufal.ic.p2.wepayu.models.pagamento.Banco;
import br.ufal.ic.p2.wepayu.models.pagamento.EmMaos;
import br.ufal.ic.p2.wepayu.models.pagamento.MetodoPagamento;
import br.ufal.ic.p2.wepayu.models.sindicato.MembroSindicato;
import br.ufal.ic.p2.wepayu.persistencia.EmpregadoDTO;
import br.ufal.ic.p2.wepayu.utils.Conversor;

import java.util.Date;

/*
    Empregado
    mãe dos três tipos de empregado: EmpregadoHorista, EmpregadoAssalariado e EmpregadoComissionado
    guarda o que todos têm em comum: nome, endereço, salário, forma de pagamento e sindicato
    é abstrata porque qualquer empregado é de um dos três tipos, e o que muda de um tipo para outro são métodos abstratos
*/

public abstract class Empregado
{

    private String nome;
    private String endereco;
    private String salario;

    private MetodoPagamento metodoPagamento = new EmMaos();
    private MembroSindicato membroSindicato;
    private AgendaPagamento agendaPagamento;

    /*
        Empregado
        cria o empregado passando pelos setters, que validam cada valor
        se algum valor for inválido a exceção impede o objeto de ser criado
    */

    public Empregado(String nome, String endereco, String salario) throws WePayUException
    {
        setNome(nome);
        setEndereco(endereco);
        setSalario(salario);
        usarAgendaPadrao();
    }

    /*
        Empregado
        construtor de cópia, copia os dados comuns de outro empregado
        é usado para trocar o tipo e para clonar no undo/redo, o sindicato é copiado para não ficar compartilhado
    */

    public Empregado(Empregado outro)
    {
        this.nome = outro.nome;
        this.endereco = outro.endereco;
        this.salario = outro.salario;
        this.metodoPagamento = outro.metodoPagamento;
        this.agendaPagamento = outro.agendaPagamento;
        if (outro.membroSindicato != null)
        {
            this.membroSindicato = new MembroSindicato(outro.membroSindicato);
        }
    }

    /*
        métodos abstratos
        o que muda de um tipo de empregado para outro, cada filha é obrigada a implementar
        tipo, cópia, agenda de pagamento padrão, salário bruto, valores da folha e seção do relatório
    */

    public abstract String getTipo();

    public abstract Empregado clonar();

    public abstract AgendaPagamento criarAgendaPadrao() throws WePayUException;

    public abstract double calcularSalarioBruto(Date pagamento);

    public abstract double[] calcularValoresFolha(Date pagamento);

    public abstract SecaoFolha escolherSecao(FolhaDePagamento folha);

    /*
        métodos exclusivos de um tipo
        por padrão lançam o erro de que o empregado não é comissionado ou não é horista
        só a filha certa sobrescreve com o comportamento real, é o polimorfismo que substitui os if de tipo
    */

    public String getComissao() throws WePayUException
    {
        throw new EmpregadoNaoEhComissionadoException();
    }

    public void alterarComissao(String comissao) throws WePayUException
    {
        throw new EmpregadoNaoEhComissionadoException();
    }

    public void registrarVenda(String data, String valor) throws WePayUException
    {
        throw new EmpregadoNaoEhComissionadoException();
    }

    public String getVendasRealizadas(String dataInicial, String dataFinal) throws WePayUException
    {
        throw new EmpregadoNaoEhComissionadoException();
    }

    public void registrarCartao(String data, String horas) throws WePayUException
    {
        throw new EmpregadoNaoEhHoristaException();
    }

    public String getHorasNormaisTrabalhadas(String dataInicial, String dataFinal) throws WePayUException
    {
        throw new EmpregadoNaoEhHoristaException();
    }

    public String getHorasExtrasTrabalhadas(String dataInicial, String dataFinal) throws WePayUException
    {
        throw new EmpregadoNaoEhHoristaException();
    }

    // volta para a agenda de pagamento padrão do tipo. usado na criação e na troca de tipo
    public void usarAgendaPadrao() throws WePayUException
    {
        agendaPagamento = criarAgendaPadrao();
    }

    public void setAgendaPagamento(AgendaPagamento agendaPagamento)
    {
        this.agendaPagamento = agendaPagamento;
    }

    // a partir de quando o empregado pode receber, o horista sobrescreve com o dia do primeiro cartão
    public Date getDataInicio()
    {
        return Conversor.criarData(1, 1, 2005);
    }

    // as datas e o período de pagamento vêm todos da agenda
    public Date getPrimeiroPagamento()
    {
        return agendaPagamento.getPrimeiroPagamentoAPartirDe(getDataInicio());
    }

    public Date getProximoPagamento(Date atual)
    {
        return agendaPagamento.getProximoPagamento(atual);
    }

    public Date getInicioPeriodo(Date pagamento)
    {
        return agendaPagamento.getInicioPeriodo(pagamento);
    }

    public int getDiasPeriodo(Date pagamento)
    {
        return agendaPagamento.getDiasPeriodo(pagamento);
    }

    // parte fixa do salário em cada pagamento, que depende de a agenda ser semanal ou mensal
    public double calcularSalarioFixo()
    {
        return agendaPagamento.calcularSalarioFixo(Conversor.lerNumero(salario));
    }

    /*
        ehDiaDePagamento
        diz se o empregado recebe nessa data
        começa no primeiro pagamento e vai pulando para o próximo até alcançar ou passar da data
    */

    public boolean ehDiaDePagamento(Date data)
    {
        Date atual = getPrimeiroPagamento();
        while (atual.before(data))
        {
            atual = getProximoPagamento(atual);
        }
        return atual.equals(data);
    }

    /*
        calcularPagamento
        devolve o bruto, o desconto e o líquido de um pagamento, nessa ordem
        o desconto junta a taxa do período com a dívida anterior com o sindicato e nunca passa do salário bruto
    */

    public double[] calcularPagamento(Date data)
    {
        double bruto = calcularSalarioBruto(data);
        double descontoTotal = Conversor.truncar(calcularDescontoPeriodo(data) + calcularDebitoSindicato(data));
        double desconto = Math.min(bruto, descontoTotal);
        return new double[]{bruto, desconto, bruto - desconto};
    }

    private double calcularDescontoPeriodo(Date data)
    {
        if (membroSindicato == null) return 0.0;

        return membroSindicato.calcularDesconto(getInicioPeriodo(data), Conversor.adicionarDias(data, 1), getDiasPeriodo(data));
    }

    /*
        calcularDebitoSindicato
        dívida com o sindicato que sobrou dos pagamentos anteriores
        quando o salário de um pagamento não cobre o desconto, a diferença passa para o próximo pagamento
    */

    private double calcularDebitoSindicato(Date data)
    {
        if (membroSindicato == null) return 0.0;

        double debito = 0.0;
        Date atual = getPrimeiroPagamento();

        while (atual.before(data))
        {
            double bruto = calcularSalarioBruto(atual);
            double devido = debito + calcularDescontoPeriodo(atual);

            if (bruto >= devido) debito = 0.0;
            else debito = devido - bruto;

            atual = getProximoPagamento(atual);
        }
        return debito;
    }

    public String formatarMetodoPagamento()
    {
        return metodoPagamento.getDescricao(endereco);
    }

    private MembroSindicato getMembro() throws WePayUException
    {
        if (membroSindicato == null)
        {
            throw new EmpregadoNaoEhSindicalizadoException();
        }
        return membroSindicato;
    }

    public boolean pertenceAoSindicato(String idMembro)
    {
        return membroSindicato != null && membroSindicato.getIdMembro().equals(idMembro);
    }

    public String getSindicalizado()
    {
        return String.valueOf(membroSindicato != null);
    }

    public String getIdSindicato() throws WePayUException
    {
        return getMembro().getIdMembro();
    }

    public String getTaxaSindical() throws WePayUException
    {
        return getMembro().getTaxaSindical();
    }

    public void registrarTaxa(String data, String valor) throws WePayUException
    {
        getMembro().registrarTaxa(data, valor);
    }

    public String getTaxasServico(String dataInicial, String dataFinal) throws WePayUException
    {
        return getMembro().getTaxasServico(dataInicial, dataFinal);
    }


    public void setMembroSindicato(MembroSindicato membroSindicato)
    {
        this.membroSindicato = membroSindicato;
    }

    public String getNome()
    {
        return nome;
    }

    public void setNome(String nome) throws WePayUException
    {
        Conversor.validarTexto(nome, new NomeNuloException());
        this.nome = nome;
    }

    public String getEndereco()
    {
        return endereco;
    }

    public void setEndereco(String endereco) throws WePayUException
    {
        Conversor.validarTexto(endereco, new EnderecoNuloException());
        this.endereco = endereco;
    }

    public String getSalario()
    {
        return salario;
    }

    public void setSalario(String salario) throws WePayUException
    {
        Conversor.converterNaoNegativo(salario, new SalarioNuloException(), new SalarioNaoNumericoException(), new SalarioNegativoException());
        this.salario = salario;
    }

    public MetodoPagamento getMetodoPagamento()
    {
        return metodoPagamento;
    }

    public void setMetodoPagamento(MetodoPagamento metodoPagamento)
    {
        this.metodoPagamento = metodoPagamento;
    }

    // devolve o valor de um atributo pelo nome que vem do teste
    // comissao, idSindicato e banco já lançam o erro certo sozinhos quando o empregado não tem aquilo
    public String getAtributo(String atributo) throws WePayUException
    {
        if (atributo.equals("nome"))
        {
            return getNome();
        }
        else if (atributo.equals("endereco"))
        {
            return getEndereco();
        }
        else if (atributo.equals("tipo"))
        {
            return getTipo();
        }
        else if (atributo.equals("sindicalizado"))
        {
            return getSindicalizado();
        }
        else if (atributo.equals("idSindicato"))
        {
            return getIdSindicato();
        }
        else if (atributo.equals("taxaSindical"))
        {
            return Conversor.completarCentavos(getTaxaSindical());
        }
        else if (atributo.equals("salario"))
        {
            return Conversor.completarCentavos(getSalario());
        }
        else if (atributo.equals("comissao"))
        {
            return getComissao();
        }
        else if (atributo.equals("metodoPagamento"))
        {
            return metodoPagamento.getNome();
        }
        else if (atributo.equals("banco"))
        {
            return metodoPagamento.getBanco();
        }
        else if (atributo.equals("agencia"))
        {
            return metodoPagamento.getAgencia();
        }
        else if (atributo.equals("contaCorrente"))
        {
            return metodoPagamento.getContaCorrente();
        }
        else if (atributo.equals("agendaPagamento"))
        {
            return agendaPagamento.getDescricao();
        }
        throw new AtributoNaoExisteException();
    }

    // altera um atributo pelo nome que vem do teste
    // o tipo não entra aqui: trocar o tipo cria outro objeto, e isso é com a fábrica
    public void alterarAtributo(String atributo, String valor) throws WePayUException
    {
        if (atributo.equals("nome"))
        {
            setNome(valor);
        }
        else if (atributo.equals("endereco"))
        {
            setEndereco(valor);
        }
        else if (atributo.equals("salario"))
        {
            setSalario(valor);
        }
        else if (atributo.equals("comissao"))
        {
            alterarComissao(valor);
        }
        else if (atributo.equals("metodoPagamento"))
        {
            setMetodoPagamento(MetodoPagamento.criar(valor));
        }
        else if (atributo.equals("sindicalizado"))
        {
            // "false" tira do sindicato. "true" sem id e taxa não muda nada
            if (!Conversor.converterBooleano(valor, new ValorNaoBooleanoException()))
            {
                setMembroSindicato(null);
            }
        }
        else
        {
            throw new AtributoNaoExisteException();
        }
    }

    // versão com os dados da conta: só serve para mudar a forma de pagamento para banco
    public void alterarAtributo(String atributo, String valor, String banco, String agencia, String contaCorrente) throws WePayUException
    {
        if (atributo.equals("metodoPagamento") && valor.equals("banco"))
        {
            setMetodoPagamento(new Banco(banco, agencia, contaCorrente));
        }
        else
        {
            throw new AtributoNaoExisteException();
        }
    }

    // escreve no DTO os dados que empregado tem, as filhas completam com o que é só delas
    public void exportarPara(EmpregadoDTO dados)
    {
        dados.setTipo(getTipo());
        dados.setNome(nome);
        dados.setEndereco(endereco);
        dados.setSalario(salario);
        dados.setAgendaPagamento(agendaPagamento.getDescricao());

        metodoPagamento.exportarPara(dados);

        if (membroSindicato != null)
        {
            membroSindicato.exportarPara(dados);
        }
    }
}
