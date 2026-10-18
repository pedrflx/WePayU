package br.ufal.ic.p2.wepayu.folha;

import br.ufal.ic.p2.wepayu.Exception.ErroArquivoFolhaException;
import br.ufal.ic.p2.wepayu.Exception.WePayUException;
import br.ufal.ic.p2.wepayu.models.empregados.Empregado;
import br.ufal.ic.p2.wepayu.utils.Conversor;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
/*
    FolhaDePagamento
    monta o relatório da folha de pagamento de um dia
    tem uma seção para cada tipo de empregado e cada empregado escolhe em qual seção entra
*/

public class FolhaDePagamento
{

    private Date data;
    private SecaoFolha secaoHoristas = new SecaoHoristas();
    private SecaoFolha secaoAssalariados = new SecaoAssalariados();
    private SecaoFolha secaoComissionados = new SecaoComissionados();

    public FolhaDePagamento(Date data)
    {
        this.data = data;
    }

    /*
        adicionar
        coloca o empregado na seção certa perguntando ao próprio empregado, sem precisar de if de tipo
    */

    public void adicionar(Empregado empregado)
    {
        empregado.escolherSecao(this).adicionar(empregado);
    }

    // adiciona só quem recebe na data da folha, pulando as posições null (removidas)
    public void adicionarQuemRecebe(ArrayList<Empregado> empregados)
    {
        for (Empregado empregado : empregados)
        {
            if (empregado != null && empregado.ehDiaDePagamento(data))
            {
                adicionar(empregado);
            }
        }
    }

    // soma o salário bruto das três seções
    public double calcularTotal()
    {
        double total = 0;
        total = secaoHoristas.somarBrutos(data, total);
        total = secaoAssalariados.somarBrutos(data, total);
        total = secaoComissionados.somarBrutos(data, total);

        return total;
    }

    public String gerarRelatorio()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("FOLHA DE PAGAMENTO DO DIA ").append(new SimpleDateFormat("yyyy-MM-dd").format(data)).append("\n");
        sb.append("====================================\n\n");

        sb.append(secaoHoristas.gerar(data));
        sb.append(secaoAssalariados.gerar(data));
        sb.append(secaoComissionados.gerar(data));

        sb.append("TOTAL FOLHA: ").append(Conversor.formatarValor(calcularTotal())).append("\n");
        return sb.toString();
    }

    // escreve o relatório no arquivo pedido pelo teste
    public void gravar(String arquivo) throws WePayUException
    {
        try
        {
            PrintWriter writer = new PrintWriter(arquivo, "UTF-8");
            writer.print(gerarRelatorio());
            writer.close();
        }
        catch (IOException e)
        {
            throw new ErroArquivoFolhaException();
        }
    }

    public SecaoFolha getSecaoHoristas()
    {
        return secaoHoristas;
    }

    public SecaoFolha getSecaoAssalariados()
    {
        return secaoAssalariados;
    }

    public SecaoFolha getSecaoComissionados()
    {
        return secaoComissionados;
    }
}
