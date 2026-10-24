package br.ufal.ic.p2.wepayu.persistencia;

import br.ufal.ic.p2.wepayu.Exception.ErroPersistenciaException;
import br.ufal.ic.p2.wepayu.Exception.WePayUException;
import br.ufal.ic.p2.wepayu.models.agendas.AgendaPagamento;
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
    salva e carrega os empregados e as agendas de pagamento no arquivo dados.xml usando XMLEncoder e XMLDecoder
    é o que faz os dados continuarem existindo de um arquivo de teste para o outro
    no arquivo vão só DTOs; na leitura cada empregado é recriado pelos construtores e métodos normais, com validação
*/

public class PersistenciaXML
{

    private String arquivo;
    private ArrayList<Empregado> empregadosLidos = new ArrayList<>();
    private ArrayList<String> agendasLidas = new ArrayList<>();

    public PersistenciaXML(String arquivo)
    {
        this.arquivo = arquivo;
    }

    // transforma cada empregado em DTO e grava junto com as agendas; quem foi removido continua null para os ids não mudarem
    public void salvar(ArrayList<Empregado> empregados, ArrayList<String> agendas) throws WePayUException
    {
        EmpregadoDTO[] lista = new EmpregadoDTO[empregados.size()];

        for (int i = 0; i < empregados.size(); i++)
        {
            if (empregados.get(i) != null)
            {
                lista[i] = new EmpregadoDTO();
                empregados.get(i).exportarPara(lista[i]);
            }
        }

        DadosDTO dados = new DadosDTO();
        dados.setEmpregados(lista);
        dados.setAgendas(agendas);

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

    // lê o arquivo e recria os empregados e as agendas; sem arquivo, as duas listas ficam vazias
    public void carregar() throws WePayUException
    {
        if (!new File(arquivo).exists())
        {
            return;
        }

        try
        {
            XMLDecoder decoder = new XMLDecoder(new FileInputStream(arquivo));
            DadosDTO dados = (DadosDTO) decoder.readObject();
            decoder.close();

            for (EmpregadoDTO empregado : dados.getEmpregados())
            {
                empregadosLidos.add(reconstruir(empregado));
            }
            agendasLidas = dados.getAgendas();
        }
        catch (FileNotFoundException e)
        {
            throw new ErroPersistenciaException();
        }
    }

    public ArrayList<Empregado> getEmpregadosLidos()
    {
        return empregadosLidos;
    }

    public ArrayList<String> getAgendasLidas()
    {
        return agendasLidas;
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

        if (dados.getAgendaPagamento() != null)
        {
            empregado.setAgendaPagamento(AgendaPagamento.criar(dados.getAgendaPagamento()));
        }

        if (dados.getIdSindicato() != null)
        {
            empregado.setMembroSindicato(new MembroSindicato(dados.getIdSindicato(), dados.getTaxaSindical()));
        }

        // os lançamentos são registrados de novo; cada lista só tem itens no tipo de empregado certo
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