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

    public Integer getAreaSum(int posX, int posY, int width, int height) {
        int res = 0;
        int pos = 0;
        int nextX = 0;
        int nextY = 0;
        try {
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    pos++;
                    nextX = posX + x;
                    nextY = posY + y;

                    try {
                        int RGBA = bufferedImage.getRGB(nextX, nextY);
                        res += pos + RGBA;
                    } catch (ArrayIndexOutOfBoundsException exception) {
                        res += pos;
                        //skip not found color
                    }
                }
            }
        } catch (ArrayIndexOutOfBoundsException exception) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(exception.getMessage());
            stringBuilder.append(" Position: ");
            stringBuilder.append(pos);
            stringBuilder.append(" Coordinates: ");
            stringBuilder.append(nextX);
            stringBuilder.append(", ");
            stringBuilder.append(nextY);
            stringBuilder.append(" width: ");
            stringBuilder.append(width);
            stringBuilder.append(" height: ");
            stringBuilder.append(height);
            System.out.println(stringBuilder.toString());
            return -1;
        }
//        System.out.println("getAreaSum[" + pos + "]: " +  res);
        return res;
    }
}
