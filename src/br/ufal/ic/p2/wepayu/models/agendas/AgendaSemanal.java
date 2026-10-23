package br.ufal.ic.p2.wepayu.models.agendas;

import br.ufal.ic.p2.wepayu.utils.Conversor;
import java.util.Calendar;
import java.util.Date;

// AgendaSemanal: paga a cada "semanas" semanas, sempre no mesmo dia da semana (1 = segunda ... 7 = domingo)
// o primeiro pagamento é a n-ésima vez que esse dia aparece a partir de 1/1/2005

public class AgendaSemanal extends AgendaPagamento
{

    private int semanas;
    private int diaSemana;

    public AgendaSemanal(String descricao, int semanas, int diaSemana)
    {
        super(descricao);
        this.semanas = semanas;
        this.diaSemana = diaSemana;
    }

    @Override
    public Date getPrimeiroPagamento()
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(Conversor.criarData(1, 1, 2005));

        // no Calendar o domingo é 1 e a segunda é 2, por isso a conversão
        while (cal.get(Calendar.DAY_OF_WEEK) != diaSemana % 7 + 1)
        {
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }

        cal.add(Calendar.DAY_OF_MONTH, 7 * (semanas - 1));
        return cal.getTime();
    }

    @Override
    public Date getProximoPagamento(Date atual)
    {
        return Conversor.adicionarDias(atual, 7 * semanas);
    }

    @Override
    public Date getInicioPeriodo(Date pagamento)
    {
        return Conversor.adicionarDias(pagamento, -(7 * semanas - 1));
    }

    @Override
    public int getDiasPeriodo(Date pagamento)
    {
        return 7 * semanas;
    }

    // um ano tem 52 semanas: o salário de 12 meses dividido em 52 partes, vezes as semanas do período
    @Override
    public double calcularSalarioFixo(double salarioMensal)
    {
        return Conversor.truncar(salarioMensal * 12 * semanas / 52.0);
    }
}