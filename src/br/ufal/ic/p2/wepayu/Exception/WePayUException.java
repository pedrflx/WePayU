package br.ufal.ic.p2.wepayu.Exception;

public abstract class WePayUException extends Exception
{
    public WePayUException(String mensagem)
    {
        super(mensagem);
    }
}
