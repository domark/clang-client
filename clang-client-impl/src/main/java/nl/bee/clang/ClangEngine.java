/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.bee.clang;

import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.ws.Holder;
import javax.xml.ws.soap.SOAPFaultException;
import nl.bee.clang.ws.ArrayOfClick;
import nl.bee.clang.ws.ArrayOfCustomer;
import nl.bee.clang.ws.ArrayOfDelivery;
import nl.bee.clang.ws.ArrayOfGroup;
import nl.bee.clang.ws.ArrayOfMailing;
import nl.bee.clang.ws.ArrayOfMethodOptions;
import nl.bee.clang.ws.ArrayOfOption;
import nl.bee.clang.ws.ClangAPI;
import nl.bee.clang.ws.ClangService;
import nl.bee.clang.ws.Click;
import nl.bee.clang.ws.Customer;
import nl.bee.clang.ws.Delivery;
import nl.bee.clang.ws.Email;
import nl.bee.clang.ws.Group;
import nl.bee.clang.ws.Mailing;
import nl.bee.clang.ws.MailingLink;
import nl.bee.clang.ws.Resource;

/**
 *
 * @author root
 */
public final class ClangEngine {

    private ClangService clangService;
    private String token;
    private static final String PROCESSING = "PROCESSING";
    private static final String READY = "READY";
    private static final int BATCHSIZE = 50;
    private static final int MILLIS = 1000;

    public ClangEngine(String token) throws ClangException {
        this(token, "https://secure.myclang.com/app/api/soap/public/index.php?wsdl");
    }

    public ClangEngine(String token, String wsdl) throws ClangException {
        try {
            clangService = new ClangAPI(new URL(wsdl), new QName("https://secure.myclang.com/app/api/soap/public/index.php", "clangAPI")).getClangPort();

            clangService.smsListOptions(token, new Holder<BigInteger>(), new Holder<ArrayOfMethodOptions>());
            this.token = token;
        } catch (MalformedURLException e) {
            throw new ClangException(e.getMessage(), e);
        } catch (SOAPFaultException e) {
            throw new ClangException(e.getFault().getFaultCode(), e);
        }
    }
    /*
     * Create customer returns a holder Customer object.
     * No customer is inserted into the database of Clang
     */
    public Customer createCustomer(String firstName, String lastName, String emailAddress) throws ClangException {
        Holder<BigInteger> code = new Holder<BigInteger>();
        Holder<Customer> customer = new Holder<Customer>();
        clangService.customerCreate(token, code, customer);
        if (code.value.equals(BigInteger.ZERO)) {
            customer.value.setFirstname(firstName);
            customer.value.setLastname(lastName);
            customer.value.setEmailAddress(emailAddress);
            return customer.value;
        } else {
            throw new ClangException(code);
        }
    }

    public Customer createCustomer(String firstName, String lastName, String emailAddress, String externalId) throws ClangException {
        Customer createCustomer = createCustomer(firstName, lastName, emailAddress);
        createCustomer.setExternalId(externalId);
        return createCustomer;
    }

    public Customer insertCustomer(Customer customer) {
        Holder<BigInteger> code = new Holder<BigInteger>();
        Holder<Customer> msg = new Holder<Customer>();
        clangService.customerInsert(token, customer, code, msg);
        return msg.value;
    }

    public Customer updateCustomer(Customer customer){
        Holder<BigInteger> code = new Holder<BigInteger>();
        Holder<Customer> msg = new Holder<Customer>();
        
        clangService.customerUpdate(token, customer, code, msg);
        return msg.value;
    }

    public Customer upsertCustomer(Customer customer){
        Holder<BigInteger> code = new Holder<BigInteger>();
        Holder<Customer> msg = new Holder<Customer>();
        
        clangService.customerUpsert(token, customer, code, msg);
        return msg.value;
    }
    
    public Boolean deleteCustomer(Customer customer){
        Holder<BigInteger> code = new Holder<BigInteger>();
        Holder<Boolean> msg = new Holder<Boolean>();
        
        clangService.customerDelete(token, customer, code, msg);
        return msg.value;
    }

