package com.fts;

import com.alibaba.fastjson.JSON;
import com.fts.jpa.entity.novel.Novel;
import com.fts.jpa.entity.json.NovelInfo;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Paths;
import java.util.UUID;

public class FTSNovelTest {

    @Test
    public void test() {
        String rootPath = "./novel";
        File rootDir = new File(rootPath);
        String[] subNovelDir = null;
        if (!rootDir.isDirectory()) return;
        subNovelDir = rootDir.list();

        if (subNovelDir.length < 0) return;

        RandomAccessFile raf = null;
        try {
            for (String categoryName : subNovelDir) {
                System.out.println("### 分类: " + categoryName);
                String categoryDirPath = Paths.get(rootPath, categoryName).toString();
                File categoryDir = new File(categoryDirPath);

                if(categoryDir.isDirectory()) {
                    String[] novels = categoryDir.list();
                    if (novels.length < 0) continue;

                    for (String novelName : novels) {
                        System.out.println(novelName);
                        String novelPath = Paths.get(categoryDirPath, novelName).toString();
                        String infoJsonPath = Paths.get(novelPath, "info.json").toString();
                        NovelInfo novelInfo = parseJson(infoJsonPath);

                        Novel.builder()
                                .id(generateUUID())
                                .name(novelName)
                                .category(categoryName)
                                .author(novelInfo.getAuthor().getName())
                                .intro(novelInfo.getIntro())
                                .catalog(novelInfo.getCatalogues())
                                .words(novelInfo.getWords())
                        ;

                        int chapterIndex = 0;
                        String chapterDir = Paths.get(novelPath, chapterIndex + ".html").toString();
                        raf = new RandomAccessFile(chapterDir, "r");
                        long fileSize = raf.length();
                        byte[] buffer = new byte[(int)fileSize];
                        raf.readFully(buffer);
                        String content = new String(buffer);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (null != raf) raf.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }



    }

    private NovelInfo parseJson(String jsonStr) {
        return JSON.parseObject(jsonStr, NovelInfo.class);
    }

    public String generateUUID() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
