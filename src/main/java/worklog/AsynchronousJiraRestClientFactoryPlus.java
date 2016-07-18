package worklog;

import com.atlassian.httpclient.api.HttpClient;
import com.atlassian.jira.rest.client.api.AuthenticationHandler;
import com.atlassian.jira.rest.client.auth.BasicHttpAuthenticationHandler;
import com.atlassian.jira.rest.client.internal.async.AsynchronousHttpClientFactory;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.jira.rest.client.internal.async.DisposableHttpClient;

import java.net.URI;

/**
 * @author ymolodkov on 18.07.16.
 */
public class AsynchronousJiraRestClientFactoryPlus extends AsynchronousJiraRestClientFactory {
    public JiraRestClientPlus create(URI serverUri, AuthenticationHandler authenticationHandler) {
        DisposableHttpClient httpClient = new AsynchronousHttpClientFactory().createClient(serverUri, authenticationHandler);
        return new AsynchronousJiraRestClientPlus(serverUri, httpClient);
    }

    public JiraRestClientPlus createWithBasicHttpAuthentication(URI serverUri, String username, String password) {
        return create(serverUri, new BasicHttpAuthenticationHandler(username, password));
    }

    public JiraRestClientPlus create(URI serverUri, HttpClient httpClient) {
        DisposableHttpClient disposableHttpClient = new AsynchronousHttpClientFactory().createClient(httpClient);
        return new AsynchronousJiraRestClientPlus(serverUri, disposableHttpClient);
    }
}
