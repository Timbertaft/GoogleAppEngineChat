package edu.gvsu.restapi;

import java.util.List;

import com.googlecode.objectify.Key;
import org.json.JSONArray;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.restlet.resource.Post;
import org.restlet.resource.Get;
import com.googlecode.objectify.ObjectifyService;

/**
 * Represents a collection of widgets.  This resource processes HTTP requests that come in on the URIs
 * in the form of:
 *
 * http://host:port/widgets
 *
 * This resource supports both HTML and JSON representations.
 *
 * @author Jonathan Engelsma (http://themobilemontage.com)
 *
 */
public class MultiResource extends ServerResource {

	private List<RegistrationInfo> multireg = null;

	@Override
	protected void doInit() {

    this.multireg = ObjectifyService.ofy()
        .load()
        .type(RegistrationInfo.class) // We want only Widgets
        .list();

		// these are the representation types this resource can use to describe the
		// set of widgets with.
		getVariants().add(new Variant(MediaType.APPLICATION_JSON));
	}

	/**
	 * Handle an HTTP GET. Represent the widget object in the requested format.
	 *
	 * @param variant
	 * @return
	 * @throws ResourceException
	 */
	@Get
	public Representation get(Variant variant) throws ResourceException {
		Representation result = null;
		if (null == this.multireg) {
			ErrorMessage em = new ErrorMessage();
			return representError(variant, em);
		} else {
			JSONArray userArray = new JSONArray();
			for(RegistrationInfo reginf : this.multireg) {
				userArray.put(reginf.toJSON());
			}

			result = new JsonRepresentation(userArray);
		}
		return result;
	}

	/**
	 * Handle a POST Http request. Create a new widget
	 *
	 * @param entity
	 * @throws ResourceException
	 */
	@Post
	public Representation post(Representation entity, Variant variant) throws ResourceException	{
		Representation rep = null;

		// We handle only a form request in this example. Other types could be
		// JSON or XML.
		try {
			if (entity.getMediaType().equals(MediaType.APPLICATION_WWW_FORM, true)) {
				// Use the incoming data in the POST request to create/store a new widget resource.
				Form form = new Form(entity);
				RegistrationInfo registrationInfo = new RegistrationInfo();

				registrationInfo.setUserName(form.getFirstValue("userName"));
				registrationInfo.setPort(Integer.parseInt(form.getFirstValue("port")));
				registrationInfo.setHost(form.getFirstValue("host"));
				registrationInfo.setStatus(Boolean.parseBoolean(form.getFirstValue("status")));

                // find and create object by key.
                Key<RegistrationInfo> refkey = Key.create(RegistrationInfo.class, registrationInfo.getUserName());
                RegistrationInfo resp = ObjectifyService.ofy()
                        .load()
                        .key(refkey)
                        .now();

                if(resp != null) {
                    getResponse().setStatus(Status.CLIENT_ERROR_CONFLICT);
                } else {
                        // persist updated object
                    ObjectifyService.ofy().save().entity(registrationInfo).now();

                    getResponse().setStatus(Status.SUCCESS_OK);
                    rep = new StringRepresentation(registrationInfo.toJSON().toString());
                    rep.setMediaType(MediaType.APPLICATION_JSON);
                    getResponse().setEntity(rep);
                }

			} else {
				getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			}
		} catch (Exception e) {
			getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
		}
		return rep;
	}

	/**
	 * Represent an error message in the requested format.
	 *
	 * @param variant
	 * @param em
	 * @return
	 * @throws ResourceException
	 */
	private Representation representError(Variant variant, ErrorMessage em)
	throws ResourceException {
		Representation result = null;
		if (variant.getMediaType().equals(MediaType.APPLICATION_JSON)) {
			result = new JsonRepresentation(em.toJSON());
		} else {
			result = new StringRepresentation(em.toString());
		}
		return result;
	}

	protected Representation representError(MediaType type, ErrorMessage em)
	throws ResourceException {
		Representation result = null;
		if (type.equals(MediaType.APPLICATION_JSON)) {
			result = new JsonRepresentation(em.toJSON());
		} else {
			result = new StringRepresentation(em.toString());
		}
		return result;
	}
}
