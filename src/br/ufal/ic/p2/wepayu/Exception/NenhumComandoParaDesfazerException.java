package br.ufal.ic.p2.wepayu.Exception;

public class NenhumComandoParaDesfazerException extends WePayUException
{
    public NenhumComandoParaDesfazerException()
    {
        super("Nao ha comando a desfazer.");
    }
}
