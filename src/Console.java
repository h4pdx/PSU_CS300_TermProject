package chocan;

import java.util.Scanner;
import java.util.InputMismatchException;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Abstract class models the concept of an interactive console
 *
 * @author michael heyman
 * @version 1.0
 */

abstract class Console {
    static final int ACTIVITY_TIMEOUT_DELAY = 120;
    private static final Logger LOGGER = Logger.getLogger(Console.class.getName());
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    static ScheduledFuture<?> timer;
    final Controller controller;
    final Scanner in;

    Console(Controller controller, Scanner in) throws IllegalArgumentException {
        this.controller = controller;
        this.in = in;

        if (in == null) {
            throw new IllegalArgumentException("Can't instantiate Console without a Scanner.");
        }

        startActivityTimer();
    }

    protected void finalize() {
        scheduler.shutdown();
        stopActivityTimer();
    }

    protected abstract void menu();

    /**
     * Method that starts the timer used to ensure authentication timeout
     */
    private void startActivityTimer() {
        LOGGER.log(Level.ALL, "Starting timer");
        timer = scheduler.schedule(this::disconnect, ACTIVITY_TIMEOUT_DELAY, TimeUnit.SECONDS);
    }

    /**
     * Method that stops the timer used to ensure authentication timeout
     */
    void stopActivityTimer() {
        if (timer != null) {
            LOGGER.log(Level.ALL, "Stopping timer");
            timer.cancel(true);
        } else {
            LOGGER.log(Level.ALL, "Attempted to stop non-existing timer");
        }
    }

    /**
     * Method that stops the timer and notifies the user of their authentication status
     */
    private void disconnect() {
        timer.cancel(true); // helps guarantee isCancelled and isDone in testing
        System.out.println("\nYou have been logged out due to inactivity.");
    }

    /**
     * Method that displays a menu-driven interface and accepts input from the user
     *
     * @param menu MenuInterface that will be used to display a menu and execute commands
     * @see MenuInterface
     */
    void run(MenuInterface menu) {
        int cmd;

        do {
            menu.displayMenu();
            cmd = readPositiveInteger("Please select one of the options: ", in);
            stopActivityTimer();
            startActivityTimer();
            in.nextLine();

            menu.executeCmd(cmd);
        } while (cmd != 0);
    }

    /**
     *
     * @param statement the string to display
     * @param in        the scanner object to read input from
     * @throws IllegalArgumentException if the user input is negative
     * @return          the positive integer input by the user
     */
    int readPositiveInteger(String statement, Scanner in) {
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
                System.out.println("Error: " + e.getMessage());
            }
        } while (cmd < 0);

        return cmd;
    }
}