package br.ufal.ic.p2.wepayu.models;

import java.util.Date;

/*
    CartaoDePonto
    cartão de ponto do horista, o valor herdado de Lancamento são as horas trabalhadas no dia
*/

public class CartaoDePonto extends Lancamento
{

    public CartaoDePonto()
    {

    }

    public CartaoDePonto(Date data, double horas)
    {
        super(data, horas);
    }

    public double getHorasNormais()
    {
        if (getValor() > 8)
        {
            return 8;
        }
        return getValor();
    }

    /*
        getHorasExtras
        o que passar de 8 horas no dia é hora extra
    */

    public double getHorasExtras()
    {
        if (getValor() > 8)
        {
            return getValor() - 8;
        }
        return 0;
    }
}
