package worklog;

import com.atlassian.jira.rest.client.api.JiraRestClient;

/**
 * @author ymolodkov on 18.07.16.
 */
public interface JiraRestClientPlus extends JiraRestClient {
    IssueWorklogsRestClient getIssueWorklogRestClient();
}