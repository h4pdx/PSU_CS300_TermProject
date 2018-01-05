package chocan;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.TreeSet;
import java.util.Comparator;
import java.util.Calendar;
import java.util.Date;
import java.io.*;

public class ConsultationLog {

    private TreeSet<Consultation> recordedConsultations;

    //Constructor
    public ConsultationLog() {
        recordedConsultations = new TreeSet<>(new ConsultationDateComp());
    }

    public boolean add(Consultation toAdd) {
        recordedConsultations.add(toAdd);
        return true;
    }

    //Write to file at program closing
    public boolean writeLogToFile() {
        try {
            String filename = "ConsultationHistory.txt";
            PrintWriter out = new PrintWriter(filename);
            System.out.println("[consultationHistory.txt created.]");
            for (Consultation currentConsultation : recordedConsultations) {
                out.println(currentConsultation.getMemberDataString() + ":");
                out.println(currentConsultation.getProviderDataString() + ":");
                out.println(currentConsultation.getServiceDataString() + ":");
                out.println(currentConsultation.consultationInfoToString() + "\n");

            }

            out.close();
            System.out.println("[Consultations.txt populated.]");
            return true;
        } catch (FileNotFoundException error) { System.out.println("[There was an error creating the file. No history written.]"); }

        return false;
    }

    //Function for printing the member report. Takes an array of member IDs,
    //and then calls the 2nd makeMemberReport with the individual ID.
    public boolean makeMemberReport() { //This function calls makeMemberReport for each memberID

        TreeSet<Member> memberTree;
        memberTree = new TreeSet<>(new MemberIDComp());
        Member tempMember = null;

        for (Consultation currentConsultation : recordedConsultations) {
            tempMember = new Member(currentConsultation.getMember());
            memberTree.add(tempMember);
        }

        Member[] memberList = null; // set to null, to be updated or returned for fail

        try {
            if (memberTree != null) {
                if (!memberTree.isEmpty()) { // null object && empty list check
                    memberList = new Member[memberTree.size()]; // dynamic size
                    int i = 0; // array index
                    for (Member toPrint : memberTree) { // iterate thru treeset
                        if (toPrint != null) { // null check on treeset objects
                            memberList[i++] = new Member(toPrint); // copy new member data and advance index
                            }
                        }
                    }
                }
            }
        catch(NullPointerException e) {
            e.printStackTrace();
            System.err.println("Null object - aborting current Member list return.");
            memberList = null;
        }


        for (int i = 0; i < memberList.length; ++i) {
            makeMemberReport(memberList[i]);
        }
        System.out.println("\nMember report printed.");
        return true;
    }

    //Function for printing the member report. Takes a a single member ID.
    private boolean makeMemberReport(Member memberToReport) {
        //Get the current date
        java.util.Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        String reportName = "Member Report for " + memberToReport.getMemberName() + ".txt";
        boolean found = false; //Stores if we have found at least one consultation for this member
        int count = 1;

        for (Consultation currentConsultation : recordedConsultations) {
            if (    currentConsultation.doesMemberIDMatch(memberToReport.getMemberID()) &&
                    currentConsultation.withinPastWeek()) {
                found = true;
            }
        }

        if(found) {
            try {
                PrintWriter out = new PrintWriter(reportName);
                out.println("[Member Report created at " + cal.getTime() + "]");
                out.println("[Member Name: " + memberToReport.getMemberName() + "]");
                out.println("[Member ID: " + memberToReport.getMemberID() + "]");
                out.println("[Member Address: " + memberToReport.getMemberAddress() + "]");
                out.println("\n[Services received within the past week]\n");

                for (Consultation currentConsultation : recordedConsultations) {
                    if (currentConsultation.doesMemberIDMatch(memberToReport.getMemberID()) &&
                            currentConsultation.withinPastWeek()) {
                        out.println("[Record #" + count++ + "]");
                        out.println(currentConsultation.getMemberReportString());

                    }
                }

                out.println("[Member report concluded.]");
                out.close();
            } catch (FileNotFoundException error) {
                System.out.print("consultationLog.makeMemberReport(int): File not found exception.");
            }
        }

        return true;
    }

