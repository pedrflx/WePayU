package br.ufal.ic.p2.wepayu.sistema;

import br.ufal.ic.p2.wepayu.Exception.*;
import br.ufal.ic.p2.wepayu.models.empregados.Empregado;
import br.ufal.ic.p2.wepayu.models.sindicato.MembroSindicato;
import br.ufal.ic.p2.wepayu.utils.Conversor;

// Sindicato: regras do sindicato que dependem de todos os empregados
// o id de sindicato não pode se repetir, e as taxas de serviço chegam pelo id do membro, não pelo id do empregado

public class Sindicato
{

    private RepositorioEmpregados repositorio;

    public Sindicato(RepositorioEmpregados repositorio)
    {
        this.repositorio = repositorio;
    }

    // acha o empregado dono do id de sindicato
    public Empregado buscarMembro(String idMembro) throws WePayUException
    {
        Conversor.validarTexto(idMembro, new IdentificacaoMembroNulaException());

        Empregado membro = repositorio.buscarMembro(idMembro);

        if (membro == null)
        {
            throw new MembroNaoExisteException();
        }

        return membro;
    }

    // "true" filia o empregado com o id e a taxa. "false" não muda nada nesta versão do comando
    public void filiar(Empregado empregado, String valor, String idSindicato, String taxaSindical) throws WePayUException
    {
        if (Conversor.converterBooleano(valor, new ValorNaoBooleanoException()))
        {
            MembroSindicato membro = new MembroSindicato(idSindicato, taxaSindical);

            if (repositorio.buscarMembro(idSindicato) != null)
            {
                throw new IdSindicatoDuplicadoException();
            }

            empregado.setMembroSindicato(membro);
        }
    }
}