package com.doan.service;

import com.doan.DTO.ChapterDTO;
import com.doan.DTO.PasswordDTO;
import com.doan.DTO.RegisterDTO;
import com.doan.entity.Story;
import com.doan.entity.User;
import org.springframework.ui.ModelMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;

public interface HomeService {
    ModelAndView payment(ModelMap model, Integer chapterId, Principal principal);

    ModelAndView postAddChapter(ChapterDTO chapterDTO);

    ModelAndView getAddChapter(Principal principal, ModelMap model, Integer storyID);

    ModelAndView trangchu(ModelMap model, Principal principal);

    ModelAndView postRegister(ModelMap model, RegisterDTO registerDTO);

    ModelAndView getAddStory(ModelMap model, Story story, Principal principal);

    ModelAndView postAddStory(MultipartFile file, ModelMap model, Story story, Principal principal);

    ModelAndView getStoryDetail(Integer id, String error, ModelMap model, Principal principal);

    ModelAndView getSearch(String name, ModelMap model, Principal principal);

    ModelAndView topNew(ModelMap modelMap, Principal principal);

    ModelAndView topView(ModelMap modelMap, Principal principal);

    ModelAndView getChapter(Integer id, String error, Principal principal, ModelMap modelMap);

    ModelAndView getUpdateStory(ModelMap model, Story story, Principal principal, Integer id);

    ModelAndView postUpdateStory(MultipartFile file, ModelMap model, Story story, Principal principal);

    ModelAndView getUpdateChapter(ModelMap modelMap, Principal principal, Integer chapterId);

    ModelAndView postUpdateChapter(ModelMap modelMap, Principal principal, ChapterDTO chapterDTO);

    ModelAndView getCategory(ModelMap modelMap, Principal principal, Integer id);

    ModelAndView getUser(ModelMap modelMap, Principal principal, String errorCode);

    ModelAndView updateUserInfo(MultipartFile file, ModelMap modelMap, Principal principal, User user);

    ModelAndView topupInfo(ModelMap modelMap, Principal principal);

    ModelAndView updatePassword(ModelMap modelMap, Principal principal, PasswordDTO passwordDTO);

    ModelAndView setStatusChapterDraft(Integer storyId, Integer chapterId, String code);

    ModelAndView getUpdateChapterDraft(ModelMap model, Principal principal, Integer id);

    ModelAndView postUpdateChapterDraft(ModelMap model, Principal principal, ChapterDTO chapterDTO);
}
