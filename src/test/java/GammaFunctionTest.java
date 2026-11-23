import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class GammaFunctionTest {

    private final GammaFunction gamma = new GammaFunction();

    @Test
    void gammaEnteroDeUnoEsUno() {
        assertEquals(1.0, gamma.computeIntGamma(1), 1e-12);
    }

    @Test
    void gammaEnteroDeDosEsUno() {
        assertEquals(1.0, gamma.computeIntGamma(2), 1e-12);
    }

    @Test
    void gammaEnteroDeCincoEs24() {
        assertEquals(24.0, gamma.computeIntGamma(5), 1e-12);
    }

    @Test
    void gammaEnteroDeCeroLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> gamma.computeIntGamma(0));
    }

    @Test
    void gammaDeUnMedioEsRaizDePi() {
        assertEquals(Math.sqrt(Math.PI), gamma.computeDblGamma(0.5), 1e-12);
    }

    @Test
    void gammaDeNueveMediosCoincideConElKit() {
        assertEquals(11.63173, gamma.computeDblGamma(4.5), 1e-5);
    }

    @Test
    void gammaDecimalDeCincoEs24() {
        assertEquals(24.0, gamma.computeDblGamma(5.0), 1e-12);
    }

    @Test
    void gammaDecimalDeUnoEsUno() {
        assertEquals(1.0, gamma.computeDblGamma(1.0), 1e-12);
    }

    @Test
    void gammaDecimalDeCeroLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> gamma.computeDblGamma(0.0));
    }

    @Test
    void gammaDecimalNegativoLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> gamma.computeDblGamma(-1.5));
    }

    @Test
    void gammaDecimalQueNoEsSemienteroLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> gamma.computeDblGamma(2.3));
    }

    @Test
    void gammaDecimalMenorQueUnoQueNoEsMedioLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> gamma.computeDblGamma(0.3));
    }

    @Test
    void getGammaValueGuardaElUltimoResultado() {
        gamma.computeIntGamma(5);
        assertEquals(24.0, gamma.getGammaValue(), 1e-12);
    }
}