    public boolean makeProviderAndEFTReport() {
        //This function calls makeProviderReport for each providerID
        //This also builds the EFT report at the same time
        float tempFees = 0;
        int entryCount = 0;

        //Time stuff
        java.util.Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        TreeSet<Provider> providerTree;
        providerTree = new TreeSet<>(new ProviderIDComp());
        Provider tempProvider = null;

        for (Consultation currentConsultation : recordedConsultations) {
            tempProvider = new Provider(currentConsultation.getProvider());
            providerTree.add(tempProvider);
        }

        Provider[] providerList = null; // set to null, to be updated or returned for fail

        try {
            if (providerTree != null) {
                if (!providerTree.isEmpty()) { // null object && empty list check
                    providerList = new Provider[providerTree.size()]; // dynamic size
                    int i = 0; // array index
                    for (Provider toPrint : providerTree) { // iterate thru treeset
                        if (toPrint != null) { // null check on treeset objects
                            providerList[i++] = new Provider(toPrint); // copy new member data and advance index
                        }
                    }
                }
            }
        }
        catch(NullPointerException e) {
            e.printStackTrace();
            System.err.println("Null object - aborting current Member list return.");
            providerList = null;
        }

        try {
            PrintWriter out = new PrintWriter("EFT report.txt");
            out.println("[EFT Report for provider transactions created at " + cal.getTime() + "]\n");
            for(int i = 0; i < providerList.length; ++i) {
                tempFees = makeProviderAndEFTReport(providerList[i]); //Call the function that handles one provider, and returns fees
                if(tempFees > 0) {
                    out.print(  "[Entry #" + (++entryCount) + "]" +
                                "[Name: " + providerList[i].getProviderName() +
                                "][ID: " + providerList[i].getProviderID() +
                                "][Transfer amount: " + tempFees + "]\n");
                }
            }

            if(entryCount == 0)
                out.println("[No transactions available for the EFT report]");
            else
                out.println("\n[End of EFT Report]");

            out.close();

        } catch (FileNotFoundException exception1) {
            System.out.println("FileNotFound exception in makeProviderandEFTReport(Provider[])");
        }

        System.out.println("Provider and EFT Reports printed.");
        return true;
    }

    private float makeProviderAndEFTReport(Provider providerToReport) {
        //This function searches the consultationLog for all providers that match the passed provider
        //This function also makes the EFT report

        //Get the current date
        java.util.Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        //Stores if we have found at least one consultation for this member
        boolean found = false;
        int count = 0;
        String filename = "Provider Report for " + providerToReport.getProviderName() + ".txt";;
        float totalFees = 0;
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        boolean doesProviderHaveActivity = false;

        for (Consultation currentConsultation : recordedConsultations) {
            if (currentConsultation.doesProviderIDMatch(providerToReport.getProviderID()) &&
                    currentConsultation.withinPastWeek())
                doesProviderHaveActivity = true;
        }

        if(doesProviderHaveActivity) {
            try {
                PrintWriter out = new PrintWriter(filename);

                for (Consultation currentConsultation : recordedConsultations) {
                    if (currentConsultation.doesProviderIDMatch(providerToReport.getProviderID()) &&
                        currentConsultation.withinPastWeek()) {
                        if (!found) {
                            out.println("[Provider Report created at " + cal.getTime() + "]");
                            out.println("[Provider Name: " + providerToReport.getProviderName() + "]");
                            out.println("[Provider ID: " + providerToReport.getProviderID() + "]");
                            out.println("[Provider Address: " + providerToReport.getProviderAddress() + "]\n");
                            found = true;
                            out.println("[Services Provided within the past week]\n");
                        }

                        ++count;
                        out.println("[Record #" + count + "]");
                        out.println(currentConsultation.getProviderReportString());
                        totalFees += currentConsultation.getFee();
                    }

            }

            if (found == true) { //Recap if at least one consultation was found
                out.println("[Total consultations: " + count + "]");
                out.println("[Total fees: $" + df.format(totalFees) + "]");
                out.println("\n[Provider report concluded]");
            }

            else {
                System.out.println("[No data was found for this provider in the past week.]");

            }

            out.close();

        } catch (FileNotFoundException error) {
            System.out.println("consultationlog.makeProviderReport(int): File not found exception #1"); }
        }
        return totalFees;
    }

