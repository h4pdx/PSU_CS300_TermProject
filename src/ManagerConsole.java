package chocan;

import java.util.Scanner;

/**
 * Class that produces the manager console interface
 *
 * @author michael heyman
 * @version 1.0
 */

class ManagerConsole extends Console {

    ManagerConsole(Controller controller, Scanner in) {
        super(controller, in);
        menu();
        stopActivityTimer();
    }

    /**
     * Method that displays and interacts with menu-driven interfaces.
     */
    protected void menu() {
        run(new MenuInterface() {
            @Override
            public void displayMenu() {
                System.out.println("\nManager Console");
                System.out.println("\t1. Manage Members");
                System.out.println("\t2. Manage Providers");
                System.out.println("\t3. Generate Reports");
                System.out.println("\t0. Exit Manager Console and Save Changes");
            }

            @Override
            public void executeCmd(int cmd) {
                switch (cmd) {
                    case 1:
                        run(new MenuInterface() {
                            @Override
                            public void displayMenu() {
                                System.out.println("\nManage Members");
                                System.out.println("\t1. Add Member");
                                System.out.println("\t2. Remove Member");
                                System.out.println("\t3. Update Member");
                                System.out.println("\t0. Exit");
                            }

                            @Override
                            public void executeCmd(int cmd) {
                                switch (cmd) {
                                    case 1:
                                        addMember();
                                        break;
                                    case 2:
                                        removeMember();
                                        break;
                                    case 3:
                                        updateMember();
                                        break;
                                    case 0:
                                        break;
                                    default:
                                        System.out.println("Invalid input!");
                                        break;
                                }
                            }
                        });

                        break;
                    case 2:
                        run(new MenuInterface() {
                            @Override
                            public void displayMenu() {
                                System.out.println("\nManage Providers");
                                System.out.println("\t1. Add Provider");
                                System.out.println("\t2. Remove Provider");
                                System.out.println("\t3. Update Provider");
                                System.out.println("\t0. Exit");
                            }

                            @Override
                            public void executeCmd(int cmd) {
                                switch (cmd) {
                                    case 1:
                                        addProvider();
                                        break;
                                    case 2:
                                        removeProvider();
                                        break;
                                    case 3:
                                        updateProvider();
                                        break;
                                    case 0:
                                        break;
                                    default:
                                        System.out.println("Invalid input!");
                                        break;
                                }
                            }
                        });
                        break;
                    case 3:
                        run(new MenuInterface() {
                            @Override
                            public void displayMenu() {
                                System.out.println("\nGenerate Reports");
                                System.out.println("\t1. Make Provider and EFT Report");
                                System.out.println("\t2. Make Member Report");
                                System.out.println("\t3. Make Accounts Payable Report");
                                System.out.println("\t0. Exit");
                            }

                            @Override
                            public void executeCmd(int cmd) {
                                switch (cmd) {
                                    case 1:
                                        controller.makeProviderAndEFTReport();
                                        break;
                                    case 2:
                                        controller.makeMemberReport();
                                        break;
                                    case 3:
                                        controller.makeManagerReport();
                                        break;
                                    case 0:
                                        break;
                                    default:
                                        System.out.println("Invalid input!");
                                        break;
                                }
                            }
                        });
                        break;
                    case 0:
                        System.out.println("Exiting Manager Console.");
                        break;
                    default:
                        System.out.println("Invalid input!");
                        break;
                }
            }
        });
    }

    /**
     * Method that calls createMember and adds the member to the database
     */
    private void addMember() {
        Member member = createMember();

        if (!controller.addMember(member)) {
            System.err.println("Failed to add member.");
        } else {
            System.out.println("Member added successfully.");
        }
    }

    /**
     * Method that creates a Member
     *
     * @return the member created in the method
     */
    private Member createMember() {
        String name, street, city, state;
        int memberID, zip;

        name = readLine("Enter member name: ", in);

        memberID = readID("Enter member ID: ", in, 9);
        in.nextLine();

        street = readLine("Enter street: ", in);
        city = readLine("Enter city: ", in);
        state = readState(in);
        zip = readZip(in);

        return new Member(name, memberID, street, city, state.toUpperCase(), zip, true);
    }

