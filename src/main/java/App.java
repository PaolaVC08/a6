import java.util.Locale;

/**
 * Programa 6 (PSP2.1): encuentra x tal que la integral de la t de 0 a x es p.
 *
 * Uso:  java App               -> corre los 3 casos de la Tabla 1 del kit
 *       java App p dof         -> busca x para ese p y esos grados de libertad
 *
 *   p     valor de la integral, entre 0 y 0.5 (usar punto decimal)
 *   dof   grados de libertad (entero >= 1)
 *
 * El error aceptable E va fijo en la constante ERROR.
 */
public class App {

    /** Error aceptable E (para la integral y para la busqueda). */
    static final double ERROR = 0.0000001;
    /** Tolerancia para comparar x contra el valor esperado de la Tabla 1 (5 decimales). */
    static final double TABLE_TOLERANCE = 0.00001;
    static final String DEFAULT_OUT_FILE = "resultados.txt";

    static final String USAGE =
            "Uso: java App [p dof]%n"
            + "  p     valor de la integral de 0 a x, entre 0 y 0.5 (ej. 0.2)%n"
            + "  dof   grados de libertad, entero >= 1 (ej. 6)%n"
            + "Sin parametros se ejecutan los 3 casos de la Tabla 1.%n";

    /** Casos de prueba de la Tabla 1: {p, dof, x esperado}. */
    static final double[][] TESTS = {
        {0.20, 6, 0.55338},
        {0.45, 15, 1.75305},
        {0.495, 4, 4.60409},
    };

    public static void main(String[] args) {
        int status = run(args, DEFAULT_OUT_FILE);
        if (status != 0) {
            System.exit(status);
        }
    }

    /** Ejecuta el programa y devuelve 0 si todo salio bien, 1 si hubo error de uso. */
    static int run(String[] args, String outFile) {
        try {
            if (args.length == 0) {
                runTable1(outFile);
            } else if (args.length == 2) {
                double p = Double.parseDouble(args[0]);
                int dof = Integer.parseInt(args[1]);
                runSingle(p, dof, outFile);
            } else {
                System.err.printf(USAGE);
                return 1;
            }
            return 0;
        } catch (IllegalArgumentException | IllegalStateException ex) {
            System.err.println("Error: " + ex.getMessage());
            System.err.printf(USAGE);
            return 1;
        }
    }

    /** Una busqueda, mostrando la tabla de intentos. */
    private static void runSingle(double p, int dof, String outFile) {
        SearchX search = new SearchX(p, dof, ERROR);
        search.search();
        String text = search.getProcedure()
                + String.format(Locale.US,
                        "%nRESULTADO: x = %.6f (integral de 0 a x = %.8f, %d intentos)%n",
                        search.getDblResultX(), search.getDblResultIntegral(),
                        search.getIntIterations());
        System.out.print(text);
        new Output().writeData(outFile, text);
        System.out.println("Resultados guardados en " + outFile);
    }

    /** Los 3 casos de la Tabla 1 con esperado, actual y estado. */
    private static void runTable1(String outFile) {
        StringBuilder text = new StringBuilder();
        text.append(String.format("%-8s %-5s %-15s %-15s %-8s%n",
                "p", "dof", "x esperado", "x actual", "Estado"));
        for (double[] t : TESTS) {
            SearchX search = new SearchX(t[0], (int) t[1], ERROR);
            search.search();
            double actual = search.getDblResultX();
            boolean ok = Math.abs(actual - t[2]) < TABLE_TOLERANCE;
            text.append(String.format(Locale.US, "%-8.3f %-5d %-15.5f %-15.6f %-8s%n",
                    t[0], (int) t[1], t[2], actual, ok ? "OK" : "FALLO"));
        }
        System.out.print(text);
        new Output().writeData(outFile, text.toString());
        System.out.println("Resultados guardados en " + outFile);
    }
}
