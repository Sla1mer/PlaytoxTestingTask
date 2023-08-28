package org.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);
    public static void main(String[] args) {

        int numberOfAccounts = 4; // Кол-во создаваемых аккаунтов
        List<Account> accounts = new ArrayList<>();

        // Создание случайных счетов
        for (int i = 0; i < numberOfAccounts; i++) {
            String randomID = String.valueOf(UUID.randomUUID());
            Account account = new Account(randomID);
            accounts.add(account);
        }

        // Создание и запуск потоков
        List<Thread> threads = new ArrayList<>();

        for (Account account : accounts) {
            Thread thread = new Thread(new TransferRunnable(account, accounts));
            thread.start();
            threads.add(thread);
        }

        // Ожидание завершения всех потоков
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
                logger.error(e.getMessage());
            }
        }

    }


}