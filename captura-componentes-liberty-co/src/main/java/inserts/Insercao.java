/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package inserts;

import com.github.britooo.looca.api.core.Looca;
import com.github.britooo.looca.api.group.discos.Disco;
import com.github.britooo.looca.api.group.discos.DiscoGrupo;
import com.github.britooo.looca.api.group.discos.Volume;
import com.github.britooo.looca.api.group.memoria.Memoria;
import com.github.britooo.looca.api.group.processador.Processador;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import tabelas.Componente;

/**
 *
 * @author mari
 */
public class Insercao {

    //O objetivo deste método é inserir no banco os dados da tabela componente
    public static void inserirDadosComponente(JdbcTemplate conexaoAzure, JdbcTemplate conexaoMysql, Integer fkMaquina) {
        //CRIA A CONEXÃO COM O LOOCA
        Looca looca = new Looca();

        //Acessando as classes
        Processador cpu = looca.getProcessador();
        Memoria memoria = looca.getMemoria();
        DiscoGrupo grupoDeDiscos = looca.getGrupoDeDiscos();
        List<Disco> discos = grupoDeDiscos.getDiscos();

        //Inserindo o Disco na tabela Componente
        for (int i = 0; i < discos.size(); i++) {
            Disco disco = discos.get(i);
            Double totalDisco = disco.getTamanho() / Math.pow(10, 9);

            if(looca.getSistema().getFabricante().contains("Linux")) {
                //INSERT DO DISCO AZURE
                conexaoAzure.update("INSERT INTO Componente (nomeComponente, total, modelo, fkMaquina) VALUES (?, ?, ?, ?);",
                        "Disco Rígido", totalDisco, disco.getModelo(), fkMaquina);
                //INSERT DO DISCO DOCKER
                conexaoMysql.update("INSERT INTO Componente (nomeComponente, total, modelo, fkMaquina) VALUES (?, ?, ?, ?);",
                        "Disco Rígido", totalDisco, disco.getModelo(), fkMaquina);
            } else {
                //INSERT DO DISCO AZURE
                conexaoAzure.update("INSERT INTO Componente (nomeComponente, total, modelo, fkMaquina) VALUES (?, ?, ?, ?);",
                        "Disco Rígido", totalDisco, disco.getModelo(), fkMaquina);
            }
        }

        //Inserindo o CPU na tabela Componente
        String modeloCpu = cpu.getNome();
        Double totalCpu = 100.0;
        
        if(looca.getSistema().getFabricante().contains("Linux")) {
            //INSERT DA CPU AZURE
            conexaoAzure.update("INSERT INTO Componente (nomeComponente, total, modelo, fkMaquina) VALUES (?, ?, ?, ?);",
                    "Processador", totalCpu, modeloCpu, fkMaquina);
            //INSERT DA CPU DOCKER
            conexaoMysql.update("INSERT INTO Componente (nomeComponente, total, modelo, fkMaquina) VALUES (?, ?, ?, ?);",
                    "Processador", totalCpu, modeloCpu, fkMaquina);
        } else {
            //INSERT DA CPU AZURE
            conexaoAzure.update("INSERT INTO Componente (nomeComponente, total, modelo, fkMaquina) VALUES (?, ?, ?, ?);",
                    "Processador", totalCpu, modeloCpu, fkMaquina);
        }

        //Inserindo a RAM na tabela Componente
        Double totalRam = memoria.getTotal() / Math.pow(10, 9);
        
        if(looca.getSistema().getFabricante().contains("Linux")) {
            //INSERT DA CPU AZURE
            conexaoAzure.update("INSERT INTO Componente (nomeComponente, total, modelo, fkMaquina) VALUES (?, ?, ?, ?);",
                    "Memória RAM", totalRam, null, fkMaquina);
            //INSERT DA CPU DOCKER
            conexaoMysql.update("INSERT INTO Componente (nomeComponente, total, modelo, fkMaquina) VALUES (?, ?, ?, ?);",
                    "Memória RAM", totalRam, null, fkMaquina);
        } else {
            //INSERT DA CPU AZURE
            conexaoAzure.update("INSERT INTO Componente (nomeComponente, total, modelo, fkMaquina) VALUES (?, ?, ?, ?);",
                    "Memória RAM", totalRam, null, fkMaquina);
        }

    }

