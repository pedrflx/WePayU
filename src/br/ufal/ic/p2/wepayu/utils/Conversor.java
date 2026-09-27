package br.ufal.ic.p2.wepayu.utils;

import br.ufal.ic.p2.wepayu.Exception.DataFinalInvalidaException;
import br.ufal.ic.p2.wepayu.Exception.DataInicialInvalidaException;
import br.ufal.ic.p2.wepayu.Exception.DataInicialPosteriorException;
import br.ufal.ic.p2.wepayu.Exception.WePayUException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/*
    Conversor
    classe de ferramentas usada no projeto inteiro para validar, converter e formatar valores
    todos os métodos são static porque não dependem de nenhum objeto
    os métodos de validação recebem qual exceção lançar, assim a mesma lógica serve para vários erros diferentes
*/

public class Conversor
{

    public static void validarTexto(String valor, WePayUException erro) throws WePayUException
    {
        if (valor == null || valor.isEmpty())
        {
            throw erro;
        }
    }

    public static int converterInteiro(String valor, WePayUException erro) throws WePayUException
    {
        validarTexto(valor, erro);

        try
        {
            return Integer.parseInt(valor);
        }
        catch (NumberFormatException e)
        {
            throw erro;
        }
    }

    public static double converterNumero(String valor, WePayUException erro) throws WePayUException
    {
        validarTexto(valor, erro);

        try
        {
            return Double.parseDouble(valor.replace(",", "."));
        }
        catch (NumberFormatException e)
        {
            throw erro;
        }
    }

    public static double converterNaoNegativo(String valor, WePayUException erroNulo, WePayUException erroNumerico, WePayUException erroNegativo) throws WePayUException
    {
        validarTexto(valor, erroNulo);

        double numero = converterNumero(valor, erroNumerico);

        if (numero < 0)
        {
            throw erroNegativo;
        }

        return numero;
    }

    public static double converterPositivo(String valor, WePayUException erro) throws WePayUException
    {
        double numero = converterNumero(valor, erro);

        if (numero <= 0)
        {
            throw erro;
        }

        return numero;
    }

    /*
        converterData
        transforma um texto no formato d/m/aaaa em Date
        setLenient(false) faz datas impossíveis como 30/2/2005 darem erro em vez de virarem outra data
    */

    public static Date converterData(String data, WePayUException erro) throws WePayUException
    {
        validarTexto(data, erro);

        try
        {
            SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy");
            sdf.setLenient(false);
            return sdf.parse(data);
        }
        catch (ParseException e)
        {
            throw erro;
        }
    }

    public static Date[] converterPeriodo(String dataInicial, String dataFinal) throws WePayUException
    {
        Date inicio = converterData(dataInicial, new DataInicialInvalidaException());
        Date fim = converterData(dataFinal, new DataFinalInvalidaException());

        if (inicio.after(fim))
        {
            throw new DataInicialPosteriorException();
        }

        return new Date[]{inicio, fim};
    }

    public static Date criarData(int dia, int mes, int ano)
    {
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(ano, mes - 1, dia);
        return cal.getTime();
    }

    public static double lerNumero(String valor)
    {
        return Double.parseDouble(valor.replace(",", "."));
    }

    public static Date adicionarDias(Date data, int dias)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(data);
        cal.add(Calendar.DAY_OF_MONTH, dias);
        return cal.getTime();
    }

    /*
        truncar
        corta o valor em 2 casas decimais sem arredondar, que é o que os testes esperam
    */

    public static double truncar(double valor)
    {
        return Math.floor(valor * 100.0 + 1e-8) / 100.0;
    }

    public static String formatarValor(double valor)
    {
        return String.format(Locale.US, "%.2f", valor).replace(".", ",");
    }

    public static String formatarHoras(double horas)
    {
        if (horas == (long) horas)
        {
            return String.valueOf((long) horas);
        }
        return String.valueOf(horas).replace(".", ",");
    }
}