    public Customer getCustomer(long id) throws ClangException {
        Holder<BigInteger> code = new Holder<BigInteger>();
        Holder<Customer> msg = new Holder<Customer>();
        try {
            clangService.customerGetById(token, id, code, msg);
            return msg.value;
        } catch (SOAPFaultException e) {
            throw new ClangException(e.getFault().getFaultCode(), e);
        }
    }

    public List<Customer> getCustomer(String emailAddress) {
        Holder<BigInteger> code = new Holder<BigInteger>();
        Holder<ArrayOfCustomer> msg = new Holder<ArrayOfCustomer>();

        clangService.customerGetByEmailAddress(token, emailAddress, code, msg);
        return msg.value.getCustomer();
    }

    public Customer getCustomerById(Long customerId){
        Holder<BigInteger> code = new Holder<BigInteger>();
        Holder<Customer> msg = new Holder<Customer>();

        clangService.customerGetById(token, customerId, code, msg);
        return msg.value;
    }

    /*
     *
     */
    public List<Customer> getCustomerByExternalId(String customerExternalId){
        Holder<BigInteger> code = new Holder<BigInteger>();
        Holder<ArrayOfCustomer> msg = new Holder<ArrayOfCustomer>();

        clangService.customerGetByExternalId(token, customerExternalId, code, msg);
        return msg.value.getCustomer();
    }

    //FIXME make test data
    public List<Customer> getCustomerByPassword(String username, String password) throws ClangException {
        Holder<BigInteger> code = new Holder<BigInteger>();
        Holder<ArrayOfCustomer> msg = new Holder<ArrayOfCustomer>();

        clangService.customerGetByUserNameAndPassword(token, username, password, code, msg);
        if (msg.value.getCustomer().size() > 1){
            throw new ClangException("Too many results for username/pasword combination");
        }
        return msg.value.getCustomer();
    }

    public List<Customer> getCustomers() throws InterruptedException, ClangException {
        Holder<BigInteger> code = new Holder<BigInteger>();
        Holder<Long> resourceId = new Holder<Long>();

        clangService.customerGetAll(token, code, resourceId);

        return getCustomerSet(resourceId);
    }

    public List<Customer> getCustomersByCriteria(Customer customerCriteria) throws InterruptedException, ClangException {
        Holder<BigInteger> code = new Holder<BigInteger>();
        Holder<Long> resourceId = new Holder<Long>();

        clangService.customerGetByObject(token, customerCriteria, code, resourceId);

        return getCustomerSet(resourceId);
    }

    private List<Customer> getCustomerSet(Holder<Long> resourceId) throws InterruptedException, ClangException {
        List<Customer> result = new ArrayList<Customer>();

        Holder<BigInteger> code = new Holder<BigInteger>();
        Holder<Resource> resource = new Holder<Resource>();

        try {
            do {
                clangService.resourceGetById(token, resourceId.value, code, resource);
                Thread.sleep(MILLIS);
            } while (resource.value.getStatus().equals(PROCESSING));

            if (resource.value.getStatus().equals(READY)) {
                int offset = 0;
                int batch = BATCHSIZE;

                Holder<ArrayOfCustomer> customers = new Holder<ArrayOfCustomer>();
                while (offset < resource.value.getSize()) {
                    clangService.customerSetGetCustomers(token, resourceId.value, offset, batch, code, customers);
                    List<Customer> customer = customers.value.getCustomer();
                    result.addAll(customer);
                    offset += batch;
                }
                clangService.resourceFree(token, resourceId.value, code, new Holder<Boolean>());
            }
        } catch (SOAPFaultException e) {
            throw new ClangException(e.getFault().getFaultCode(), e);
        }

        return result;

    }

    public Group getGroup(long id) throws ClangException {
        Holder<BigInteger> code = new Holder<BigInteger>();
        Holder<Group> group = new Holder<Group>();

        try {
            clangService.groupGetById(token, id, code, group);
            return group.value;
        } catch (Exception e) {
            if (code.value != null){
                throw new ClangException(code, e);
            } else {
                throw new ClangException("no group found", e);
            }
        }
    }

    public List<Group> getGroups(){
        Holder<BigInteger> code = new Holder<BigInteger>();
        Holder<ArrayOfGroup> groups = new Holder<ArrayOfGroup>();

        clangService.groupGetAll(token, code, groups);
        return groups.value.getGroup();
    }

