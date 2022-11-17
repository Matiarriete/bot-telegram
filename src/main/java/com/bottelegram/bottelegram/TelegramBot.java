package com.bottelegram.bottelegram;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.apache.http.*;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.time.LocalDateTime;

import static java.lang.Thread.sleep;


public class TelegramBot extends TelegramLongPollingBot {

    static String URL_FICHERO = System.getProperty("user.dir");
    @Override
    public String getBotUsername() {
        return "SoleraBot";
    }

    @Override
    public String getBotToken() {
        return "5707282542:AAFg9stljCKtFDYTqkuOMIxft83QEhheIUQ";
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
             if (update.getMessage().getText().equals("/ganador"))
                ganadorJSON(update);
            if (update.getMessage().getText().equals("/help")){
                SendMessage sendMessage = sendMessage(update.getMessage().getChatId(), "AYUDA:\n/help -> Ayuda\n/ganador -> Ganador en el momento" +
                        " \n/puntuacion -> Puntuacion de cada equipo \n/escuchar -> Enciende la opcion de escuchar si el archivo JSON es modificado" );
                execute(sendMessage);
            }
            if (update.getMessage().getText().equals("/puntuacion"))
                puntuacionJSON(update);
            if(update.getMessage().getText().equals("/escuchar"))
                while(true)
                        verifyModify(update);

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SendMessage sendMessage(Long chatId, String texto) {
        return new SendMessage(chatId.toString(), texto);
    }

    public void ganadorJSON(Update update) throws Exception{
        String name = null;
        String myJson = new JSONParser().parse(traerJSON()).toString();
        int valorMax = 0;
        JSONObject ob = new JSONObject(myJson);
        JSONArray teams = ob.getJSONArray("teamdata");
        JSONArray points;
        for (int i = 0; i < teams.length(); i++) {
            int resultado = 0;
            points = teams.getJSONObject(i).getJSONArray("actividades");
            for (int j = 0; j < points.length(); j++) {
                resultado = points.getJSONObject(j).getInt("puntos") + resultado;
            }
            if (resultado > valorMax){
                valorMax = resultado;
                name = teams.getJSONObject(i).getString("name");
            }else{
                if (resultado == valorMax){
                    name = name + " " + teams.getJSONObject(i).getString("name");
                }
            }
        }
        SendMessage send = sendMessage(update.getMessage().getChatId(), "El ganador/es es/son " + name);
        execute(send);
    }

    public void puntuacionJSON(Update update) throws Exception{
        String[] equipos = new String[10];
        String  name = null;
        String myJson = new JSONParser().parse(traerJSON()).toString();
        JSONObject ob = new JSONObject(myJson);
        JSONArray teams = ob.getJSONArray("teamdata");
        JSONArray points;
        for (int i = 0; i < teams.length(); i++) {
            int resultado = 0;
            points = teams.getJSONObject(i).getJSONArray("actividades");
            for (int j = 0; j < points.length(); j++) {
                resultado = points.getJSONObject(j).getInt("puntos") + resultado;
            }
            name = teams.getJSONObject(i).getString("name") + ": " + resultado;
            equipos[i] = name;
        }
        for (int i = 0; i < teams.length(); i++) {
            System.out.println(equipos[i]);
        }
        SendMessage send = sendMessage(update.getMessage().getChatId(), "La puntuacion de los equipos son: \n" + equipos[0] + "\n" + equipos[1] + "\n" + equipos[2] + "\n" + equipos[3] + "\n" + equipos[4] + "\n"
                + equipos[5] + "\n" + equipos[6] + "\n" + equipos[7] + "\n" + equipos[8] + "\n" + equipos[9]);
        execute(send);
    }

//    public void verifyModify(Update update) throws Exception {
//        WatchService watchService
//                = FileSystems.getDefault().newWatchService();
//
//        Path path = Paths.get(URL_FICHERO);
//
//        path.register(
//                watchService,
//                StandardWatchEventKinds.ENTRY_MODIFY);
//
//        WatchKey key;
//        while ((key = watchService.take()) != null) {
//            for (WatchEvent<?> event : key.pollEvents()) {
//                ganadorJSON(update);
//            }
//            key.reset();
//            break;
//        }
//    }

    public void verifyModify(Update update) throws Exception {

    }

    public String traerJSON() throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet("https://raw.githubusercontent.com/Matiarriete/TelegramJSON/main/teamdata.json");
        CloseableHttpResponse response = httpClient.execute(request);
        HttpEntity entity = response.getEntity();
        return EntityUtils.toString(entity);

    }
}
