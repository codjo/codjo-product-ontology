package net.codjo.product.ontology.plugin;
import net.codjo.ontology.common.xml.OntologyConfiguration;
import net.codjo.ontology.common.xml.OntologyXmlizer;
import net.codjo.product.ontology.message.Status;
import net.codjo.reflect.collect.ClassCollector;
import net.codjo.reflect.collect.PackageFilter;
import java.io.IOException;
/**
 *
 */
class ProductXmlizer {

    private final OntologyConfiguration productConfiguration = new OntologyConfiguration();


    ProductXmlizer() {
        ClassCollector classCollector = new ClassCollector(Status.class);
        Class[] classes = new Class[0];
        try {
            classCollector.addClassFilter(new PackageFilter(Status.class.getPackage().getName(), false));
            classes = classCollector.collect();
        }
        catch (IOException e) {
            //
        }
        for (Class aClass : classes) {
            productConfiguration.addAlias(aClass);
        }

        productConfiguration.setXsdFile("product.xsd");
    }


    public String toXml(Status status) {
        return OntologyXmlizer.toXml(status, productConfiguration);
    }


    public Status fromXml(String xml) {
        return (Status)OntologyXmlizer.fromXml(xml, productConfiguration);
    }
}
