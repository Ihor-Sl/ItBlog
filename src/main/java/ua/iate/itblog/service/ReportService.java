package ua.iate.itblog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.iate.itblog.exception.NotFoundException;
import ua.iate.itblog.model.report.CreateReportRequest;
import ua.iate.itblog.model.report.Report;
import ua.iate.itblog.repository.ReportRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;

    public List<Report> findAll() {
        return reportRepository.findAll();
    }

    public Report findById(String id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("errors.report.id.not-found", id));
    }

    public void createReport(CreateReportRequest createReportRequest) {
        Report report = Report.builder()
                .id(UUID.randomUUID().toString())
                .title(createReportRequest.getTitle())
                .description(createReportRequest.getDescription())
                .postId(createReportRequest.getPostId())
                .userId(createReportRequest.getUserId())
                .reporterId(createReportRequest.getReporterId())
                .build();
        reportRepository.save(report);
    }

    public void deleteReport(String id) {
        reportRepository.deleteById(id);
    }
}