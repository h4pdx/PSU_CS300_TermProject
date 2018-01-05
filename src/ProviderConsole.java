package chocan;

import java.util.Scanner;

/**
 * Class that produces the provider console interface
 *
 * @author michael heyman
 * @version 1.0
 */

class ProviderConsole extends Console {
    private Provider provider;
    private Member member;

    ProviderConsole(Controller controller, Scanner in, Provider provider) {
        super(controller, in);

        if (provider == null) {
            throw new IllegalArgumentException("ProviderConsole: Provider can't be null");
        }

        this.provider = provider;
        menu();
        stopActivityTimer();
    }

    /**
     * Method that displays and interacts with menu-driven interfaces
     */
    protected void menu() {
        run(new MenuInterface() {
            @Override
            public void displayMenu() {
                System.out.println("\nProvider Console");
                System.out.println("\t1. Validate Member");
                System.out.println("\t2. Log Consultation");
                System.out.println("\t3. View Provider Directory");
                System.out.println("\t0. Exit Provider Console and Save Changes");
            }

            @Override
            public void executeCmd(int cmd) {
                switch (cmd) {
                    case 1:
                        int memberNumber = readPositiveInteger("Please input the member number: ", in);
                        validateMember(memberNumber);
                        break;
                    case 2:
                        logConsultation();
                        break;
                    case 3:
                        controller.displayAllProviders();
                        break;
                    case 0:
                        System.out.println("Exiting Provider Console.");
                        break;
                    default:
                        System.out.println("Invalid input!");
                        // maybe throw exception here and catch it in the caller
                        break;
                }
            }
        });
    }

    /**
     * Method that validates a member
     *
     * @param memberNumber integer matching member ID to validate
     */
    private void validateMember(int memberNumber) {
        Member member = controller.searchMember(memberNumber);

        if (member != null) {
            this.member = member;
            System.out.printf("Found member '%s'\n", member.getMemberName());
        } else {
            System.out.println("Member not found.");
        }
    }

    /**
     * Method that logs a consultation
     */
    private void logConsultation() {
        int serviceNumber = readPositiveInteger("Enter service number: ", in);
        Service service = controller.searchService(serviceNumber);

        if (service != null) {
            int month, day, year;
            String comment = "";

            while (member == null) {
                int memberNumber = readPositiveInteger("Please input the member number: ", in);
                validateMember(memberNumber);
            }

            month = readPositiveInteger("Please enter month of service: ", in);
            day = readPositiveInteger("Please enter day of service: ", in);
            year = readPositiveInteger("Please enter year of service: ", in);

            String answer;
            System.out.print("Would you like to add a comment? (Y/n): ");
            in.nextLine();
            answer = in.next();

            if (answer.equalsIgnoreCase("yes") || answer.equalsIgnoreCase("y")) {
                System.out.print("Please enter a service comment: ");
                in.nextLine();
                comment = in.nextLine();
            }

            controller.makeConsultation(service, provider, member, month, day, year, comment);
        }
        else {
            System.out.println("Service not found.");
        }
    }
}
