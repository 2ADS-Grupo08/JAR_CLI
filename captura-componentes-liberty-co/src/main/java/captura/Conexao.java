package captura;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author mari
 */
public class Conexao {

    private JdbcTemplate connection;

    public Conexao(String server) {
        BasicDataSource dataSource = new BasicDataSource();

        if (server.equalsIgnoreCase("mysql")) {
            dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");

            dataSource.setUrl("jdbc:mysql://172.17.0.2:3306/liberty-co?autoReconnect=true&useSSL=false"); // trocar o localhost:3306 pelo endereço do banco e o tecflix pelo nome do banco
            
            dataSource.setUsername("root"); //Usuario do banco
            dataSource.setPassword("#Gfgrupo8"); //Senha do banco

            this.connection = new JdbcTemplate(dataSource);
        } else { 
            dataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            dataSource.setUrl("jdbc:sqlserver://liberty-co.database.windows.net:1433;database"
                    + "=bd-liberty-co;user=admin-liberty-co@liberty-co;password={#Gfgrupo8};"
                    + "encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;");

            this.connection = new JdbcTemplate(dataSource);
        }

    }

    public JdbcTemplate getConnection() {
        return connection;
    }
}
