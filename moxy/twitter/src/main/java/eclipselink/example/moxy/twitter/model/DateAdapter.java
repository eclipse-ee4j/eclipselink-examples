package eclipselink.example.moxy.twitter.model;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class DateAdapter extends XmlAdapter<String, Date> {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");

    @Override
    public String marshal(Date date) throws Exception {
        return dateFormat.format(date);
    }

    @Override
    public Date unmarshal(String string) throws Exception {
        return dateFormat.parse(string);
    }

}