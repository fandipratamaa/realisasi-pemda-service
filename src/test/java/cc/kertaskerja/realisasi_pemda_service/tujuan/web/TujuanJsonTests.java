package cc.kertaskerja.realisasi_pemda_service.tujuan.web;

import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import cc.kertaskerja.realisasi_pemda_service.tujuan.domain.Tujuan;
import cc.kertaskerja.realisasi_pemda_service.tujuan.domain.TujuanStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.time.Instant;
import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class TujuanJsonTests {
    @Autowired
    private JacksonTester<Tujuan> json;

   @Test
   void testSerializeRealisasiTujuan() throws Exception {
var realisasiTujuan = new Tujuan(304L, "T1", "TUJ-1",
                "I1", "IND-1",
                "TAR-1", "10.0", 10.0, "%",
                "2025", "01", "Visi Misi 1", "(realisasi/target)*100", JenisRealisasi.NAIK, TujuanStatus.UNCHECKED, null, Instant.now(), Instant.now(), null, 1);
       var jsonContent = json.write(realisasiTujuan);
       assertThat(jsonContent).extractingJsonPathNumberValue("@.id")
               .isEqualTo(realisasiTujuan.id().intValue());
       assertThat(jsonContent).extractingJsonPathStringValue("@.status")
               .isEqualTo(realisasiTujuan.status().name());
       assertThat(jsonContent).extractingJsonPathStringValue("@.tujuanId")
               .isEqualTo(realisasiTujuan.tujuanId());
       assertThat(jsonContent).extractingJsonPathStringValue("@.tujuan")
               .isEqualTo(realisasiTujuan.tujuan());
       assertThat(jsonContent).extractingJsonPathStringValue("@.indikatorId")
               .isEqualTo(realisasiTujuan.indikatorId());
       assertThat(jsonContent).extractingJsonPathStringValue("@.indikator")
               .isEqualTo(realisasiTujuan.indikator());
       assertThat(jsonContent).extractingJsonPathStringValue("@.targetId")
               .isEqualTo(realisasiTujuan.targetId());
       assertThat(jsonContent).extractingJsonPathStringValue("@.target")
               .isEqualTo(realisasiTujuan.target());
       assertThat(jsonContent).extractingJsonPathNumberValue("@.realisasi")
               .isEqualTo(realisasiTujuan.realisasi());
       assertThat(jsonContent).extractingJsonPathStringValue("@.satuan")
               .isEqualTo(realisasiTujuan.satuan());
       assertThat(jsonContent).extractingJsonPathStringValue("@.jenisRealisasi")
               .isEqualTo(realisasiTujuan.jenisRealisasi().toString());
       assertThat(jsonContent).extractingJsonPathStringValue("@.status")
               .isEqualTo(realisasiTujuan.status().toString());
       assertThat(jsonContent).extractingJsonPathNumberValue("@.version")
               .isEqualTo(realisasiTujuan.version());
       assertThat(jsonContent).extractingJsonPathStringValue("@.createdDate")
               .isEqualTo(realisasiTujuan.createdDate().toString());
       assertThat(jsonContent).extractingJsonPathStringValue("@.lastModifiedDate")
               .isEqualTo(realisasiTujuan.lastModifiedDate().toString());
       assertThat(jsonContent).extractingJsonPathStringValue("@.capaian")
               .isEqualTo("100.00%");
   }
}
