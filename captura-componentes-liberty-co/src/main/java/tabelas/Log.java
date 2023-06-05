/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tabelas;

import java.sql.Date;
import java.time.LocalDateTime;

/**
 *
 * @author mari
 */
public class Log {
    private Integer idLog;
    private String momentoCaptura;
    private Double emUso;
    private Integer fkComponente;
    private Integer fkMaquina;
    
    public Log(Integer idLog, String momentoCaptura, Double emUso, Integer fkComponente, Integer fkMaquina) {
        this.idLog = idLog;
        this.momentoCaptura = momentoCaptura;
        this.emUso = emUso;
        this.fkComponente = fkComponente;
        this.fkMaquina = fkMaquina;
    }

    public Integer getIdLog() {
        return idLog;
    }

    public void setIdLog(Integer idLog) {
        this.idLog = idLog;
    }

    public String getMomentoCaptura() {
        return momentoCaptura;
    }

    public void setMomentoCaptura(String momentoCaptura) {
        this.momentoCaptura = momentoCaptura;
    }

    public Double getEmUso() {
        return emUso;
    }

    public void setEmUso(Double emUso) {
        this.emUso = emUso;
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
        return String.format ("""
                             
                             ------------------------------
                             Log                 
                             ------------------------------
                             idLog: %d
                             momentoCaptura: %s
                             emUso: %.2f
                             fkComponente: %d
                             fkMaquina: %d
                             """,
                idLog, momentoCaptura, emUso, fkComponente, fkMaquina
        );
    }
    
}
