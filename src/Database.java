package chocan;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.TreeSet;
import java.util.Comparator;
import java.util.Scanner;
import java.io.File;

/* Database class
* - 3 data members, all TreeSet objects
* - TreeSets will hold objects of the three base data classes
* - Utilize searching & sorting methods for accessing and copying objects
* - Delete exisitng entries, and Add new ones to respective trees
* Database will be contained within the Controller class, alongside ConsultationLog
* No direct user input
*/

public class Database
{
    // 3 separate lists
    private TreeSet<Member> memberList;         // Member objects sorted by MemberID
    private TreeSet<Provider> providerList;     // Provider objects sorted by ProviderID
    private TreeSet<Service> serviceList;       // Service objects sorted by Service Number
    private int managerID = 8675309;            // hard-coded manager ID (as opposed to a 4th list, of Managers)

    /***************************************************************************
     * Class Methods
     * Constructor and overridden finalize()
     */

    public Database() {
        this.memberList = new TreeSet<>(new memberComp()); // seed with custom comparators
        this.providerList = new TreeSet<>(new providerComp());
        this.serviceList = new TreeSet<>(new serviceComp());
        // read files into lists
        if (!loadDatabase()) {
            System.err.println("Not all data was not loaded. Database incomplete.");
        }
    }

    // ---------------------------------------------------------------------

    // copy constructor - allocates new lists and copies source lists
    public Database(Database source) {
        try {
            this.memberList = new TreeSet<>(new memberComp()); // initialize new lists
            for (Member toCopy : source.memberList) {
                Member newCopy = new Member(toCopy); // new object allocated for each source entry
                this.memberList.add(newCopy); // add copy into new list
            }
            // same as above
            this.providerList = new TreeSet<>(new providerComp());
            for (Provider toCopy : source.providerList) {
                Provider newCopy = new Provider(toCopy);
                this.providerList.add(newCopy);
            }
            // same as above
            this.serviceList = new TreeSet<>(new serviceComp());
            for (Service toCopy : source.serviceList) {
                Service newCopy = new Service(toCopy);
                this.serviceList.add(newCopy);
            }
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
            System.err.println("Invalid object for copy constructor - Database object required.");
        }
        catch (NullPointerException e) {
            e.printStackTrace();
            System.err.println("Null object - aborting full Database copy.");
        }
    }

    // ---------------------------------------------------------------------

    // Destructor
    @Override
    public void finalize() {
        this.clearAll(); // clear trees
        System.gc(); // call garbage collector
    }

    // ---------------------------------------------------------------------

    // will write lists to .txt files, then clear trees of all data
    public void clearAll() {
        // write data to files
        if (!this.printAllDataFile()) {
            System.err.println("Error - Some data not written to external files.");
        }
        // clear all trees, make all null
        if (this.memberList != null) {
            this.memberList.clear();
            this.memberList = null;
        }
        if (this.providerList != null) {
            this.providerList.clear();
            this.providerList = null;
        }
        if (this.serviceList != null) {
            this.serviceList.clear();
            this.serviceList = null;
        }
    }

    /***************************************************************************
     * Write contents of TreeSets out to .txt files
     */

    // Write Lists into 3 separate data files
    public boolean printAllDataFile() {
        return (this.writeMemberListToFile() && this.writeProviderListToFile() && this.writeServiceListToFile());
    }

    // ---------------------------------------------------------------------

    // write memberList to Members.txt
    public boolean writeMemberListToFile() {
        boolean success = false;
        try {
            if (this.memberList != null) {
                if (!this.memberList.isEmpty()) {
                    PrintWriter fileOut = new PrintWriter("Members.txt");
                    for (Member toPrint : this.memberList) {
                        if (toPrint != null) {
                            fileOut.println(toPrint.toString()); // overridden class toString(), ":" delimiters
                        } else {
                            fileOut.close(); // close file stream if exception is thrown
                            throw new NullPointerException("Null Member List entry encountered when writing Database to file.");
                        }
                    }
                    fileOut.close(); // normal closing of file stream after loop exits
                    success = true; // if exception throws, success stays false
                }
            } else {
                throw new NullPointerException("Invalid Member List object encountered when writing to Member.txt.");
            }
        } catch (FileNotFoundException e) {
            System.err.println("Members.txt file not found.");
        } catch (NullPointerException e) {
            e.printStackTrace();
            System.err.println("Abort writing Member List to file.");
        }
        return success;
    }

