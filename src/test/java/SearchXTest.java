import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SearchXTest {

    private static final double E = 0.0000001;
    private static final double TABLE_TOLERANCE = 0.00001;

    private static SearchX solved(double p, int dof) {
        SearchX search = new SearchX(p, dof, E);
        search.search();
        return search;
    }

    // ---- Tabla 1 del kit ----

    @Test
    void conP0_20Y6DofXEs0_55338() {
        assertEquals(0.55338, solved(0.20, 6).getDblResultX(), TABLE_TOLERANCE);
    }

    @Test
    void conP0_45Y15DofXEs1_75305() {
        assertEquals(1.75305, solved(0.45, 15).getDblResultX(), TABLE_TOLERANCE);
    }

    @Test
    void conP0_495Y4DofXEs4_60409() {
        assertEquals(4.60409, solved(0.495, 4).getDblResultX(), TABLE_TOLERANCE);
    }

    // ---- Resultado ----

    @Test
    void laIntegralFinalEstaDentroDelErrorAceptable() {
        SearchX search = solved(0.20, 6);
        assertTrue(Math.abs(search.getDblResultIntegral() - 0.20) < E);
    }

    @Test
    void elNumeroDeIntentosEsElNumeroDeFilas() {
        SearchX search = solved(0.20, 6);
        assertEquals(search.getIntIterations(), search.getTrialXs().size());
    }

    // ---- Reglas del algoritmo ----

    @Test
    void elPrimerIntentoUsaXIgualAUno() {
        assertEquals(1.0, solved(0.20, 6).getTrialXs().get(0), 1e-12);
    }

    @Test
    void elPrimerIntentoUsaDIgualAMedio() {
        assertEquals(0.5, solved(0.20, 6).getDs().get(0), 1e-12);
    }

    @Test
    void siLaIntegralEstaBajaSeSumaDAX() {
        // p = 0.45 con 15 dof: la integral de 0 a 1 es menor que 0.45, entonces x = 1 + 0.5
        assertEquals(1.5, solved(0.45, 15).getTrialXs().get(1), 1e-12);
    }

    @Test
    void siLaIntegralEstaAltaSeRestaDAX() {
        // p = 0.20 con 6 dof: la integral de 0 a 1 es mayor que 0.20, entonces x = 1 - 0.5
        assertEquals(0.5, solved(0.20, 6).getTrialXs().get(1), 1e-12);
    }

    @Test
    void siElSignoNoCambiaDNoCambia() {
        // p = 0.001: los dos primeros intentos estan altos (mismo signo)
        assertEquals(0.5, solved(0.001, 5).getDs().get(1), 1e-12);
    }

    @Test
    void siElSignoCambiaDSeDivideEntreDos() {
        // p = 0.20 con 6 dof: intento 1 alto, intento 2 bajo -> d = 0.5 / 2
        assertEquals(0.25, solved(0.20, 6).getDs().get(1), 1e-12);
    }

    @Test
    void laAccionRegistradaIndicaElCambioDeSigno() {
        assertTrue(solved(0.20, 6).getActions().get(1).contains("cambio de signo"));
    }

    @Test
    void conPMuyPequenaLaBusquedaPasaPorXIgualACero() {
        // x = 1 -> 0.5 -> 0.0: el kit advierte que x puede valer 0
        assertEquals(0.0, solved(0.001, 5).getTrialXs().get(2), 1e-12);
    }

    @Test
    void laIntegralEnXCeroEsCeroSinErrores() {
        assertEquals(0.0, new SearchX(0.001, 5, E).computeIntegral(0.0), 1e-15);
    }

    @Test
    void conPMuyPequenaLaBusquedaTermina() {
        assertTrue(solved(0.001, 5).getDblResultX() > 0);
    }

    @Test
    void elUltimoIntentoIndicaQueTermino() {
        SearchX search = solved(0.20, 6);
        int last = search.getActions().size() - 1;
        assertTrue(search.getActions().get(last).contains("terminamos"));
    }

    // ---- Pasos individuales ----

    @Test
    void elErrorEsLaIntegralMenosP() {
        assertEquals(0.05, new SearchX(0.20, 6, E).computeError(0.25), 1e-12);
    }

    @Test
    void unErrorMenorQueEEstaDentroDelError() {
        assertTrue(new SearchX(0.20, 6, E).isWithinError(E / 2));
    }

    @Test
    void unErrorNegativoPequenoTambienEstaDentroDelError() {
        assertTrue(new SearchX(0.20, 6, E).isWithinError(-E / 2));
    }

    @Test
    void unErrorMayorQueENoEstaDentroDelError() {
        assertFalse(new SearchX(0.20, 6, E).isWithinError(E * 2));
    }

    @Test
    void unErrorNegativoSignificaIntegralBajaConSignoMenosUno() {
        assertEquals(-1, new SearchX(0.20, 6, E).computeSign(-0.01));
    }

    @Test
    void unErrorPositivoSignificaIntegralAltaConSignoMasUno() {
        assertEquals(1, new SearchX(0.20, 6, E).computeSign(0.01));
    }

    @Test
    void elPrimerAjusteDeDNuncaLoDivide() {
        assertFalse(new SearchX(0.20, 6, E).adjustD(1));
    }

    // ---- Procedimiento ----

    @Test
    void elProcedimientoIncluyeElEncabezadoDeLaTabla() {
        assertTrue(solved(0.20, 6).getProcedure().contains("iter"));
    }

    @Test
    void elProcedimientoIncluyeLosDatos() {
        assertTrue(solved(0.20, 6).getProcedure().contains("DATOS: p = 0.2, dof = 6"));
    }

    // ---- Validaciones ----

    @Test
    void pIgualACeroLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> new SearchX(0.0, 6, E));
    }

    @Test
    void pNegativaLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> new SearchX(-0.1, 6, E));
    }

    @Test
    void pIgualACeroPuntoCincoLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> new SearchX(0.5, 6, E));
    }

    @Test
    void pMayorQueCeroPuntoCincoLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> new SearchX(0.7, 6, E));
    }

    @Test
    void ceroGradosDeLibertadLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> new SearchX(0.2, 0, E));
    }

    @Test
    void errorAceptableCeroLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> new SearchX(0.2, 6, 0.0));
    }

    @Test
    void sinConvergerEnElMaximoDeIntentosLanzaExcepcion() {
        // p muy cerca de 0.5 con 1 dof: x seria ~3183 y el kit avanza solo de 0.5 en 0.5
        SearchX search = new SearchX(0.4999, 1, E);
        assertThrows(IllegalStateException.class, search::search);
    }
}
