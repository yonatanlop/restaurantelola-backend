import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.URI;
import java.util.concurrent.TimeUnit;

public class RestauranteLolaLauncher extends JFrame {
    private JTextArea logArea;
    private JButton startButton;
    private JButton stopButton;
    private JProgressBar progressBar;
    private Process backendProcess;
    private Process frontendProcess;
    
    public RestauranteLolaLauncher() {
        setTitle("Restaurante Doña Lola - Launcher");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        
        // Panel superior con logo y título
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(41, 128, 185));
        JLabel titleLabel = new JLabel("RESTAURANTE DOÑA LOLA - Sistema POS");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);
        
        // Área de log
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(logArea);
        add(scrollPane, BorderLayout.CENTER);
        
        // Panel inferior con botones y barra de progreso
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        bottomPanel.add(progressBar, BorderLayout.NORTH);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        startButton = new JButton("Iniciar Sistema");
        startButton.setFont(new Font("Arial", Font.BOLD, 14));
        startButton.setBackground(new Color(46, 204, 113));
        startButton.setForeground(Color.WHITE);
        startButton.addActionListener(e -> startSystem());
        
        stopButton = new JButton("Detener Sistema");
        stopButton.setFont(new Font("Arial", Font.BOLD, 14));
        stopButton.setBackground(new Color(231, 76, 60));
        stopButton.setForeground(Color.WHITE);
        stopButton.setEnabled(false);
        stopButton.addActionListener(e -> stopSystem());
        
        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);
        bottomPanel.add(buttonPanel, BorderLayout.CENTER);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void log(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }
    
    private void updateProgress(int value, String text) {
        SwingUtilities.invokeLater(() -> {
            progressBar.setValue(value);
            progressBar.setString(text);
        });
    }
    
    private void startSystem() {
        startButton.setEnabled(false);
        stopButton.setEnabled(true);
        logArea.setText("");
        
        new Thread(() -> {
            try {
                log("========================================");
                log("   INICIANDO SISTEMA");
                log("========================================");
                log("");
                
                // Verificar Java
                updateProgress(10, "Verificando Java...");
                log("[CHECK] Verificando Java...");
                if (!checkCommand("java -version")) {
                    log("[ERROR] Java no está instalado");
                    showError("Java no está instalado o no está en el PATH");
                    return;
                }
                log("  ✓ Java encontrado");
                
                // Verificar Node.js
                updateProgress(20, "Verificando Node.js...");
                log("[CHECK] Verificando Node.js...");
                if (!checkCommand("node -v")) {
                    log("[ERROR] Node.js no está instalado");
                    showError("Node.js no está instalado o no está en el PATH");
                    return;
                }
                log("  ✓ Node.js encontrado");
                
                // Iniciar Backend
                updateProgress(30, "Iniciando Backend...");
                log("");
                log("[1/3] Iniciando Backend (Spring Boot)...");
                backendProcess = startBackend();
                
                // Esperar backend
                updateProgress(50, "Esperando Backend...");
                log("      Esperando que el backend inicie...");
                if (waitForPort(8080, 60)) {
                    log("  ✓ Backend iniciado correctamente");
                } else {
                    log("  ✗ Backend no respondió a tiempo");
                }
                
                // Iniciar Frontend
                updateProgress(70, "Iniciando Frontend...");
                log("");
                log("[2/3] Iniciando Frontend (React)...");
                frontendProcess = startFrontend();
                
                // Esperar frontend
                updateProgress(85, "Esperando Frontend...");
                log("      Esperando que el frontend inicie...");
                if (waitForPort(3000, 30)) {
                    log("  ✓ Frontend iniciado correctamente");
                } else {
                    log("  ✗ Frontend no respondió a tiempo");
                }
                
                // Abrir navegador
                updateProgress(95, "Abriendo navegador...");
                log("");
                log("[3/3] Abriendo navegador...");
                Desktop.getDesktop().browse(new URI("http://localhost:3000"));
                
                updateProgress(100, "Sistema iniciado correctamente");
                log("");
                log("========================================");
                log("   SISTEMA INICIADO CORRECTAMENTE");
                log("========================================");
                log("");
                log("Backend:  http://localhost:8080");
                log("Frontend: http://localhost:3000");
                log("");
                log("El sistema está corriendo.");
                
            } catch (Exception e) {
                log("[ERROR] " + e.getMessage());
                e.printStackTrace();
                showError("Error al iniciar el sistema: " + e.getMessage());
            }
        }).start();
    }
    
    private void stopSystem() {
        log("");
        log("Deteniendo sistema...");
        
        if (backendProcess != null && backendProcess.isAlive()) {
            backendProcess.destroy();
            log("  ✓ Backend detenido");
        }
        
        if (frontendProcess != null && frontendProcess.isAlive()) {
            frontendProcess.destroy();
            log("  ✓ Frontend detenido");
        }
        
        log("Sistema detenido.");
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
        updateProgress(0, "Sistema detenido");
    }
    
    private boolean checkCommand(String command) {
        try {
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor(5, TimeUnit.SECONDS);
            return process.exitValue() == 0 || process.exitValue() == 1; // Java retorna 1 con -version
        } catch (Exception e) {
            return false;
        }
    }
    
    private Process startBackend() throws IOException {
        String os = System.getProperty("os.name").toLowerCase();
        ProcessBuilder pb;
        
        if (os.contains("win")) {
            pb = new ProcessBuilder("cmd", "/c", "mvn spring-boot:run");
        } else {
            pb = new ProcessBuilder("bash", "-c", "mvn spring-boot:run");
        }
        
        pb.directory(new File(System.getProperty("user.dir")));
        pb.redirectErrorStream(true);
        return pb.start();
    }
    
    private Process startFrontend() throws IOException {
        String os = System.getProperty("os.name").toLowerCase();
        ProcessBuilder pb;
        
        File frontendDir = new File(System.getProperty("user.dir"), "tialola-frontend");
        
        if (os.contains("win")) {
            pb = new ProcessBuilder("cmd", "/c", "npm run dev");
        } else {
            pb = new ProcessBuilder("bash", "-c", "npm run dev");
        }
        
        pb.directory(frontendDir);
        pb.redirectErrorStream(true);
        return pb.start();
    }
    
    private boolean waitForPort(int port, int timeoutSeconds) {
        for (int i = 0; i < timeoutSeconds; i++) {
            try {
                java.net.Socket socket = new java.net.Socket("localhost", port);
                socket.close();
                return true;
            } catch (IOException e) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    return false;
                }
            }
        }
        return false;
    }
    
    private void showError(String message) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
            startButton.setEnabled(true);
            stopButton.setEnabled(false);
        });
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            RestauranteLolaLauncher launcher = new RestauranteLolaLauncher();
            launcher.setVisible(true);
        });
    }
}