    // ---------------------------------------------------------------------

    // write providerList to Providers.txt
    public boolean writeProviderListToFile() {
        boolean success = false;
        try {
            if (this.providerList != null) {
                if (!this.providerList.isEmpty()) {
                    PrintWriter fileOut = new PrintWriter("Providers.txt");
                    for (Provider toPrint : this.providerList) {
                        if (toPrint != null) {
                            fileOut.println(toPrint.toString()); // each entry as a String, separated by ":"
                        } else {
                            fileOut.close(); // close is exception is thrown
                            throw new NullPointerException("Null Provider Database entry encountered while saving to file.");
                        }
                    }
                    fileOut.close(); // normal execution should reach here after loop
                    success = true; // set true if loop traversed
                }
            } else {
                throw new NullPointerException("Invalid Provider List object encountered when writing to Provider.txt.");
            }
        } catch (FileNotFoundException e) {
            System.err.println("Providers.txt file not found.");
        } catch (NullPointerException e) {
            System.err.println("Abort writing Provider list to file.");
        }
        return success;
    }

    // ---------------------------------------------------------------------

    // Write serviceList to Services.txt
    public boolean writeServiceListToFile() {
        boolean success = false;
        try {
            if (this.serviceList != null) {
                if (!this.serviceList.isEmpty()) {
                    PrintWriter fileOut = new PrintWriter("Services.txt");
                    for (Service toPrint : this.serviceList) {
                        if (toPrint != null) {
                            fileOut.println(toPrint.toString()); // loop thru tree, save each entry as a String in a new line
                        } else {
                            fileOut.close();
                            throw new NullPointerException("Null Service Database entry encountered while saving to file.");
                        }
                    }
                    fileOut.close(); // normal execution of loop
                    success = true;
                }
            } else {
                throw new NullPointerException("Null Service ");
            }
        } catch (FileNotFoundException e) {
            System.err.println("Services.txt file not found.");
        } catch (NullPointerException e) {
            System.err.println("Abort writing Service list to file.");
        }
        return success;
    }

    /***************************************************************************
     * Read from (.txt) files into TreeSets
     */

    // Call all three read file methods, and return tru if all 3 succeed
    // errors indicate specifically which files aren't loaded, no message if success.
    public boolean loadDatabase() {
        boolean loadMembers = false, loadProviders = false, loadServices = false, loadAll = false;
        if (this.readMemberFile()) {
            loadMembers = true;
        } else {
            System.err.println("Members.txt not read in from file. Member list empty.");
        }
        if (this.readProviderFile()) {
            loadProviders = true;
        } else {
            System.err.println("Providers.txt not read in from file. Provider list empty.");
        }
        if (this.readServiceFile()) {
            loadServices = true;
        } else {
            System.err.println("Service.txt not read in from file. Service list empty.");
        }
        if (loadMembers && loadProviders && loadServices) {
            loadAll = true; // only true if all three files read in successfully
        }
        return loadAll;
    }

    // ---------------------------------------------------------------------

