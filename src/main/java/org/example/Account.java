package org.example;

public class Account {

    private String ID;
    private int Money;


    public Account() {
    }

    public Account(String ID) {
        this.ID = ID;
        this.Money = 10000;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public int getMoney() {
        return Money;
    }

    public void setMoney(int money) {
        Money = money;
    }
}

