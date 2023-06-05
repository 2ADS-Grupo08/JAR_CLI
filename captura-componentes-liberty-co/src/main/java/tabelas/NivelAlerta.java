/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tabelas;

/**
 *
 * @author mari
 */
public class NivelAlerta {
    private Integer idNivelAlerta;
    private Double nivelAlerta;
    private Integer fkComponente;
    private Integer fkMaquina;

    public NivelAlerta(Integer idNivelAlerta, Double nivelAlerta, Integer fkComponente, Integer fkMaquina) {
        this.idNivelAlerta = idNivelAlerta;
        this.nivelAlerta = nivelAlerta;
        this.fkComponente = fkComponente;
        this.fkMaquina = fkMaquina;
    }

    public NivelAlerta() {
    }

    public Integer getIdNivelAlerta() {
        return idNivelAlerta;
    }

    public void setIdNivelAlerta(Integer idNivelAlerta) {
        this.idNivelAlerta = idNivelAlerta;
    }

    public Double getNivelAlerta() {
        return nivelAlerta;
    }

    public void setNivelAlerta(Double nivelAlerta) {
        this.nivelAlerta = nivelAlerta;
    }
    
    public Integer getFkComponente() {
        return fkComponente;
    }

    public void setFkComponente(Integer fkComponente) {
        this.fkComponente = fkComponente;
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
                             NivelAlerta                 
                             ------------------------------
                             idNivelAlerta: %d
                             nivelAlerta: %.2f
                             fkComponente: %d
                             fkMaquina: %d
                             """,
                idNivelAlerta, nivelAlerta, fkComponente, fkMaquina
        );
    }
    
    
}
