package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.Exception.*;
import br.ufal.ic.p2.wepayu.utils.Conversor;
import java.util.ArrayList;
import java.util.Date;

/*
    EmpregadoComissionado
    empregado com salário fixo mais comissão sobre as vendas, recebe a cada 2 sextas-feiras
    guarda a comissão e os resultados de venda
*/

public class EmpregadoComissionado extends Empregado
{

    private String comissao;
    private ArrayList<ResultadoDeVenda> vendas = new ArrayList<>();

    public EmpregadoComissionado()
    {
    }

    public EmpregadoComissionado(String nome, String endereco, String salario, String comissao) throws WePayUException
    {
        super(nome, endereco, salario);
        setComissao(comissao);
    }

    public EmpregadoComissionado(Empregado outro)
    {
        super(outro);
    }

    @Override
    public String getTipo()
    {
        return "comissionado";
    }

    @Override
    public Empregado clonar()
    {
        EmpregadoComissionado copia = new EmpregadoComissionado(this);
        copia.comissao = comissao;
        copia.vendas = new ArrayList<>(vendas);
        return copia;
    }

    @Override
    public String getComissao()
    {
        return comissao;
    }

    @Override
    public void alterarComissao(String comissao) throws WePayUException
    {
        setComissao(comissao);
    }

    /*
        setComissao
        valida e guarda a comissão
        tem nome de setter para o XMLDecoder conseguir usar, por isso o Empregado usa o nome alterarComissao
    */

    public void setComissao(String comissao) throws WePayUException
    {
        Conversor.converterNaoNegativo(comissao, new ComissaoNulaException(), new ComissaoNaoNumericaException(), new ComissaoNegativaException());
        this.comissao = comissao;
    }

    @Override
    public void registrarVenda(String data, String valor) throws WePayUException
    {
        Date dataVenda = Conversor.converterData(data, new DataInvalidaException());
        double valorVenda = Conversor.converterPositivo(valor, new ValorNaoPositivoException());

        vendas.add(new ResultadoDeVenda(dataVenda, valorVenda));
    }

    @Override
    public String getVendasRealizadas(String dataInicial, String dataFinal) throws WePayUException
    {
        Date[] periodo = Conversor.converterPeriodo(dataInicial, dataFinal);
        return Conversor.formatarValor(somarVendas(periodo[0], periodo[1]));
    }

    private double somarVendas(Date inicio, Date fim)
    {
        double total = 0;
        for (ResultadoDeVenda venda : vendas)
        {
            if (venda.estaEntre(inicio, fim))
            {
                total += venda.getValor();
            }
        }
        return total;
    }

    /*
        calcularFixo
        parte fixa de cada pagamento: salário mensal vezes 24 dividido por 52
        são 12 meses de salário divididos em 26 pagamentos no ano
    */

    private double calcularFixo()
    {
        return Conversor.truncar((Conversor.lerNumero(getSalario()) * 24) / 52.0);
    }

    private double calcularVendasPeriodo(Date pagamento)
    {
        return somarVendas(getInicioPeriodo(pagamento), Conversor.adicionarDias(pagamento, 1));
    }

    private double calcularComissao(Date pagamento)
    {
        return Conversor.truncar(calcularVendasPeriodo(pagamento) * Conversor.lerNumero(comissao));
    }

    @Override
    public Date getPrimeiroPagamento()
    {
        return Conversor.criarData(14, 1, 2005);
    }

    @Override
    public Date getProximoPagamento(Date atual)
    {
        return Conversor.adicionarDias(atual, 14);
    }

    @Override
    public Date getInicioPeriodo(Date pagamento)
    {
        return Conversor.adicionarDias(pagamento, -13);
    }

    @Override
    public int getDiasPeriodo(Date pagamento)
    {
        return 14;
    }

    @Override
    public double calcularSalarioBruto(Date pagamento)
    {
        return calcularFixo() + calcularComissao(pagamento);
    }

    @Override
    public double[] calcularValoresFolha(Date pagamento)
    {
        double[] valores = calcularPagamento(pagamento);
        return new double[]{calcularFixo(), calcularVendasPeriodo(pagamento), calcularComissao(pagamento), valores[0], valores[1], valores[2]};
    }

    @Override
    public SecaoFolha escolherSecao(FolhaDePagamento folha)
    {
        return folha.getSecaoComissionados();
    }

    public ArrayList<ResultadoDeVenda> getVendas()
    {
        return vendas;
    }

    public void setVendas(ArrayList<ResultadoDeVenda> vendas)
    {
        this.vendas = vendas;
    }
}
