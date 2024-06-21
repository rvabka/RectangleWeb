package pl.umcs.oop.imageweb;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

@RestController
@RequestMapping("/api")
public class ImageFromController {
    @GetMapping
    public String showSite(){
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
        return site;
    }

    public BufferedImage imageBrighter(BufferedImage srcImage, int value) throws IOException {

        for (int y = 0; y < srcImage.getHeight(); y++) {
            for (int x = 0; x < srcImage.getWidth(); x++) {
                int rgb = srcImage.getRGB(x, y);
                int a = (rgb >> 24) & 0xff;
                int r = ((rgb >> 16) & 0xff) + value;
                if(r > 255) r = 255;
                else if(r < 0) r = 0;
                int g = ((rgb >> 8) & 0xff) + value;
                if(g > 255) g = 255;
                else if(g < 0) g = 0;
                int b = (rgb & 0xff) + value;
                if(b > 255) b = 255;
                else if(b < 0) b = 0;

                int newRGB = (a << 24) | (r << 16) | (g << 8) | b;
                srcImage.setRGB(x, y, newRGB);
            }
        }
        return srcImage;
    }

    public String getBrighterEncodedImage(int value, String secretMessage) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(secretMessage);
            ByteArrayInputStream bais = new ByteArrayInputStream(decodedBytes);
            BufferedImage image = ImageIO.read(bais);

            BufferedImage brighterImage = imageBrighter(image, value);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(brighterImage, "png", baos);
            byte[] outputBytes = baos.toByteArray();

            return Base64.getEncoder().encodeToString(outputBytes);
        }
        catch (IOException e) {
            throw new RuntimeException("failed to process image", e);
        }
    }
@PostMapping("/upload")
@ResponseBody
public String handleFileUpload(@RequestParam("image") MultipartFile file, @RequestParam("brightness") int brightness) {
    if (!file.isEmpty() && file.getContentType().startsWith("image")) {
        try {
            byte[] bytes = file.getBytes();
            String base64Image = Base64.getEncoder().encodeToString(bytes);
            base64Image = getBrighterEncodedImage(brightness, base64Image);
            return "<html><body><img src='data:image/png;base64, " + base64Image + "' /></body></html>";
        } catch (IOException e) {
            e.printStackTrace();
            return "Błąd podczas przetwarzania obrazka.";
        }
    } else {
        return "Błąd - proszę wybrać plik obrazka.";
    }
}
}
