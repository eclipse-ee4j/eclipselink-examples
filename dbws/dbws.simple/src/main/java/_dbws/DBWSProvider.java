package _dbws;

//Java extension libraries
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.BindingType;
import javax.xml.ws.Provider;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.WebServiceProvider;
import static javax.xml.ws.Service.Mode.MESSAGE;

//EclipseLink imports
import org.eclipse.persistence.internal.dbws.ProviderHelper;

@WebServiceProvider(
    wsdlLocation = "WEB-INF/wsdl/eclipselink-dbws.wsdl",
    serviceName = "empService",
    portName = "empServicePort",
    targetNamespace = "urn:empService"
)
@ServiceMode(MESSAGE)
public class DBWSProvider extends ProviderHelper implements Provider<SOAPMessage> {

    // Container injects wsContext here
    @Resource
    protected WebServiceContext wsContext;

    public  DBWSProvider() {
        super();
    }

    @PostConstruct
    public void init() {
        ClassLoader parentClassLoader = Thread.currentThread().getContextClassLoader();
        ServletContext sc = ProviderListener.SC;
        boolean mtomEnabled = false;
        BindingType thisBindingType = this.getClass().getAnnotation(BindingType.class);
        if (thisBindingType != null) {
            if (thisBindingType.value().toLowerCase().contains("mtom=true")) {
                mtomEnabled = true;
            }
        }
        super.init(parentClassLoader, sc, mtomEnabled);
    }

    @Override
    public SOAPMessage invoke(SOAPMessage request) {
        if (wsContext != null) {
            setMessageContext(wsContext.getMessageContext());
        }
        return super.invoke(request);
    }

    @Override
    @PreDestroy
    public void destroy() {
        super.destroy();
    }
};
