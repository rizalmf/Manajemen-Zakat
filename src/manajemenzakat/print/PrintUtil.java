/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manajemenzakat.print;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import manajemenzakat.model.Mustahiq;
import manajemenzakat.model.Zakat;
import manajemenzakat.model.ZakatKeluar;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 *
 * @author PKane_NS
 */
public class PrintUtil {
    public JasperPrint getLaporanDistribusiZakat(List<ZakatKeluar> laporans) throws Exception{
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("tanggalCetak", sdf.format(new Date()));
        List<ZakatKeluar> models= laporans;
        InputStream is = PrintUtil.class.getResourceAsStream("resource/laporan_distribusi.jasper");
        return JasperFillManager.fillReport(is, parameters, new JRBeanCollectionDataSource(models));
    }
    public JasperPrint getLaporanMustahiq(List<Mustahiq> laporans) throws Exception{
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("tanggalCetak", sdf.format(new Date()));
        for (int i = 0; i < laporans.size(); i++) {
            laporans.get(i).setNama_jenis(laporans.get(i).getJenisMustahiq().getNama_jenis());
        }
        List<Mustahiq> models= laporans;
        InputStream is = PrintUtil.class.getResourceAsStream("resource/laporan_mustahiq.jasper");
        return JasperFillManager.fillReport(is, parameters, new JRBeanCollectionDataSource(models));
    }
    public JasperPrint getInvoicePenerimaan(Zakat zakat) throws Exception{
        Map<String, Object> parameters = new HashMap<>();
        List<Zakat> models= new ArrayList<>();
        models.add(zakat);
        InputStream is = PrintUtil.class.getResourceAsStream("resource/invoice.jasper");
        return JasperFillManager.fillReport(is, parameters, new JRBeanCollectionDataSource(models));
    }
}
