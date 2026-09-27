package br.ufal.ic.p2.wepayu.Exception;

/*
    WePayUException
    mãe de todas as exceções do sistema, cada erro tem sua própria classe filha com a mensagem fixa
    é abstrata porque nunca é lançada diretamente, sempre é lançada uma das filhas
    a mensagem vai para a Exception do Java pelo super e é ela que o EasyAccept compara nos testes
*/

public abstract class WePayUException extends Exception
{
    public WePayUException(String mensagem)
    {
        super(mensagem);
    }
}
