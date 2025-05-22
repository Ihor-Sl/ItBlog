package ua.iate.itblog.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import ua.iate.itblog.model.report.CreateReportRequest;
import ua.iate.itblog.service.ReportService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReportControllerTest {

    @InjectMocks
    private ReportController reportController;

    @Mock
    private ReportService reportService;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createReportGet_shouldAddCreateReportRequestToModelAndReturnView() {
        String postId = "post1";
        String userId = "user1";
        String reporterId = "reporter1";

        String view = reportController.createReportGet(model, postId, userId, reporterId);

        ArgumentCaptor<CreateReportRequest> captor = ArgumentCaptor.forClass(CreateReportRequest.class);
        verify(model).addAttribute(eq("createReportRequest"), captor.capture());

        CreateReportRequest captured = captor.getValue();
        assertEquals(postId, captured.getPostId());
        assertEquals(userId, captured.getUserId());
        assertEquals(reporterId, captured.getReporterId());

        assertEquals("report/create-report", view);
    }

    @Test
    void createReportPost_withBindingErrors_shouldReturnCreateReportView() {
        CreateReportRequest request = new CreateReportRequest();
        when(bindingResult.hasErrors()).thenReturn(true);

        String view = reportController.createReportPost(request, bindingResult, model);

        verify(model).addAttribute("createReportRequest", request);
        assertEquals("report/create-report", view);
        verifyNoInteractions(reportService);
    }

    @Test
    void createReportPost_withoutErrors_shouldCallServiceAndRedirect() {
        CreateReportRequest request = new CreateReportRequest();
        when(bindingResult.hasErrors()).thenReturn(false);

        String view = reportController.createReportPost(request, bindingResult, model);

        verify(reportService).createReport(request);
        assertEquals("redirect:/", view);
    }

    @Test
    void reports_shouldAddReportsToModelAndReturnView() {
        when(reportService.findAll()).thenReturn(List.of());

        String view = reportController.reports(model);

        verify(model).addAttribute(eq("reports"), any());
        assertEquals("report/reports", view);
    }

    @Test
    void deleteReport_shouldCallServiceAndRedirect() {
        String reportId = "report123";

        String view = reportController.deleteReport(reportId);

        verify(reportService).deleteReport(reportId);
        assertEquals("redirect:/reports", view);
    }
}
