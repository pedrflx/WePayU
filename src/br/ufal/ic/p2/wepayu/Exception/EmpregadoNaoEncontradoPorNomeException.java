package br.ufal.ic.p2.wepayu.Exception;

public class EmpregadoNaoEncontradoPorNomeException extends WePayUException
{
    public EmpregadoNaoEncontradoPorNomeException()
    {
        super("Nao ha empregado com esse nome.");
    }
}