    public List<Customer> getGroupMembers(Long groupId, Boolean includeRecursive) throws InterruptedException, ClangException{
        Holder<BigInteger> code = new Holder<BigInteger>();
        Holder<Long> resourceId = new Holder<Long>();

        clangService.groupGetMembers(token, groupId, includeRecursive, code, resourceId);

        return getCustomerSet(resourceId);
    }

    public Boolean removeFromGroup(Group group, Customer customer){
        Holder<BigInteger> code = new Holder<BigInteger>();
        Holder<Boolean> result = new Holder<Boolean>();

        clangService.groupRemoveMember(token, group, customer, code, result);
        return result.value;
    }

    public List<Group> getGroupsByCustomer(Long customerId){
        Holder<BigInteger> code = new Holder<BigInteger>();
        Holder<ArrayOfGroup> groups = new Holder<ArrayOfGroup>();

        clangService.customerGetGroups(token, customerId, code, groups);
        return groups.value.getGroup();
    }

    public Boolean addToGroup(Group group, Customer customer){
        Holder<BigInteger> code = new Holder<BigInteger>();
        Holder<Boolean> result = new Holder<Boolean>();

        clangService.groupAddMember(token, group, customer, code, result);
        return result.value;
    }

    public Mailing getMailing(Long id){
        Holder<BigInteger> code = new Holder<BigInteger>();
        Holder<Mailing> mailing = new Holder<Mailing>();

        clangService.mailingGetById(token, id, code, mailing);
        return mailing.value;
    }

    public List<Mailing> getMailings() throws InterruptedException, ClangException {
        Holder<BigInteger> code = new Holder<BigInteger>();
        Holder<Long> resourceId = new Holder<Long>();
        
        clangService.mailingGetAll(token, code, resourceId);

        return getMailingSet(resourceId);
    }

    public List<Mailing> getQuickMailings() throws InterruptedException, ClangException {
        Holder<BigInteger> code = new Holder<BigInteger>();
        Holder<Long> resourceId = new Holder<Long>();

        clangService.mailingGetQuickmails(token, code, resourceId);

        return getMailingSet(resourceId);
    }

    private List<Mailing> getMailingSet(Holder<Long> resourceId) throws InterruptedException, ClangException {
        List<Mailing> result = new ArrayList<Mailing>();

        Holder<BigInteger> code = new Holder<BigInteger>();
        Holder<Resource> resource = new Holder<Resource>();

        try {
            do {
                clangService.resourceGetById(token, resourceId.value, code, resource);
                Thread.sleep(MILLIS);
            } while (resource.value.getStatus().equals(PROCESSING));

            if (resource.value.getStatus().equals(READY)) {
                int offset = 0;
                int batch = BATCHSIZE;

                Holder<ArrayOfMailing> mailings = new Holder<ArrayOfMailing>();
                while (offset < resource.value.getSize()) {
                    clangService.mailingSetGetMailings(token, resourceId.value, offset, batch, code, mailings);
                    List<Mailing> mailing = mailings.value.getMailing();
                    result.addAll(mailing);
                    offset += batch;
                }
                clangService.resourceFree(token, resourceId.value, code, new Holder<Boolean>());
            }
        } catch (SOAPFaultException e) {
            throw new ClangException(e.getFault().getFaultCode(), e);
        }

        return result;
    }

    public List<Delivery> getDeliveriesByMailing(Mailing mailing) throws InterruptedException, ClangException{
        Holder<BigInteger> code = new Holder<BigInteger>();
        Holder<Long> resourceId = new Holder<Long>();

        clangService.mailingGetDeliverySet(token, mailing, code, resourceId);

        return getDeliverySet(resourceId);
    }

