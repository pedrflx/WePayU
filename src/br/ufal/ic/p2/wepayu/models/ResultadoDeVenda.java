package br.ufal.ic.p2.wepayu.models;

import java.util.Date;

/*
    ResultadoDeVenda
    venda feita por um comissionado, com a data e o valor da venda
    não acrescenta nada ao Lancamento, existe para dar nome a esse tipo de lançamento
*/

public class ResultadoDeVenda extends Lancamento
{

    public ResultadoDeVenda()
    {
    }

    public ResultadoDeVenda(Date data, double valor)
    {
        super(data, valor);
    }
}
