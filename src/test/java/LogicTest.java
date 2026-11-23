import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class LogicTest {

    private static final double E = 0.00001;

    private static Logic solved(double lower, double upper, int dof) {
        Logic logic = new Logic(10, E, dof, lower, upper);
        logic.logic1a();
        return logic;
    }

    @Test
    void integralDeCeroA1_1Con9DofCoincideConElKit() {
        assertEquals(0.35006, solved(0.0, 1.1, 9).getDblResult(), 0.000005 + 1e-12);
    }

    @Test
    void integralDeCeroA1_1812Con10DofCoincideConElKit() {
        assertEquals(0.36757, solved(0.0, 1.1812, 10).getDblResult(), 0.000005 + 1e-12);
    }

    @Test
    void integralDeCeroA2_75Con30DofCoincideConElKit() {
        assertEquals(0.49500, solved(0.0, 2.750, 30).getDblResult(), 0.000005 + 1e-12);
    }

    @Test
    void ejemploDelKitTerminaConVeinteSegmentos() {
        assertEquals(20, solved(0.0, 1.1, 9).getIntFinalNumSeg());
    }

    @Test
    void ejemploDelKitDa0_35005864() {
        assertEquals(0.35005864, solved(0.0, 1.1, 9).getDblResult(), 1e-8);
    }

    @Test
    void integralDeCeroACeroEsCeroSinErrores() {
        assertEquals(0.0, solved(0.0, 0.0, 9).getDblResult(), 1e-15);
    }

    @Test
    void integralEntreDosLimitesEsLaDiferenciaDeLasIntegralesDesdeCero() {
        double to2 = solved(0.0, 2.0, 9).getDblResult();
        double to1 = solved(0.0, 1.0, 9).getDblResult();
        assertEquals(to2 - to1, solved(1.0, 2.0, 9).getDblResult(), 2 * E);
    }

    @Test
    void procedimientoMuestraLaIteracionUno() {
        assertTrue(solved(0.0, 1.1, 9).getProcedure().contains("Iteracion 1: num_seg = 10"));
    }

    @Test
    void procedimientoMuestraLaIteracionDos() {
        assertTrue(solved(0.0, 1.1, 9).getProcedure().contains("Iteracion 2: num_seg = 20"));
    }

    @Test
    void procedimientoIndicaCuandoTermina() {
        assertTrue(solved(0.0, 1.1, 9).getProcedure().contains("terminamos"));
    }

    @Test
    void procedimientoIndicaCuandoDuplicaSegmentos() {
        Logic logic = new Logic(2, 1e-9, 9, 0.0, 1.1);
        logic.logic1a();
        assertTrue(logic.getProcedure().contains("duplicamos num_seg"));
    }

    @Test
    void lineaDeReporteIncluyeLosLimites() {
        assertTrue(solved(0.0, 1.1, 9).getReportLine().contains("a = 0.0, b = 1.1"));
    }

    @Test
    void lineaDeReporteIncluyeElResultado() {
        assertTrue(solved(0.0, 1.1, 9).getReportLine().contains("0.35005864"));
    }

    @Test
    void saveReportEscribeElTextoEnElArchivo(@TempDir Path dir) throws Exception {
        Path file = dir.resolve("sub").resolve("out.txt");
        solved(0.0, 1.1, 9).saveReport(file.toString(), "hola");
        assertEquals("hola", Files.readString(file));
    }

    @Test
    void sinConvergerLanzaExcepcion() {
        Logic logic = new Logic(10, 1e-300, 9, 0.0, 1.1);
        assertThrows(IllegalStateException.class, logic::logic1a);
    }

    @Test
    void segmentosImparesLanzanExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> new Logic(9, E, 9, 0.0, 1.0));
    }

    @Test
    void ceroSegmentosLanzanExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> new Logic(0, E, 9, 0.0, 1.0));
    }

    @Test
    void errorCeroLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> new Logic(10, 0.0, 9, 0.0, 1.0));
    }

    @Test
    void ceroGradosDeLibertadLanzanExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> new Logic(10, E, 0, 0.0, 1.0));
    }
}