    //O objetivo deste método é inserir no banco os dados da tabela log
    public static void inserirDadosLog(JdbcTemplate conexaoAzure, JdbcTemplate conexaoMysql, Componente componente) {
        LocalDateTime dataHoraAtual = LocalDateTime.now();
        //CRIA A CONEXÃO COM O LOOCA
        Looca looca = new Looca();

        //Acessando as classes
        Processador cpu = looca.getProcessador();
        Memoria memoria = looca.getMemoria();
        DiscoGrupo grupoDeDiscos = looca.getGrupoDeDiscos();
        List<Disco> discos = grupoDeDiscos.getDiscos();
        List<Volume> volumes = grupoDeDiscos.getVolumes();

        //A GENTE VAI INSERIR NA TABELA DE LOG OS DADOS REFERENTE AO COMPONENTE
        if (componente.getNomeComponente().equalsIgnoreCase("Processador")) {
            //Inserindo o emUso da CPU na tabela Log
            Double cpuEmUso = cpu.getUso();

            if(looca.getSistema().getFabricante().contains("Linux")) {
                //INSERT DO DISCO AZURE
                conexaoAzure.update("INSERT INTO Log (momentoCaptura, emUso, fkComponente, fkMaquina) VALUES (?, ?, ?, ?);",
                        dataHoraAtual, cpuEmUso, componente.getIdComponente(), componente.getFkMaquina());
                // INSERT DA CPU DOCKER
                conexaoMysql.update("INSERT INTO Log (momentoCaptura, emUso, fkComponente, fkMaquina) VALUES (?, ?, ?, null);",
                        dataHoraAtual, cpuEmUso, componente.getIdComponente());
            } else {
                //INSERT DO DISCO AZURE
                conexaoAzure.update("INSERT INTO Log (momentoCaptura, emUso, fkComponente, fkMaquina) VALUES (?, ?, ?, ?);",
                        dataHoraAtual, cpuEmUso, componente.getIdComponente(), componente.getFkMaquina());
            }
        } else if (componente.getNomeComponente().equalsIgnoreCase("Memória RAM")) {
            //Inserindo o emUso da RAM na tabela Log
            Double ramEmUso = memoria.getEmUso() / Math.pow(10, 9);
            
            if(looca.getSistema().getFabricante().contains("Linux")) {
                //INSERT DO DISCO AZURE
                conexaoAzure.update("INSERT INTO Log (momentoCaptura, emUso, fkComponente, fkMaquina) VALUES (?, ?, ?, ?);",
                        dataHoraAtual, ramEmUso, componente.getIdComponente(), componente.getFkMaquina());
                //INSERT DA RAM DOCKER
                conexaoMysql.update("INSERT INTO Log (momentoCaptura, emUso, fkComponente, fkMaquina) VALUES (?, ?, ?, null);",
                        dataHoraAtual, ramEmUso, componente.getIdComponente());
            } else {
                //INSERT DO DISCO AZURE
                conexaoAzure.update("INSERT INTO Log (momentoCaptura, emUso, fkComponente, fkMaquina) VALUES (?, ?, ?, ?);",
                        dataHoraAtual, ramEmUso, componente.getIdComponente(), componente.getFkMaquina());
            }
        } else if (componente.getNomeComponente().equalsIgnoreCase("Disco Rígido")) {
            for (int i = 0; i < discos.size(); i++) {
                if (discos.get(i).getModelo().equalsIgnoreCase(componente.getModelo())) {
                    Volume volume = volumes.get(i);
                    Double discoEmUso = (volume.getTotal() - volume.getDisponivel()) / Math.pow(10, 9);
                    
                    if(looca.getSistema().getFabricante().contains("Linux")) {
                        //INSERT DO DISCO AZURE
                        conexaoAzure.update("INSERT INTO Log (momentoCaptura, emUso, fkComponente, fkMaquina) VALUES (?, ?, ?, ?);",
                                dataHoraAtual, discoEmUso, componente.getIdComponente(), componente.getFkMaquina());
                        //INSERT DO DISCO DOCKER
                        conexaoMysql.update("INSERT INTO Log (momentoCaptura, emUso, fkComponente, fkMaquina) VALUES (?, ?, ?, null);",
                                dataHoraAtual, discoEmUso, componente.getIdComponente());                        
                    } else {
                        //INSERT DO DISCO AZURE
                        conexaoAzure.update("INSERT INTO Log (momentoCaptura, emUso, fkComponente, fkMaquina) VALUES (?, ?, ?, ?);",
                                dataHoraAtual, discoEmUso, componente.getIdComponente(), componente.getFkMaquina());
                    }
                }
            }
        }
        System.out.println(String.format("Dados inseridos para o componente %s", componente.getNomeComponente()));
    }
    
    public static void inserirDadosJanelaEncerrada(Integer pid, String nomeJanela, JdbcTemplate conexaoAzure, JdbcTemplate conexaoMysql, Integer fkMaquina) {
        LocalDateTime dataHoraAtual = LocalDateTime.now();
        Looca looca = new Looca();
        
        if(looca.getSistema().getFabricante().contains("Linux")) {
            conexaoAzure.update("INSERT INTO JanelaEncerrada (pid, nomeJanela, momentoEncerrado, fkMaquina) VALUES (?, ?, ?, ?);",
                    pid, nomeJanela, dataHoraAtual, fkMaquina);
            conexaoMysql.update("INSERT INTO JanelaEncerrada (pid, nomeJanela, momentoEncerrado, fkMaquina) VALUES (?, ?, ?, ?);",
                    pid, nomeJanela, dataHoraAtual, fkMaquina);
        } else {
            conexaoAzure.update("INSERT INTO JanelaEncerrada (pid, nomeJanela, momentoEncerrado, fkMaquina) VALUES (?, ?, ?, ?);",
                    pid, nomeJanela, dataHoraAtual, fkMaquina);
        }
    }
}
