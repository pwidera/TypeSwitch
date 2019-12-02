package ninja.programista.typeswitch;

import org.junit.Assert;
import org.junit.Test;

public class StringSwitch {

    private String getCountryCapitol(String country) {
        String capitol;
        switch (country) {
            case "Poland" : capitol = "Warsaw"; break;
            case "France" : capitol = "Paris"; break;
            default: capitol = "unknown"; break;
        }
        return capitol;
    }

    @Test
    public void stringTest() {
        Assert.assertEquals(getCountryCapitol("Poland"), "Warsaw");
        Assert.assertEquals(getCountryCapitol("France"), "Paris");
        Assert.assertEquals(getCountryCapitol("Uganda"), "unknown");
    }
}
