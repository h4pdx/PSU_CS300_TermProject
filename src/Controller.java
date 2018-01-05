package chocan;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Calendar;

public class Controller {

    private ConsultationLog     theConsults;    // Stores consultation history.
    private Database            theData;        // Holds data used to make new consultations and reports.


    /*
    * Controller construction methods
    *
    *
    * */



    private boolean buildLog() {
        boolean             toRet = true;
        java.lang.String    dataFile = new java.lang.String("consultationHistory.txt");
        Scanner             srcFile = null;
        Member              mem = null;
        Provider            prov = null;
        Service             serv = null;
        Consultation        aConsult = null;

        try {
            srcFile = new Scanner(new File(dataFile));
            srcFile.useDelimiter(":\\s*");
            this.theConsults = new ConsultationLog();

            while (srcFile.hasNextLine()) {
                try {
                    mem = new Member(srcFile.next(),
                            Integer.parseInt(srcFile.next()),
                            srcFile.next(),
                            srcFile.next(),
                            srcFile.next(),
                            srcFile.nextInt(),
                            Boolean.parseBoolean(srcFile.next()));
                } catch (NoSuchElementException fileComplete) { break; }

                prov = new Provider(srcFile.next(),
                        Integer.parseInt(srcFile.next()),
                        srcFile.next(),
                        srcFile.next(),
                        srcFile.next(),
                        Integer.parseInt(srcFile.next()),
                        Integer.parseInt(srcFile.next()),
                        Integer.parseInt(srcFile.next()));

                serv = new Service(srcFile.next(),
                        Integer.parseInt(srcFile.next()),
                        Integer.parseInt(srcFile.next()));

                aConsult = new Consultation(mem, prov, serv,
                        srcFile.nextInt(),
                        srcFile.nextInt(),
                        srcFile.nextInt(),
                        srcFile.nextInt(),
                        srcFile.nextInt(),
                        srcFile.nextInt(),
                        srcFile.nextInt(),
                        srcFile.nextInt(),
                        srcFile.nextInt(),
                        srcFile.next(),
                        srcFile.next());

                theConsults.add(aConsult); // This consultation method throws a NullPointerException.
            }

            srcFile.close();
        }

        catch (FileNotFoundException noBaseFile) { toRet = false; }

        return toRet;
    } // end of function


    public Controller(){
        try{
            theData = new Database();
            throw new FileNotFoundException(); //!!!!!!!!!!!!!!!!!!!!make sure to remove this
        }catch(FileNotFoundException e) {
            //System.out.println("File not found"); make sure to comment this correctly
        }

        this.buildLog();
    } // end of function


    /*****************************************

    Methods that interact with Database

    *******************************************/


    public Boolean isManager (int token){
    	if(theData.isManager(token))
    		return true;
    	else return false;
    } // end of function


    public Boolean isProvider (int token){
    	return theData.isProvider(token);
    } // end of functions



    public Member searchMember (int memberNumber){
        return theData.searchMember(memberNumber);
    } // end of function


    public Provider searchProvider( int providerNumber) {
        return theData.searchProvider(providerNumber);
    } // end of function


    public Service searchService (int serviceCode) {
        return theData.searchService(serviceCode);
    } // end of function


    public void displayAllProviders(){
        System.out.println("Printing all providers: ");
        theData.displayAllProviders();
    }


    public Boolean addService (Service toAdd) {
         Boolean     toRet = false;

             if (toAdd != null) {
                 toRet = this.theData.addService (toAdd);
             }

         return toRet;
     } // end of function


    public Boolean addMember (Member toAdd) {
         Boolean     success = false;

         if (toAdd != null) {
             success = this.theData.addMember (toAdd);
         }

         return success;
     } // end of function


    public Boolean addProvider (Provider toAdd) {
         Boolean success = false;

         if (toAdd != null) {
             success = this.theData.addProvider (toAdd);
         }

         return success;
     } // end of function


    public Boolean removeMember (Member toRemove) {
         Boolean     success = false;

         if (toRemove != null) {
             //success = this.theData.removeMember (toAdd);
         }

         return success;
     } // end of function


    public Boolean removeProvider (Provider toRemove) {
         Boolean success = false;

         if (toRemove != null) {
             //success = this.theData.removeProvider (toRemove);
         }

         return success;
     } // end of function


    public Boolean removeMember (int key) {
        return theData.deleteMember(key);
    } // end of function


    public Boolean removeProvider (int key) {
        return theData.deleteProvider(key);
    } // end of function


    public Boolean removeService (int key) {
        return theData.deleteService(key);
    } // end of function



 /*************************************************

    Methods that interact with ConsultationLog and Consultation.

 **************************************************/


    public Boolean makeConsultation (Service aServ, Provider aProv, Member aMem, int month, int day, int year, String comment) {
        DateFormat          dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        GregorianCalendar   cal = new GregorianCalendar();

        int             currMonth = cal.get(Calendar.MONTH) + 1;
        int             currYear = cal.get(Calendar.YEAR);
        int             currDay = cal.get(Calendar.DAY_OF_WEEK);
        int             currHour = cal.get(Calendar.HOUR);
        int             currMin = cal.get(Calendar.MINUTE);
        int             currSec = cal.get(Calendar.SECOND);
        String          AMPM;

        if (cal.get(Calendar.AM_PM) == Calendar.AM) {
            AMPM = new String("AM");
        } else {
            AMPM = new String("PM");
        }

        if (comment == null) {
            comment = new String("Comment(s): ");
        }

        return theConsults.add(new Consultation(aMem, aProv, aServ, month, day, year,
                                        currMonth, currDay, currYear, currHour, currMin, currSec, AMPM, comment));
     } // end of function


    // The formatting of the out data file needs to be as expected by the controller.
    public Boolean saveData () {
    	theData.finalize(); // Writes text files
        return      theConsults.writeLogToFile();
 } // end of function


    public Boolean makeMemberReport () {
        return theConsults.makeMemberReport();
    } // end of function


    public Boolean makeProviderAndEFTReport () {
        return theConsults.makeProviderAndEFTReport();
    } // end of function


    public Boolean makeManagerReport () {
     return theConsults.makeAPMgrReport();
    } // end of function



} // end of class
