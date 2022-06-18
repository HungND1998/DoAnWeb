package com.doan.controller;

import cn.hutool.crypto.Mode;
import com.doan.DTO.AddMoneyDTO;
import com.doan.DTO.AuthorConfigDTO;
import com.doan.DTO.ChapterDTO;
import com.doan.DTO.SearchPaymentDTO;
import com.doan.entity.Category;
import com.doan.entity.Slide;
import com.doan.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;

@Controller
@RequestMapping("/admin")
public class AdminApi {
    private final AdminService adminService;

    @Autowired
    public AdminApi(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping(value = {"/dashboard", "/"})
    public ModelAndView dashboard(ModelMap modelMap, Principal principal) {
        return adminService.dashboard(modelMap, principal);
    }
    @GetMapping("/category")
    public ModelAndView category(ModelMap modelMap, Principal principal) {
        return adminService.category(modelMap, principal);
    }

    @GetMapping("/add-category")
    public ModelAndView getAddCategory(ModelMap modelMap, Principal principal) {
        return adminService.getAddCategory(modelMap, principal);
    }

    @PostMapping("/add-category")
    public ModelAndView postaddCategory(@ModelAttribute("categoryDTO") Category category,  ModelMap modelMap, Principal principal) {
        return adminService.postAddCategory(category, modelMap, principal);
    }

    @GetMapping("/update-category")
    public ModelAndView getUpdateCategory(@RequestParam("id") Integer id, ModelMap modelMap, Principal principal) {
        return adminService.getUpdateCategory(id, modelMap, principal);
    }

    @PostMapping("/update-category")
    public ModelAndView postUpdateCategory(@ModelAttribute("categoryDTO") Category category,  ModelMap modelMap, Principal principal) {
        return adminService.postUpdateCategory(category, modelMap, principal);
    }

    @GetMapping("/category/status")
    public ModelAndView categoryStatus(@RequestParam("id") Integer id, @RequestParam("status") boolean status, ModelMap modelMap, Principal principal) {
        return adminService.categoryStatus(id, status, modelMap, principal);
    }

    @GetMapping("/payment")
    public ModelAndView payment(ModelMap modelMap, Principal principal) {
        return adminService.getPayment(modelMap, principal);
    }
    @PostMapping("/payment/search")
    public ModelAndView paymentSearch(ModelMap modelMap, Principal principal, @ModelAttribute("searchPaymentDTO") SearchPaymentDTO searchPaymentDTO) {
       return adminService.postPaymentSearch(modelMap, principal, searchPaymentDTO);
    }

    @GetMapping("/payment/topup")
    public ModelAndView getAddMoney(@RequestParam(value = "error", required = false) String error, ModelMap modelMap, Principal principal) {
        return adminService.getTopup(error, modelMap, principal);
    }
    @PostMapping("/payment/topup")
    public ModelAndView postAddMoney(ModelMap modelMap, Principal principal, @ModelAttribute("addMoneyDTO") AddMoneyDTO addMoneyDTO) {
        return adminService.postTopup(modelMap, principal, addMoneyDTO);
    }
    @GetMapping("/payment/show-topup")
    public ModelAndView show_topup(ModelMap modelMap, Principal principal) {
        return adminService.showTopup(modelMap, principal);
    }

    @PostMapping("/payment/search-topup")
    public ModelAndView searchTopup(ModelMap modelMap, Principal principal, @ModelAttribute("searchPaymentDTO") SearchPaymentDTO searchPaymentDTO) {
        return adminService.postTopupSearch(modelMap, principal, searchPaymentDTO);
    }
    @GetMapping("/slide")
    public ModelAndView getSlide(ModelMap modelMap, Principal principal) {
        return adminService.getSlider(principal, modelMap);
    }

    @PostMapping("/slide")
    public ModelAndView postSlide(ModelMap modelMap, Principal principal, @ModelAttribute("slide") Slide slide, @RequestPart("file") MultipartFile file) {
        System.out.println(slide);
        return adminService.postSlide(file, slide.getId(), slide.getStoryId());
    }

    @PostMapping("/add-slide")
    public ModelAndView postAddSlide(ModelMap modelMap, Principal principal, @ModelAttribute("slide") Slide slide, @RequestPart("file") MultipartFile file) {
        System.out.println(slide);
        return adminService.postAddSlide(file, slide.getStoryId(), slide.getStoryId());
    }

    @GetMapping("/story")
    public ModelAndView getStory(ModelMap modelMap, Principal principal) {
        return adminService.getStory(modelMap, principal);
    }

    @GetMapping("/story/status")
    private ModelAndView setStoryStatus(@RequestParam("story_id") Integer id ,@RequestParam("code") boolean status) {
        return adminService.setStoryStatus(id, status);
    }

    @GetMapping("/chapter")
    public ModelAndView getStory(@RequestParam("story_id") Integer id, ModelMap modelMap, Principal principal) {
        return adminService.getChapterByStoryId(id, modelMap, principal);
    }


    @GetMapping("/chapter/status")
    public ModelAndView setChapterStatus(@RequestParam("story_id") Integer storyId, @RequestParam("chapter_id") Integer chapterId ,@RequestParam("code") boolean status) {
        return adminService.setChapterStatus(storyId, chapterId, status);
    }

    @GetMapping("/user")
    public ModelAndView getUser(Principal principal,ModelMap modelMap) {
        return adminService.getAllUser(principal, modelMap);
    }
    @GetMapping("/user/status")
    public ModelAndView setStatusUser( @RequestParam("user_id") Integer userId ,@RequestParam("code") boolean status) {
        return adminService.setStatusUser(userId, status);
    }

    @GetMapping("/chapter-draft/status")
    public ModelAndView setStatusChapterDraft(@RequestParam(value = "story_id") Integer storyId, @RequestParam(value = "chapter_id") Integer chapterId, @RequestParam("code") String code ) {
        return adminService.setStatusChapterDraft(storyId, chapterId, code);
    }

    @GetMapping("/author/config")
    public ModelAndView configAuthor(@RequestParam(value = "id") Integer id, ModelMap modelMap, Principal principal) {
        return adminService.getAuthorConfig(id, modelMap, principal);
    }
    @PostMapping("/author/config")
    public ModelAndView postAuthorConfig(ModelMap modelMap, Principal principal, @ModelAttribute("authorConfigDTO") AuthorConfigDTO authorConfigDTO) {
        return adminService.postAuthorConfig(authorConfigDTO, modelMap, principal);
    }


}
