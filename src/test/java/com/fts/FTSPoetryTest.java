package com.fts;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.fts.jpa.entity.json.AuthorShi;
import com.fts.jpa.entity.json.YuanQuJson;
import com.fts.jpa.entity.poetry.*;
import com.fts.jpa.repository.poetry.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.util.*;

@SpringBootTest
@Slf4j
public class FTSPoetryTest {

    @Autowired
    private TangShiDao tangShiDao;

    @Autowired
    private SongShiDao songShiDao;

    @Autowired
    private SongCiDao songCiDao;

    @Autowired
    private PoetryAuthorDao poetryAuthorDao;

    @Autowired
    private YuanQuDao yuanQuDao;

    @Test
    public void test() {
        int index = 0;
        while (true) {
            if (index > 20000) break;
            System.out.println("current index: " + index);
            index += 1000;
        }
    }

    @Test
    public void insert_songci() {
        String filePrefix = "poetry/宋词/ci.song.";
        String fileSuffix = ".json";
        int fileIndex = 0;

        long start = System.currentTimeMillis();
        while (true) {
            String filename = filePrefix + fileIndex + fileSuffix;
            try {
                String jsonStr = read1(filename);
                System.out.println("successfully read: " + filename);
                JSONArray jsonArray = JSON.parseArray(jsonStr);
                List<SongCi> songCis = jsonArray.toJavaList(SongCi.class);
                String[] uuids = generateUUIDs(songCis.size());
                for (int i = 0; i < songCis.size(); i++) {
                    SongCi songCi = songCis.get(i);
                    songCi.setId(uuids[i]);
                    songCi.setParagraphs(songCi.getParagraphs().substring(1, songCi.getParagraphs().length() - 1));
                }
                songCiDao.saveAll(songCis);
                log.info("save {} to db success", filename);
            } catch (FileNotFoundException e) {
                log.error("file: {} not found!, error: {}", filename, e.getClass().getName());
                break;
            } catch (IOException e) {
                log.error("file: {} read failed!, error: {}", filename, e.getClass().getName());
                break;
            }
            fileIndex += 1000;
        }
        System.out.println("total execute time: " + (System.currentTimeMillis() - start));
    }

    @Test
    public void insert_tangshi() {
        String filePrefix = "poetry/全唐诗/poet.tang.";
        String fileSuffix = ".json";
        int fileIndex = 0;

        long start = System.currentTimeMillis();
        while (true) {
            String filename = filePrefix + fileIndex + fileSuffix;
            try {
                String jsonStr = read1(filename);
                System.out.println("successfully read: " + filename);
                JSONArray jsonArray = JSON.parseArray(jsonStr);
                List<TangShi> tangShis = jsonArray.toJavaList(TangShi.class);
                for (TangShi tangShi : tangShis) {
                    tangShi.setId(tangShi.getId().substring(0, 8));
                    tangShi.setParagraphs(tangShi.getParagraphs().substring(1, tangShi.getParagraphs().length() - 1));
                }
                tangShiDao.saveAll(tangShis);
                log.info("save {} to db success", filename);
            } catch (FileNotFoundException e) {
                log.error("file: {} not found!, error: {}", filename, e.getClass().getName());
                break;
            } catch (IOException e) {
                log.error("file: {} read failed!, error: {}", filename, e.getClass().getName());
                break;
            }
            fileIndex += 1000;
        }
        System.out.println("total execute time: " + (System.currentTimeMillis() - start));
    }

    @Test
    public void insert_songshi() {
        String filePrefix = "poetry/全唐诗/poet.song.";
        String fileSuffix = ".json";
        int fileIndex = 0;

        long start = System.currentTimeMillis();
        while (true) {
            String filename = filePrefix + fileIndex + fileSuffix;
            try {
                String jsonStr = read1(filename);
                System.out.println("successfully read: " + filename);
                JSONArray jsonArray = JSON.parseArray(jsonStr);
                List<SongShi> songShis = jsonArray.toJavaList(SongShi.class);
                for (SongShi songShi : songShis) {
                    songShi.setId(songShi.getId().substring(0, 8));
                    songShi.setParagraphs(songShi.getParagraphs().substring(1, songShi.getParagraphs().length() - 1));
                }
                songShiDao.saveAll(songShis);
                log.info("save {} to db success", filename);
            } catch (FileNotFoundException e) {
                log.error("file: {} not found!, error: {}", filename, e.getClass().getName());
                break;
            } catch (IOException e) {
                log.error("file: {} read failed!, error: {}", filename, e.getClass().getName());
                break;
            }
            fileIndex += 1000;
        }
        System.out.println("total execute time: " + (System.currentTimeMillis() - start));
    }

