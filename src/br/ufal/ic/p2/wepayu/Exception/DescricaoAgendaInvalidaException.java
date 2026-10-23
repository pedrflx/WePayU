//para quando o formato da agenda está errado

package br.ufal.ic.p2.wepayu.Exception;

public class DescricaoAgendaInvalidaException extends WePayUException
{

    public DescricaoAgendaInvalidaException()
    {
        super("Descricao de agenda invalida");
    }
}