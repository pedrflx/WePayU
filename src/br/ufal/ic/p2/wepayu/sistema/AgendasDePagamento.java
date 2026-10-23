package br.ufal.ic.p2.wepayu.sistema;

import br.ufal.ic.p2.wepayu.Exception.*;
import br.ufal.ic.p2.wepayu.models.agendas.AgendaPagamento;
import java.util.ArrayList;

// AgendasDePagamento: lista das agendas que os empregados podem escolher
// começa com as três padrão e cresce com criarAgendaDePagamentos.

public class AgendasDePagamento
{
    private ArrayList<String> disponiveis = new ArrayList<>();

    public AgendasDePagamento()
    {
        zerar();
    }

    public void zerar()
    {
        disponiveis = new ArrayList<>();
        disponiveis.add("semanal 5");
        disponiveis.add("mensal $");
        disponiveis.add("semanal 2 5");
    }

    // primeiro confere se a descrição segue as regras, depois se ela já existe
    public void criar(String descricao) throws WePayUException
    {
        AgendaPagamento.criar(descricao);

        if (disponiveis.contains(descricao))
        {
            throw new AgendaJaExisteException();
        }

        disponiveis.add(descricao);
    }

    // devolve a agenda pedida, se ela estiver entre as disponíveis
    public AgendaPagamento buscar(String descricao) throws WePayUException
    {
        if (!disponiveis.contains(descricao))
        {
            throw new AgendaNaoDisponivelException();
        }

        return AgendaPagamento.criar(descricao);
    }

    public ArrayList<String> getDisponiveis()
    {
        return disponiveis;
    }
}