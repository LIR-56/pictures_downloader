package image;

import spark.Request;
import spark.Response;
import spark.Route;
import org.json.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import application.StandardResponse;


public class ImagesController {

    private static final String dirWithImages = "/images/";
    public static final String dirWithImagesFullName = System.getProperty("java.io.tmpdir") + dirWithImages;

    private static final ArrayList<ImageItem> images = new ArrayList<>();
    private static volatile int nextItemId = 0;

    public static Route addImages = (Request request, Response response) -> {

        StandardResponse result = new StandardResponse();
        JSONArray arr;
        try {
            JSONObject a = new JSONObject(request.body());
            arr = a.getJSONArray("images");
        } catch (Exception e) {
            result.setStatus("Error");
            result.setData(result.getData().append("Cannot parse images URL from input JSON."));
            return result.toJSON();
        }

        result.setData(result.getData().append("["));
        for (Object i : arr) {
            String url = i.toString();
            int currentID;
            synchronized (images) {
                currentID = nextItemId;
                nextItemId++;
                images.add(null);
            }
            try{
                URL imageInInternet = new URL(url);
                ReadableByteChannel rbc = Channels.newChannel(imageInInternet.openStream());
                FileOutputStream fos = new FileOutputStream(new File(dirWithImagesFullName + currentID));
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

                BufferedImage imageDownloaded = ImageIO.read(new File(dirWithImagesFullName + currentID));
                ImageItem image = new ImageItem(imageDownloaded , url);
                ImageIO.write(image.getImageSmall(),
                        "png",
                        new File(dirWithImagesFullName + currentID + "_small"));

                images.add(currentID, image);
                result.setData(result.getData().append("\n").append(toJSON(currentID, image)));
                result.setData(result.getData().append(","));
            } catch (Exception e) {
                result.setData(result.getData().append("\n{\n" + "\"error\": ").
                        append(e.getMessage()).
                        append(",\n").
                        append("\"source\": ").
                        append(url).
                        append("\n").append("},"));

                removeImageFilesByID(currentID);
            }
        }

        result.setData(result.getData().deleteCharAt(result.getData().length() - 1).append("]"));
        return result.toJSON();
    };

    public static Route deleteImage = (request, response) -> {
        int currentID = Integer.parseInt(request.params(":id"));
        StandardResponse result = new StandardResponse();
        if (currentID < 0 || currentID >= images.size()) {
            result.setStatus("Error");
            result.setData("\"Image not found\"");
            return result.toJSON();
        }

        if (images.get(currentID) == null) {
            result.setData("\"Image was already removed\"");
            result.setStatus("Error");
            return result.toJSON();
        }

        images.set(currentID, null);
        removeImageFilesByID(currentID);

        result.setData("\"Image was deleted: " + currentID + "\"");
        return result.toJSON();
    };

    public static Route fetchImagesList = (request, response) -> {
        StringBuilder result = new StringBuilder();
        result.append("{ \"images\": [");
        for (int i = 0; i < images.size(); i++) {
            if (images.get(i) != null) {
                result.append(toJSON(i, images.get(i)));
                result.append(",");
            }
        }
        if (result.lastIndexOf(",") == result.length()-1) {
            result.deleteCharAt(result.length() - 1); //remove last comma
        }
        result.append("] }");
        return result.toString();
    };

    public static Route fetchImage = (request, response) -> {
        int currentId = Integer.parseInt(request.params(":id"));
        if (currentId < 0 || currentId >= images.size()) {
            response.status(404);
            return "Image not found";
        }
        if (images.get(currentId) == null) {
            response.status(410);
            return "Image was removed";
        }
        return getHTMLForImage(currentId, false);
    };


    public static Route fetchSmallImage = (request, response) -> {
        int currentId = Integer.parseInt(request.params(":id"));
        if (currentId < 0 || currentId >= images.size()) {
            response.status(404);
            return getHTMLForError("Image not found");
        }
        if (images.get(currentId) == null) {
            response.status(410);
            return getHTMLForError("Image was removed");
        }
        return getHTMLForImage(currentId, true);
    };


    private static String getHTMLForImage(int id, boolean isSmall) {
        return "<html><body><img src=\"" +
                dirWithImages + id + (isSmall ? "_small" : "") + "\"></body></html>";
    }

    private static String getHTMLForError(String error) {
        return "<html><body><h1>"+ error +"</h1></body></html>";

    }

    private static String toJSON(int id, ImageItem image) {
        return "{\n" +
                "\"id\": " + id + ",\n" +
                "\"source\": " + image.getSource() + "\n" +
                "}";
    }

    private static void removeImageFilesByID(int id) {
        File imageFileToDelete = new File(dirWithImagesFullName + id);
        if (imageFileToDelete.exists()) {
            imageFileToDelete.delete();
        }

        File smallImageFileToDelete = new File(dirWithImagesFullName + id + "_small");
        if (smallImageFileToDelete.exists()) {
            smallImageFileToDelete.delete();
        }
    }
}
