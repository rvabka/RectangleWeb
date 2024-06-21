package pl.umcs.oop.imageweb;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

@RestController // Adnotacja określająca, że jest to kontroler REST
@RequestMapping("/api") // Ustawia podstawowy URL dla wszystkich endpointów w tym kontrolerze
public class ImageFromController {

    @GetMapping // Adnotacja określająca, że jest to metoda obsługująca żądania GET
    public String showSite(){
        // Tworzy stronę HTML z formularzem do wysyłania obrazka
        String site = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Upload Image</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <h2>Wyślij obrazek</h2>\n" +
                "    <form method=\"post\" action=\"/api/upload\" enctype=\"multipart/form-data\">\n" +
                "        <input type=\"file\" name=\"image\" accept=\"image/*\">\n" +
                "        <br><br>\n" +
                "        <label for=\"brightness\">Zmiana jasności:</label>\n" +
                "        <input type=\"range\" id=\"brightness\" name=\"brightness\" min=\"-255\" max=\"255\" value=\"0\">\n" +
                "        <br><br>\n" +
                "        <button type=\"submit\">Upload</button>\n" +
                "    </form>\n" +
                "</body>\n" +
                "</html>";
        return site; // Zwraca stronę HTML
    }

    // Metoda zmieniająca jasność obrazu
    public BufferedImage imageBrighter(BufferedImage srcImage, int value) throws IOException {
        // Iteruje przez każdy piksel obrazu
        for (int y = 0; y < srcImage.getHeight(); y++) {
            for (int x = 0; x < srcImage.getWidth(); x++) {
                int rgb = srcImage.getRGB(x, y); // Pobiera kolor piksela
                int a = (rgb >> 24) & 0xff; // Pobiera kanał alfa
                int r = ((rgb >> 16) & 0xff) + value; // Zwiększa wartość czerwieni
                if(r > 255) r = 255; // Upewnia się, że wartość nie przekracza 255
                else if(r < 0) r = 0; // Upewnia się, że wartość nie jest mniejsza niż 0
                int g = ((rgb >> 8) & 0xff) + value; // Zwiększa wartość zieleni
                if(g > 255) g = 255;
                else if(g < 0) g = 0;
                int b = (rgb & 0xff) + value; // Zwiększa wartość niebieskiego
                if(b > 255) b = 255;
                else if(b < 0) b = 0;

                // Składa nowy kolor piksela
                int newRGB = (a << 24) | (r << 16) | (g << 8) | b;
                srcImage.setRGB(x, y, newRGB); // Ustawia nowy kolor piksela
            }
        }
        return srcImage; // Zwraca zmodyfikowany obraz
    }

    // Metoda zwracająca jaśniejszy obraz w formacie Base64
    public String getBrighterEncodedImage(int value, String secretMessage) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(secretMessage); // Dekoduje obraz z Base64
            ByteArrayInputStream bais = new ByteArrayInputStream(decodedBytes); // Tworzy strumień wejściowy z bajtów obrazu
            BufferedImage image = ImageIO.read(bais); // Odczytuje obraz ze strumienia

            BufferedImage brighterImage = imageBrighter(image, value); // Zmienia jasność obrazu

            ByteArrayOutputStream baos = new ByteArrayOutputStream(); // Tworzy strumień wyjściowy
            ImageIO.write(brighterImage, "png", baos); // Zapisuje zmodyfikowany obraz do strumienia
            byte[] outputBytes = baos.toByteArray(); // Pobiera bajty zmodyfikowanego obrazu

            return Base64.getEncoder().encodeToString(outputBytes); // Koduje bajty obrazu do Base64 i zwraca jako string
        } catch (IOException e) {
            throw new RuntimeException("failed to process image", e); // Rzuca wyjątek w przypadku błędu
        }
    }

    @PostMapping("/upload") // Adnotacja określająca, że jest to metoda obsługująca żądania POST na /api/upload
    @ResponseBody // Określa, że wynik metody jest ciałem odpowiedzi
    public String handleFileUpload(@RequestParam("image") MultipartFile file, @RequestParam("brightness") int brightness) {
        if (!file.isEmpty() && file.getContentType().startsWith("image")) { // Sprawdza, czy plik nie jest pusty i czy jest obrazem
            try {
                byte[] bytes = file.getBytes(); // Pobiera bajty pliku
                String base64Image = Base64.getEncoder().encodeToString(bytes); // Koduje bajty pliku do Base64
                base64Image = getBrighterEncodedImage(brightness, base64Image); // Zmienia jasność obrazu
                // Zwraca stronę HTML z obrazem w formacie Base64
                return "<html><body><img src='data:image/png;base64, " + base64Image + "' /></body></html>";
            } catch (IOException e) {
                e.printStackTrace(); // Wypisuje stos błędów w przypadku wyjątku
                return "Błąd podczas przetwarzania obrazka."; // Zwraca komunikat błędu
            }
        } else {
            return "Błąd - proszę wybrać plik obrazka."; // Zwraca komunikat błędu, jeśli plik nie jest obrazem lub jest pusty
        }
    }
}
