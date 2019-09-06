Picture downloader in format of REST API service

There are several ways to run the application (all commands should be executed in application directory):
1. Using docker-compose:
   
        docker-compose up
2. Using docker:
   
        docker build -t picture_downloader .
        docker run --rm -it picture_downloader:latest
3. Using maven and java (jdk 9 required) directly:
        
        To build project:
            mvn -f pom.xml clean package

        To run project:
            java -jar ./target/my-app-1.0.jar

How to use:
Application starts on 8080 port.

Available REST API interactions:

1.        POST http://localhost:8080/api/v1/imageDownloader  - to download images, input should be in JSON format and contain "images" array with sources URLs, like:
        {
                "images":["ftp://95.143.198.59/sockenbilder.se/bjuraker2/foreg.gif",
                        "https://athleticsport.pl/36926-home_default/tcz10-talerz-czarny-10-kg-hms.jpg",
                        "https://www.janome.com/inspire/Embroidery/penguin-bmp-design-from-digitizer-10000/penguin-bmp-design-from-digitizer-10000/dig10k_penguin.bmp",
                        "https://upload.wikimedia.org/wikipedia/commons/4/47/PNG_transparency_demonstration_1.png",
                        "https://upload.wikimedia.org/wikipedia/commons/2/2c/Rotating_earth_(large).gif"]
        }
        
        In answer system will return JSON with array "data", which in normal case would contain entry for each source like:
        {
                "id": 143,
                "source": ftp://95.143.198.59/sockenbilder.se/bjuraker2/foreg.gif
        }
        where id is new id of image in system.


2.      GET http://localhost:8080/api/v1/images - return list of all images in the same format (id + source)
3.      DELETE http://localhost:8080/api/v1/images/:id - removes image of with id = :id

Available HTML interactions:
1.      GET http://localhost:8080/downloaded/images/:id - shows HTML page with downloaded picture

2.      GET http://localhost:8080/downloaded/images/:id/small - shows HTML page with preview (100px*100px static version) of downloaded picture

Downloaded images and preview versions can be found directly:       
   http://localhost:8080/images/:id - for full image        
   http://localhost:8080/images/:id_small - preview version


Checked supported formats:
        jpg, 
        gif (with static preview), 
        png, 
        bmp.

