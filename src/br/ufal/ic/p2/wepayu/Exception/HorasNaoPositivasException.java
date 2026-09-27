package br.ufal.ic.p2.wepayu.Exception;

public class HorasNaoPositivasException extends WePayUException
{
    public HorasNaoPositivasException()
    {
        super("Horas devem ser positivas.");
    }
}