    @Test
    public void insert_qu() {
        String filename = "poetry/元曲/yuanqu.json";
        long start = System.currentTimeMillis();
        try {
            String jsonStr = read1(filename);
            System.out.println("successfully read: " + filename);
            JSONArray jsonArray = JSON.parseArray(jsonStr);
            List<YuanQuJson> yuanQuJsonList = jsonArray.toJavaList(YuanQuJson.class);
            String[] uuids = generateUUIDs(yuanQuJsonList.size());

            List<YuanQu> yuanQus = new ArrayList<>();
            Set<Author> authors = new HashSet<>();

            for (int i = 0; i < yuanQuJsonList.size(); i++) {
                YuanQuJson yqJson = yuanQuJsonList.get(i);
                YuanQu yuanQu = new YuanQu();
                yuanQu.setId(uuids[i]);
                yuanQu.setTitle(yqJson.getTitle());
                yuanQu.setAuthor(yqJson.getAuthor());
                yuanQu.setParagraphs(yqJson.getParagraphs().substring(1, yqJson.getParagraphs().length() - 1));
                yuanQus.add(yuanQu);

                Author author = new Author();
                author.setId(uuids[i]);
                author.setName(yqJson.getAuthor());
                author.setDynasty("元");
                authors.add(author);
            }
            yuanQuDao.saveAll(yuanQus);
            poetryAuthorDao.saveAll(authors);
            log.info("save {} to db success", filename);
        } catch (FileNotFoundException e) {
            log.error("file: {} not found!, error: {}", filename, e.getClass().getName());
        } catch (IOException e) {
            log.error("file: {} read failed!, error: {}", filename, e.getClass().getName());
        }
        System.out.println("total execute time: " + (System.currentTimeMillis() - start));
    }

    @Test
    public void insert_shi_author() {
        String filename = "poetry/全唐诗/authors.song.json";
        try {
            String jsonStr = read1(filename);
            System.out.println("successfully read: " + filename);
            JSONArray jsonArray = JSON.parseArray(jsonStr);
            List<AuthorShi> authorShis = jsonArray.toJavaList(AuthorShi.class);
            List<Author> authors = new ArrayList<>();
            for (AuthorShi as : authorShis) {
                Author author = new Author();
                author.setId(as.getId().substring(0, 8));
                author.setName(as.getName());
                author.setDynasty("宋");
                author.setDescription(as.getDesc());
                authors.add(author);
            }
            poetryAuthorDao.saveAll(authors);
            log.info("save {} to db success", filename);
        } catch (FileNotFoundException e) {
            log.error("file: {} not found!, error: {}", filename, e.getClass().getName());
        } catch (IOException e) {
            log.error("file: {} read failed!, error: {}", filename, e.getClass().getName());
        }


    }

    // RandomAccessFile
    public String read1(String filename) throws IOException {
        StringBuilder jsonStr = new StringBuilder();
        try (RandomAccessFile raf = new RandomAccessFile(filename, "r")) {
            long length = raf.length();
            byte[] buffer = new byte[(int) length];
            // TODO If file size is too large, read in delimited chunks
            if (length > 0) {
                raf.readFully(buffer);
                jsonStr.append(new String(buffer));
            }
        }
        return jsonStr.toString();
    }

    // BufferedReader read line by line
    public String read2(String filename) throws IOException {
        StringBuilder jsonStr = new StringBuilder();
        try (   FileReader fileReader = new FileReader(filename);
                BufferedReader bufferedReader = new BufferedReader(fileReader)
        ) {
            String line = null;
//            boolean leftBracketExist = false, rightBracketExist = false;
            while (null != (line = bufferedReader.readLine())) {
                jsonStr.append(line);
//                if (line.contains("{")) {
//                    leftBracketExist = true;
//                }
//                if (leftBracketExist) {
//                    jsonStr.append(line);
//                }
//
//                if (line.contains("}")) {
//                    leftBracketExist = false;
//                }
            }
        }
        return jsonStr.toString();
    }

    public String[] generateUUIDs(int count) {
        String[] uuids = new String[count];
        for (int i = 0; i < count; i++) {
            uuids[i] = UUID.randomUUID().toString().substring(0, 8);
        }
        return uuids;
    }
}
