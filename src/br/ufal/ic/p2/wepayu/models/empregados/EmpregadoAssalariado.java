package br.ufal.ic.p2.wepayu.models.empregados;

import br.ufal.ic.p2.wepayu.Exception.WePayUException;
import br.ufal.ic.p2.wepayu.folha.FolhaDePagamento;
import br.ufal.ic.p2.wepayu.folha.SecaoFolha;
import br.ufal.ic.p2.wepayu.models.agendas.AgendaPagamento;

import java.util.Date;

/*
    EmpregadoAssalariado
    empregado com salário fixo mensal, recebe no último dia útil do mês
*/

public class EmpregadoAssalariado extends Empregado
{

    public EmpregadoAssalariado(String nome, String endereco, String salario) throws WePayUException
    {
        super(nome, endereco, salario);
    }

    public EmpregadoAssalariado(Empregado outro)
    {
        super(outro);
    }

    @Override
    public String getTipo()
    {
        return "assalariado";
    }

    @Override
    public Empregado clonar()
    {
        return new EmpregadoAssalariado(this);
    }

    @Override
    public AgendaPagamento criarAgendaPadrao() throws WePayUException
    {
        return AgendaPagamento.criar("mensal $");
    }

    @Override
    public double calcularSalarioBruto(Date pagamento)
    {
        return calcularSalarioFixo();
    }

    @Override
    public double[] calcularValoresFolha(Date pagamento)
    {
        return calcularPagamento(pagamento);
    }

    @Override
    public SecaoFolha escolherSecao(FolhaDePagamento folha)
    {
        return folha.getSecaoAssalariados();
    }
}
