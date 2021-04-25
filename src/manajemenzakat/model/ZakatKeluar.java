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
public class ZakatKeluar {
    private int id_zakat_keluar;
    private String kode;
    private int id_mustahiq;
    private double nominal;
    private int id_user;
    private String is_money;
    private String tanggal;
    private String mustahiq_nama;
    private String amil_nama;

    public String getAmil_nama() {
        return amil_nama;
    }

    public void setAmil_nama(String amil_nama) {
        this.amil_nama = amil_nama;
    }

    public String getMustahiq_nama() {
        return mustahiq_nama;
    }

    public void setMustahiq_nama(String mustahiq_nama) {
        this.mustahiq_nama = mustahiq_nama;
    }
    
    public String getIs_money() {
        return is_money;
    }

    public void setIs_money(String is_money) {
        this.is_money = is_money;
    }
    
    
    public int getId_zakat_keluar() {
        return id_zakat_keluar;
    }

    public void setId_zakat_keluar(int id_zakat_keluar) {
        this.id_zakat_keluar = id_zakat_keluar;
    }

    public String getKode() {
        return kode;
    }

    public void setKode(String kode) {
        this.kode = kode;
    }

    public int getId_mustahiq() {
        return id_mustahiq;
    }

    public void setId_mustahiq(int id_mustahiq) {
        this.id_mustahiq = id_mustahiq;
    }

    public double getNominal() {
        return nominal;
    }

    public void setNominal(double nominal) {
        this.nominal = nominal;
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }
    
}
