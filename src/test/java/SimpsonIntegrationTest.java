import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SimpsonIntegrationTest {

    /** Reproduce el ejemplo del kit (Programa 5): de 0 a 1.1 con 9 grados de libertad. */
    private SimpsonIntegration kitExample(int numSeg) {
        SimpsonIntegration s = new SimpsonIntegration();
        s.integrate(numSeg, 0.0, 1.1, 9);
        return s;
    }

    @Test
    void primeraPasadaConDiezSegmentosDa0_3500589() {
        assertEquals(0.3500589, kitExample(10).getDblFinalValue(), 1e-6);
    }

    @Test
    void segundaPasadaConVeinteSegmentosDa0_35005864() {
        assertEquals(0.35005864, kitExample(20).getDblFinalValue(), 1e-8);
    }

    @Test
    void anchoDeSegmentoEs0_11() {
        assertEquals(0.11, kitExample(10).getDblW(), 1e-12);
    }

    @Test
    void coeficienteDelKitEs0_388035() {
        assertEquals(0.388035, kitExample(10).getDblCoeff(), 1e-6);
    }

    @Test
    void exponenteConNueveDofEsMenosCinco() {
        assertEquals(-5.0, kitExample(10).getDblExponent(), 1e-12);
    }

    @Test
    void ultimoXiEsElLimiteSuperior() {
        assertEquals(1.1, kitExample(10).getDblTotXi()[10], 1e-12);
    }

    @Test
    void primeraBaseDelPuntoUnoEs1_00134() {
        assertEquals(1.00134, kitExample(10).getDblFirstBaseTerms()[1], 1e-5);
    }

    @Test
    void potenciaDelPuntoUnoEs0_9933() {
        assertEquals(0.9933, kitExample(10).getDblPowerTerms()[1], 1e-4);
    }

    @Test
    void alturaDeLaCurvaEnElPuntoUnoEs0_38544() {
        assertEquals(0.38544, kitExample(10).getDblFxi()[1], 1e-5);
    }

    @Test
    void terminoDelPuntoUnoEs0_05653() {
        assertEquals(0.05653, kitExample(10).getDblFinalTerms()[1], 1e-5);
    }

    @Test
    void multiplicadoresSiguenElPatronUnoCuatroDos() {
        SimpsonIntegration s = new SimpsonIntegration();
        s.integrate(6, 0.0, 1.0, 5);
        assertArrayEquals(new int[] {1, 4, 2, 4, 2, 4, 1}, s.getIntMultipliers());
    }

    @Test
    void integralHastaUnXMuyGrandeSeAcercaACeroPuntoCinco() {
        assertEquals(0.5, new SimpsonIntegration().integrate(2000, 0.0, 40.0, 5), 1e-3);
    }

    @Test
    void limiteSuperiorNegativoDaAreaNegativa() {
        SimpsonIntegration s = new SimpsonIntegration();
        assertEquals(-s.integrate(20, 0.0, 1.1, 9), s.integrate(20, 0.0, -1.1, 9), 1e-12);
    }

    @Test
    void integralDeCeroACeroEsCero() {
        assertEquals(0.0, new SimpsonIntegration().integrate(10, 0.0, 0.0, 9), 1e-15);
    }

    @Test
    void limitesInvertidosDanAreaNegativa() {
        SimpsonIntegration s = new SimpsonIntegration();
        assertEquals(-s.integrate(20, 0.0, 1.1, 9), s.integrate(20, 1.1, 0.0, 9), 1e-12);
    }

    @Test
    void conLimiteInferiorDistintoDeCeroElAnchoEsBMenosAEntreN() {
        SimpsonIntegration s = new SimpsonIntegration();
        s.integrate(4, 1.0, 3.0, 9);
        assertEquals(0.5, s.getDblW(), 1e-12);
    }

    @Test
    void conLimiteInferiorDistintoDeCeroXiEmpiezaEnA() {
        SimpsonIntegration s = new SimpsonIntegration();
        s.integrate(4, 1.0, 3.0, 9);
        assertArrayEquals(new double[] {1.0, 1.5, 2.0, 2.5, 3.0}, s.getDblTotXi(), 1e-12);
    }

    @Test
    void integralEntreDosLimitesEsLaDiferenciaDeIntegralesDesdeCero() {
        SimpsonIntegration s = new SimpsonIntegration();
        double from1to2 = s.integrate(20, 1.0, 2.0, 9);
        double zeroTo2 = s.integrate(40, 0.0, 2.0, 9);
        double zeroTo1 = s.integrate(20, 0.0, 1.0, 9);
        assertEquals(zeroTo2 - zeroTo1, from1to2, 1e-9);
    }

    @Test
    void numeroImparDeSegmentosLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> new SimpsonIntegration().integrate(3, 0.0, 1.0, 9));
    }

    @Test
    void ceroSegmentosLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> new SimpsonIntegration().integrate(0, 0.0, 1.0, 9));
    }

    @Test
    void ceroGradosDeLibertadLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> new SimpsonIntegration().integrate(10, 0.0, 1.0, 0));
    }

    @Test
    void procedimientoMuestraLaFormulaDeW() {
        assertTrue(kitExample(10).getProcedure().contains("W = (b - a) / num_seg"));
    }

    @Test
    void procedimientoMuestraLaSumaDeLosTerminos() {
        assertTrue(kitExample(10).getProcedure()
                .contains("p = suma de los terminos = 0.35005890"));
    }

    @Test
    void procedimientoOmiteLaTablaConMuchosSegmentos() {
        SimpsonIntegration s = new SimpsonIntegration();
        s.integrate(SimpsonIntegration.MAX_ROWS_IN_TABLE + 2, 0.0, 1.1, 9);
        assertTrue(s.getProcedure().contains("tabla omitida"));
    }
}
