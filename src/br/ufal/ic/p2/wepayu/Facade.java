package br.ufal.ic.p2.wepayu;

import br.ufal.ic.p2.wepayu.Exception.*;
import br.ufal.ic.p2.wepayu.models.Empregado;
import java.util.Date;

public class Facade
{

    Empregado[] empregados = new Empregado[100];
    int proximoId = 1;

    private Empregado[][] historyEmpregados = new Empregado[500][100];
    private int[] historyProximoId = new int[500];
    private int currentState = 0;
    private int maxState = 0;
    private boolean encerrado = false;

    public Facade()
    {
        historyProximoId[0] = proximoId;
    }

    private Empregado cloneEmpregado(Empregado e)
    {
        if (e == null) return null;
        Empregado copy = new Empregado(e.getNome(), e.getEndereco(), e.getTipo(), e.getSalario());
        copy.setComissao(e.getComissao());
        copy.setMetodoPagamento(e.getMetodoPagamento());
        copy.setBanco(e.getBanco());
        copy.setAgencia(e.getAgencia());
        copy.setContaCorrente(e.getContaCorrente());
        copy.setSindicalizado(e.getSindicalizado());
        copy.setIdSindicato(e.getIdSindicato());
        copy.setTaxaSindical(e.getTaxaSindical());

        for (int i = 0; i < e.getQtdCartoes(); i++) copy.registrarCartao(e.getDatasCartoes()[i], e.getHorasCartoes()[i]);
        for (int i = 0; i < e.getQtdVendas(); i++) copy.registrarVenda(e.getDatasVendas()[i], e.getValoresVendas()[i]);
        for (int i = 0; i < e.getQtdTaxas(); i++) copy.registrarTaxa(e.getDatasTaxas()[i], e.getValoresTaxas()[i]);

        return copy;
    }

    private void saveState()
    {
        currentState++;
        maxState = currentState;

        historyProximoId[currentState] = proximoId;
        historyEmpregados[currentState] = new Empregado[100];

        for (int i = 1; i < proximoId; i++)
        {
            if (empregados[i] != null)
            {
                historyEmpregados[currentState][i] = cloneEmpregado(empregados[i]);
            }
        }
    }

    private void loadState(int stateIndex)
    {
        proximoId = historyProximoId[stateIndex];
        empregados = new Empregado[100];

        for (int i = 1; i < proximoId; i++)
        {
            if (historyEmpregados[stateIndex][i] != null)
            {
                empregados[i] = cloneEmpregado(historyEmpregados[stateIndex][i]);
            }
        }
    }

    public void undo() throws Exception
    {
        if (encerrado)
        {
            throw new SistemaEncerradoException();
        }

        if (currentState <= 0)
        {
            throw new NenhumComandoParaDesfazerException();
        }

        currentState--;
        loadState(currentState);
    }

    public void redo() throws Exception
    {
        if (encerrado)
        {
            throw new SistemaEncerradoException();
        }

        if (currentState >= maxState)
        {
            throw new NenhumComandoParaRefazerException();
        }

        currentState++;
        loadState(currentState);
    }

    public void zerarSistema()
    {
        empregados = new Empregado[100];
        proximoId = 1;
        encerrado = false;

        saveState();
    }

    public void encerrarSistema()
    {
        encerrado = true;
    }

    public String getNumeroDeEmpregados() throws Exception
    {
        int count = 0;
        for (int i = 1; i < proximoId; i++)
        {
            if (empregados[i] != null) count++;
        }
        return String.valueOf(count);
    }

    public String getEmpregadoPorNome(String nome, String indice) throws Exception
    {
        int ind;
        try { ind = Integer.parseInt(indice); }
        catch(Exception e) { throw new IndiceInvalidoException(); }

        int count = 0;
        for (int i = 1; i < proximoId; i++)
        {
            if (empregados[i] != null && empregados[i].getNome().equals(nome))
            {
                count++;
                if (count == ind) return String.valueOf(i);
            }
        }
        throw new EmpregadoNaoEncontradoPorNomeException();
    }

    public String criarEmpregado(String nome, String endereco, String tipo, String salario) throws Exception
    {
        if (nome == null || nome.isEmpty()) throw new NomeNuloException();
        if (endereco == null || endereco.isEmpty()) throw new EnderecoNuloException();
        if (tipo.equals("comissionado")) throw new TipoNaoAplicavelException();
        if (!tipo.equals("horista") && !tipo.equals("assalariado")) throw new TipoInvalidoException();
        if (salario == null || salario.isEmpty()) throw new SalarioNuloException();

        try
        {
            double salValor = Double.parseDouble(salario.replace(",", "."));
            if (salValor < 0) throw new SalarioNegativoException();
        }
        catch (NumberFormatException e)
        {
            throw new SalarioNaoNumericoException();
        }

        empregados[proximoId] = new Empregado(nome, endereco, tipo, salario);

        String idGerado = String.valueOf(proximoId);
        proximoId++;

        saveState();

        return idGerado;
    }

