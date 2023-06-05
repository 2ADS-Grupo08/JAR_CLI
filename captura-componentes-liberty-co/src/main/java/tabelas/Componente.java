/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tabelas;

/**
 *
 * @author mari
 */
public class Componente {
    private Integer idComponente;
    private String nomeComponente;
    private Double total;
    private String modelo;
    private Integer fkMaquina;

    public Componente(Integer idComponente, String nomeComponente, Double total, String modelo, Integer fkMaquina) {
        this.idComponente = idComponente;
        this.nomeComponente = nomeComponente;
        this.total = total;
        this.modelo = modelo;
        this.fkMaquina = fkMaquina;
    }
    
    public Componente() {
        
    }

    public Integer getIdComponente() {
        return idComponente;
    }

    public void setIdComponente(Integer idComponente) {
        this.idComponente = idComponente;
    }

    public String getNomeComponente() {
        return nomeComponente;
    }

    public void setNomeComponente(String nomeComponente) {
        this.nomeComponente = nomeComponente;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
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
                             Componente                 
                             ------------------------------
                             idComponente: %d
                             nomeComponente: %s
                             total: %.2f
                             modelo: %s
                             fkMaquina: %d
                             """,
                idComponente, nomeComponente, total, modelo, fkMaquina
        );
    }
    
    
}
