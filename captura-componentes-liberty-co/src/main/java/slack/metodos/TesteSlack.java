/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package slack.metodos;

import java.io.IOException;
import org.json.JSONObject;

/**
 *
 * @author Eryka
 */
public class TesteSlack {
    public static void main(String[] args)throws IOException, InterruptedException{
        JSONObject json = new JSONObject();
        json.put("text", "Hello world");
        Slack.enviarMensagem(json);
    }
}
