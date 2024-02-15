import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.util.Scanner;

public class proyecto {

    public static String hash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes("UTF-8"));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Ingrese su nombre de usuario:");
        String username = scanner.nextLine();
        System.out.println("Ingrese su clave:");
        String password = scanner.nextLine();
        
        boolean userFound = false;
        String salt = "";
        String storedHashedPassword = "";
        
        File file = new File("C:\\Users\\BRAYAN MORENO\\Documentsusuarios.txt");
        try {
            // Verificar si el archivo existe
            if (!file.exists()) {
                System.out.println("El archivo usuarios.txt no existe. Se creará uno nuevo.");
                if (file.createNewFile()) {
                    System.out.println("Archivo usuarios.txt creado con éxito.");
                } else {
                    System.out.println("Error al crear el archivo usuarios.txt.");
                    System.exit(1); // Salir del programa si hay un error al crear el archivo
                }
            }
            
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3 && parts[0].trim().equals(username)) {
                    userFound = true;
                    salt = parts[1].trim(); // Assuming the salt is stored in the second column of the file
                    storedHashedPassword = parts[2].trim(); // Assuming the hashed password is stored in the third column of the file
                    break;
                }
            }
            br.close();
        } catch (IOException e) {
            System.out.println("Error al leer el archivo usuarios.txt: " + e.getMessage());
            System.exit(1); // Salir del programa si hay un error de lectura del archivo
        }
        
        if (userFound) {
            // Calcula el hash de la contraseña ingresada con la sal y compáralo con la contraseña almacenada
            String hashedPassword = hash(password + salt);
            System.out.println("Contraseña ingresada (con sal): " + hashedPassword);
            System.out.println("Contraseña almacenada: " + storedHashedPassword);
            if (hashedPassword.equals(storedHashedPassword)) {
                System.out.println("¡Usuario autorizado!");
                // Opción para cambiar la contraseña
                System.out.println("¿Desea cambiar su contraseña? (s/n)");
                String changePassword = scanner.nextLine();
                if (changePassword.equalsIgnoreCase("s")) {
                    System.out.println("Ingrese su nueva contraseña:");
                    String newPassword = scanner.nextLine();
                    // Actualiza la contraseña en el archivo
                    try {
                        FileWriter fileWriter = new FileWriter(file);
                        PrintWriter printWriter = new PrintWriter(fileWriter);
                        // Escribir el contenido anterior excepto la línea correspondiente al usuario actual
                        BufferedReader reader = new BufferedReader(new FileReader(file));
                        String currentLine;
                        while ((currentLine = reader.readLine()) != null) {
                            String[] parts = currentLine.split(",");
                            if (!parts[0].trim().equals(username)) {
                                printWriter.println(currentLine);
                            }
                        }
                        reader.close();
                        // Escribir la nueva línea con el usuario y la nueva contraseña
                        printWriter.println(username + "," + salt + "," + hash(newPassword + salt));
                        printWriter.close();
                        System.out.println("Contraseña actualizada correctamente.");
                    } catch (IOException ex) {
                        System.out.println("Error al actualizar la contraseña: " + ex.getMessage());
                    }
                }
            } else {
                System.out.println("¡Clave incorrecta! Usuario rechazado.");
            }
        } else {
            System.out.println("El usuario '" + username + "' no se encontró en el registro.");
            System.out.println("¿Desea agregarlo al registro? (s/n)");
            String respuesta = scanner.nextLine();
            if (respuesta.equalsIgnoreCase("s")) {
                try {
                    FileWriter fileWriter = new FileWriter(file, true);
                    PrintWriter printWriter = new PrintWriter(fileWriter);
                    printWriter.println(username + ",nueva_sal," + hash(password + "nueva_sal"));
                    printWriter.close();
                    System.out.println("Usuario agregado al registro correctamente.");
                } catch (IOException ex) {
                    System.out.println("Error al agregar el usuario al registro: " + ex.getMessage());
                }
            }
        }
        
        scanner.close();
    }
}
