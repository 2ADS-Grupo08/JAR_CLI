/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package telas;

import captura.Conexao;
import com.github.britooo.looca.api.core.Looca;
import com.github.britooo.looca.api.group.discos.Disco;
import com.github.britooo.looca.api.group.discos.DiscoGrupo;
import com.github.britooo.looca.api.group.discos.Volume;
import com.github.britooo.looca.api.util.Conversor;
import encerrar.janelas.inovacao.EncerraJanelas;
import inserts.Insercao;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import tabelas.Componente;
import tabelas.Janela;
import tabelas.Maquina;
import tabelas.NivelAlerta;

/**
 *
 * @author leo
 */
public class TelaCapturaCLI {
    private static JdbcTemplate jdbcAzure;
    private static JdbcTemplate jdbcMysql;
    private static Looca looca;

    /**
     * Creates new form TelaCaptura
     */
    public TelaCapturaCLI() {

        //Iniciando uma nova conexão
        Conexao conexaoAzure = new Conexao("azure");
        Conexao conexaoMysql = new Conexao("mysql");
        jdbcAzure = conexaoAzure.getConnection();
        jdbcMysql = conexaoMysql.getConnection();

        //Conectando com o Looca
        looca = new Looca();

    }

    private Double calcularPorcentagemCpu() {
        Double porcenUsoCpu = looca.getProcessador().getUso();

        return porcenUsoCpu;
    }

    private Double calcularPorcentagemRam() {
        Double totalRam = looca.getMemoria().getTotal() / Math.pow(10, 9);

        Double usoRam = looca.getMemoria().getEmUso() / Math.pow(10, 9);

        Double porcenUsoRam = 100 * usoRam / totalRam;

        return porcenUsoRam;
    }

    private void validarComponenteEmNivelAlerta(List<Componente> componentes) {
        Integer idComponenteCpu = 0;
        Integer idMaquinaCpu = 0;
        Integer idComponenteRam = 0;
        Integer idMaquinaRam = 0;

        for (Componente c : componentes) {
            if (c.getNomeComponente().equals("Processador")) {
                idComponenteCpu = c.getIdComponente();
                idMaquinaCpu = c.getFkMaquina();
            }
            if (c.getNomeComponente().equals("Memória RAM")) {
                idComponenteRam = c.getIdComponente();
                idMaquinaRam = c.getFkMaquina();
            }
        }
        validarLogComNivelAlerta(idComponenteCpu, idMaquinaCpu, idComponenteRam, idMaquinaRam);
    }

