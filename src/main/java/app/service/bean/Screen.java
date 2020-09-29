package app.service.bean;

import lombok.Builder;
import lombok.Getter;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

@Builder
@Getter
public class Screen {
    private final BufferedImage bufferedImage;
    private final int width;
    private final int height;

    public List<Integer> croppedToSet(int w, int h) {
        List<Integer> res = new LinkedList<>();
        for (int posY = 0; posY < this.height; posY = posY + h) {
            for (int posX = 0; posX < this.width; posX = posX + w) {
                int row = getAreaSum(posX, posY, w, h);
                res.add(row);
            }
        }
        return res;
    }

    public Integer getAreaSum(int posX, int posY, int width, int height){
        int res = 0;
        int pos = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pos++;
                int RGBA = bufferedImage.getRGB(posX + x, posY + y);
                int alpha = (RGBA >> 24) & 255;
                int red = (RGBA >> 16) & 255;
                int green = (RGBA >> 8) & 255;
                int blue = RGBA & 255;
                res += pos + alpha + red + green + blue;
            }
        }
        return res;
    }
}
