//this class represents a signle provider

package chocan;

public class Provider
{
    private String providerName;
    private int providerID;
    private String providerAddress;
    private String providerCity;
    private String providerState;
    private int providerZip;
    private int numberOfConsultations;
    private int feeForAWeek;

    public Provider(String providerName, int providerID, String providerAddress, String providerCity, String providerState, int providerZip, int numberOfConsultations, int feeForAWeek) {
        this.providerName = providerName;
        this.providerID = providerID;
        this.providerAddress = providerAddress;
        this.providerCity = providerCity;
        this.providerState = providerState;
        this.providerZip = providerZip;
        this.numberOfConsultations = numberOfConsultations;
        this.feeForAWeek = feeForAWeek;
    }
    
    public Provider(Provider source) {
        this.providerName = source.providerName;
        this.providerID = source.providerID;
        this.providerAddress = source.providerAddress;
        this.providerCity = source.providerCity;
        this.providerState = source.providerState;
        this.providerZip = source.providerZip;
        this.numberOfConsultations = source.numberOfConsultations;
        this.feeForAWeek = source.feeForAWeek;
    }
    
    public String toString() {
        return providerName + ":" + providerID + ":" + providerAddress + ":" + providerCity + ":" + providerState + ":" + providerZip + ":" + numberOfConsultations + ":" + feeForAWeek;
    }
    
    public void addAConsultation() {
        this.numberOfConsultations += 1;
    }

    public int getNumberOfConsultations() {
        return this.numberOfConsultations;
    }

    public String getProviderAddress() {
        return this.providerAddress +" "+ this.providerCity +", "+ this.providerState +" "+ this.providerZip;
    }
    
    public String getProviderName() {
        return providerName;
    }

    public int getProviderID() {
        return providerID;
    }

    public void displayAll() {
        System.out.println(this.providerName +" - "+ this.providerID);
        System.out.println(this.providerAddress +"\n"+ this.providerCity +", " + this.providerState +" "+ this.providerZip);
        System.out.println(this.numberOfConsultations);
        System.out.println(this.feeForAWeek);
    }
}
