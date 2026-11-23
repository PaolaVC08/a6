import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class AppTest {

    private static String outFile(Path dir) {
        return dir.resolve("res.txt").toString();
    }

    @Test
    void sinParametrosTerminaBien(@TempDir Path dir) {
        assertEquals(0, App.run(new String[] {}, outFile(dir)));
    }

    @Test
    void sinParametrosNoMarcaFallos(@TempDir Path dir) throws Exception {
        App.run(new String[] {}, outFile(dir));
        assertFalse(Files.readString(Path.of(outFile(dir))).contains("FALLO"));
    }

    @Test
    void sinParametrosMuestraLosTresCasosOk(@TempDir Path dir) throws Exception {
        App.run(new String[] {}, outFile(dir));
        String text = Files.readString(Path.of(outFile(dir)));
        assertEquals(3, text.split("OK", -1).length - 1);
    }

    @Test
    void conPYDofTerminaBien(@TempDir Path dir) {
        assertEquals(0, App.run(new String[] {"0.2", "6"}, outFile(dir)));
    }

    @Test
    void conPYDofEscribeElResultado(@TempDir Path dir) throws Exception {
        App.run(new String[] {"0.2", "6"}, outFile(dir));
        assertTrue(Files.readString(Path.of(outFile(dir))).contains("RESULTADO: x = 0.553381"));
    }

    @Test
    void conPYDofEscribeLaTablaDeIntentos(@TempDir Path dir) throws Exception {
        App.run(new String[] {"0.2", "6"}, outFile(dir));
        assertTrue(Files.readString(Path.of(outFile(dir))).contains("accion"));
    }

    @Test
    void unSoloParametroDaError(@TempDir Path dir) {
        assertEquals(1, App.run(new String[] {"0.2"}, outFile(dir)));
    }

    @Test
    void tresParametrosDanError(@TempDir Path dir) {
        assertEquals(1, App.run(new String[] {"0.2", "6", "0.1"}, outFile(dir)));
    }

    @Test
    void unPNoNumericoDaError(@TempDir Path dir) {
        assertEquals(1, App.run(new String[] {"abc", "6"}, outFile(dir)));
    }

    @Test
    void unDofDecimalDaError(@TempDir Path dir) {
        assertEquals(1, App.run(new String[] {"0.2", "6.5"}, outFile(dir)));
    }

    @Test
    void unPFueraDeRangoDaError(@TempDir Path dir) {
        assertEquals(1, App.run(new String[] {"0.7", "6"}, outFile(dir)));
    }

    @Test
    void unDofCeroDaError(@TempDir Path dir) {
        assertEquals(1, App.run(new String[] {"0.2", "0"}, outFile(dir)));
    }

    @Test
    void siNoConvergeDaError(@TempDir Path dir) {
        assertEquals(1, App.run(new String[] {"0.4999", "1"}, outFile(dir)));
    }

    @Test
    void mainConParametrosValidosCreaElArchivoPorDefecto() {
        App.main(new String[] {"0.2", "6"});
        assertTrue(Files.exists(Path.of(App.DEFAULT_OUT_FILE)));
    }
}
