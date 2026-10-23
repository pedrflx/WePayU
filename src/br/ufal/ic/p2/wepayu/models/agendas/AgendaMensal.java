package br.ufal.ic.p2.wepayu.models.agendas;

import br.ufal.ic.p2.wepayu.utils.Conversor;
import java.util.Calendar;
import java.util.Date;

// AgendaMensal: paga qualquer mês no mesmo dia (de 1 a 28, para existir em todos os meses)
// o período vai do dia seguinte ao pagamento do mês anterior até o dia do pagamento

public class AgendaMensal extends AgendaPagamento
{

    private int dia;

    public AgendaMensal(String descricao, int dia)
    {
        super(descricao);
        this.dia = dia;
    }

    @Override
    public Date getPrimeiroPagamento()
    {
        return Conversor.criarData(dia, 1, 2005);
    }

    @Override
    public Date getProximoPagamento(Date atual)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(atual);
        cal.add(Calendar.MONTH, 1);
        return cal.getTime();
    }

    @Override
    public Date getInicioPeriodo(Date pagamento)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(pagamento);
        cal.add(Calendar.MONTH, -1);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        return cal.getTime();
    }

    // o período tem o tamanho do mês anterior ao pagamento
    @Override
    public int getDiasPeriodo(Date pagamento)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(pagamento);
        cal.add(Calendar.MONTH, -1);
        return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    @Override
    public double calcularSalarioFixo(double salarioMensal)
    {
        return salarioMensal;
    }
}