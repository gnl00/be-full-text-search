package com.fts;

import com.alibaba.fastjson.JSON;
import com.fts.jpa.entity.novel.AuthorNovel;
import com.fts.jpa.entity.novel.Novel;
import com.fts.jpa.entity.json.NovelInfo;
import com.fts.jpa.entity.novel.NovelChapter;
import com.fts.jpa.dao.novel.NovelAuthorDao;
import com.fts.jpa.dao.novel.NovelChapterDao;
import com.fts.jpa.dao.novel.NovelDao;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Paths;
import java.util.*;

@Slf4j
@SpringBootTest
public class FTSNovelTest {

    @Autowired
    private NovelDao novelDao;

    @Autowired
    private NovelChapterDao novelChapterDao;

    @Autowired
    private NovelAuthorDao novelAuthorDao;

    Map<String, String> authorMap = new HashMap<>();

    @Test
    public void insert_novel() {
        String rootPath = "novel";
        File rootDir = new File(rootPath);
        String[] subNovelDir = null;
        if (!rootDir.isDirectory()) return;
        subNovelDir = rootDir.list();

        if (subNovelDir.length < 0) return;

        RandomAccessFile raf = null;
        try {
            Set<AuthorNovel> authorNovelSet = new HashSet<>();
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
                        try {
                            raf = new RandomAccessFile(infoJsonPath, "r");
                        } catch (FileNotFoundException e) {
                            log.info("info file {} not found, skipping", infoJsonPath);
                            continue;
                        }
                        long infoJsonLength = raf.length();
                        byte[] infoBuffer = new byte[(int)infoJsonLength];
                        raf.readFully(infoBuffer);
                        String infoJsonStr = new String(infoBuffer);
                        NovelInfo novelInfo = parseJson(infoJsonStr);
                        raf.close();

                        String authorId = generateUUID();
                        AuthorNovel author = novelInfo.getAuthor();
                        if (!authorMap.containsKey(author.getName())) {
                            authorMap.put(author.getName(), authorId);
                        } else {
                            authorId  = authorMap.get(author.getName());
                        }
                        author.setId(authorId);
                        authorNovelSet.add(author);

                        String novelId = generateUUID();
                        Novel novel = Novel.builder()
                                .id(novelId)
                                .name(novelName)
                                .category(categoryName)
                                .authorId(authorId)
                                .author(author.getName())
                                .intro(novelInfo.getIntro())
                                .catalog(novelInfo.getCatalogues())
                                .words(novelInfo.getWords())
                                .build();

                        List<NovelChapter> novelChapters = new ArrayList<>();
                        int chapterIndex = 0;
                        while (true) {
                            String chapterDir = Paths.get(novelPath, chapterIndex + ".html").toString();
                            try {
                                raf = new RandomAccessFile(chapterDir, "r");
                            } catch (FileNotFoundException e) {
                                log.error("file {} not found!", chapterDir);
                                break;
                            }
                            long fileSize = raf.length();
                            byte[] buffer = new byte[(int)fileSize];
                            raf.readFully(buffer);
                            String chapterContent = new String(buffer);

                            NovelChapter novelChapter = NovelChapter.builder()
                                    .id(generateUUID())
                                    .novelId(novelId)
                                    .novelName(novelName)
                                    .chapter(chapterIndex + "")
                                    .content(chapterContent)
                                    .build();

                            novelChapters.add(novelChapter);
                            chapterIndex++;
                        }

                        novelDao.save(novel);
                        novelChapterDao.saveAll(novelChapters);
                    }
                }
            }
            novelAuthorDao.saveAll(authorNovelSet);
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
