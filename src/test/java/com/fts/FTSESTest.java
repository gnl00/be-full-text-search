package com.fts;

import com.fts.es.entity.Bank;
import com.fts.es.entity.Poetry;
import com.fts.es.repository.BankRepository;
import com.fts.es.repository.PoetryRepository;
import com.fts.jpa.entity.poetry.SongCi;
import com.fts.jpa.entity.poetry.SongShi;
import com.fts.jpa.entity.poetry.TangShi;
import com.fts.jpa.repository.poetry.SongCiDao;
import com.fts.jpa.repository.poetry.SongShiDao;
import com.fts.jpa.repository.poetry.TangShiDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

@SpringBootTest
public class FTSESTest {
    @Autowired
    private SongCiDao songCiDao;

    @Autowired
    private TangShiDao tangShiDao;

    @Autowired
    private SongShiDao songShiDao;

    @Autowired
    private BankRepository bankRepository;
    @Autowired
    private PoetryRepository poetryRepository;

    @Test
    public void test() {
        Iterable<Bank> accounts = bankRepository.findAll();
        System.out.println(accounts);
    }

    @Test
    public void insert_songci() {
        List<SongCi> songCis = songCiDao.findAll();
        List<Poetry> poetryList = new ArrayList<>();
        songCis.forEach(sc -> {
            Poetry poetry = Poetry.builder()
                    .title(sc.getRhythmic())
                    .author(sc.getAuthor())
                    .paragraphs(sc.getParagraphs())
                    .category("宋词")
                    .build();
            poetryList.add(poetry);
        });
        poetryRepository.saveAll(poetryList);
    }

    @Test
    public void insert_tangshi() {
        List<TangShi> tangShis = tangShiDao.findAll();
        List<Poetry> poetryList = new ArrayList<>();
        tangShis.forEach(shi -> {
            Poetry poetry = Poetry.builder()
                    .title(shi.getTitle())
                    .author(shi.getAuthor())
                    .paragraphs(shi.getParagraphs())
                    .category("唐诗")
                    .build();
            poetryList.add(poetry);
        });
        poetryRepository.saveAll(poetryList);
    }

    @Test
    public void insert_songshi() {
        List<SongShi> songShis = songShiDao.findAll();
        List<Poetry> poetryList = new ArrayList<>();
        // task split
        songShis.forEach(shi -> {
            Poetry poetry = Poetry.builder()
                    .title(shi.getTitle())
                    .author(shi.getAuthor())
                    .paragraphs(shi.getParagraphs())
                    .category("宋诗")
                    .build();
            poetryList.add(poetry);
        });
        poetryRepository.saveAll(poetryList);
    }

    @Test
    public void splitTask() {
        int totalSize = 250000;

        int taskCount = 7;
        int[] taskList = new int[7];
        // 怎么判断应该分成多少个子任务？应该是可配置的，默认分成 7 个子任务
        int preSubTask = totalSize / taskCount; // 初次分配
        Arrays.fill(taskList, preSubTask);
        int lastSubTask = totalSize - preSubTask * (taskCount - 1);  // 初次未完全分配，最后一个子任务承担多一点任务
        taskList[taskList.length - 1] = lastSubTask;

        ThreadPoolExecutor myThreadPool = new ThreadPoolExecutor(32, 64, 15, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (int i = 0; i < taskCount; i++) {
            final int index = i;
            CompletableFuture<Void> runAsync = CompletableFuture.runAsync(() -> {
                doSth(taskList[index]);
            }, myThreadPool);
            futures.add(runAsync);
        }
        System.out.println("before all of");
        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        try {
            allOf.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        System.out.println("after all of");
    }

    // assume that here is the real task
    private void doSth(int param) {
        try {
            int sleepTime = (int) (Math.random() * 10);
            System.out.println("sleep time: " + sleepTime);
            TimeUnit.SECONDS.sleep(sleepTime);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(param);
        System.out.println(Thread.currentThread().getName());
    }
}
