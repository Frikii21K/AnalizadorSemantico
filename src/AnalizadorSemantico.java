import java.util.*;
import java.util.regex.*;

public class AnalizadorSemantico {

    // Clase que contiene el resultado del análisis
    public static class ResultadoAnalisis {
        public List<ErrorInfo> errores;
        public List<SymbolInfo> simbolos;
        
        public ResultadoAnalisis() {
            errores = new ArrayList<>();
            simbolos = new ArrayList<>();
        }
    }

    // Clase para representar un error encontrado
    public static class ErrorInfo {
        public int linea;
        public String mensaje;
        
        public ErrorInfo(int linea, String mensaje) {
            this.linea = linea;
            this.mensaje = mensaje;
        }
    }

    // Clase para representar un símbolo (variable)
    public static class SymbolInfo {
        public int linea;
        public String tipoDato;
        public String variable;
        public String valor;
        public String ide; 
        public String alcance; 

        public SymbolInfo(int linea, String tipoDato, String variable, String valor, String ide, String alcance) {
            this.linea = linea;
            this.tipoDato = tipoDato;
            this.variable = variable;
            this.valor = valor;
            this.ide = ide;
            this.alcance = alcance;
        }
    }

    // Método de análisis
    public static ResultadoAnalisis analizar(String codigo) {
        ResultadoAnalisis resultado = new ResultadoAnalisis();
        Map<String, SymbolInfo> tablaSimbolos = new HashMap<>();
        
        String[] lineas = codigo.split("\\n");
        int numLinea = 1;
        for (String linea : lineas) {
            String lineaTrim = linea.trim();
            if (lineaTrim.isEmpty()) {
                numLinea++;
                continue;
            }
            
            if (!lineaTrim.endsWith(";")) {
                resultado.errores.add(new ErrorInfo(numLinea, "Falta punto y coma al final de la línea"));
                numLinea++;
                continue;
            }
            
            String sinPuntoYComa = lineaTrim.substring(0, lineaTrim.length() - 1).trim();
            
            if (sinPuntoYComa.startsWith("int ")) {
                analizarInt(sinPuntoYComa, numLinea, tablaSimbolos, resultado);
            } else if (sinPuntoYComa.startsWith("float ")) {
                analizarFloat(sinPuntoYComa, numLinea, tablaSimbolos, resultado);
            } else if (sinPuntoYComa.startsWith("String ")) {
                analizarString(sinPuntoYComa, numLinea, tablaSimbolos, resultado);
            } else if (sinPuntoYComa.startsWith("boolean ")) {
                analizarBoolean(sinPuntoYComa, numLinea, tablaSimbolos, resultado);
            } else if (sinPuntoYComa.startsWith("char ")) {
                analizarChar(sinPuntoYComa, numLinea, tablaSimbolos, resultado);
            } else if (sinPuntoYComa.startsWith("double ")) {
                analizarDouble(sinPuntoYComa, numLinea, tablaSimbolos, resultado);
            } else {
                resultado.errores.add(new ErrorInfo(numLinea, "Tipo de dato no soportado o sintaxis incorrecta"));
            }
            
            numLinea++;
        }
        return resultado;
    }

    // Métodos para analizar diferentes tipos de datos
    private static void analizarInt(String sinPuntoYComa, int numLinea, Map<String, SymbolInfo> tablaSimbolos, ResultadoAnalisis resultado) {
        Pattern p = Pattern.compile("^int\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*=\\s*([+-]?\\d+)$");
        Matcher m = p.matcher(sinPuntoYComa);
        if (m.matches()) {
            String var = m.group(1);
            String valor = m.group(2);
            if (tablaSimbolos.containsKey(var)) {
                resultado.errores.add(new ErrorInfo(numLinea, "Variable duplicada: " + var));
            } else {
                String ide = var + "_" + numLinea;
                SymbolInfo simbolo = new SymbolInfo(numLinea, "int", var, valor, ide, "local");
                resultado.simbolos.add(simbolo);
                tablaSimbolos.put(var, simbolo);
            }
        } else {
            resultado.errores.add(new ErrorInfo(numLinea, "El valor asignado a una variable de tipo int no es un entero válido"));
        }
    }

    private static void analizarFloat(String sinPuntoYComa, int numLinea, Map<String, SymbolInfo> tablaSimbolos, ResultadoAnalisis resultado) {
        Pattern p = Pattern.compile("^float\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*=\\s*([+-]?\\d+(\\.\\d+)?([eE][+-]?\\d+)?)$");
        Matcher m = p.matcher(sinPuntoYComa);
        if (m.matches()) {
            String var = m.group(1);
            String valor = m.group(2);
            if (tablaSimbolos.containsKey(var)) {
                resultado.errores.add(new ErrorInfo(numLinea, "Variable duplicada: " + var));
            } else {
                String ide = var + "_" + numLinea;
                SymbolInfo simbolo = new SymbolInfo(numLinea, "float", var, valor, ide, "local");
                resultado.simbolos.add(simbolo);
                tablaSimbolos.put(var, simbolo);
            }
        } else {
            resultado.errores.add(new ErrorInfo(numLinea, "El valor asignado a una variable de tipo float no es un número flotante válido"));
        }
    }

