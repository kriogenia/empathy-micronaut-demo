package co.empathy.controllers.search;

import co.empathy.util.TestHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.uri.UriBuilder;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
public class Week2SearchIntegrationTest {

	@Inject
	@Client("/")
	RxHttpClient client;

	@Inject
	ObjectMapper mapper;

	@Inject
	TestHelper helper;

	// TODO fix all

	private final UriBuilder baseUri = UriBuilder.of("/search");
	private final UriBuilder allUri = UriBuilder.of("/search/all");

	private final Logger LOG = LoggerFactory.getLogger(Week2SearchIntegrationTest.class);

	@Test
	public void testForrestGump() throws JsonProcessingException {
		LOG.info("Search the Forrest Gump movie with title query");
		var uri = baseUri.queryParam("title", "Forrest Gump").toString();
		var request = HttpRequest.GET(uri);
		var jsonResult = client.toBlocking().retrieve(request);
		var retrieved = mapper.readValue(jsonResult, helper.getImdbResponseType());
		// Check we got the movie
		assertNotNull(retrieved);
		assertNotNull(retrieved.getItems());
		// Forrest Gump retrieve
		var forrestGumpMovie = retrieved.getItems().stream()
				.filter(x -> Objects.requireNonNull(x.getPrimaryTitle()).equals("Forrest Gump"))
				.filter(x -> Objects.requireNonNull(x.getTitleType()).equals("movie"))
				.collect(Collectors.toList());
		// At least one movie
		assertEquals(1, forrestGumpMovie.size());
		// What's its id?
		LOG.info("Forrest Gump Id: {}", forrestGumpMovie.get(0).getId());
	}

	@Test
	public void testTheAvengersSingle() throws JsonProcessingException {
		LOG.info("Search the 2012 The Avengers movie with title query");
		var uri = baseUri.queryParam("title", "The Avengers").toString();
		var request = HttpRequest.GET(uri);
		var jsonResult = client.toBlocking().retrieve(request);
		var retrieved = mapper.readValue(jsonResult, helper.getImdbResponseType());
		// Check we didn't get the 2012 movie
		assertNotNull(retrieved);
		assertNotNull(retrieved.getItems());
		var theAvengers = retrieved.getItems().stream().filter(
				x -> Objects.requireNonNull(x.getTitleType()).equals("movie")
						&& Objects.requireNonNull(x.getStartYear()).equals("2012"));
		assertFalse(theAvengers.count() > 0);
		LOG.info("Movie The Avengers (2012) not found");
	}

	@Test
	public void testTheAvengersMulti() throws JsonProcessingException {
		LOG.info("Search the 2012 The Avengers movie with general query");
		var uri = allUri.queryParam("query", "the avengers movie").toString();
		var request = HttpRequest.GET(uri);
		var jsonResult = client.toBlocking().retrieve(request);
		var retrieved = mapper.readValue(jsonResult, helper.getImdbResponseType());
		// Check we got the 2012 movie
		assertNotNull(retrieved);
		assertNotNull(retrieved.getItems());
		var theAvengers = retrieved.getItems().stream().filter(
				x -> Objects.requireNonNull(x.getTitleType()).equals("movie")
						&& Objects.requireNonNull(x.getStartYear()).equals("2012"));
		assertTrue(theAvengers.count() > 0);
		LOG.info("Movie The Avengers (2012) found");
	}

	@Test
	public void testSpidermanSingle() throws JsonProcessingException {
		LOG.info("Search the 2002 Spiderman movie with title query");
		var uri = baseUri.queryParam("title", "Spiderman").toString();
		var request = HttpRequest.GET(uri);
		var jsonResult = client.toBlocking().retrieve(request);
		var retrieved = mapper.readValue(jsonResult, helper.getImdbResponseType());
		// Check we got the movie
		assertNotNull(retrieved);
		assertNotNull(retrieved.getItems());
		var spidermanTobey = retrieved.getItems().stream().filter(
				x -> Objects.requireNonNull(x.getTitleType()).equals("movie")
						&& Objects.requireNonNull(x.getStartYear()).equals("2002"));
		assertFalse(spidermanTobey.count() > 0);
		LOG.info("Movie Spiderman (2002) not found");
	}

	@Test
	public void testSpidermanMulti() throws JsonProcessingException {
		LOG.info("Search the 2002 Spiderman movie with general query");
		var uri = allUri.queryParam("query", "Spiderman movie").toString();
		var request = HttpRequest.GET(uri);
		var jsonResult = client.toBlocking().retrieve(request);
		var retrieved = mapper.readValue(jsonResult, helper.getImdbResponseType());
		// Check we got the movie
		assertNotNull(retrieved);
		assertNotNull(retrieved.getItems());
		var spidermanTobey = retrieved.getItems().stream().filter(
				x -> Objects.requireNonNull(x.getTitleType()).equals("movie")
						&& Objects.requireNonNull(x.getStartYear()).equals("2002"));
		assertTrue(spidermanTobey.count() > 0);
		LOG.info("Movie Spiderman (2002) found (yeah, this search really improved)");
	}

}