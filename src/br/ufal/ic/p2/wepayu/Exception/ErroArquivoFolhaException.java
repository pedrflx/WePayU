package br.ufal.ic.p2.wepayu.Exception;

public class ErroArquivoFolhaException extends WePayUException
{

    public ErroArquivoFolhaException()
    {
        super("Nao foi possivel gravar o arquivo da folha.");
    }
}