package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.utils.Conversor;
import java.util.Locale;

/*
    SecaoComissionados
    seção dos comissionados na folha, com as colunas de fixo, vendas e comissão
*/

public class SecaoComissionados extends SecaoFolha
{

    public SecaoComissionados()
    {
        super("===================== COMISSIONADOS ===========================================================================================",
              "Nome                  Fixo     Vendas   Comissao Salario Bruto Descontos Salario Liquido Metodo",
              "===================== ======== ======== ======== ============= ========= =============== ======================================",
              "TOTAL COMISSIONADOS", 6);
    }

    @Override
    protected String formatar(String rotulo, double[] valores)
    {
        return String.format(Locale.US, "%-21s %8s %8s %8s %13s %9s %15s", rotulo,
                Conversor.formatarValor(valores[0]), Conversor.formatarValor(valores[1]), Conversor.formatarValor(valores[2]),
                Conversor.formatarValor(valores[3]), Conversor.formatarValor(valores[4]), Conversor.formatarValor(valores[5]));
    }
}
