package com.finickyltd.finicky.models;

public class User {

    private int id;
    private String name;
    private String email;
    private String password;
    private String account_type;
    private String membership_type;
    private String subscription_expiration;
    private int isRenewalEnabled;

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public User(int id, String name, String email){
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public User(int id, String name, String email, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public User(int id, String name, String email, String password, String account_type, String membership_type){
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.account_type = account_type;
        this.membership_type = membership_type;
    }

    public User(int id, String name, String email, String password, String account_type, String membership_type, String subscription_expiration){
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.account_type = account_type;
        this.membership_type = membership_type;
        this.subscription_expiration = subscription_expiration;
    }

    public User(int id, String name, String email, String password, String account_type, String membership_type, String subscription_expiration, int isRenewalEnabled){
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.account_type = account_type;
        this.membership_type = membership_type;
        this.subscription_expiration = subscription_expiration;
        this.isRenewalEnabled = isRenewalEnabled;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPassword(){
        return this.password;
    }

    public String getAccount_type() {
        return account_type;
    }

    public String getMembership_type() {
        return membership_type;
    }

    public String getSubscription_expiration() {
        return subscription_expiration;
    }

    public int getRenewalEnabled() {
        return isRenewalEnabled;
    }
}