    private void validarLogComNivelAlerta(Integer idComponenteCpu, Integer idMaquinaCpu, Integer idComponenteRam, Integer idMaquinaRam) {
        Double porcenUsoCpu = calcularPorcentagemCpu();
        Double porcenUsoRam = calcularPorcentagemRam();

        NivelAlerta nivelAlerta = new NivelAlerta();

        List<NivelAlerta> niveisAlerta = jdbcAzure.query("SELECT * FROM NivelAlerta",
                new BeanPropertyRowMapper(NivelAlerta.class));

        for (NivelAlerta n : niveisAlerta) {
            if (n.getFkComponente().equals(idComponenteCpu) && n.getFkMaquina().equals(idMaquinaCpu)) {
                if (porcenUsoCpu > n.getNivelAlerta()) {
                    encontrarJanelas(n.getFkMaquina());
                     String mensagem = String.format("A CPU ultrapassou o limite de %.2f%%", n.getNivelAlerta());
                    try {
                        slack.metodos.Slack.alertaSlack(mensagem);
                    } catch (IOException ex) {
                        Logger.getLogger(TelaCapturaCLI.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(TelaCapturaCLI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            if (n.getFkComponente().equals(idComponenteRam) && n.getFkMaquina().equals(idMaquinaRam)) {
                if (porcenUsoRam > n.getNivelAlerta()) {
                    encontrarJanelas(n.getFkMaquina());
                    String mensagem = String.format("A RAM ultrapassou o limite de %.2f%%", n.getNivelAlerta());
                    try {
                        slack.metodos.Slack.alertaSlack(mensagem);
                    } catch (IOException ex) {
                        Logger.getLogger(TelaCapturaCLI.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(TelaCapturaCLI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            }
        }
    }

    private void encontrarJanelas(Integer fkMaquina) {
        Janela janelas = new Janela();
        List<Long> pids = new ArrayList();
        List<String> listaNomeJanelas = new ArrayList();

        List<Janela> listaJanelasBanco = jdbcAzure.query("SELECT * FROM Janela WHERE fkMaquina = ?",
                new BeanPropertyRowMapper(Janela.class), fkMaquina);

        for (Janela j : listaJanelasBanco) {
            for (int i = 0; i < looca.getGrupoDeJanelas().getJanelasVisiveis().size(); i++) {
                String tituloJanelaLooca = looca.getGrupoDeJanelas().getJanelasVisiveis().get(i).getTitulo();
                System.out.println(tituloJanelaLooca);
                if (tituloJanelaLooca.contains(j.getNomeJanela())) {
                    pids.add(looca.getGrupoDeJanelas().getJanelasVisiveis().get(i).getPid());
                    listaNomeJanelas.add(j.getNomeJanela());
                }
            }
        }
        encerrarJanelas(pids, listaNomeJanelas, fkMaquina);
    }
    
    private void encerrarJanelas(List<Long> pids, List<String> listaNomeJanelas, Integer fkMaquina) {
        for (int i = 0; i < listaNomeJanelas.size(); i++) {
            try {
                EncerraJanelas.terminalLinux(pids.get(i) + "");
                Insercao.inserirDadosJanelaEncerrada(Integer.valueOf(pids.get(i) + ""), listaNomeJanelas.get(i), jdbcAzure, jdbcMysql, fkMaquina);
            } catch (IOException ex) {
                Logger.getLogger(TelaCapturaCLI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @SuppressWarnings("unchecked")

    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
//                TelaCaptura tela = new TelaCaptura();

//                tela.setVisible(true);

                String hostNameMaquina = looca.getRede().getParametros().getHostName();

                List<Componente> getFkMaquina = new ArrayList();
                List<Maquina> maquinas = new ArrayList();
                List<Componente> componentes = new ArrayList();

                maquinas = jdbcAzure.query("SELECT * FROM Maquina WHERE hostName = ?;",
                        new BeanPropertyRowMapper(Maquina.class), hostNameMaquina);

                getFkMaquina = jdbcAzure.query("SELECT fkMaquina FROM Componente;", new BeanPropertyRowMapper(Componente.class));

                // Se a lista de máquinas não estiver nula, ou seja, se existir uma máquina com o hostName correspondente:
                if (maquinas != null) {
                    // Pegando o ID da máquina atual, que está rodando o JAR
                    Integer idMaquina = maquinas.get(0).getIdMaquina();
                    // Validação se existe componente dessa máquina inserido
                    Boolean existeComponente = false;
                    // Passa por todos os componentes para verificar a FK da máquina
                    for (Componente c : getFkMaquina) {
                        // Validar se o ID da máquina tem na tabela Componente
                        if (c.getFkMaquina().equals(idMaquina)) {
                            // Se existe, o componente já foi inserido na tabela
                            existeComponente = true;
                        }
                    }
                    // Se não existe, ele insere os componentes na tabela
                    if (existeComponente.equals(false)) {
                        Insercao.inserirDadosComponente(jdbcAzure, jdbcMysql, idMaquina);
                        componentes = jdbcAzure.query("SELECT * FROM Componente WHERE fkMaquina = ?;",
                                new BeanPropertyRowMapper(Componente.class), idMaquina);

                    }
                }

                new Timer().scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {

                        List<Maquina> maquinas = jdbcAzure.query("SELECT * FROM Maquina WHERE hostName = ?;",
                                new BeanPropertyRowMapper(Maquina.class), hostNameMaquina);
                        List<Componente> componentes = jdbcAzure.query("SELECT * FROM Componente WHERE fkMaquina = ?;",
                                new BeanPropertyRowMapper(Componente.class), maquinas.get(0).getIdMaquina());

                        for (Componente c : componentes) {
                            Insercao.inserirDadosLog(jdbcAzure, jdbcMysql, c);
                        }
                        
                        DiscoGrupo grupoDeDiscos = looca.getGrupoDeDiscos();
                        List<Disco> discos = grupoDeDiscos.getDiscos();
                        List<Volume> volumes = grupoDeDiscos.getVolumes();
                        for (int i = 0; i < discos.size(); i++) {
                            if (i == 0) {
                                Volume volume = volumes.get(i);
                                Long emUso = (volume.getTotal() - volume.getDisponivel());
                            }
                        }
                        
                    }
                }, 0, 5000);
            }
        });
}
}
