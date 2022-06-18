package com.doan.service.impl;

import com.doan.DTO.*;
import com.doan.entity.*;
import com.doan.repository.*;
import com.doan.service.AdminService;
import com.doan.service.MinIOService;
import com.doan.utils.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class AdminServiceImpl implements AdminService {
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final StoryRepository storyRepository;
    private final ChapterRepository chapterRepository;
    private final ChapterDraftRepository chapterDraftRepository;
    private final PaymentConfigRepository paymentConfigRepository;
    private final SlideRepository slideRepository;
    private final MinIOService minIOService;

    @Autowired
    public AdminServiceImpl(CategoryRepository categoryRepository, UserRepository userRepository, TransactionRepository transactionRepository,
                            WalletRepository walletRepository, StoryRepository storyRepository, ChapterRepository chapterRepository, PaymentConfigRepository paymentConfigRepository,
                            SlideRepository slideRepository, MinIOService minIOService, ChapterDraftRepository chapterDraftRepository) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.walletRepository = walletRepository;
        this.storyRepository = storyRepository;
        this.chapterRepository = chapterRepository;
        this.paymentConfigRepository = paymentConfigRepository;
        this.slideRepository = slideRepository;
        this.minIOService = minIOService;
        this.chapterDraftRepository = chapterDraftRepository;
    }

    public void getUser(Principal principal, ModelMap modelMap) {
        String username = null;
        if (Objects.nonNull(principal)) {
            username = principal.getName();
        }
        User user = userRepository.findByUsername(username).orElse(null);
        modelMap.addAttribute("user", user);
    }

    public void getListCategory(ModelMap modelMap, Principal principal) {
        List<Category> list = categoryRepository.findAll();
        modelMap.addAttribute("listCategory", list);
    }

    public void changeStatus(Integer id, boolean status) {
        Category category = categoryRepository.findById(id).orElse(null);
        if (Objects.nonNull(category)) {
            category.setStatus(status);
            categoryRepository.save(category);
        }
    }

    public void payment(ModelMap modelMap, Principal principal) {
        List<Transaction> transactions = transactionRepository.findAllByPayment();
        List<PaymentDTO> paymentDTOS = new ArrayList<>();
        Integer doanhThuThang = 0;
        Integer doanhThuNam = 0;
        if (!CollectionUtils.isEmpty(transactions)) {
            for (Transaction t : transactions) {
                PaymentDTO paymentDTO = new PaymentDTO();
                paymentDTO.setId(t.getId());
                Wallet userWallet = walletRepository.getById(t.getUserWalletId());
                paymentDTO.setUser(userWallet.getUser().getUsername());
                Wallet authorWallet = walletRepository.getById(t.getAuthorWalletId());
                paymentDTO.setAuthor(authorWallet.getUser().getUsername());
                paymentDTO.setChargeRate(t.getChargeRate());
                Chapter chapter = chapterRepository.getById(t.getChapterId());
                paymentDTO.setChapterId(chapter.getId());
                paymentDTO.setStoryId(chapter.getStory().getId());
                paymentDTO.setPaymentDate(t.getCreatedAt());
                paymentDTO.setAmount(t.getAmount());
                paymentDTOS.add(paymentDTO);

                if (t.getCreatedAt().getMonth().equals(LocalDateTime.now().getMonth())) {
                    doanhThuThang = doanhThuThang + (t.getAmount() * (100 - t.getChargeRate())) / 100;
                }
                if (t.getCreatedAt().getYear() == LocalDateTime.now().getYear()) {
                    doanhThuNam = doanhThuNam + (t.getAmount() * (100 - t.getChargeRate())) / 100;
                }
            }

        }

        modelMap.addAttribute("doanhThuThang", doanhThuThang);
        modelMap.addAttribute("doanhThuNam", doanhThuNam);
        modelMap.addAttribute("paymentDTO", paymentDTOS);
    }

    public void show_topup(ModelMap modelMap, Principal principal) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy");
        List<Transaction> transactions = transactionRepository.findAllByTopup();
        List<TopupDTO> topupDTOS = new ArrayList<>();
        int topupThang = 0;
        int topupNam = 0;
        if (!CollectionUtils.isEmpty(transactions)) {
            for (Transaction t : transactions) {
                TopupDTO topupDTO = new TopupDTO();
                topupDTO.setId(t.getId());
                Wallet user = walletRepository.getById(t.getUserWalletId());
                topupDTO.setUser(user.getUser().getUsername());
                topupDTO.setCreated_at(t.getCreatedAt());
                topupDTO.setAmount(t.getAmount());
                topupDTOS.add(topupDTO);
                if (t.getCreatedAt().getMonth().equals(LocalDateTime.now().getMonth())) {
                    topupThang = topupThang + (t.getAmount() * (100 - t.getChargeRate())) / 100;
                }
                if (t.getCreatedAt().getYear() == LocalDateTime.now().getYear()) {
                    topupNam = topupNam + (t.getAmount() * (100 - t.getChargeRate())) / 100;
                }
            }
        }
        modelMap.addAttribute("topupThang", topupThang);
        modelMap.addAttribute("topupNam", topupNam);
        modelMap.addAttribute("topupDTOS", topupDTOS);

    }

    public ModelAndView topupSearch(ModelMap modelMap, Principal principal, SearchPaymentDTO searchPaymentDTO) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy");
        List<Transaction> transactions = transactionRepository.findAllByTopup();
        List<TopupDTO> topupDTOS = new ArrayList<>();
        User user = userRepository.findByUsername(searchPaymentDTO.getSearch()).orElse(null);
        int topupThang = 0;
        int topupNam = 0;
        if(Objects.isNull(user)) {
            modelMap.addAttribute("topupThang", topupThang);
            modelMap.addAttribute("topupNam", topupNam);
            modelMap.addAttribute("topupDTOS", topupDTOS);
            return new ModelAndView("/admin/payment/payment", modelMap);
        }
        if (!CollectionUtils.isEmpty(transactions)) {
            for (Transaction t : transactions) {
                Wallet userWallet = walletRepository.getById(t.getUserWalletId());
                if(userWallet.getUser().getId() == user.getId()) {
                    TopupDTO topupDTO = new TopupDTO();
                    topupDTO.setId(t.getId());
                    topupDTO.setUser(userWallet.getUser().getUsername());
                    topupDTO.setCreated_at(t.getCreatedAt());
                    topupDTO.setAmount(t.getAmount());
                    topupDTOS.add(topupDTO);
                    if (t.getCreatedAt().getMonth().equals(LocalDateTime.now().getMonth())) {
                        topupThang = topupThang + (t.getAmount() * (100 - t.getChargeRate())) / 100;
                    }
                    if (t.getCreatedAt().getYear() == LocalDateTime.now().getYear()) {
                        topupNam = topupNam + (t.getAmount() * (100 - t.getChargeRate())) / 100;
                    }
                }

            }
        }
        modelMap.addAttribute("topupThang", topupThang);
        modelMap.addAttribute("topupNam", topupNam);
        modelMap.addAttribute("topupDTOS", topupDTOS);
        return new ModelAndView("/admin/payment/payment", modelMap);
    }

    public void paymentSearch(SearchPaymentDTO searchPaymentDTO, ModelMap modelMap, Principal principal) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy");
        List<Transaction> transactions = transactionRepository.findAllByPayment();
        List<PaymentDTO> paymentDTOS = new ArrayList<>();
        User user = userRepository.findByUsername(searchPaymentDTO.getSearch()).orElse(null);
        Integer doanhThuThang = 0;
        Integer doanhThuNam = 0;
        if (Objects.isNull(user)) {

            modelMap.addAttribute("doanhThuThang", doanhThuThang);
            modelMap.addAttribute("doanhThuNam", doanhThuNam);
            modelMap.addAttribute("paymentDTO", paymentDTOS);
        } else {
            if (!CollectionUtils.isEmpty(transactions)) {
                for (Transaction t : transactions) {

                    Wallet userWallet = walletRepository.getById(t.getUserWalletId());

                    Wallet authorWallet = walletRepository.getById(t.getAuthorWalletId());
                    if("author".equalsIgnoreCase(searchPaymentDTO.getAuthor()) && user.getId() == authorWallet.getUser().getId()) {
                        PaymentDTO paymentDTO = new PaymentDTO();
                        paymentDTO.setId(t.getId());
                        paymentDTO.setUser(userWallet.getUser().getUsername());
                        paymentDTO.setAuthor(authorWallet.getUser().getUsername());
                        paymentDTO.setChargeRate(t.getChargeRate());
                        Chapter chapter = chapterRepository.getById(t.getChapterId());
                        paymentDTO.setChapterId(chapter.getId());
                        paymentDTO.setStoryId(chapter.getStory().getId());
                        paymentDTO.setPaymentDate(t.getCreatedAt());
                        paymentDTO.setAmount(t.getAmount());
                        paymentDTOS.add(paymentDTO);
                        if (t.getCreatedAt().getMonth().equals(LocalDateTime.now().getMonth())) {
                            doanhThuThang = doanhThuThang + (t.getAmount() * (100 - t.getChargeRate())) / 100;
                        }
                        if (t.getCreatedAt().getYear() == LocalDateTime.now().getYear()) {
                            doanhThuNam = doanhThuNam + (t.getAmount() * (100 - t.getChargeRate())) / 100;
                        }
                    }
                    if("user".equalsIgnoreCase(searchPaymentDTO.getUser()) && user.getId() == userWallet.getUser().getId()) {
                        PaymentDTO paymentDTO = new PaymentDTO();
                        paymentDTO.setId(t.getId());
                        paymentDTO.setUser(userWallet.getUser().getUsername());
                        paymentDTO.setAuthor(authorWallet.getUser().getUsername());
                        paymentDTO.setChargeRate(t.getChargeRate());
                        Chapter chapter = chapterRepository.getById(t.getChapterId());
                        paymentDTO.setChapterId(chapter.getId());
                        paymentDTO.setStoryId(chapter.getStory().getId());
                        paymentDTO.setPaymentDate(t.getCreatedAt());
                        paymentDTO.setAmount(t.getAmount());
                        paymentDTOS.add(paymentDTO);
                        if (t.getCreatedAt().getMonth().equals(LocalDateTime.now().getMonth())) {
                            doanhThuThang = doanhThuThang + (t.getAmount() * (100 - t.getChargeRate())) / 100;
                        }
                        if (t.getCreatedAt().getYear() == LocalDateTime.now().getYear()) {
                            doanhThuNam = doanhThuNam + (t.getAmount() * (100 - t.getChargeRate())) / 100;
                        }
                    }
                }

            }

            modelMap.addAttribute("doanhThuThang", doanhThuThang);
            modelMap.addAttribute("doanhThuNam", doanhThuNam);
            modelMap.addAttribute("paymentDTO", paymentDTOS);
        }
    }

    @Override
    public ModelAndView dashboard(ModelMap modelMap, Principal principal) {
        getUser(principal, modelMap);
        modelMap.addAttribute("category", "dashboard");
        return new ModelAndView("admin/dashboard", modelMap);
    }

    @Override
    public ModelAndView category(ModelMap modelMap, Principal principal) {
        getUser(principal, modelMap);
        modelMap.addAttribute("category", "category");
        getListCategory(modelMap, principal);
        return new ModelAndView("admin/category/category", modelMap);
    }

    @Override
    public ModelAndView categoryStatus(Integer id, boolean status, ModelMap modelMap, Principal principal) {
        getUser(principal, modelMap);
        modelMap.addAttribute("category", "category");
        changeStatus(id, status);
        getListCategory(modelMap, principal);
        return new ModelAndView("admin/category/category", modelMap);
    }

    @Override
    public ModelAndView getPayment(ModelMap modelMap, Principal principal) {
        modelMap.addAttribute("searchPaymentDTO", new SearchPaymentDTO());
        modelMap.addAttribute("category", "payment");
        modelMap.addAttribute("show", "payment");
        getUser(principal, modelMap);
        payment(modelMap, principal);
        return new ModelAndView("/admin/payment/payment", modelMap);
    }

    @Override
    public ModelAndView postPaymentSearch(ModelMap modelMap, Principal principal, SearchPaymentDTO searchPaymentDTO) {
        modelMap.addAttribute("category", "payment");
        modelMap.addAttribute("show", "payment");
        getUser(principal, modelMap);
        paymentSearch(searchPaymentDTO, modelMap, principal);
        return new ModelAndView("/admin/payment/payment", modelMap);
    }

    @Override
    public ModelAndView getTopup(String error, ModelMap modelMap, Principal principal) {
        if (!StringUtils.isEmpty(error)) {
            if ("00".equalsIgnoreCase(error)) {
                modelMap.addAttribute("error", "Nạp tiền thành công");
            } else if ("01".equalsIgnoreCase(error)) {
                modelMap.addAttribute("error", "Nạp tiền thất bại");
            } else if ("02".equalsIgnoreCase(error)) {
                modelMap.addAttribute("error", "Tài khoản đã bị khoá hoặc tài khoản không tồn tại");
            } else if ("03".equalsIgnoreCase(error)) {
                modelMap.addAttribute("error", "Số tiền không hợp lệ");
            }
        } else {
            modelMap.addAttribute("error", null);
        }

        modelMap.addAttribute("show", "topup");
        modelMap.addAttribute("category", "payment");
        modelMap.addAttribute("addMoneyDTO", new AddMoneyDTO());
        getUser(principal, modelMap);
        return new ModelAndView("/admin/payment/topup", modelMap);
    }

    @Override
    public ModelAndView postTopup(ModelMap modelMap, Principal principal, AddMoneyDTO addMoneyDTO) {
        modelMap.addAttribute("show", "topup");
        if (Objects.nonNull(addMoneyDTO)) {
            if (addMoneyDTO.getAmount() <= 0) {
                return new ModelAndView("redirect:/admin/payment/topup?error=03");
            }
            if (addMoneyDTO.getAmount() > 0 && !StringUtils.isEmpty(addMoneyDTO.getUsername())) {
                User user = userRepository.findByUsername(addMoneyDTO.getUsername()).orElse(null);

                if (Objects.nonNull(user)) {
                    if (!user.isStatus()) {
                        return new ModelAndView("redirect:/admin/payment/topup?error=02");
                    }
                    try {
                        Transaction transaction = new Transaction();
                        transaction.setUserWalletId(user.getWallet().getId());
                        transaction.setAmount(addMoneyDTO.getAmount());
                        transaction.setType("TOPUP");
                        transaction.setStatus("SUCCESS");
                        transaction.setCreatedAt(LocalDateTime.now());
                        transactionRepository.save(transaction);

                        Wallet wallet = user.getWallet();
                        wallet.setAmount(wallet.getAmount() + addMoneyDTO.getAmount());
                        wallet.setUpdatedAt(LocalDateTime.now());
                        walletRepository.save(wallet);
                        return new ModelAndView("redirect:/admin/payment/topup?error=00");
                    } catch (Exception e) {
                        return new ModelAndView("redirect:/admin/payment/topup?error=01");
                    }
                } else {
                    return new ModelAndView("redirect:/admin/payment/topup?error=02");
                }

            }
        }
        return new ModelAndView("redirect:/admin/payment/topup");
    }

    @Override
    public ModelAndView showTopup(ModelMap modelMap, Principal principal) {
        modelMap.addAttribute("searchPaymentDTO", new SearchPaymentDTO());
        modelMap.addAttribute("category", "payment");
        modelMap.addAttribute("show", "topup");
        getUser(principal, modelMap);
        show_topup(modelMap, principal);
        return new ModelAndView("/admin/payment/payment", modelMap);
    }

    @Override
    public ModelAndView postTopupSearch(ModelMap modelMap, Principal principal, SearchPaymentDTO searchPaymentDTO) {
        modelMap.addAttribute("category", "payment");
        modelMap.addAttribute("show", "topup");
        getUser(principal, modelMap);
        return  topupSearch(modelMap, principal, searchPaymentDTO);
    }

    @Override
    public ModelAndView postSlide(MultipartFile file, Integer id, Integer storyId) {
        Slide slide = slideRepository.findById(id).orElse(null);
        if (!file.isEmpty() && Objects.nonNull(slide)) {
            try {
                String url = minIOService.saveMultipartFile(file);
                slide.setImageUrl(url);
            } catch (Exception e) {
                return new ModelAndView("redirect:/admin/slide");
            }
        }
        if(storyId != null) {
            slide.setStoryId(storyId);
        }
        slide.setUpdateAt(LocalDateTime.now());
        slideRepository.save(slide);
        return new ModelAndView("redirect:/admin/slide");
    }

    @Override
    public ModelAndView postAddSlide(MultipartFile file, Integer id, Integer storyId) {
        Slide slide = new Slide();
        if (!file.isEmpty() && Objects.nonNull(slide)) {
            try {
                String url = minIOService.saveMultipartFile(file);
                slide.setImageUrl(url);
            } catch (Exception e) {
                return new ModelAndView("redirect:/admin/slide");
            }
        }
        if(storyId != null) {
            slide.setStoryId(storyId);
        }
        slide.setCreatedAt(LocalDateTime.now());
        slideRepository.save(slide);
        return new ModelAndView("redirect:/admin/slide");
    }

    @Override
    public ModelAndView getSlider(Principal principal, ModelMap modelMap) {
        List<Slide> list = slideRepository.findAll();
        getUser(principal, modelMap);
        modelMap.addAttribute("category", "slide-category");
        modelMap.addAttribute("slides", list);
        modelMap.addAttribute("slide", new Slide());
        return new ModelAndView("/admin/slide/slide", modelMap);
    }

    @Override
    public ModelAndView getStory(ModelMap modelMap, Principal principal) {
        List<Story> list = storyRepository.findAll();
        modelMap.addAttribute("category", "story-category");
        modelMap.addAttribute("stories", list);
        modelMap.addAttribute("show", "story");
        getUser(principal, modelMap);
        return new ModelAndView("/admin/story/story", modelMap);
    }

    @Override
    public ModelAndView setStoryStatus(Integer id, boolean status) {
        Story story = storyRepository.findById(id).orElse(null);
        if(Objects.nonNull(story)) {
            story.setStatus(status);
            storyRepository.save(story);
            return new ModelAndView("redirect:/admin/story");
        } else {
            return new ModelAndView("redirect:/admin/story");
        }
    }

    @Override
    public ModelAndView getChapterByStoryId(Integer storyId, ModelMap modelMap, Principal principal) {
        List<Chapter> list = chapterRepository.findByStoryId(storyId);
        List<ChapterDraft> listDraft = chapterDraftRepository.findByChapterDraftPendingStoryId(storyId);
        getUser(principal, modelMap);
        modelMap.addAttribute("category", "story-category");
        if(!CollectionUtils.isEmpty(listDraft)) {
            modelMap.addAttribute("chapterDraft", listDraft);
        } else {
            modelMap.addAttribute("chapterDraft", new ArrayList<>());
        }
        if(!CollectionUtils.isEmpty(list)) {
            modelMap.addAttribute("category", "story-category");
            modelMap.addAttribute("show", "chapter");
            modelMap.addAttribute("chapters", list);
            return new ModelAndView("/admin/story/story", modelMap);
        } else {
            modelMap.addAttribute("category", "story-category");
            modelMap.addAttribute("show", "chapter");
            modelMap.addAttribute("chapters", new ArrayList<>());
            return new ModelAndView("/admin/story/story", modelMap);
        }
    }

    @Override
    public ModelAndView setChapterStatus(Integer storyId, Integer chapterId, boolean status) {
        Chapter chapter = chapterRepository.findById(chapterId).orElse(null);
        if(Objects.nonNull(chapter)) {
            chapter.setStatus(status);
            chapterRepository.save(chapter);
        }
        return new ModelAndView("redirect:/admin/chapter?story_id=" + storyId);
    }

    @Override
    public ModelAndView getAllUser(Principal principal,ModelMap modelMap) {
        List<User> users = userRepository.findAll();
        getUser(principal, modelMap);
        modelMap.addAttribute("users", users);
        modelMap.addAttribute("category", "user-category");
        return new ModelAndView("/admin/user/user", modelMap);
    }

    @Override
    public ModelAndView setStatusUser(Integer userId, boolean status) {
        User user = userRepository.findById(userId).orElse(null);
        if(Objects.nonNull(user)) {
            user.setStatus(status);
            userRepository.save(user);
            return new ModelAndView("redirect:/admin/user");
        } else {
            return new ModelAndView("redirect:/admin/user");
        }
    }

    @Override
    public ModelAndView getAddCategory(ModelMap modelMap, Principal principal) {
        getUser(principal, modelMap);
        modelMap.addAttribute("category", "category");
        modelMap.addAttribute("categoryDTO", new Category());
        modelMap.addAttribute("type", "NEW");
        return new ModelAndView("/admin/category/add-category", modelMap);
    }

    @Override
    public ModelAndView getUpdateCategory(Integer id, ModelMap modelMap, Principal principal) {
        getUser(principal, modelMap);
        modelMap.addAttribute("category", "category");
        Category category = categoryRepository.findById(id).orElse(null);
        if(Objects.nonNull(category)) {
            modelMap.addAttribute("categoryDTO", category);
            modelMap.addAttribute("type", "UPDATE");
        }
        return new ModelAndView("/admin/category/add-category", modelMap);

    }

    @Override
    public ModelAndView postAddCategory(Category category, ModelMap modelMap, Principal principal) {
        if(Objects.nonNull(category)) {
            category.setStatus(true);
            category.setCreateAt(LocalDateTime.now());
            categoryRepository.save(category);
        }
        return new ModelAndView("redirect:/admin/category");
    }

    @Override
    public ModelAndView postUpdateCategory(Category category, ModelMap modelMap, Principal principal) {
        Category categoryUpdate = categoryRepository.findById(category.getId()).orElse(null);
        if(Objects.nonNull(categoryUpdate)) {
            categoryUpdate.setCategoryName(category.getCategoryName());
            categoryUpdate.setUpdateAt(LocalDateTime.now());
            categoryRepository.save(categoryUpdate);
        }
        return new ModelAndView("redirect:/admin/category");
    }

    @Override
    public ModelAndView setStatusChapterDraft(Integer storyId, Integer chapterId, String code) {
        ChapterDraft chapterDraft = chapterDraftRepository.findById(chapterId).orElse(null);
        if(Objects.nonNull(chapterDraft)) {
            if(!StringUtils.isEmpty(code) && "DELETE".equalsIgnoreCase(code)) {
                chapterDraftRepository.delete(chapterDraft);
            }
            if(!StringUtils.isEmpty(code) && "ACTIVE".equalsIgnoreCase(code)) {
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
        return new ModelAndView("redirect:/admin/chapter?story_id=" + storyId);
    }

    @Override
    public ModelAndView getAuthorConfig(Integer id, ModelMap modelMap, Principal principal) {
        User user = userRepository.findById(id).orElse(null);
        if(Objects.nonNull(user)) {
            boolean checkAuthor = false;
            for(Role r : user.getRoles()) {
                if("ROLE_AUTHOR".equalsIgnoreCase(r.getRole())) {
                    checkAuthor = true;
                }
            }
            PaymentConfig paymentConfig = paymentConfigRepository.findByAuthorId(id).orElse(null);
            if(checkAuthor) {
                AuthorConfigDTO authorConfigDTO = new AuthorConfigDTO();
                authorConfigDTO.setUsername(user.getUsername());
                authorConfigDTO.setId(user.getId());
                if(Objects.nonNull(paymentConfig)) {
                    authorConfigDTO.setChargeRate(paymentConfig.getChargeRate());
                } else {
                    authorConfigDTO.setChargeRate(0);
                }
                modelMap.addAttribute("authorConfigDTO", authorConfigDTO);
            } else {
               return new ModelAndView("redirect:/admin/user");
            }
        }
        modelMap.addAttribute("category", "user-category");
        getUser(principal, modelMap);
        return new ModelAndView("/admin/user/author-config", modelMap);
    }

    @Override
    public ModelAndView postAuthorConfig(AuthorConfigDTO authorConfigDTO, ModelMap modelMap, Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElse(null);
        if(Objects.nonNull(authorConfigDTO)) {
            PaymentConfig paymentConfig = paymentConfigRepository.findByAuthorId(authorConfigDTO.getId()).orElse(null);
            if(Objects.nonNull(paymentConfig)) {
                paymentConfig.setChargeRate(authorConfigDTO.getChargeRate());
                paymentConfig.setUser(user);
                paymentConfig.setUpdatedAt(LocalDateTime.now());
                paymentConfigRepository.save(paymentConfig);
                return new ModelAndView("redirect:/admin/user");
            } else {
                paymentConfig = new PaymentConfig();
                paymentConfig.setAuthorId(authorConfigDTO.getId());
                paymentConfig.setChargeRate(authorConfigDTO.getChargeRate());
                paymentConfig.setUser(user);
                paymentConfig.setCreatedAt(LocalDateTime.now());
                paymentConfigRepository.save(paymentConfig);
                return new ModelAndView("redirect:/admin/user");
            }
        } else {
            return new ModelAndView("redirect:/admin/user");
        }

    }
}
