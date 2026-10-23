package br.ufal.ic.p2.wepayu.models.agendas;

import br.ufal.ic.p2.wepayu.Exception.DescricaoAgendaInvalidaException;
import br.ufal.ic.p2.wepayu.Exception.WePayUException;
import br.ufal.ic.p2.wepayu.utils.Conversor;
import java.util.Date;

// AgendaPagamento: mãe das agendas, diz em que dias o empregado recebe e qual período cada pagamento cobre
// a descrição ("semanal 2 5", "mensal $", "mensal 1"...) é o que aparece no getAtributoEmpregado

public abstract class AgendaPagamento
{

    private String descricao;

    public AgendaPagamento(String descricao)
    {
        this.descricao = descricao;
    }

    public String getDescricao()
    {
        return descricao;
    }

    public abstract Date getPrimeiroPagamento();

    public abstract Date getProximoPagamento(Date atual);

    public abstract Date getInicioPeriodo(Date pagamento);

    public abstract int getDiasPeriodo(Date pagamento);

    public abstract double calcularSalarioFixo(double salarioMensal);

    // primeiro dia de pagamento que não vem antes da data
    public Date getPrimeiroPagamentoAPartirDe(Date data)
    {
        Date atual = getPrimeiroPagamento();
        while (atual.before(data))
        {
            atual = getProximoPagamento(atual);
        }
        return atual;
    }

    // transforma a descrição na agenda certa. qualquer formato fora das regras lança DescricaoAgendaInvalidaException
    public static AgendaPagamento criar(String descricao) throws WePayUException
    {
        Conversor.validarTexto(descricao, new DescricaoAgendaInvalidaException());
        String[] partes = descricao.split(" ");

        if (partes[0].equals("mensal") && partes.length == 2)
        {
            if (partes[1].equals("$")) return new AgendaMensalUltimoDia(descricao);
            return new AgendaMensal(descricao, lerNumero(partes[1], 1, 28));
        }
        if (partes[0].equals("semanal") && partes.length == 2)
        {
            return new AgendaSemanal(descricao, 1, lerNumero(partes[1], 1, 7));
        }
        if (partes[0].equals("semanal") && partes.length == 3)
        {
            return new AgendaSemanal(descricao, lerNumero(partes[1], 1, 52), lerNumero(partes[2], 1, 7));
        }
        throw new DescricaoAgendaInvalidaException();
    }

    // lê uma parte da descrição como número e confere se está entre o mínimo e o máximo
    private static int lerNumero(String texto, int minimo, int maximo) throws WePayUException
    {
        int numero = Conversor.converterInteiro(texto, new DescricaoAgendaInvalidaException());

        if (numero < minimo || numero > maximo)
        {
            throw new DescricaoAgendaInvalidaException();
        }
        return numero;
    }
}