    private List<Delivery> getDeliverySet(Holder<Long> resourceId) throws InterruptedException, ClangException {
        List<Delivery> result = new ArrayList<Delivery>();

        Holder<BigInteger> code = new Holder<BigInteger>();
        Holder<Resource> resource = new Holder<Resource>();

        try {
            do {
                clangService.resourceGetById(token, resourceId.value, code, resource);
                Thread.sleep(MILLIS);
            } while (resource.value.getStatus().equals(PROCESSING));

            if (resource.value.getStatus().equals(READY)) {
                int offset = 0;
                int batch = BATCHSIZE;

                Holder<ArrayOfDelivery> deliveries = new Holder<ArrayOfDelivery>();
                while (offset < resource.value.getSize()) {
                    clangService.deliverySetGetDeliveries(token, resourceId.value, offset, batch, code, deliveries);
                    List<Delivery> deliverys = deliveries.value.getDelivery();
                    result.addAll(deliverys);
                    offset += batch;
                }
                clangService.resourceFree(token, resourceId.value, code, new Holder<Boolean>());
            }
        } catch (SOAPFaultException e) {
            throw new ClangException(e.getFault().getFaultCode(), e);
        }

        return result;
    }

    public List<Click> getClicks(MailingLink mailingLink) throws InterruptedException, ClangException {
        List<Click> result = new ArrayList<Click>();

        Holder<BigInteger> code = new Holder<BigInteger>();
        Holder<Long> resourceId = new Holder<Long>();

        clangService.mailingLinkGetClickSet(token, mailingLink, code, resourceId);

        Holder<Resource> resource = new Holder<Resource>();

        try {
            do {
                clangService.resourceGetById(token, resourceId.value, code, resource);
                Thread.sleep(MILLIS);
            } while (resource.value.getStatus().equals(PROCESSING));

            if (resource.value.getStatus().equals(READY)) {
                int offset = 0;
                int batch = BATCHSIZE;

                Holder<ArrayOfClick> clicks = new Holder<ArrayOfClick>();
                while (offset < resource.value.getSize()) {
                    clangService.clickSetGetClicks(token, resourceId.value, offset, batch, code, clicks);
                    List<Click> click = clicks.value.getClick();
                    result.addAll(click);
                    offset += batch;
                }
                clangService.resourceFree(token, resourceId.value, code, new Holder<Boolean>());
            }
        } catch (SOAPFaultException e) {
            throw new ClangException(e.getFault().getFaultCode(), e);
        }

        return result;
    }


    public Boolean sendEmailToCustomer(Customer customer, Email email){
        Holder<BigInteger> code = new Holder<BigInteger>();
        Holder<Boolean> result = new Holder<Boolean>();

        clangService.emailSendToCustomer(token, email.getId(), customer.getId(), null, null, code, result);
        return result.value;
    }

    public Boolean addToCampaign(long campaignId, long customerId){
        Holder<BigInteger> code = new Holder<BigInteger>();
        Holder<Boolean> result = new Holder<Boolean>();

        clangService.campaignAddMember(token, campaignId, customerId, code, result);
        return result.value;
    }

    public Boolean executeCampaign(long campaignId, ArrayOfOption options){
        Holder<BigInteger> code = new Holder<BigInteger>();
        Holder<Boolean> result = new Holder<Boolean>();
        
        clangService.campaignExecute(token, campaignId, options, code, result);
        return result.value;
    }

    public Boolean hasMember(long campaignId, long customerId){
        Holder<BigInteger> code = new Holder<BigInteger>();
        Holder<Boolean> result = new Holder<Boolean>();
        
        clangService.campaignHasMember(token, campaignId, customerId, code, result);
        return result.value;
    }

    public Boolean removeFromCampaigne(long campaignId, long customerId){
        Holder<BigInteger> code = new Holder<BigInteger>();
        Holder<Boolean> result = new Holder<Boolean>();

        clangService.campaignRemoveMember(token, campaignId, customerId, code, result);
        return result.value;
    }

    public List<Customer> getMembers(long campaignId) throws InterruptedException, ClangException {
        Holder<BigInteger> code = new Holder<BigInteger>();
        Holder<Long> resourceId = new Holder<Long>();

        clangService.campaignGetMembers(token, campaignId, code, resourceId);

        return getCustomerSet(resourceId);
    }

    public Email getEmail(Long emailId){
        Holder<BigInteger> code = new Holder<BigInteger>();
        Holder<Email> result = new Holder<Email>();

        clangService.emailGetById(token, emailId, code, result);
        return result.value;
    }

    public Email updateEmail(Email email){
        Holder<BigInteger> code = new Holder<BigInteger>();
        Holder<Email> result = new Holder<Email>();

        clangService.emailUpdate(token, email, code, result);
        return result.value;
    }
}
