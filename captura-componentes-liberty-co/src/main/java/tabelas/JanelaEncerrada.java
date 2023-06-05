/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tabelas;

public class JanelaEncerrada {
    private Integer idJanelaEncerrada;
    private Integer pid;
    private String nomeJanela;
    private String momentoEncerrado;
    private Integer fkMaquina;

    public JanelaEncerrada(Integer idJanelaEncerrada, Integer pid, String nomeJanela, String momentoEncerrado, Integer fkMaquina) {
        this.idJanelaEncerrada = idJanelaEncerrada;
        this.pid = pid;
        this.nomeJanela = nomeJanela;
        this.momentoEncerrado = momentoEncerrado;
        this.fkMaquina = fkMaquina;
    }
    
    public JanelaEncerrada() {
        
    }

    public Integer getIdJanelaEncerrada() {
        return idJanelaEncerrada;
    }

    public void setIdJanelaEncerrada(Integer idJanelaEncerrada) {
        this.idJanelaEncerrada = idJanelaEncerrada;
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public String getNomeJanela() {
        return nomeJanela;
    }

    public void setNomeJanela(String nomeJanela) {
        this.nomeJanela = nomeJanela;
    }

    public String getMomentoEncerrado() {
        return momentoEncerrado;
    }

    public void setMomentoEncerrado(String momentoEncerrado) {
        this.momentoEncerrado = momentoEncerrado;
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
                             JanelaEncerrada                 
                             ------------------------------
                             idJanelaEncerrada: %d
                             pid: %d
                             nomeJanela: %s
                             momentoEncerrado %s
                             fkMaquina: %d
                             """,
                idJanelaEncerrada, pid, nomeJanela, momentoEncerrado, fkMaquina
        );
    }
    
}
