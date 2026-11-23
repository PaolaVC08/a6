import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Busca el valor de x tal que la integral de la distribucion t de 0 a x vale p.
 *
 * Algoritmo del kit (Programa 6):
 *   1. x = 1.0 y d = 0.5
 *   2. Integrar de 0 a x y calcular error = integral - p
 *   3. Si |error| < E, terminamos
 *   4. Si la integral es muy baja (error < 0):  x = x + d
 *      Si la integral es muy alta (error > 0):  x = x - d
 *   5. Regla para d: si el signo del error cambia respecto al intento anterior,
 *      d = d / 2; si el signo es el mismo, d no cambia
 *   6. Repetir desde el paso 2
 */
public class SearchX {

    static final double INITIAL_X = 1.0;
    static final double INITIAL_D = 0.5;
    static final int INITIAL_SEGMENTS = 10;
    static final int MAX_ITERATIONS = 200;

    private double dblP;
    private int intDOF;
    private double dblE;

    private double dblTrialX;
    private double dblD;
    private int intPreviousSign;      // 0 = todavia no hay intento anterior

    private double dblResultX;
    private double dblResultIntegral;
    private int intIterations;

    // Una entrada por intento, para mostrar la tabla del procedimiento
    private final List<Double> trialXs = new ArrayList<>();
    private final List<Double> integrals = new ArrayList<>();
    private final List<Double> errors = new ArrayList<>();
    private final List<Double> ds = new ArrayList<>();
    private final List<String> actions = new ArrayList<>();

    public SearchX(double dblP, int intDOF, double dblE) {
        if (dblP <= 0 || dblP >= 0.5) {
            throw new IllegalArgumentException(
                    "p debe estar entre 0 y 0.5 (sin incluirlos): " + dblP);
        }
        if (intDOF < 1) {
            throw new IllegalArgumentException("dof debe ser >= 1: " + intDOF);
        }
        if (dblE <= 0) {
            throw new IllegalArgumentException("E debe ser > 0: " + dblE);
        }
        this.dblP = dblP;
        this.intDOF = intDOF;
        this.dblE = dblE;
    }

    /** Ejecuta la busqueda completa y deja el resultado en getDblResultX(). */
    public void search() {
        trialXs.clear();
        integrals.clear();
        errors.clear();
        ds.clear();
        actions.clear();
        intIterations = 0;

        dblTrialX = INITIAL_X;
        dblD = INITIAL_D;
        intPreviousSign = 0;

        boolean finished = false;
        while (!finished) {
            intIterations++;
            if (intIterations > MAX_ITERATIONS) {
                throw new IllegalStateException(
                        "No convergio en " + MAX_ITERATIONS + " intentos");
            }

            double xUsed = dblTrialX;
            double integral = computeIntegral(xUsed);
            double error = computeError(integral);

            if (isWithinError(error)) {
                addRow(xUsed, integral, error, dblD, "error < E: terminamos");
                dblResultX = xUsed;
                dblResultIntegral = integral;
                finished = true;
            } else {
                int sign = computeSign(error);
                boolean halved = adjustD(sign);
                String action = "";
                if (halved) {
                    action = "cambio de signo, d = d/2. ";
                }
                if (sign < 0) {
                    action = action + "integral muy baja: x = x + d";
                } else {
                    action = action + "integral muy alta: x = x - d";
                }
                addRow(xUsed, integral, error, dblD, action);
                moveTrialX(sign);
                intPreviousSign = sign;
            }
        }
    }

    /** Integral de 0 a x de la t (con la regla de Simpson del Programa 5). Si x = 0 da 0. */
    public double computeIntegral(double x) {
        Logic logic = new Logic(INITIAL_SEGMENTS, dblE, intDOF, 0.0, x);
        logic.logic1a();
        return logic.getDblResult();
    }

    /** error = integral - p  (negativo: integral baja; positivo: integral alta). */
    public double computeError(double integral) {
        return integral - dblP;
    }

    /** Dentro del error aceptable si |error| < E. */
    public boolean isWithinError(double error) {
        return Math.abs(error) < dblE;
    }

    /** -1 si la integral esta muy baja, +1 si esta muy alta. */
    public int computeSign(double error) {
        if (error < 0) {
            return -1;
        }
        return 1;
    }

    /** Si el signo cambio respecto al intento anterior, d = d / 2. Devuelve true si cambio d. */
    public boolean adjustD(int sign) {
        if (intPreviousSign != 0 && sign != intPreviousSign) {
            dblD = dblD / 2;
            return true;
        }
        return false;
    }

    /** Integral baja: x = x + d. Integral alta: x = x - d. */
    public void moveTrialX(int sign) {
        if (sign < 0) {
            dblTrialX = dblTrialX + dblD;
        } else {
            dblTrialX = dblTrialX - dblD;
        }
    }

    private void addRow(double x, double integral, double error, double d, String action) {
        trialXs.add(x);
        integrals.add(integral);
        errors.add(error);
        ds.add(d);
        actions.add(action);
    }

    /** Texto con la tabla de todos los intentos (una fila por intento). */
    public String getProcedure() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(Locale.US, "DATOS: p = %s, dof = %d, E = %s%n",
                dblP, intDOF, dblE));
        sb.append(String.format(Locale.US, "Inicio: x = %s, d = %s%n%n", INITIAL_X, INITIAL_D));
        sb.append(String.format("%4s %12s %12s %13s %8s  %s%n",
                "iter", "x", "integral", "error", "d", "accion"));
        for (int i = 0; i < trialXs.size(); i++) {
            sb.append(String.format(Locale.US, "%4d %12.8f %12.8f %+13.8f %8.5f  %s%n",
                    i + 1, trialXs.get(i), integrals.get(i), errors.get(i),
                    ds.get(i), actions.get(i)));
        }
        return sb.toString();
    }

    public double getDblResultX() { return dblResultX; }
    public double getDblResultIntegral() { return dblResultIntegral; }
    public int getIntIterations() { return intIterations; }
    public List<Double> getTrialXs() { return trialXs; }
    public List<Double> getIntegrals() { return integrals; }
    public List<Double> getErrors() { return errors; }
    public List<Double> getDs() { return ds; }
    public List<String> getActions() { return actions; }
}
