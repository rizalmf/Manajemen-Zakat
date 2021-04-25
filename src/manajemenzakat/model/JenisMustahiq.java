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
public class JenisMustahiq {
    private int id_jenis_mustahiq;
    private String nama_jenis;
    private String keterangan;

    public JenisMustahiq() {
    }

    public JenisMustahiq(int id_jenis_mustahiq, String nama_jenis, String keterangan) {
        this.id_jenis_mustahiq = id_jenis_mustahiq;
        this.nama_jenis = nama_jenis;
        this.keterangan = keterangan;
    }

    
    public int getId_jenis_mustahiq() {
        return id_jenis_mustahiq;
    }

    public void setId_jenis_mustahiq(int id_jenis_mustahiq) {
        this.id_jenis_mustahiq = id_jenis_mustahiq;
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
