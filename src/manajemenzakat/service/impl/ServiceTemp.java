/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manajemenzakat.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import static manajemenzakat.controller.additional.RowTypeRole.ROLE_ADMINISTRATOR;
import manajemenzakat.model.JenisMustahiq;
import manajemenzakat.model.JenisZakat;
import manajemenzakat.model.Log;
import manajemenzakat.model.Mustahiq;
import manajemenzakat.model.Response;
import manajemenzakat.model.User;
import manajemenzakat.model.Zakat;
import manajemenzakat.model.ZakatKeluar;
import manajemenzakat.service.Service;
import manajemenzakat.util.Session;

/**
 *
 * @author RIZAL
 */
public class ServiceTemp implements Service{
    private List<Mustahiq> mustahiqList;
    private List<Zakat> zakatList;
    private List<Log> logList;
    private List<User> userList;
    private SimpleDateFormat sdf;
    
    @Override
    public Response create() {
        if (mustahiqList == null) {
            userList = new ArrayList<>();
            mustahiqList = new ArrayList<>();
            zakatList = new ArrayList<>();
            logList = new ArrayList<>();
            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
        return new Response(true, "", "Inisiasi database berhasil");
    }
    @Override
    public Response login(User user) {
        user.setNama("Administrator");
        user.setNo_telp("081216285386");
        user.setId_user(1);
        user.setPassword("");
        user.setRole(ROLE_ADMINISTRATOR);
        user.setIs_login("Y");
        user.setAlamat("#Ngalam");
        Session.setUser(user);
        saveUser(user);
        saveLog(new Log(
                (logList.size()+1), 
                user.getId_user(), 
                user.getNama()+" login", 
                sdf.format(new Date())
        ));
        return new Response(true, user, "Login berhasil");
    }
    
    @Override
    public Response logout(User user) {
        saveLog(new Log(
                (logList.size()+1), 
                user.getId_user(), 
                user.getNama()+" logout", 
                sdf.format(new Date())
        ));
        return new Response(true, user, "Logout berhasil");
    }
    
    @Override
    public Response saveUser(User user) {
        userList.add(user);
        saveLog(new Log(
                (logList.size()+1), 
                Session.getUser().getId_user(), 
                Session.getUser().getNama()+" membuat user baru bernama "+user.getNama(), 
                sdf.format(new Date())
        ));
        return new Response(true, user, "save user berhasil");
    }
    
    @Override
    public Response deleteUser(int id_user, String nama) {
        for (User u : userList) {
            if (u.getId_user()== id_user) {
                userList.remove(u);
                saveLog(new Log(
                        (logList.size()+1), 
                        Session.getUser().getId_user(), 
                        Session.getUser().getNama()+" menghapus Amil bernama "+u.getNama(), 
                        sdf.format(new Date())
                ));
                return new Response(true, userList, "Amil berhasil dihapus");
            }
        }
        return new Response(false, mustahiqList, "Gagal menghapus Amil");
    }
    
    @Override
    public Response getUserList(String search) {
        if (search.replaceAll(" ", "").isEmpty()) {
            return new Response(true, userList, "Data ditemukan!");
        }
        List<User> list = new ArrayList<>();
        userList.forEach((z) -> {
            if (z.getNama().toLowerCase().contains(search.toLowerCase()) 
                    || z.getNo_telp().toLowerCase()
                            .contains(search.toLowerCase())) {
                list.add(z);
            }
        });
        return new Response(true, list, "Data filter ditemukan!");
    }
    
    private ObservableList<JenisMustahiq> getJenisMustahiqList(){
        ObservableList<JenisMustahiq> list = FXCollections.observableArrayList();
//        List<JenisMustahiq> list = new ArrayList<>();
        list.add(new JenisMustahiq(0, "Fakir", "Orang-orang yang tidak mempunyai "
                + "harta atau penghasilan layak untuk\n" +
"memenuhi kebutuhan pokok"));
        list.add(new JenisMustahiq(1, "Miskin", "Orang yang memiliki pekerjaan "
                + "tetap tetapi tidak dapat mencukupi kebutuhannya sehari-hari"));
        list.add(new JenisMustahiq(2, "Amil", "orang yang ditunjuk untuk mengumpulkan "
                + "zakat, menyimpannya, membaginya kepada yang berhak dan mengerjakan "
                + "pembukuannya"));
        list.add(new JenisMustahiq(3, "Muallaf", "Orang yang baru masuk islam"));
        list.add(new JenisMustahiq(4, "Fi Riqab", "Budak atau hamba sahaya"));
        list.add(new JenisMustahiq(5, "Gharim", "Orang yang tersangkut (mempunyai) "
                + "utang karena kegiatannya dalam\n urusan kepentingan umum, antara "
                + "lain mendamaikan perselisihan\n antara keluarga, memelihara "
                + "persatuan umat Islam, melayani kegiatan dakwah Islam dan sebagainya"));
        list.add(new JenisMustahiq(6, "Fisabilillah", "Pejuang dijalan Allah"));
        list.add(new JenisMustahiq(7, "Ibnu Sabil", "Musafir dan para pelajar "
                + "perantauan"));
        return list;
    }
    @Override
    public Response getJenisMustahiq() {
        return new Response(true, getJenisMustahiqList(), "Data ditemukan!");
    }

    @Override
    public Response saveMustahiq(Mustahiq mustahiq) {
        Response response;
        String text = "";
        if (mustahiq.getId_mustahiq() <= 0) {
            mustahiq.setId_mustahiq(mustahiqList.size()+1);
            mustahiq.setJenisMustahiq(getJenisMustahiqList().get(mustahiq.getId_jenis_mustahiq()));
            mustahiqList.add(mustahiq);
            text = " membuat mustahiq baru dengan nama ";
            response = new Response(true, mustahiqList, "Mustahiq berhasil ditambahkan");
        }else{
            for (int i=0; i < mustahiqList.size(); i++) {
                Mustahiq m = mustahiqList.get(i);
                if (mustahiq.getId_mustahiq() == m.getId_mustahiq()) {
                    mustahiqList.get(i).setNama(mustahiq.getNama());
                    mustahiqList.get(i).setAlamat(mustahiq.getAlamat());
                    mustahiqList.get(i).setKeterangan(mustahiq.getKeterangan());
                    mustahiqList.get(i).setId_jenis_mustahiq(mustahiq.getId_jenis_mustahiq());
                    mustahiqList.get(i).setJenisMustahiq(getJenisMustahiqList().get(mustahiq.getId_jenis_mustahiq()));
                    text = " mengubah data mustahiq bernama ";
                    break;
                }
            }
            response = new Response(true, mustahiqList, "Mustahiq berhasil diupdate");
        }
        saveLog(new Log(
                (logList.size()+1), 
                Session.getUser().getId_user(), 
                Session.getUser().getNama()+text+mustahiq.getNama(), 
                sdf.format(new Date())
        ));
        return response;
    }

    @Override
    public Response getMustahiqList(String search) {
        if (search.replaceAll(" ", "").isEmpty()) {
            return new Response(true, mustahiqList, "Data ditemukan!");
        }
        List<Mustahiq> list = new ArrayList<>();
        mustahiqList.forEach((m) -> {
            if (m.getNama().toLowerCase().contains(search.toLowerCase()) 
                    || m.getAlamat().toLowerCase().contains(search.toLowerCase())
                    ||m.getKeterangan().toLowerCase().contains(search.toLowerCase()) 
                    || m.getJenisMustahiq().getNama_jenis().toLowerCase()
                            .contains(search.toLowerCase())) {
                list.add(m);
            }
        });
        return new Response(true, list, "Data filter ditemukan!");
    }

    @Override
    public Response deleteMustahiq(int id_mustahiq, String nama) {
        for (Mustahiq m : mustahiqList) {
            if (m.getId_mustahiq() == id_mustahiq) {
                mustahiqList.remove(m);
                saveLog(new Log(
                        (logList.size()+1), 
                        Session.getUser().getId_user(), 
                        Session.getUser().getNama()+" menghapus mustahiq bernama "+m.getNama(), 
                        sdf.format(new Date())
                ));
                return new Response(true, mustahiqList, "Mustahiq berhasil dihapus");
            }
        }
        return new Response(false, mustahiqList, "Gagal menghapus mustahiq");
    }

    @Override
    public Response getJenisZakat() {
        return new Response(true, getJenisZakatList(), "Data ditemukan!");
    }

    @Override
    public Response saveZakat(Zakat zakat) {
        zakat.setId_zakat(zakatList.size()+1);
        zakat.setJenisZakat(getJenisZakatList().get(zakat.getId_jenis_zakat()));
        zakat.setUser(Session.getUser());
        zakatList.add(zakat);
        saveLog(new Log(
                (logList.size()+1), 
                Session.getUser().getId_user(), 
                Session.getUser().getNama()+" menambah penerimaan zakat dari "
                        + "muzakki bernama "+zakat.getNama(), 
                sdf.format(new Date())
        ));
        return new Response(true, zakatList, "Zakat berhasil ditambahkan");
    }

    @Override
    public Response deleteZakat(int id_zakat, String nama, String kode) {
        for (Zakat z : zakatList) {
            if (z.getId_zakat() == id_zakat) {
                zakatList.remove(z);
                saveLog(new Log(
                        (logList.size()+1), 
                        Session.getUser().getId_user(), 
                        Session.getUser().getNama()+" menghapus penerimaan zakat dari "
                                + "muzakki bernama "+z.getNama(), 
                        sdf.format(new Date())
                ));
                return new Response(true, zakatList, "Zakat berhasil dihapus");
            }
        }
        return new Response(false, zakatList, "Gagal menghapus zakat");
    }

    @Override
    public Response getZakatList(String search) {
        if (search.replaceAll(" ", "").isEmpty()) {
            return new Response(true, zakatList, "Data ditemukan!");
        }
        List<Zakat> list = new ArrayList<>();
        zakatList.forEach((z) -> {
            if (z.getNama().toLowerCase().contains(search.toLowerCase()) 
                    || z.getAlamat().toLowerCase().contains(search.toLowerCase())
                    || z.getKode().toLowerCase().contains(search.toLowerCase()) 
                    || z.getJenisZakat().getNama_jenis().toLowerCase()
                            .contains(search.toLowerCase())
                    || z.getJiwa().toLowerCase().contains(search.toLowerCase())
                    || z.getUser().getNama().toLowerCase()
                            .contains(search.toLowerCase())
                    || z.getTanggal().contains(search)) {
                list.add(z);
            }
        });
        return new Response(true, list, "Data filter ditemukan!");
    }

    private ObservableList<JenisZakat> getJenisZakatList(){
        ObservableList<JenisZakat> list = FXCollections.observableArrayList();
        list.add(new JenisZakat(0, "ZFB", "Zakat Fitrah(Beras)", "Liter", "L", "N"));
        list.add(new JenisZakat(1, "ZFU", "Zakat Fitrah(Uang)", "Rupiah", "Rp.", "Y"));
        list.add(new JenisZakat(2, "ZM", "Zakat Mal", "Rupiah", "Rp.", "Y"));
        list.add(new JenisZakat(3, "ZQ", "Infaq", "Rupiah", "Rp.", "Y"));
        return list;
    }

    @Override
    public Response close() {
//        mustahiqList = new ArrayList<>();
//        zakatList = new ArrayList<>();
        return new Response(true, "", "flush berhasil");
    }

    @Override
    public Response getLogList(String search, int id_user) {
        if (search.replaceAll(" ", "").isEmpty()) {
            return new Response(true, logList, "Data ditemukan!");
        }
        List<Log> list = new ArrayList<>();
        logList.forEach((l) -> {
            if ((l.getWaktu().contains(search) 
                    || l.getText().toLowerCase().contains(search.toLowerCase()))
                    &&  l.getId_user() == id_user) {
                list.add(l);
            }
        });
        return new Response(true, list, "Data filter ditemukan!");
    }

    @Override
    public Response saveLog(Log log) {
       logList.add(log);
       return new Response(true, logList, "Log berhasil ditambahkan");
    }

    @Override
    public Response getLogNotif() {
        return new Response(true, logList, "Data ditemukan!");
    }

    @Override
    public Response getDashboardZakat() {
        return new Response(true, null, "Data ditemukan!");
    }

    @Override
    public Response getDashboardChart() {
        return new Response(true, null, "Data ditemukan!");
    }

    @Override
    public Response getZakatKeluarList() {
        return new Response(true, null, "Data ditemukan!");
    }

    @Override
    public Response saveZakatKeluar(ZakatKeluar zakatKeluar) {
        return new Response(true, null, "Data ditemukan!");
    }

    @Override
    public Response deleteZakatKeluar(int id_zakat_keluar, String nama) {
        return new Response(true, null, "Data ditemukan!");
    }
}
