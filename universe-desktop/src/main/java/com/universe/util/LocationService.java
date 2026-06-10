package com.universe.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Resolves a desktop device location. Windows Location is preferred for
 * Wi-Fi/GPS accuracy; public-network geolocation remains a coarse fallback.
 */
public final class LocationService {

    private static final String DEFAULT_ENDPOINT = "https://ipwho.is/";
    private static final int DEFAULT_TIMEOUT_SECONDS = 8;

    private final URI endpoint;
    private final Duration timeout;
    private final HttpClient httpClient;

    public LocationService() {
        this.endpoint = URI.create(AppConfig.get("location.api.url", DEFAULT_ENDPOINT));
        this.timeout = Duration.ofSeconds(
                Math.max(1, AppConfig.getInt("location.api.timeout.seconds", DEFAULT_TIMEOUT_SECONDS)));
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(timeout)
                .build();
    }

    public Coordinates locate() {
        if (isWindows()) {
            try {
                return locateWithWindowsDevice();
            } catch (IllegalStateException ex) {
                if (Thread.currentThread().isInterrupted()) {
                    throw ex;
                }
            }
        }

        return locateFromNetwork();
    }

    private Coordinates locateFromNetwork() {
        try {
            return locateWithJavaHttp();
        } catch (IOException ex) {
            return locateWithWindowsNetworkFallback(ex);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Yêu cầu lấy tọa độ đã bị gián đoạn.", ex);
        }
    }

