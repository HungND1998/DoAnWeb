package com.doan.service.impl;

import com.doan.entity.Chapter;
import com.doan.entity.ChapterDraft;
import com.doan.repository.ChapterDraftRepository;
import com.doan.repository.ChapterRepository;
import com.doan.utils.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class ScheduleService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleService.class);
    private final ChapterRepository chapterRepository;
    private final ChapterDraftRepository chapterDraftRepository;

    @Autowired
    public ScheduleService(ChapterRepository chapterRepository, ChapterDraftRepository chapterDraftRepository) {
        this.chapterRepository = chapterRepository;
        this.chapterDraftRepository = chapterDraftRepository;
    }

    @Scheduled(cron = "0 * * ? * *")
    private void schedule() {
        LOGGER.info("===START Schedule====");
        List<ChapterDraft> list = chapterDraftRepository.findByChapterDraftPending();
        for(ChapterDraft chapterDraft : list) {
            LocalDate time = chapterDraft.getTime().toLocalDate();

            LocalDate timeNow = LocalDate.now();
            LOGGER.info("===Time chapter draft {} and time now {}====", time, timeNow);
            if(time.equals(timeNow)) {
                Chapter chapter = new Chapter();
                chapter.setChapterName(chapterDraft.getChapterName());
                chapter.setContent(chapterDraft.getContent());
                chapter.setPrice(chapterDraft.getPrice());
                chapter.setStory(chapterDraft.getStory());
                chapter.setCreateAt(LocalDateTime.now());
                chapter.setStatus(true);
                chapterRepository.save(chapter);
                chapterDraft.setStatus(Constant.Status.ACTIVE);
                chapterDraftRepository.save(chapterDraft);
            }

        }
    }
}
