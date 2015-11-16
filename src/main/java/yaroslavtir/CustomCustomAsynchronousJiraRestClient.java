package yaroslavtir;

import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClient;
import com.atlassian.jira.rest.client.internal.async.DisposableHttpClient;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

public class CustomCustomAsynchronousJiraRestClient extends AsynchronousJiraRestClient {

    private AsynchronousRemoteLinkRestClient remoteLinkRestClient;

    public CustomCustomAsynchronousJiraRestClient(URI serverUri, DisposableHttpClient httpClient) {
        super(serverUri, httpClient);
        this.remoteLinkRestClient = new AsynchronousRemoteLinkRestClient(UriBuilder.fromUri(serverUri).path("/rest/api/latest").build(), httpClient);
    }

    public AsynchronousRemoteLinkRestClient getRemoteLinkRestClient() {
        return remoteLinkRestClient;
    }
}
