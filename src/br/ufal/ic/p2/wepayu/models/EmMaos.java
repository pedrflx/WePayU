package br.ufal.ic.p2.wepayu.models;

/*
    EmMaos
    o empregado recebe o cheque em mãos, é a formar padrão de empregado novo
*/

public class EmMaos extends MetodoPagamento
{

    @Override
    public String getNome()
    {
        return "emMaos";
    }

    @Override
    public String getDescricao(String endereco)
    {
        return "Em maos";
    }
}
