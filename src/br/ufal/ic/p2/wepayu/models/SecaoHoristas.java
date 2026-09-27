package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.utils.Conversor;
import java.util.Locale;

/*
    SecaoHoristas
    seção dos horistas na folha, com as colunas de horas normais e horas extras
*/

public class SecaoHoristas extends SecaoFolha
{

    public SecaoHoristas()
    {
        super("===================== HORISTAS ================================================================================================",
              "Nome                                 Horas Extra Salario Bruto Descontos Salario Liquido Metodo",
              "==================================== ===== ===== ============= ========= =============== ======================================",
              "TOTAL HORISTAS", 5);
    }

    @Override
    protected String formatar(String rotulo, double[] valores)
    {
        return String.format(Locale.US, "%-36s %5s %5s %13s %9s %15s", rotulo,
                Conversor.formatarHoras(valores[0]), Conversor.formatarHoras(valores[1]),
                Conversor.formatarValor(valores[2]), Conversor.formatarValor(valores[3]), Conversor.formatarValor(valores[4]));
    }
}
