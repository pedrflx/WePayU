package br.ufal.ic.p2.wepayu.models;

/*
    Correios
    o empregado recebe o cheque pelos correios, no endereço dele
*/

public class Correios extends MetodoPagamento
{

    @Override
    public String getNome()
    {
        return "correios";
    }

    @Override
    public String getDescricao(String endereco)
    {
        return "Correios, " + endereco;
    }
}
