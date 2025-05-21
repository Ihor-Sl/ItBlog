package ua.iate.itblog.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ua.iate.itblog.model.report.CreateReportRequest;
import ua.iate.itblog.service.ReportService;

@Controller
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    public final ReportService reportService;

    @GetMapping("/create-report")
    public String createReportGet(Model model, @RequestParam(required = false) String postId, @RequestParam String userId,
                                  @RequestParam String reporterId) {
        CreateReportRequest reportRequest = new CreateReportRequest();
        reportRequest.setPostId(postId);
        reportRequest.setUserId(userId);
        reportRequest.setReporterId(reporterId);
        model.addAttribute("createReportRequest", reportRequest);
        return "report/create-report";
    }

    @PostMapping("/create-report")
    public String createReportPost(@ModelAttribute("createReportRequest") @Valid CreateReportRequest createReportRequest,
                                   BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("createReportRequest", createReportRequest);
            return "report/create-report";
        }
        reportService.createReport(createReportRequest);
        return "redirect:/";
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String reports(Model model) {
        model.addAttribute("reports", reportService.findAll());
        return "report/reports";
    }

    @PostMapping("/{reportId}/delete")
    public String deleteReport(@PathVariable String reportId) {
        reportService.deleteReport(reportId);
        return "redirect:/reports";
    }
}