package net.codjo.product.ontology.plugin;
import net.codjo.agent.Agent;
import net.codjo.product.ontology.message.Status;
/**
 *
 */
public interface ProductOntologyPluginOperations {
    String toXml(Status status);


    Status fromXml(String xml);


    ProductOntology createProductOntology(Agent agent);
}
