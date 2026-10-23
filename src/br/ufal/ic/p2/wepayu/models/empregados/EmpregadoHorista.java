package br.ufal.ic.p2.wepayu.models.empregados;

import br.ufal.ic.p2.wepayu.Exception.DataInvalidaException;
import br.ufal.ic.p2.wepayu.Exception.HorasNaoPositivasException;
import br.ufal.ic.p2.wepayu.Exception.WePayUException;
import br.ufal.ic.p2.wepayu.folha.FolhaDePagamento;
import br.ufal.ic.p2.wepayu.folha.SecaoFolha;
import br.ufal.ic.p2.wepayu.models.agendas.AgendaPagamento;
import br.ufal.ic.p2.wepayu.models.lancamentos.CartaoDePonto;
import br.ufal.ic.p2.wepayu.persistencia.EmpregadoDTO;
import br.ufal.ic.p2.wepayu.utils.Conversor;

import java.util.ArrayList;
import java.util.Date;

/*
    EmpregadoHorista
    empregado que ganha por hora trabalhada e recebe toda sexta-feira
    guarda os cartões de ponto, as horas acima de 8 no dia valem 1,5 vez o valor da hora
*/

public class EmpregadoHorista extends Empregado
{

    private ArrayList<CartaoDePonto> cartoes = new ArrayList<>();

    public EmpregadoHorista(String nome, String endereco, String salario) throws WePayUException
    {
        super(nome, endereco, salario);
    }

    public EmpregadoHorista(Empregado outro)
    {
        super(outro);
    }

    @Override
    public String getTipo()
    {
        return "horista";
    }

    @Override
    public Empregado clonar()
    {
        EmpregadoHorista copia = new EmpregadoHorista(this);
        copia.cartoes = new ArrayList<>(cartoes);
        return copia;
    }

    /*
        registrarCartao
        sobrescreve a versão do Empregado que dava erro
        valida a data e as horas e guarda um novo CartaoDePonto
    */

    @Override
    public void registrarCartao(String data, String horas) throws WePayUException
    {
        Date dataCartao = Conversor.converterData(data, new DataInvalidaException());
        double horasTrabalhadas = Conversor.converterPositivo(horas, new HorasNaoPositivasException());

        cartoes.add(new CartaoDePonto(dataCartao, horasTrabalhadas));
    }

    @Override
    public String getHorasNormaisTrabalhadas(String dataInicial, String dataFinal) throws WePayUException
    {
        Date[] periodo = Conversor.converterPeriodo(dataInicial, dataFinal);
        return Conversor.formatarHoras(somarHorasNormais(periodo[0], periodo[1]));
    }

    @Override
    public String getHorasExtrasTrabalhadas(String dataInicial, String dataFinal) throws WePayUException
    {
        Date[] periodo = Conversor.converterPeriodo(dataInicial, dataFinal);
        return Conversor.formatarHoras(somarHorasExtras(periodo[0], periodo[1]));
    }

    private double somarHorasNormais(Date inicio, Date fim)
    {
        double total = 0;
        for (CartaoDePonto cartao : cartoes)
        {
            if (cartao.estaEntre(inicio, fim))
            {
                total += cartao.getHorasNormais();
            }
        }
        return total;
    }

    private double somarHorasExtras(Date inicio, Date fim)
    {
        double total = 0;
        for (CartaoDePonto cartao : cartoes)
        {
            if (cartao.estaEntre(inicio, fim))
            {
                total += cartao.getHorasExtras();
            }
        }
        return total;
    }

    @Override
    public AgendaPagamento criarAgendaPadrao() throws WePayUException
    {
        return AgendaPagamento.criar("semanal 5");
    }

    /*
        getDataInicio
        o horista é considerado contratado no dia do primeiro cartão lançado, ou em 1/1/2005 se não tiver cartão
        assim ele não acumula dívida com o sindicato antes de começar a trabalhar
    */

    @Override
    public Date getDataInicio()
    {
        Date primeira = super.getDataInicio();
        if (!cartoes.isEmpty())
        {
            primeira = cartoes.get(0).getData();
            for (CartaoDePonto cartao : cartoes)
            {
                if (cartao.getData().before(primeira))
                {
                    primeira = cartao.getData();
                }
            }
        }
        return primeira;
    }

    /*
        calcularSalarioBruto
        horas normais vezes o valor da hora mais horas extras vezes o valor da hora vezes 1,5
        o fim do período é o dia seguinte ao pagamento para incluir o próprio dia do pagamento
    */

    @Override
    public double calcularSalarioBruto(Date pagamento)
    {
        Date inicio = getInicioPeriodo(pagamento);
        Date fim = Conversor.adicionarDias(pagamento, 1);
        double salarioHora = Conversor.lerNumero(getSalario());

        return Conversor.truncar(somarHorasNormais(inicio, fim) * salarioHora) + Conversor.truncar(somarHorasExtras(inicio, fim) * salarioHora * 1.5);
    }

    @Override
    public double[] calcularValoresFolha(Date pagamento)
    {
        Date inicio = getInicioPeriodo(pagamento);
        Date fim = Conversor.adicionarDias(pagamento, 1);
        double[] valores = calcularPagamento(pagamento);

        return new double[]{somarHorasNormais(inicio, fim), somarHorasExtras(inicio, fim), valores[0], valores[1], valores[2]};
    }

    @Override
    public SecaoFolha escolherSecao(FolhaDePagamento folha)
    {
        return folha.getSecaoHoristas();
    }

    @Override
    public void exportarPara(EmpregadoDTO dados)
    {
        super.exportarPara(dados);

        for (CartaoDePonto cartao : cartoes)
        {
            dados.getCartoes().add(cartao.paraDTO());
        }
    }

}