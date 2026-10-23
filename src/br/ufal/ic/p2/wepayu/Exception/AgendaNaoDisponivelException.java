//para quando alguém escolhe uma agenda que ainda não foi criada

package br.ufal.ic.p2.wepayu.Exception;

public class AgendaNaoDisponivelException extends WePayUException
{

    public AgendaNaoDisponivelException()
    {
        super("Agenda de pagamento nao esta disponivel");
    }
}