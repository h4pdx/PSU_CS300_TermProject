package chocan;
import java.util.Calendar;
import java.util.Date;


public class Consultation {

    //Consultations store copies of 3 objects for information purposes
    protected Member memberData;
    protected Provider providerData;
    protected Service serviceData;

    //A date in MMDDYY format for using as a comparator in TreeSet
    protected int datePerformed;

    //Specific variables for date storage for the date the service was *performed*
    protected int monthPerformed;
    protected int dayPerformed;
    protected int yearPerformed;

    //Specific variables for date storage for the date the service was *recorded*
    protected int monthRecorded;
    protected int dayRecorded;
    protected int yearRecorded;
    protected int hourRecorded;
    protected int minuteRecorded;
    protected int secondRecorded;
    protected String AMPM;

    protected String comments;

    //Constructor
    public Consultation(Member tempMember,
                        Provider tempProvider,
                        Service tempService,
                        int setMonthPerformed,
                        int setDayPerformed,
                        int setYearPerformed,
                        int setMonthRecorded,
                        int setDayRecorded,
                        int setYearRecorded,
                        int setHourRecorded,
                        int setMinuteRecorded,
                        int setSecondRecorded,
                        String setAMPM,
                        String setComment) {
        memberData = new Member(tempMember);
        providerData = new Provider(tempProvider);
        serviceData = new Service(tempService);
        monthPerformed = setMonthPerformed;
        dayPerformed = setDayPerformed;
        yearPerformed = setYearPerformed;
        monthRecorded = setMonthRecorded;
        dayRecorded = setDayRecorded;
        yearRecorded = setYearRecorded;
        hourRecorded = setHourRecorded;
        minuteRecorded = setMinuteRecorded;
        secondRecorded = setSecondRecorded;
        AMPM = setAMPM;
        comments = setComment;

        //Below lines creates a date in the format MMDDYY for comparison and adding to the tree
        if (monthPerformed > 9)
            datePerformed = monthPerformed * 100000; //Setting MM in MMDDYY
        else
            datePerformed = monthPerformed * 10000; //Setting M in 0MDDYY

        if (dayPerformed > 9)
            datePerformed = datePerformed + (dayPerformed * 1000); //Setting DD in MMDDYY
        else
            datePerformed = datePerformed + (dayPerformed * 100); //Setting 0D in MM0DYY

        datePerformed = datePerformed + yearPerformed; //Setting YY in MMDDYY
    } //Constructor end

    public int getDatePerformed() {
        return datePerformed;
    }
    public String getMemberName() {
        return memberData.getMemberName();
    }
    public String getMemberAddress() {
        return memberData.getMemberAddress();
    }
    public String getProviderName() {
        return providerData.getProviderName();
    }
    public String getProviderAddress() {
        return providerData.getProviderAddress();
    }
    public int getProviderID() {
        return providerData.getProviderID();
    }
    public float getFee() {
        return serviceData.getServiceCost();
    }

    Member getMember() { return memberData; }
    Provider getProvider () { return providerData; }

    //makeMemberReport writes info from this consultation to a file for the member report.
    String getMemberReportString() {
        return  "[Date of service: " + monthPerformed + "/" + dayPerformed + "/" + yearPerformed + "]\n" +
                "[Provider name: " + providerData.getProviderName() + "]\n" +
                "[Service name: " + serviceData.getServiceName() + "]\n";
    }

    //makeMemberReport writes info from this consultation to a file for the provider report.
    String getProviderReportString() {
            return  "[Date of service: " + monthPerformed + "/" + dayPerformed + "/" + yearPerformed + "]\n" +
                    "[Date and time record was created: " +
                    monthRecorded + "/" +
                    dayRecorded + "/" +
                    yearRecorded + " at " +
                    hourRecorded + ":" +
                    minuteRecorded + ":" +
                    secondRecorded + " " +
                    AMPM + "]\n" +
                    "[Member name: " + memberData.getMemberName() +
                    "]\n[Member ID: " + memberData.getMemberID() +
                    "]\n[Service code: " + serviceData.getServiceNumber() +
                    "]\n[Fee: $" + serviceData.getServiceCost() + "]\n";
    }

    //For building reports and seeing if this consultation was done with a specific member
    public boolean doesMemberIDMatch(int compareID) {
        if (compareID == memberData.getMemberID())
            return true;
        return false;
    }

    //For building reports and seeing if this consultation was done with a specific provider
    public boolean doesProviderIDMatch(int compareID) {
        if (compareID == providerData.getProviderID())
            return true;

        return false;
    }

    //Sees if this consultation was performed within the past week.
    public Boolean withinPastWeek() {
        //Returns true if compareDay is the same day or in the past 7 day
        java.util.Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        if(cal.get(Calendar.YEAR) != yearPerformed)
            return false;
        if((cal.get(Calendar.MONTH) + 1) != monthPerformed)
            return false;
        if (cal.get(Calendar.DAY_OF_MONTH) - dayPerformed < 0) //Result is negative, meaning the dayPerformed is in the future
            return false;
        if (cal.get(Calendar.DAY_OF_MONTH) - dayPerformed <= 7) //Result is 7 or less, but not negative. Compared day is within a week.
            return true;
        else
            return false; //Result of dayPerformed-compareDay is greater than 7, and is too many days away.
    }

    public String consultationInfoToString() {
        return (monthPerformed  + ":" +
                dayPerformed    + ":" +
                yearPerformed   + ":" +
                monthRecorded   + ":" +
                dayRecorded     + ":" +
                yearRecorded    + ":" +
                hourRecorded    + ":" +
                minuteRecorded  + ":" +
                secondRecorded  + ":" +
                AMPM            + ":" +
                comments        + ":");
    }

    public String getProviderDataString() {
        return providerData.toString();
    }

    public String getMemberDataString() {
        return memberData.toString();
    }

    public String getServiceDataString() {
        return serviceData.toString();
    }

}




