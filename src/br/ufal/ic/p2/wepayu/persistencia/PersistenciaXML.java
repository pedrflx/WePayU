package br.ufal.ic.p2.wepayu.persistencia;

import br.ufal.ic.p2.wepayu.Exception.ErroPersistenciaException;
import br.ufal.ic.p2.wepayu.Exception.WePayUException;
import br.ufal.ic.p2.wepayu.models.empregados.Empregado;
import br.ufal.ic.p2.wepayu.models.sindicato.MembroSindicato;
import br.ufal.ic.p2.wepayu.sistema.FabricaEmpregado;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

/*
    PersistenciaXML
    salva e carrega a lista de empregados no arquivo dados.xml usando XMLEncoder e XMLDecoder
    é o que faz os dados continuarem existindo de um arquivo de teste para o outro
    no arquivo vão só DTOs.
    na leitura cada empregado é recriado pelos construtores e métodos normais, com validação
*/

public class PersistenciaXML
{

    private String arquivo;

    public PersistenciaXML(String arquivo)
    {
        this.arquivo = arquivo;
    }

    // transforma cada empregado em DTO e grava
    // quem foi removido continua null para os ids não mudarem
    public void salvar(ArrayList<Empregado> empregados) throws WePayUException
    {
        EmpregadoDTO[] dados = new EmpregadoDTO[empregados.size()];

        for (int i = 0; i < empregados.size(); i++)
        {
            if (empregados.get(i) != null)
            {
                dados[i] = new EmpregadoDTO();
                empregados.get(i).exportarPara(dados[i]);
            }
        }

        try
        {
            XMLEncoder encoder = new XMLEncoder(new FileOutputStream(arquivo));
            encoder.writeObject(dados);
            encoder.close();
        }
        catch (FileNotFoundException e)
        {
            throw new ErroPersistenciaException();
        }
    }

    // lê os DTOs do arquivo e recria cada empregado. sem arquivo, começa com a lista vazia
    public ArrayList<Empregado> carregar() throws WePayUException
    {
        ArrayList<Empregado> empregados = new ArrayList<>();

        if (!new File(arquivo).exists())
        {
            return empregados;
        }

        try
        {
            XMLDecoder decoder = new XMLDecoder(new FileInputStream(arquivo));
            EmpregadoDTO[] lidos = (EmpregadoDTO[]) decoder.readObject();
            decoder.close();

            for (EmpregadoDTO dados : lidos)
            {
                empregados.add(reconstruir(dados));
            }
            return empregados;
        }
        catch (FileNotFoundException e)
        {
            throw new ErroPersistenciaException();
        }
    }

    // recria o empregado pelos construtores e métodos normais, então tudo passa pela validação de novo
    private Empregado reconstruir(EmpregadoDTO dados) throws WePayUException
    {
        if (dados == null)
        {
            return null;
        }

        Empregado empregado = criarPeloTipo(dados);

        if (dados.getMetodoPagamento().equals("banco"))
        {
            empregado.alterarAtributo("metodoPagamento", "banco", dados.getBanco(), dados.getAgencia(), dados.getContaCorrente());
        }
        else
        {
            empregado.alterarAtributo("metodoPagamento", dados.getMetodoPagamento());
        }

        if (dados.getIdSindicato() != null)
        {
            empregado.setMembroSindicato(new MembroSindicato(dados.getIdSindicato(), dados.getTaxaSindical()));
        }

        // os lançamentos são registrados de novo
        // cada lista só tem itens no tipo de empregado certo
        for (LancamentoDTO cartao : dados.getCartoes())
        {
            empregado.registrarCartao(cartao.getData(), cartao.getValor());
        }

        for (LancamentoDTO venda : dados.getVendas())
        {
            empregado.registrarVenda(venda.getData(), venda.getValor());
        }

        for (LancamentoDTO taxa : dados.getTaxas())
        {
            empregado.registrarTaxa(taxa.getData(), taxa.getValor());
        }

        return empregado;
    }

    // só o comissionado é criado com comissão
    private Empregado criarPeloTipo(EmpregadoDTO dados) throws WePayUException
    {
        if (dados.getTipo().equals("comissionado"))
        {
            return FabricaEmpregado.criar(dados.getNome(), dados.getEndereco(), dados.getTipo(), dados.getSalario(), dados.getComissao());
        }

        return FabricaEmpregado.criar(dados.getNome(), dados.getEndereco(), dados.getTipo(), dados.getSalario());
    }
}