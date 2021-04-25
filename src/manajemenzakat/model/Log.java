/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manajemenzakat.model;

/**
 *
 * @author RIZAL
 */
public class Log {
    private int id_log;
    private int id_user;
    private String text;
    private String waktu;

    public Log() {
    }

    public Log(int id_log, int id_user, String text, String waktu) {
        this.id_log = id_log;
        this.id_user = id_user;
        this.text = text;
        this.waktu = waktu;
    }
    public Log(int id_user, String text, String waktu) {
        this.id_user = id_user;
        this.text = text;
        this.waktu = waktu;
    }
    
    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public String getWaktu() {
        return waktu;
    }

    public void setWaktu(String waktu) {
        this.waktu = waktu;
    }
    
    public int getId_log() {
        return id_log;
    }

    public void setId_log(int id_log) {
        this.id_log = id_log;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
    
    
}
