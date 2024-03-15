package com.fts;

import com.fts.es.entity.Bank;
import com.fts.es.entity.NovelChapterIndex;
import com.fts.es.entity.NovelDetail;
import com.fts.es.entity.Poetry;
import com.fts.es.repository.BankRepository;
import com.fts.es.repository.NovelChapterRepository;
import com.fts.es.repository.NovelDetailRepository;
import com.fts.es.repository.PoetryRepository;
import com.fts.jpa.entity.novel.AuthorNovel;
import com.fts.jpa.entity.novel.Novel;
import com.fts.jpa.entity.novel.NovelChapter;
import com.fts.jpa.entity.poetry.SongCi;
import com.fts.jpa.entity.poetry.SongShi;
import com.fts.jpa.entity.poetry.TangShi;
import com.fts.jpa.dao.novel.NovelAuthorDao;
import com.fts.jpa.dao.novel.NovelChapterDao;
import com.fts.jpa.dao.novel.NovelDao;
import com.fts.jpa.dao.poetry.SongCiDao;
import com.fts.jpa.dao.poetry.SongShiDao;
import com.fts.jpa.dao.poetry.TangShiDao;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@SpringBootTest
public class FTSESTest {
    @Autowired
    private SongCiDao songCiDao;
    @Autowired
    private TangShiDao tangShiDao;
    @Autowired
    private SongShiDao songShiDao;
    @Autowired
    private NovelDao novelDao;
    @Autowired
    private NovelAuthorDao novelAuthorDao;
    @Autowired
    private NovelChapterDao novelChapterDao;

    @Autowired
    private BankRepository bankRepository;
    @Autowired
    private PoetryRepository poetryRepository;
    @Autowired
    private NovelDetailRepository novelDetailRepository;
    @Autowired
    private NovelChapterRepository novelChapterRepository;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

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
    public void insert_novel() {
        List<Novel> novels = novelDao.findAll();
        // List to Map: https://www.wdbyte.com/java8/java8-list-to-map/
        Map<String, Novel> novelMap = novels.stream().collect(Collectors.toMap(Novel::getId, novel -> novel));

        // get authorIntro/authorDynasty form authorNovelDao
        List<String> authorIds = novels.stream().map(Novel::getAuthorId).toList();
        List<AuthorNovel> novelAuthors = novelAuthorDao.findAllById(authorIds);
        Map<String, AuthorNovel> authorMap = novelAuthors.stream().collect(Collectors.toMap(AuthorNovel::getId, author -> author));

        List<NovelChapter> chapters = novelChapterDao.findAll();
        // Java groupBy: https://www.jianshu.com/p/0687e7003eb2
        // Map<String, List<NovelChapter>> groupedByChapter = chapters.stream().collect(Collectors.groupingBy(NovelChapter::getNovelId));

        List<NovelChapterIndex> novelChapterIndices = new ArrayList<>();
        for (NovelChapter novelChapter : chapters) {
            Novel novel = novelMap.get(novelChapter.getNovelId());
            NovelChapterIndex novelChapterIndex = NovelChapterIndex.builder()
                    .novelId(novel.getId())
                    .authorId(novel.getAuthorId())
                    .catalog(novel.getCatalog())
                    .chapterId(novelChapter.getId())
                    .chapterNo(Integer.valueOf(novelChapter.getChapter()))
                    .content(novelChapter.getContent())
                    .build();
            novelChapterIndices.add(novelChapterIndex);
        }

        // insert novel data from db to es
        List<NovelDetail> novelIndices = new ArrayList<>();
        for (Novel novel : novels) {
            AuthorNovel author = authorMap.get(novel.getAuthorId());
            // populate NovelDetail
            NovelDetail novelDetail = NovelDetail.builder()
                    .novelId(novel.getId())
                    .novelName(novel.getName())
                    .authorId(author.getId())
                    .authorName(author.getName())
                    .authorIntro(author.getIntro())
                    .authorDynasty(author.getDynasty())
                    .novelIntro(novel.getIntro())
                    .catalog(novel.getCatalog())
                    .category(novel.getCategory())
                    .words(novel.getWords())
                    .build();
            novelIndices.add(novelDetail);
        }
        novelDetailRepository.saveAll(novelIndices);

        long startTime = System.currentTimeMillis();

        // 插入 1 // 同样报错 es_rejected_execution_exception
        // elasticsearchTemplate#bulkIndex // 批量插入

        // 插入 2
        batchInsert(novelChapterIndices); // 任务拆分插入

        System.out.println("total time: " + (System.currentTimeMillis() - startTime));
    }

