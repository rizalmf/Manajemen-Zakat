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
public class JenisZakat {
    private int id_jenis_zakat;
    private String kode;
    private String nama_jenis;
    private String keterangan;
    private String satuan;
    private String is_money;
    
    public JenisZakat() {
    }

    public JenisZakat(int id_jenis_zakat, String kode, String nama_jenis, 
            String keterangan, String satuan, String is_money) {
        this.id_jenis_zakat = id_jenis_zakat;
        this.kode = kode;
        this.nama_jenis = nama_jenis;
        this.keterangan = keterangan;
        this.satuan = satuan;
        this.is_money = is_money;
    }

    public String getIs_money() {
        return is_money;
    }

    public void setIs_money(String is_money) {
        this.is_money = is_money;
    }

    public String getSatuan() {
        return satuan;
    }

    public void setSatuan(String satuan) {
        this.satuan = satuan;
    }

    
    public int getId_jenis_zakat() {
        return id_jenis_zakat;
    }

    public void setId_jenis_zakat(int id_jenis_zakat) {
        this.id_jenis_zakat = id_jenis_zakat;
    }

    public String getKode() {
        return kode;
    }

    public void setKode(String kode) {
        this.kode = kode;
    }

    public String getNama_jenis() {
        return nama_jenis;
    }

    public void setNama_jenis(String nama_jenis) {
        this.nama_jenis = nama_jenis;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }
    
    
}
