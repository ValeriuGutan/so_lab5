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
        Scanner scanner = new Scanner(System.in);
        
        try {
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
            scanner.close();
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