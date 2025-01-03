package fi.tuni.compse110.java3.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.CompletableFuture;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.apache.hc.core5.http.message.BasicHeader;

import fi.tuni.compse110.java3.utility.DialogUtils;

/**
 * Gets data from APIs and caches the responses to avoid rate limits.
 */
public class CachedRequest {

    /**
     * Returns API response.
     *
     * @param api            API url
     * @param maxResponseAge how many seconds the response can be cached
     * @return the cached response
     * @throws IOException on failed request
     */
    public static CachedResponse get(String api, long maxResponseAge) throws IOException {
        int apiHash = api.hashCode();
        long requestTime = System.currentTimeMillis();

        CachedResponse cached = readCached("cache/" + apiHash, requestTime, maxResponseAge);

        if (cached != null) {
            return cached;
        } else {
            System.out.println(apiHash + " fetching " + api);
            CompletableFuture<CachedResponse> future = new CompletableFuture<>();

            try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
                ClassicHttpRequest request = ClassicRequestBuilder.get(api)
                        .setHeader(new BasicHeader("accept", "application/json")).build();

                httpclient.execute(request, response -> {
                    System.out.println(response.getCode() + " " + response.getReasonPhrase());

                    if (response.getCode() == 200) {
                        HttpEntity entity = response.getEntity();

                        BufferedReader bufferedReader = new BufferedReader(
                                new InputStreamReader(entity.getContent()));

                        StringBuilder out = new StringBuilder();
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            out.append(line);
                        }

                        EntityUtils.consume(entity);

                        CachedResponse cachedResponse = new CachedResponse(requestTime, out.toString());
                        try {
                            DiskIO.write("cache/" + apiHash, cachedResponse);
                        } catch (Exception e) {
                            DialogUtils.showErrorDialog("Failed to save response", e);
                        }

                        future.complete(cachedResponse);
                    } else {
                        future.completeExceptionally(
                                new IOException(response.getCode() + " " + response.getReasonPhrase()));
                    }

                    return future;
                });
            }

            return future.join();
        }
    }

    /**
     * Returns cached response or null if not found or outdated.
     *
     * @param file           the filename of saved request
     * @param requestTime    the time of current request
     * @param maxResponseAge the maximum age of the cached response in seconds
     * @return the cached response or null if not found, corrupted, or outdated
     */
    private static CachedResponse readCached(String file, long requestTime, long maxResponseAge) {
        try {
            CachedResponse cachedResponse = (CachedResponse) DiskIO.read(file, CachedResponse.class);

            if (cachedResponse == null) {
                return null; // Not found
            }

            if (cachedResponse.getTime() == 0 || cachedResponse.getResponse() == null) {
                return null; // Corrupted
            }

            long timeDiff = requestTime - cachedResponse.getTime();

            if (timeDiff > 1000 * maxResponseAge) {
                return null; // Too old
            }

            return cachedResponse;
        } catch (Exception ex) {
            // noinspection CallToPrintStackTrace
            ex.printStackTrace();

            return null; // Likely corrupted
        }
    }
}
