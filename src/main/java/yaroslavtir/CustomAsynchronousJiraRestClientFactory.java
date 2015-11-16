package yaroslavtir;

import com.atlassian.httpclient.api.HttpClient;
import com.atlassian.jira.rest.client.api.AuthenticationHandler;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClientFactory;
import com.atlassian.jira.rest.client.auth.BasicHttpAuthenticationHandler;
import com.atlassian.jira.rest.client.internal.async.AsynchronousHttpClientFactory;
import com.atlassian.jira.rest.client.internal.async.DisposableHttpClient;

import java.net.URI;

public class CustomAsynchronousJiraRestClientFactory implements JiraRestClientFactory {

    public JiraRestClient create(final URI serverUri, final AuthenticationHandler authenticationHandler) {
        final DisposableHttpClient httpClient = new AsynchronousHttpClientFactory()
                .createClient(serverUri, authenticationHandler);
        return new CustomCustomAsynchronousJiraRestClient(serverUri, httpClient);
    }

    public JiraRestClient createWithBasicHttpAuthentication(final URI serverUri, final String username, final String password) {
        return create(serverUri, new BasicHttpAuthenticationHandler(username, password));
    }

    public JiraRestClient create(final URI serverUri, final HttpClient httpClient) {
        final DisposableHttpClient disposableHttpClient = new AsynchronousHttpClientFactory().createClient(httpClient);
        return new CustomCustomAsynchronousJiraRestClient(serverUri, disposableHttpClient);
    }
}