    /**
     * Method that removes a member from the database
     */
    private void removeMember() {
        int memberID = readPositiveInteger("Enter the Member ID of the member to delete: ", in);

        if (controller.removeMember(memberID)) {
            System.out.println("Member deleted successfully.");
        }
        else {
            System.out.println("Member not found");
        }
    }

    /**
     * Method that updates a member in the database
     */
    private void updateMember() {
        int memberID = readPositiveInteger("Enter the Member ID of the member to update: ", in);
        in.nextLine();

        if (controller.searchMember(memberID) == null) {
            System.out.println("Member with ID " + memberID + " not found.");
            return;
        }

        Member member = createMember();

        controller.removeMember(memberID);
        controller.addMember(member);
    }

    /**
     * Method that calls createProvider adds the provider to the database
     */
    private void addProvider() {
        Provider provider = createProvider();

        if (!controller.addProvider(provider)) {
            System.err.println("Failed to add provider.");
        }
    }

    /**
     * Method that creates a Provider
     *
     * @return the provider created in the method
     */
    private Provider createProvider() {
        String name, street, city, state;
        int providerID, zip, numberOfConsultations, weeklyFee;

        name = readLine("Enter provider name: ", in);

        providerID = readID("Enter provider ID: ", in, 9);
        in.nextLine();

        street = readLine("Enter street: ", in);
        city = readLine("Enter city: ", in);
        state = readState(in);
        zip = readZip(in);

        numberOfConsultations = readPositiveInteger("Enter number of consultations: ", in);
        weeklyFee = readPositiveInteger("Enter weekly fee: ", in);

        return new Provider(name, providerID, street, city, state.toUpperCase(), zip, numberOfConsultations, weeklyFee);
    }

    /**
     * Method that removes a provider from the database
     */
    private void removeProvider() {
        int providerID = readPositiveInteger("Enter the Provider ID of the provider to delete: ", in);

        if (controller.removeProvider(providerID)) {
            System.out.println("Provider deleted successfully.");
        }
        else {
            System.out.println("Provider not found");
        }
    }

    /**
     * Method that updates a provider in the database
     */
    private void updateProvider() {
        int providerID = readPositiveInteger("Enter the Provider ID of the member to update: ", in);
        in.nextLine();

        if (controller.searchProvider(providerID) == null) {
            System.out.println("Provider with ID " + providerID + " not found.");
            return;
        }

        Provider provider = createProvider();

        controller.removeProvider(providerID);
        controller.addProvider(provider);
    }

    /**
     * Method that prints a statement and reads a line from the Scanner.
     *
     * @param statement string to be printed to the console
     * @param in Scanner object to be read from
     * @return string read by Scanner
     */
    private String readLine(String statement, Scanner in) {
        System.out.print(statement);

        return in.nextLine();
    }

    /**
     * Method that prints a statement and reads an ID of length
     *
     * @param statement string to be printed to the console
     * @param in Scanner object to be read from
     * @param length length that the ID should have
     * @return integer ID of length length
     */
    private int readID(String statement, Scanner in, int length) {
        int ID;

        do {
            ID = readPositiveInteger(statement, in);

            if (String.valueOf(ID).length() != length) {
                System.err.println("ID must be " + length + " digits long.");
            }
        } while (String.valueOf(ID).length() != length);

        return ID;
    }

    /**
     * Method that reads a two character state
     *
     * @param in Scanner object to be read from
     * @return string with two characters
     */
    private String readState(Scanner in) {
        String state;

        do {
            state = readLine("Enter state: ", in);

            if (state.length() != 2) {
                System.err.println("Invalid state abbreviation.");
            }
        } while (state.length() != 2);

        return state;
    }

    /**
     * Method that reads a 5 digit integer
     *
     * @param in Scanner object to be read from
     * @return integer with 5 digits
     */
    private int readZip(Scanner in) {
        int zip;

        do {
            zip = readPositiveInteger("Enter zip: ", in);

            if (String.valueOf(zip).length() != 5) {
                System.err.println("Zip code must be 5 digits long.");
            }
        } while (String.valueOf(zip).length() != 5);

        return zip;
    }
}
