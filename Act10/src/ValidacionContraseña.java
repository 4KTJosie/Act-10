import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class ValidacionContraseña {
    private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ExecutorService executorService = Executors.newFixedThreadPool(5); // Configura el número máximo de hilos concurrentes

        System.out.print("Ingrese la ruta del archivo de registro: ");
        String logFilePath = scanner.nextLine();

        while (true) {
            System.out.print("Ingrese una contraseña (o 'exit' para salir): ");
            String password = scanner.nextLine();

            if (password.equalsIgnoreCase("exit")) {
                break;
            }

            executorService.submit(() -> {
                boolean isValid = validatePassword(password);
                logPasswordValidationResult(logFilePath, password, isValid);
            });
        }

        executorService.shutdown();
        try {
            executorService.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        scanner.close();
    }

    private static boolean validatePassword(String password) {
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    private static void logPasswordValidationResult(String logFilePath, String password, boolean isValid) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFilePath, true))) {
            String result = isValid ? "Válida" : "Inválida";
            writer.write(String.format("Contraseña: %s, Resultado: %s%n", password, result));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}