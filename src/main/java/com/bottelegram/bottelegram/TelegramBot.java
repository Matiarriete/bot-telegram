package com.bottelegram.bottelegram;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.JSONObject;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.apache.http.*;

import java.io.*;
import java.time.LocalDateTime;

public class TelegramBot extends TelegramLongPollingBot {

    int hora = 0;
    SendMessage sendMessage;

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
                sendMessage = sendMessage(update.getMessage().getChatId(), "AYUDA:\n/help -> Ayuda\n/ganador -> Ganador en el momento" +
                        " \n/puntuacion -> Puntuacion de cada equipo \n/puntuacionEquipo [Nombre del equipo] -> Puntuacion detallada de cada equipo \n/escuchar -> Enciende la opcion de escuchar si el archivo JSON es modificado" );
                execute(sendMessage);
            }
            if (update.getMessage().getText().equals("/puntuacion"))
                puntuacionJSON(update);
            if(update.getMessage().getText().equals("/escuchar")) {
                sendMessage = sendMessage(update.getMessage().getChatId(), "AVISO: Informamos que una vez que seleccione esta opcion " +
                        "no podra realizar ninguna otra accion mas y recibira un mensaje cada hora informando el ganador actual." +
                        " Si desea continuar con esta seleccion envie /escucharSI");
                execute(sendMessage);
            }
            if(update.getMessage().getText().equals("/escucharSI")) {
                sendMessage = sendMessage(update.getMessage().getChatId(), "Iniciando el modo escucha ...");
                while (true) {
                    verifyModify(update);
                }
            }
            if(update.getMessage().getText().contains("/puntuacionEquipo")){
                try {
                    puntuacionEquipoJSON(update, update.getMessage().getText().substring(18));
                } catch (Exception e){
                    sendMessage = sendMessage(update.getMessage().getChatId(), "Verifique que escribio un grupo");
                    execute(sendMessage);
                }
            }
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
        sendMessage = sendMessage(update.getMessage().getChatId(), "El ganador/es es/son " + name);
        execute(sendMessage);
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
        sendMessage = sendMessage(update.getMessage().getChatId(), "La puntuacion de los equipos son: \n" + equipos[0] + "\n" + equipos[1] + "\n" + equipos[2] + "\n" + equipos[3] + "\n" + equipos[4] + "\n"
                + equipos[5] + "\n" + equipos[6] + "\n" + equipos[7] + "\n" + equipos[8] + "\n" + equipos[9]);
        execute(sendMessage);
    }

    public void puntuacionEquipoJSON(Update update, String nombre) throws Exception{
        int resultado = 0;
        String textoFinal = "";
        String  name = null;
        String myJson = new JSONParser().parse(traerJSON()).toString();
        JSONObject ob = new JSONObject(myJson);
        JSONArray teams = ob.getJSONArray("teamdata");
        JSONArray points = null;
        for (int i = 0; i < teams.length(); i++) {
            if(teams.getJSONObject(i).getString("name").equalsIgnoreCase(nombre)) {
                points = teams.getJSONObject(i).getJSONArray("actividades");

                for (int j = 0; j < points.length(); j++) {
                    resultado = points.getJSONObject(j).getInt("puntos") + resultado;
                    name = points.getJSONObject(j).getString("name") + ": " + points.getJSONObject(j).getInt("puntos");
                    textoFinal = textoFinal + name + "\n";
                }
                textoFinal = textoFinal + "\n" + "----------------------------" +  "\n" + "TOTAL: " + resultado;
            }
        }

        if(textoFinal.equals("")) sendMessage = sendMessage(update.getMessage().getChatId(), "Verifique que el nombre del equipo este bien escrito");
        else sendMessage = sendMessage(update.getMessage().getChatId(), "Las puntuaciones del equipo son: \n" + textoFinal);

        execute(sendMessage);
    }

    public void verifyModify(Update update) throws Exception {
        if (LocalDateTime.now().getHour() == hora){
            if(hora < 23)hora = hora + 1;
            else hora = 0;
            ganadorJSON(update);
        }
    }

    public String traerJSON() throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet("https://raw.githubusercontent.com/danibanez/bootcampsolera/main/src/data/teamdata.json");
        CloseableHttpResponse response = httpClient.execute(request);
        HttpEntity entity = response.getEntity();
        return EntityUtils.toString(entity);

    }
}
