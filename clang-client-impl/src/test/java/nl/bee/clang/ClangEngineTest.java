/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.bee.clang;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.bee.clang.ws.ArrayOfClick;
import nl.bee.clang.ws.ArrayOfOption;
import nl.bee.clang.ws.Click;
import nl.bee.clang.ws.Customer;
import nl.bee.clang.ws.Delivery;
import nl.bee.clang.ws.Email;
import nl.bee.clang.ws.EmailBlock;
import nl.bee.clang.ws.EmailBlockContainer;
import nl.bee.clang.ws.Group;
import nl.bee.clang.ws.Mailing;
import nl.bee.clang.ws.MailingLink;
import nl.bee.clang.ws.Option;
import nl.bee.clang.ws.Tag;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author root
 */
@Ignore
public class ClangEngineTest {

    static ClangEngine clangEngine;

    @BeforeClass
    public static void setUpClass() throws Exception {
        clangEngine = new ClangEngine("1aecf568-84f4-4ffb-950c-b2a9e3173f22"); //Bee
//        clangEngine = new ClangEngine("db0653ed-c637-4385-b345-7a39217247cd", "http://localhost:8181/?WSDL"); //Domark
//        clangEngine = new ClangEngine("db0653ed-c637-4385-b345-7a39217247cd"); //Domark
//        clangEngine = new ClangEngine("77b3be54-a353-4675-9389-e6c4b6bb5c5a"); //Ricoh
//        clangEngine = new ClangEngine("6d704f3d-0ab4-4944-93d4-c49d107f908d"); //ECR

    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws ClangException {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of createCustomer method, of class ClangEngine.
     */
    @Test
    @Ignore
    public void createCustomer() throws Exception {
        System.out.println("createCustomer");

        Customer result = clangEngine.createCustomer("David", "Villa", "villa@spain.es");
        assertNull(result.getId());
        assertEquals("Villa", result.getLastname());
    }

    /**
     * Test of insertCustomer method, of class ClangEngine.
     */
    @Test
    @Ignore
    public void insertCustomer() {
        System.out.println("insertCustomer");
        Customer customer = new Customer();
        customer.setLastname("Winkel");
        customer.setEmailAddress("matthijs@thebeesite.nl");

        Customer insertCustomer = clangEngine.insertCustomer(customer);
        assertNotNull(insertCustomer.getId());
    }

    @Test
    @Ignore
    public void updateCustomer(){
        System.out.println("update Customer");
        Customer customer = clangEngine.getCustomerById(35L);

        customer.setStatus("PROSPECT");

        Customer insertCustomer = clangEngine.updateCustomer(customer);
        assertNotNull(insertCustomer.getId());
    }

    @Test
    @Ignore
    public void customerById() {
        System.out.println("getCustomer");

        Customer getCustomer = null;
        try {
            getCustomer = clangEngine.getCustomer(35L);
        } catch (ClangException ex) {
            Logger.getLogger(ClangEngineTest.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        assertNotNull(getCustomer);
    }

    @Test
    @Ignore
    public void getCustomerByWrongId() {
        System.out.println("getCustomer with non-existing id");

        Customer getCustomer;
        try {
            getCustomer = clangEngine.getCustomer(21L);
            assertNotNull(getCustomer.getLastname());
        } catch (Exception ex) {
            Logger.getLogger(ClangEngineTest.class.getName()).log(Level.INFO, ex.getMessage(), ex);
        }
    }

    @Test
    @Ignore
    public void getCustomerByWrongExternalId() {
        System.out.println("getCustomer with non-existing external id");

        try {
            List<Customer> customersByExternalId = clangEngine.getCustomerByExternalId("21");
            assertTrue(customersByExternalId.isEmpty());
        } catch (Exception ex) {
            Logger.getLogger(ClangEngineTest.class.getName()).log(Level.INFO, ex.getMessage(), ex);
        }
    }

    @Test
    @Ignore
    public void getCustomerByEmailAddress() {
        System.out.println("getCustomers with emailaddress");

        List<Customer> customers = clangEngine.getCustomer("suzanne@thebeesite.nl");
        assertTrue(customers.size() > 0);
        for (Customer customer : customers) {
            System.out.println(customer.getFirstname());
        }
    }

    @Test
    @Ignore
    public void getCustomerByExternalId() {
        System.out.println("getCustomers with external id");

        List<Customer> customers = clangEngine.getCustomerByExternalId("1234");
        assertTrue(customers.size() > 0);
        for (Customer customer : customers) {
            System.out.println(customer.getFirstname());
        }
    }

    @Test
    @Ignore
    public void getGroups(){
        System.out.println("get All Groups");
        List<Group> groups = clangEngine.getGroups();
        assertFalse(groups.isEmpty());
        for (Group group : groups) {
            System.out.println(String.format("%s [%s]", group.getName(), group.getId()));
        }
    }

    @Test
    @Ignore
    public void getGroupMembers(){
        System.out.println("get Group Members");
        Long groupId = 11L;
        Boolean includeRecursive = true;

        try {
            List<Customer> groupMembers = clangEngine.getGroupMembers(groupId, includeRecursive);
            assertFalse(groupMembers.isEmpty());

            for (Customer customer : groupMembers) {
                System.out.println(customer.getFirstname());
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(ClangEngineTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClangException ex) {
            Logger.getLogger(ClangEngineTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    @Ignore
    public void addToGroup(){
        System.out.println("add Customer to Group");
        try {
            assertTrue(clangEngine.addToGroup(clangEngine.getGroup(1L), clangEngine.getCustomer(2L)));
        } catch (Exception ex) {
            Logger.getLogger(ClangEngineTest.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    @Test
    @Ignore
    public void getGroupsByCustomer(){
        System.out.println("groups by customer");
        List<Group> groupByCustomer = clangEngine.getGroupsByCustomer(2L);
        assertFalse(groupByCustomer.isEmpty());
    }

    @Test
    @Ignore
    public void getGroupsByWrongCustomer(){
        System.out.println("groups by wrong customer");
        List<Group> groupByCustomer = clangEngine.getGroupsByCustomer(9L);
        assertTrue(groupByCustomer.isEmpty());
    }

    @Test
    @Ignore
    public void getCustomers(){
        System.out.println("get Customers");
        try {
            assertEquals(2, clangEngine.getCustomers().size());
        } catch (Exception ex) {
            Logger.getLogger(ClangEngineTest.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    @Test
    @Ignore
    public void getMailing(){
        Mailing mailing = clangEngine.getMailing(69L);
        for (MailingLink link : mailing.getLinks().getMailingLink()){
            System.out.println("link id: "+link.getId());
            System.out.println("link desc: "+link.getDescription());
            System.out.println("link url: "+link.getUrl());
            System.out.println("link track: "+link.isTrack());
            System.out.println("link source: "+link.isSource());
        }
        assertNotNull(mailing);
    }

    @Test
    @Ignore
    public void getMailings(){
        System.out.println("get Mailings");
        try {
            List<Mailing> mailings = clangEngine.getMailings();
            for (Mailing mailing : mailings) {
                System.out.println("id:"+mailing.getId());
                System.out.println("name:"+mailing.getCampaignName());
            }
            assertEquals(60, mailings.size());
        } catch (Exception ex) {
            Logger.getLogger(ClangEngineTest.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    @Test
    @Ignore
    public void getQuickMailings(){
        System.out.println("get QuickMailings");
        try {
            List<Mailing> mailings = clangEngine.getQuickMailings();
            for (Mailing mailing : mailings) {
                System.out.println("id:"+mailing.getId());
                System.out.println("name:"+mailing.getCampaignName());
            }
            assertEquals(14, mailings.size());
        } catch (Exception ex) {
            Logger.getLogger(ClangEngineTest.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    //FIXME Exception handling
    @Test
    @Ignore
    public void getMailLinkClicks() throws ClangException{
        System.out.println("get MailingLinkClicks");
        Mailing mailing = clangEngine.getMailing(69L);

        MailingLink link = mailing.getLinks().getMailingLink().get(1);
        System.out.println("link id:"+link.getId());
        try {
            List<Click> clicks = clangEngine.getClicks(link);
            for (Click click : clicks) {
                System.out.println("click id:"+click.getId());
                System.out.println("customer:"+click.getCustomerId());
                System.out.println("clickedAt:"+click.getClickedAt());
                System.out.println("browser:"+click.getBrowserInformation().getName());
            }
            assertEquals(1, clicks.size());
        } catch (InterruptedException ex) {
            Logger.getLogger(ClangEngineTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //FIXME exception handling
    @Test
    @Ignore
    public void getDeliveriesByMailing() throws ClangException{
        System.out.println("get Deliveries by Mailing");
        Mailing mailing = clangEngine.getMailing(69L);
        try {
            List<Delivery> deliveriesByMailing = clangEngine.getDeliveriesByMailing(mailing);
            for (Delivery delivery : deliveriesByMailing) {
                System.out.println("delivery id: "+delivery.getId());
                System.out.println("status: "+delivery.getStatus());
                System.out.println("customer: "+delivery.getCustomer().getLastname());
                ArrayOfClick clicks = delivery.getClicks();
                if (clicks != null){
                    for (Click click : clicks.getClick()) {
                        System.out.println("click id: "+click.getId());
                        System.out.println("click mailing id: "+click.getMailingId());
                        System.out.println("click date: "+click.getClickedAt());
                        System.out.println("Link id: "+click.getLink().getId());
                        System.out.println("link url: "+click.getLink().getUrl());
                        for (Tag tag : click.getLink().getTags().getTag()){
                            System.out.println("tag id: "+tag.getId());
                            System.out.println("tag name: "+tag.getName());
                        }
                    }
                }
            }
            assertEquals(26, deliveriesByMailing.size());
        } catch (InterruptedException ex) {
            Logger.getLogger(ClangEngineTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    @Ignore
    public void sendEmailToCustomer(){
        System.out.println("send Email to Customer");
        Customer customer;
        try {
            customer = clangEngine.getCustomer(4L);
            Email email = new Email();
            email.setId(62L);
            assertTrue(clangEngine.sendEmailToCustomer(customer, email));
        } catch (Exception ex) {
            Logger.getLogger(ClangEngineTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    @Ignore
    public void addCustomerToCampaign(){
        System.out.println("add Customer to Campaign");
        Long customerId = 8L;
        Long campaignId = 7L;
        try {
            assertTrue(clangEngine.addToCampaign(campaignId, customerId));
        } catch (Exception ex) {
            Logger.getLogger(ClangEngineTest.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Test
    @Ignore
    public void removeCustomerFromCampaign(){
        System.out.println("add Customer to Campaign");
        Long customerId = 6L;
        Long campaignId = 7L;
        try {
            assertTrue(clangEngine.removeFromCampaigne(campaignId, customerId));
        } catch (Exception ex) {
            Logger.getLogger(ClangEngineTest.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Test
    @Ignore
    public void executeCampaign(){
        System.out.println("execute Campaign with Customer");
        long campaignId = 5; //FT Registration

        ArrayOfOption options = new ArrayOfOption();
        Option option = new Option();
        option.setName("ID");
        option.setValue("52");
        options.getOption().add(option);

        try{
            assertTrue(clangEngine.executeCampaign(campaignId, options));
        } catch (Exception ex){
            Logger.getLogger(ClangEngineTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    @Ignore
    public void getMembers(){
        System.out.println("get Campaign members");
        long campaignId = 7;
        try {
            List<Customer> members = clangEngine.getMembers(campaignId);
            for (Customer customer : members) {
                System.out.println(customer.getId());
            }

        } catch (InterruptedException ex) {
            Logger.getLogger(ClangEngineTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClangException ex) {
            Logger.getLogger(ClangEngineTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    @Ignore
    public void hasMembers(){
        System.out.println("is member in Campaign");
        long campaignId = 7;
        long customerId = 6;

        assertTrue(clangEngine.hasMember(campaignId, customerId));
    }

    @Test
    @Ignore
    public void getEmail(){
        Long emailId = new Long("601");

        Email email = clangEngine.getEmail(emailId);

        List<EmailBlockContainer> emailBlockContainers = email.getHtmlBlocks().getEmailBlockContainer();
        for (EmailBlockContainer emailBlockContainer : emailBlockContainers) {
            if (emailBlockContainer.getName().equals("Users")){
                EmailBlock emailBlock = emailBlockContainer.getBlocks().getEmailBlock().get(0);
                emailBlock.setContent("Matthijs te Winkel, Duncan Bloem");
            }
            if (emailBlockContainer.getName().equals("Duration")){
                EmailBlock emailBlock = emailBlockContainer.getBlocks().getEmailBlock().get(0);
                emailBlock.setContent("12 months");
            }
            if (emailBlockContainer.getName().equals("EndDate")){
                EmailBlock emailBlock = emailBlockContainer.getBlocks().getEmailBlock().get(0);
                emailBlock.setContent("1 Dec 2011");
            }
            if (emailBlockContainer.getName().equals("RAS")){
                EmailBlock emailBlock = emailBlockContainer.getBlocks().getEmailBlock().get(0);
                emailBlock.setContent("GFM, USD");
            }
            if (emailBlockContainer.getName().equals("Amount")){
                EmailBlock emailBlock = emailBlockContainer.getBlocks().getEmailBlock().get(0);
                emailBlock.setContent("2500");
            }
            if (emailBlockContainer.getName().equals("OrderNumber")){
                EmailBlock emailBlock = emailBlockContainer.getBlocks().getEmailBlock().get(0);
                emailBlock.setContent("12345");
            }
        }

        Email updateEmail = clangEngine.updateEmail(email);

        Customer customer = clangEngine.getCustomerById(35L);
        clangEngine.sendEmailToCustomer(customer , updateEmail);
    }
}
