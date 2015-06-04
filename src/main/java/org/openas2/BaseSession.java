package org.openas2;

import java.util.HashMap;
import java.util.Map;

import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap;
import javax.annotation.Nonnull;

import org.openas2.cert.CertificateFactory;
import org.openas2.partner.PartnershipFactory;
import org.openas2.processor.Processor;


public abstract class BaseSession implements Session 
{
    private Map<String, Component> components;

    /**
     * Creates a <code>BaseSession</code> object, then calls the <code>init()</code> method.
     *
     * @throws OpenAS2Exception
     *
     * @see #init()
     */
    public BaseSession() throws OpenAS2Exception 
    {
        this(true);
    }

    /**
     * Creates a <code>BaseSession</code> object, with a parameter to initialize or not java mail.
     *
     * @param initJavaMail true to initialize javamail, false to do nothing.
     * @throws OpenAS2Exception
     */
    public BaseSession(boolean initJavaMail) throws OpenAS2Exception 
    {
    	if (initJavaMail)
    	{
    		initJavaMail();
    	}
    }

    /**
     * Short-cut method to retrieve a certificate factory.
     *
     * @return the currently registered <code>CertificateFactory</code> component
     *
     * @throws ComponentNotFound If a <code>CertificateFactory</code> component has not been
     *         registered
     *
     * @see CertificateFactory
     * @see Component
     */
    @Override
    @Nonnull
    public CertificateFactory getCertificateFactory() throws ComponentNotFoundException 
    {
        return (CertificateFactory) getComponent(CertificateFactory.COMPID_CERTIFICATE_FACTORY);
    }

    /**
     * Registers a component to a specified ID.
     *
     * @param componentID registers the component to this ID
     * @param comp component to register
     *
     * @see Component
     */
    @Override
    public void setComponent(@Nonnull final String componentID, @Nonnull final Component comp)
    {
        Map<String, Component> objects = getComponents();
        objects.put(componentID, comp);
    }

    /**
     * Gets the <code>Component</code> currently registered with an ID
     *
     * @param componentID ID to search for
     *
     * @return the component registered to the ID or null
     *
     * @throws ComponentNotFound If a component is not registered with the ID
     */
    @Override
    @Nonnull
    public Component getComponent(@Nonnull final String componentID) throws ComponentNotFoundException
    {
        Map<String, Component> comps = getComponents();
        Component comp = (Component) comps.get(componentID);

		if (comp == null)
		{
            throw new ComponentNotFoundException(componentID);
        }

        return comp;
    }

    
    /**
     * Return a map of component ID's to <code>Component</code> objects.
     *
     * @return all registered components, mapped by ID
     */
    @Override
    @Nonnull
    public Map<String, Component> getComponents()
    {
        if (components == null) 
        {
            components = new HashMap<>();
        }

        return components;
    }

    /**
     * Short-cut method to retrieve a partner factory.
     *
     * @return the currently registered <code>PartnerFactory</code> component
     *
     * @throws ComponentNotFound If a <code>PartnerFactory</code> component has not been registered
     *
     * @see PartnershipFactory
     * @see Component
     */
    @Override
    @Nonnull
    public PartnershipFactory getPartnershipFactory() throws ComponentNotFoundException
    {
        return (PartnershipFactory) getComponent(PartnershipFactory.COMPID_PARTNERSHIP_FACTORY);
    }

    /**
     * Short-cut method to retrieve a processor.
     *
     * @return the currently registered <code>Processor</code> component
     *
     * @throws ComponentNotFound If a <code>Processor</code> component has not been registered
     *
     * @see Processor
     * @see Component
     */
    @Override
    @Nonnull
    public Processor getProcessor() throws ComponentNotFoundException
    {
        return (Processor) getComponent(Processor.COMPID_PROCESSOR);
    }

    /**
     * Adds a group of content handlers to the Mailcap <code>CommandMap</code>. These handlers are
     * used by the JavaMail API to encode and decode information of specific mime types.
     *
     * @throws OpenAS2Exception If an error occurs while initializing mime types
     */
    private void initJavaMail() throws OpenAS2Exception 
    {
        MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
        mc.addMailcap(
            "message/disposition-notification;; x-java-content-handler=org.openas2.util.DispositionDataContentHandler");
        CommandMap.setDefaultCommandMap(mc);
    }
}