    private static void analizarString(String sinPuntoYComa, int numLinea, Map<String, SymbolInfo> tablaSimbolos, ResultadoAnalisis resultado) {
        Pattern p = Pattern.compile("^String\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*=\\s*\"(.*)\"$");
        Matcher m = p.matcher(sinPuntoYComa);
        if (m.matches()) {
            String var = m.group(1);
            String valor = m.group(2);
            if (tablaSimbolos.containsKey(var)) {
                resultado.errores.add(new ErrorInfo(numLinea, "Variable duplicada: " + var));
            } else {
                String ide = var + "_" + numLinea;
                SymbolInfo simbolo = new SymbolInfo(numLinea, "String", var, valor, ide, "local");
                resultado.simbolos.add(simbolo);
                tablaSimbolos.put(var, simbolo);
            }
        } else {
            resultado.errores.add(new ErrorInfo(numLinea, "El valor asignado a una variable de tipo String debe estar entre comillas dobles"));
        }
    }

    private static void analizarBoolean(String sinPuntoYComa, int numLinea, Map<String, SymbolInfo> tablaSimbolos, ResultadoAnalisis resultado) {
        Pattern p = Pattern.compile("^boolean\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*=\\s*(true|false)$");
        Matcher m = p.matcher(sinPuntoYComa);
        if (m.matches()) {
            String var = m.group(1);
            String valor = m.group(2);
            if (tablaSimbolos.containsKey(var)) {
                resultado.errores.add(new ErrorInfo(numLinea, "Variable duplicada: " + var));
            } else {
                String ide = var + "_" + numLinea;
                SymbolInfo simbolo = new SymbolInfo(numLinea, "boolean", var, valor, ide, "local");
                resultado.simbolos.add(simbolo);
                tablaSimbolos.put(var, simbolo);
            }
        } else {
            resultado.errores.add(new ErrorInfo(numLinea, "El valor asignado a una variable de tipo boolean debe ser 'true' o 'false'"));
        }
    }

    private static void analizarChar(String sinPuntoYComa, int numLinea, Map<String, SymbolInfo> tablaSimbolos, ResultadoAnalisis resultado) {
         Pattern p = Pattern.compile("^char\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*=\\s*'([^']{1})'$");
        Matcher m = p.matcher(sinPuntoYComa);
        if (m.matches()) {
            String var = m.group(1);
            String valor = m.group(2);
            if (tablaSimbolos.containsKey(var)) {
                resultado.errores.add(new ErrorInfo(numLinea, "Variable duplicada: " + var));
            } else {
                String ide = var + "_" + numLinea;
                SymbolInfo simbolo = new SymbolInfo(numLinea, "char", var, valor, ide, "local");
                resultado.simbolos.add(simbolo);
                tablaSimbolos.put(var, simbolo);
            }
        } else {
            resultado.errores.add(new ErrorInfo(numLinea, "El valor asignado a una variable de tipo char debe ser un solo carácter entre comillas simples"));
        }
    }
    

    private static void analizarDouble(String sinPuntoYComa, int numLinea, Map<String, SymbolInfo> tablaSimbolos, ResultadoAnalisis resultado) {
        Pattern p = Pattern.compile("^double\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*=\\s*([+-]?\\d+(\\.\\d+)?([eE][+-]?\\d+)?)$");
        Matcher m = p.matcher(sinPuntoYComa);
        if (m.matches()) {
            String var = m.group(1);
            String valor = m.group(2);
            if (tablaSimbolos.containsKey(var)) {
                resultado.errores.add(new ErrorInfo(numLinea, "Variable duplicada: " + var));
            } else {
                String ide = var + "_" + numLinea;
                SymbolInfo simbolo = new SymbolInfo(numLinea, "double", var, valor, ide, "local");
                resultado.simbolos.add(simbolo);
                tablaSimbolos.put(var, simbolo);
            }
        } else {
            resultado.errores.add(new ErrorInfo(numLinea, "El valor asignado a una variable de tipo double no es un número flotante válido"));
        }
    }

    public static void main(String[] args) {
        String codigo = "int a = 10;\nfloat b = 12.5;\nboolean isActive = true;\nchar letra = 'A';\nString texto = \"Hello World\";";
        ResultadoAnalisis resultado = analizar(codigo);
        
        System.out.println("Errores encontrados:");
        for (ErrorInfo error : resultado.errores) {
            System.out.println("Línea " + error.linea + ": " + error.mensaje);
        }
        
        System.out.println("\nSímbolos encontrados:");
        for (SymbolInfo simbolo : resultado.simbolos) {
            System.out.println("Variable: " + simbolo.variable + ", Tipo: " + simbolo.tipoDato + ", Valor: " + simbolo.valor);
        }
    }
}
