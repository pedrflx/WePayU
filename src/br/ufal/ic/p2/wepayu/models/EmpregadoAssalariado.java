package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.Exception.WePayUException;
import br.ufal.ic.p2.wepayu.utils.Conversor;
import java.util.Calendar;
import java.util.Date;

/*
    EmpregadoAssalariado
    empregado com salário fixo mensal, recebe no último dia útil do mês
*/

public class EmpregadoAssalariado extends Empregado
{

    public EmpregadoAssalariado()
    {
    }

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
    public Date getPrimeiroPagamento()
    {
        return Conversor.criarData(31, 1, 2005);
    }

    /*
        getProximoPagamento
        último dia do mês seguinte, se cair no sábado volta 1 dia e se cair no domingo volta 2
    */

    @Override
    public Date getProximoPagamento(Date atual)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(atual);
        cal.add(Calendar.MONTH, 1);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));

        int diaSemana = cal.get(Calendar.DAY_OF_WEEK);
        if (diaSemana == Calendar.SATURDAY) cal.add(Calendar.DAY_OF_MONTH, -1);
        else if (diaSemana == Calendar.SUNDAY) cal.add(Calendar.DAY_OF_MONTH, -2);

        return cal.getTime();
    }

    @Override
    public Date getInicioPeriodo(Date pagamento)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(pagamento);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return cal.getTime();
    }

    @Override
    public int getDiasPeriodo(Date pagamento)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(pagamento);
        return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    @Override
    public double calcularSalarioBruto(Date pagamento)
    {
        return Conversor.lerNumero(getSalario());
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