    // read Member.txt into Database
    public boolean readMemberFile() {
        try {
            String memName, memAddress, memCity, memState, line, sorted[];
            int memID, memZip;
            boolean memStatus, success = true;
            File file = new File("Members.txt"); // path name
            Scanner fileIn = new Scanner(file); //new Scanner object initialized with file object
            // loop continues until end of file or adding failed
            while (fileIn.hasNextLine() && success) {
                line = fileIn.nextLine(); // Read in the line, store in a string
                sorted = line.split(":", 7); // Split String into array
                // Member fields assigned appropriate data
                memName = sorted[0]; // Name will be first index of new array, and so on
                memID = Integer.parseInt(sorted[1]); // Convert from string to int
                memAddress = sorted[2]; // No conversion needed
                memCity = sorted[3];
                memState = sorted[4];
                memZip = Integer.parseInt(sorted[5]); // Convert
                memStatus = Boolean.parseBoolean(sorted[6]); // Convert from string to boolean
                if (!this.addMember(memName, memID, memAddress, memCity, memState, memZip, memStatus)) {
                    success = false; // Will exit loop if adding was unsuccessful - room for exception handling here
                }
            }
            fileIn.close();
        } catch (FileNotFoundException e) {
            System.err.println("Members.txt file not found - check directory.");
        }
        return ((this.memberList != null) && (this.memberList.size() > 0)); // return true if memberCount is not 0
    }

    // ---------------------------------------------------------------------

    // read Provider.txt into Database
    public boolean readProviderFile() {
        try {
            String provName, provAddress, provCity, provState, line, sorted[];
            int provID, provZip, numConsults, weeklyFee;
            boolean success = true;
            File file = new File("Providers.txt"); // path name
            Scanner fileIn = new Scanner(file);
            while (fileIn.hasNextLine() && success) {
                line = fileIn.nextLine();
                sorted = line.split(":", 8);
                // Provider fields assigned appropriate indices
                provName = sorted[0];
                provID = Integer.parseInt(sorted[1]);
                provAddress = sorted[2];
                provCity = sorted[3];
                provState = sorted[4];
                provZip = Integer.parseInt(sorted[5]);
                numConsults = Integer.parseInt(sorted[6]);
                weeklyFee = Integer.parseInt(sorted[7]);
                // add new Provider object to list
                if (!this.addProvider(provName, provID, provAddress, provCity, provState, provZip, numConsults, weeklyFee)) {
                    success = false; // exit loop if any add function fails
                }
            }
            fileIn.close();
        } catch (FileNotFoundException e) {
            System.err.println("Providers.txt file not found - check directory.");
        }
        return ((this.providerList != null) && (this.providerList.size() > 0));
    }

    // ---------------------------------------------------------------------

    // read Service.txt into Database
    public boolean readServiceFile() {
        try {
            String servName, line, sorted[];
            int servNum, servCost;
            boolean success = true;
            File file = new File("Services.txt"); // path
            Scanner fileIn = new Scanner(file);
            // loop thru file and read lines into array, split, insert, repeat
            while (fileIn.hasNextLine() && success) {
                line = fileIn.nextLine();
                sorted = line.split(":", 3);
                // assign Service fields from split array
                servName = sorted[0];
                servNum = Integer.parseInt(sorted[1]);
                servCost = Integer.parseInt(sorted[2]);
                // create and add Service object to serviceList
                if (!this.addService(servName, servNum, servCost)) {
                    success = false;
                }
            }
            fileIn.close();
        } catch (FileNotFoundException e) {
            System.err.println("Services.txt file not found - check directory.");
        }
        return ((this.serviceList != null) && (this.serviceList.size() > 0));
    }

    /***************************************************************************
     * Simple getters and validity checks
     */

    // return manager name
    public String getManagerName() {
        return "Manager";
    }

    // ---------------------------------------------------------------------

    // return manager key number
    public int getManagerID() {
        return this.managerID;
    }

    // ---------------------------------------------------------------------

    // check passed-in number against ManagerKey, returns true if match
    public boolean isManager(int managerID) {
        try {
            if (this.managerID == managerID){
                return true;
            }
        } catch(IllegalArgumentException e) {
            e.printStackTrace();
            System.err.println("Invalid parameter - enter a valid Manager ID.");
        }
        return false;
    }

    // ---------------------------------------------------------------------

