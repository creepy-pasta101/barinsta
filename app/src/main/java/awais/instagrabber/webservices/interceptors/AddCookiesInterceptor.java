package awais.instagrabber.webservices.interceptors;

import androidx.annotation.NonNull;

import java.io.IOException;

import awais.instagrabber.utils.Constants;
import awais.instagrabber.utils.LocaleUtils;
import awais.instagrabber.utils.TextUtils;
import awais.instagrabber.utils.Utils;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AddCookiesInterceptor implements Interceptor {
    @NonNull
    @Override
    public Response intercept(@NonNull final Chain chain) throws IOException {
        final Request request = chain.request();
        final Request.Builder builder = request.newBuilder();
        final String cookie = Utils.settingsHelper.getString(Constants.COOKIE);
        final boolean hasCookie = !TextUtils.isEmpty(cookie);
        if (hasCookie) {
            builder.addHeader("Cookie", cookie);
        }
        // Instagram correlates the session with the app id; without it the mobile endpoints answer
        // checkpoint_required. The web profile call sets its own app id, so skip it here.
        if (request.url().host().contains("i.instagram.com")
                && !request.url().encodedPath().contains("/api/v1/users/web_profile_info")) {
            builder.header("X-IG-App-ID", "567067343352427");
        }
        final String userAgentHeader = "User-Agent";
        if (request.header(userAgentHeader) == null) {
            builder.addHeader(userAgentHeader, Utils.settingsHelper.getString(hasCookie ? Constants.APP_UA : Constants.BROWSER_UA));
        }
        final String languageHeader = "Accept-Language";
        if (request.header(languageHeader) == null) {
            builder.addHeader(languageHeader, LocaleUtils.getCurrentLocale().getLanguage() + ",en-US;q=0.8");
        }
        final Request updatedRequest = builder.build();
        return chain.proceed(updatedRequest);
    }
}