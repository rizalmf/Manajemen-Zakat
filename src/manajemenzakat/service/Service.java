/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manajemenzakat.service;

import manajemenzakat.model.Log;
import manajemenzakat.model.Mustahiq;
import manajemenzakat.model.Response;
import manajemenzakat.model.User;
import manajemenzakat.model.Zakat;
import manajemenzakat.model.ZakatKeluar;

/**
 *
 * @author RIZAL
 */
public interface Service {
    Response create();
    Response login(User user);
    Response logout(User user);
    Response saveUser(User user);
    Response deleteUser(int id_user, String nama);
    Response getUserList(String search);
    Response close();
    
    Response getDashboardZakat();
    Response getDashboardChart();
    
    Response getJenisMustahiq();
    Response saveMustahiq(Mustahiq mustahiq);
    Response deleteMustahiq(int id_mustahiq, String nama);
    Response getMustahiqList(String search);
    
    Response getJenisZakat();
    Response saveZakat(Zakat zakat);
    Response deleteZakat(int id_zakat, String nama, String kode);
    Response getZakatList(String search);
    
    Response getZakatKeluarList();
    Response saveZakatKeluar(ZakatKeluar zakatKeluar);
    Response deleteZakatKeluar(int id_zakat_keluar, String kode);
        
    Response getLogList(String search, int id_user);
    Response saveLog(Log log);
    Response getLogNotif();
}
