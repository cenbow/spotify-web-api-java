package se.michaelthelin.spotify.methods;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.SettableFuture;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import se.michaelthelin.spotify.Api;
import se.michaelthelin.spotify.TestConfiguration;
import se.michaelthelin.spotify.TestUtil;
import se.michaelthelin.spotify.models.Artist;
import se.michaelthelin.spotify.models.Page;
import se.michaelthelin.spotify.models.SpotifyEntityType;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.*;

@RunWith(MockitoJUnitRunner.class)
public class ArtistSearchRequestTest {

  @Test
  public void shouldGetArtistsResult_async() throws Exception {
    final Api api = Api.DEFAULT_API;

    final ArtistSearchRequest.Builder requestBuilder = api.searchArtists("tania bowra").limit(20).offset(0);
    if (TestConfiguration.USE_MOCK_RESPONSES) {
      requestBuilder.httpManager(TestUtil.MockedHttpManager.returningJson("search-artist.json"));
    }
    final ArtistSearchRequest request = requestBuilder.build();

    final CountDownLatch asyncCompleted = new CountDownLatch(1);

    final SettableFuture<Page<Artist>> searchResultFuture = request.getAsync();

    Futures.addCallback(searchResultFuture, new FutureCallback<Page<Artist>>() {
      @Override
      public void onSuccess(Page<Artist> artistSearchResult) {
        assertEquals(20, artistSearchResult.getLimit());
        assertEquals(0, artistSearchResult.getOffset());
        assertTrue(artistSearchResult.getTotal() > 0);
        assertEquals("null", artistSearchResult.getNext());
        assertEquals("null", artistSearchResult.getPrevious());
        assertEquals("https://api.spotify.com/v1/search?query=tania+bowra&offset=0&limit=20&type=artist", artistSearchResult.getHref());

        List<Artist> artists = artistSearchResult.getItems();

        Artist firstArtist = artists.get(0);
        assertEquals("08td7MxkoHQkXnWAYD8d6Q", firstArtist.getId());
        assertEquals("https://open.spotify.com/artist/08td7MxkoHQkXnWAYD8d6Q", firstArtist.getExternalUrls().get("spotify"));
        assertNotNull(firstArtist.getGenres());
        assertEquals("https://api.spotify.com/v1/artists/08td7MxkoHQkXnWAYD8d6Q", firstArtist.getHref());
        assertNotNull(firstArtist.getImages());
        assertEquals("Tania Bowra", firstArtist.getName());
        assertTrue(firstArtist.getPopularity() >= 0 && firstArtist.getPopularity() <= 100);
        assertEquals(SpotifyEntityType.ARTIST, firstArtist.getType());
        assertEquals("spotify:artist:08td7MxkoHQkXnWAYD8d6Q", firstArtist.getUri());

        asyncCompleted.countDown();
      }

      @Override
      public void onFailure(Throwable throwable) {
        fail("Failed to resolve future");
      }
    });

    asyncCompleted.await(1, TimeUnit.SECONDS);
  }

  @Test
  public void shouldGetArtistsResult_sync() throws Exception {
    final Api api = Api.DEFAULT_API;

    final ArtistSearchRequest.Builder requestBuilder = api.searchArtists("tania bowra");
    if (TestConfiguration.USE_MOCK_RESPONSES) {
      requestBuilder.httpManager(TestUtil.MockedHttpManager.returningJson("search-artist.json"));
    }
    final ArtistSearchRequest request = requestBuilder.build();

    final Page<Artist> artistSearchResult = request.get();

    assertEquals(20, artistSearchResult.getLimit());
    assertEquals(0, artistSearchResult.getOffset());
    assertTrue(artistSearchResult.getTotal() > 0);
    assertEquals("null", artistSearchResult.getNext());
    assertEquals("null", artistSearchResult.getPrevious());
    assertEquals("https://api.spotify.com/v1/search?query=tania+bowra&offset=0&limit=20&type=artist", artistSearchResult.getHref());

    List<Artist> artists = artistSearchResult.getItems();

    Artist firstArtist = artists.get(0);
    assertEquals("08td7MxkoHQkXnWAYD8d6Q", firstArtist.getId());
    assertEquals("https://open.spotify.com/artist/08td7MxkoHQkXnWAYD8d6Q", firstArtist.getExternalUrls().get("spotify"));
    assertNotNull(firstArtist.getGenres());
    assertEquals("https://api.spotify.com/v1/artists/08td7MxkoHQkXnWAYD8d6Q", firstArtist.getHref());
    assertNotNull(firstArtist.getImages());
    assertEquals("Tania Bowra", firstArtist.getName());
    assertTrue(firstArtist.getPopularity() >= 0 && firstArtist.getPopularity() <= 100);
    assertEquals(SpotifyEntityType.ARTIST, firstArtist.getType());
    assertEquals("spotify:artist:08td7MxkoHQkXnWAYD8d6Q", firstArtist.getUri());
  }

}