package com.doan.controller;

import com.doan.DTO.ChapterDTO;
import com.doan.DTO.PasswordDTO;
import com.doan.DTO.RegisterDTO;
import com.doan.entity.Story;
import com.doan.entity.User;
import com.doan.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.security.Principal;

@Controller
public class HomeApi {
    private final HomeService homeService;

    @Autowired
    public HomeApi(HomeService homeService) {
        this.homeService = homeService;
    }

    @GetMapping(value = {"/", "/trang-chu"})
    public ModelAndView trangchu(ModelMap model, Principal principal) {
        return homeService.trangchu(model, principal);
    }

    @GetMapping("/login")
    public ModelAndView login() {
        return new ModelAndView("login");
    }

    @GetMapping("/register")
    public ModelAndView register(ModelMap model, @Valid RegisterDTO registerDTO) {
        return new ModelAndView("register");
    }

    @PostMapping("/register")
    public ModelAndView registerForm(ModelMap model, @Valid RegisterDTO registerDTO, BindingResult result) {
        if (result.hasErrors()) {
            return new ModelAndView("register");
        }
       return homeService.postRegister(model, registerDTO);
    }

    @GetMapping("/add-story")
    public ModelAndView addStory(ModelMap model, @Valid Story story, Principal principal) {
        return homeService.getAddStory(model, story, principal);
    }

    @PostMapping("/add-story")
    public ModelAndView postAddStory(@RequestPart("file") MultipartFile file, ModelMap model, @Valid Story story, Principal principal) {
        return homeService.postAddStory(file, model, story, principal);
    }

    @GetMapping("/403")
    public ModelAndView errorpage() {
        return new ModelAndView("403");
    }

    @GetMapping("/story-detail")
    public ModelAndView storyDetail(@RequestParam(value = "id", required = false) Integer id, @RequestParam(value = "error", required = false) String error, ModelMap model, Principal principal){
        return homeService.getStoryDetail(id, error, model, principal);
    }

    @GetMapping("/search")
    public ModelAndView search(@RequestParam("name") String name, ModelMap model, Principal principal) {
        return homeService.getSearch(name, model, principal);
    }

    @GetMapping("/add-chapter")
    public ModelAndView addChapter(Principal principal, ModelMap model, @RequestParam(value = "id", required = false) Integer storyId) {
        return homeService.getAddChapter(principal, model, storyId);
    }

    @PostMapping("/add-chapter")
    public ModelAndView saveChapter(Principal principal,@ModelAttribute("chapterDTO") ChapterDTO chapterDTO){
        return homeService.postAddChapter(chapterDTO);
    }

    @GetMapping("/payment")
    public ModelAndView payment(@RequestParam("chapter_id") Integer chapterId, Principal principal, ModelMap model) {
        return homeService.payment(model, chapterId, principal);
    }

    @GetMapping("/chapter-detail")
    public ModelAndView getStoryDetail(@RequestParam("id") Integer id, @RequestParam(value = "error", required = false) String error, Principal principal, ModelMap modelMap ) {
        return homeService.getChapter(id, error, principal, modelMap);
    }

    @GetMapping("/update-story")
    public ModelAndView updateStory(ModelMap model, @Valid Story story, Principal principal, @RequestParam("id") Integer storyId) {
        return homeService.getUpdateStory(model, story, principal, storyId);
    }

    @PostMapping("/update-story")
    public ModelAndView postUpdateStory(@RequestPart(value = "file", required = false) MultipartFile file, ModelMap model, @Valid Story story, Principal principal) {
        return homeService.postUpdateStory(file, model, story, principal);
    }

    @PostMapping("/update-chapter")
    public ModelAndView postUpdateStory(ModelMap model, Principal principal, @ModelAttribute("chapterDTO") ChapterDTO chapterDTO) {
        return homeService.postUpdateChapter(model, principal, chapterDTO);
    }

    @GetMapping("/update-chapter")
    public ModelAndView updateChapter(ModelMap model, Principal principal, @RequestParam("id") Integer chapterId) {
        return homeService.getUpdateChapter(model,  principal, chapterId);
    }

    @GetMapping("/update-chapter-draft")
    public ModelAndView getUpdateChapterDraft(ModelMap model, Principal principal, @RequestParam("id") Integer chapterId) {
        return homeService.getUpdateChapterDraft(model,  principal, chapterId);
    }
    @PostMapping("/update-chapter-draft")
    public ModelAndView updateChapterDraft(ModelMap model, Principal principal, @ModelAttribute("chapterDTO") ChapterDTO chapterDTO) {
        return homeService.postUpdateChapterDraft(model,  principal, chapterDTO);
    }
    @GetMapping("/category")
    public ModelAndView getCategory(ModelMap modelMap, Principal principal, @RequestParam("id") Integer id) {
        return homeService.getCategory(modelMap,principal,id);
    }
    @GetMapping("/user")
    public ModelAndView getUser(ModelMap modelMap, Principal principal, @RequestParam(value = "error-code", required = false) String errorCode) {
        return homeService.getUser(modelMap, principal, errorCode);
    }
    @PostMapping("/update-user-info")
    public ModelAndView updateUserInfo(@RequestPart("file") MultipartFile file, ModelMap modelMap, Principal principal, @ModelAttribute("user") User user) {
        return  homeService.updateUserInfo(file, modelMap, principal, user);
    }

    @GetMapping("/topup-info")
    public ModelAndView topupInfo(ModelMap modelMap, Principal principal) {
        return homeService.topupInfo(modelMap, principal);
    }

    @GetMapping("/top-new")
    public ModelAndView topNew(ModelMap modelMap, Principal principal) {
        return homeService.topNew(modelMap, principal);
    }
    @GetMapping("/top-view")
    public ModelAndView topView(ModelMap modelMap, Principal principal) {
        return homeService.topView(modelMap, principal);
    }

    @PostMapping("/update-password")
    public ModelAndView updatePassword(ModelMap modelMap, Principal principal, @ModelAttribute("passwordDTO") PasswordDTO passwordDTO) {
        return homeService.updatePassword(modelMap, principal, passwordDTO);
    }

    @GetMapping("/chapter-draft/status")
    public ModelAndView setStatusChapterDraft(@RequestParam(value = "story_id") Integer storyId, @RequestParam(value = "chapter_id") Integer chapterId, @RequestParam("code") String code ) {
        return homeService.setStatusChapterDraft(storyId, chapterId, code);
    }
}
