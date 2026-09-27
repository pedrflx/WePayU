package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.Exception.*;
import br.ufal.ic.p2.wepayu.utils.Conversor;

/*
    Banco
    o empregado recebe por depósito na conta
    só ele tem banco, agência e conta, e só ele sobrescreve os getters que na mãe lançam erro
*/

public class Banco extends MetodoPagamento
{

    private String banco;
    private String agencia;
    private String contaCorrente;

    public Banco()
    {

    }

    public Banco(String banco, String agencia, String contaCorrente) throws WePayUException
    {
        Conversor.validarTexto(banco, new BancoNuloException());
        Conversor.validarTexto(agencia, new AgenciaNulaException());
        Conversor.validarTexto(contaCorrente, new ContaCorrenteNulaException());

        this.banco = banco;
        this.agencia = agencia;
        this.contaCorrente = contaCorrente;
    }

    @Override
    public String getNome()
    {
        return "banco";
    }

    @Override
    public String getDescricao(String endereco)
    {
        return banco + ", Ag. " + agencia + " CC " + contaCorrente;
    }

    @Override
    public String getBanco()
    {
        return banco;
    }

    public void setBanco(String banco)
    {
        this.banco = banco;
    }

    @Override
    public String getAgencia()
    {
        return agencia;
    }

    public void setAgencia(String agencia)
    {
        this.agencia = agencia;
    }

    @Override
    public String getContaCorrente()
    {
        return contaCorrente;
    }

    public void setContaCorrente(String contaCorrente)
    {
        this.contaCorrente = contaCorrente;
    }
}
