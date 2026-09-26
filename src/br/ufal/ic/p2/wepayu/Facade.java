package br.ufal.ic.p2.wepayu;

import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoExisteException;
import br.ufal.ic.p2.wepayu.models.Empregado;

public class Facade
{
    Empregado[] empregados = new Empregado[100];
    int proximoId = 1;

    public void zerarSistema()
    {
        empregados = new Empregado[100];
        proximoId = 1;
    }

    public void encerrarSistema()
    {

    }

    public String criarEmpregado(String nome, String endereco, String tipo, String salario) throws Exception
    {
        if (nome == null || nome.isEmpty()) throw new Exception("Nome nao pode ser nulo.");
        if (endereco == null || endereco.isEmpty()) throw new Exception("Endereco nao pode ser nulo.");
        if (tipo.equals("comissionado")) throw new Exception("Tipo nao aplicavel.");
        if (!tipo.equals("horista") && !tipo.equals("assalariado")) throw new Exception("Tipo invalido.");
        if (salario == null || salario.isEmpty()) throw new Exception("Salario nao pode ser nulo.");

        try
        {
            double salValor = Double.parseDouble(salario.replace(",", "."));
            if (salValor < 0) throw new Exception("Salario deve ser nao-negativo.");
        }
        catch (NumberFormatException e)
        {
            throw new Exception("Salario deve ser numerico.");
        }

        empregados[proximoId] = new Empregado(nome, endereco, tipo, salario);

        String idGerado = String.valueOf(proximoId);
        proximoId++;

        return idGerado;
    }

    public String criarEmpregado(String nome, String endereco, String tipo, String salario, String comissao) throws Exception
    {
        if (nome == null || nome.isEmpty()) throw new Exception("Nome nao pode ser nulo.");
        if (endereco == null || endereco.isEmpty()) throw new Exception("Endereco nao pode ser nulo.");
        if (tipo.equals("horista") || tipo.equals("assalariado")) throw new Exception("Tipo nao aplicavel.");
        if (!tipo.equals("comissionado")) throw new Exception("Tipo invalido.");
        if (salario == null || salario.isEmpty()) throw new Exception("Salario nao pode ser nulo.");

        try
        {
            double salValor = Double.parseDouble(salario.replace(",", "."));
            if (salValor < 0) throw new Exception("Salario deve ser nao-negativo.");
        }
        catch (NumberFormatException e)
        {
            throw new Exception("Salario deve ser numerico.");
        }

        if (comissao == null || comissao.isEmpty()) throw new Exception("Comissao nao pode ser nula.");

        try
        {
            double comValor = Double.parseDouble(comissao.replace(",", "."));
            if (comValor < 0) throw new Exception("Comissao deve ser nao-negativa.");
        }
        catch (NumberFormatException e)
        {
            throw new Exception("Comissao deve ser numerica.");
        }

        empregados[proximoId] = new Empregado(nome, endereco, tipo, salario, comissao);

        String idGerado = String.valueOf(proximoId);
        proximoId++;

        return idGerado;
    }

    public void removerEmpregado(String emp) throws Exception
    {

        if (emp == null || emp.isEmpty())
        {
            throw new Exception("Identificacao do empregado nao pode ser nula.");
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
    }

    public String getAtributoEmpregado(String emp, String atributo) throws Exception
    {
        if (emp == null || emp.isEmpty())
        {
            throw new Exception("Identificacao do empregado nao pode ser nula.");
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
            return "false";
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
            return empregadoTemporario.getComissao();
        }

        throw new Exception("Atributo nao existe.");
    }


    public void lancaCartao(String emp, String data, String horas) throws Exception
    {

        if (emp == null || emp.isEmpty()) throw new Exception("Identificacao do empregado nao pode ser nula.");

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
            throw new Exception("Empregado nao eh horista.");
        }

        try
        {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("d/M/yyyy");
            sdf.setLenient(false);
            sdf.parse(data);
        }
        catch (Exception e)
        {
            throw new Exception("Data invalida.");
        }

        double horasTrabalhadas;

        try
        {
            horasTrabalhadas = Double.parseDouble(horas.replace(",", "."));
            if (horasTrabalhadas <= 0) throw new Exception("Horas devem ser positivas.");
        }
        catch (Exception e)
        {
            throw new Exception("Horas devem ser positivas.");
        }

        empregadoTemporario.registrarCartao(data, horasTrabalhadas);

    }

    public String getHorasNormaisTrabalhadas(String emp, String dataInicial, String dataFinal) throws Exception
    {
        if (emp == null || emp.isEmpty()) throw new Exception("Identificacao do empregado nao pode ser nula.");
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
            throw new Exception("Empregado nao eh horista.");
        }

        java.util.Date dataIni;

        try
        {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("d/M/yyyy");
            sdf.setLenient(false);
            dataIni = sdf.parse(dataInicial);
        }
        catch (Exception e)
        {
            throw new Exception("Data inicial invalida.");
        }

        java.util.Date dataFim;

        try
        {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("d/M/yyyy");
            sdf.setLenient(false);
            dataFim = sdf.parse(dataFinal);
        }
        catch (Exception e)
        {
            throw new Exception("Data final invalida.");
        }

        if (dataIni.after(dataFim))
        {
            throw new Exception("Data inicial nao pode ser posterior aa data final.");
        }

        double horasNormais = 0;
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("d/M/yyyy");

        for (int i = 0; i < empregadoTemporario.getQtdCartoes(); i++)
        {
            java.util.Date dataCartao = sdf.parse(empregadoTemporario.getDatasCartoes()[i]);

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
        if (emp == null || emp.isEmpty()) throw new Exception("Identificacao do empregado nao pode ser nula.");

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
            throw new Exception("Empregado nao eh horista.");
        }

        java.util.Date dataIni;

        try
        {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("d/M/yyyy");
            sdf.setLenient(false);
            dataIni = sdf.parse(dataInicial);
        }
        catch (Exception e)
        {
            throw new Exception("Data inicial invalida.");
        }

        java.util.Date dataFim;

        try
        {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("d/M/yyyy");
            sdf.setLenient(false);
            dataFim = sdf.parse(dataFinal);
        }
        catch (Exception e)
        {
            throw new Exception("Data final invalida.");
        }

        if (dataIni.after(dataFim))
        {
            throw new Exception("Data inicial nao pode ser posterior aa data final.");
        }

        double horasExtras = 0;
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("d/M/yyyy");

        for (int i = 0; i < empregadoTemporario.getQtdCartoes(); i++)
        {
            java.util.Date dataCartao = sdf.parse(empregadoTemporario.getDatasCartoes()[i]);

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
}