package worklog;

import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClient;
import com.atlassian.jira.rest.client.internal.async.DisposableHttpClient;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

/**
 * @author ymolodkov on 18.07.16.
 */
public class AsynchronousJiraRestClientPlus extends AsynchronousJiraRestClient implements JiraRestClientPlus {
    private final IssueWorklogsRestClient issueWorklogsRestClient;

    public AsynchronousJiraRestClientPlus(final URI serverUri, final DisposableHttpClient httpClient) {
        super(serverUri, httpClient);
        final URI baseUri = UriBuilder.fromUri(serverUri).path("/rest/api/2").build(); // '/rest/api/latest' or '/rest/api/2'
        issueWorklogsRestClient = new AsynchronousIssueWorklogsRestClient(baseUri, httpClient);
    }

    public IssueWorklogsRestClient getIssueWorklogRestClient() {
        return issueWorklogsRestClient;
    }
}