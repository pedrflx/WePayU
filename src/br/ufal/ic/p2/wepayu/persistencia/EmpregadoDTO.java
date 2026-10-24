package br.ufal.ic.p2.wepayu.persistencia;

import java.util.ArrayList;

// EmpregadoDTO: só os dados de um empregado, do jeito que vão para o dados.xml
// tem construtor vazio, getters e setters porque o XMLEncoder e o XMLDecoder precisam deles (pra assim os modelos não precisarem)

public class EmpregadoDTO
{

    private String tipo;
    private String nome;
    private String endereco;
    private String salario;
    private String comissao;
    private String agendaPagamento;

    private String metodoPagamento;
    private String banco;
    private String agencia;
    private String contaCorrente;

    private String idSindicato;
    private String taxaSindical;

    private ArrayList<LancamentoDTO> cartoes = new ArrayList<>();
    private ArrayList<LancamentoDTO> vendas = new ArrayList<>();
    private ArrayList<LancamentoDTO> taxas = new ArrayList<>();

    public EmpregadoDTO()
    {
    }

    public String getTipo()
    {
        return tipo;
    }

    public void setTipo(String tipo)
    {
        this.tipo = tipo;
    }

    public String getNome()
    {
        return nome;
    }

    public void setNome(String nome)
    {
        this.nome = nome;
    }

    public String getEndereco()
    {
        return endereco;
    }

    public void setEndereco(String endereco)
    {
        this.endereco = endereco;
    }

    public String getSalario()
    {
        return salario;
    }

    public void setSalario(String salario)
    {
        this.salario = salario;
    }

    public String getComissao()
    {
        return comissao;
    }

    public void setComissao(String comissao)
    {
        this.comissao = comissao;
    }

    public String getAgendaPagamento()
    {
        return agendaPagamento;
    }

    public void setAgendaPagamento(String agendaPagamento)
    {
        this.agendaPagamento = agendaPagamento;
    }

    public String getMetodoPagamento()
    {
        return metodoPagamento;
    }

    public void setMetodoPagamento(String metodoPagamento)
    {
        this.metodoPagamento = metodoPagamento;
    }

    public String getBanco()
    {
        return banco;
    }

    public void setBanco(String banco)
    {
        this.banco = banco;
    }

    public String getAgencia()
    {
        return agencia;
    }

    public void setAgencia(String agencia)
    {
        this.agencia = agencia;
    }

    public String getContaCorrente()
    {
        return contaCorrente;
    }

    public void setContaCorrente(String contaCorrente)
    {
        this.contaCorrente = contaCorrente;
    }

    public String getIdSindicato()
    {
        return idSindicato;
    }

    public void setIdSindicato(String idSindicato)
    {
        this.idSindicato = idSindicato;
    }

    public String getTaxaSindical()
    {
        return taxaSindical;
    }

    public void setTaxaSindical(String taxaSindical)
    {
        this.taxaSindical = taxaSindical;
    }

    public ArrayList<LancamentoDTO> getCartoes()
    {
        return cartoes;
    }

    public void setCartoes(ArrayList<LancamentoDTO> cartoes)
    {
        this.cartoes = cartoes;
    }

    public ArrayList<LancamentoDTO> getVendas()
    {
        return vendas;
    }

    public void setVendas(ArrayList<LancamentoDTO> vendas)
    {
        this.vendas = vendas;
    }

    public ArrayList<LancamentoDTO> getTaxas()
    {
        return taxas;
    }

    public void setTaxas(ArrayList<LancamentoDTO> taxas)
    {
        this.taxas = taxas;
    }
}