    public String criarEmpregado(String nome, String endereco, String tipo, String salario, String comissao) throws Exception
    {
        if (nome == null || nome.isEmpty()) throw new NomeNuloException();
        if (endereco == null || endereco.isEmpty()) throw new EnderecoNuloException();
        if (tipo.equals("horista") || tipo.equals("assalariado")) throw new TipoNaoAplicavelException();
        if (!tipo.equals("comissionado")) throw new TipoInvalidoException();
        if (salario == null || salario.isEmpty()) throw new SalarioNuloException();

        try
        {
            double salValor = Double.parseDouble(salario.replace(",", "."));
            if (salValor < 0) throw new SalarioNegativoException();
        }
        catch (NumberFormatException e)
        {
            throw new SalarioNaoNumericoException();
        }

        if (comissao == null || comissao.isEmpty()) throw new ComissaoNulaException();

        try
        {
            double comValor = Double.parseDouble(comissao.replace(",", "."));
            if (comValor < 0) throw new ComissaoNegativaException();
        }
        catch (NumberFormatException e)
        {
            throw new ComissaoNaoNumericaException();
        }

        empregados[proximoId] = new Empregado(nome, endereco, tipo, salario, comissao);

        String idGerado = String.valueOf(proximoId);
        proximoId++;

        saveState();

        return idGerado;
    }

    public void removerEmpregado(String emp) throws Exception
    {
        if (emp == null || emp.isEmpty())
        {
            throw new IdentificacaoEmpregadoNulaException();
        }

        int id;

        try
        {
            id = Integer.parseInt(emp);
        }
        catch (Exception e)
        {
            throw new EmpregadoNaoExisteException();
        }

        if (id <= 0 || empregados[id] == null)
        {
            throw new EmpregadoNaoExisteException();
        }

        empregados[id] = null;
        saveState();
    }

    public String getAtributoEmpregado(String emp, String atributo) throws Exception
    {
        if (emp == null || emp.isEmpty())
        {
            throw new IdentificacaoEmpregadoNulaException();
        }

        int id;

        try
        {
            id = Integer.parseInt(emp);
        }
        catch (Exception e)
        {
            throw new EmpregadoNaoExisteException();
        }

        if (id <= 0 || empregados[id] == null)
        {
            throw new EmpregadoNaoExisteException();
        }

        Empregado empregadoTemporario = empregados[id];

        if (atributo.equals("nome"))
        {
            return empregadoTemporario.getNome();
        }
        else if (atributo.equals("endereco"))
        {
            return empregadoTemporario.getEndereco();
        }
        else if (atributo.equals("tipo"))
        {
            return empregadoTemporario.getTipo();
        }
        else if (atributo.equals("sindicalizado"))
        {
            return empregadoTemporario.getSindicalizado();
        }
        else if (atributo.equals("idSindicato"))
        {
            if (empregadoTemporario.getSindicalizado().equals("false")) throw new EmpregadoNaoEhSindicalizadoException();
            return empregadoTemporario.getIdSindicato();
        }
        else if (atributo.equals("taxaSindical"))
        {
            if (empregadoTemporario.getSindicalizado().equals("false")) throw new EmpregadoNaoEhSindicalizadoException();
            String taxa = empregadoTemporario.getTaxaSindical();
            if (!taxa.contains(",")) return taxa + ",00";
            return taxa;
        }
        else if (atributo.equals("salario"))
        {
            String sal = empregadoTemporario.getSalario();
            if (!sal.contains(","))
            {
                return sal + ",00";
            }
            return sal;
        }
        else if (atributo.equals("comissao"))
        {
            if (!empregadoTemporario.getTipo().equals("comissionado")) throw new EmpregadoNaoEhComissionadoException();
            return empregadoTemporario.getComissao();
        }
        else if (atributo.equals("metodoPagamento"))
        {
            return empregadoTemporario.getMetodoPagamento();
        }
        else if (atributo.equals("banco") || atributo.equals("agencia") || atributo.equals("contaCorrente"))
        {
            if (!empregadoTemporario.getMetodoPagamento().equals("banco")) throw new EmpregadoNaoRecebeEmBancoException();
            if (atributo.equals("banco")) return empregadoTemporario.getBanco();
            if (atributo.equals("agencia")) return empregadoTemporario.getAgencia();
            if (atributo.equals("contaCorrente")) return empregadoTemporario.getContaCorrente();
        }

        throw new AtributoNaoExisteException();
    }

    public void lancaCartao(String emp, String data, String horas) throws Exception
    {
        if (emp == null || emp.isEmpty()) throw new IdentificacaoEmpregadoNulaException();

        int id;

        try
        {
            id = Integer.parseInt(emp);
        }
        catch (Exception e)
        {
            throw new EmpregadoNaoExisteException();
        }

        if (id <= 0 || empregados[id] == null) throw new EmpregadoNaoExisteException();

        Empregado empregadoTemporario = empregados[id];

        if (!empregadoTemporario.getTipo().equals("horista"))
        {
            throw new EmpregadoNaoEhHoristaException();
        }

        try
        {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("d/M/yyyy");
            sdf.setLenient(false);
            sdf.parse(data);
        }
        catch (Exception e)
        {
            throw new DataInvalidaException();
        }

        double horasTrabalhadas;

        try
        {
            horasTrabalhadas = Double.parseDouble(horas.replace(",", "."));
            if (horasTrabalhadas <= 0) throw new HorasNaoPositivasException();
        }
        catch (Exception e)
        {
            throw new HorasNaoPositivasException();
        }

        empregadoTemporario.registrarCartao(data, horasTrabalhadas);
        saveState();
    }

