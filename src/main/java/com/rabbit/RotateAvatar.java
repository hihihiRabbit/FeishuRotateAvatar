package com.rabbit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;

/**
 * @author liuyunsong3
 */
@Slf4j
public class RotateAvatar {
    static int counter = 0;
    // 原始头像路径
    private static final String AVATAR_PATH = "./src/main/resources/头像.jpg";

    public static void main(String[] args) {
        long timestamp = System.currentTimeMillis();
        Date date = new Date(timestamp);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        String formatted = format.format(date);

        log.info("===========项目启动===========");
        log.info("北京时间："+formatted);

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        Runnable task = () -> {
            try {
                BufferedImage avatar = ImageIO.read(new File(AVATAR_PATH));
                BufferedImage rotatedAvatar = rotateImageByTime(avatar);
                File tempFile = File.createTempFile("rotated_avatar", ".jpg");
                ImageIO.write(rotatedAvatar, "jpg", tempFile);
                uploadAvatar(tempFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        scheduler.scheduleAtFixedRate(task, 0, 60, TimeUnit.SECONDS);

        // 防止主线程退出
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static BufferedImage rotateImageByTime(BufferedImage image) {
        LocalTime now = LocalTime.now();
        int minutes = now.getHour() * 60 + now.getMinute();
        // 12小时720分钟，计算旋转角度
        double angle = (minutes / 720.0) * 360;

        return rotateImage(image, angle);
    }

    private static BufferedImage rotateImage(BufferedImage image, double angle) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage rotatedImage = new BufferedImage(width, height, image.getType());
        Graphics2D g2d = rotatedImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        AffineTransform transform = new AffineTransform();
        transform.rotate(Math.toRadians(angle), width / 2.0, height / 2.0);
        g2d.drawImage(image, transform, null);
        g2d.dispose();
        return rotatedImage;
    }

    private static void uploadAvatar(File file) throws IOException {
        String cookie = "passport_web_did=7403703276660588653; QXV0aHpDb250ZXh0=76a5a4eea6a544a99d837b6b42daea09; _gcl_au=1.1.699461459.1723809000; locale=zh-CN; _gid=GA1.2.1394885613.1723806890; __tea__ug__uid=clzwnfl5800002v76futqifnw; _ga=GA1.1.567402225.1723806890; landing_url=https://login.f.mioffice.cn/accounts/page/login?app_id=2&query_scope=all&redirect_uri=https%3A%2F%2Fxiaomi.f.mioffice.cn%2Fdocx%2Fdoxk4J91R6tuWI6eK0WdOXiIjry%3Flogin_redirect_times%3D1; session=XN0YXJ0-cbfqaaa7-9bf0-4916-a27d-f8b006705m7n-WVuZA; session_list=XN0YXJ0-cbfqaaa7-9bf0-4916-a27d-f8b006705m7n-WVuZA; is_anonymous_session=; lang=zh; _csrf_token=04fde520f39574eb4f2fade7fd0fa9f4d7b7c987-1723809318; _ga_VPYRHN104D=GS1.1.1723809000.1.1.1723810730.60.0.0; sl_session=eyJhbGciOiJFUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE3MjM4NTUyMzYsInVuaXQiOiJrYTRsYXJrIiwicmF3Ijp7Im1ldGEiOiJBV1o2RmpjSWdBQnRYWU9YTENsQUFRdG12enpuVThBQWJXYS9QT2RUd0FCdFpyOCtJdFlBQUd3Q0tnRUFRVUZCUVVGQlFVRkJRVlp0ZG5vMGFUWk5SVUZpUVQwOSIsInN1bSI6Ijg2ZTgwYTVhMTE5OWE3NzRhZTZjZWQzZDFmMTExM2NiOTEzM2FkZDI5YWZmZmMyNWQyMzJmMDFhNDQwNTBkZTUiLCJsb2MiOiJ6aF9jbiIsImFwYyI6ImthbWkiLCJpYXQiOjE3MjM4MTIwMzYsInNhYyI6eyJVc2VyU3RhZmZTdGF0dXMiOiIxIn0sImxvZCI6bnVsbCwibnMiOiJsYXJrIiwibnNfdWlkIjoiNzM4NDIzODk2NDY0ODk2ODMwMSIsIm5zX3RpZCI6IjY3MzgzOTU2ODM0MDI4MDk2MTEiLCJvdCI6Mn19.jgkXpwgH1MPP_1vM8CRxxeMs1hKqKBeoRhifVliYaOBUpZaDop_Fh4_uca52EnlznNcOL8ucbJ4cqYmUpRfVJQ; swp_csrf_token=bc4f638f-56ea-4d6e-b37b-5aa71d352df7; t_beda37=4dfe2c8ef97da84515ec1cb400ca20c7aff619864d02ff65008cd10bdcec9e97";

        OkHttpClient client = new OkHttpClient().newBuilder().build();

        // 构建MultipartBody
        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("avatar", file.getName(),
                        RequestBody.create(MediaType.parse("application/octet-stream"), file))
                .build();


        // 构建请求
        Request request = new Request.Builder()
                .url("https://internal-api-lark-api.f.mioffice.cn/accounts/web/avatar?_t=172380")
                .method("POST", requestBody)
                .addHeader("Cookie", cookie)
                .addHeader("X-App-Id", "12")
                .addHeader("X-Api-Version", "1.0.18")
                .addHeader("X-Device-Info", "platform=websdk")
//                .addHeader("X-Csrf-Token", "3c0a8fa9-c6bd-4d6e-b19d-a6d646042c78")
                .addHeader("Host", "internal-api-lark-api.f.mioffice.cn")
                .addHeader("Origin", "https://xiaomi.f.mioffice.cn")
                .addHeader("Referer", "https://xiaomi.f.mioffice.cn/")
                .addHeader("Sec-Ch-Ua", "\"Not/A)Brand\";v=\"8\", \"Chromium\";v=\"126\", \"Google Chrome\";v=\"126\"")
                .build();

        // 发送请求并获取响应
        Response response = client.newCall(request).execute();
        String responseBody = response.body().string();
        long timestamp = System.currentTimeMillis();
        Date date = new Date(timestamp);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        String formatted = format.format(date);

        log.info("执行：{}\n北京时间：{}\n响应码：{}\n响应体：{}",
                counter++,
                formatted,
                response.code(),
                responseBody);
    }
}