    private void batchInsert(List<NovelChapterIndex> indexList){
        // index to array
        NovelChapterIndex[] indices = new NovelChapterIndex[indexList.size()];
        for (int i = 0; i < indexList.size(); i++) {
            indices[i] = indexList.get(i);
        }

        // task split
        splitInsertES(indices);
    }

    private void splitInsertES(NovelChapterIndex[] indices) {
        int taskCount = indices.length;
        log.info("==> total task count: {}", taskCount);
        // 怎么判断应该分成多少个子任务？应该是可配置的，默认分成 workerCount 个子任务
        int workerCount = 48;
        log.info("==> total worker count: {}", workerCount);
        // 保存需要处理的任务数量
        // 比如存在 taskCount=2501，workerCount=5
        // a=[500,500,500,500,501]
        // 第一个 worker 处理 0 到小于 a[0] ==> 0 ～ 499
        // 第二个 worker 处理 a[1] 到小于 a[2] ==> 500 ～ 999
        // ...
        // 最后一个处理 a[length-1] ~ 小于 taskCount

        int[] taskList = new int[workerCount];
        int preSubTask = taskCount / workerCount; // 初次分配
        Arrays.fill(taskList, preSubTask);
        int lastSubTask = taskCount - preSubTask * (workerCount - 1);  // 初次未完全分配，最后一个子任务承担多一点任务
        log.info("==> pre-sub-task handle: {}, last sub task handle: {}", preSubTask, lastSubTask);
        taskList[taskList.length - 1] = lastSubTask;

        log.info("before task start...");

        ThreadPoolExecutor myThreadPool = new ThreadPoolExecutor(workerCount, workerCount * 2 + 1, 20, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        int lastFinal = 0;
        for (int i = 0; i < taskList.length; i++) {
            int fromIndex = i == 0 ? 0 : lastFinal;
            int finalIndex = fromIndex + taskList[i];

            lastFinal+=taskList[i];

            CompletableFuture<Void> runAsync = CompletableFuture.runAsync(() -> {
                doInsert(indices, fromIndex, finalIndex);
            }, myThreadPool); // 使用自定义的 thread pool
            futures.add(runAsync);
        }

        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        try {
            allOf.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        log.info("all task done...");

    }

    private volatile AtomicInteger insertCount = new AtomicInteger(0);
    private void doInsert(NovelChapterIndex[] indices, int minIndex, int maxIndex) {
        log.info("Thread: {} handle from {} to {}", Thread.currentThread().getName(), minIndex, maxIndex);

        List<IndexQuery> queries = new ArrayList<>();
        List<NovelChapterIndex> indexList = new ArrayList<>(Arrays.asList(indices).subList(minIndex, maxIndex));
        for (NovelChapterIndex novelChapterIndex : indexList) {
            IndexQuery indexQuery = new IndexQuery();
            indexQuery.setObject(novelChapterIndex);
            queries.add(indexQuery);
        }
        elasticsearchTemplate.bulkIndex(queries, NovelChapterIndex.class); // ~ 1.1 minutes
        // novelChapterRepository.saveAll(indexList); // ~ 1.1 minutes
        log.info("insert count: {}", insertCount.addAndGet(indexList.size()));
    }

    @Test
    public void splitTask() {
        int totalSize = 250000;

        int taskCount = 7;
        int[] taskList = new int[taskCount];
        // 怎么判断应该分成多少个子任务？应该是可配置的，默认分成 7 个子任务
        int preSubTask = totalSize / taskCount; // 初次分配
        Arrays.fill(taskList, preSubTask);
        int lastSubTask = totalSize - preSubTask * (taskCount - 1);  // 初次未完全分配，最后一个子任务承担多一点任务
        taskList[taskList.length - 1] = lastSubTask;

        ThreadPoolExecutor myThreadPool = new ThreadPoolExecutor(32, 64, 20, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

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
