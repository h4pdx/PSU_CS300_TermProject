//this class represents a single service

package chocan;

public class Service
{
    private String serviceName;
    private int serviceNumber;
    private int serviceCost;
    
    public Service(String serviceName, int serviceNumber, int serviceCost) {
        this.serviceName = serviceName;
        this.serviceNumber = serviceNumber;
        this.serviceCost = serviceCost;
    }

    public Service(Service source) {
        this.serviceName = source.serviceName;
        this.serviceNumber = source.serviceNumber;
        this.serviceCost = source.serviceCost;
    }
    
    public String toString() { 
        return serviceName + ":" + serviceNumber + ":" + serviceCost;
    }  
    
    public String getServiceName() {
        return this.serviceName;
    }

    public int getServiceNumber() {
        return this.serviceNumber;
    }

    public int getServiceCost() {
        return this.serviceCost;
    }

    public void displayAll() {
        System.out.println(this.serviceName);
        System.out.println(this.serviceNumber);
        System.out.println(this.serviceCost);
    }
}
