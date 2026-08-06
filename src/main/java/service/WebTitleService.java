package service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

@Service
public class WebTitleService {

    public String obtenerTitulo(String url) {

        try {

            if (!url.startsWith("http")) {
                url = "https://" + url;
            }

            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0")
                    .timeout(5000)
                    .get();

            return doc.title();

        } catch (Exception e) {

            return "Sitio Web";

        }
    }
}