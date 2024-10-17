import java.io.*;
import java.util.regex.*;

public class TnProyecto2 {

    public static void main(String[] args) {
        // Asegurarse de que se pasa el archivo como argumento
        if (args.length != 1) {
            System.out.println("Por favor, proporciona la ruta del archivo.");
            return;
        }

        int nivelAnidamiento = 0; // Nivel de anidamiento para los ciclos
        int operacionesDentroSi = 0;
        int operacionesDentroSino = 0;
        boolean enSi = false;
        boolean enSino = false;
        StringBuilder ecuacionFinal = new StringBuilder("T(n) = ");

        // Patrón para reconocer las líneas del archivo
        Pattern paraPattern = Pattern.compile("^para\\s+([a-zA-Z]+)=(-?\\d+),(-?\\w+),(\\+|\\-)\\d+$", Pattern.CASE_INSENSITIVE);
        Pattern fparaPattern = Pattern.compile("^fpara$", Pattern.CASE_INSENSITIVE);
        Pattern siPattern = Pattern.compile("^si\\s*\\((.*)\\)", Pattern.CASE_INSENSITIVE);
        Pattern sinoPattern = Pattern.compile("^sino$", Pattern.CASE_INSENSITIVE);
        Pattern fsiPattern = Pattern.compile("^fsi$", Pattern.CASE_INSENSITIVE);
        Pattern leaPattern = Pattern.compile("^lea\\s+\\w+\\(?(\\d*,?\\d*)\\)?$", Pattern.CASE_INSENSITIVE);
        Pattern escPattern = Pattern.compile("^esc\\s+\\w+\\(?(\\d*,?\\d*)\\)?$", Pattern.CASE_INSENSITIVE);
        Pattern asignacionPattern = Pattern.compile("^\\w+\\(?(\\d*,?\\d*)\\)?=\\w+\\(?(\\d*,?\\d*)\\)?[+\\-*]?", Pattern.CASE_INSENSITIVE);

        try (BufferedReader br = new BufferedReader(new FileReader(args[0]))) {
            String linea;

            while ((linea = br.readLine()) != null) {
                // Ignorar espacios en blanco y convertir todo a minúsculas
                linea = linea.trim().toLowerCase();

                // Detectar ciclo "para"
                Matcher paraMatcher = paraPattern.matcher(linea);
                if (paraMatcher.matches()) {
                    // Extraer los valores a, b para el ciclo "para"
                    String variable = paraMatcher.group(1);
                    String inicio = paraMatcher.group(2);
                    String fin = paraMatcher.group(3);

                    nivelAnidamiento++;
                    // Representación simbólica del ciclo "para"
                    ecuacionFinal.append("2 + Sumatoria_{")
                                  .append(variable)
                                  .append("=")
                                  .append(inicio)
                                  .append("}^{")
                                  .append(fin)
                                  .append("} (K + 2) ");
                    continue;
                }

                // Detectar fin de ciclo "fpara"
                if (fparaPattern.matcher(linea).matches()) {
                    nivelAnidamiento--;
                    continue;
                }

                // Detectar condicional "si"
                Matcher siMatcher = siPattern.matcher(linea);
                if (siMatcher.matches()) {
                    enSi = true;
                    ecuacionFinal.append("+ 1 + ( ");
                    operacionesDentroSi = 0; // Reiniciar las operaciones dentro del "si"
                    continue;
                }

                // Detectar "sino"
                if (sinoPattern.matcher(linea).matches()) {
                    enSi = false;
                    enSino = true;
                    ecuacionFinal.append(" ) Max( ");
                    operacionesDentroSino = 0; // Reiniciar las operaciones dentro del "sino"
                    continue;
                }

                // Detectar fin de condicional "fsi"
                if (fsiPattern.matcher(linea).matches()) {
                    enSino = false;
                    if (operacionesDentroSino > 0) {
                        ecuacionFinal.append("L = ").append(operacionesDentroSino).append(" )");
                    } else {
                        ecuacionFinal.append("K = ").append(operacionesDentroSi).append(" )");
                    }
                    continue;
                }

                // Detectar operaciones simples como "lea", "esc" o asignaciones
                if (leaPattern.matcher(linea).matches() || escPattern.matcher(linea).matches() || asignacionPattern.matcher(linea).matches()) {
                    if (enSi) {
                        operacionesDentroSi += 1;
                    } else if (enSino) {
                        operacionesDentroSino += 1;
                    }
                    ecuacionFinal.append("+ 1 ");
                    continue;
                }
            }

        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }

        // Mostrar la ecuación final en lugar del tiempo numérico
        System.out.println(ecuacionFinal.toString().trim());
    }
}
