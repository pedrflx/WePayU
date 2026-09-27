package br.ufal.ic.p2.wepayu.Exception;

public class ValorNaoPositivoException extends WePayUException
{

    public ValorNaoPositivoException()
    {
        super("Valor deve ser positivo.");
    }
}
