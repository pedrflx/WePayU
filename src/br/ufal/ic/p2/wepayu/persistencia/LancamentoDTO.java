package br.ufal.ic.p2.wepayu.persistencia;

// LancamentoDTO: data e valor de um lançamento (cartão, venda ou taxa) do jeito que vão para o dados.xml
// guarda tudo como texto, no mesmo formato que os testes usam, para ser registrado de novo na leitura

public class LancamentoDTO
{

    private String data;
    private String valor;

    public LancamentoDTO()
    {
    }

    public LancamentoDTO(String data, String valor)
    {
        this.data = data;
        this.valor = valor;
    }

    public String getData()
    {
        return data;
    }

    public void setData(String data)
    {
        this.data = data;
    }

    public String getValor()
    {
        return valor;
    }

    public void setValor(String valor)
    {
        this.valor = valor;
    }
}