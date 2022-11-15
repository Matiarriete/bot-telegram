package com.bottelegram.bottelegram;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
public class BotTelegramApplication {

	public static void main(String[] args) {
		try{
			TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
			botsApi.registerBot(new TelegramBot());
		} catch(Exception e){
			e.printStackTrace();
		}

	}



}
