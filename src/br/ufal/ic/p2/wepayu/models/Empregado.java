package br.ufal.ic.p2.wepayu.models;

public class Empregado
{

    private String nome;
    private String endereco;
    private String tipo;
    private String salario;
    private String comissao;

    private String[] datasCartoes = new String[100];
    private double[] horasCartoes = new double[100];
    private int qtdCartoes = 0;

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

    public String getNome()
    {
        return nome;
    }

    public String getEndereco()
    {
        return endereco;
    }

    public String getTipo()
    {
        return tipo;
    }

    public String getSalario()
    {
        return salario;
    }

    public String getComissao()
    {
        return comissao;
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
}