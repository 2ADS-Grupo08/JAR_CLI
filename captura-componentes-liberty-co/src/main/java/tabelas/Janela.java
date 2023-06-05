/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tabelas;

/**
 *
 * @author mari
 */
public class Janela {
    private Integer idJanela;
    private String nomeJanela;
    private Integer fkMaquina;

    public Janela(Integer idJanela, String nomeJanela, Integer fkMaquina) {
        this.idJanela = idJanela;
        this.nomeJanela = nomeJanela;
        this.fkMaquina = fkMaquina;
    }
    
    public Janela() {
        
    }

    public Integer getIdJanela() {
        return idJanela;
    }

    public void setIdJanela(Integer idJanela) {
        this.idJanela = idJanela;
    }

    public String getNomeJanela() {
        return nomeJanela;
    }

    public void setNomeJanela(String nomeJanela) {
        this.nomeJanela = nomeJanela;
    }

    public Integer getFkMaquina() {
        return fkMaquina;
    }

    public void setFkMaquina(Integer fkMaquina) {
        this.fkMaquina = fkMaquina;
    }

    @Override
    public String toString() {
        return String.format("""
                             
                             ------------------------------
                             Janela                 
                             ------------------------------
                             idJanela: %d
                             nomeJanela: %s
                             fkMaquina: %d
                             """,
                idJanela, nomeJanela, fkMaquina
        );
    }
    
}
