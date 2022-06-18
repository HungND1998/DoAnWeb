package com.doan.service;

import com.doan.DTO.AddMoneyDTO;
import com.doan.DTO.AuthorConfigDTO;
import com.doan.DTO.SearchPaymentDTO;
import com.doan.entity.Category;
import org.springframework.ui.ModelMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import java.security.Principal;

public interface AdminService {

    ModelAndView dashboard(ModelMap modelMap, Principal principal);


    ModelAndView category(ModelMap modelMap, Principal principal);

    ModelAndView getAddCategory(ModelMap modelMap, Principal principal);

    ModelAndView getUpdateCategory(Integer id, ModelMap modelMap, Principal principal);

    ModelAndView postAddCategory(Category category, ModelMap modelMap, Principal principal);

    ModelAndView postUpdateCategory(Category category, ModelMap modelMap, Principal principal);

    ModelAndView categoryStatus(Integer id, boolean status, ModelMap modelMap, Principal principal);

    ModelAndView getPayment(ModelMap modelMap, Principal principal);

    ModelAndView postPaymentSearch(ModelMap modelMap, Principal principal, SearchPaymentDTO searchPaymentDTO);

    ModelAndView postTopupSearch(ModelMap modelMap, Principal principal, SearchPaymentDTO searchPaymentDTO);

    ModelAndView getTopup(String error, ModelMap modelMap, Principal principal);

    ModelAndView postTopup(ModelMap modelMap, Principal principal, AddMoneyDTO addMoneyDTO);

    ModelAndView showTopup(ModelMap modelMap, Principal principal);

    ModelAndView postSlide(MultipartFile file, Integer id, Integer storyId);

    ModelAndView postAddSlide(MultipartFile file, Integer id, Integer storyId);

    ModelAndView getSlider(Principal principal, ModelMap modelMap);

    ModelAndView getStory(ModelMap modelMap, Principal principal);

    ModelAndView setStoryStatus(Integer id, boolean status);

    ModelAndView getChapterByStoryId(Integer storyId,ModelMap modelMap, Principal principal);

    ModelAndView setChapterStatus(Integer storyId, Integer chapterId, boolean status);

    ModelAndView getAllUser(Principal principal,ModelMap modelMap);

    ModelAndView setStatusUser(Integer userId, boolean status);

    ModelAndView setStatusChapterDraft(Integer storyId, Integer chapterId, String code);

    ModelAndView getAuthorConfig(Integer id, ModelMap modelMap, Principal principal);

    ModelAndView postAuthorConfig(AuthorConfigDTO authorConfigDTO, ModelMap modelMap, Principal principal);
}
