package com.bookingTour.controller;

import com.bookingTour.model.TourInfo;
import com.bookingTour.service.TourService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;
import java.util.Optional;

@Controller
@RequestMapping("/tours")
public class ToursController {

    private static final Logger logger = LoggerFactory.getLogger(ToursController.class);

    @Autowired
    private TourService tourService;

    @GetMapping(value = {"", "/index"})
    public String index(@RequestParam(name = "page", required = false) Optional<Integer> page, Locale locale,
                        Model model, HttpServletRequest request) {
        TourInfo tourInfo = new TourInfo();
        tourInfo.setPage(page.orElse(1));
        Page<TourInfo> tours = tourService.paginate(tourInfo);
        model.addAttribute("tours", tours);
        model.addAttribute("tourSearch", tourInfo);
        return "tours/index";
    }

    @RequestMapping(value = "/search")
    public String search(@ModelAttribute(name = "tourSearch") TourInfo tourSearch, Model model) {
        logger.info("list user page with search");
        Page<TourInfo> tours = tourService.paginate(tourSearch);
        model.addAttribute("tours", tours);
        return "tours/index";
    }

    @GetMapping(value = {"/add"})
    public String add(Locale locale, Model model) {
        model.addAttribute("tour", new TourInfo());
        return "tours/add";
    }

    @PostMapping(value = {"", "/"})
    public String create(@ModelAttribute("tour") @Validated TourInfo tourInfo, BindingResult bindingResult, final RedirectAttributes redirectAttributes, Model model, HttpServletRequest request) throws Exception {
        if (bindingResult.hasErrors()) {
            logger.info("Returning add tour page, validate failed");
            return "tours/add";
        }

        TourInfo tour = tourService.addTour(tourInfo);
        if (tour.getId() != null) {
            redirectAttributes.addFlashAttribute("css", "success");
            redirectAttributes.addFlashAttribute("msg", "Tour is added!");
        } else {
            redirectAttributes.addFlashAttribute("css", "error");
            redirectAttributes.addFlashAttribute("msg", "Add Tour fails!!!!");
        }
        return"redirect: "+request.getContextPath()+"/tours/index";
}

    @GetMapping(value = "/{id}")
    public String show(@PathVariable Long id, Model model) {
        model.addAttribute("tour", tourService.findTour(id));
        return "tours/show";
    }

    @GetMapping(value = "{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("tour", tourService.findTour(id));
        return "tours/edit";
    }

    @PutMapping(value = "/{id}")
    public String update(@ModelAttribute("tour") @Validated TourInfo tourInfo,
                         BindingResult bindingResult, Model model, HttpServletRequest request) throws Exception {
        if (bindingResult.hasErrors()) {
            logger.info("Returning edit tour page, validate failed");
            return "tours/edit";
        }
        TourInfo tour = tourService.editTour(tourInfo);
        return "redirect: " + request.getContextPath() + "/tours/" + tour.getId();
    }

    @DeleteMapping(value = "/{id}", produces = {MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public String destroy(@PathVariable Long id, Model model, HttpServletRequest request,
                          HttpServletResponse response) throws Exception {
        logger.info("Deleting tour: " + id);
        tourService.deleteTour(new TourInfo(id));
        return "redirect: " + request.getContextPath() + "/tours/index";
    }
}