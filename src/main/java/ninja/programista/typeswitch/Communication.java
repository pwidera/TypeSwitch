package ninja.programista.typeswitch;

public class Communication {

    static class AddCustomerEvent{}
    static class RemoveCustomerEvent{}
    static class ModifyCustomerEvent{}

    public void onEvent(AddCustomerEvent event) {
        // do stuff
    }

    public void onEvent(ModifyCustomerEvent event) {
        // do stuff
    }

    public void onEvent(RemoveCustomerEvent event) {
        // do stuff
    }

}