    public String getHorasNormaisTrabalhadas(String emp, String dataInicial, String dataFinal) throws Exception
    {
        if (emp == null || emp.isEmpty()) throw new IdentificacaoEmpregadoNulaException();
        int id;

        try
        {
            id = Integer.parseInt(emp);
        }
        catch (Exception e)
        {
            throw new EmpregadoNaoExisteException();
        }

        if (id <= 0 || empregados[id] == null) throw new EmpregadoNaoExisteException();
        Empregado empregadoTemporario = empregados[id];

        if (!empregadoTemporario.getTipo().equals("horista"))
        {
            throw new EmpregadoNaoEhHoristaException();
        }

        Date dataIni;

        try
        {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("d/M/yyyy");
            sdf.setLenient(false);
            dataIni = sdf.parse(dataInicial);
        }
        catch (Exception e)
        {
            throw new DataInicialInvalidaException();
        }

        Date dataFim;

        try
        {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("d/M/yyyy");
            sdf.setLenient(false);
            dataFim = sdf.parse(dataFinal);
        }
        catch (Exception e)
        {
            throw new DataFinalInvalidaException();
        }

        if (dataIni.after(dataFim))
        {
            throw new DataInicialPosteriorException();
        }

        double horasNormais = 0;
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("d/M/yyyy");

        for (int i = 0; i < empregadoTemporario.getQtdCartoes(); i++)
        {
            Date dataCartao = sdf.parse(empregadoTemporario.getDatasCartoes()[i]);

            if (!dataCartao.before(dataIni) && dataCartao.before(dataFim))
            {
                double horas = empregadoTemporario.getHorasCartoes()[i];
                if (horas > 8)
                {
                    horasNormais += 8;
                }
                else
                {
                    horasNormais += horas;
                }
            }
        }

        if (horasNormais == (long) horasNormais)
        {
            return String.valueOf((long) horasNormais);
        }
        else
        {
            return String.valueOf(horasNormais).replace(".", ",");
        }
    }

    public String getHorasExtrasTrabalhadas(String emp, String dataInicial, String dataFinal) throws Exception
    {
        if (emp == null || emp.isEmpty()) throw new IdentificacaoEmpregadoNulaException();

        int id;

        try
        {
            id = Integer.parseInt(emp);
        }
        catch (Exception e)
        {
            throw new EmpregadoNaoExisteException();
        }

        if (id <= 0 || empregados[id] == null) throw new EmpregadoNaoExisteException();

        Empregado empregadoTemporario = empregados[id];

        if (!empregadoTemporario.getTipo().equals("horista"))
        {
            throw new EmpregadoNaoEhHoristaException();
        }

        Date dataIni;

        try
        {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("d/M/yyyy");
            sdf.setLenient(false);
            dataIni = sdf.parse(dataInicial);
        }
        catch (Exception e)
        {
            throw new DataInicialInvalidaException();
        }

        Date dataFim;

        try
        {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("d/M/yyyy");
            sdf.setLenient(false);
            dataFim = sdf.parse(dataFinal);
        }
        catch (Exception e)
        {
            throw new DataFinalInvalidaException();
        }

        if (dataIni.after(dataFim))
        {
            throw new DataInicialPosteriorException();
        }

        double horasExtras = 0;
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("d/M/yyyy");

        for (int i = 0; i < empregadoTemporario.getQtdCartoes(); i++)
        {
            Date dataCartao = sdf.parse(empregadoTemporario.getDatasCartoes()[i]);

            if (!dataCartao.before(dataIni) && dataCartao.before(dataFim))
            {
                double horas = empregadoTemporario.getHorasCartoes()[i];
                if (horas > 8)
                {
                    horasExtras += (horas - 8);
                }
            }
        }

        if (horasExtras == (long) horasExtras)
        {
            return String.valueOf((long) horasExtras);
        }
        else
        {
            return String.valueOf(horasExtras).replace(".", ",");
        }
    }

    public void lancaVenda(String emp, String data, String valor) throws Exception
    {
        if (emp == null || emp.isEmpty()) throw new IdentificacaoEmpregadoNulaException();

        int id;

        try
        {
            id = Integer.parseInt(emp);
        }
        catch (Exception e)
        {
            throw new EmpregadoNaoExisteException();
        }

        if (id <= 0 || empregados[id] == null) throw new EmpregadoNaoExisteException();

        Empregado empregadoTemporario = empregados[id];

        if (!empregadoTemporario.getTipo().equals("comissionado"))
        {
            throw new EmpregadoNaoEhComissionadoException();
        }

        try
        {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("d/M/yyyy");
            sdf.setLenient(false);
            sdf.parse(data);
        }
        catch (Exception e)
        {
            throw new DataInvalidaException();
        }

        double valorVenda;

        try
        {
            valorVenda = Double.parseDouble(valor.replace(",", "."));
            if (valorVenda <= 0) throw new ValorNaoPositivoException();
        }
        catch (Exception e)
        {
            throw new ValorNaoPositivoException();
        }

        empregadoTemporario.registrarVenda(data, valorVenda);
        saveState();
    }