    public boolean makeAPMgrReport() {
        ManagerReportObject tempReportObject = null;
        int totalProviders = 0;
        int totalConsultations = 0;
        float totalFees = 0;
        java.util.Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        TreeSet<Provider> providerTree;
        providerTree = new TreeSet<>(new ProviderIDComp());
        Provider tempProvider = null;

        //Get a tree of providers from the consultationLog
        for (Consultation currentConsultation : recordedConsultations) {
            tempProvider = new Provider(currentConsultation.getProvider());
            providerTree.add(tempProvider);
        }

        Provider[] providerList = null; // set to null, to be updated or returned for fail

        try {
            if (providerTree != null) {
                if (!providerTree.isEmpty()) { // null object && empty list check
                    providerList = new Provider[providerTree.size()]; // dynamic size
                    int i = 0; // array index
                    for (Provider toPrint : providerTree) { // iterate thru treeset
                        if (toPrint != null) { // null check on treeset objects
                            providerList[i++] = new Provider(toPrint); // copy new member data and advance index
                        }
                    }
                }
            }
        }

        catch(NullPointerException e) {
            e.printStackTrace();
            System.err.println("Null object - aborting current Member list return.");
            providerList = null;
        }

        try {
            PrintWriter out = new PrintWriter("AP Manager report.txt");
            out.println("[AP manager report created at " + cal.getTime() + "]\n");

            out.println("[Provider activity within the past week]\n");
            for(int i = 0; i < providerList.length; ++i) {
                //This line takes the provider ID and then goes through every consultation to check for a match.
                //Matches get added to an object that stores a running total for fees, # of services, and the provider's name.
                tempReportObject = makeAPMgrReport(providerList[i].getProviderID());

                //Temp report object will be null if this provider had no services provided in the past week
                if(tempReportObject != null) {
                    out.println("[Provider #" + ++totalProviders +
                                "]\n[Name: " + tempReportObject.getProviderName() +
                                "]\n[# of consultations: " + tempReportObject.getConsultationCount() +
                                "]\n[Fees:" + tempReportObject. getFees() +
                                "]\n");

                    totalConsultations += tempReportObject.getConsultationCount();
                    totalFees += tempReportObject.getFees();
                }
            }

            if(totalProviders < 1) {
                System.out.println("No providers performed services within the past week.");
                out.println("[No services performed within the past week]");
            }
            else {
                out.println(    "[Summary]" +
                                "\n[# of providers: " + totalProviders +
                                "]\n[# of consultations:" + totalConsultations +
                                "]\n[Total fee: $" + totalFees +
                                "\n\n[AP Manager report concluded]");
            }
            out.close();

        } catch (FileNotFoundException error) {
            System.out.print("consultationLog.makeAPManagerReport(int[]): File not found exception.");
        }

        System.out.println("AP Manager report printed.");
        return true;
    }

    //2nd portion of the AP manager report process
    //Takes a provider ID and goes through the tree, looking for matches
    private ManagerReportObject makeAPMgrReport(int providerID) {
            int count = 0; //stores number of consultations by this provider
            ManagerReportObject toReturn;
            float feesRunningTotal = 0;
            String name = null;

            for (Consultation currentConsultation : recordedConsultations) {
                if (    currentConsultation.doesProviderIDMatch(providerID) &&
                        currentConsultation.withinPastWeek()) {
                        count++;
                        feesRunningTotal += currentConsultation.getFee();
                        name = currentConsultation.getProviderName();
                }
            }

            if(count > 0) {
                toReturn = new ManagerReportObject(feesRunningTotal, count, name);
                return toReturn;
            }
            return null;
        }
}

class ConsultationDateComp implements Comparator<Consultation> {
    @Override
    public int compare(Consultation c1, Consultation c2) {
        //If the result is negative, the first date more recent.
        //If the result is equal, the dates are equal.
        //If the result is positive, the second date is more recent.
        return c1.getDatePerformed() - c2.getDatePerformed();
    }
}

class MemberIDComp implements Comparator<Member> {
    @Override
    public int compare(Member m1, Member m2) {
        //If the result is negative, the first date more recent.
        //If the result is equal, the dates are equal.
        //If the result is positive, the second date is more recent.
        return m1.getMemberID() - m2.getMemberID();
    }
}

class ProviderIDComp implements Comparator<Provider> {
    @Override
    public int compare(Provider p1, Provider p2) {
        //If the result is negative, the first date more recent.
        //If the result is equal, the dates are equal.
        //If the result is positive, the second date is more recent.
        return p1.getProviderID() - p2.getProviderID();
    }
}

