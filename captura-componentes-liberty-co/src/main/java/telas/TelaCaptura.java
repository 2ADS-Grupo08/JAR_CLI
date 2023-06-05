/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.List;
import java.util.TimerTask;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import tabelas.Componente;
import tabelas.Janela;
import tabelas.Maquina;
import tabelas.NivelAlerta;

/**
 *
 * @author mari
 */
public class TelaCaptura extends javax.swing.JFrame {

    private static JdbcTemplate jdbcAzure;
    private static JdbcTemplate jdbcMysql;
    private static Looca looca;

    /**
     * Creates new form TelaCaptura
     */
    public TelaCaptura() {
        initComponents();

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
                     String mensagem = String.format(":warning: A _*CPU*_ ultrapassou o limite de %.2f%% :warning:", n.getNivelAlerta());
                    try {
                        slack.metodos.Slack.alertaSlack(mensagem);
                    } catch (IOException ex) {
                        Logger.getLogger(TelaCaptura.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(TelaCaptura.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            if (n.getFkComponente().equals(idComponenteRam) && n.getFkMaquina().equals(idMaquinaRam)) {
                if (porcenUsoRam > n.getNivelAlerta()) {
                    encontrarJanelas(n.getFkMaquina());
                    String mensagem = String.format(":warning: A _*RAM*_ ultrapassou o limite de %.2f%% :warning:", n.getNivelAlerta());
                    try {
                        slack.metodos.Slack.alertaSlack(mensagem);
                    } catch (IOException ex) {
                        Logger.getLogger(TelaCaptura.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(TelaCaptura.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(TelaCaptura.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        gradiente1 = new telas.Gradiente();
        p_fundo = new javax.swing.JPanel();
        lb_nome_empresa = new javax.swing.JLabel();
        lb_status = new javax.swing.JLabel();
        lb_titulo_status = new javax.swing.JLabel();
        lb_titulo_processador = new javax.swing.JLabel();
        lb_titulo_ram = new javax.swing.JLabel();
        lb_titulo_disco1 = new javax.swing.JLabel();
        lb_processador = new javax.swing.JLabel();
        lb_ram = new javax.swing.JLabel();
        lb_disco1 = new javax.swing.JLabel();
        lb_titulo_disco2 = new javax.swing.JLabel();
        lb_disco2 = new javax.swing.JLabel();
        lb_titulo_disco3 = new javax.swing.JLabel();
        lb_disco3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        gradiente1.setPreferredSize(new java.awt.Dimension(800, 450));

        p_fundo.setBackground(new java.awt.Color(255, 255, 255));
        p_fundo.setEnabled(false);

        lb_nome_empresa.setBackground(new java.awt.Color(29, 71, 106));
        lb_nome_empresa.setFont(new java.awt.Font("Potti Sreeramulu", 1, 36)); // NOI18N
        lb_nome_empresa.setForeground(new java.awt.Color(29, 71, 106));
        lb_nome_empresa.setText("LIBERTY COMPANY");

        lb_status.setBackground(new java.awt.Color(0, 0, 0));
        lb_status.setFont(new java.awt.Font("Liberation Sans", 1, 15)); // NOI18N
        lb_status.setForeground(new java.awt.Color(0, 0, 0));
        lb_status.setText("Erro");

        lb_titulo_status.setBackground(new java.awt.Color(29, 71, 106));
        lb_titulo_status.setFont(new java.awt.Font("Liberation Sans", 1, 15)); // NOI18N
        lb_titulo_status.setForeground(new java.awt.Color(29, 71, 106));
        lb_titulo_status.setText("Status:");

        lb_titulo_processador.setBackground(new java.awt.Color(29, 71, 106));
        lb_titulo_processador.setFont(new java.awt.Font("Liberation Sans", 1, 15)); // NOI18N
        lb_titulo_processador.setForeground(new java.awt.Color(29, 71, 106));
        lb_titulo_processador.setText("Processador:");

        lb_titulo_ram.setBackground(new java.awt.Color(29, 71, 106));
        lb_titulo_ram.setFont(new java.awt.Font("Liberation Sans", 1, 15)); // NOI18N
        lb_titulo_ram.setForeground(new java.awt.Color(29, 71, 106));
        lb_titulo_ram.setText("Memória RAM:");

        lb_titulo_disco1.setBackground(new java.awt.Color(29, 71, 106));
        lb_titulo_disco1.setFont(new java.awt.Font("Liberation Sans", 1, 15)); // NOI18N
        lb_titulo_disco1.setForeground(new java.awt.Color(29, 71, 106));
        lb_titulo_disco1.setText("Disco 1:");

        lb_processador.setBackground(new java.awt.Color(0, 0, 0));
        lb_processador.setForeground(new java.awt.Color(0, 0, 0));
        lb_processador.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lb_processador.setText("erro ao exibir processador");

        lb_ram.setBackground(new java.awt.Color(0, 0, 0));
        lb_ram.setForeground(new java.awt.Color(0, 0, 0));
        lb_ram.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lb_ram.setText("erro ao exibir ram");

        lb_disco1.setBackground(new java.awt.Color(0, 0, 0));
        lb_disco1.setForeground(new java.awt.Color(0, 0, 0));
        lb_disco1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lb_disco1.setText("disco não encontrado");

        lb_titulo_disco2.setBackground(new java.awt.Color(29, 71, 106));
        lb_titulo_disco2.setFont(new java.awt.Font("Liberation Sans", 1, 15)); // NOI18N
        lb_titulo_disco2.setForeground(new java.awt.Color(29, 71, 106));
        lb_titulo_disco2.setText("Disco 2:");

        lb_disco2.setBackground(new java.awt.Color(0, 0, 0));
        lb_disco2.setForeground(new java.awt.Color(0, 0, 0));
        lb_disco2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lb_disco2.setText("disco não encontrado");

        lb_titulo_disco3.setBackground(new java.awt.Color(29, 71, 106));
        lb_titulo_disco3.setFont(new java.awt.Font("Liberation Sans", 1, 15)); // NOI18N
        lb_titulo_disco3.setForeground(new java.awt.Color(29, 71, 106));
        lb_titulo_disco3.setText("Disco 3:");

        lb_disco3.setBackground(new java.awt.Color(0, 0, 0));
        lb_disco3.setForeground(new java.awt.Color(0, 0, 0));
        lb_disco3.setText("disco não encontrado");

        javax.swing.GroupLayout p_fundoLayout = new javax.swing.GroupLayout(p_fundo);
        p_fundo.setLayout(p_fundoLayout);
        p_fundoLayout.setHorizontalGroup(
            p_fundoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(p_fundoLayout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addGroup(p_fundoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(p_fundoLayout.createSequentialGroup()
                        .addComponent(lb_titulo_status)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lb_status))
                    .addComponent(lb_nome_empresa))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(p_fundoLayout.createSequentialGroup()
                .addGap(46, 46, 46)
                .addGroup(p_fundoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lb_titulo_processador)
                    .addComponent(lb_titulo_ram)
                    .addComponent(lb_processador, javax.swing.GroupLayout.PREFERRED_SIZE, 289, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lb_ram, javax.swing.GroupLayout.PREFERRED_SIZE, 289, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 46, Short.MAX_VALUE)
                .addGroup(p_fundoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lb_titulo_disco2)
                    .addComponent(lb_titulo_disco1)
                    .addComponent(lb_titulo_disco3)
                    .addComponent(lb_disco1, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lb_disco2, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lb_disco3, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(64, 64, 64))
        );
        p_fundoLayout.setVerticalGroup(
            p_fundoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(p_fundoLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(lb_nome_empresa)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(p_fundoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lb_status)
                    .addComponent(lb_titulo_status))
                .addGap(47, 47, 47)
                .addGroup(p_fundoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(p_fundoLayout.createSequentialGroup()
                        .addComponent(lb_titulo_processador)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lb_processador)
                        .addGap(15, 15, 15)
                        .addComponent(lb_titulo_ram)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lb_ram))
                    .addGroup(p_fundoLayout.createSequentialGroup()
                        .addComponent(lb_titulo_disco1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lb_disco1)
                        .addGap(15, 15, 15)
                        .addComponent(lb_titulo_disco2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lb_disco2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lb_titulo_disco3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lb_disco3)))
                .addContainerGap(87, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout gradiente1Layout = new javax.swing.GroupLayout(gradiente1);
        gradiente1.setLayout(gradiente1Layout);
        gradiente1Layout.setHorizontalGroup(
            gradiente1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, gradiente1Layout.createSequentialGroup()
                .addContainerGap(30, Short.MAX_VALUE)
                .addComponent(p_fundo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30))
        );
        gradiente1Layout.setVerticalGroup(
            gradiente1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, gradiente1Layout.createSequentialGroup()
                .addContainerGap(30, Short.MAX_VALUE)
                .addComponent(p_fundo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(gradiente1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(gradiente1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TelaCaptura.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TelaCaptura.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TelaCaptura.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TelaCaptura.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                TelaCaptura tela = new TelaCaptura();

                tela.setVisible(true);

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
                        
                        tela.lb_processador.setText(String.format("%.2f%%", looca.getProcessador().getUso()));
                        tela.lb_ram.setText(Conversor.formatarBytes(looca.getMemoria().getEmUso()));
                        DiscoGrupo grupoDeDiscos = looca.getGrupoDeDiscos();
                        List<Disco> discos = grupoDeDiscos.getDiscos();
                        List<Volume> volumes = grupoDeDiscos.getVolumes();
                        for (int i = 0; i < discos.size(); i++) {
                            if (i == 0) {
                                Volume volume = volumes.get(i);
                                Long emUso = (volume.getTotal() - volume.getDisponivel());
                                tela.lb_disco1.setText(String.valueOf(Conversor.formatarBytes(emUso)));
                            }
                        }
                        tela.lb_status.setText("ativo");
                        
                        tela.validarComponenteEmNivelAlerta(componentes);
                    }
                }, 0, 30000);
            }
        });
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private telas.Gradiente gradiente1;
    private javax.swing.JLabel lb_disco1;
    private javax.swing.JLabel lb_disco2;
    private javax.swing.JLabel lb_disco3;
    private javax.swing.JLabel lb_nome_empresa;
    private javax.swing.JLabel lb_processador;
    private javax.swing.JLabel lb_ram;
    private javax.swing.JLabel lb_status;
    private javax.swing.JLabel lb_titulo_disco1;
    private javax.swing.JLabel lb_titulo_disco2;
    private javax.swing.JLabel lb_titulo_disco3;
    private javax.swing.JLabel lb_titulo_processador;
    private javax.swing.JLabel lb_titulo_ram;
    private javax.swing.JLabel lb_titulo_status;
    private javax.swing.JPanel p_fundo;
    // End of variables declaration//GEN-END:variables
}
