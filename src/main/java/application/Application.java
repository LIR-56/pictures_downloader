package application;

import image.ImagesController;
import sun.misc.SignalHandler;

import java.io.File;

import static application.Utils.deleteDir;
import static spark.Spark.*;


public class Application {

    public static void main(String[] args) {

        createDirForImages();
        addLogicForGracefulShutdown();

        port(8080);
        staticFiles.externalLocation(System.getProperty("java.io.tmpdir"));

        path("/api", () ->
                path("/v1", () -> {
                    before("/*", (request, response) -> response.type("application/json"));

                    post("/imageDownloader", ImagesController.addImages);
                    delete("/images/:id", ImagesController.deleteImage);
                    get("/images", ImagesController.fetchImagesList);
                })
        );

        get("/downloaded/images/:id", ImagesController.fetchImage);
        get("/downloaded/images/:id/small", ImagesController.fetchSmallImage);
    }


    private static void createDirForImages() {
        File f = new File(image.ImagesController.dirWithImagesFullName);
        if (!f.exists()) {
            f.mkdir();
        }
    }


    private static void addLogicForGracefulShutdown() {
        SignalHandler signalHandler = sig -> {
            stop();
            deleteDir(new File(System.getProperty("java.io.tmpdir") + "/images/"));
        };

        ShutdownSignalHandler.install("TERM", signalHandler);
        ShutdownSignalHandler.install("INT", signalHandler);
        ShutdownSignalHandler.install("ABRT", signalHandler);
    }


}
