package edu.gvsu.restapi;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.Delete;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Key;


/**
 * Represents a collection of widgets.  This resource processes HTTP requests that come in on the URIs
 * in the form of:
 *
 * http://host:port/widgets/{id}
 *
 * This resource supports both HTML and JSON representations.
 *
 * @author Jonathan Engelsma (http://themobilemontage.com)
 *
 */
public class SingleResource extends ServerResource {

	private RegistrationInfo singlereg = null;

	@Override
	public void doInit() {

		// URL requests routed to this resource have the widget id on them.
		String id = (String) getRequest().getAttributes().get("id");

		// lookup the widget in google's persistance layer.
    	Key<RegistrationInfo> refkey = Key.create(RegistrationInfo.class, id);
   		this.singlereg = ObjectifyService.ofy()
				.load()
				.key(refkey)
				.now();

		// these are the representation types this resource supports.
		getVariants().add(new Variant(MediaType.APPLICATION_JSON));
	}

	/**
	 * Represent the widget object in the requested format.
	 *
	 * @param variant
	 * @return
	 * @throws ResourceException
	 */
	@Get
	public Representation get(Variant variant) throws ResourceException {
		Representation result = null;
		if (null == this.singlereg) {
			ErrorMessage em = new ErrorMessage();
			getResponse().setStatus(Status.CLIENT_ERROR_NOT_FOUND);
			return representError(variant, em);
		} else {
				result = new JsonRepresentation(this.singlereg.toJSON());
		}
		return result;
	}

	/**
	 * Handle a PUT Http request. Update an existing widget
	 *
	 * @param entity
	 * @throws ResourceException
	 */
	@Put
	public Representation put(Representation entity) throws ResourceException {
		Representation rep = null;
		try {
			if (null == this.singlereg) {
				ErrorMessage em = new ErrorMessage();
				rep = representError(entity.getMediaType(), em);
				getResponse().setEntity(rep);
				getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				return rep;
			}
			if (entity.getMediaType().equals(MediaType.APPLICATION_WWW_FORM, true)) {
				Form form = new Form(entity);

                singlereg.setUserName(form.getFirstValue("userName"));
                singlereg.setPort(Integer.parseInt(form.getFirstValue("port")));
                singlereg.setHost(form.getFirstValue("host"));
                singlereg.setStatus(Boolean.parseBoolean(form.getFirstValue("status")));

				// persist object
				ObjectifyService.ofy()
						.save()
						.entity(this.singlereg)
						.now();

				getResponse().setStatus(Status.SUCCESS_OK);
				rep = new JsonRepresentation(this.singlereg.toJSON());
				getResponse().setEntity(rep);

			} else {
				getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			}
		} catch (Exception e) {
			getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
		}
		return rep;
	}

	/**
	 * Handle a DELETE Http Request. Delete an existing widget
	 *
	 * @param
	 * @throws ResourceException
	 */
	@Delete
	public Representation delete(Variant variant) throws ResourceException {
		Representation rep = null;
		try {
			if (null == this.singlereg) {
				ErrorMessage em = new ErrorMessage();
				rep = representError(MediaType.APPLICATION_JSON, em);
				getResponse().setEntity(rep);
				getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				return rep;
			}

			rep = new JsonRepresentation(this.singlereg.toJSON());

			// remove from persistance layer
			ObjectifyService.ofy()
				.delete()
				.entity(this.singlereg);

			getResponse().setStatus(Status.SUCCESS_OK);
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
	private Representation representError(Variant variant, ErrorMessage em)	throws ResourceException {
		Representation result = null;
		if (variant.getMediaType().equals(MediaType.APPLICATION_JSON)) {
			result = new JsonRepresentation(em.toJSON());
		} else {
			result = new StringRepresentation(em.toString());
		}
		return result;
	}

	protected Representation representError(MediaType type, ErrorMessage em) throws ResourceException {
		Representation result = null;
		if (type.equals(MediaType.APPLICATION_JSON)) {
			result = new JsonRepresentation(em.toJSON());
		} else {
			result = new StringRepresentation(em.toString());
		}
		return result;
	}
}
