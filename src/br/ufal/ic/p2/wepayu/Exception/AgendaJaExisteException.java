//para quando alguém tenta criar uma agenda que já existe

package br.ufal.ic.p2.wepayu.Exception;

public class AgendaJaExisteException extends WePayUException
{

    public AgendaJaExisteException()
    {
        super("Agenda de pagamentos ja existe");
    }
}