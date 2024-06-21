package pl.umcs.oop.imageweb;

import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

@RestController
@RequestMapping("/image")
public class ImageController {

    @GetMapping("/getBrighterEncodedImage")
    public String getBrighterEncodedImage(@RequestParam  int value, @RequestBody String secretMessage) {
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

    @GetMapping("/getBrighterDecodedImage")
    public String getBrighterDecodedImage(@RequestParam  int value, @RequestBody String secretMessage) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(secretMessage);
            ByteArrayInputStream bais = new ByteArrayInputStream(decodedBytes);
            BufferedImage image = ImageIO.read(bais);

            BufferedImage brighterImage = imageBrighter(image, value);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(brighterImage, "png", baos);
            byte[] outputBytes = baos.toByteArray();
            String encodedImage = Base64.getEncoder().encodeToString(outputBytes);
            String decodedImage = new String(Base64.getDecoder().decode(encodedImage));
            return decodedImage;
        }
        catch (IOException e) {
            throw new RuntimeException("failed to process image", e);
        }


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

    @GetMapping("/testImage")
    public String testImage() {
        try {
            // Tworzenie prostego obrazu (1x1 piksel, czerwony)
            BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
            img.setRGB(0, 0, (255 << 16) | (0 << 8) | 0);

            // Konwersja obrazu do Base64
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(img, "png", baos);
            byte[] bytes = baos.toByteArray();
            String base64Image = Base64.getEncoder().encodeToString(bytes);

            return base64Image;
        } catch (IOException e) {
            throw new RuntimeException("Failed to encode image", e);
        }
    }

}
