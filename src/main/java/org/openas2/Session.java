package org.openas2;

import java.util.Map;

import javax.annotation.Nonnull;

import org.openas2.cert.CertificateFactory;
import org.openas2.partner.PartnershipFactory;
import org.openas2.processor.Processor;


/**
 * The <code>Session</code> interface provides configuration and resource information, and a means for
 * components to access the functionality of other components.
 *
 * @author Aaron Silinskas
 *
 * @see Component
 * @see org.openas2.cert.CertificateFactory
 * @see org.openas2.partner.PartnershipFactory
 * @see org.openas2.processor.Processor 
 */
public interface Session 
{
    /** Official OpenAS2 title */
    public static final String TITLE = IOpenAs2.NAME + " v" + IOpenAs2.CURRENT_VERSION;

    /**
     * Short-cut method to retrieve a certificate factory.
     *
     * @return the currently registered <code>CertificateFactory</code> component
     *
     * @throws ComponentNotFoundException If a <code>CertificateFactory</code> component has not been
     *         registered
     *
     * @see CertificateFactory
     * @see Component
     */
    @Nonnull
    CertificateFactory getCertificateFactory() throws ComponentNotFoundException;

    /**
     * Registers a component to a specified ID.
     *
     * @param componentID registers the component to this ID
     * @param comp component to register
     *
     * @see Component
     */
    void setComponent(@Nonnull final String componentID, @Nonnull final Component comp);

    /**
     * Gets the <code>Component</code> currently registered with an ID
     *
     * @param componentID ID to search for
     *
     * @return the component registered to the ID or null
     *
     * @throws ComponentNotFoundException If a component is not registered with the ID
     */
    @Nonnull
    Component getComponent(@Nonnull final String componentID) throws ComponentNotFoundException;

    /**
     * Return a map of component ID's to <code>Component</code> objects.
     *
     * @return all registered components, mapped by ID
     */
    @Nonnull
    Map<String, Component> getComponents();

    /**
     * Short-cut method to retrieve a partner factory.
     *
     * @return the currently registered <code>PartnerFactory</code> component
     *
     * @throws ComponentNotFoundException If a <code>PartnerFactory</code> component has not been registered
     *
     * @see PartnershipFactory
     * @see Component
     */
    @Nonnull
    PartnershipFactory getPartnershipFactory() throws ComponentNotFoundException;

    /**
     * Short-cut method to retrieve a processor.
     *
     * @return the currently registered <code>Processor</code> component
     *
     * @throws ComponentNotFoundException If a <code>Processor</code> component has not been registered
     * @see Processor
     * @see Component
     */
    @Nonnull
    Processor getProcessor() throws ComponentNotFoundException;
}
