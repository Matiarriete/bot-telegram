package com.bottelegram.bottelegram;

import org.json.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.JSONObject;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.FileReader;

public class TelegramBot extends TelegramLongPollingBot {
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
            leerJSON(update);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SendMessage sendMessage(Long chatId, String texto) {
        return new SendMessage(chatId.toString(), texto);
    }

    public void leerJSON(Update update) throws Exception{
        String name = null;
        String myJson = new JSONParser().parse(new FileReader("C://Users//matia//Desktop//teamdata.json")).toString();
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
}
