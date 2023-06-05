/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package encerrar.janelas.inovacao;

import com.github.britooo.looca.api.core.Looca;
import java.io.IOException;

/**
 *
 * @author mari
 */
public class EncerraJanelas {

    static Looca looca = new Looca();

    public static void terminalLinux(String pid) throws IOException {
        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec("kill -9 " + pid);

        System.out.println("entrei no método terminalLinux");

        try {
            int codigoDeSaida = process.waitFor();
            System.out.println("Código de saída " + codigoDeSaida);
        } catch (InterruptedException e) {
        }

    }
}
