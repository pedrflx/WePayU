package br.ufal.ic.p2.wepayu.models;

import java.util.Date;

/*
    TaxaServico
    taxa extra de serviço cobrada pelo sindicato, é descontada no próximo pagamento do empregado
*/

public class TaxaServico extends Lancamento
{

    public TaxaServico()
    {
    }

    public TaxaServico(Date data, double valor)
    {
        super(data, valor);
    }
}
