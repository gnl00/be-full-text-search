package com.fts;

import com.fts.es.entity.Bank;
import com.fts.es.repository.BankRepository;
import com.fts.es.repository.PoetryRepository;
import com.fts.jpa.entity.SongCi;
import com.fts.jpa.repository.SongCiDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class FTSESTest {
    @Autowired
    private SongCiDao songCiDao;

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
        // poetryRepository.saveAll(); // TODO save to es from db
    }
}
