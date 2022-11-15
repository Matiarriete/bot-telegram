package com.bottelegram.bottelegram;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

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
        SendMessage send = sendMessage(update.getMessage().getChatId(), "El texto escrito fue: " + update.getMessage().getText());
        System.out.println("Mensaje recibido: " + update.getMessage().getText());
        try {
            execute(send);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public SendMessage sendMessage(Long chatId, String texto) {
        return new SendMessage(chatId.toString(), texto);
    }
}
