package br.ufal.ic.p2.wepayu.persistencia;

import br.ufal.ic.p2.wepayu.Exception.ErroPersistenciaException;
import br.ufal.ic.p2.wepayu.Exception.WePayUException;
import br.ufal.ic.p2.wepayu.models.Empregado;
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
*/

public class PersistenciaXML
{

    private String arquivo;

    public PersistenciaXML(String arquivo)
    {
        this.arquivo = arquivo;
    }

    public void salvar(ArrayList<Empregado> empregados) throws WePayUException
    {
        try
        {
            XMLEncoder encoder = new XMLEncoder(new FileOutputStream(arquivo));
            encoder.writeObject(empregados.toArray(new Empregado[0]));
            encoder.close();
        }
        catch (FileNotFoundException e)
        {
            throw new ErroPersistenciaException();
        }
    }

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
            Empregado[] lidos = (Empregado[]) decoder.readObject();
            decoder.close();

            for (Empregado empregado : lidos)
            {
                empregados.add(empregado);
            }
            return empregados;
        }
        catch (FileNotFoundException e)
        {
            throw new ErroPersistenciaException();
        }
    }
}