    public String getVendasRealizadas(String emp, String dataInicial, String dataFinal) throws Exception
    {
        if (emp == null || emp.isEmpty()) throw new IdentificacaoEmpregadoNulaException();

        int id;

        try
        {
            id = Integer.parseInt(emp);
        }
        catch (Exception e)
        {
            throw new EmpregadoNaoExisteException();
        }

        if (id <= 0 || empregados[id] == null) throw new EmpregadoNaoExisteException();

        Empregado empregadoTemporario = empregados[id];

        if (!empregadoTemporario.getTipo().equals("comissionado"))
        {
            throw new EmpregadoNaoEhComissionadoException();
        }

        Date dataIni;

        try
        {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("d/M/yyyy");
            sdf.setLenient(false);
            dataIni = sdf.parse(dataInicial);
        }
        catch (Exception e)
        {
            throw new DataInicialInvalidaException();
        }

        Date dataFim;

        try
        {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("d/M/yyyy");
            sdf.setLenient(false);
            dataFim = sdf.parse(dataFinal);
        }
        catch (Exception e)
        {
            throw new DataFinalInvalidaException();
        }

        if (dataIni.after(dataFim))
        {
            throw new DataInicialPosteriorException();
        }

        double totalVendas = 0;
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("d/M/yyyy");

        for (int i = 0; i < empregadoTemporario.getQtdVendas(); i++)
        {
            Date dataVenda = sdf.parse(empregadoTemporario.getDatasVendas()[i]);

            if (!dataVenda.before(dataIni) && dataVenda.before(dataFim))
            {
                totalVendas += empregadoTemporario.getValoresVendas()[i];
            }
        }

        return String.format(java.util.Locale.US, "%.2f", totalVendas).replace(".", ",");
    }

    public void alteraEmpregado(String emp, String atributo, String valor) throws Exception
    {
        if (emp == null || emp.isEmpty()) throw new IdentificacaoEmpregadoNulaException();

        int id;

        try
        {
            id = Integer.parseInt(emp);
        }
        catch (Exception e)
        {
            throw new EmpregadoNaoExisteException();
        }

        if (id <= 0 || empregados[id] == null) throw new EmpregadoNaoExisteException();

        Empregado empregadoTemporario = empregados[id];

        if (atributo.equals("nome"))
        {
            if (valor == null || valor.isEmpty()) throw new NomeNuloException();
            empregadoTemporario.setNome(valor);
        }
        else if (atributo.equals("endereco"))
        {
            if (valor == null || valor.isEmpty()) throw new EnderecoNuloException();
            empregadoTemporario.setEndereco(valor);
        }
        else if (atributo.equals("tipo"))
        {
            if (!valor.equals("horista") && !valor.equals("assalariado") && !valor.equals("comissionado")) throw new TipoInvalidoException();
            empregadoTemporario.setTipo(valor);
        }
        else if (atributo.equals("salario"))
        {
            if (valor == null || valor.isEmpty()) throw new SalarioNuloException();
            try
            {
                double v = Double.parseDouble(valor.replace(",", "."));
                if (v < 0) throw new SalarioNegativoException();
            }
            catch (NumberFormatException e)
            {
                throw new SalarioNaoNumericoException();
            }
            empregadoTemporario.setSalario(valor);
        }
        else if (atributo.equals("comissao"))
        {
            if (!empregadoTemporario.getTipo().equals("comissionado")) throw new EmpregadoNaoEhComissionadoException();
            if (valor == null || valor.isEmpty()) throw new ComissaoNulaException();
            try
            {
                double v = Double.parseDouble(valor.replace(",", "."));
                if (v < 0) throw new ComissaoNegativaException();
            }
            catch (NumberFormatException e)
            {
                throw new ComissaoNaoNumericaException();
            }
            empregadoTemporario.setComissao(valor);
        }
        else if (atributo.equals("metodoPagamento"))
        {
            if (!valor.equals("correios") && !valor.equals("emMaos") && !valor.equals("banco")) throw new MetodoPagamentoInvalidoException();
            empregadoTemporario.setMetodoPagamento(valor);
        }
        else if (atributo.equals("sindicalizado"))
        {
            if (!valor.equals("true") && !valor.equals("false")) throw new ValorNaoBooleanoException();
            if (valor.equals("false"))
            {
                empregadoTemporario.setSindicalizado("false");
                empregadoTemporario.setIdSindicato(null);
                empregadoTemporario.setTaxaSindical(null);
            }
        }
        else
        {
            throw new AtributoNaoExisteException();
        }

        saveState();
    }

    public void alteraEmpregado(String emp, String atributo, String valor, String ext) throws Exception
    {
        if (emp == null || emp.isEmpty()) throw new IdentificacaoEmpregadoNulaException();

        int id;

        try
        {
            id = Integer.parseInt(emp);
        }
        catch (Exception e)
        {
            throw new EmpregadoNaoExisteException();
        }

        if (id <= 0 || empregados[id] == null) throw new EmpregadoNaoExisteException();

        Empregado empregadoTemporario = empregados[id];

        if (atributo.equals("tipo"))
        {
            if (valor.equals("comissionado"))
            {
                empregadoTemporario.setTipo(valor);
                empregadoTemporario.setComissao(ext);
            }
            else if (valor.equals("horista") || valor.equals("assalariado"))
            {
                empregadoTemporario.setTipo(valor);
                empregadoTemporario.setSalario(ext);
            }
            else
            {
                throw new TipoInvalidoException();
            }
        }
        else
        {
            throw new AtributoNaoExisteException();
        }

        saveState();
    }

