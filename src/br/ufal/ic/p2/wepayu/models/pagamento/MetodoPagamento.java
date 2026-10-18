package br.ufal.ic.p2.wepayu.models.pagamento;

import br.ufal.ic.p2.wepayu.Exception.MetodoPagamentoInvalidoException;
import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoRecebeEmBancoException;
import br.ufal.ic.p2.wepayu.Exception.WePayUException;
import br.ufal.ic.p2.wepayu.persistencia.EmpregadoDTO;

/*
    MetodoPagamento
    mãe das formas de receber o salário: EmMaos, Correios e Banco
    é abstrata porque cada empregado recebe por uma dessas três formas
*/

public abstract class MetodoPagamento
{

    // transforma o texto do teste na forma de pagamento (o banco usa o construtor com os dados da conta)
    public static MetodoPagamento criar(String metodo) throws WePayUException
    {
        if (metodo.equals("emMaos")) return new EmMaos();
        if (metodo.equals("correios")) return new Correios();
        throw new MetodoPagamentoInvalidoException();
    }

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

    // escreve no DTO como o empregado recebe. o Banco completa com os dados da conta
    public void exportarPara(EmpregadoDTO dados)
    {
        dados.setMetodoPagamento(getNome());
    }

}
