package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoRecebeEmBancoException;
import br.ufal.ic.p2.wepayu.Exception.WePayUException;

/*
    MetodoPagamento
    mãe das formas de receber o salário: EmMaos, Correios e Banco
    é abstrata porque cada empregado recebe por uma dessas três formas
*/

public abstract class MetodoPagamento
{

    public abstract String getNome();

    public abstract String getDescricao(String endereco);

    /*
        getBanco, getAgencia e getContaCorrente
        por padrão o empregado não recebe em banco, então os três lançam EmpregadoNaoRecebeEmBancoException
        só a classe Banco sobrescreve e devolve os dados de verdade
    */

    public String getBanco() throws WePayUException
    {
        throw new EmpregadoNaoRecebeEmBancoException();
    }

    public String getAgencia() throws WePayUException
    {
        throw new EmpregadoNaoRecebeEmBancoException();
    }

    public String getContaCorrente() throws WePayUException
    {
        throw new EmpregadoNaoRecebeEmBancoException();
    }
}
