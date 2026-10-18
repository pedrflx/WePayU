package br.ufal.ic.p2.wepayu.models.sindicato;

import br.ufal.ic.p2.wepayu.Exception.*;
import br.ufal.ic.p2.wepayu.models.lancamentos.TaxaServico;
import br.ufal.ic.p2.wepayu.utils.Conversor;
import java.util.ArrayList;
import java.util.Date;
import br.ufal.ic.p2.wepayu.persistencia.EmpregadoDTO;

/*
    MembroSindicato
    dados de um empregado filiado ao sindicato: identificação, taxa sindical diária e taxas de serviço lançadas
    o empregado tem um MembroSindicato quando é sindicalizado e fica com null quando não é
*/

public class MembroSindicato
{

    private String idMembro;
    private String taxaSindical;
    private ArrayList<TaxaServico> taxas = new ArrayList<>();


    public MembroSindicato(String idMembro, String taxaSindical) throws WePayUException
    {
        Conversor.validarTexto(idMembro, new IdentificacaoSindicatoNulaException());
        Conversor.converterNaoNegativo(taxaSindical, new TaxaSindicalNulaException(), new TaxaSindicalNaoNumericaException(), new TaxaSindicalNegativaException());

        this.idMembro = idMembro;
        this.taxaSindical = taxaSindical;
    }

    public MembroSindicato(MembroSindicato outro)
    {
        this.idMembro = outro.idMembro;
        this.taxaSindical = outro.taxaSindical;
        this.taxas = new ArrayList<>(outro.taxas);
    }

    public void registrarTaxa(String data, String valor) throws WePayUException
    {
        Date dataTaxa = Conversor.converterData(data, new DataInvalidaException());
        double valorTaxa = Conversor.converterPositivo(valor, new ValorNaoPositivoException());

        taxas.add(new TaxaServico(dataTaxa, valorTaxa));
    }

    public String getTaxasServico(String dataInicial, String dataFinal) throws WePayUException
    {
        Date[] periodo = Conversor.converterPeriodo(dataInicial, dataFinal);
        return Conversor.formatarValor(somarTaxas(periodo[0], periodo[1]));
    }

    /*
        calcularDesconto
        desconto do sindicato num pagamento: taxa diária vezes os dias do período mais as taxas de serviço do período
        os dias vêm do empregado, porque cada tipo de empregado tem um período diferente
    */

    public double calcularDesconto(Date inicio, Date fim, int dias)
    {
        double desconto = Conversor.lerNumero(taxaSindical) * dias;
        desconto += somarTaxas(inicio, fim);

        return Conversor.truncar(desconto);
    }

    private double somarTaxas(Date inicio, Date fim)
    {
        double total = 0;
        for (TaxaServico taxa : taxas)
        {
            if (taxa.estaEntre(inicio, fim))
            {
                total += taxa.getValor();
            }
        }
        return total;
    }

    public String getIdMembro()
    {
        return idMembro;
    }

    public String getTaxaSindical()
    {
        return taxaSindical;
    }

    // escreve no DTO o id, a taxa sindical e as taxas de serviço
    public void exportarPara(EmpregadoDTO dados)
    {
        dados.setIdSindicato(idMembro);
        dados.setTaxaSindical(taxaSindical);

        for (TaxaServico taxa : taxas)
        {
            dados.getTaxas().add(taxa.paraDTO());
        }
    }

}
