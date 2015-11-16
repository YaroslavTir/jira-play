import com.atlassian.jira.rest.client.api.*;
import com.atlassian.jira.rest.client.api.domain.*;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import com.atlassian.util.concurrent.Promise;
import yaroslavtir.CustomAsynchronousJiraRestClientFactory;
import yaroslavtir.CustomCustomAsynchronousJiraRestClient;
import yaroslavtir.RemoteLink;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

/**
 * @author yaroslavTir.
 */
public class Main {

    private static final String URL = "http://localhost:8080";
    private static final String ADMIN_USERNAME = "yaroslavTir@gmail.com";
    private static final String ADMIN_PASSWORD = "jira";

    public static void main(String[] args) throws Exception {
        JiraRestClient client = getJiraRestClient();
        remoteLinkTest(client);
//        String key = createIssue(client);
//        getIssue(client, key);
//        updateIssue(client, key);
//        deleteIssue(client, key);
        client.close();
    }

    private static JiraRestClient getJiraRestClient() throws URISyntaxException {
        JiraRestClientFactory factory = new CustomAsynchronousJiraRestClientFactory();
        JiraRestClient client = factory.createWithBasicHttpAuthentication(new URI(URL), ADMIN_USERNAME, ADMIN_PASSWORD);
        SearchRestClient searchClient = client.getSearchClient();
        SearchResult result = searchClient.searchJql("project = 'sync project' and status = 'open'").claim();
        System.out.println(result);
        return client;
    }

    private static void getIssue(JiraRestClient client, String key) throws ExecutionException, InterruptedException {
        IssueRestClient issueClient = client.getIssueClient();
        Promise<Issue> issuePromise = issueClient.getIssue(key);
        Issue issue = issuePromise.claim();
        issue.getUpdateDate();
        System.out.println(issue.getId());
    }

    private static void remoteLinkTest(JiraRestClient client){
        CustomCustomAsynchronousJiraRestClient customClient = (CustomCustomAsynchronousJiraRestClient) client;
        String issueKey = "SP-7605";
        Iterable<RemoteLink> remoteLinks = customClient.getRemoteLinkRestClient().getRemoteLink(issueKey).claim();
        RemoteLink remoteLink = remoteLinks.iterator().next();
        customClient.getRemoteLinkRestClient().createRemoteLink(issueKey, remoteLink).claim();
    }

    private static String createIssue(JiraRestClient client) throws ExecutionException, InterruptedException {
        IssueInput issueInput = getIssueForInsert(client);
        IssueRestClient issueClient = client.getIssueClient();
        BasicIssue basicIssue = issueClient.createIssue(issueInput).claim();
        String key = basicIssue.getKey();
        System.out.println(key);
        return key;
    }


    private static void updateIssue(JiraRestClient client, String key) throws ExecutionException, InterruptedException {
        IssueInput issueInput = getIssueForUpdate(client);
        IssueRestClient issueClient = client.getIssueClient();
        issueClient.updateIssue(key, issueInput).claim();
        System.out.println("updated");
    }

    private static void deleteIssue(JiraRestClient client, String key) throws ExecutionException, InterruptedException {
        IssueRestClient issueClient = client.getIssueClient();
        issueClient.deleteIssue(key, false).claim();
        System.out.println("deleted");
    }


    private static IssueInput getIssueForInsert(JiraRestClient client) throws ExecutionException, InterruptedException {
        IssueInputBuilder issueBuilder = new IssueInputBuilder("10000", 3L);

        issueBuilder.setProject(retrieveProject(client));
        issueBuilder.setDescription("issue description");
        issueBuilder.setSummary("issue summary");

        return issueBuilder.build();
    }

    private static IssueInput getIssueForUpdate(JiraRestClient client) throws ExecutionException, InterruptedException {
        IssueInputBuilder issueBuilder = new IssueInputBuilder("10000", 3L);

        issueBuilder.setProject(retrieveProject(client));
        issueBuilder.setAssignee(retrieveUser(client));
        issueBuilder.setDescription("updated description");
        issueBuilder.setSummary("updated summary");

        return issueBuilder.build();
    }

    private static User retrieveUser(JiraRestClient client) throws InterruptedException, ExecutionException {
        UserRestClient userClient = client.getUserClient();
        return userClient.getUser(ADMIN_USERNAME).claim();
    }

    private static Project retrieveProject(JiraRestClient client) throws ExecutionException, InterruptedException {
        ProjectRestClient projectClient = client.getProjectClient();
        Promise<Project> project = projectClient.getProject("10000");
        Project projectObj = project.claim();
        return projectObj;
    }


}
