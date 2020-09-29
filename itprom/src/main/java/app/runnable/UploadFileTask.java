package app.runnable;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;

import java.io.File;
import java.io.IOException;

public class UploadFileTask implements Runnable {
    final private CloseableHttpClient client;
    final private String url;
    final private File file;

    public UploadFileTask(CloseableHttpClient client, String url, File file) {
        this.client = client;
        this.url = url;
        this.file = file;
    }

    // standard constructors
    public void run() {
        try {
            HttpPost httpPost = new HttpPost(url);

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addBinaryBody("screen", file, ContentType.APPLICATION_OCTET_STREAM, file.getName());
            HttpEntity multipart = builder.build();
            httpPost.setEntity(multipart);

            CloseableHttpResponse response = client.execute(httpPost);
        } catch (IOException ioException) {
//            ioException.printStackTrace();
        }
    }
}
