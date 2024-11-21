import java.io.Console;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Scanner;

public class SystemControl {

    public static void main(String[] args) {
        String os = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        Scanner scanner = null;

        System.out.println("Sistemul de operare detectat: " + os);
        
        
        try {
            if (os.contains("mac")) {
                createMacStartupScript();
            } else if (os.contains("windows")) {
                createWindowsStartupScript();
            }
            
            scanner = new Scanner(System.in);
            
            if (os.contains("mac")) {
                Console console = System.console();
                if (console == null) {
                    System.err.println("Nu se poate accesa consola pentru citirea securizată a parolei!");
                    return;
                }
                System.out.print("Introduceți parola pentru Mac: ");
                char[] passwordChars = console.readPassword();
                String password = new String(passwordChars);
                java.util.Arrays.fill(passwordChars, ' ');
            }

            System.out.println("\nAlegeți acțiunea dorită:");
            System.out.println("1. Restart sistem");
            System.out.println("2. Oprire sistem");
            System.out.print("Introduceți opțiunea (1 sau 2): ");
            
            int choice = scanner.nextInt();
            
            String action = (choice == 1) ? "restart" : "shutdown";
            writeMessageToDesktop("Sistemul va executa " + action + " în câteva secunde.");
            openFileOnDesktop(os.contains("windows") ? "notepad" : "open");
            
            Thread.sleep(3000);
            
            switch (choice) {
                case 1:
                    System.out.println("Se execută restart...");
                    if (os.contains("mac")) {
                        restartMac();
                    } else if (os.contains("windows")) {
                        restartWindows();
                        
                    }
                    break;
                case 2:
                    System.out.println("Se execută oprirea...");
                    if (os.contains("mac")) {
                        shutdownMac();
                    } else if (os.contains("windows")) {
                        shutdownWindows();
                    }
                    break;
                default:
                    System.out.println("Opțiune invalidă!");
                    return;
            }
            
        } catch (Exception e) {
            System.err.println("A apărut o eroare: " + e.getMessage());
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
    }

    private static void createMacStartupScript() throws IOException {
        String userHome = System.getProperty("user.home");
        Path launchAgentsPath = Paths.get(userHome, "Library", "LaunchAgents");
        Path plistPath = launchAgentsPath.resolve("com.user.systemcontrol.plist");

        // Creăm directorul LaunchAgents dacă nu există
        if (!launchAgentsPath.toFile().exists()) {
            launchAgentsPath.toFile().mkdirs();
        }
        String plistContent = String.format("""
            <?xml version="1.0" encoding="UTF-8"?>
            <!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
            <plist version="1.0">
            <dict>
                <key>Label</key>
                <string>com.user.systemcontrol</string>
                <key>ProgramArguments</key>
                <array>
                    <string>java</string>
                    <string>-cp</string>
                    <string>%s</string>
                    <string>SystemControl</string>
                </array>
                <key>RunAtLoad</key>
                <true/>
            </dict>
            </plist>
            """, System.getProperty("user.dir"));

        try (FileWriter writer = new FileWriter(plistPath.toFile())) {
            writer.write(plistContent);
        }

        Runtime.getRuntime().exec("chmod 644 " + plistPath);
        Runtime.getRuntime().exec("launchctl load " + plistPath);
    }

    private static void createWindowsStartupScript() throws IOException {
        String userHome = System.getProperty("user.home");
        Path startupPath = Paths.get(userHome, "AppData", "Roaming", "Microsoft", "Windows", "Start Menu", "Programs", "Startup");
        Path batPath = startupPath.resolve("SystemControl.bat");


        String batContent = String.format("""
            @echo off
            java -cp "%s" SystemControl
            """, System.getProperty("user.dir"));

        try (FileWriter writer = new FileWriter(batPath.toFile())) {
            writer.write(batContent);
        }
    }

    private static void shutdownMac() throws IOException, InterruptedException {
        String command = "sudo shutdown -h now";
        Process process = Runtime.getRuntime().exec(new String[]{"bash", "-c", command});
        process.waitFor();
        System.out.println("Comanda de oprire Mac a fost executată: " + command);
    }


    private static void restartMac() throws IOException, InterruptedException {
        String command = "sudo shutdown -r now";
        
        Process process = Runtime.getRuntime().exec(new String[]{"bash", "-c", command});
        
        process.waitFor();
        System.out.println("Comanda de restart Mac a fost executată: " + command);
    }
    private static void shutdownWindows() throws IOException {
        String command = "shutdown /s /t 0";
        Runtime.getRuntime().exec(command);
        System.out.println("Comanda de oprire Windows a fost executată: " + command);
    }

        private static void writeMessageToDesktop(String message) throws IOException {
        String userHome = System.getProperty("user.home");
        Path desktopPath = Paths.get(userHome, "Desktop", "system_log.txt");

        try (FileWriter writer = new FileWriter(desktopPath.toFile())) {
            writer.write(message);
            System.out.println("Mesajul a fost scris în: " + desktopPath);
        }
    }

    private static void restartWindows() throws IOException {
        String command = "shutdown /r /t 0";
        Runtime.getRuntime().exec(command);
        System.out.println("Comanda de restart Windows a fost executată: " + command);
    }

    private static void openFileOnDesktop(String command) throws IOException {
        String userHome = System.getProperty("user.home");
        Path desktopPath = Paths.get(userHome, "Desktop", "system_log.txt");

        Runtime.getRuntime().exec(new String[]{command, desktopPath.toString()});
        System.out.println("Fișierul a fost deschis: " + desktopPath);
    }

}