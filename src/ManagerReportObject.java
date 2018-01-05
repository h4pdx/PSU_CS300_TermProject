package chocan;
public class ManagerReportObject {
    private float consultantFees;
    private int consultationCount;
    private String providerName;

    ManagerReportObject(float setConsultantFees, int setConsultationCount, String setProviderName) {
        consultantFees = setConsultantFees;
        consultationCount = setConsultationCount;
        providerName = setProviderName;
    }

    public float getFees() {
        return consultantFees;
    }

    public int getConsultationCount() {
        return consultationCount;
    }

    public String getProviderName() {
        return providerName;
    }
}
