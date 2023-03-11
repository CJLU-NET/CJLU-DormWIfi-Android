package net.fosky.cjlu.wifi.cmcc.utils;

import java.util.HashMap;
import java.util.Map;

public class Intranet {
    public static boolean LoginUrl (String login_page, String user, String pass) {
        String ip = "";
        // String login_page = "https://portal1.cjlu.edu.cn/a33.htm";

        String[] split = login_page.substring(login_page.indexOf("?") + 1).split("&");

        for (String str : split) {
            if (str.startsWith("wlanuserip=")) {
                ip = str.split("=")[1];
                break;
            }
        }

        if (login_page.contains("a79.htm")) login_page = login_page.replace("a79.htm", "a30.htm");

        String content = HttpClient.Get(login_page, new HashMap<>());
        String mac = Helper.getSubString(content, "ss4=\"", "\";ss5");
        // String ip = Helper.getSubString(content, "v46ip='", "' ");

        String para_get = "c=ACSetting&a=Login&wlanuserip=" + ip + "&wlanacip=null&wlanacname=123457890&port=&iTermType=2&mac=" + mac + "&ip=" + ip + "&redirect=null";

        String url = "https://portal1.cjlu.edu.cn:801/eportal/?" + para_get;

        String para_post = "DDDDD=" + user + "&upass=" + pass + "&R1=0&R2=&R6=1&para=00&0MKKey=123456";

        Map<String, String> header = new HashMap<>();
        // header.put("Cookie", "Cookie: wlanacname=1234567890; wlanacip=null");

        String res = HttpClient.Post(url, para_post, header);

        if (res.contains("UID='")) {
            return res.contains("UID='12345678901234567890123456'");
        } else {
            url = "https://portal1.cjlu.edu.cn/a33.htm";
            res = HttpClient.Get(url, header);

            if (res.contains("UID='")) {
                return res.contains("UID='12345678901234567890123456'");
            }
        }

        return false;
    }

    public static boolean Login (String user, String pass) {
        String login_page = Wifi.getPortalPage();

        if (login_page.equals("")) return false;

        if (!login_page.contains("portal1.cjlu.edu.cn")) {
            return true;
        }

        return LoginUrl(login_page, user, pass);
    }
}
