package com.doan.service.impl;

import com.doan.DTO.ChapterDTO;
import com.doan.DTO.PasswordDTO;
import com.doan.DTO.RegisterDTO;
import com.doan.DTO.StoryDTO;
import com.doan.entity.*;
import com.doan.repository.*;
import com.doan.service.*;
import com.doan.utils.CommonUtils;
import com.doan.utils.Constant;
import com.doan.utils.VNCharacterUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class HomeServiceImpl implements HomeService {
    private final UserRepository userRepository;
    private final AuthenService authenService;
    private final StoryRepository storyRepository;
    private final ChapterRepository chapterRepository;
    private final CategoryRepository categoryRepository;
    private final MinIOService minIOService;
    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final PaymentConfigRepository paymentConfigRepository;
    private final ChapterDraftRepository chapterDraftRepository;
    private final SlideRepository slideRepository;

    @Autowired
    public HomeServiceImpl(UserRepository userRepository, SlideRepository slideRepository, AuthenService authenService, StoryRepository storyRepository, ChapterRepository chapterRepository, CategoryRepository categoryRepository, MinIOService minIOService, TransactionRepository transactionRepository, WalletRepository walletRepository, PaymentConfigRepository paymentConfigRepository, ChapterDraftRepository chapterDraftRepository) {
        this.userRepository = userRepository;
        this.authenService = authenService;
        this.storyRepository = storyRepository;
        this.chapterRepository = chapterRepository;
        this.categoryRepository = categoryRepository;
        this.minIOService = minIOService;
        this.transactionRepository = transactionRepository;
        this.walletRepository = walletRepository;
        this.paymentConfigRepository = paymentConfigRepository;
        this.chapterDraftRepository = chapterDraftRepository;
        this.slideRepository = slideRepository;
    }

    public List<Category> getListCategory() {
        return categoryRepository.findAll();
    }

    void getCategory(ModelMap model) {
        List<Category> categoryList = getListCategory();
        categoryList.sort(Comparator.comparing(Category::getId));
        model.addAttribute("categories", categoryList);
    }

    void getTopView(ModelMap modelMap) {
        List<Story> list = storyRepository.findTop4ByStatusOrderByViewDesc(true);
        modelMap.addAttribute("topView", list);
    }
    void getTopNew(ModelMap modelMap) {
        List<Story> list = storyRepository.findTop4ByStatusOrderByCreatedAtDesc(true);
        modelMap.addAttribute("topNew", list);
    }



    void getSlide(ModelMap modelMap) {
        List<Slide> slides = slideRepository.findAll();
        StringBuilder slideContent = new StringBuilder();
        if(!CollectionUtils.isEmpty(slides)) {
            slideContent.append("<div class=\"container\">\n" +
                    "    <div id=\"demo\" class=\"carousel slide\" data-ride=\"carousel\">\n" +
                    "        <ul class=\"carousel-indicators\">");
            for(int i = 0 ;i < slides.size(); i++) {
                if(i == 0) {
                    slideContent.append("<li data-target=\"#demo\" data-slide-to=\"" + i + " \" class=\"active\"></li>");
                } else {
                    slideContent.append("<li data-target=\"#demo\" data-slide-to=\"" + i + " \"></li>");
                }
            }
            slideContent.append(" </ul> <div class=\"carousel-inner\">");
            for(int i = 0; i<slides.size() ; i++) {
                if(i==0) {
                    slideContent.append("<div class=\"carousel-item active\">\n" +
                            "            <img src=\"");
                    slideContent.append(slides.get(i).getImageUrl() + "\" width=\"1100\" height=\"500\">");
                    slideContent.append("<div class=\"carousel-caption\">");
                    slideContent.append("<a href=\"/story-detail?id=" + slides.get(i).getStoryId() +"\"><button type=\"button\" class=\"btn btn-success my-2 my-sm-0 my-2 my-sm-0\"> Đọc ngay</button> </a>");
                    slideContent.append("</div> </div>");
                } else {
                    slideContent.append("<div class=\"carousel-item\">\n" +
                            "            <img src=\"");
                    slideContent.append(slides.get(i).getImageUrl() + "\" width=\"1100\" height=\"500\">");
                    slideContent.append("<div class=\"carousel-caption\">");
                    slideContent.append("<a href=\"/story-detail?id=" + slides.get(i).getStoryId() +"\"><button type=\"button\" class=\"btn btn-success my-2 my-sm-0 my-2 my-sm-0\"> Đọc ngay</button> </a>");
                    slideContent.append("</div> </div>");
                }
            }
            slideContent.append("</div> <a class=\"carousel-control-prev\" href=\"#demo\" data-slide=\"prev\">\n" +
                    "            <span class=\"carousel-control-prev-icon\"></span>\n" +
                    "        </a>\n" +
                    "        <a class=\"carousel-control-next\" href=\"#demo\" data-slide=\"next\">\n" +
                    "            <span class=\"carousel-control-next-icon\"></span>\n" +
                    "        </a>\n" +
                    "    </div>\n" +
                    "</div>");
        }
        modelMap.addAttribute("slides", slideContent.toString());
    }

    void getUsername(ModelMap model, Principal principal) {
        User user = null;
        if (Objects.nonNull(principal)) {
            user = userRepository.findByUsername(principal.getName()).orElse(null);
        }
        if (Objects.nonNull(user)) {
            model.addAttribute("user", user);
        } else {
            model.addAttribute("user", new User());
        }
    }

    void getStoryByUsername(ModelMap model, Principal principal) {
        if(Objects.nonNull(principal)) {
            String username = principal.getName();
            User user = userRepository.findByUsername(username).orElse(null);
            if (Objects.nonNull(user)) {
                List<Story> list = storyRepository.findAllByUserId(user.getId());
                model.addAttribute("stories", list);
            }
        }
    }

    void getStoryNewest(ModelMap model) {
        List<Story> storiesNewest = storyRepository.findTop10ByStatusOrderByCreatedAtDesc(true);
        model.addAttribute("storiesNewest", storiesNewest);
    }

    @Override
    public ModelAndView postAddChapter(ChapterDTO chapterDTO) {
        Story story = storyRepository.findById(chapterDTO.getStoryId()).orElse(null);

        if(Objects.nonNull(story) && StringUtils.isEmpty(chapterDTO.getTime())) {
            Chapter chapter = new Chapter();
            chapter.setChapterName(chapterDTO.getName());
            chapter.setContent(chapterDTO.getContent());
            chapter.setStory(story);
            chapter.setPrice(chapterDTO.getPrice());
            chapter.setCreateAt(LocalDateTime.now());
            chapter.setStatus(true);
            chapterRepository.save(chapter);
            return new ModelAndView( "redirect:/story-detail?id=" + chapterDTO.getStoryId());
        } else if(Objects.nonNull(story) && !StringUtils.isEmpty(chapterDTO.getTime())) {
            LocalDate timer = CommonUtils.convertStringToDate(chapterDTO.getTime());
            ChapterDraft chapter = new ChapterDraft();
            chapter.setChapterName(chapterDTO.getName());
            chapter.setContent(chapterDTO.getContent());
            chapter.setStory(story);
            chapter.setPrice(chapterDTO.getPrice());
            chapter.setCreateAt(LocalDateTime.now());
            chapter.setTime(timer.atStartOfDay());
            chapter.setStatus(Constant.Status.PENDING);
            chapterDraftRepository.save(chapter);
            return new ModelAndView( "redirect:/story-detail?id=" + chapterDTO.getStoryId());
        }
        else {
            return new ModelAndView( "redirect:/");
        }

    }



    @Override
    public ModelAndView payment(ModelMap model, Integer chapterId, Principal principal) {
        if (Objects.nonNull(principal)) {
            String username = principal.getName();
            User user = userRepository.findByUsername(username).orElse(null);
            Chapter chapter = chapterRepository.getById(chapterId);
            Story story = chapter.getStory();
            Wallet walletUser = walletRepository.findByUserId(user.getId()).orElse(null);
            Wallet walletAuthor = walletRepository.findByUserId(story.getUser().getId()).orElse(null);
            Wallet walletMain = walletRepository.findMainWallet().orElse(null);
            if(story.getUser().getId() == user.getId()) {
                return new ModelAndView("redirect:/story-detail?id="+ chapter.getStory().getId());

            }

            if (chapter.getPrice() > walletUser.getAmount()) {
                return new ModelAndView("redirect:/story-detail?id="+ chapter.getStory().getId() + "&error=" + "01");
            }
            Transaction trans = transactionRepository.findByTransactionExist(walletUser.getId(), walletAuthor.getId(), walletMain.getId(), chapter.getId()).orElse(null);
            if(Objects.nonNull(trans)) {
                return new ModelAndView("redirect:/story-detail?id="+ chapter.getStory().getId() + "&error=" + "02");

            }
            if (Objects.nonNull(user)) {
                PaymentConfig paymentConfig = paymentConfigRepository.findByAuthorId(story.getUser().getId()).orElse(null);
                if (Objects.nonNull(paymentConfig)) {
                    walletUser.setAmount(walletUser.getAmount() - chapter.getPrice());
                    walletUser.setUpdatedAt(LocalDateTime.now());
                    walletAuthor.setAmount(walletAuthor.getAmount() + (chapter.getPrice() * paymentConfig.getChargeRate()) / 100);
                    walletAuthor.setUpdatedAt(LocalDateTime.now());
                    walletMain.setAmount(walletMain.getAmount() + (chapter.getPrice() * (100 - paymentConfig.getChargeRate())) / 100);
                    walletUser.setUpdatedAt(LocalDateTime.now());
                    Transaction transaction = new Transaction();
                    transaction.setChapterId(chapter.getId());
                    transaction.setUserWalletId(walletUser.getId());
                    transaction.setAuthorWalletId(walletAuthor.getId());
                    transaction.setMainWalletId(walletMain.getId());
                    transaction.setAmount(chapter.getPrice());
                    transaction.setChargeRate(paymentConfig.getChargeRate());
                    transaction.setCreatedAt(LocalDateTime.now());
                    transaction.setStatus("SUCCESS");
                    transaction.setType("PAYMENT");
                    transactionRepository.save(transaction);

                    List<Wallet> list = new ArrayList<>();
                    list.add(walletAuthor);
                    list.add(walletUser);
                    list.add(walletMain);
                    walletRepository.saveAll(list);
                    model.addAttribute("error", null);
                    return new ModelAndView("redirect:/story-detail?id="+ chapter.getStory().getId() + "&error=" + "00");
                } else {
                    walletUser.setAmount(walletUser.getAmount() - chapter.getPrice());
                    walletUser.setUpdatedAt(LocalDateTime.now());
                    walletAuthor.setAmount(walletAuthor.getAmount() + chapter.getPrice());
                    walletAuthor.setUpdatedAt(LocalDateTime.now());
                    walletMain.setAmount(walletMain.getAmount());
                    walletUser.setUpdatedAt(LocalDateTime.now());

                    Transaction transaction = new Transaction();
                    transaction.setChapterId(chapter.getId());
                    transaction.setUserWalletId(walletUser.getId());
                    transaction.setAuthorWalletId(walletAuthor.getId());
                    transaction.setMainWalletId(walletMain.getId());
                    transaction.setAmount(chapter.getPrice());
                    transaction.setChargeRate(100);
                    transaction.setStatus("SUCCESS");
                    transaction.setType("PAYMENT");
                    transaction.setCreatedAt(LocalDateTime.now());
                    transactionRepository.save(transaction);

                    List<Wallet> list = new ArrayList<>();
                    list.add(walletAuthor);
                    list.add(walletUser);
                    list.add(walletMain);
                    walletRepository.saveAll(list);
                    model.addAttribute("error", null);
                    return new ModelAndView("redirect:/story-detail?id="+ chapter.getStory().getId() + "&error=" + "00");
                }
            } else {

                return new ModelAndView("redirect:/");
            }
        } else {
            return new ModelAndView("redirect:/");
        }
    }

    @Override
    public ModelAndView getAddChapter(Principal principal, ModelMap model, Integer storyId) {
        getCategory(model);
        getUsername(model, principal);
        ChapterDTO chapterDTO = new ChapterDTO();;
        chapterDTO.setStoryId(storyId);
        model.addAttribute("chapterDTO", chapterDTO);
        return new ModelAndView( "add-chapter");
    }

    public ModelAndView search(String name, ModelMap model) {
        List<Story> list = storyRepository.findAll();
        List<Story> listSearch = new ArrayList<>();
        String nameSearch = VNCharacterUtils.removeAccent(name);
        for (Story story : list) {
            String storyName = VNCharacterUtils.removeAccent(story.getStoryName());
            if (storyName.contains(nameSearch)) {
                listSearch.add(story);
            }
        }
        return new ModelAndView("");
    }


    public List<Story> getListStoryByUserId(Integer userId) {
        List<Story> list = storyRepository.findAllByUserId(userId);
        return list;
    }

    public List<Story> getSearch(String search) {
        List<Story> list = storyRepository.findAll();
        List<Story> listSearch = new ArrayList<>();
        String nameSearch = VNCharacterUtils.removeAccent(search);
        for (Story story : list) {
            String storyName = VNCharacterUtils.removeAccent(story.getStoryName());
            if (storyName.toUpperCase().contains(nameSearch.toUpperCase())) {
                listSearch.add(story);
            }
        }
        return listSearch;
    }

    public StoryDTO getListChapterByUsername(Integer storyId, Principal principal) {
        List<ChapterDTO> list = new ArrayList<>();
        StoryDTO storyDTO = new StoryDTO();
        if (Objects.nonNull(principal)) {
            String username = principal.getName();
            User user = userRepository.findByUsername(username).orElse(null);
            for(Role r : user.getRoles()) {
                if("ROLE_ADMIN".equalsIgnoreCase(r.getRole())) {
                    storyDTO.setAdmin(true);
                }
            }
            Wallet wallet = walletRepository.findByUserId(user.getId()).orElse(null);
            Story story = storyRepository.findById(storyId).orElse(new Story());
            List<Chapter> listChapter = chapterRepository.findByStoryActive(storyId);
            if (!CollectionUtils.isEmpty(listChapter)) {
                for (Chapter chapter : listChapter) {
                    ChapterDTO chapterDTO = new ChapterDTO();
                    chapterDTO.setId(chapter.getId());
                    chapterDTO.setName(chapter.getChapterName());
                    chapterDTO.setContent(chapter.getContent());
                    chapterDTO.setPrice(chapter.getPrice());
                    chapterDTO.setStatus(chapter.isStatus());
                    Transaction transaction = transactionRepository.findTransaction(wallet.getId(), chapter.getId()).orElse(null);
                    if (Objects.nonNull(transaction)) {
                        chapterDTO.setPaymentStatus(true);
                    } else {
                        chapterDTO.setPaymentStatus(false);
                    }
                    if (user.getId() == story.getUser().getId()) {

                        chapterDTO.setPaymentStatus(true);
                    }
                    list.add(chapterDTO);
                }
            }
            if(story.getUser().getId() == user.getId()) {
                storyDTO.setAuthor(true);
            } else {
                storyDTO.setAuthor(false);
            }
            storyDTO.setView(story.getView());
            storyDTO.setId(story.getId());
            storyDTO.setName(story.getStoryName());
            storyDTO.setContent(story.getContent());
            storyDTO.setImage(story.getImage());
            storyDTO.setChapters(list);
        } else {
            Story story = storyRepository.findById(storyId).orElse(new Story());
            List<Chapter> listChapter = chapterRepository.findByStoryActive(storyId);
            if (!CollectionUtils.isEmpty(listChapter)) {
                for (Chapter chapter : listChapter) {
                    ChapterDTO chapterDTO = new ChapterDTO();
                    chapterDTO.setId(chapter.getId());
                    chapterDTO.setName(chapter.getChapterName());
                    chapterDTO.setContent(chapter.getContent());
                    chapterDTO.setPrice(chapter.getPrice());
                    chapterDTO.setPaymentStatus(false);
                    list.add(chapterDTO);
                }
            }

            storyDTO.setView(story.getView());
            storyDTO.setId(story.getId());
            storyDTO.setName(story.getStoryName());
            storyDTO.setContent(story.getContent());
            storyDTO.setImage(story.getImage());
            storyDTO.setChapters(list);
        }

        return storyDTO;
    }

    @Override
    public ModelAndView trangchu(ModelMap model, Principal principal) {
        getCategory(model);
        getUsername(model, principal);
        getSlide(model);
        getTopNew(model);
        getTopView(model);
        getStoryByUsername(model, principal);
        return new ModelAndView("home");
    }

    @Override
    public ModelAndView postRegister(ModelMap model, RegisterDTO registerDTO) {
        boolean check = authenService.register(registerDTO);
        if (check) {
            return new ModelAndView("redirect:/");
        } else {
            return new ModelAndView("register");
        }
    }

    @Override
    public ModelAndView getAddStory(ModelMap model, Story story, Principal principal) {
        getCategory(model);
        getUsername(model, principal);
        return new ModelAndView("add-story");
    }

    @Override
    public ModelAndView postAddStory(MultipartFile file, ModelMap model, Story story, Principal principal) {
        getUsername(model, principal);
        String url = minIOService.saveMultipartFile(file);
        User user = userRepository.findByUsername(principal.getName()).orElse(null);
        if (Objects.isNull(user)) {
            return new ModelAndView("add-story");
        }
        story.setUser(user);
        story.setCreatedAt(LocalDateTime.now());
        story.setImage(url);
        story.setStatus(true);
        storyRepository.save(story);
        return new ModelAndView("redirect:/");
    }

    @Override
    public ModelAndView getStoryDetail(Integer id, String error, ModelMap model, Principal principal) {
        if("01".equalsIgnoreCase(error)) {
            model.addAttribute("error", "Tài khoản của quý khách không đủ số dư để thanh toán");
        } else if ("02".equalsIgnoreCase(error)) {
            model.addAttribute("error", "Chương truyện đã được thanh toán trước đây");
        } else if ("00".equalsIgnoreCase(error)) {
            model.addAttribute("error", "Chương truyện đã được thanh toán thành công");
        } else if ("03".equalsIgnoreCase(error)) {
            model.addAttribute("error", "Cập nhật truyện thành công");
        } else if ("04".equalsIgnoreCase(error)) {
            model.addAttribute("error", "Cập nhật chương truyện thất bại");
        }
        getUsername(model, principal);
        getCategory(model);
        getStoryNewest(model);
        StoryDTO story = getListChapterByUsername(id, principal);
        List<ChapterDraft> list = chapterDraftRepository.findByChapterDraftPendingStoryId(id);
        if(!CollectionUtils.isEmpty(list)) {
            model.addAttribute("chapterDraft", list);
        } else {
            model.addAttribute("chapterDraft", new ArrayList<>());
        }
        model.addAttribute("story", story);
        return new ModelAndView("story-detail", model);
    }

    @Override
    public ModelAndView getSearch(String name, ModelMap model, Principal principal) {
        List<Story> list = getSearch(name);
        getUser(model, principal, null);
        getCategory(model);
        model.addAttribute("stories", list);
        return new ModelAndView("search", model);
    }

    @Override
    public ModelAndView getChapter(Integer id, String error, Principal principal, ModelMap modelMap) {
        Chapter chapter = chapterRepository.findById(id).orElse(null);
        getUsername(modelMap, principal);
        getCategory(modelMap);
        getStoryNewest(modelMap);
        if(Objects.nonNull(chapter)) {
            modelMap.addAttribute("chapter", chapter);
            Story story = chapter.getStory();
            story.setView(story.getView() + 1);
            storyRepository.save(story);
        }

        return new ModelAndView("chapter-detail", modelMap);
    }

    @Override
    public ModelAndView getUpdateStory(ModelMap model, Story story, Principal principal, Integer storyId) {
        story = storyRepository.findById(storyId).orElse(null);
        model.addAttribute("story", story);
        getCategory(model);
        getUsername(model, principal);
        return new ModelAndView("update-story", model);
    }

    @Override
    public ModelAndView postUpdateStory(MultipartFile file, ModelMap model, Story story, Principal principal) {
        getUsername(model, principal);
        Story story1 = storyRepository.findById(story.getId()).orElse(null);
        if(!file.isEmpty()) {
            String url = minIOService.saveMultipartFile(file);
            story1.setImage(url);
        }
        User user = userRepository.findByUsername(principal.getName()).orElse(null);
        if (Objects.isNull(user)) {
            return new ModelAndView("redirect:/story-detail?id=" + story.getId() + "&error=04");
        }
        if(!StringUtils.isEmpty(story.getStoryName())) {
            story1.setStoryName(story.getStoryName());
        }
        if(!StringUtils.isEmpty(story.getContent())) {
            story1.setContent(story.getContent());
        }
        if(!CollectionUtils.isEmpty(story.getCategories())) {
            story1.setCategories(story.getCategories());
        }
        story1.setUpdateAt(LocalDateTime.now());
        storyRepository.save(story1);
        return new ModelAndView("redirect:/story-detail?id=" + story.getId() + "&error=03");
    }

    @Override
    public ModelAndView getUpdateChapter(ModelMap modelMap, Principal principal, Integer chapterId) {
        Chapter chapter = chapterRepository.findById(chapterId).orElse(null);
        if(Objects.nonNull(chapter)) {
            ChapterDTO chapterDTO = new ChapterDTO();
            chapterDTO.setId(chapter.getId());
            chapterDTO.setPrice(chapter.getPrice());
            chapterDTO.setContent(chapter.getContent());
            chapterDTO.setName(chapter.getChapterName());
            chapterDTO.setStoryId(chapter.getStory().getId());
            chapterDTO.setUserId(chapter.getStory().getUser().getId());
            modelMap.addAttribute("chapterDTO", chapterDTO);
        }
        getCategory(modelMap);
        getUsername(modelMap, principal);
        return new ModelAndView("update-chapter", modelMap);
    }

    @Override
    public ModelAndView postUpdateChapter(ModelMap modelMap, Principal principal, ChapterDTO chapterDTO) {
        Chapter chapter = chapterRepository.findById(chapterDTO.getId()).orElse(null);
        if(Objects.nonNull(chapter)) {
            if(!StringUtils.isEmpty(chapterDTO.getName())) {
                chapter.setChapterName(chapterDTO.getName());
            }
            if(!StringUtils.isEmpty(chapter.getContent())) {
                chapter.setContent(chapterDTO.getContent());
            }
            if(!chapter.getPrice().equals(chapterDTO.getPrice()) && chapterDTO.getPrice() >= 0) {
                chapter.setPrice(chapterDTO.getPrice());
            }
            chapter.setUpdateAt(LocalDateTime.now());
            chapterRepository.save(chapter);
        }
        return new ModelAndView("redirect:/chapter-detail?id="+ chapter.getId(), modelMap);
    }

    @Override
    public ModelAndView getCategory(ModelMap modelMap, Principal principal, Integer id) {
        getUsername(modelMap,principal);
        getCategory(modelMap);
        Category category = categoryRepository.findById(id).orElse(null);
        if(Objects.nonNull(category)) {
            modelMap.addAttribute("type", null);
            modelMap.addAttribute("category", category);
            modelMap.addAttribute("topNew", new ArrayList<>());
            modelMap.addAttribute("topView", new ArrayList<>());
        } else {
            modelMap.addAttribute("topNew", new ArrayList<>());
            modelMap.addAttribute("topView", new ArrayList<>());
            modelMap.addAttribute("type", null);
            modelMap.addAttribute("category", new Category());
        }
        return new ModelAndView("category", modelMap);
    }

    @Override
    public ModelAndView topNew(ModelMap modelMap, Principal principal) {
        getUsername(modelMap,principal);
        getCategory(modelMap);
        List<Story> list = storyRepository.findTop4ByStatusOrderByCreatedAtDesc(true);
        if(!CollectionUtils.isEmpty(list)) {
            modelMap.addAttribute("type", "topNew");
            modelMap.addAttribute("topNew", list);
        } else {
            modelMap.addAttribute("type", "topNew");
            modelMap.addAttribute("topView", new ArrayList<>());
        }
        return new ModelAndView("category", modelMap);
    }

    @Override
    public ModelAndView topView(ModelMap modelMap, Principal principal) {
        getUsername(modelMap,principal);
        getCategory(modelMap);
        List<Story> list = storyRepository.findTop4ByStatusOrderByViewDesc(true);
        if(!CollectionUtils.isEmpty(list)) {
            modelMap.addAttribute("type", "topView");
            modelMap.addAttribute("topView", list);
        } else {
            modelMap.addAttribute("type", "topView");
            modelMap.addAttribute("topView", new ArrayList<>());
        }
        return new ModelAndView("category", modelMap);
    }

    @Override
    public ModelAndView getUser(ModelMap modelMap, Principal principal, String errorCode) {
        if("00".equalsIgnoreCase(errorCode)) {
            modelMap.addAttribute("error", "Cập nhật thông tin thành công");
        } else if ("01".equalsIgnoreCase(errorCode)) {
            modelMap.addAttribute("error", "Cập nhật mật khẩu thành công");
        } else if ("02".equalsIgnoreCase(errorCode)) {
            modelMap.addAttribute("error", "Mật khẩu xác nhận không trùng khớp");
        } else if ("03".equalsIgnoreCase(errorCode)) {
            modelMap.addAttribute("error", "Mật khẩu cũ không chính xác");
        }
        if(Objects.isNull(principal)) {
            return new ModelAndView("redirect:/");
        }
        modelMap.addAttribute("passwordDTO", new PasswordDTO());
        getCategory(modelMap);
        getUsername(modelMap, principal);
        User user = userRepository.findByUsername(principal.getName()).orElse(new User());
        modelMap.addAttribute("user", user);
        return new ModelAndView("user", modelMap);
    }

    @Override
    public ModelAndView topupInfo(ModelMap modelMap, Principal principal) {
        getUser(modelMap, principal, null);
        getCategory(modelMap);
        return new ModelAndView("topup-info", modelMap);
    }

    @Override
    public ModelAndView updateUserInfo(MultipartFile file, ModelMap modelMap, Principal principal, User user) {
        User userDB= null;
        if (Objects.nonNull(principal)) {
            userDB = userRepository.findByUsername(principal.getName()).orElse(null);
        } else {
            return new ModelAndView("redirect:/");
        }
        if(!file.isEmpty() && Objects.nonNull(user)) {
            String avatar = minIOService.saveMultipartFile(file);
            userDB.setAvatar(avatar);
        }
        if(!StringUtils.isEmpty(user.getAddress())&& Objects.nonNull(user)) {
            userDB.setAddress(user.getAddress());
        }
        if(!StringUtils.isEmpty(user.getPhone())&& Objects.nonNull(user)) {
            userDB.setPhone(user.getPhone());
        }
        userRepository.save(userDB);
        return  new ModelAndView("redirect:/user?error-code=00");
    }

    @Override
    public ModelAndView updatePassword(ModelMap modelMap, Principal principal, PasswordDTO passwordDTO) {
        User user = null;
        if(Objects.nonNull(principal)) {
            user = userRepository.findByUsername(principal.getName()).orElse(null);
        }
        if(Objects.nonNull(user)) {
            if(user.getPasswordRegister().equalsIgnoreCase(passwordDTO.getOldPassword())) {
                if (!StringUtils.isEmpty(passwordDTO.getNewPassword()) && !StringUtils.isEmpty(passwordDTO.getConfirmPassword())) {

                    if(passwordDTO.getNewPassword().equals(passwordDTO.getConfirmPassword())) {
                        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                        user.setPassword(passwordEncoder.encode(passwordDTO.getNewPassword()));
                        user.setPasswordRegister(passwordDTO.getNewPassword());
                        user.setPasswordRegisterConfirm(passwordDTO.getConfirmPassword());
                        user.setUpdateAt(LocalDateTime.now());
                        userRepository.save(user);
                    } else {
                        return  new ModelAndView("redirect:/user?error-code=02");
                    }
                }
            }  else {
                return  new ModelAndView("redirect:/user?error-code=03");
            }
            return  new ModelAndView("redirect:/user?error-code=01");
        } else {
            return new ModelAndView("redirect:/");
        }
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
        return new ModelAndView("redirect:/story-detail?id=" + storyId);
    }

    @Override
    public ModelAndView getUpdateChapterDraft(ModelMap model, Principal principal, Integer id) {
        getUsername(model, principal);
        getCategory(model);
        ChapterDraft chapterDraft = chapterDraftRepository.findById(id).orElse(null);
        if(Objects.nonNull(chapterDraft)) {
            ChapterDTO chapterDTO = new ChapterDTO();
            chapterDTO.setId(chapterDraft.getId());
            chapterDTO.setContent(chapterDraft.getContent());
            chapterDTO.setName(chapterDraft.getChapterName());
            chapterDTO.setPrice(chapterDraft.getPrice());
            chapterDTO.setStoryId(chapterDraft.getStory().getId());
            chapterDTO.setTime(CommonUtils.convertDateToString(chapterDraft.getTime()));
            model.addAttribute("chapterDTO", chapterDTO);
        }
        return new ModelAndView("update-chapter-draft", model);
    }

    @Override
    public ModelAndView postUpdateChapterDraft(ModelMap model, Principal principal, ChapterDTO chapterDTO) {
        ChapterDraft chapterDraft = chapterDraftRepository.findById(chapterDTO.getId()).orElse(null);
        if(Objects.nonNull(chapterDraft)) {
            if(!StringUtils.isEmpty(chapterDTO.getName())) {
                chapterDraft.setChapterName(chapterDTO.getName());
            }
            if(!StringUtils.isEmpty(chapterDTO.getContent())) {
                chapterDraft.setContent(chapterDTO.getContent());
            }
            if(!StringUtils.isEmpty(chapterDTO.getTime())) {
                chapterDraft.setTime(CommonUtils.convertStringToDate(chapterDTO.getTime()).atStartOfDay());
            }
            chapterDraft.setPrice(chapterDTO.getPrice());
            chapterDraft.setUpdateAt(LocalDateTime.now());
            chapterDraftRepository.save(chapterDraft);
            return new ModelAndView("redirect:/story-detail?id=" + chapterDTO.getStoryId());
        } else {
            return new ModelAndView("redirect:/story-detail?id=" + chapterDTO.getStoryId());
        }
    }
}