    public void alteraEmpregado(String emp, String atributo, String valor, String idSindicato, String taxaSindical) throws Exception
    {
        if (emp == null || emp.isEmpty()) throw new IdentificacaoEmpregadoNulaException();

        int id;

        try
        {
            id = Integer.parseInt(emp);
        }
        catch (Exception e)
        {
            throw new EmpregadoNaoExisteException();
        }

        if (id <= 0 || empregados[id] == null) throw new EmpregadoNaoExisteException();

        if (atributo.equals("sindicalizado"))
        {
            if (!valor.equals("true") && !valor.equals("false")) throw new ValorNaoBooleanoException();
            if (valor.equals("true"))
            {
                if (idSindicato == null || idSindicato.isEmpty()) throw new IdentificacaoSindicatoNulaException();
                if (taxaSindical == null || taxaSindical.isEmpty()) throw new TaxaSindicalNulaException();
                try
                {
                    double taxa = Double.parseDouble(taxaSindical.replace(",", "."));
                    if (taxa < 0) throw new TaxaSindicalNegativaException();
                }
                catch (NumberFormatException e)
                {
                    throw new TaxaSindicalNaoNumericaException();
                }

                for (int i = 1; i < proximoId; i++)
                {
                    if (empregados[i] != null && idSindicato.equals(empregados[i].getIdSindicato()))
                    {
                        throw new IdSindicatoDuplicadoException();
                    }
                }

                Empregado empregadoTemporario = empregados[id];
                empregadoTemporario.setSindicalizado("true");
                empregadoTemporario.setIdSindicato(idSindicato);
                empregadoTemporario.setTaxaSindical(taxaSindical);
            }
        }
        else
        {
            throw new AtributoNaoExisteException();
        }

        saveState();
    }

    public void alteraEmpregado(String emp, String atributo, String valor, String banco, String agencia, String contaCorrente) throws Exception
    {
        if (emp == null || emp.isEmpty()) throw new IdentificacaoEmpregadoNulaException();

        int id;

        try
        {
            id = Integer.parseInt(emp);
        }
        catch (Exception e)
        {
            throw new EmpregadoNaoExisteException();
        }

        if (id <= 0 || empregados[id] == null) throw new EmpregadoNaoExisteException();

        Empregado empregadoTemporario = empregados[id];

        if (atributo.equals("metodoPagamento") && valor.equals("banco"))
        {
            if (banco == null || banco.isEmpty()) throw new BancoNuloException();
            if (agencia == null || agencia.isEmpty()) throw new AgenciaNulaException();
            if (contaCorrente == null || contaCorrente.isEmpty()) throw new ContaCorrenteNulaException();

            empregadoTemporario.setMetodoPagamento(valor);
            empregadoTemporario.setBanco(banco);
            empregadoTemporario.setAgencia(agencia);
            empregadoTemporario.setContaCorrente(contaCorrente);
        }
        else
        {
            throw new AtributoNaoExisteException();
        }

        saveState();
    }

    public void lancaTaxaServico(String membro, String data, String valor) throws Exception
    {
        if (membro == null || membro.isEmpty()) throw new IdentificacaoMembroNulaException();

        Empregado empMembro = null;

        for (int i = 1; i < proximoId; i++)
        {
            if (empregados[i] != null && membro.equals(empregados[i].getIdSindicato()))
            {
                empMembro = empregados[i];
                break;
            }
        }

        if (empMembro == null) throw new MembroNaoExisteException();

        try
        {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("d/M/yyyy");
            sdf.setLenient(false);
            sdf.parse(data);
        }
        catch (Exception e)
        {
            throw new DataInvalidaException();
        }

        double valorTaxa;

        try
        {
            valorTaxa = Double.parseDouble(valor.replace(",", "."));
            if (valorTaxa <= 0) throw new ValorNaoPositivoException();
        }
        catch (Exception e)
        {
            throw new ValorNaoPositivoException();
        }

        empMembro.registrarTaxa(data, valorTaxa);
        saveState();
    }

    public String getTaxasServico(String emp, String dataInicial, String dataFinal) throws Exception
    {
        if (emp == null || emp.isEmpty()) throw new IdentificacaoEmpregadoNulaException();

        int id;

        try
        {
            id = Integer.parseInt(emp);
        }
        catch (Exception e)
        {
            throw new EmpregadoNaoExisteException();
        }

        if (id <= 0 || empregados[id] == null) throw new EmpregadoNaoExisteException();

        Empregado empregadoTemporario = empregados[id];

        if (empregadoTemporario.getSindicalizado().equals("false"))
        {
            throw new EmpregadoNaoEhSindicalizadoException();
        }

        Date dataIni;

        try
        {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("d/M/yyyy");
            sdf.setLenient(false);
            dataIni = sdf.parse(dataInicial);
        }
        catch (Exception e)
        {
            throw new DataInicialInvalidaException();
        }

        Date dataFim;

        try
        {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("d/M/yyyy");
            sdf.setLenient(false);
            dataFim = sdf.parse(dataFinal);
        }
        catch (Exception e)
        {
            throw new DataFinalInvalidaException();
        }

        if (dataIni.after(dataFim))
        {
            throw new DataInicialPosteriorException();
        }

        double totalTaxas = 0;
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("d/M/yyyy");

        for (int i = 0; i < empregadoTemporario.getQtdTaxas(); i++)
        {
            Date dataTaxa = sdf.parse(empregadoTemporario.getDatasTaxas()[i]);

            if (!dataTaxa.before(dataIni) && dataTaxa.before(dataFim))
            {
                totalTaxas += empregadoTemporario.getValoresTaxas()[i];
            }
        }

        return String.format(java.util.Locale.US, "%.2f", totalTaxas).replace(".", ",");
    }

