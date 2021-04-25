/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manajemenzakat.service.impl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class ServiceJdbc implements Service{
    private Connection connection;
    private PreparedStatement statement;
    private Connection getConnection(){
        try {
            if (connection == null) {
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection("jdbc:sqlite:manajemenzakat.db");
            }else{
                if (connection.isClosed()) {
                    Class.forName("org.sqlite.JDBC");
                    connection = DriverManager.getConnection("jdbc:sqlite:manajemenzakat.db");
                }
            }
        } catch (Exception e) {
            Logger.getLogger(ServiceJdbc.class.getName()).log(Level.SEVERE, null, e);
        }
        return connection;
    }
    private SimpleDateFormat sdf;
    @Override
    public Response create() {
        try {
            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //create table user
            String createUser = "CREATE TABLE IF NOT EXISTS `user` (" +
                "  id_user integer primary key autoincrement," +
                "  username varchar," +
                "  password varchar," +
                "  nama varchar," +
                "  foto blob," +
                "  no_telp varchar," +
                "  role integer," +
                "  path varchar," +
                "  is_login varchar," +
                "  alamat varchar" +
                ")";
            statement = getConnection().prepareStatement(createUser);
            statement.executeUpdate();
            
            //create table log
            String createLog = "CREATE TABLE IF NOT EXISTS `log` (" +
                "  id_log integer primary key autoincrement," +
                "  id_user integer," +
                "  text varchar," +
                "  waktu varchar" +
                ")";
            statement = getConnection().prepareStatement(createLog);
            statement.executeUpdate();
            
            //create table mustahiq
            String createMustahiq = "CREATE TABLE IF NOT EXISTS `mustahiq` (" +
                "  id_mustahiq integer primary key autoincrement," +
                "  nama varchar," +
                "  alamat varchar," +
                "  keterangan varchar," +
                "  foto blob," +
                "  id_jenis_mustahiq integer," +
                "  id_user integer," +
                "  path varchar" +
                ")";
            statement = getConnection().prepareStatement(createMustahiq);
            statement.executeUpdate();
            
            //create table zakat
            String createZakat = "CREATE TABLE IF NOT EXISTS `zakat` (" +
                "  id_zakat integer primary key autoincrement," +
                "  kode varchar," +
                "  nama varchar," +
                "  jiwa varchar," +
                "  id_jenis_zakat integer," +
                "  nominal double," +
                "  id_user integer," +
                "  alamat varchar," +
                "  tanggal varchar" +
                ")";
            statement = getConnection().prepareStatement(createZakat);
            statement.executeUpdate();
            
            //create table zakat
            String createZakatKeluar = "CREATE TABLE IF NOT EXISTS `zakat_keluar` (" +
                "  id_zakat_keluar integer primary key autoincrement," +
                "  kode varchar," +
                "  id_mustahiq integer," +
                "  id_jenis_zakat integer," +
                "  nominal double," +
                "  id_user integer," +
                "  is_money varchar," +
                "  tanggal varchar" +
                ")";
            statement = getConnection().prepareStatement(createZakatKeluar);
            statement.executeUpdate();
            
            //check new installed
            String check = "select count(id_user) as total from user where role=?";
            statement = getConnection().prepareStatement(check);
            statement.setInt(1, ROLE_ADMINISTRATOR);
            ResultSet rs = statement.executeQuery();
            int total =(rs.next()) ? rs.getInt("total"): 0;
            if (total < 1) {
                User user = new User();
                user.setNama("Rizal Maulana Fahmi");
                user.setUsername("admin");
                user.setPassword("admin");
                user.setAlamat("#Raisohilum");
                user.setNo_telp("081216285386");
                user.setRole(ROLE_ADMINISTRATOR);
                saveUser(user);
            }
            return new Response(true, null, "Database berhasi di eksekusi");
        } catch (Exception e) {
            Logger.getLogger(ServiceJdbc.class.getName()).log(Level.SEVERE, null, e);
            return new Response(false, null, "Gagal e:"+e.getMessage());
        }
    }
    private String hashPwd(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hashInBytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : hashInBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
    @Override
    public Response login(User user) {
        String error = "user tidak ditemukan";
        try {
//            String sql = "select * from user where role=?";
            String sql = "select * from user where username=? and password=?";
            statement = getConnection().prepareStatement(sql); 
//            statement.setInt(1, ROLE_ADMINISTRATOR);
            statement.setString(1, user.getUsername());
            statement.setString(2, hashPwd(user.getPassword()));
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {    
                user.setNama(rs.getString("nama"));
                user.setNo_telp(rs.getString("no_telp"));
                user.setId_user(rs.getInt("id_user"));
                user.setRole(rs.getInt("role"));
                user.setPassword(rs.getString("password"));
                user.setIs_login("Y");
                user.setAlamat(rs.getString("alamat"));
                user.setPath(rs.getString("path"));
                saveLog(new Log( 
                        user.getId_user(), 
                        user.getNama()+" login", 
                        sdf.format(new Date())
                ));
                return new Response(true, user, "login berhasil");
            }
        } catch (Exception e) {
            Logger.getLogger(ServiceJdbc.class.getName()).log(Level.SEVERE, null, e);
            error = "Gagal e:"+e.getMessage();
        }
        return new Response(false, null, error);
    }

    @Override
    public Response logout(User user) {
        Response response = saveLog(new Log(
                user.getId_user(), 
                user.getNama()+" logout", 
                sdf.format(new Date())
        ));
        if (response.isStatus()) {
            return new Response(true, user, "Logout berhasil");
        }
        return new Response(false, null, "Logout gagal");
    }

    @Override
    public Response saveUser(User user) {
        String error;
        try {
            String sql;
            String msg;
            if (user.getId_user() <= 0) {
                sql = "insert into user values(null, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                statement = getConnection().prepareStatement(sql);
                statement.setString(1, user.getUsername());
                statement.setString(2, hashPwd(user.getPassword()));
                statement.setString(3, user.getNama());
                statement.setBytes(4, user.getFoto());
                statement.setString(5, user.getNo_telp());
                statement.setInt(6, user.getRole());
                statement.setString(7, user.getPath());
                statement.setString(8, user.getIs_login());
                statement.setString(9, user.getAlamat());
                msg =" membuat user baru bernama ";
            }else{
                sql = "update user set username=?, password=?, nama=?, no_telp=?,"
                        + "role=?, path=?, alamat=? where id_user=?";
                statement = getConnection().prepareStatement(sql);
                statement.setString(1, user.getUsername());
                statement.setString(2, user.getPassword());
                statement.setString(3, user.getNama());
                statement.setString(4, user.getNo_telp());
                statement.setInt(5, user.getRole());
                statement.setString(6, user.getPath());
                statement.setString(7, user.getAlamat());
                statement.setInt(8, user.getId_user());
                msg = " mengubah user bernama ";
            }
            statement.executeUpdate();
            saveLog(new Log(
                    Session.getUser().getId_user(), 
                    Session.getUser().getNama()+msg+user.getNama(), 
                    sdf.format(new Date())
            ));
            return new Response(true, user, "save user berhasil");
        } catch (Exception e) {
            error = "save user gagal e:"+e.getMessage();
        }
        return new Response(false, null, error);
    }

    @Override
    public Response deleteUser(int id_user, String nama) {
        String error;
        try {
            String sql = "delete from user where id_user=?";
            statement = getConnection().prepareStatement(sql);
            statement.setInt(1, id_user);
            statement.executeUpdate();
            saveLog(new Log(
                        Session.getUser().getId_user(), 
                        Session.getUser().getNama()+" menghapus Amil bernama "+nama, 
                        sdf.format(new Date())
                ));
            return new Response(true, null, "Amil berhasil dihapus");
        } catch (Exception e) {
            error = "delete Amil gagal e:"+e.getMessage();
        }
        return new Response(false, null, error);
    }

    @Override
    public Response getUserList(String search) {
        String error;
        try {
            String sql = "select * from user "
                    + "where nama like ? or alamat like ?"
                    + "order by role asc, nama asc";
            statement = getConnection().prepareStatement(sql);
            statement.setString(1, "%"+search+"%");
            statement.setString(2, "%"+search+"%");
            ResultSet rs = statement.executeQuery();
            List<User> userList = new ArrayList<>();
            while (rs.next()) {                
                User user = new User();
                user.setNama(rs.getString("nama"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setAlamat(rs.getString("alamat"));
                user.setNo_telp(rs.getString("no_telp"));
                user.setRole(rs.getInt("role"));
                user.setPath(rs.getString("path"));
                user.setId_user(rs.getInt("id_user"));
                userList.add(user);
            }
            return new Response(true, userList, "Data ditemukan!");
        } catch (Exception e) {
            error = "get list gagal e:"+e.getMessage();
        }
        return new Response(false, null, error);
    }

    @Override
    public Response close() {
        String error;
        try {
            getConnection().close();
            System.out.println("close connection db");
            return new Response(true, null, "berhasil close connection");
        } catch (Exception e) {
            error = "close db gagal e:"+e.getMessage();
        }
        return new Response(false, null, error);
    }
    private ObservableList<JenisMustahiq> getJenisMustahiqList(){
        ObservableList<JenisMustahiq> list = FXCollections.observableArrayList();
//        List<JenisMustahiq> list = new ArrayList<>();
        list.add(new JenisMustahiq(0, "Fakir", "Orang-orang yang tidak mempunyai "
                + "harta atau penghasilan layak untuk\n" 
                +"memenuhi kebutuhan pokok"));
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
        String error;
        try {
            String sql;
            String msg;
            if (mustahiq.getId_mustahiq()<= 0) {
                sql = "insert into mustahiq values(null, ?, ?, ?, ?, ?, ?, ?)";
                statement = getConnection().prepareStatement(sql);
                statement.setString(1, mustahiq.getNama());
                statement.setString(2, mustahiq.getAlamat());
                statement.setString(3, mustahiq.getKeterangan());
                statement.setBytes(4, mustahiq.getFoto());
                statement.setInt(5, mustahiq.getId_jenis_mustahiq());
                statement.setInt(6, mustahiq.getId_user());
                statement.setString(7, mustahiq.getPath());
                msg =" membuat mustahiq baru bernama ";
            }else{
                sql = "update mustahiq set nama=?, alamat=?, keterangan=?, "
                        + "foto=?, id_jenis_mustahiq=?, id_user=?, path=? "
                        + "where id_mustahiq=?";
                statement = getConnection().prepareStatement(sql);
                statement.setString(1, mustahiq.getNama());
                statement.setString(2, mustahiq.getAlamat());
                statement.setString(3, mustahiq.getKeterangan());
                statement.setBytes(4, mustahiq.getFoto());
                statement.setInt(5, mustahiq.getId_jenis_mustahiq());
                statement.setInt(6, mustahiq.getId_user());
                statement.setString(7, mustahiq.getPath());
                statement.setInt(8, mustahiq.getId_mustahiq());
                msg = " mengubah mustahiq bernama ";
            }
            statement.executeUpdate();
            saveLog(new Log(
                    Session.getUser().getId_user(), 
                    Session.getUser().getNama()+msg+mustahiq.getNama(), 
                    sdf.format(new Date())
            ));
            return new Response(true, mustahiq, "save mustahiq berhasil");
        } catch (Exception e) {
            error = "save mustahiq gagal e:"+e.getMessage();
        }
        return new Response(false, null, error);
    }

    @Override
    public Response deleteMustahiq(int id_mustahiq, String nama) {
        String error;
        try {
            String sql = "delete from mustahiq where id_mustahiq=?";
            statement = getConnection().prepareStatement(sql);
            statement.setInt(1, id_mustahiq);
            statement.executeUpdate();
            saveLog(new Log(
                        Session.getUser().getId_user(), 
                        Session.getUser().getNama()+" menghapus Mustahiq bernama "+nama, 
                        sdf.format(new Date())
                ));
            return new Response(true, null, "Mustahiq berhasil dihapus");
        } catch (Exception e) {
            error = "delete Mustahiq gagal e:"+e.getMessage();
        }
        return new Response(false, null, error);
    }

    @Override
    public Response getMustahiqList(String search) {
        String error;
        try {
            String sql = "select * from mustahiq "
                    + "where nama like ? or alamat like ? or keterangan like ? "
                    + "order by id_jenis_mustahiq asc, nama asc";
            statement = getConnection().prepareStatement(sql);
            statement.setString(1, "%"+search+"%");
            statement.setString(2, "%"+search+"%");
            statement.setString(3, "%"+search+"%");
            ResultSet rs = statement.executeQuery();
            List<Mustahiq> list = new ArrayList<>();
            while (rs.next()) {                
                Mustahiq m = new Mustahiq();
                m.setNama(rs.getString("nama"));
                m.setAlamat(rs.getString("alamat"));
                m.setKeterangan(rs.getString("keterangan"));
                m.setFoto(rs.getBytes("foto"));
                int id_jenis_mustahiq = rs.getInt("id_jenis_mustahiq");
                m.setId_jenis_mustahiq(id_jenis_mustahiq);
                m.setJenisMustahiq(getJenisMustahiqList().get(id_jenis_mustahiq));
                m.setId_user(rs.getInt("id_user"));
                m.setPath(rs.getString("path"));
                m.setId_mustahiq(rs.getInt("id_mustahiq"));
                list.add(m);
            }
            return new Response(true, list, "Data ditemukan!");
        } catch (Exception e) {
            error = "get list gagal e:"+e.getMessage();
        }
        return new Response(false, null, error);
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
    public Response getJenisZakat() {
        return new Response(true, getJenisZakatList(), "Data ditemukan!");
    }

    @Override
    public Response saveZakat(Zakat zakat) {
        String error;
        try {
            String sql;
            String msg;
            if (zakat.getId_zakat()<= 0) {
                sql = "insert into zakat values(null, ?, ?, ?, ?, ?, ?, ?, ?)";
                statement = getConnection().prepareStatement(sql);
                statement.setString(1, zakat.getKode());
                statement.setString(2, zakat.getNama());
                statement.setString(3, zakat.getJiwa());
                statement.setInt(4, zakat.getId_jenis_zakat());
                statement.setDouble(5, zakat.getNominal());
                statement.setInt(6, zakat.getId_user());
                statement.setString(7, zakat.getAlamat());
                statement.setString(8, zakat.getTanggal());
                msg =" menambah penerimaan zakat dari "
                        + "muzakki bernama ";
            }else{
                sql = "update zakat set kode=?, nama=?, jiwa=?, "
                        + "id_jenis_zakat=?, nominal=?, id_user=?, alamat=?, "
                        + "tanggal=? where id_zakat=?";
                statement = getConnection().prepareStatement(sql);
                statement.setString(1, zakat.getKode());
                statement.setString(2, zakat.getNama());
                statement.setString(3, zakat.getJiwa());
                statement.setInt(4, zakat.getId_jenis_zakat());
                statement.setDouble(5, zakat.getNominal());
                statement.setInt(6, zakat.getId_user());
                statement.setString(7, zakat.getAlamat());
                statement.setString(8, zakat.getTanggal());
                statement.setInt(9, zakat.getId_zakat());
                msg = " mengubah penerimaan zakat dari "
                        + "muzakki bernama ";
            }
            statement.executeUpdate();
            saveLog(new Log(
                    Session.getUser().getId_user(), 
                    Session.getUser().getNama()+msg+zakat.getNama(), 
                    sdf.format(new Date())
            ));
            return new Response(true, zakat, "save zakat berhasil");
        } catch (Exception e) {
            error = "save zakat gagal e:"+e.getMessage();
        }
        return new Response(false, null, error);
    }

    @Override
    public Response deleteZakat(int id_zakat, String nama, String kode) {
        String error;
        try {
            String sql = "delete from zakat where id_zakat=?";
            statement = getConnection().prepareStatement(sql);
            statement.setInt(1, id_zakat);
            statement.executeUpdate();
            saveLog(new Log(
                        Session.getUser().getId_user(), 
                        Session.getUser().getNama()+" menghapus Zakat dengan nota "
                                +kode+" dari muzakki bernama "+nama, 
                        sdf.format(new Date())
                ));
            return new Response(true, null, "Zakat berhasil dihapus");
        } catch (Exception e) {
            error = "delete Zakat gagal e:"+e.getMessage();
        }
        return new Response(false, null, error);
    }
    private double setNominal(String text){
        double nom;
        try {
            nom =Double.parseDouble(text);
        } catch (Exception e) {
            nom = 0;
        }
        return nom;
    }
    @Override
    public Response getZakatList(String search) {
        String error;
        try {
            String sql = "select z.*, u.nama as user_nama "
                    + "from zakat z join user u on z.id_user=u.id_user "
                    + "where z.kode like ? or z.nama like ? or z.jiwa like ? "
                    + " or z.tanggal like ? or z.nominal=? or u.nama like ? "
                    + "order by tanggal desc, id_zakat desc";
            statement = getConnection().prepareStatement(sql);
            statement.setString(1, "%"+search+"%");
            statement.setString(2, "%"+search+"%");
            statement.setString(3, "%"+search+"%");
            statement.setString(4, "%"+search+"%");
            statement.setDouble(5, setNominal(search));
            statement.setString(6, "%"+search+"%");
            ResultSet rs = statement.executeQuery();
            List<Zakat> list = new ArrayList<>();
            while (rs.next()) {                
                Zakat z = new Zakat();
                z.setKode(rs.getString("kode"));
                z.setNama(rs.getString("nama"));
                z.setJiwa(rs.getString("jiwa"));
                int id_jenis_zakat = rs.getInt("id_jenis_zakat");
                z.setId_jenis_zakat(id_jenis_zakat);
                z.setJenisZakat(getJenisZakatList().get(id_jenis_zakat));
                z.setJenis_nama(getJenisZakatList().get(id_jenis_zakat).getNama_jenis());
                z.setUser_nama(rs.getString("user_nama"));
                z.setNominal(rs.getDouble("nominal"));
                z.setId_user(rs.getInt("id_user"));
                z.setAlamat(rs.getString("alamat"));
                z.setTanggal(rs.getString("tanggal"));
                z.setId_zakat(rs.getInt("id_zakat"));
                list.add(z);
            }
            return new Response(true, list, "Data ditemukan!");
        } catch (Exception e) {
            Logger.getLogger(ServiceJdbc.class.getName()).log(Level.SEVERE, null, e);
            error = "get list gagal e:"+e.getMessage();
        }
        return new Response(false, null, error);
    }

    @Override
    public Response getLogList(String search, int id_user) {
        String error;
        try {
            String sql = "select * from log "
                    + "where id_user= ? and ( waktu like ? or text like ? ) "
                    + "order by waktu desc";
            statement = getConnection().prepareStatement(sql);
            statement.setInt(1, id_user);
            statement.setString(2, "%"+search+"%");
            statement.setString(3, "%"+search+"%");
            ResultSet rs = statement.executeQuery();
            List<Log> list = new ArrayList<>();
            while (rs.next()) {                
                Log l = new Log();
                l.setId_log(rs.getInt("id_log"));
                l.setId_user(rs.getInt("id_user"));
                l.setText(rs.getString("text"));
                l.setWaktu(rs.getString("waktu"));
                list.add(l);
            }
            return new Response(true, list, "Data ditemukan!");
        } catch (Exception e) {
            error = "get list gagal e:"+e.getMessage();
        }
        return new Response(false, null, error);
    }

    @Override
    public Response saveLog(Log log) {
        String error;
        try {
            String sql = "insert into log values(null, ?, ?, ?)";
            statement = getConnection().prepareStatement(sql);
            statement.setInt(1, log.getId_user());
            statement.setString(2, log.getText());
            statement.setString(3, log.getWaktu());
            statement.executeUpdate();
            return new Response(true, null, "save log berhasil");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            error = "save log gagal e:"+e.getMessage();
        }
        return new Response(false, null, error);
    }

    @Override
    public Response getLogNotif() {
        String error;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            String sql = "select * from log "
                    + "where id_user= ? and waktu like ?"
                    + "order by waktu desc limit 6";
            statement = getConnection().prepareStatement(sql);
            statement.setInt(1, Session.getUser().getId_user());
            statement.setString(2, "%"+df.format(new Date())+"%");
            ResultSet rs = statement.executeQuery();
            List<Log> list = new ArrayList<>();
            while (rs.next()) {                
                Log l = new Log();
                l.setId_log(rs.getInt("id_log"));
                l.setId_user(rs.getInt("id_user"));
                l.setText(rs.getString("text"));
                l.setWaktu(rs.getString("waktu"));
                list.add(l);
            }
            return new Response(true, list, "Data ditemukan!");
        } catch (Exception e) {
            error = "get list gagal e:"+e.getMessage();
        }
        return new Response(false, null, error);
    }

    @Override
    public Response getDashboardZakat() {
        String error;
        try {
            String sql = "select sum(nominal) as total, id_jenis_zakat from zakat "
                    + "group by id_jenis_zakat";
            statement = getConnection().prepareStatement(sql);
            ResultSet rs = statement.executeQuery();
            double[] total = new double[4];
            while (rs.next()) {     
                total[rs.getInt("id_jenis_zakat")] = rs.getDouble("total");
            }
            return new Response(true, total, "Data ditemukan!");
        } catch (Exception e) {
            Logger.getLogger(ServiceJdbc.class.getName()).log(Level.SEVERE, null, e);
            error = "get data gagal e:"+e.getMessage();
        }
        return new Response(false, null, error);
    }
    
    @Override
    public Response getDashboardChart() {
        String error;
        try {
            String sql = "select count(id_zakat) as total, id_jenis_zakat from zakat "
                    + "group by id_jenis_zakat";
            statement = getConnection().prepareStatement(sql);
            ResultSet rs = statement.executeQuery();
            double[] total = new double[4];
            while (rs.next()) {     
                total[rs.getInt("id_jenis_zakat")] = rs.getDouble("total");
            }
            return new Response(true, total, "Data ditemukan!");
        } catch (Exception e) {
            Logger.getLogger(ServiceJdbc.class.getName()).log(Level.SEVERE, null, e);
            error = "get data gagal e:"+e.getMessage();
        }
        return new Response(false, null, error);
    }

    @Override
    public Response getZakatKeluarList() {
        String error;
        try {
            String sql = "select z.*, m.nama as m_nama, u.nama as u_nama "
                    + "from zakat_keluar z "
                    + "join mustahiq m on z.id_mustahiq= m.id_mustahiq "
                    + "join user u on z.id_user=u.id_user "
                    + "order by z.tanggal desc";
            statement = getConnection().prepareStatement(sql);
            ResultSet rs = statement.executeQuery();
            List<ZakatKeluar> list = new ArrayList<>();
            Map<String, Object> map = new HashMap<>();
            double[] total = new double[2];
            while (rs.next()) {                
                ZakatKeluar z = new ZakatKeluar();
                z.setId_zakat_keluar(rs.getInt("id_zakat_keluar"));
                z.setId_user(rs.getInt("id_user"));
                z.setId_mustahiq(rs.getInt("id_mustahiq"));
                String is_money = rs.getString("is_money");
                z.setIs_money(is_money);
                z.setKode(rs.getString("kode"));
                double nominal = rs.getDouble("nominal");
                z.setNominal(nominal);
                z.setTanggal(rs.getString("tanggal"));
                z.setMustahiq_nama(rs.getString("m_nama"));
                z.setAmil_nama(rs.getString("u_nama"));
                if (is_money.equalsIgnoreCase("Y")) {
                    total[0] += nominal;
                }else{
                    total[1] += nominal;
                }
                list.add(z);
            }
            map.put("list", list);
            map.put("total", total);
            return new Response(true, map, "Data ditemukan!");
        } catch (Exception e) {
            Logger.getLogger(ServiceJdbc.class.getName()).log(Level.SEVERE, null, e);
            error = "get list gagal e:"+e.getMessage();
        }
        return new Response(false, null, error);
    }

    @Override
    public Response saveZakatKeluar(ZakatKeluar zakatKeluar) {
        String error;
        try {
            String sql = "insert into zakat_keluar values(null, ?, ?, ?, ?, ?, ?, ?)";
            statement = getConnection().prepareStatement(sql);
            statement.setString(1, zakatKeluar.getKode());
            statement.setInt(2, zakatKeluar.getId_mustahiq());
            statement.setInt(3, 0);
            statement.setDouble(4, zakatKeluar.getNominal());
            statement.setInt(5, zakatKeluar.getId_user());
            statement.setString(6, zakatKeluar.getIs_money());
            statement.setString(7, zakatKeluar.getTanggal());
            String msg =" menambah distribusi zakat  "
                        + "dengan kode "+zakatKeluar.getKode();
            statement.executeUpdate();
            saveLog(new Log(
                    Session.getUser().getId_user(), 
                    Session.getUser().getNama()+msg, 
                    sdf.format(new Date())
            ));
            return new Response(true, zakatKeluar, "save distribusi zakat berhasil");
        } catch (Exception e) {
            error = "save distribusi zakat gagal e:"+e.getMessage();
        }
        return new Response(false, null, error);
    }

    @Override
    public Response deleteZakatKeluar(int id_zakat_keluar, String kode) {
        String error;
        try {
            String sql = "delete from zakat_keluar where id_zakat_keluar=?";
            statement = getConnection().prepareStatement(sql);
            statement.setInt(1, id_zakat_keluar);
            statement.executeUpdate();
            saveLog(new Log(
                        Session.getUser().getId_user(), 
                        Session.getUser().getNama()+" menghapus Zakat keluar dengan nota "
                                +kode, 
                        sdf.format(new Date())
                ));
            return new Response(true, null, "Zakat keluar berhasil dihapus");
        } catch (Exception e) {
            error = "delete Zakat keluar gagal e:"+e.getMessage();
        }
        return new Response(false, null, error);
    }
}
