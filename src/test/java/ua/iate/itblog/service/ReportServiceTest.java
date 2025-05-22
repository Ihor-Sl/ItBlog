package ua.iate.itblog.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.iate.itblog.exception.NotFoundException;
import ua.iate.itblog.model.report.CreateReportRequest;
import ua.iate.itblog.model.report.Report;
import ua.iate.itblog.repository.ReportRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private ReportRepository reportRepository;

    @InjectMocks
    private ReportService reportService;

    @Test
    void testFindAll_shouldReturnAllReports() {
        List<Report> expectedReports = List.of(
                Report.builder().id("1").title("Spam").build(),
                Report.builder().id("2").title("Abuse").build()
        );
        when(reportRepository.findAll()).thenReturn(expectedReports);

        List<Report> actualReports = reportService.findAll();

        assertEquals(expectedReports, actualReports);
        verify(reportRepository).findAll();
    }

    @Test
    void testFindById_existingId_shouldReturnReport() {
        Report report = Report.builder().id("123").title("Test Report").build();
        when(reportRepository.findById("123")).thenReturn(Optional.of(report));

        Report result = reportService.findById("123");

        assertEquals(report, result);
        verify(reportRepository).findById("123");
    }

    @Test
    void testFindById_nonExistingId_shouldThrowNotFoundException() {
        when(reportRepository.findById("nonexistent")).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                reportService.findById("nonexistent")
        );

        assertEquals("errors.report.id.not-found", exception.getMessage());
        assertEquals("nonexistent", exception.getParams()[0]);
        verify(reportRepository).findById("nonexistent");
    }

    @Test
    void testCreateReport_shouldSaveReport() {
        CreateReportRequest request = CreateReportRequest.builder()
                .title("Test Report")
                .description("Test description")
                .postId("postId")
                .UserId("userId")
                .reporterId("reporterId")
                .build();

        reportService.createReport(request);

        ArgumentCaptor<Report> captor = ArgumentCaptor.forClass(Report.class);
        verify(reportRepository).save(captor.capture());

        Report savedReport = captor.getValue();
        assertEquals("Test Report", savedReport.getTitle());
        assertEquals("Test description", savedReport.getDescription());
        assertEquals("postId", savedReport.getPostId());
        assertEquals("userId", savedReport.getUserId());
        assertEquals("reporterId", savedReport.getReporterId());
        assertNotNull(savedReport.getId());
    }

    @Test
    void testDeleteReport_shouldCallRepositoryDelete() {
        reportService.deleteReport("123");
        verify(reportRepository).deleteById("123");
    }
}
