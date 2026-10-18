package br.ufal.ic.p2.wepayu.sistema;

import br.ufal.ic.p2.wepayu.Exception.*;
import br.ufal.ic.p2.wepayu.models.empregados.Empregado;
import java.util.ArrayList;

// Historico: guarda uma cópia da lista de empregados a cada comando, para o undo e o redo
// estadoAtual é a posição, dentro da lista de cópias, do estado em que o sistema está agora

public class Historico
{

    private ArrayList<ArrayList<Empregado>> estados = new ArrayList<>();
    private int estadoAtual = 0;

    // o primeiro estado é a lista com que o sistema começou
    public Historico(ArrayList<Empregado> inicial)
    {
        estados.add(copiar(inicial));
    }

    // guarda o estado novo (se houve o undo antes, apaga os estados que estavam na frente)
    public void salvar(ArrayList<Empregado> atual)
    {
        while (estados.size() > estadoAtual + 1)
        {
            estados.remove(estados.size() - 1);
        }

        estados.add(copiar(atual));
        estadoAtual++;
    }

    // volta um estado e devolve a lista daquele momento
    public ArrayList<Empregado> desfazer() throws WePayUException
    {
        if (estadoAtual <= 0)
        {
            throw new NenhumComandoParaDesfazerException();
        }

        estadoAtual--;
        return copiar(estados.get(estadoAtual));
    }

    // avança um estado e devolve a lista daquele momento
    public ArrayList<Empregado> refazer() throws WePayUException
    {
        if (estadoAtual >= estados.size() - 1)
        {
            throw new NenhumComandoParaRefazerException();
        }

        estadoAtual++;
        return copiar(estados.get(estadoAtual));
    }

    // copia a lista inteira, clonando cada empregado (null continua null)
    private ArrayList<Empregado> copiar(ArrayList<Empregado> origem)
    {
        ArrayList<Empregado> copia = new ArrayList<>();
        for (Empregado empregado : origem)
        {
            if (empregado == null)
            {
                copia.add(null);
            }
            else
            {
                copia.add(empregado.clonar());
            }
        }
        return copia;
    }
}