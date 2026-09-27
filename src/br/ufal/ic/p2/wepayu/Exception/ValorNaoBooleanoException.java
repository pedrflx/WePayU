package br.ufal.ic.p2.wepayu.Exception;

public class ValorNaoBooleanoException extends WePayUException
{
    public ValorNaoBooleanoException()
    {
        super("Valor deve ser true ou false.");
    }
}
