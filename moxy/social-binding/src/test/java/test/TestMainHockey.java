package test;

import javax.xml.bind.JAXBException;

import org.junit.Test;

import eclipselink.example.moxy.socialbinding.Main;

public class TestMainHockey {

    @Test
    public void test() throws JAXBException {
        Main.main(new String[] {"hockey"});
    }
}
