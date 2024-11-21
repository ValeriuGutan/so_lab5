import java.io.Console;
import java.io.IOException;
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
            
            
            Thread.sleep(2000);
            
            switch (choice) {
                case 1:
                    System.out.println("Se execută restart...");
                    if (os.contains("mac")) {
                        restartMac();
                    }
                    break;
                case 2:
                    System.out.println("Se execută oprirea...");
                    if (os.contains("mac")) {
                        shutdownMac();
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


}