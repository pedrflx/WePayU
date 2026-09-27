package br.ufal.ic.p2.wepayu.models;

import java.util.ArrayList;
import java.util.Date;

/*
    SecaoFolha
    mãe das três partes do relatório: SecaoHoristas, SecaoAssalariados e SecaoComissionados
    o que é igual entre elas fica aqui: cabeçalho, uma linha por empregado em ordem alfabética e a linha de total
    cada filha só informa seus textos e como formatar os números
*/

public abstract class SecaoFolha
{

    private static final String LINHA = "===============================================================================================================================";

    private String titulo;
    private String colunas;
    private String separadores;
    private String rotuloTotal;
    private int quantidadeValores;
    private ArrayList<Empregado> empregados = new ArrayList<>();

    public SecaoFolha(String titulo, String colunas, String separadores, String rotuloTotal, int quantidadeValores)
    {
        this.titulo = titulo;
        this.colunas = colunas;
        this.separadores = separadores;
        this.rotuloTotal = rotuloTotal;
        this.quantidadeValores = quantidadeValores;
    }

    /*
        formatar
        monta o texto dos números de uma linha, cada seção tem suas próprias colunas e larguras
        serve tanto para a linha de cada empregado quanto para a linha de total
    */

    protected abstract String formatar(String rotulo, double[] valores);

    public void adicionar(Empregado empregado)
    {
        empregados.add(empregado);
    }

    /*
        gerar
        monta o texto da seção
        para cada empregado pega os valores da folha, soma nos totais e escreve a linha com a forma de pagamento no fim
    */

    public String gerar(Date data)
    {
        ordenarPorNome();

        StringBuilder sb = new StringBuilder();
        sb.append(LINHA).append("\n");
        sb.append(titulo).append("\n");
        sb.append(LINHA).append("\n");
        sb.append(colunas).append("\n");
        sb.append(separadores).append("\n");

        double[] totais = new double[quantidadeValores];

        for (Empregado empregado : empregados)
        {
            double[] valores = empregado.calcularValoresFolha(data);
            for (int i = 0; i < quantidadeValores; i++)
            {
                totais[i] += valores[i];
            }
            sb.append(formatar(empregado.getNome(), valores)).append(" ").append(empregado.formatarMetodoPagamento()).append("\n");
        }

        sb.append("\n");
        sb.append(formatar(rotuloTotal, totais)).append("\n");
        sb.append("\n");

        return sb.toString();
    }

    public double somarBrutos(Date data, double total)
    {
        for (Empregado empregado : empregados)
        {
            total += empregado.calcularSalarioBruto(data);
        }
        return total;
    }

    private void ordenarPorNome()
    {
        for (int i = 0; i < empregados.size() - 1; i++)
        {
            for (int j = 0; j < empregados.size() - i - 1; j++)
            {
                if (empregados.get(j).getNome().compareTo(empregados.get(j + 1).getNome()) > 0)
                {
                    Empregado temp = empregados.get(j);
                    empregados.set(j, empregados.get(j + 1));
                    empregados.set(j + 1, temp);
                }
            }
        }
    }
}
