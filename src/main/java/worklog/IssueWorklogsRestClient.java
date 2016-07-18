package worklog;

import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.atlassian.jira.rest.client.api.domain.Worklog;
import com.atlassian.util.concurrent.Promise;

import java.util.List;

/**
 * @author ymolodkov on 18.07.16.
 */
public interface IssueWorklogsRestClient {
    Promise<List<Worklog>> getIssueWorklogs(BasicIssue issue);
}