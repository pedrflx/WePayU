package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.Exception.*;
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

    /*
        Empregado
        construtor vazio usado pelo XMLDecoder para recriar o objeto quando os dados são carregados
        pelo mesmo motivo todas as classes salvas no XML têm construtor vazio e setters
    */

    public Empregado()
    {
    }

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
        if (outro.membroSindicato != null)
        {
            this.membroSindicato = new MembroSindicato(outro.membroSindicato);
        }
    }

    /*
        métodos abstratos
        o que muda de um tipo de empregado para outro, cada filha é obrigada a implementar
        tipo, cópia, datas e período de pagamento, salário bruto, valores da folha e seção do relatório
    */

    public abstract String getTipo();

    public abstract Empregado clonar();

    public abstract Date getPrimeiroPagamento();

    public abstract Date getProximoPagamento(Date atual);

    public abstract Date getInicioPeriodo(Date pagamento);

    public abstract int getDiasPeriodo(Date pagamento);

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

    public MembroSindicato getMembroSindicato()
    {
        return membroSindicato;
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
}
