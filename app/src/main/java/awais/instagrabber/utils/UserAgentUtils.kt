@file:JvmName("UserAgentUtils")

package awais.instagrabber.utils

/* GraphQL user agents (which are just standard browser UA"s).
 * Go to https://www.whatismybrowser.com/guides/the-latest-user-agent/ to update it
 * Windows first (Assume win64 not wow64): Chrome, Firefox, Edge
 * Then macOS: Chrome, Firefox, Safari
 */
@JvmField
val browsers = arrayOf(
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.81 Safari/537.36",
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:93.0) Gecko/20100101 Firefox/93.0",
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.81 Safari/537.36 Edg/94.0.992.47",
    "Mozilla/5.0 (Macintosh; Intel Mac OS X 11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.81 Safari/537.36",
    "Mozilla/5.0 (Macintosh; Intel Mac OS X 11.6; rv:93.0) Gecko/20100101 Firefox/93.0",
    "Mozilla/5.0 (Macintosh; Intel Mac OS X 11_6) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.0 Safari/605.1.15"
)

// The Instagram Android app version this client impersonates. Instagram rejects requests that
// claim a stale app version ("needs_upgrade" / "checkpoint_required"), so this must be refreshed
// against a current build. See https://www.apkmirror.com/apk/instagram/ (pick a recent stable
// release and copy its version name and version code).
private const val igVersion = "451.0.0.53.80"
private const val igVersionCode = "385713116"

// you can pick *any* device as long as you LEAVE OUT the resolution for maximum download quality
// https://github.com/dilame/instagram-private-api/blob/master/src/samples/devices.json
// Kept to recent hardware running a current Android release: a modern app version paired with a
// 2016-era device/OS is itself an inconsistency Instagram can flag.
@JvmField
val devices = arrayOf(
    "34/14; 420dpi; 1080x2400; Google/google; Pixel 8 Pro; husky; husky",
    "34/14; 420dpi; 1080x2340; Google/google; Pixel 8; shiba; shiba",
    "33/13; 420dpi; 1080x2400; Google/google; Pixel 7; panther; panther",
    "34/14; 480dpi; 1440x3120; samsung; SM-S918B; dm3q; qcom",
    "34/14; 450dpi; 1080x2340; samsung; SM-S911B; dm1q; qcom",
    "33/13; 420dpi; 1080x2340; samsung; SM-A546B; a54x; s5e8835",
    "34/14; 480dpi; 1080x2400; Xiaomi; 23127PN0CG; aurora; qcom",
    "33/13; 440dpi; 1080x2400; Xiaomi; 2211133G; fuxi; qcom",
    "34/14; 420dpi; 1080x2412; OnePlus; CPH2581; 12; qcom",
    "33/13; 420dpi; 1080x2400; OnePlus; CPH2451; 11; qcom",
    "34/14; 420dpi; 1080x2400; motorola; moto g84 5G; bangkk; qcom",
    "33/13; 420dpi; 1080x2400; realme; RMX3780; RE5C2A; qcom",
    "34/14; 420dpi; 1080x2400; OPPO; CPH2557; OP5C2A; qcom",
    "33/13; 420dpi; 1080x2400; vivo; V2246; PD2246; qcom",
    "34/14; 420dpi; 1080x2400; Nothing; A065; Spacewar; qcom",
    "33/13; 420dpi; 1080x2400; HONOR; RKY-LX1; HWRKY; qcom"
)

fun generateBrowserUA(code: Int): String {
    return browsers[code]
}

fun generateAppUA(code: Int, lang: String): String {
    return "Instagram " + igVersion + " Android (" + devices[code] + "; " + lang + "; " + igVersionCode + ")"
}
