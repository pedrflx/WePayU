package br.ufal.ic.p2.wepayu.models;

public class Empregado
{

    private String nome;
    private String endereco;
    private String tipo;
    private String salario;
    private String comissao;

    private String metodoPagamento = "emMaos";
    private String banco;
    private String agencia;
    private String contaCorrente;

    private String[] datasCartoes = new String[100];
    private double[] horasCartoes = new double[100];
    private int qtdCartoes = 0;

    private String[] datasVendas = new String[100];
    private double[] valoresVendas = new double[100];
    private int qtdVendas = 0;

    private String sindicalizado = "false";
    private String idSindicato;
    private String taxaSindical;

    private String[] datasTaxas = new String[100];
    private double[] valoresTaxas = new double[100];
    private int qtdTaxas = 0;

    public Empregado(String nome, String endereco, String tipo, String salario)
    {
        this.nome = nome;
        this.endereco = endereco;
        this.tipo = tipo;
        this.salario = salario;
    }

    public Empregado(String nome, String endereco, String tipo, String salario, String comissao)
    {
        this.nome = nome;
        this.endereco = endereco;
        this.tipo = tipo;
        this.salario = salario;
        this.comissao = comissao;
    }

    public void registrarCartao(String data, double horas)
    {
        datasCartoes[qtdCartoes] = data;
        horasCartoes[qtdCartoes] = horas;
        qtdCartoes++;
    }

    public void registrarVenda(String data, double valor)
    {
        datasVendas[qtdVendas] = data;
        valoresVendas[qtdVendas] = valor;
        qtdVendas++;
    }

    public void registrarTaxa(String data, double valor)
    {
        datasTaxas[qtdTaxas] = data;
        valoresTaxas[qtdTaxas] = valor;
        qtdTaxas++;
    }

    public String getNome()
    {
        return nome;
    }

    public void setNome(String nome)
    {
        this.nome = nome;
    }

    public String getEndereco()
    {
        return endereco;
    }

    public void setEndereco(String endereco)
    {
        this.endereco = endereco;
    }

    public String getTipo()
    {
        return tipo;
    }

    public void setTipo(String tipo)
    {
        this.tipo = tipo;
    }

    public String getSalario()
    {
        return salario;
    }

    public void setSalario(String salario)
    {
        this.salario = salario;
    }

    public String getComissao()
    {
        return comissao;
    }

    public void setComissao(String comissao)
    {
        this.comissao = comissao;
    }

    public String getMetodoPagamento()
    {
        return metodoPagamento;
    }

    public void setMetodoPagamento(String metodoPagamento)
    {
        this.metodoPagamento = metodoPagamento;
    }

    public String getBanco()
    {
        return banco;
    }

    public void setBanco(String banco)
    {
        this.banco = banco;
    }

    public String getAgencia()
    {
        return agencia;
    }

    public void setAgencia(String agencia)
    {
        this.agencia = agencia;
    }

    public String getContaCorrente()
    {
        return contaCorrente;
    }

    public void setContaCorrente(String contaCorrente)
    {
        this.contaCorrente = contaCorrente;
    }

    public String[] getDatasCartoes()
    {
        return datasCartoes;
    }

    public double[] getHorasCartoes()
    {
        return horasCartoes;
    }

    public int getQtdCartoes()
    {
        return qtdCartoes;
    }

    public String[] getDatasVendas()
    {
        return datasVendas;
    }

    public double[] getValoresVendas()
    {
        return valoresVendas;
    }

    public int getQtdVendas()
    {
        return qtdVendas;
    }

    public String getSindicalizado()
    {
        return sindicalizado;
    }

    public void setSindicalizado(String sindicalizado)
    {
        this.sindicalizado = sindicalizado;
    }

    public String getIdSindicato()
    {
        return idSindicato;
    }

    public void setIdSindicato(String idSindicato)
    {
        this.idSindicato = idSindicato;
    }

    public String getTaxaSindical()
    {
        return taxaSindical;
    }

    public void setTaxaSindical(String taxaSindical)
    {
        this.taxaSindical = taxaSindical;
    }

    public String[] getDatasTaxas()
    {
        return datasTaxas;
    }

    public double[] getValoresTaxas()
    {
        return valoresTaxas;
    }

    public int getQtdTaxas()
    {
        return qtdTaxas;
    }
}