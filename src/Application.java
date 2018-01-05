package chocan;

import java.util.Scanner;
import java.util.InputMismatchException;

/**
 * This class is the entry point for the Chocan Management System.
 * It instantiates the Controller that will be used to interact with the
 * other elements of the program.
 * <p>
 * Its purpose is to welcome and authenticate the user, and instantiate
 * appropriate console interfaces.
 *
 * @author michael heyman
 * @version 1.0
 */

class Application {
    private static final Scanner in = new Scanner(System.in);
    private static final Controller controller = new Controller();

    /**
     * Method that asks the user for a login ID and initiates
     * and terminates the session.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        System.out.println("\nWelcome to ChocAn Management System.\n");

        String answer;
        do {
            int loginID = readAuthToken("Please login with your access number: ", in);
            authenticate(loginID);

            System.out.print("Would you like to quit? (Y/n): ");
            answer = in.next();
        } while (answer.equalsIgnoreCase("no") || answer.equalsIgnoreCase("n"));

        System.out.println("\nThank you for using ChocAn Management System.\n");
        controller.saveData();
        in.close();
    }

    /**
     * Prints a statement to the console and returns a positive integer
     *
     * @param statement the string to display
     * @param in        the scanner object to read input from
     * @throws IllegalArgumentException if the user input is negative
     * @return      the positive integer input by the user
     */
    static int readAuthToken(String statement, Scanner in) {
        int cmd = -1;

        do {
            System.out.print(statement);

            try {
                cmd = in.nextInt();
                if (cmd < 0) {
                    throw new IllegalArgumentException("Only positive numbers are allowed.");
                }
            } catch (InputMismatchException e) {
                System.out.print("Error: That number either invalid or out of range.\n\n");
                in.nextLine();  // clears buffer
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage() + "\n");
                in.nextLine();  // clears buffer
            }
        } while (cmd < 0);

        return cmd;
    }

    /**
     * Receives a loginID and verifies if that ID matches a manager
     * or provider's record.
     * <p>
     * Instantiates a manager or provider console if the loginID is valid.
     *
     * @param loginID the integer to authenticate
     */
    static void authenticate(int loginID) {
        if (controller.isManager(loginID)) {
            new ManagerConsole(controller, in);
        }
        else if (controller.isProvider(loginID)) {
            Provider provider = controller.searchProvider(loginID);
            new ProviderConsole(controller, in, provider);
        }
        else {
            System.out.println("Invalid member.");
        }
    }
}
