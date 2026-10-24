package br.ufal.ic.p2.wepayu.persistencia;

import java.util.ArrayList;

// DadosDTO: tudo que vai para o dados.xml de uma vez: os empregados e as agendas de pagamento disponíveis

public class DadosDTO
{

    private EmpregadoDTO[] empregados = new EmpregadoDTO[0];
    private ArrayList<String> agendas = new ArrayList<>();

    public DadosDTO()
    {
    }

    public EmpregadoDTO[] getEmpregados()
    {
        return empregados;
    }

    public void setEmpregados(EmpregadoDTO[] empregados)
    {
        this.empregados = empregados;
    }

    public ArrayList<String> getAgendas()
    {
        return agendas;
    }

    public void setAgendas(ArrayList<String> agendas)
    {
        this.agendas = agendas;
    }
}