    private double trunc2(double val)
    {
        return Math.floor(val * 100.0 + 1e-8) / 100.0;
    }

    private Date getFirstPayday(Empregado e) throws Exception
    {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        if (e.getTipo().equals("assalariado"))
        {
            return sdf.parse("2005-01-31");
        }
        else if (e.getTipo().equals("comissionado"))
        {
            return sdf.parse("2005-01-14");
        }
        else
        {
            Date earliest = sdf.parse("2099-12-31");
            if (e.getQtdCartoes() > 0)
            {
                java.text.SimpleDateFormat sdfBR = new java.text.SimpleDateFormat("d/M/yyyy");
                for (int i = 0; i < e.getQtdCartoes(); i++)
                {
                    Date d = sdfBR.parse(e.getDatasCartoes()[i]);
                    if (d.before(earliest)) earliest = d;
                }
            }
            else
            {
                earliest = sdf.parse("2005-01-01");
            }

            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime(earliest);
            while (cal.get(java.util.Calendar.DAY_OF_WEEK) != java.util.Calendar.FRIDAY)
            {
                cal.add(java.util.Calendar.DAY_OF_MONTH, 1);
            }
            return cal.getTime();
        }
    }

    private Date getNextPayday(Empregado e, Date curr)
    {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(curr);
        if (e.getTipo().equals("horista"))
        {
            cal.add(java.util.Calendar.DAY_OF_MONTH, 7);
        }
        else if (e.getTipo().equals("comissionado"))
        {
            cal.add(java.util.Calendar.DAY_OF_MONTH, 14);
        }
        else
        {
            cal.add(java.util.Calendar.MONTH, 1);
            cal.set(java.util.Calendar.DAY_OF_MONTH, cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH));
            int dow = cal.get(java.util.Calendar.DAY_OF_WEEK);
            if (dow == java.util.Calendar.SATURDAY) cal.add(java.util.Calendar.DAY_OF_MONTH, -1);
            else if (dow == java.util.Calendar.SUNDAY) cal.add(java.util.Calendar.DAY_OF_MONTH, -2);
        }
        return cal.getTime();
    }

    private boolean isPayday(Empregado e, Date date)
    {
        try
        {
            Date first = getFirstPayday(e);
            if (first == null || date.before(first)) return false;
            Date curr = first;
            while (curr.before(date))
            {
                curr = getNextPayday(e, curr);
            }
            return curr.equals(date);
        }
        catch (Exception ex)
        {
            return false;
        }
    }

    private Date getStartDate(Empregado e, Date payday)
    {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(payday);

        if (e.getTipo().equals("horista"))
        {
            cal.add(java.util.Calendar.DAY_OF_MONTH, -6);
            return cal.getTime();
        }
        else if (e.getTipo().equals("comissionado"))
        {
            cal.add(java.util.Calendar.DAY_OF_MONTH, -13);
            return cal.getTime();
        }
        else
        {
            cal.set(java.util.Calendar.DAY_OF_MONTH, 1);
            return cal.getTime();
        }
    }

    private double calcularBruto(Empregado e, Date payday) throws Exception
    {
        Date start = getStartDate(e, payday);
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("d/M/yyyy");

        if (e.getTipo().equals("horista"))
        {
            double salHora = Double.parseDouble(e.getSalario().replace(",", "."));
            double normais = 0, extras = 0;
            for (int k = 0; k < e.getQtdCartoes(); k++)
            {
                Date dt = sdf.parse(e.getDatasCartoes()[k]);
                if (!dt.before(start) && !dt.after(payday))
                {
                    double h = e.getHorasCartoes()[k];
                    if (h > 8) { normais += 8; extras += (h - 8); } else { normais += h; }
                }
            }
            return trunc2(normais * salHora) + trunc2(extras * salHora * 1.5);
        }
        else if (e.getTipo().equals("comissionado"))
        {
            double sal = Double.parseDouble(e.getSalario().replace(",", "."));
            double pCom = Double.parseDouble(e.getComissao().replace(",", "."));
            double fixo = trunc2((sal * 24) / 52.0);
            double vendas = 0;
            for (int k = 0; k < e.getQtdVendas(); k++)
            {
                Date dt = sdf.parse(e.getDatasVendas()[k]);
                if (!dt.before(start) && !dt.after(payday))
                {
                    vendas += e.getValoresVendas()[k];
                }
            }
            double comissao = trunc2(vendas * pCom);
            return fixo + comissao;
        }
        else
        {
            return Double.parseDouble(e.getSalario().replace(",", "."));
        }
    }

    private double calcularDescontoPeriodo(Empregado e, Date payday) throws Exception
    {
        if (!e.getSindicalizado().equals("true")) return 0.0;

        Date start = getStartDate(e, payday);
        int days = 0;
        if (e.getTipo().equals("horista")) days = 7;
        else if (e.getTipo().equals("comissionado")) days = 14;
        else
        {
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime(payday);
            days = cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH);
        }

        double taxaM = Double.parseDouble(e.getTaxaSindical().replace(",", "."));
        double desc = taxaM * days;

        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("d/M/yyyy");
        for (int k = 0; k < e.getQtdTaxas(); k++)
        {
            Date dt = sdf.parse(e.getDatasTaxas()[k]);
            if (!dt.before(start) && !dt.after(payday))
            {
                desc += e.getValoresTaxas()[k];
            }
        }
        return trunc2(desc);
    }

    private double calcularDebitoSindicato(Empregado e, Date targetPayday) throws Exception
    {
        if (!e.getSindicalizado().equals("true")) return 0.0;

        Date currPayday = getFirstPayday(e);
        if (currPayday == null) return 0.0;

        double debt = 0.0;

        while (currPayday.before(targetPayday))
        {
            double bruto = calcularBruto(e, currPayday);
            double desc = calcularDescontoPeriodo(e, currPayday);
            double totalOwed = debt + desc;

            if (bruto >= totalOwed) debt = 0.0;
            else debt = totalOwed - bruto;

            currPayday = getNextPayday(e, currPayday);
        }
        return debt;
    }

    private String formatNum(double num)
    {
        return String.format(java.util.Locale.US, "%.2f", num).replace(".", ",");
    }

    private String formatHoras(double h)
    {
        if (h == (long) h) return String.valueOf((long) h);
        else return String.valueOf(h).replace(".", ",");
    }

    private String formatMetodo(Empregado e)
    {
        if (e.getMetodoPagamento().equals("emMaos")) return "Em maos";
        if (e.getMetodoPagamento().equals("correios")) return "Correios, " + e.getEndereco();
        if (e.getMetodoPagamento().equals("banco"))
        {
            return e.getBanco() + ", Ag. " + e.getAgencia() + " CC " + e.getContaCorrente();
        }
        return "";
    }

    private void sortEmpregados(Empregado[] arr, int count)
    {
        for (int i = 0; i < count - 1; i++)
        {
            for (int j = 0; j < count - i - 1; j++)
            {
                if (arr[j].getNome().compareTo(arr[j+1].getNome()) > 0)
                {
                    Empregado temp = arr[j];
                    arr[j] = arr[j+1];
                    arr[j+1] = temp;
                }
            }
        }
    }

    public String totalFolha(String data) throws Exception
    {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("d/M/yyyy");
        sdf.setLenient(false);
        Date payday = sdf.parse(data);

        double totalGeral = 0;
        for (int i = 1; i < proximoId; i++)
        {
            if (empregados[i] != null && isPayday(empregados[i], payday))
            {
                totalGeral += calcularBruto(empregados[i], payday);
            }
        }
        return formatNum(totalGeral);
    }

    public void rodaFolha(String data, String saida) throws Exception
    {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("d/M/yyyy");
        sdf.setLenient(false);
        Date payday = sdf.parse(data);

        Empregado[] horistas = new Empregado[100];
        Empregado[] assalariados = new Empregado[100];
        Empregado[] comissionados = new Empregado[100];
        int hCount = 0, aCount = 0, cCount = 0;
        double totalGeral = 0;

        for (int i = 1; i < proximoId; i++)
        {
            if (empregados[i] != null && isPayday(empregados[i], payday))
            {
                if (empregados[i].getTipo().equals("horista")) horistas[hCount++] = empregados[i];
                else if (empregados[i].getTipo().equals("assalariado")) assalariados[aCount++] = empregados[i];
                else if (empregados[i].getTipo().equals("comissionado")) comissionados[cCount++] = empregados[i];
            }
        }

        sortEmpregados(horistas, hCount);
        sortEmpregados(assalariados, aCount);
        sortEmpregados(comissionados, cCount);

        StringBuilder sb = new StringBuilder();
        java.text.SimpleDateFormat outSdf = new java.text.SimpleDateFormat("yyyy-MM-dd");

        sb.append("FOLHA DE PAGAMENTO DO DIA ").append(outSdf.format(payday)).append("\n");
        sb.append("====================================\n\n");

        sb.append("===============================================================================================================================\n");
        sb.append("===================== HORISTAS ================================================================================================\n");
        sb.append("===============================================================================================================================\n");
        sb.append("Nome                                 Horas Extra Salario Bruto Descontos Salario Liquido Metodo\n");
        sb.append("==================================== ===== ===== ============= ========= =============== ======================================\n");

        double tbH = 0, tdH = 0, tlH = 0;
        double thH = 0, teH = 0;

        for (int i = 0; i < hCount; i++)
        {
            Empregado e = horistas[i];
            Date start = getStartDate(e, payday);

            double normais = 0, extras = 0;
            java.text.SimpleDateFormat sdfBR = new java.text.SimpleDateFormat("d/M/yyyy");
            for (int k = 0; k < e.getQtdCartoes(); k++)
            {
                Date dt = sdfBR.parse(e.getDatasCartoes()[k]);
                if (!dt.before(start) && !dt.after(payday))
                {
                    double h = e.getHorasCartoes()[k];
                    if (h > 8) { normais += 8; extras += (h - 8); } else { normais += h; }
                }
            }

            double bruto = calcularBruto(e, payday);
            double descAtual = calcularDescontoPeriodo(e, payday);
            double debitoPassado = calcularDebitoSindicato(e, payday);

            double descTotal = trunc2(descAtual + debitoPassado);
            double descReportado = Math.min(bruto, descTotal);
            double liq = bruto - descReportado;

            tbH += bruto; tdH += descReportado; tlH += liq;
            thH += normais; teH += extras;
            totalGeral += bruto;

            String linha = String.format(java.util.Locale.US, "%-36s %5s %5s %13s %9s %15s %s\n",
                    e.getNome(), formatHoras(normais), formatHoras(extras), formatNum(bruto), formatNum(descReportado), formatNum(liq), formatMetodo(e));
            sb.append(linha);
        }
        sb.append("\n");
        sb.append(String.format(java.util.Locale.US, "%-36s %5s %5s %13s %9s %15s\n", "TOTAL HORISTAS", formatHoras(thH), formatHoras(teH), formatNum(tbH), formatNum(tdH), formatNum(tlH)));
        sb.append("\n");

        sb.append("===============================================================================================================================\n");
        sb.append("===================== ASSALARIADOS ============================================================================================\n");
        sb.append("===============================================================================================================================\n");
        sb.append("Nome                                             Salario Bruto Descontos Salario Liquido Metodo\n");
        sb.append("================================================ ============= ========= =============== ======================================\n");

        double tbA = 0, tdA = 0, tlA = 0;

        for (int i = 0; i < aCount; i++)
        {
            Empregado e = assalariados[i];

            double bruto = calcularBruto(e, payday);
            double descAtual = calcularDescontoPeriodo(e, payday);
            double debitoPassado = calcularDebitoSindicato(e, payday);

            double descTotal = trunc2(descAtual + debitoPassado);
            double descReportado = Math.min(bruto, descTotal);
            double liq = bruto - descReportado;

            tbA += bruto; tdA += descReportado; tlA += liq;
            totalGeral += bruto;

            String linha = String.format(java.util.Locale.US, "%-48s %13s %9s %15s %s\n",
                    e.getNome(), formatNum(bruto), formatNum(descReportado), formatNum(liq), formatMetodo(e));
            sb.append(linha);
        }
        sb.append("\n");
        sb.append(String.format(java.util.Locale.US, "%-48s %13s %9s %15s\n", "TOTAL ASSALARIADOS", formatNum(tbA), formatNum(tdA), formatNum(tlA)));
        sb.append("\n");

        sb.append("===============================================================================================================================\n");
        sb.append("===================== COMISSIONADOS ===========================================================================================\n");
        sb.append("===============================================================================================================================\n");
        sb.append("Nome                  Fixo     Vendas   Comissao Salario Bruto Descontos Salario Liquido Metodo\n");
        sb.append("===================== ======== ======== ======== ============= ========= =============== ======================================\n");

        double tbC = 0, tdC = 0, tlC = 0, tfC = 0, tvC = 0, tcC = 0;

        for (int i = 0; i < cCount; i++)
        {
            Empregado e = comissionados[i];
            Date start = getStartDate(e, payday);

            double sal = Double.parseDouble(e.getSalario().replace(",", "."));
            double pCom = Double.parseDouble(e.getComissao().replace(",", "."));
            double fixo = trunc2((sal * 24) / 52.0);

            double vendas = 0;
            java.text.SimpleDateFormat sdfBR = new java.text.SimpleDateFormat("d/M/yyyy");
            for (int k = 0; k < e.getQtdVendas(); k++)
            {
                Date dt = sdfBR.parse(e.getDatasVendas()[k]);
                if (!dt.before(start) && !dt.after(payday))
                {
                    vendas += e.getValoresVendas()[k];
                }
            }
            double comissao = trunc2(vendas * pCom);

            double bruto = calcularBruto(e, payday);
            double descAtual = calcularDescontoPeriodo(e, payday);
            double debitoPassado = calcularDebitoSindicato(e, payday);

            double descTotal = trunc2(descAtual + debitoPassado);
            double descReportado = Math.min(bruto, descTotal);
            double liq = bruto - descReportado;

            tfC += fixo; tvC += vendas; tcC += comissao;
            tbC += bruto; tdC += descReportado; tlC += liq;
            totalGeral += bruto;

            String linha = String.format(java.util.Locale.US, "%-21s %8s %8s %8s %13s %9s %15s %s\n",
                    e.getNome(), formatNum(fixo), formatNum(vendas), formatNum(comissao), formatNum(bruto), formatNum(descReportado), formatNum(liq), formatMetodo(e));
            sb.append(linha);
        }
        sb.append("\n");
        sb.append(String.format(java.util.Locale.US, "%-21s %8s %8s %8s %13s %9s %15s\n", "TOTAL COMISSIONADOS", formatNum(tfC), formatNum(tvC), formatNum(tcC), formatNum(tbC), formatNum(tdC), formatNum(tlC)));
        sb.append("\n");

        sb.append("TOTAL FOLHA: ").append(formatNum(totalGeral)).append("\n");

        java.io.PrintWriter writer = new java.io.PrintWriter(saida, "UTF-8");
        writer.print(sb.toString());
        writer.close();
        saveState();
    }

}