package br.ufal.ic.p2.wepayu.models.agendas;

import br.ufal.ic.p2.wepayu.utils.Conversor;
import java.util.Calendar;
import java.util.Date;

// AgendaMensalUltimoDia: a agenda "mensal $", paga no último dia útil de cada mês
// se o último dia cair no sábado volta 1 dia, se cair no domingo volta 2. o período é o mês inteiro

public class AgendaMensalUltimoDia extends AgendaPagamento
{

    public AgendaMensalUltimoDia(String descricao)
    {
        super(descricao);
    }

    @Override
    public Date getPrimeiroPagamento()
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(Conversor.criarData(1, 1, 2005));
        return ultimoDiaUtil(cal);
    }

    @Override
    public Date getProximoPagamento(Date atual)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(atual);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.add(Calendar.MONTH, 1);
        return ultimoDiaUtil(cal);
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
    public double calcularSalarioFixo(double salarioMensal)
    {
        return salarioMensal;
    }

    // vai para o último dia do mês do calendário recebido e recua o fim de semana
    private Date ultimoDiaUtil(Calendar cal)
    {
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));

        int diaSemana = cal.get(Calendar.DAY_OF_WEEK);
        if (diaSemana == Calendar.SATURDAY) cal.add(Calendar.DAY_OF_MONTH, -1);
        else if (diaSemana == Calendar.SUNDAY) cal.add(Calendar.DAY_OF_MONTH, -2);

        return cal.getTime();
    }
}