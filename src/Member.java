/**
* This class represents a single member
*
* @author Daniel Reimer
* @since  2017-11-27
*/
package chocan;
public class Member
{
    private String memberName;
    private int memberID;
    private String memberAddress;
    private String memberCity;
    private String memberState;
    private int memberZip;
    private boolean memberStatus;

    public Member(String memberName, int memberID, String memberAddress, String memberCity, String memberState, int memberZip, boolean memberStatus) {
        this.memberName = memberName;
        this.memberID = memberID;
        this.memberAddress = memberAddress;
        this.memberCity = memberCity;
        this.memberState = memberState;
        this.memberZip = memberZip;
        this.memberStatus = memberStatus;
    }
    public Member(Member source) {
        this.memberName = source.memberName;
	    this.memberID = source.memberID;
        this.memberAddress = source.memberAddress;
        this.memberCity = source.memberCity;
        this.memberState = source.memberState;
        this.memberZip = source.memberZip;
        this.memberStatus = source.memberStatus;
    }
    public String toString() {
        return memberName + ":" + memberID + ":" + memberAddress + ":" + memberCity + ":" + memberState + ":" + memberZip + ":" + memberStatus;
    }

    public boolean isMemberSuspended() {
        if(this.memberStatus == true) {
            return false;
        } else {
            return true;
        }
    }

    public void setMemberStatus(boolean updatedStatus) {
        this.memberStatus = updatedStatus;
    }
    
    public String getMemberName() {
        return this.memberName;
    }

    public int getMemberID() {
        return this.memberID;
    }

    public String getMemberAddress() {
        return this.memberAddress +" "+ this.memberCity +", "+ this.memberState +" "+ this.memberZip;
    }

    public void displayAll() {
	    System.out.println("\n" + this.memberName + " - " + this.memberID);
	    System.out.println(this.memberAddress + "\n" + this.memberCity + ", " + this.memberState + " " + this.memberZip);
        System.out.println("Membership Status: "); 
	    if (memberStatus == true) {
             System.out.println("VALID");
	    } else {
            System.out.println("NOT VALID");
	    }
    }
}
