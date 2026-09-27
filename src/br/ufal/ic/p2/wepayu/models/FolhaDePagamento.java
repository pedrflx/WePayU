package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.utils.Conversor;
import java.text.SimpleDateFormat;
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

    public String gerarRelatorio()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("FOLHA DE PAGAMENTO DO DIA ").append(new SimpleDateFormat("yyyy-MM-dd").format(data)).append("\n");
        sb.append("====================================\n\n");

        sb.append(secaoHoristas.gerar(data));
        sb.append(secaoAssalariados.gerar(data));
        sb.append(secaoComissionados.gerar(data));

        double total = 0;
        total = secaoHoristas.somarBrutos(data, total);
        total = secaoAssalariados.somarBrutos(data, total);
        total = secaoComissionados.somarBrutos(data, total);

        sb.append("TOTAL FOLHA: ").append(Conversor.formatarValor(total)).append("\n");
        return sb.toString();
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
