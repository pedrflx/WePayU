package br.ufal.ic.p2.wepayu.Exception;

public class ErroPersistenciaException extends WePayUException
{

    public ErroPersistenciaException()
    {
        super("Nao foi possivel acessar o arquivo de dados.");
    }
}
