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
public class Mustahiq {
    private int id_mustahiq;
    private String nama;
    private String alamat;
    private String keterangan;
    private byte[] foto;
    private int id_jenis_mustahiq;
    private JenisMustahiq jenisMustahiq;
    private String nama_jenis;
    private User user;
    private int id_user;
    private String path;

    public String getNama_jenis() {
        return nama_jenis;
    }

    public void setNama_jenis(String nama_jenis) {
        this.nama_jenis = nama_jenis;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
    

    public JenisMustahiq getJenisMustahiq() {
        return jenisMustahiq;
    }

    public void setJenisMustahiq(JenisMustahiq jenisMustahiq) {
        this.jenisMustahiq = jenisMustahiq;
    }
    
    
    public Mustahiq(String nama, String alamat, String keterangan, 
            int id_jenis_mustahiq, int id_user) {
        this.nama = nama;
        this.alamat = alamat;
        this.keterangan = keterangan;
        this.id_jenis_mustahiq = id_jenis_mustahiq;
        this.id_user = id_user;
    }

    public Mustahiq() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }
    
    public int getId_mustahiq() {
        return id_mustahiq;
    }

    public void setId_mustahiq(int id_mustahiq) {
        this.id_mustahiq = id_mustahiq;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public byte[] getFoto() {
        return foto;
    }

    public void setFoto(byte[] foto) {
        this.foto = foto;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }
    
    public int getId_jenis_mustahiq() {
        return id_jenis_mustahiq;
    }

    public void setId_jenis_mustahiq(int id_jenis_mustahiq) {
        this.id_jenis_mustahiq = id_jenis_mustahiq;
    }
    
}
