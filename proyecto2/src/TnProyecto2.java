import java.io.*;
import java.util.*;
import java.util.regex.*;

public class TnProyecto2 {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java TnProyecto2 <input_file>");
            return;
        }

        String inputFile = args[0];
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            String line;
            StringBuilder formula = new StringBuilder("1");
            Stack<StringBuilder> loopStack = new Stack<>();
            Stack<String> loopVariables = new Stack<>();
            boolean inConditional = false;
            StringBuilder conditionalFormula = new StringBuilder();

            Pattern leaPattern = Pattern.compile("(?i)^lea");
            Pattern escPattern = Pattern.compile("(?i)^esc");
            Pattern paraPattern = Pattern.compile("(?i)^para");
            Pattern fparaPattern = Pattern.compile("(?i)^fpara");
            Pattern siPattern = Pattern.compile("(?i)^si");
            Pattern sinoPattern = Pattern.compile("(?i)^sino");
            Pattern fsiPattern = Pattern.compile("(?i)^fsi");
            Pattern assignPattern = Pattern.compile("^[a-zA-Z]+\\s*=\\s*.+");

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.equalsIgnoreCase("inicio") || line.equalsIgnoreCase("pare")) {
                    continue;
                }

                if (leaPattern.matcher(line).find() || escPattern.matcher(line).find() || assignPattern.matcher(line).find()) {
                    if (inConditional) {
                        conditionalFormula.append("1 + ");
                    } else if (!loopStack.isEmpty()) {
                        loopStack.peek().append("1 + ");
                    } else {
                        formula.append(" + 1");
                    }
                } else if (paraPattern.matcher(line).find()) {
                    String loopVar = line.split(" ")[1].split("=")[0];
                    loopVariables.push(loopVar);
                    StringBuilder loopFormula = new StringBuilder("2 + Sumatoria_{" + loopVar + "=1}^{n} (");
                    loopStack.push(loopFormula);
                } else if (fparaPattern.matcher(line).find()) {
                    String loopVar = loopVariables.pop();
                    StringBuilder loopFormula = loopStack.pop();
                    loopFormula.append("2)");
                    if (!loopStack.isEmpty()) {
                        loopStack.peek().append(loopFormula).append(" + ");
                    } else {
                        formula.append(" + ").append(loopFormula);
                    }
                } else if (siPattern.matcher(line).find()) {
                    inConditional = true;
                    conditionalFormula = new StringBuilder("1 + Max(");
                } else if (sinoPattern.matcher(line).find()) {
                    conditionalFormula.append(", ");
                } else if (fsiPattern.matcher(line).find()) {
                    inConditional = false;
                    conditionalFormula.append("1)");
                    if (!loopStack.isEmpty()) {
                        loopStack.peek().append(conditionalFormula).append(" + ");
                    } else {
                        formula.append(" + ").append(conditionalFormula);
                    }
                }
            }

            String finalFormula = formula.toString();
            if (finalFormula.endsWith(" + ")) {
                finalFormula = finalFormula.substring(0, finalFormula.length() - 3);
            }

            System.out.println("T(n) = " + finalFormula);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}