package org.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class TransferRunnable implements Runnable{

    private static final int MAX_TRANSACTIONS = 30; // Максимальное кол-во транзакций
    private Account currentAccount;
    private List<Account> allAccounts;
    private Random random = new Random();
    private static int transactionsCompleted  = 0;
    private static final Object lock = new Object(); // Объект для синхронизации переменной transactionsCompleted
    private static final Logger logger = LogManager.getLogger(TransferRunnable.class);

    public TransferRunnable(Account currentAccount, List<Account> allAccounts) {
        this.currentAccount = currentAccount;
        this.allAccounts = allAccounts;
    }

    @Override
    public void run() {
        while (!shouldFinish()) {
            Account targetAccount = getRandomAccount();

            int transferAmount = random.nextInt(currentAccount.getMoney());

            // Проверка на отрицательный баланс и на перевод с одного аккаунта на этот же
            if (currentAccount.getMoney() >= transferAmount && !Objects.equals(currentAccount.getID(), targetAccount.getID())) {

                // Это условие предотвращает блокировку потоков и расставляет приоритеты
                if (currentAccount.getID().compareTo(targetAccount.getID()) < 0) {
                    synchronized (currentAccount) {
                        synchronized (targetAccount) {
                            currentAccount.setMoney(currentAccount.getMoney() - transferAmount);
                            targetAccount.setMoney(targetAccount.getMoney() + transferAmount);

                            synchronized (lock) {
                                writeLogMessage(transferAmount, targetAccount);

                                transactionsCompleted++;
                            }
                        }
                    }
                } else {
                    synchronized (targetAccount) {
                        synchronized (currentAccount) {
                            currentAccount.setMoney(currentAccount.getMoney() - transferAmount);
                            targetAccount.setMoney(targetAccount.getMoney() + transferAmount);

                            synchronized (lock) {
                                writeLogMessage(transferAmount, targetAccount);

                                transactionsCompleted++;
                            }
                        }
                    }
                }

                sleepRandomTime();
            }
        }
    }

    private void writeLogMessage(int transferAmount, Account targetAccount) {
        logger.info("Transferred " + transferAmount + " from " +
                currentAccount.getID() + " to " + targetAccount.getID());
    }

    private Account getRandomAccount() {
        int randomIndex = random.nextInt(allAccounts.size());
        return allAccounts.get(randomIndex);
    }

    private void sleepRandomTime() {
        int sleepTime = random.nextInt(1,3) * 1000;
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
    }

    private boolean shouldFinish() {
        return transactionsCompleted >= MAX_TRANSACTIONS;
    }
}
