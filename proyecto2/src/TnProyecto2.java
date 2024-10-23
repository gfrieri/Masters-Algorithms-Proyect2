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
            StringBuilder formula = new StringBuilder();
            Stack<StringBuilder> loopStack = new Stack<>();
            Stack<String> loopVariables = new Stack<>();
            boolean inConditional = false;
            boolean hasElse = false;
            StringBuilder conditionalFormula = new StringBuilder();
            int siCount = 0;
            int sinoCount = 0;

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

                if (sinoPattern.matcher(line).find()) {
                    hasElse = true;
                    sinoCount = 0;
                } else if (siPattern.matcher(line).find()) {
                    inConditional = true;
                    hasElse = false;
                    siCount = 0;
                } else if (fsiPattern.matcher(line).find()) {
                    inConditional = false;
                    if (hasElse) {
                        conditionalFormula.append("Max(").append(siCount).append(", ").append(sinoCount).append(")");
                    } else {
                        conditionalFormula.append(siCount);
                    }
                    if (!loopStack.isEmpty()) {
                        loopStack.peek().append(" + ").append(conditionalFormula).append(" + ");
                    } else {
                        if (formula.length() > 0) {
                            formula.append(" + ");
                        }
                        formula.append(conditionalFormula);
                    }
                    hasElse = false;
                }

                else if (leaPattern.matcher(line).find() || escPattern.matcher(line).find() || assignPattern.matcher(line).find()) {
                    if (inConditional) {
                        if (!hasElse) {
                            siCount++;
                        } else {
                            sinoCount++;
                        }
                    } else if (!loopStack.isEmpty()) {
                        loopStack.peek().append("1 + ");
                    } else {
                        if (formula.length() > 0) {
                            formula.append(" + ");
                        }
                        formula.append("1");
                    }
                }

                else if (paraPattern.matcher(line).find()) {
                    String loopVar = line.split(" ")[1].split("=")[0];
                    loopVariables.push(loopVar);
                    StringBuilder loopFormula = new StringBuilder("2 + Sumatoria_{" + loopVar + "=1}^{n} (");
                    loopStack.push(loopFormula);
                } else if (fparaPattern.matcher(line).find()) {
                    String loopVar = loopVariables.pop();
                    StringBuilder loopFormula = loopStack.pop();
                    
                    if (siCount > 0 || sinoCount > 0) {
                        loopFormula.append("1 + ");
                    }
                    
                    loopFormula.append("2)");
                    if (!loopStack.isEmpty()) {
                        loopStack.peek().append(loopFormula).append(" + ");
                    } else {
                        if (formula.length() > 0) {
                            formula.append(" + ");
                        }
                        formula.append(loopFormula);
                    }
                    
                    siCount = 0;
                    sinoCount = 0;
                }
            }

            String finalFormula = formula.toString().replaceAll("\\( \\+ 1", "(1");
            if (finalFormula.endsWith(" + ")) {
                finalFormula = finalFormula.substring(0, finalFormula.length() - 3);
            }

            finalFormula = finalFormula.replaceAll("([^+])Max\\(", "$1 + Max(");

            System.out.println("T(n) = " + finalFormula);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
