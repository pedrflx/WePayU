package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.utils.Conversor;
import java.util.Locale;

/*
    SecaoAssalariados
    seção dos assalariados na folha, só com bruto, descontos e líquido
*/

public class SecaoAssalariados extends SecaoFolha
{

    public SecaoAssalariados()
    {
        super("===================== ASSALARIADOS ============================================================================================",
              "Nome                                             Salario Bruto Descontos Salario Liquido Metodo",
              "================================================ ============= ========= =============== ======================================",
              "TOTAL ASSALARIADOS", 3);
    }

    @Override
    protected String formatar(String rotulo, double[] valores)
    {
        return String.format(Locale.US, "%-48s %13s %9s %15s", rotulo,
                Conversor.formatarValor(valores[0]), Conversor.formatarValor(valores[1]), Conversor.formatarValor(valores[2]));
    }
}
