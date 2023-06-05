package telas;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import captura.Conexao;
import com.github.britooo.looca.api.core.Looca;
import com.github.britooo.looca.api.group.discos.Disco;
import com.github.britooo.looca.api.group.discos.DiscoGrupo;
import com.github.britooo.looca.api.group.discos.Volume;
import inserts.Insercao;
import org.springframework.jdbc.core.JdbcTemplate;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import tabelas.Componente;
import tabelas.Maquina;

/**
 *
 * @author leonardo.prado
 */
public class LoginCLI {

    private static JdbcTemplate jdbcAzure;
    private static JdbcTemplate jdbcMysql;
    private static Looca looca;
    private static final Logger logger = Logger.getLogger(LoginCLI.class.getName());

    public static void logFormatacao() throws IOException {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String dataFormatada = dateFormat.format(date);

        Path pathW = Paths.get("/home/leo/Desktop/JAR");
        if (!Files.exists(pathW)) {
            Files.createDirectory(pathW);
        }

        FileHandler fileHandler = new FileHandler(String.format("/home/leo/Desktop/JAR/%s.txt", dataFormatada), true);

        fileHandler.setFormatter(new Formatter() {
            private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd >> HH:mm:ss");

            public String format(LogRecord record) {
                StringBuilder builder = new StringBuilder();
                builder.append(dateFormat.format(new Date())).append(" ");
                builder.append(record.getLevel()).append(": ");
                builder.append(record.getMessage()).append(" (");
                builder.append(record.getSourceClassName()).append(".");
                builder.append(record.getSourceMethodName()).append(")");
                builder.append(System.lineSeparator());
                return builder.toString();
            }
        }
        );
        logger.addHandler(fileHandler);
        logger.setLevel(Level.ALL);
    }

    public static void main(String args[]) throws IOException {

        List<Maquina> maquinas = new ArrayList<>();
        List<Componente> getFkMaquina = new ArrayList();
        List<Componente> componentes = new ArrayList();

        Conexao conexaoAzure = new Conexao("azure");
        Conexao conexaoMysql = new Conexao("mysql");

        jdbcAzure = conexaoAzure.getConnection();
        jdbcMysql = conexaoMysql.getConnection();
        looca = new Looca();

        Scanner scannerNome = new Scanner(System.in);
        Scanner scannerSobrenome = new Scanner(System.in);
        Scanner scannerHostName = new Scanner(System.in);
        String nome;
        String sobrenome;
        String hostName;

        System.out.println("LOGIN");
        System.out.println("Nome:");
        nome = scannerNome.nextLine();
        System.out.println("Sobrenome:");
        sobrenome = scannerSobrenome.nextLine();
        System.out.println("HostName:");
        hostName = scannerHostName.nextLine();

        String hostNamePc = looca.getRede().getParametros().getHostName();
        maquinas = jdbcAzure.query("SELECT * FROM Maquina WHERE hostName = ?",
                new BeanPropertyRowMapper(Maquina.class), hostNamePc);

        if (maquinas.size() > 0) {
            if ((maquinas.get(0).getNomeDono().equals(nome)) && (maquinas.get(0).getSobrenomeDono().equals(sobrenome)) && (maquinas.get(0).getHostName().equals(hostName))) {
                logger.info("Login realizado por " + nome + " efetuado com sucesso!!");

                String hostNameMaquina = looca.getRede().getParametros().getHostName();
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
            } else {
                System.out.println("Não encontrado");
                logger.severe("Login realizado por " + nome + " falhou!!");
            }
        }
    }
}
