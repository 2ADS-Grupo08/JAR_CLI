/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tabelas;

/**
 *
 * @author mari
 */
public class Maquina {
    private Integer idMaquina;
    private String hostName;
    private String marcaMaquina;
    private String sistemaOperacional;
    private String nomeDono;
    private String sobrenomeDono;
    private String cargoDono;
    private String status;
    private Integer fkGestor;

    public Maquina(Integer idMaquina, String hostName, String marcaMaquina, String sistemaOperacional, String nomeDono, String sobrenomeDono, String cargoDono, String status, Integer fkGestor) {
        this.idMaquina = idMaquina;
        this.hostName = hostName;
        this.marcaMaquina = marcaMaquina;
        this.sistemaOperacional = sistemaOperacional;
        this.nomeDono = nomeDono;
        this.sobrenomeDono = sobrenomeDono;
        this.cargoDono = cargoDono;
        this.status = status;
        this.fkGestor = fkGestor;
    }
    
    public Maquina () {
        
    }

    public Integer getIdMaquina() {
        return idMaquina;
    }

    public void setIdMaquina(Integer idMaquina) {
        this.idMaquina = idMaquina;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getMarcaMaquina() {
        return marcaMaquina;
    }

    public void setMarcaMaquina(String marcaMaquina) {
        this.marcaMaquina = marcaMaquina;
    }

    public String getSistemaOperacional() {
        return sistemaOperacional;
    }

    public void setSistemaOperacional(String sistemaOperacional) {
        this.sistemaOperacional = sistemaOperacional;
    }

    public String getNomeDono() {
        return nomeDono;
    }

    public void setNomeDono(String nomeDono) {
        this.nomeDono = nomeDono;
    }

    public String getSobrenomeDono() {
        return sobrenomeDono;
    }

    public void setSobrenomeDono(String sobrenomeDono) {
        this.sobrenomeDono = sobrenomeDono;
    }

    public String getCargoDono() {
        return cargoDono;
    }

    public void setCargoDono(String cargoDono) {
        this.cargoDono = cargoDono;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getFkGestor() {
        return fkGestor;
    }

    public void setFkGestor(Integer fkGestor) {
        this.fkGestor = fkGestor;
    }

    @Override
    public String toString() {
        return String.format("""
                             
                             ------------------------------
                             Maquina                 
                             ------------------------------
                             idMaquina: %d
                             hostName: %s
                             marcaMaquina: %s
                             sistemaOperacional: %s
                             nomeDono: %s
                             sobrenomeDono: %s
                             cargoDono: %s
                             status: %s
                             fkGestor: %d
                             """,
                idMaquina, hostName, marcaMaquina, sistemaOperacional, nomeDono, sobrenomeDono, cargoDono, status, fkGestor
                );
    }
    
}
