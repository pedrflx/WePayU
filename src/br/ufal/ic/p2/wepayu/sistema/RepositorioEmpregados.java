package br.ufal.ic.p2.wepayu.sistema;

import br.ufal.ic.p2.wepayu.Exception.*;
import br.ufal.ic.p2.wepayu.models.empregados.Empregado;
import br.ufal.ic.p2.wepayu.utils.Conversor;
import java.util.ArrayList;

// RepositorioEmpregados: guarda a lista de empregados e sabe achar, adicionar e remover
// o id é a posição + 1; quem é removido vira null para os ids dos outros não mudarem

public class RepositorioEmpregados
{

    private ArrayList<Empregado> empregados = new ArrayList<>();

    public RepositorioEmpregados()
    {

    }

    public RepositorioEmpregados(ArrayList<Empregado> empregados)
    {
        this.empregados = empregados;
    }

    // transforma o id do teste na posição da lista (id 1 fica na posição 0)
    public int buscarPosicao(String id) throws WePayUException
    {
        Conversor.validarTexto(id, new IdentificacaoEmpregadoNulaException());

        int numero = Conversor.converterInteiro(id, new EmpregadoNaoExisteException());

        if (numero <= 0 || numero > empregados.size() || empregados.get(numero - 1) == null)
        {
            throw new EmpregadoNaoExisteException();
        }

        return numero - 1;
    }

    public Empregado buscar(String id) throws WePayUException
    {
        return empregados.get(buscarPosicao(id));
    }

    // devolve o id do empregado novo
    public String adicionar(Empregado novo)
    {
        empregados.add(novo);

        return String.valueOf(empregados.size());
    }

    // coloca null em vez de apagar, assim os outros ids não mudam
    public void remover(String id) throws WePayUException
    {
        empregados.set(buscarPosicao(id), null);
    }

    // troca o empregado por outro de outro tipo, mantendo o mesmo id
    public void substituir(String id, Empregado novo) throws WePayUException
    {
        empregados.set(buscarPosicao(id), novo);
    }

    // conta só quem não foi removido
    public int contar()
    {
        int total = 0;
        for (Empregado empregado : empregados)
        {
            if (empregado != null) total++;
        }
        return total;
    }

    public String buscarIdPorNome(String nome, String indice) throws WePayUException
    {
        int posicaoPedida = Conversor.converterInteiro(indice, new IndiceInvalidoException());

        int encontrados = 0;
        for (int i = 0; i < empregados.size(); i++)
        {
            if (empregados.get(i) != null && empregados.get(i).getNome().equals(nome))
            {
                encontrados++;
                if (encontrados == posicaoPedida) return String.valueOf(i + 1);
            }
        }
        throw new EmpregadoNaoEncontradoPorNomeException();
    }

    // devolve o empregado dono daquele id de sindicato, ou null se ninguém tiver
    public Empregado buscarMembro(String idMembro)
    {
        for (Empregado empregado : empregados)
        {
            if (empregado != null && empregado.pertenceAoSindicato(idMembro))
            {
                return empregado;
            }
        }
        return null;
    }

    // troca a lista inteira pela que veio do histórico (undo e redo)
    public void restaurar(ArrayList<Empregado> empregados)
    {
        this.empregados = empregados;
    }

    // começa de novo com a lista vazia
    public void zerar()
    {
        empregados = new ArrayList<>();
    }

    public ArrayList<Empregado> getEmpregados()
    {
        return empregados;
    }
}