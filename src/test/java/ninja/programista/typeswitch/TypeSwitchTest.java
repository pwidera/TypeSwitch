package ninja.programista.typeswitch;

import ninja.programista.typeswitch.Communication.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.logging.Logger;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TypeSwitchTest {

    @Mock
    private Communication c;

    @Mock
    private Logger logger;

    private TypeSwitch myTypeSwitch;

    @Test
    public void switchOverClassTest() {
        processEvent(new AddCustomerEvent());
        processEvent(new ModifyCustomerEvent());
        processEvent(new RemoveCustomerEvent());

        verify(c).onEvent(any(AddCustomerEvent.class));
        verify(c).onEvent(any(ModifyCustomerEvent.class));
        verify(c).onEvent(any(RemoveCustomerEvent.class));
    }

    private void processEvent(Object event) {
        switch (event.getClass().getSimpleName()) {
            case "AddCustomerEvent": c.onEvent((AddCustomerEvent)event);
                break;
            case "ModifyCustomerEvent": c.onEvent((ModifyCustomerEvent) event);
                break;
            case "RemoveCustomerEvent": c.onEvent((RemoveCustomerEvent)event);
                break;
            default:
                // throw unknown event exception
        }
    }

    @Test
    public void matchTest() {
        myTypeSwitch = TypeSwitchBuilder.getInstance()
                .with(AddCustomerEvent.class, c::onEvent)
                .with(ModifyCustomerEvent.class, c::onEvent)
                .with(RemoveCustomerEvent.class, c::onEvent)
                .build();

        myTypeSwitch.handle(new AddCustomerEvent());
        myTypeSwitch.handle(new ModifyCustomerEvent());
        myTypeSwitch.handle(new RemoveCustomerEvent());

        verify(c).onEvent(any(AddCustomerEvent.class));
        verify(c).onEvent(any(ModifyCustomerEvent.class));
        verify(c).onEvent(any(RemoveCustomerEvent.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void missingMatch() {
        myTypeSwitch = TypeSwitchBuilder.getInstance()
                .onMismath(this::throwException);

        myTypeSwitch.handle(new String());
    }

    private void throwException(Object o) {
        throw new IllegalArgumentException("missing match for event " + c.getClass());
    }


    @Test
    public void quietMismath() {
        myTypeSwitch = TypeSwitchBuilder.getInstance();

        myTypeSwitch.handle(new String());
    }

    @Test(expected = CustomException.class)
    public void onError() {
        myTypeSwitch = TypeSwitchBuilder.getInstance()
                .with(RemoveCustomerEvent.class, c::onEvent)
                .onError(TypeSwitchTest::replaceWithCustomError);

        Mockito.doThrow(new IllegalStateException()).when(c).onEvent(any(RemoveCustomerEvent.class));

        // this method throws IllegalStateException, but we catch it and replace it with CustomException
        myTypeSwitch.handle(new RemoveCustomerEvent());
    }

    private static void replaceWithCustomError(Exception e) {
        throw new CustomException(e);
    }

    @Test
    public void withLog() {
        myTypeSwitch = TypeSwitchBuilder.getInstance()
                .with(AddCustomerEvent.class, c::onEvent)
                .with(ModifyCustomerEvent.class, c::onEvent)
                .with(RemoveCustomerEvent.class, c::onEvent)
                .withPerfLog((o, duration) ->
                        logger.info(String.format("Processed event %s in time %d", o, duration)));


        myTypeSwitch.handle(new AddCustomerEvent());
        myTypeSwitch.handle(new ModifyCustomerEvent());
        myTypeSwitch.handle(new RemoveCustomerEvent());

        verify(logger).info(contains("Processed event ninja.programista.typeswitch.Communication$AddCustomerEvent"));
        verify(logger).info(contains("Processed event ninja.programista.typeswitch.Communication$ModifyCustomerEvent"));
        verify(logger).info(contains("Processed event ninja.programista.typeswitch.Communication$RemoveCustomerEvent"));
    }

}
