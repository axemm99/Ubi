package com.example.ubi_interfaces.classes;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class User {

    String name;
    String id; // Não tenho a certeza se os id's são todos ints...
    String email;
    int performanceId; // Por default vai ser;
    String picture; //Não sei see vai ser sempre String por causa de guardar as imagens na base de dados
    boolean currentUser; // Saber se o user está a ver o perfil dele ou nao
    String tagName = "USER Class";
    Map<String, Integer> achievements;

    /* Os AuthType vão ser
    * firebase
    * facebook
    * google */
    String authType; // Mais fácil para saber que calls fazer

    public User() {

    }

    public User(String id, String email, String name, String authType, Map<String, Integer>... achievements) {
        this.id = id;
        this.authType = authType;
        this.email = email;
        this.name = name;
        Log.d("Achievements length: ",achievements.length+" coisos ");
        this.achievements = achievements.length != 0 ? achievements[0] : null;
    }

    // Função que devolve o utilizador logado



    // Não precisa de um "set" pelo menos por agora
    public String getId() { return this.id; }

    // Também não precisa de um SET
    public String getAuthType() { return this.authType; }

    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return this.email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public int getPerformanceId() {
        return this.performanceId;
    }
    public void setPerformanceId(int perfId) {
        this.performanceId = perfId;
    }

    public String getPicture() {
        return this.picture;
    }
    public void setPicture(String picture) {
        this.picture = picture;
    }

    public boolean isCurrentUser () {
        boolean current = false;
        return true;
    }

    public Map<String, Integer> getAchievements() {
        Log.d("Achievments User Class", String.valueOf(this.achievements));
        return this.achievements; }
    public void setAchievements(Map<String, Integer> achis) {
        this.achievements = achis;
    }
    public void addAchievment(String idAchievment, Integer totalPoints) {
        this.achievements.put(idAchievment, totalPoints);
    }
}
