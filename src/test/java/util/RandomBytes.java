package util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Objects.requireNonNull;

public final class RandomBytes {

    public static URL getResource(final int count) {
        return requireNonNull(RandomBytes.class.getResource("RandomBytes" + count + ".txt"));
    }

    public static <R> R applyResourceStream(final int count, final Function<? super InputStream, ? extends R> function)
            throws IOException {
        requireNonNull(function, "function is null");
        try (InputStream stream = getResource(count).openStream()) {
            return function.apply(stream);
        }
    }

    public static <R> R applyResourceIntStream(final int count, final Function<? super IntStream, ? extends R> function)
            throws URISyntaxException, IOException {
        requireNonNull(function, "function is null");
        try (IntStream stream = Files.lines(Paths.get(getResource(count).toURI()))
                .filter(Objects::nonNull)
                .map(String::trim)
                .mapToInt(Integer::parseInt)) {
            return function.apply(stream);
        }
    }

    public static List<Integer> getResourceList(final int count) throws URISyntaxException, IOException {
        return applyResourceIntStream(count, s -> s.boxed().collect(Collectors.toList()));
    }

    public static byte[] bytes(final int count) {
        getResource(count);
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            Files.lines(Paths.get(getResource(count).toURI()))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(l -> Integer.parseInt(l, 16))
                    .forEach(baos::write);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
        return baos.toByteArray();
    }

    private RandomBytes() {
        throw new AssertionError("instantiation is not allowed");
    }
}
