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
public class Zakat {
    private int id_zakat;
    private String kode;
    private String nama;
    private String jiwa;
    private int id_jenis_zakat;
    private double nominal;
    private int id_user;
    private User user;
    private String user_nama;
    private String alamat;
    private JenisZakat jenisZakat;
    private String jenis_nama;
    private String tanggal;

    public String getJenis_nama() {
        return jenis_nama;
    }

    public void setJenis_nama(String jenis_nama) {
        this.jenis_nama = jenis_nama;
    }

    public String getUser_nama() {
        return user_nama;
    }

    public void setUser_nama(String user_nama) {
        this.user_nama = user_nama;
    }

    public Zakat() {
    }

    public Zakat(int id_zakat, String kode, String nama, String jiwa, 
            int id_jenis_zakat, int id_user, User user, String alamat, 
            JenisZakat jenisZakat, double nominal) {
        this.id_zakat = id_zakat;
        this.kode = kode;
        this.nama = nama;
        this.jiwa = jiwa;
        this.id_jenis_zakat = id_jenis_zakat;
        this.id_user = id_user;
        this.user = user;
        this.alamat = alamat;
        this.jenisZakat = jenisZakat;
        this.nominal = nominal;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public double getNominal() {
        return nominal;
    }

    public void setNominal(double nominal) {
        this.nominal = nominal;
    }

    
    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public int getId_zakat() {
        return id_zakat;
    }

    public void setId_zakat(int id_zakat) {
        this.id_zakat = id_zakat;
    }

    public String getKode() {
        return kode;
    }

    public void setKode(String kode) {
        this.kode = kode;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getJiwa() {
        return jiwa;
    }

    public void setJiwa(String jiwa) {
        this.jiwa = jiwa;
    }

    public int getId_jenis_zakat() {
        return id_jenis_zakat;
    }

    public void setId_jenis_zakat(int id_jenis_zakat) {
        this.id_jenis_zakat = id_jenis_zakat;
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public JenisZakat getJenisZakat() {
        return jenisZakat;
    }

    public void setJenisZakat(JenisZakat jenisZakat) {
        this.jenisZakat = jenisZakat;
    }
    
    
}