    // check validity of MemberID against current memberList entries
    public boolean isMember(int memberID) {
        try {
            if (this.memberList != null) { // check for null list object
                for (Member toVerify : this.memberList) {
                    if (toVerify != null) { // check for null member object
                        if (toVerify.getMemberID() == memberID) {
                            return true; // exit loop and indicate valid ProviderID#
                        }
                    } else {
                        throw new NullPointerException("Invalid Member data encountered within list while validating Provider ID.");
                    }
                }
            } else {
                throw new NullPointerException("Invalid Member list object encountered when checking ID validity.");
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            System.err.println("Invalid parameter - valid Member ID number required.");
        } catch (NullPointerException e) {
            e.printStackTrace(); // catch null object errors here, make clear to user
            System.err.println("Abort Member validation.");
        }
        return false; // if empty list, or search failed
    }

    // ---------------------------------------------------------------------

    // check validity of ProviderID against current providerList entries
    public boolean isProvider(int providerID) {
        try {
            if (this.providerList != null) {
                for (Provider toVerify : this.providerList) {
                    if (toVerify != null) {
                        if (toVerify.getProviderID() == providerID) {
                            return true; // exit loop and indicate valid ProviderID#
                        }
                    } else {
                        throw new NullPointerException("Invalid Provider data encountered within list while validating Provider ID.");
                    }
                }
            } else {
                throw new NullPointerException("Invalid Provider list object encountered when checking ID validity.");
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            System.err.println("Invalid parameter - valid Provider ID number required.");
        } catch (NullPointerException e) {
            e.printStackTrace();
            System.err.println("Abort Provider validation.");
        }
        return false; // if empty list, or search failed
    }

    // ---------------------------------------------------------------------

    // current number of listed Members
    public int getMemberCount() {
        try {
            if (this.memberList != null) {
                return this.memberList.size(); // normal operation, return 0 if empty
            } else {
                throw new NullPointerException("Error - Null Member list object.");
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            System.err.println("Error - Null object. Abort Member list entry count.");
            return 0; // return error code
        }
    }

    // ---------------------------------------------------------------------

    // current number of listed Providers
    public int getProviderCount() {
        try {
            if (this.providerList != null) {
                return this.providerList.size();
            } else {
                throw new NullPointerException("Error - Null Provider list object.");
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            System.err.println("Error - Null object. Abort Provider list entry count");
            return 0;
        }
    }

    // ---------------------------------------------------------------------

    // current number of listed Services
    public int getServiceCount() {
        try {
            if (this.serviceList != null) {
                return this.serviceList.size(); // Normal operation of method, return a number
            } else {
                throw new NullPointerException("Error - Null Service list object.");
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            System.err.println("Error - Null list object. Abort Service list entry count.");
            return 0; //   Error code returned if exception thrown
        }
    }

    // ---------------------------------------------------------------------

    // Member - wrapper for checking if list is empty
    public boolean isMemberListEmpty() {
        try {
            if (this.memberList != null) {
                return this.memberList.isEmpty(); // return TRUE if list is empty
            } else {
                throw new NullPointerException("Invalid Member list object.");
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            System.err.println("Abort Member list empty check.");
            return false;
        }
    }

    // ---------------------------------------------------------------------

    // Provider - wrapper, slightly less typing than using if (count != 0)
    public boolean isProviderListEmpty() {
        try {
            if (this.providerList != null) {
                return this.providerList.isEmpty(); // TRUE if list is empty
            } else {
                throw new NullPointerException("Invalid Provider list object.");
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            System.err.println("Abort Provider list empty check.");
            return false;
        }
    }

    // ---------------------------------------------------------------------

    // Service - wrapper for external use
    public boolean isServiceListEmpty() {
        try {
            if (this.serviceList != null) {
                return this.serviceList.isEmpty(); // TRUE if list is empty
            } else {
                throw new NullPointerException("Invalid Service list object.");
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            System.err.println("Abort Service list empty check.");
            return false;
        }
    }

    // ---------------------------------------------------------------------

    // return TRUE if the member already exists within the list
    // wrapper for TreeSet.contains(), which is seeded with custom Comparator
    public boolean isDuplicateMem(Member toCheck) {
        try {
            return ((toCheck != null) && (this.memberList.contains(toCheck)));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            System.err.println("Error - Invalid parameter for Member duplicate check.");
        }
        return false;
    }

    // ---------------------------------------------------------------------

    // same as above, for Provider class
    public boolean isDuplicateProv(Provider toCheck) {
        try {
            return ((toCheck != null) && (this.providerList.contains(toCheck)));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            System.err.println("Invalid parameter for Provider duplicate check.");
        }
        return false;
    }

    // ---------------------------------------------------------------------

    // same as above, for Service class
    public boolean isDuplicateServ(Service toCheck) {
        try {
            return ((toCheck != null) && (this.serviceList.contains(toCheck)));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            System.err.println("Invalid parameter for Service duplicate check.");
        }
        return false;
    }

    /***************************************************************************
     * Add entries to lists, listed in order of Member, Provider, Service
     */

    // Add Member with Object
    public boolean addMember(Member toAdd) {
        boolean success = false;
        try {
            if (toAdd != null) {
                // TreeSet.contains() based on Comparator<MemberID>
                if (this.memberList != null) {
                    if (!this.memberList.contains(toAdd)) { // check for duplicates
                        if (this.memberList.add(toAdd)) { // add object argument
                            success = true;
                        }
                    }
                } else {
                    throw new NullPointerException("Add Member failure - invalid Member List object.");
                }
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            System.err.println("Invalid Add Member parameter - Member object required.");
        } catch (NullPointerException e) {
            e.printStackTrace();
            System.err.println("Null object - aborting Add Member process.");
        } catch (ClassCastException e) {
            e.printStackTrace();
            System.err.println("Invalid Object Type.");
        }
        return success;
    }

    // ---------------------------------------------------------------------

    // add Member based on passed in individual data members
    public boolean addMember(String newName, int newID, String newAddress, String newCity, String newState, int newZip, boolean newStatus) {
        boolean success = false;
        try {
            if (newName != null || newAddress != null || newCity != null || newState != null) { // if any arguments are null
                Member toAdd = new Member(newName, newID, newAddress, newCity, newState, newZip, newStatus);
                // pass new object off to other add method
                if (this.addMember(toAdd)) {
                    success = true;
                }
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            System.err.println("Invalid Add Member parameters - Member data fields required.");
        } catch (NullPointerException e) {
            e.printStackTrace();
            System.err.println("Null object - aborting Add Member process.");
        } catch (ClassCastException e) {
            e.printStackTrace();
            System.err.println("Invalid Object Type.");
        }
        return success;
    }

    // ---------------------------------------------------------------------

    // add Provider from passed-in object from Controller
    public boolean addProvider(Provider toAdd) {
        boolean success = false;
        try {
            if (toAdd != null) {
                if (this.providerList != null) {
                    if (!this.providerList.contains(toAdd)) { // check for duplicate first
                        if (this.providerList.add(toAdd)) {
                            success = true;
                        }
                    }
                } else {
                    throw new NullPointerException("Add Provider failure - invalid Provider list object.");
                }
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            System.err.println("Invalid Add Provider parameter - Provider object required.");
        } catch (NullPointerException e) {
            e.printStackTrace();
            System.err.println("Abort add Provider process.");
        } catch (ClassCastException e) {
            e.printStackTrace();
            System.err.println("Invalid Object Type.");
        }
        return success;
    }

    // ---------------------------------------------------------------------

    // add Provider based on passed-in individual data members from Controller
    public boolean addProvider(String newName, int newID, String newAddress, String newCity, String newState, int newZip, int newNumberOfConsultations, int newFeeForAWeek) {
        boolean success = false;
        try {
            if (newName != null || newAddress != null || newCity != null || newState != null) {
                Provider toAdd = new Provider(newName,newID,newAddress,newCity,newState,newZip,newNumberOfConsultations,newFeeForAWeek);
                // pass new object off to other addProvider
                if (this.addProvider(toAdd)) {
                    success = true;
                }
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid Add Provider parameters - Provider data fields required.");
        } catch (NullPointerException e) {
            System.err.println("Abort add Provider process.");
        } catch (ClassCastException e) {
            e.printStackTrace();
            System.err.println("Invalid Object Type.");
        }
        return success; // only returns true if object was not duplicate + successfully added
    }

    // ---------------------------------------------------------------------

    // Add Service from already initialized object
    public boolean addService(Service toAdd) {
        boolean success = false;
        try {
            if (toAdd != null) {
                if (this.serviceList != null) {
                    if (!serviceList.contains(toAdd)) { // check for duplicate
                        if (serviceList.add(toAdd)) {
                            success = true;
                        }
                    }
                } else {
                    throw new NullPointerException("Add Service failure - Invalid Service list object.");
                }
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            System.err.println("Invalid Add Service parameter - Service object required.");
        } catch (NullPointerException e) {
            e.printStackTrace();
            System.err.println("Abort add Service process.");
        } catch (ClassCastException e) {
            e.printStackTrace();
            System.err.println("Invalid Object Type.");
        }
        return success;
    }

    // ---------------------------------------------------------------------

    // Add Service object, using individual arguments
    public boolean addService(String newName, int newNumber, int newCost) {
        boolean success = false;
        try {
            if (newName != null) {
                Service toAdd = new Service(newName, newNumber, newCost);
                // pass off to other addService method
                if (this.addService(toAdd)) {
                    success = true;
                }
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            System.err.println("Invalid Add Service parameters - Service data fields required.");
        } catch (NullPointerException e) {
            e.printStackTrace();
            System.err.println("Abort add Service process.");
        } catch (ClassCastException e) {
            e.printStackTrace();
            System.err.println("Invalid Object Type.");
        }
        return success;
    }

    /***************************************************************************
     * Single-remove specified entries from Lists
     */

    // delete Member based specified ID
    public boolean deleteMember(int memberID) {
        try {
            if (this.memberList != null) { // check for null object, foreach loop checks for empty list
                if (!this.memberList.isEmpty()) {
                    for (Member toRemove : this.memberList) { // search tree
                        if (toRemove != null) {
                            if (toRemove.getMemberID() == memberID) { // match ID with input key
                                if (this.memberList.remove(toRemove)) { // if removed, return true
                                    return true; // no need to continue looping further, return immediately
                                }
                            }
                        } else {
                            throw new NullPointerException("Null Member entry encountered while searching list.");
                        }
                    }
                } else {
                    System.err.println("Nothing to delete - Member List is empty.");
                }
            } else {
                throw new NullPointerException("Delete Member failure - invalid Member list object.");
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            System.err.println("Invalid parameter for deleting Member entry - enter a valid Member ID.");
        } catch (NullPointerException e) {
            e.printStackTrace();
            System.err.println("Abort Member search & delete.");
        }
        return false; // if list is empty, or search failed
    }

    // ---------------------------------------------------------------------

    // delete Provider based on specified ID argument
    public boolean deleteProvider(int providerID) {
        try {
            if (this.providerList != null) { // check for empty list, skip loop if empty
                if (!this.providerList.isEmpty()) {
                    for (Provider toRemove : this.providerList) { // iterate thru tree and search
                        if (toRemove != null) {
                            if (toRemove.getProviderID() == providerID) { // compare id with input (arg)
                                if (this.providerList.remove(toRemove)) { // remove if match
                                    return true; // return true and exit function immediately
                                }
                            }
                        } else {
                            throw new NullPointerException("Null Provider entry encountered while searching list.");
                        }
                    }
                } else {
                    System.err.println("Nothing to delete - Provider list is empty.");
                }
            } else {
                throw new NullPointerException("Delete Provider failure - invalid Provider list object.");
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            System.err.println("Invalid parameter for deleting Provider entry - enter a valid Provider ID.");
        } catch (NullPointerException e) {
            e.printStackTrace();
            System.err.println("Abort Provider search & delete.");
        }
        return false; // if list is empty, or search failed
    }

    // ---------------------------------------------------------------------

    //delete Service based on specified service Number argument
    public boolean deleteService(int serviceNum) {
        try {
            if (this.serviceList != null) {
                if (!this.serviceList.isEmpty()) { // empty list check, quit otherwise
                    for (Service toRemove : this.serviceList) { // if list isn't empty, iterate thru
                        if (toRemove != null) {
                            if (toRemove.getServiceNumber() == serviceNum) { // match number with argument
                                if (this.serviceList.remove(toRemove)) { // remove if match
                                    return true; // return true right away if search & remove successful
                                }
                            }
                        } else {
                            throw new NullPointerException("Null Service entry encountered while searching list.");
                        }
                    }
                } else {
                    System.err.println("Nothing to delete - Service list is empty.");
                }
            } else {
                throw new NullPointerException("Delete Service failure - invalid Service list object.");
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            System.err.println("Invalid parameter for deleting a service - enter a valid Service Number.");
        } catch (NullPointerException e) {
            e.printStackTrace();
            System.err.println("Abort Service search & delete.");
        }
        return false; // if list is empty, or search failed
    }

    /***************************************************************************
     * Search a list for a specific entry
     */

    // Search for Members by their ID #, return object or null
    public Member searchMember(int memberID) {
        Member found = null; // return a null object if nothing found
        try {
            if (this.memberList != null) {
                if (!this.memberList.isEmpty()) {
                    for (Member toFind : this.memberList) {
                        // search list for matching memberID
                        if (toFind != null) {
                            if (toFind.getMemberID() == memberID) {
                                found = new Member(toFind); // initialize a new object to return
                                break; // exit loop if member found
                            }
                        } else {
                            throw new NullPointerException("Error - Null Member entry encountered while searching list.");
                        }
                    }
                } else { // these messages might be unnecessary
                    System.err.println("Nothing to Search - Member List is Empty.");
                }
            } else {
                throw new NullPointerException("Search Member failure - invalid Member list object.");
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            System.err.println("Invalid parameter for searching Member data - enter a valid Member ID.");
        } catch (NullPointerException e) {
            e.printStackTrace();
            System.err.println("Abort Member search.");
        }
        return found; // return null if nothing found
    }

    // ---------------------------------------------------------------------

    // search Provider list by their ID #
    public Provider searchProvider(int providerID) {
        Provider found = null; // will return null if search fails
        try {
            if (this.providerList != null) {
                if (!this.providerList.isEmpty()) {
                    for (Provider toFind : this.providerList) {
                        if (toFind != null) {
                            if (toFind.getProviderID() == providerID) {
                                found = new Provider(toFind); // new provider, return object
                                break;
                            }
                        } else {
                            throw new NullPointerException("Null Provider entry encountered while searching list.");
                        }
                    }
                } else {
                    System.err.println("Nothing to Search - Provider List is Empty.");
                }
            } else {
                throw new NullPointerException("Search Provider failure - invalid Provider list object.");
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            System.err.println("Invalid parameter for deleting Provider data - enter a valid Provider ID.");
            found = null;
        } catch (NullPointerException e) {
            e.printStackTrace();
            System.err.println("Abort Provider search.");
            found = null;
        }
        return found; // return null if nothing found
    }

    // ---------------------------------------------------------------------

    // search Services by NUMBER
    public Service searchService(int serviceNum) {
        Service found = null;
        try {
            if (this.serviceList != null) {
                if (!this.serviceList.isEmpty()) {
                    for (Service toFind : this.serviceList) {
                        if (toFind != null) {
                            if (toFind.getServiceNumber() == serviceNum) {
                                found = new Service(toFind); // new Service obj to return
                                break;
                            }
                        } else {
                            throw new NullPointerException("Null Service entry encountered while searching list.");
                        }
                    }
                } else { // messages may be redundant/unnecessary
                    System.err.println("Nothing to Search - Service List is Empty.");
                }
            } else {
                throw new NullPointerException("Search Service failure1 - invalid Service list object.");
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            System.err.println("Invalid parameter for deleting Service data - enter a valid Service Number.");
            found = null;
        } catch (NullPointerException e) {
            e.printStackTrace();
            System.err.println("Abort Service search.");
            found = null;
        }
        return found; // return null if nothing found
    }

    /***************************************************************************
     * Display entire contents of a given List
     */

    // Display contents of memberList
    public void displayAllMembers() {
        try {
            if (this.memberList != null) {
                if (!this.memberList.isEmpty()) {
                    System.out.println("All Members (" + this.memberList.size() + "):");
                    System.out.println(" - - - - - - - - - - - - - - - - - - - - ");
                    for (Member toPrint : this.memberList) {
                        if (toPrint != null) {
                            toPrint.displayAll(); // call individual display method each iteration
                            System.out.println(" - - - - - - - - - - - - - - - - - - - - ");
                        } else {
                            throw new NullPointerException("Error - Null Member entry encountered while displaying list.");
                        }
                    }
                } else {
                    System.err.println("Nothing to display - Member list is empty.");
                }
            } else {
                throw new NullPointerException("Error - Null Member list object.");
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            System.err.println("Abort Member list display.");
        }
    }

    // ---------------------------------------------------------------------

    // Display contents of providerList
    public void displayAllProviders() {
        try {
            if (this.providerList != null) {
                if (!this.providerList.isEmpty()) {
                    System.out.println("All Providers (" + this.providerList.size() + "):");
                    System.out.println(" - - - - - - - - - - - - - - - - - - - - ");
                    for (Provider toPrint : this.providerList) {
                        if (toPrint != null) {
                            toPrint.displayAll();
                            System.out.println(" - - - - - - - - - - - - - - - - - - - - ");
                        } else {
                            throw new NullPointerException("Error - Null Provider entry encountered while displaying list.");
                        }
                    }
                } else {
                    System.err.println("Nothing to display - Provider list is empty.");
                }
            } else {
                throw new NullPointerException("Error - Null Provider list object.");
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            System.err.println("Abort Provider list display.");
        }
    }

    // ---------------------------------------------------------------------

    // Display contents of serviceList
    public void displayAllServices() {
        try {
            if (this.serviceList != null) {
                if (!this.serviceList.isEmpty()) {
                    System.out.println("All Services (" + this.serviceList.size() + "):");
                    System.out.println(" - - - - - - - - - - - - - - - - - - - - ");
                    for (Service toPrint : this.serviceList) {
                        if (toPrint != null) {
                            toPrint.displayAll();
                            System.out.println(" - - - - - - - - - - - - - - - - - - - - ");
                        } else {
                            throw new NullPointerException("Null Service entry encountered while displaying list.");
                        }
                    }
                } else {
                    System.err.println("Nothing to display - Service list is empty.");
                }
            } else {
                throw new NullPointerException("Error - Null Service list object.");
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            System.err.println("Abort Service list display.");
        }
    }

    /*************************************************************************
    */

} // end Database class


/***************************************************************************
 * Comparator classes, for seeding TreeSets
 */

// Seeding TreeSet objects with a right proper Comparator object
// Member Comparator for memberList, sorted by ID number
class memberComp implements Comparator<Member> {
    @Override
    public int compare(Member m1, Member m2) {
        //return m1.getMemberName().compareTo(m2.getMemberName());
        return m1.getMemberID() - m2.getMemberID();
    }
}

// ---------------------------------------------------------------------

// Provider Comparator for providerList, sorted by ID number
class providerComp implements Comparator<Provider> {
    @Override
    public int compare(Provider p1, Provider p2) {
        return p1.getProviderID() - p2.getProviderID();
    }
}

// ---------------------------------------------------------------------

class serviceComp implements Comparator<Service> {
    @Override
    public int compare(Service s1, Service s2) {
        return s1.getServiceNumber() - s2.getServiceNumber();
    }
}

// ---------------------------------------------------------------------
