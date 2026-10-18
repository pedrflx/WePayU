package br.ufal.ic.p2.wepayu.sistema;

import br.ufal.ic.p2.wepayu.Exception.*;
import br.ufal.ic.p2.wepayu.models.empregados.Empregado;
import br.ufal.ic.p2.wepayu.models.empregados.EmpregadoAssalariado;
import br.ufal.ic.p2.wepayu.models.empregados.EmpregadoComissionado;
import br.ufal.ic.p2.wepayu.models.empregados.EmpregadoHorista;

// FabricaEmpregado: transforma o texto do tipo ("horista", "assalariado", "comissionado") no objeto certo
// é o único lugar do sistema que decide qual classe criar a partir de um texto

public class FabricaEmpregado
{

    // cria horista e assalariado (comissionado precisa da versão com comissão)
    public static Empregado criar(String nome, String endereco, String tipo, String salario) throws WePayUException
    {
        if (tipo.equals("horista")) return new EmpregadoHorista(nome, endereco, salario);
        if (tipo.equals("assalariado")) return new EmpregadoAssalariado(nome, endereco, salario);
        if (tipo.equals("comissionado")) throw new TipoNaoAplicavelException();
        throw new TipoInvalidoException();
    }

    // só comissionado recebe comissão
    public static Empregado criar(String nome, String endereco, String tipo, String salario, String comissao) throws WePayUException
    {
        if (tipo.equals("comissionado")) return new EmpregadoComissionado(nome, endereco, salario, comissao);
        if (tipo.equals("horista") || tipo.equals("assalariado")) throw new TipoNaoAplicavelException();
        throw new TipoInvalidoException();
    }

    // troca o tipo copiando os dados comuns (comissionado não pode, porque ficaria sem comissão)
    public static Empregado converterTipo(Empregado empregado, String tipo) throws WePayUException
    {
        if (tipo.equals("horista")) return new EmpregadoHorista(empregado);
        if (tipo.equals("assalariado")) return new EmpregadoAssalariado(empregado);
        if (tipo.equals("comissionado")) throw new ComissaoNulaException();
        throw new TipoInvalidoException();
    }

    // troca o tipo e já define o valor extra: a comissão do comissionado ou o salário novo dos outros
    public static Empregado converterTipo(Empregado empregado, String tipo, String valorExtra) throws WePayUException
    {
        if (tipo.equals("comissionado"))
        {
            EmpregadoComissionado novo = new EmpregadoComissionado(empregado);
            novo.setComissao(valorExtra);
            return novo;
        }

        Empregado novo = converterTipo(empregado, tipo);
        novo.setSalario(valorExtra);
        return novo;
    }
}