    private Coordinates locateWithJavaHttp() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(endpoint)
                .timeout(timeout)
                .header("Accept", "application/json")
                .header("User-Agent", "UniVerse-Desktop/1.0")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(
                request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IllegalStateException(
                    "Dịch vụ định vị phản hồi mã " + response.statusCode() + ".");
        }
        return parseResponse(response.body());
    }

    private Coordinates locateWithWindowsDevice() {
        String script = "[Console]::OutputEncoding=[System.Text.Encoding]::UTF8; "
                + "Add-Type -AssemblyName System.Runtime.WindowsRuntime; "
                + "$null=[Windows.Devices.Geolocation.Geolocator,"
                + "Windows.Devices.Geolocation,ContentType=WindowsRuntime]; "
                + "$locator=New-Object Windows.Devices.Geolocation.Geolocator; "
                + "$locator.DesiredAccuracy='High'; "
                + "$operation=$locator.GetGeopositionAsync(); "
                + "$method=[System.WindowsRuntimeSystemExtensions].GetMethods() "
                + "| Where-Object { $_.Name -eq 'AsTask' -and $_.IsGenericMethod "
                + "-and $_.GetParameters().Count -eq 1 } | Select-Object -First 1; "
                + "$task=$method.MakeGenericMethod("
                + "[Windows.Devices.Geolocation.Geoposition]).Invoke($null,@($operation)); "
                + "if (-not $task.Wait(" + timeout.toMillis() + ")) "
                + "{ throw 'Windows Location timed out' }; "
                + "$coordinate=$task.Result.Coordinate; "
                + "[PSCustomObject]@{"
                + "latitude=$coordinate.Point.Position.Latitude;"
                + "longitude=$coordinate.Point.Position.Longitude;"
                + "accuracy=$coordinate.Accuracy"
                + "} | ConvertTo-Json -Compress";

        JsonObject json = parseJson(runPowerShell(script));
        double latitude = getCoordinate(json, "latitude");
        double longitude = getCoordinate(json, "longitude");
        validateCoordinateRange(latitude, longitude);
        return new Coordinates(
                latitude,
                longitude,
                "",
                getOptionalNumber(json, "accuracy"),
                LocationSource.DEVICE);
    }

    private Coordinates locateWithWindowsNetworkFallback(IOException javaHttpError) {
        if (!isWindows()) {
            throw connectionError(javaHttpError);
        }

        String script = "[Console]::OutputEncoding=[System.Text.Encoding]::UTF8; "
                + "$ProgressPreference='SilentlyContinue'; "
                + "Invoke-RestMethod -Uri $env:UNIVERSE_LOCATION_URL -TimeoutSec "
                + timeout.toSeconds() + " "
                + "| ConvertTo-Json -Compress -Depth 4";
        try {
            ProcessBuilder processBuilder = createPowerShellProcess(script);
            processBuilder.environment().put("UNIVERSE_LOCATION_URL", endpoint.toString());
            return parseResponse(runProcess(processBuilder));
        } catch (IllegalStateException ex) {
            ex.addSuppressed(javaHttpError);
            throw ex;
        }
    }

    private String runPowerShell(String script) {
        return runProcess(createPowerShellProcess(script));
    }

    private ProcessBuilder createPowerShellProcess(String script) {
        ProcessBuilder processBuilder = new ProcessBuilder(
                "powershell.exe",
                "-NoLogo",
                "-NoProfile",
                "-NonInteractive",
                "-Command",
                script);
        processBuilder.redirectErrorStream(true);
        return processBuilder;
    }

    private String runProcess(ProcessBuilder processBuilder) {
        try {
            Process process = processBuilder.start();
            boolean finished = process.waitFor(timeout.toSeconds() + 4, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                throw new IllegalStateException("Yêu cầu lấy tọa độ phản hồi quá thời gian.");
            }

            String output = new String(
                    process.getInputStream().readAllBytes(), StandardCharsets.UTF_8).trim();
            if (process.exitValue() != 0) {
                throw new IllegalStateException("Windows không cung cấp được tọa độ.");
            }
            return output;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Yêu cầu lấy tọa độ đã bị gián đoạn.", ex);
        } catch (IllegalStateException ex) {
            throw ex;
        } catch (IOException ex) {
            throw connectionError(ex);
        }
    }

    private IllegalStateException connectionError(Exception cause) {
        return new IllegalStateException(
                "Không thể kết nối dịch vụ định vị. Hãy kiểm tra Internet hoặc nhập tay.",
                cause);
    }

    static Coordinates parseResponse(String responseBody) {
        JsonObject json = parseJson(responseBody);

        if (json.has("success") && !json.get("success").getAsBoolean()) {
            String message = getText(json, "message");
            throw new IllegalStateException(message.isBlank()
                    ? "Dịch vụ định vị không xác định được vị trí."
                    : message);
        }

        double latitude = getCoordinate(json, "latitude");
        double longitude = getCoordinate(json, "longitude");
        validateCoordinateRange(latitude, longitude);

        List<String> locationParts = new ArrayList<>();
        addIfPresent(locationParts, getText(json, "city"));
        addIfPresent(locationParts, getText(json, "region"));
        addIfPresent(locationParts, getText(json, "country"));
        return new Coordinates(
                latitude,
                longitude,
                String.join(", ", locationParts),
                Double.NaN,
                LocationSource.NETWORK);
    }

    private static JsonObject parseJson(String responseBody) {
        try {
            return JsonParser.parseString(responseBody).getAsJsonObject();
        } catch (RuntimeException ex) {
            throw new IllegalStateException("Dịch vụ định vị trả về dữ liệu không hợp lệ.", ex);
        }
    }

    private static double getCoordinate(JsonObject json, String name) {
        JsonElement value = json.get(name);
        if (value == null || value.isJsonNull()) {
            throw new IllegalStateException("Dịch vụ định vị thiếu trường " + name + ".");
        }
        try {
            double coordinate = value.getAsDouble();
            if (!Double.isFinite(coordinate)) {
                throw new NumberFormatException();
            }
            return coordinate;
        } catch (RuntimeException ex) {
            throw new IllegalStateException(
                    "Dịch vụ định vị trả về " + name + " không hợp lệ.", ex);
        }
    }

    private static void validateCoordinateRange(double latitude, double longitude) {
        if (latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180) {
            throw new IllegalStateException(
                    "Dịch vụ định vị trả về tọa độ ngoài phạm vi hợp lệ.");
        }
    }

    private static String getText(JsonObject json, String name) {
        JsonElement value = json.get(name);
        return value == null || value.isJsonNull() ? "" : value.getAsString().trim();
    }

    private static double getOptionalNumber(JsonObject json, String name) {
        JsonElement value = json.get(name);
        if (value == null || value.isJsonNull()) {
            return Double.NaN;
        }
        try {
            double number = value.getAsDouble();
            return Double.isFinite(number) ? number : Double.NaN;
        } catch (RuntimeException ex) {
            return Double.NaN;
        }
    }

    private static void addIfPresent(List<String> values, String value) {
        if (!value.isBlank() && !values.contains(value)) {
            values.add(value);
        }
    }

    private static boolean isWindows() {
        return System.getProperty("os.name", "")
                .toLowerCase(Locale.ROOT)
                .contains("win");
    }

    public enum LocationSource {
        DEVICE,
        NETWORK
    }

    public record Coordinates(
            double latitude,
            double longitude,
            String displayName,
            double accuracyMeters,
            LocationSource source) {
    }
}
