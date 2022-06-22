package foo;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.google.api.server.spi.auth.common.User;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.config.Nullable;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.api.client.googleapis.auth.clientlogin.ClientLogin.Response;
import com.google.api.server.spi.auth.EspAuthenticator;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.PropertyProjection;
import com.google.appengine.api.datastore.PreparedQuery.TooManyResultsException;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.appengine.api.datastore.Transaction;

@Api(name = "myApi",
     version = "v1",
     audiences = "381930324621-0odprcg2kfa6bn0tmgs9v095nvjvr2ul.apps.googleusercontent.com",
  	 clientIds = "381930324621-0odprcg2kfa6bn0tmgs9v095nvjvr2ul.apps.googleusercontent.com",
     namespace =
     @ApiNamespace(
		   ownerDomain = "helloworld.example.com",
		   ownerName = "helloworld.example.com",
		   packagePath = "")
     )

public class PetitionEndpoint {
  


	@ApiMethod(name = "petitions", httpMethod = HttpMethod.GET)
	public CollectionResponse<Entity> petitions(@Nullable @Named("nextPage") String nextPage) {
        FetchOptions fetchOptions = FetchOptions.Builder.withLimit(10);
		Query q = new Query("Petition");
        if (!nextPage.equals("0")) {
            fetchOptions.startCursor(Cursor.fromWebSafeString(nextPage));
        }
        q.addSort("nombreSignature", SortDirection.DESCENDING).addSort("created_at", SortDirection.DESCENDING);
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		PreparedQuery pq = datastore.prepare(q);
        QueryResultList<Entity> petitions;
       
        petitions =  pq.asQueryResultList(fetchOptions);
        String cursorString = petitions.getCursor().toWebSafeString();
    
        return CollectionResponse.<Entity>builder().setItems(petitions).setNextPageToken(cursorString).build();
	}
    @ApiMethod(name = "top100", httpMethod = HttpMethod.GET)
	public List<Entity> top100() {
        FetchOptions fetchOptions = FetchOptions.Builder.withLimit(100);
		Query q = new Query("Petition");
        q.addSort("nombreSignature", SortDirection.DESCENDING).addSort("created_at", SortDirection.DESCENDING);
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		PreparedQuery pq = datastore.prepare(q);
        QueryResultList<Entity> result = pq.asQueryResultList(fetchOptions);
        return result;
	}

    @ApiMethod(name = "mesPetitions", path = "petitions/{userID}/mesPetitions",httpMethod = HttpMethod.GET)
	public CollectionResponse<Entity> mesPetitions(@Nullable @Named("nextPage") String nextPage, @Named("userID") String userID) {
        FetchOptions fetchOptions = FetchOptions.Builder.withLimit(10);
		Query q = new Query("Petition");
        if (!nextPage.equals("0")) {
            fetchOptions.startCursor(Cursor.fromWebSafeString(nextPage));
        }
        q.setFilter(new FilterPredicate("signataires", FilterOperator.EQUAL, userID)).addSort("created_at", SortDirection.DESCENDING);
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		PreparedQuery pq = datastore.prepare(q);
        QueryResultList<Entity> petitions;
       
        petitions =  pq.asQueryResultList(fetchOptions);
        String cursorString = petitions.getCursor().toWebSafeString();
    
    
        return CollectionResponse.<Entity>builder().setItems(petitions).setNextPageToken(cursorString).build();
	}
    @ApiMethod(name = "savePetition", httpMethod = HttpMethod.POST)
	public Entity savePetition(Petition p) {
       
        Random r = new Random();
		int k = r.nextInt(50000);
		Entity e = new Entity("Petition", Long.MAX_VALUE-(new Date()).getTime()+":"+k); // quelle est la clef ?? non specifié -> clef automatique
		e.setProperty("proprietaire", p.proprietaire);
        e.setProperty("nomProprietaire", p.nomProprietaire);
		e.setProperty("description", p.description);
		e.setProperty("titre", p.titre);
		e.setProperty("theme", p.theme);
        e.setProperty("nombreSignature", 0);
		e.setProperty("created_at", new Date());
        e.setProperty("nomProprietaire", p.nomProprietaire);
        ArrayList<String> signataires =new ArrayList<String>();
        signataires.add(p.proprietaire);
        e.setProperty("signataires", signataires);
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(e);
		return e;
	}

    @ApiMethod(name = "infoPetition", path="petitions/{petitionID}", httpMethod = HttpMethod.GET)
	public Entity infoPetition(@Named("petitionID") String petitionID) throws EntityNotFoundException {

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Key petitionKey = new Entity("Petition", petitionID).getKey();
		Entity e = datastore.get(petitionKey);
		return e;
	}
    @ApiMethod(name = "addSignature", path="signature/petition/{petitionID}/{userID}", httpMethod = HttpMethod.GET)
	public Entity addSignature(@Named("petitionID") String petitionID, @Named("userID") String userID) throws EntityNotFoundException  {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Query q = new Query("Petition");
        Key petitionKey = new Entity("Petition", petitionID).getKey();
        q.setFilter(CompositeFilterOperator.and(
				new FilterPredicate("__key__", FilterOperator.EQUAL, petitionKey),
				new FilterPredicate("signataires", FilterOperator.EQUAL, userID) 
		)); 

        PreparedQuery pq = datastore.prepare(q);
        QueryResultList<Entity> result = pq.asQueryResultList(FetchOptions.Builder.withDefaults());
        
        Entity response = new Entity("Response");
        response.setProperty("status", "ok");
        
        
        if(result.isEmpty()) {
            Transaction txn = datastore.beginTransaction();
            Entity e = datastore.get(petitionKey);

            long nbSignataire = (long) e.getProperty("nombreSignature");
            nbSignataire ++;
            e.setProperty("nombreSignature", nbSignataire);
            @SuppressWarnings("unchecked") // Cast can't verify generic type.
            ArrayList<String> signataires = (ArrayList<String>) e.getProperty("signataires");
            signataires.add(userID);
            e.setProperty("signataires", signataires);
            datastore.put(e); 
            txn.commit();
            response.setProperty("status", "ok");
        }else {
            response.setProperty("status", "nok");
            
        }
        
	
		return response;
	}

}
