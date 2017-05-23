Citrus demo voting application ![Logo][1]
==============

This demo application demonstrates the combination of Citrus and Cucumber for behavior driven development (BDD). 
The tests combine BDD feature stories with the famous Gherkin syntax and Citrus integration test capabilities. 
Read about this feature in [reference guide][4].
 
Objectives
---------

The voting demo application is a simple Spring boot web application. The app provides a Http REST interface for clients and browsers. 
The automated testing shows the usage of both Cucumber and Citrus in combination. Step definitions are able to use *CitrusResource*
annotations for injecting a TestDesigner instance. The test designer is then used in steps to build a Citrus integration test.

At the end the Citrus test is automatically executed. We can use normal step definition classes that use Gherkin annotations
(@Given, @When, @Then) provided by Cucumber.

Get started
---------

We start with setting a special object factory in cucumber.properties

    cucumber.api.java.ObjectFactory=cucumber.runtime.java.CitrusObjectFactory
    
This object factory enables dependency injection for Citrus related resources as well as special test preparations. 
No we can use a normal feature test using JUnit and Cucumber runner.

    @RunWith(Cucumber.class)
    @CucumberOptions(
            plugin = { "com.consol.citrus.cucumber.CitrusReporter" } )
    public class VotingFeatureIT {
    }

The test feature is described in a story using Gherkin syntax.

    Feature: Voting Http REST API
    
      Background:
        Given New voting "Do you like cucumbers?"
        And voting options are "yes:no"
    
      Scenario: Create voting
        When client creates the voting
        Then client should be able to get the voting
        And the list of votings should contain "Do you like cucumbers?"
    
      Scenario: Add votes
        When client creates the voting
        And client votes for "yes"
        Then votes should be
          | yes | 1 |
          | no  | 0 |
    
      Scenario: Top vote
        When client creates the voting
        And client votes for "no"
        Then votes should be
          | yes | 0 |
          | no  | 1 |
        And top vote should be "no"
        
The steps executed are defined in a separate class where a Citrus test designer is used to build integration test logic.
The test steps call REST API operations as client and verify the response messages from the server. In addition to that Citrus
provides backend service simulation for JMS and Mail SMTP.

    public class VotingIntegrationSteps {
    
        private final String votingClient = "votingClient";
        private final String mailServer = "mailServer";
        private final String reportingEndpoint = "reportingEndpoint";
    
        @CitrusResource
        private TestDesigner designer;
    
        @Given("^New voting \"([^\"]*)\"$")
        public void newVoting(String title) {
            designer.variable("id", "citrus:randomUUID()");
            designer.variable("title", title);
            designer.variable("options", buildOptionsAsJsonArray("yes:no"));
            designer.variable("closed", false);
            designer.variable("report", false);
        }
    
        @Given("^voting options are \"([^\"]*)\"$")
        public void votingOptions(String options) {
            designer.variable("options", buildOptionsAsJsonArray(options));
        }
    
        @Given("^reporting is enabled$")
        public void reportingIsEnabled() {
            designer.variable("report", true);
        }
    
        @When("^(?:I|client) creates? the voting$")
        public void createVoting() {
            designer.http()
                .client(votingClient)
                .post("/voting")
                .contentType("application/json")
                .payload("{ \"id\": \"${id}\", \"title\": \"${title}\", \"options\": ${options}, \"report\": ${report} }");
    
            designer.http().client(votingClient)
                .response(HttpStatus.OK)
                .messageType(MessageType.JSON);
        }
    
        @When("^(?:I|client) votes? for \"([^\"]*)\"$")
        public void voteFor(String option) {
            designer.http().client(votingClient)
                    .send()
                    .put("voting/${id}/" + option);
    
            designer.http().client(votingClient)
                    .receive()
                    .response(HttpStatus.OK);
        }
        
        [...]
    }    

Configuration
---------

In order to enable Citrus Cucumber support we need to specify a special object factory in *cucumber.properties*.
    
    cucumber.api.java.ObjectFactory=cucumber.runtime.java.CitrusObjectFactory
    
The object factory takes care on creating all step definition instances. The object factory is able to inject *@CitrusResource*
annotated fields in step classes.
    
The usage of this special object factory is mandatory in order to combine Citrus and Cucumber capabilities. 
   
We also have the usual *citrus-context.xml* Citrus Spring configuration that is automatically loaded within the object factory.
So you can define and use Citrus components as usual within your test. In this sample we use a Http client component to call some
REST API on the voting application.

Run
---------

The sample application uses Maven as build tool. So you can compile, package and test the
sample with Maven.
 
     mvn clean install -Dembedded=true
    
This executes the complete Maven build lifecycle. The embedded option automatically starts a Jetty web
container before the integration test phase. The voting Spring Boot system under test is automatically deployed and started in this phase.
After that the Citrus test cases are able to interact with the voting application in the integration test phase.

During the build you will see Citrus performing some integration tests.
After the tests are finished the embedded Spring Boot infrastructure and the voting application are automatically stopped.

System under test
---------

The sample uses a small voting application as system under test. The application is a Spring Boot web application
that you can deploy on any web container. As we have already seen earlier in this README the Spring Boot application is automatically
started during the Maven build lifecycle when using the **embedded=true** option. This approach is fantastic 
when running automated tests in a continuous build.
  
Besides that you can start the voting application manually in order to access the web front end with a browser.  

You can start the sample voting application with this command.

     mvn spring-boot:run

Point your browser to
 
    http://localhost:8080/

You will see the web UI of the voting application. Now you can play around with the web frontend and create some new votes.

The application uses some JMS interface for sending reports to a simulated backend. In case the reporting is enabled you need
to also start the ActiveMQ message broker. You can do this in a separate command line terminal.

    mvn activemq:run

Now we are ready to execute some Citrus tests in a separate JVM.

Citrus test
---------

Once the sample application is deployed and running you can execute the Citrus test cases.
Open a separate command line terminal and navigate to the sample folder.

Execute all Citrus tests by calling

     mvn integration-test

You can also pick a single test by calling

     mvn integration-test -Ptest=VotingFeatureIT

You should see Citrus performing several tests with lots of debugging output in both terminals (sample application server
and Citrus test client). And of course green tests at the very end of the build.

Of course you can also start the Citrus tests from your favorite IDE.
Just start the Citrus test using the JUnit IDE integration in IntelliJ, Eclipse or Netbeans.

Further information
---------

For more information on Citrus see [www.citrusframework.org][2], including
a complete [reference manual][3].

 [1]: http://www.citrusframework.org/img/brand-logo.png "Citrus"
 [2]: http://www.citrusframework.org
 [3]: http://www.citrusframework.org/reference/html/
 [4]: http://www.citrusframework.org/reference/html/cucumber.html