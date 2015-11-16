package yaroslavtir;

import com.atlassian.jira.rest.client.internal.json.*;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.net.URI;

/**
 * Created by YMolodkov on 08.10.2015.
 */
public class RemoteLinkJsonParser implements JsonObjectParser<RemoteLink> {

    private final FieldSchemaJsonParser schemaJsonParser = new FieldSchemaJsonParser();

    public RemoteLink parse(final JSONObject jsonObject) throws JSONException {
        final JSONObject object = jsonObject.getJSONObject("object");
        final String id = jsonObject.getString("id");
        final String title = object.getString("title");
        final URI uri = JsonParseUtil.parseURI(object.getString("url"));
        return new RemoteLink(id, title, uri);
    }

    public static JsonArrayParser<Iterable<RemoteLink>> createRemoteLinkArrayParser() {
        return GenericJsonArrayParser.create(new RemoteLinkJsonParser());
    }
}
