package br.ufal.ic.p2.wepayu.models;

import java.util.Date;

/*
    Lancamento
    mãe de tudo que é lançado com uma data e um valor: CartaoDePonto, ResultadoDeVenda e TaxaServico
    é abstrata porque cada lançamento é de um desses três tipos
*/

public abstract class Lancamento
{

    private Date data;
    private double valor;

    public Lancamento()
    {
    }

    public Lancamento(Date data, double valor)
    {
        this.data = data;
        this.valor = valor;
    }

    /*
        estaEntre
        diz se o lançamento aconteceu dentro do período
        o dia inicial entra e o final não, porque uma data significa 00:00 daquele dia
    */

    public boolean estaEntre(Date inicio, Date fim)
    {
        return !data.before(inicio) && data.before(fim);
    }

    public Date getData()
    {
        return data;
    }

    public void setData(Date data)
    {
        this.data = data;
    }

    public double getValor()
    {
        return valor;
    }

    public void setValor(double valor)
    {
        this.valor = valor;
    }
}
