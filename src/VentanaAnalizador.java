import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

public class VentanaAnalizador extends JFrame {

    private JTextArea txtCodigo;
    private JTable tablaErrores;
    private JTable tablaSimbolos;
    private JLabel lblMensajeError;
    private DefaultTableModel modeloErrores;
    private DefaultTableModel modeloSimbolos;
    private JButton btnAnalizar;

    public VentanaAnalizador() {
        setTitle("Analizador Semántico");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        initComponentes();
        setVisible(true);
    }

    private void initComponentes() {
        configurarLookAndFeel();
        configurarFuentes();
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblHeader = crearHeader();
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.add(lblHeader, BorderLayout.CENTER);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        txtCodigo = crearTextAreaCodigo();
        JScrollPane scrollCodigo = new JScrollPane(txtCodigo);
        scrollCodigo.setBorder(BorderFactory.createTitledBorder("Código Fuente"));
        
        JTabbedPane tabbedPane = crearTabbedPane();
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollCodigo, tabbedPane);
        splitPane.setDividerLocation(300);
        splitPane.setResizeWeight(0.5);
        splitPane.setDividerSize(5);
        mainPanel.add(splitPane, BorderLayout.CENTER);

        JPanel bottomPanel = crearPanelInferior();
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }

    private void configurarLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info: UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("No se pudo cargar Nimbus: " + e.getMessage());
        }
    }

    private void configurarFuentes() {
        UIManager.put("Label.font", new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("Button.font", new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("TextArea.font", new Font("Consolas", Font.PLAIN, 14));
        UIManager.put("Table.font", new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("Table.headerFont", new Font("Segoe UI", Font.BOLD, 14));
    }

    private JLabel crearHeader() {
        JLabel lblHeader = new JLabel("Analizador Semántico", SwingConstants.CENTER);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblHeader.setForeground(new Color(33, 150, 243));
        return lblHeader;
    }

    private JTextArea crearTextAreaCodigo() {
        JTextArea txtCodigo = new JTextArea();
        txtCodigo.setFont(new Font("Consolas", Font.PLAIN, 16));
        txtCodigo.setLineWrap(true);
        txtCodigo.setWrapStyleWord(true);
        return txtCodigo;
    }

    private JTabbedPane crearTabbedPane() {
        JTabbedPane tabbedPane = new JTabbedPane();

        modeloErrores = new DefaultTableModel(new Object[]{"Linea", "Error"}, 0);
        tablaErrores = new JTable(modeloErrores);
        JScrollPane scrollErrores = new JScrollPane(tablaErrores);
        scrollErrores.setBorder(BorderFactory.createEmptyBorder());
        tabbedPane.addTab("Errores", scrollErrores);

        modeloSimbolos = new DefaultTableModel(new Object[]{"Linea", "Tipo de Dato", "Variable", "Valor", "IDE"}, 0);
        tablaSimbolos = new JTable(modeloSimbolos);
        JScrollPane scrollSimbolos = new JScrollPane(tablaSimbolos);
        scrollSimbolos.setBorder(BorderFactory.createEmptyBorder());
        tabbedPane.addTab("Símbolos", scrollSimbolos);

        return tabbedPane;
    }

    private JPanel crearPanelInferior() {
        btnAnalizar = new JButton("Analizar");
        btnAnalizar.setBackground(new Color(33, 150, 243));
        btnAnalizar.setForeground(Color.WHITE);
        btnAnalizar.setFocusPainted(false);
        btnAnalizar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnAnalizar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                analizarCodigo();
            }
        });

        lblMensajeError = new JLabel("");
        lblMensajeError.setForeground(Color.RED);
        lblMensajeError.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.add(btnAnalizar);
        bottomPanel.add(lblMensajeError);

        return bottomPanel;
    }

    private void analizarCodigo() {
        // Limpiar resultados previos
        modeloErrores.setRowCount(0);
        modeloSimbolos.setRowCount(0);
        lblMensajeError.setText("");

        String codigo = txtCodigo.getText();
        
        // Validación previa antes de análisis
        if (codigo.trim().isEmpty()) {
            lblMensajeError.setText("Por favor ingresa código para analizar.");
            return;
        }

        try {
            AnalizadorSemantico.ResultadoAnalisis resultado = AnalizadorSemantico.analizar(codigo);

            // Rellenar la tabla de errores
            for (AnalizadorSemantico.ErrorInfo error : resultado.errores) {
                modeloErrores.addRow(new Object[]{error.linea, error.mensaje});
            }

            // Rellenar la tabla de símbolos
            for (AnalizadorSemantico.SymbolInfo simbolo : resultado.simbolos) {
                modeloSimbolos.addRow(new Object[]{
                    simbolo.linea, simbolo.tipoDato, simbolo.variable, simbolo.valor, simbolo.ide
                });
            }

            // Mostrar mensaje si existen errores semánticos
            if (!resultado.errores.isEmpty()) {
                lblMensajeError.setText("Se encontraron errores semánticos");
            }
        } catch (Exception e) {
            lblMensajeError.setText("Error en el análisis del código: " + e.getMessage());
        }
    }
}
